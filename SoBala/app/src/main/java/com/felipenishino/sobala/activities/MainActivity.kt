package com.felipenishino.sobala.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityMainBinding
import com.felipenishino.sobala.db.ProdutoService
import com.felipenishino.sobala.model.Produto
import com.google.android.material.snackbar.Snackbar
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
            .baseUrl("http://murmuring-wave-61983.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create())
            .client(http)
            .build()

        val productService = retrofit.create(ProdutoService::class.java)

        val call = productService.listAll()

        val callback = object : Callback<List<Produto>> {

            override fun onResponse(call: Call<List<Produto>>, response: Response<List<Produto>>) {
//                binding.progressBar.visibility = View.INVISIBLE
//                binding.shimer.visibility = View.INVISIBLE
//                binding.shimer.stopShimmer()
//                binding.scrollView2.visibility = View.VISIBLE
//                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val listaProduto = response.body()
                    Log.d("API", listaProduto.toString())
                }
                else {
//                    Snackbar.make(
//                        binding.container,
//                        "Deu ruim",
//                        Snackbar.LENGTH_LONG
//                    ).show()
//
//                    Log.e("ERRO-Retrofit", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Produto>>, t: Throwable) {
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

                Log.e("ERRO-Retrofit", "Falha Conexão",t)
            }

        }

        call.enqueue(callback)

    }
}