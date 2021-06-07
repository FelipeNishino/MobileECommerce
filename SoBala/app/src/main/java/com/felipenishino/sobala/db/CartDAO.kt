package com.felipenishino.sobala.db

import androidx.room.*
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Product

@Dao
interface CartDAO {
    @Insert
    fun insertCart(cart: Cart)

    @Query(value = "Select * from Cart")
    fun getCart(): Cart?

    @Update
    fun updateCart(cart: Cart)

    @Delete
    fun deleteCart(cart: Cart)
}