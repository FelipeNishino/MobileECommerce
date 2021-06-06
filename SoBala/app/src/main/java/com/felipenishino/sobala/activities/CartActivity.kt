package com.felipenishino.sobala.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.room.Room
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityCartBinding
import com.felipenishino.sobala.databinding.CartProductCardBinding
import com.felipenishino.sobala.databinding.ProductCardBinding
import com.felipenishino.sobala.db.AppDatabase
import com.felipenishino.sobala.db.ProdutoService
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Product
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

enum class Operation {
    SUM, SUBTRACTION, ATRIBUTION, NONE
}

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    fun updateUI(products: Set<Product>, idToQuantity: Map<Int, Int>) {
        binding.cartContainer.removeAllViews()
        products.forEach { product ->
            val productBinding
                    =
                    CartProductCardBinding.inflate(layoutInflater)

            productBinding.txtCartProductName.text = product.nome
//            productBinding.txtProductPrice.text = "R\$${product.preco}"
            productBinding.editCartProductQuantity.setText(idToQuantity[product.id].toString())

            Picasso.get().load(product.linkImg).into(productBinding.imgCartProduct)

            productBinding.cardViewCart.setOnClickListener {
                val intent = Intent(this,  DetalheProdutoActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }

            productBinding.editCartProductQuantity.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    var newValue = productBinding.editCartProductQuantity.text.toString().toInt()
                    if (newValue < 1) {
                        newValue = 1
                    }
                    Thread { refreshProducts(product.id, newValue, Operation.ATRIBUTION) }.start()
                }
            }

            productBinding.btnDecrementQuantity.setOnClickListener {
                if (idToQuantity[product.id]!! > 1) {
                    productBinding.editCartProductQuantity.setText((idToQuantity[product.id]!! - 1).toString())
                    Thread { refreshProducts(product.id, op = Operation.SUBTRACTION) }.start()
                }
            }

            productBinding.btnIncrementQuantity.setOnClickListener {
                productBinding.editCartProductQuantity.setText((idToQuantity[product.id]!! + 1).toString())
                Thread { refreshProducts(product.id, op = Operation.SUM) }.start()
            }

            binding.cartContainer.addView(productBinding.root)
        }
    }

    private fun refreshProducts(productID: Int? = null, newValue: Int? = null , op: Operation) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
        var cart = db.cartDAO().getCart() ?: Cart(1, mutableSetOf(), mutableMapOf())
        if (cart.products.isNotEmpty()) {
            if (op != Operation.NONE) {
                productID?.let { id ->
                    cart.productIdToQuantity[id]?.let {
                        when (op) {
                            Operation.ATRIBUTION -> cart.productIdToQuantity[id] = newValue ?: 1
                            Operation.SUBTRACTION -> cart.productIdToQuantity[id] = it - 1
                            Operation.SUM -> cart.productIdToQuantity[id] = it + 1
                        }
                    }
                }
                db.cartDAO().updateCart(cart)
            }
            runOnUiThread {
                updateUI(cart.products, cart.productIdToQuantity)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Thread { refreshProducts(op = Operation.NONE) }.start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        menu?.let {
            menu.findItem(R.id.barbtnSearch).isVisible = false
            menu.findItem(R.id.cart).isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}