package com.example.android_news_trigger_v3

import org.json.JSONObject

data class NotificationDTO(
    val packageInfo: String,
    val ticker: String,
    val title: String,
    val text: String,
    val url: String,
    val postTime: Long
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
                jsonObject.getString("url"),
                jsonObject.getLong("postTime")
            )
        }
    }
}