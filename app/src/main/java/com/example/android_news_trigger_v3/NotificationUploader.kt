package com.example.android_news_trigger_v3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class NotificationUploader(private val database: Database) {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun scheduleUpload() {
        scope.launch {
            database
                .allNotificationsNotUploadedForBackground()
                .asFlow()
                .map {
                    database.copyFromDatabase(it.list)
                }
                .collect {
                    it.forEach {
                        uploadNotification(it)
                    }
                }
        }
    }

    private suspend fun uploadNotification(notification: Notification) {
        val jsonNotification = JSONObject().apply {
            put("packageInfo", notification.packageInfo)
            put("ticker", notification.ticker)
            put("title", notification.title)
            put("text", notification.text)
            put("url", notification.url)
            put("postTime", notification.postTime)
        }

        val apiUrl =
            "http://192.168.50.103:696/api/v1/tweet/add"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8")
            connection.doOutput = true

            val outputStream: OutputStream = BufferedOutputStream(connection.outputStream)
            outputStream.write(jsonNotification.toString().toByteArray(Charsets.UTF_8))
            outputStream.flush()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                // Upload successful, update uploaded field in local database
                database.updateNotification(notification, update = { it.uploaded = true })
            } else {
                // Handle the error by reading the response
                val reader = BufferedReader(InputStreamReader(connection.errorStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                // Handle the error response here
            }
        } catch (e: Exception) {
            // Handle general exceptions that might occur during the upload
            println("Error during upload: ${e.message}")
            // You can also log the exception or perform other error handling tasks here
        } finally {
            connection.disconnect()
        }
    }
}