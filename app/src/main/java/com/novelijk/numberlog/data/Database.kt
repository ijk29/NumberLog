package com.novelijk.numberlog.data


import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DataPoint::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun dao(): DAO
}