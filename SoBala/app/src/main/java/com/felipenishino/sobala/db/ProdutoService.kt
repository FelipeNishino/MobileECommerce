package com.felipenishino.sobala.db

import com.felipenishino.sobala.model.Produto
import retrofit2.Call
import retrofit2.http.GET

interface ProdutoService {

    @GET("/sobala/produtos")
    fun listAll(): Call<List<Produto>>

}