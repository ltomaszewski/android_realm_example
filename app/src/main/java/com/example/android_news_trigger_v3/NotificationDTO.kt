package com.example.android_news_trigger_v3

import org.json.JSONObject

data class NotificationDTO(
    val packageInfo: String,
    val ticker: String,
    val title: String,
    val text: String,
    val url: String
) {
    companion object {
        // Factory method to create NotificationData from JSON string
        fun fromJson(jsonString: String): NotificationDTO {
            val jsonObject = JSONObject(jsonString)
            return NotificationDTO(
                jsonObject.getString("packageInfo"),
                jsonObject.getString("ticker"),
                jsonObject.getString("title"),
                jsonObject.getString("text"),
                jsonObject.getString("url")
            )
        }

        // Factory method to create a random NotificationDTO object
        fun createRandomNotification(): NotificationDTO {
            val packageInfo = "com.example.app"
            val ticker = "New Notification"
            val title = "Random Title"
            val text = "Random Text"
            val url = "https://example.com"

            return NotificationDTO(
                packageInfo,
                ticker,
                title,
                text,
                url
            )
        }
    }
}