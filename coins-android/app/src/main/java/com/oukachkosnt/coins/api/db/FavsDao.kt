package com.oukachkosnt.coins.api.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface FavsDao {
    @Insert
    fun insertFav(id: CryptoCoinEntity): Completable

    @Delete
    fun deleteFav(id: CryptoCoinEntity): Completable

    @Query("SELECT id FROM fav_crypto_coins")
    fun getFavs(): Single<List<CryptoCoinEntity>>
}