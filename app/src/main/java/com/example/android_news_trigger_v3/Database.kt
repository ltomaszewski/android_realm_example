package com.example.android_news_trigger_v3

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.TypedRealmObject

class Database {
    private val config = RealmConfiguration.Builder(schema = setOf(Notification::class)).build()
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
        realm.writeBlocking {
            findLatest(notification)?.let(update)
        }
    }

    fun allNotifications(): RealmResults<Notification> {
        return realm.query<Notification>().find()
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