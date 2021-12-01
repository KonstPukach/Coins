package com.oukachkosnt.coins.repository

import android.content.Context
import androidx.room.Room
import com.oukachkosnt.coins.api.db.FavsDatabase

object DbRepository {
    private var dbInstance: FavsDatabase? = null

    fun getFavsDb(context: Context) : FavsDatabase {
        if (dbInstance == null) {
            dbInstance = createInstance(context)
        }
        return dbInstance ?: createInstance(context)
    }

    private fun createInstance(context: Context): FavsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            FavsDatabase::class.java,
            FavsDatabase.DB_NAME
        ).allowMainThreadQueries().build()
    }
}