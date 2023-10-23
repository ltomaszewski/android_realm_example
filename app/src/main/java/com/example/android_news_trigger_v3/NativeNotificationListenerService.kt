package com.example.android_news_trigger;

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.android_news_trigger_v3.ContentType
import com.example.android_news_trigger_v3.EventType
import org.json.JSONObject

public class NativeNotificationListenerService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val postTime = sbn.postTime // TODO: Needs to be added to model datbase and bla bla
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

        val intent = Intent(EventType.NOTIFICATION.value)
        intent.putExtra(ContentType.JSON.value, notificationData)
        sendBroadcast(intent)
    }
}