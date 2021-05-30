package com.felipenishino.sobala.db

import androidx.room.Database
import com.felipenishino.sobala.model.Product

@Database(entities = arrayOf(Product::class), version = 1)
abstract class AppDatabase {
    abstract fun cartDAO(): CartDAO
}