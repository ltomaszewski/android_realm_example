package com.example.android_news_trigger_v3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@ExperimentalMaterial3Api
@ExperimentalComposeUiApi
@Composable
fun CRUDScreen(database: Database, notificationUploader: NotificationUploader) {
    var serverIpAddress by remember { mutableStateOf("") }
    val serverIpState = rememberUpdatedState(serverIpAddress)
    var isApplyButtonEnabled by remember { mutableStateOf(false) }
    var isTextFieldFocused by remember { mutableStateOf(false) }
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }



    LaunchedEffect(Unit) {
        database
            .allNotifications()
            .collect { results ->
                withContext(Dispatchers.Main) {
                    notifications =
                        database.copyFromDatabase(results.list).sortedByDescending { it.createAt }
                }
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
            OutlinedTextField(
                value = serverIpState.value,
                onValueChange = {
                    serverIpAddress = it
                    isApplyButtonEnabled = true
                },
                label = { Text("Server IP Address") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                    }
                ),
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .weight(1f)
                    .padding(end = 8.dp)

            )

            Button(
                onClick = {
                    notificationUploader.updateHostIp(serverIpAddress)
                    isApplyButtonEnabled = false
                    keyboardController?.hide()
                    focusRequester.freeFocus()
                },
                enabled = isApplyButtonEnabled,
                modifier = Modifier
                    .height(64.dp)
                    .padding(top = 8.dp)
            ) {
                Text("Apply")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    database.setAllNotificationNotUploaded()
                },
                enabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text("Flush All")
            }

            Button(
                onClick = {
                    database.forceReUploadNotUploadedByUpdateUpdatedAt()
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text("Flush not uploaded")
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
