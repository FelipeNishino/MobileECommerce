package com.felipenishino.sobala.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Product (
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var nome: String,
    var preco: Double,
    var categoria: String,
    var desconto: Int,
    var descricao: String
) : Serializable