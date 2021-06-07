package com.felipenishino.sobala.activities

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*
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

        binding.progressIndicator.visibility = View.INVISIBLE

        binding.navigationView.setNavigationItemSelectedListener {
            binding.drawerLayout.closeDrawers()
            when(it.itemId) {
                R.id.about -> {
                    val i = Intent(this, AboutActivity::class.java)
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
                    true
                }
            }
        }

        getProducts(null)
        updateMenu()
    }

    private fun updateMenu() {
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        val isAuthenticated = getCurrentUser() != null
        navigationView.menu.findItem(R.id.purchaseHistory)?.isVisible = isAuthenticated
        navigationView.menu.findItem(R.id.logout)?.isVisible = isAuthenticated
        navigationView.menu.findItem(R.id.login)?.isVisible = !isAuthenticated
    }

    fun updateUI(products: List<Product>) {
        binding.progressIndicator.visibility = View.INVISIBLE
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance("BRL")
        products.forEach { product ->
            val productBinding = ProductCardBinding.inflate(layoutInflater)

            productBinding.txtProductName.text = product.nome
            productBinding.txtProductPrice.text = formatter.format(product.preco)

            Picasso.get().load(product.linkImg).into(productBinding.imgProduct)

            productBinding.cardViewProduct.setOnClickListener {
                val intent = Intent(this,  DetalheProdutoActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)
            }

            binding.productContainer.addView(productBinding.root)
        }
    }

    fun getProducts(query: String?){
        binding.productContainer.removeAllViews()
        binding.progressIndicator.visibility = View.VISIBLE
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
                if (response.isSuccessful) {
                    var listaProduto = response.body()
                    listaProduto?.let { list ->
                        query?.let {
                            updateUI(list.filter { product -> product.nome.contains(Regex(it, RegexOption.IGNORE_CASE)) || product.marca.contains(Regex(it, RegexOption.IGNORE_CASE)) })
                            return
                        }
                        updateUI(list)
                        return
                    }
                    Log.d("homeRetrofit", "Chamada bem-sucedida, porém retornou vazio")
                }
                else {
                    Log.e("homeRetrofit", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("homeRetrofit", "Falha na conexão",t)
                Snackbar.make(binding.root, R.string.notifyFailedRequest, Snackbar.LENGTH_LONG)
                    .show()
            }
        }

        call.enqueue(callback)
    }

    override fun onResume() {
        super.onResume()
        updateMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu?.let {
            menu.findItem(R.id.barbtnSearch)
        }

        val searchView = searchItem!!.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                getProducts(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("searchView", "text changed")
                return true
            }
        })

        searchView.setOnCloseListener {
            getProducts(null)
            false
        }

        searchView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                searchView.clearFocus()
            }
        }

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateMenu()
            }
        }
    }
}