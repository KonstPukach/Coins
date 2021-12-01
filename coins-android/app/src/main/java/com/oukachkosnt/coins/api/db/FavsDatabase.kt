package com.oukachkosnt.coins.api.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ CryptoCoinEntity::class ], version = 1, exportSchema = true)
abstract class FavsDatabase : RoomDatabase() {
    abstract fun favsDao(): FavsDao

    companion object {
        const val DB_NAME = "favs_db"
    }
}