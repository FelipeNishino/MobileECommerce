package com.felipenishino.sobala.model

data class Produto (
    var id: Int,
    var name: String,
    var price: Double,
    var category: String,
    var discount: Int,
    var description: String
)