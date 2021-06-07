package com.felipenishino.sobala.model

data class Product (
    var id: Int,
    var nome: String,
    var preco: Double,
    var marca: String,
    var desconto: Int,
    var descricao: String,
    var linkImg: String
) : Serializable
