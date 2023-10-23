package com.example.android_news_trigger_v3

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class Item() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var isComplete: Boolean = false
    var summary: String = ""
    var owner_id: String = ""
    constructor(ownerId: String = "") : this() {
        owner_id = ownerId
    }

    companion object {
        fun createRandomItem(ownerId: String): Item {
            val randomItem = Item(ownerId)
            randomItem.isComplete = (0..1).random() == 1 // Random true or false
            randomItem.summary = generateRandomSummary()
            return randomItem
        }

        private fun generateRandomSummary(): String {
            val summaries = listOf(
                "Lorem ipsum dolor sit amet.",
                "Sed ut perspiciatis unde omnis iste natus error.",
                "Eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur."
            )
            return summaries.random()
        }
    }
}