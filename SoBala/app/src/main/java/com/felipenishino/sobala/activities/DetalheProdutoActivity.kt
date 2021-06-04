package com.felipenishino.sobala.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.room.Room
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityDetalheProdutoBinding
import com.felipenishino.sobala.db.AppDatabase
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Product
import com.squareup.picasso.Picasso
import java.util.*

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

        binding.btnAddCart.setOnClickListener { addProductToCart(product, binding.editProductQuantity.text.toString().toInt()) }
    }

    private fun addProductToCart(product: Product, quantity: Int) {
        Thread {
            val db = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
            var cart = db.cartDAO().getCart()
            if (cart == null) {
                cart = Cart(1, mutableSetOf(), mutableMapOf())
                db.cartDAO().insertCart(cart)
            }
            cart.products.add(product)

            if (cart.productIdToQuantity[product.id] != null) {
                cart.productIdToQuantity[product.id] = quantity + cart.productIdToQuantity[product.id]!!
            }
            else {
                cart.productIdToQuantity[product.id] = quantity
            }
            db.cartDAO().updateCart(cart)
        }.start()
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