package com.felipenishino.sobala.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityListProductsBinding
import com.felipenishino.sobala.databinding.ProductCardBinding
import com.felipenishino.sobala.db.ProdutoService
import com.felipenishino.sobala.model.Product
import com.felipenishino.sobala.utils.getCurrentUser
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
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
    var toggle: ActionBarDrawerToggle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.openDrawer, R.string.closeDrawer)

        toggle?.let {
            binding.drawerLayout.addDrawerListener(it)
            it.syncState()
        }

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            when(it.itemId) {
                R.id.about -> {
                    val i = Intent(this, AboutActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.account -> {
                    val i = Intent(this, AccountActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.purchaseHistory -> {
                    val i = Intent(this, PurchaseHistoryActivity::class.java)
                    startActivity(i)
                    true
                }
                R.id.login -> {
                    val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

                    val intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()

                    startActivityForResult(intent, 1)
                    true
                }
                R.id.logout -> {
                    FirebaseAuth.getInstance().signOut()
                    updateMenu()
                    true
                }
                else -> {
                    Log.d("navItemSelected", "Error, no valid id found.")
                    false
                }
            }
        }

        listAllProducts()
        updateMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cart -> {
                val i = Intent(this, CartActivity::class.java)
                startActivity(i)
            }
            else -> {
                toggle?.let {
                    return it.onOptionsItemSelected(item)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun updateUI(products: List<Product>) {
        binding.productContainer.removeAllViews()
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
                        Log.d("homeRetrofit", "Chamada bem-sucedida, porém retornou vazio")
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
                    Log.e("homeRetrofit", response.errorBody().toString())
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

                Log.e("homeRetrofit", "Falha Conexão",t)
            }

        }

        call.enqueue(callback)
    }

    override fun onResume() {
        super.onResume()
        refreshProducts()
    }

    private fun updateMenu() {
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val isAuthenticated = getCurrentUser() != null
        navigationView.menu.findItem(R.id.account)?.isVisible = isAuthenticated
        navigationView.menu.findItem(R.id.purchaseHistory)?.isVisible = isAuthenticated
        navigationView.menu.findItem(R.id.logout)?.isVisible = isAuthenticated
        navigationView.menu.findItem(R.id.login)?.isVisible = !isAuthenticated
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val navigationView = findViewById<NavigationView>(R.id.navigationView)
                updateMenu()
            }
        }
    }
}