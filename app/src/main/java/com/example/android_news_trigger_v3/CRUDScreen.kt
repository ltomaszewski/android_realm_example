package com.example.android_news_trigger_v3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun CRUDScreen(database: Database) {
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }

    LaunchedEffect(Unit) {
        database
            .allNotifications()
            .asFlow()
            .collect { results ->
                notifications = database.copyFromDatabase(results.list)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    val dto = NotificationDTO.createRandomNotification()
                    val notification = Notification.fromDto(dto)
                    database.saveNotification(notification)
                },
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text("Save")
            }

            Button(
                onClick = {
                    database.allNotifications()
                        .last()
                        .let {
                            database.deleteNotification(it)
                        }
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text("Delete")
            }
        }

        // Display the list of all items
        LazyColumn {
            items(notifications.size) { index ->
                val notification = notifications[index]
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(color = if (index % 2 == 0) Color.Black.copy(alpha = 0.2F) else Color.White)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "ID: ${notification._id.toString()}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "Package Info: ${notification.packageInfo}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(text = "Ticker: ${notification.ticker}", modifier = Modifier.padding(4.dp))
                    Text(text = "Title: ${notification.title}", modifier = Modifier.padding(4.dp))
                    Text(text = "Text: ${notification.text}", modifier = Modifier.padding(4.dp))
                    Text(text = "URL: ${notification.url}", modifier = Modifier.padding(4.dp))
                    Text(
                        text = "PostTime: ${notification.postTime}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "CreatedAt: ${notification.createAt}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = "TimeDiff: ${notification.createAt - notification.postTime}",
                        modifier = Modifier.padding(4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(if (notification.uploaded) Color.Green else Color.Red)
                    )
                }
            }
        }
    }
}
