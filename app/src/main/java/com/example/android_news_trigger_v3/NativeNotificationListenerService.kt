package com.example.android_news_trigger;

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import org.json.JSONObject

public class NativeNotificationListenerService : NotificationListenerService() {
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        // Schedule test notifications every 10 seconds
        scheduleTestNotifications()
    }

    private fun scheduleTestNotifications() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                sendTestNotification()
                handler.postDelayed(this, 10000) // 10000 milliseconds = 10 seconds
            }
        }, 1000) // Delay the first notification for 10 seconds
    }

    private fun sendTestNotification() {
        // Create a sample notification JSON object
        val jsonObject = JSONObject()
        jsonObject.put("packageInfo", "com.example.testapp")
        jsonObject.put("ticker", "Test Notification")
        jsonObject.put("title", "Test Notification")
        jsonObject.put("text", "This is a test notification.")
        jsonObject.put("url", "https://example.com")

        // Convert JSON object to a string
        val notificationData: String = jsonObject.toString()

        val intent = Intent("test-event")
        intent.putExtra("jsonString", notificationData)
        sendBroadcast(intent)
        // Invoke the Flutter method to send the test notification
//        methodChannel.invokeMethod("onNotificationReceived", notificationData)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        print("onNotificationPosted")
        val pack = sbn.packageName
        var ticker = ""
        if (sbn.notification.tickerText != null) {
            ticker = sbn.notification.tickerText.toString()
        }
        var extras: Bundle? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            extras = sbn.notification.extras
        }

        val title = extras!!.getString("android.title")
        val text = extras!!.getCharSequence("android.text").toString()
        val url = sbn.tag

        val jsonObject = JSONObject()
        jsonObject.put("packageInfo", pack)
        jsonObject.put("ticker", ticker ?: JSONObject.NULL)
        jsonObject.put("title", title ?: JSONObject.NULL)
        jsonObject.put("text", text ?: JSONObject.NULL)
        jsonObject.put("url", url ?: JSONObject.NULL)

        // Write the JSON object to the file
        val notificationData: String = jsonObject.toString()
//        methodChannel.invokeMethod("onNotificationReceived", notificationData)

//        val keys = extras!!.keySet()
//        Log.i("Keys", keys.toString())
//
//        val keysValue = ArrayList<String>()
//
//        for (key in keys) {
//            val value = extras!![key]
//            if (value != null) {
//                val keyValueString = "$key: $value"
//                keysValue.add(keyValueString)
//            }
//        }
//        Log.i("keysValue", keysValue.toString())
//        Log.i("keysValue", keysValue.toString())
//        Log.i("Url", url)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
//        methodChannel.invokeMethod("onNotificationRemoved", "sbn")
    }
}