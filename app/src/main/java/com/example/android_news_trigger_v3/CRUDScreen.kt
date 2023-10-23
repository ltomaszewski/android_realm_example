package com.example.android_news_trigger_v3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }

    LaunchedEffect(Unit) {
        database
            .allItems()
            .asFlow()
            .collect { results ->
                allItems = database.copyFromDatabase(results.list)
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
                    database.saveItem(Item.createRandomItem("exampleOwnerId"))
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
                    val allItemstoList = database.allItems().toList()
                    if (allItemstoList.isNotEmpty()) {
                        database.deleteItem(allItemstoList.first())
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
            items(allItems.size) { index ->
                val item = allItems[index]
                Column(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .background(color = if (index % 2 == 0) Color.Blue else Color.Red)
                        .padding(16.dp)
                ) {
                    Text(text = item._id.toString(), modifier = Modifier.padding(8.dp))
                    Text(text = item.owner_id, modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}