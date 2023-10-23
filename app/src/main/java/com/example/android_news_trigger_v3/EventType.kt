package com.example.android_news_trigger_v3

enum class EventType(val value: String) {
    TEST_EVENT("test-event"),
    NOTIFICATION("notification-event")
    // Add more event types as needed
}

enum class ContentType(val value: String) {
    JSON("json"),
    // Add more event types as needed
}