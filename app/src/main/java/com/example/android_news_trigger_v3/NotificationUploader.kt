package com.example.android_news_trigger_v3

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
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
    private var hostIp = "192.168.50.103"

    fun scheduleUpload() {
        scope.launch {
            database
                .allNotificationsNotUploaded()
                .flowOn(Dispatchers.Main)
                .map {
                    database.copyFromDatabase(it.list)
                }
                .collect {
                    it.forEach {
                        uploadNotification(it)
                        Thread.sleep(20) // For unknown reason the UI is not getting updated due to too frequent updates in database. Simple sleep solve it
                    }
                }
        }
    }

    fun updateHostIp(hostIp: String) {
        this.hostIp = hostIp
        database.setAllNotificationNotUploaded()
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
            "http://$hostIp:696/api/v1/tweet/add"

        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection

        try {
            connection.requestMethod = "POST"
            connection.connectTimeout = 1000
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