package com.felipenishino.sobala.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.felipenishino.sobala.databinding.ActivityDetalheProdutoBinding
import com.felipenishino.sobala.model.Product

class DetalheProdutoActivity : AppCompatActivity() {
    lateinit var binding: ActivityDetalheProdutoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalheProdutoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val product = intent.getSerializableExtra("product") as Product

        binding.txtNome.text = product.nome
        binding.txtPreco.text = "R\$${product.preco}"
    }
}