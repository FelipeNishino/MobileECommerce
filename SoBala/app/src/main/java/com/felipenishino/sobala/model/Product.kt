package com.felipenishino.sobala.model

data class Product (
    var id: Int,
    var nome: String,
    var preco: Double,
    var categoria: String,
    var desconto: Int,
    var descricao: String
)