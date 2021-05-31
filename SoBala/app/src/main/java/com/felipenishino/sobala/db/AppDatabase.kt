package com.felipenishino.sobala.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.felipenishino.sobala.model.Product

@Database(entities = arrayOf(Product::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cartDAO(): CartDAO
}