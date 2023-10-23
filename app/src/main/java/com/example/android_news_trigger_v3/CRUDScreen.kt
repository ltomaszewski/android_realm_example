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
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query

@Composable
fun CRUDScreen(realm: Realm) {
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }

    LaunchedEffect(Unit) {
        val query = realm.query<Item>().find()

        query.asFlow()
            .collect { results ->
                allItems = realm.copyFromRealm(results.list)
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
                    realm.writeBlocking {
                        copyToRealm(Item.createRandomItem("exampleOwnerId"))
                    }
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
                    realm.writeBlocking {
                        val allItems = query<Item>().find()
                        if (allItems.isNotEmpty()) {
                            val itemToDelete = allItems.first()
                            delete(itemToDelete)
                        }
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