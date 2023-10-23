package com.example.android_news_trigger_v3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.android_news_trigger_v3.ui.theme.Android_news_trigger_v3Theme
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    data class NotificationData(
        val packageInfo: String,
        val ticker: String,
        val title: String,
        val text: String,
        val url: String
    )

    private val database = Database()
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent
                    .getStringExtra("jsonString")?.also {
                        val jsonObject = JSONObject(it)
                        val notificationData = NotificationData(
                            jsonObject.getString("packageInfo"),
                            jsonObject.getString("ticker"),
                            jsonObject.getString("title"),
                            jsonObject.getString("text"),
                            jsonObject.getString("url")
                        )
                        database.saveItem(Item(notificationData.text))
                    }
            }
        }

        // Register the receiver to listen for "update-ui-event" broadcasts
        val filter = IntentFilter("test-event")
        registerReceiver(receiver, filter)

        setContent {
            Android_news_trigger_v3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CRUDScreen(database)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        database.onDestroy()
    }
}