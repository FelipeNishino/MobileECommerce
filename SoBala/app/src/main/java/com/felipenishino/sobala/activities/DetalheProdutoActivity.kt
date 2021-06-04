package com.felipenishino.sobala.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityDetalheProdutoBinding
import com.felipenishino.sobala.model.Product
import com.felipenishino.sobala.utils.getCurrentUser
import com.squareup.picasso.Picasso

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
        binding.txtMarca.text = product.marca
        Picasso.get().load(product.linkImg).into(binding.imageView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.cart -> {
                val i = Intent(this, CartActivity::class.java)
                startActivity(i)
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}