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
import com.felipenishino.sobala.databinding.ProductCardBinding
import com.felipenishino.sobala.db.AppDatabase
import com.felipenishino.sobala.db.ProdutoService
import com.felipenishino.sobala.model.Product
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
    }

    fun updateUI(products: List<Product>) {
        binding.cartContainer.removeAllViews()
        products.forEach { product ->
            val productBinding
                    =
                    ProductCardBinding.inflate(layoutInflater)

            productBinding.txtProductName.text = product.nome
            productBinding.txtProductPrice.text = "R\$${product.preco}"

            productBinding.cardViewProduct.setOnClickListener {
                val intent = Intent(this,  DetalheProdutoActivity::class.java)
                intent.putExtra("product", product)
                //startActivityForResult(intent, 0)
                startActivity(intent)
            }
            binding.cartContainer.addView(productBinding.root)
        }
    }

    fun refreshProducts() {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
        Thread {
            val prd = db.cartDAO().getAllCart()

            runOnUiThread {
                updateUI(prd)
            }

        }.start()
    }

    override fun onResume() {
        super.onResume()
        refreshProducts()
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