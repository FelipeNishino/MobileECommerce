package com.felipenishino.sobala.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityListProductsBinding
import com.felipenishino.sobala.databinding.ProductCardBinding
import com.felipenishino.sobala.db.ProdutoService
import com.felipenishino.sobala.model.Product
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ListProductsActivity : AppCompatActivity() {
    lateinit var binding: ActivityListProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        listAllProducts()


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
        }
        return super.onOptionsItemSelected(item)
    }


    fun updateUI(products: List<Product>) {
        products.forEach { product ->
            val productBinding
                    =
                ProductCardBinding.inflate(layoutInflater)

            productBinding.txtProductName.text = product.nome
            productBinding.txtProductPrice.text = "R\$${product.preco}"


            Picasso.get().load(product.linkImg).into(productBinding.imgProduct)


            productBinding.cardViewProduct.setOnClickListener {
                val intent = Intent(this,  DetalheProdutoActivity::class.java)
                intent.putExtra("product", product)
                //startActivityForResult(intent, 0)
                startActivity(intent)
            }


            binding.productContainer.addView(productBinding.root)
        }
    }

    fun refreshProducts() {
        listAllProducts()
    }

    fun listAllProducts(){
        val http = OkHttpClient
            .Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit
            .Builder()
            .baseUrl("https://murmuring-wave-61983.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(http)
            .build()

        val productService = retrofit.create(ProdutoService::class.java)

        val call = productService.listAll()

        val callback = object : Callback<List<Product>> {

            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
//                binding.progressBar.visibility = View.INVISIBLE
//                binding.shimer.visibility = View.INVISIBLE
//                binding.shimer.stopShimmer()
//                binding.scrollView2.visibility = View.VISIBLE
//                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val listaProduto = response.body()
                    if (listaProduto.isNullOrEmpty()) {
                        Log.d("asdasdsa", "Chamada bem-sucedida, porém retornou vazio")
                    }
                    else {
                        updateUI(listaProduto!!)
                    }
                }
                else {
//                    Snackbar.make(
//                        binding.container,
//                        "Deu ruim",
//                        Snackbar.LENGTH_LONG
//                    ).show()
//
                    Log.e("asdasdsa", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
//                binding.progressBar.visibility = View.INVISIBLE
//                binding.shimer.visibility = View.INVISIBLE
//                binding.shimer.stopShimmer()
//                binding.scrollView2.visibility = View.VISIBLE
//                binding.swipeRefresh.isRefreshing = false
//
//                Snackbar.make(
//                    binding.container,
//                    "Deu ruim, sem net",
//                    Snackbar.LENGTH_LONG
//                ).show()

                Log.e("asdasdsa", "Falha Conexão",t)
            }

        }

        call.enqueue(callback)
    }

    override fun onResume() {
        super.onResume()
        refreshProducts()
    }
}