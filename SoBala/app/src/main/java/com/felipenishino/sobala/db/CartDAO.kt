package com.felipenishino.sobala.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.felipenishino.sobala.model.Product

@Dao
interface CartDAO {
    @Insert
    fun saveInCart(product: Product)

    @Query(value = "Select * from Product")
    fun getAllCart(): List<Product>

    @Delete
    fun deleteProductCart(product: Product)
}