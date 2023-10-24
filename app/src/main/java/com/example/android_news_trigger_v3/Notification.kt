package com.example.android_news_trigger_v3

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

open class Notification : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var packageInfo: String = ""
    var ticker: String = ""
    var title: String = ""
    var text: String = ""
    var url: String = ""
    var uploaded: Boolean = false
    var postTime: Long = -1
    var createAt: Long = -1

    constructor() {}

    constructor(
        packageInfo: String = "",
        ticker: String = "",
        title: String = "",
        text: String = "",
        url: String = "",
        postTime: Long = -1
    ) : super() {
        this.packageInfo = packageInfo
        this.ticker = ticker
        this.title = title
        this.text = text
        this.url = url
        this.postTime = postTime
        val currentTimeMillis = System.currentTimeMillis()
        this.createAt = currentTimeMillis / 1000
    }

    // Factory method to create Notification from DTO
    companion object {
        fun fromDto(dto: NotificationDTO): Notification {
            return Notification(
                packageInfo = dto.packageInfo,
                ticker = dto.ticker,
                title = dto.title,
                text = dto.text,
                url = dto.url,
                postTime = dto.postTime
            )
        }
    }
}