package com.example.weatherapp_ramide.db.local

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.Closeable

class LocalDatabase(context: Context, databaseName: String) : Closeable {
    private var roomDB: LocalRoomDatabase = Room.databaseBuilder(
        context = context.applicationContext,
        klass = LocalRoomDatabase::class.java,
        name = databaseName
    ).build()

    private var scope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun insert(city: LocalCity) = scope.launch {
        roomDB.localCityDao().upsert(city)
    }

    fun update(city: LocalCity) = scope.launch {
        roomDB.localCityDao().upsert(city)
    }

    fun delete(city: LocalCity) = scope.launch {
        roomDB.localCityDao().delete(city)
    }

    fun getCities() = roomDB.localCityDao().getCities()

    override fun close() {
        scope.cancel()
        roomDB.close()
    }
}