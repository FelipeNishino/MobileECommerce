package com.felipenishino.sobala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Product
import com.felipenishino.sobala.utils.Converters

@Database(entities = arrayOf(Cart::class), version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cartDAO(): CartDAO
}