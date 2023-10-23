package com.example.android_news_trigger_v3

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.TypedRealmObject

class Database {
    private val config = RealmConfiguration.Builder(schema = setOf(Item::class)).build()
    private val realm: Realm = Realm.open(config)

    fun saveItem(item: Item) {
        realm.writeBlocking {
            copyToRealm(item)
        }
    }

    fun deleteItem(item: Item) {
        realm.writeBlocking {
            findLatest(item)?.let { delete(it) }
        }
    }

    fun allItems(): RealmResults<Item> {
        return realm.query<Item>().find()
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