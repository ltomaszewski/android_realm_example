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

class MainActivity : ComponentActivity() {
    private val database = Database()
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                intent
                    .getStringExtra(ContentType.JSON.value)?.also {
                        val notificationDTO = NotificationDTO.fromJson(it)
                        val notification = Notification.fromDto(notificationDTO)
                        database.saveNotification(notification)
                    }
            }
        }

        val notificationFilter = IntentFilter(EventType.NOTIFICATION.value)
        registerReceiver(receiver, notificationFilter)

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