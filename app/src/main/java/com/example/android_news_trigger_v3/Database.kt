package com.example.android_news_trigger_v3

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.TypedRealmObject

class Database {
    private val config = RealmConfiguration.Builder(schema = setOf(Notification::class))
        .schemaVersion(2)
        .build()
    private val realm: Realm = Realm.open(config)


    fun saveNotification(notification: Notification) {
        realm.writeBlocking {
            copyToRealm(notification)
        }
    }

    fun deleteNotification(notification: Notification) {
        realm.writeBlocking {
            findLatest(notification)?.let { delete(it) }
        }
    }

    fun updateNotification(notification: Notification, update: (Notification) -> Unit) {
        val realm = Realm.open(config)
        realm.writeBlocking {
            val manageNotification = query<Notification>("_id = $0", notification._id).find()
            manageNotification.toList().first().let(update)
        }
        realm.close()
    }

    fun allNotifications(): RealmResults<Notification> {
        return realm.query<Notification>().find()
    }

    fun allNotificationsNotUploadedForBackground(): RealmResults<Notification> {
        return realm.query<Notification>("uploaded = false").find()
    }

    fun <T : TypedRealmObject> copyFromDatabase(
        collection: Iterable<T>,
        depth: UInt = UInt.MAX_VALUE
    ): List<T> {
        return realm.copyFromRealm(collection)
    }

    fun onDestroy() {
        realm.close()
    }
}