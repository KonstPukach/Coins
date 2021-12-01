package com.oukachkosnt.coins.api.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oukachkosnt.coins.data.domain.CryptoCoinData

@Entity(tableName = "fav_crypto_coins")
data class CryptoCoinEntity(
    @PrimaryKey val id: String
)

fun CryptoCoinData.mapToEntity(): CryptoCoinEntity = CryptoCoinEntity(this.id)