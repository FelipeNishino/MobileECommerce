package com.felipenishino.sobala.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.felipenishino.sobala.utils.Converters
import java.io.Serializable

@Entity
data class Cart (
        @PrimaryKey(autoGenerate = true)
        var id: Int? = null,
        var products: MutableSet<Product>,
        var productIdToQuantity: MutableMap<Int, Int>
) : Serializable
