package com.example.android_news_trigger_v3

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.TypedRealmObject
import kotlinx.coroutines.flow.Flow

class Database {
    private val config = RealmConfiguration.Builder(schema = setOf(Notification::class))
        .schemaVersion(3)
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

    fun setAllNotificationNotUploaded() {
        realm.writeBlocking {
            val allNotifications = query<Notification>().find()
            for (notification in allNotifications) {
                notification.uploaded = false
                val currentTimeMillis = System.currentTimeMillis()
                notification.updatedAt = currentTimeMillis / 1000
            }
        }
    }

    fun forceReUploadNotUploadedByUpdateUpdatedAt() {
        realm.writeBlocking {
            val allNotifications = query<Notification>("uploaded = $0", false).find()
            for (notification in allNotifications) {
                val currentTimeMillis = System.currentTimeMillis()
                notification.updatedAt = currentTimeMillis / 1000
            }
        }
    }

    fun updateNotification(notification: Notification, update: (Notification) -> Unit) {
        realm.writeBlocking {
            val manageNotification = query<Notification>("_id = $0", notification._id).find()
            manageNotification.toList().first().let {
                update(it)
                val currentTimeMillis = System.currentTimeMillis()
                it.updatedAt = currentTimeMillis / 1000
            }
        }
    }

    fun allNotifications(): Flow<ResultsChange<Notification>> {
        return realm.query<Notification>().sort("postTime", Sort.DESCENDING).asFlow()
    }

    fun allNotificationsNotUploaded(): Flow<ResultsChange<Notification>> {
        return realm.query<Notification>("uploaded = $0", false).asFlow()
    }

    fun allNotificationLister() {

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