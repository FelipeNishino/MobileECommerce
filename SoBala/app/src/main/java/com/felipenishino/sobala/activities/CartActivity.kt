package com.felipenishino.sobala.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityCartBinding
import com.felipenishino.sobala.databinding.CartProductCardBinding
import com.felipenishino.sobala.db.AppDatabase
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Product
import com.felipenishino.sobala.model.Purchase
import com.felipenishino.sobala.model.PurchaseItem
import com.felipenishino.sobala.utils.getCurrentUser
import com.felipenishino.sobala.utils.onPurchase
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

enum class Operation {
    SUM, SUBTRACTION, ATTRIBUTION, NONE
}

class CartActivity : AppCompatActivity() {
    lateinit var binding: ActivityCartBinding
    lateinit var cart: Cart
    var database: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        configDatabase()
        Thread { refreshProducts(null, op = Operation.NONE) }.start()

        if (getCurrentUser() == null) {
            binding.btnFinish.text = getString(R.string.needsLogin)
            binding.btnFinish.setOnClickListener {
                val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

                val intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build()

                startActivityForResult(intent, 1)
            }
        }
        else {
            updateFinishButton()
        }

        binding.txtEmptyCart.visibility = View.INVISIBLE
    }

    fun updateFinishButton() {
        binding.btnFinish.text = getString(R.string.btnFinishPurchase)
        binding.btnFinish.setOnClickListener {
            onClickPurchase(cart)
            deleteCart()
        }
    }

    fun updateUI(products: Set<Product>, idToQuantity: Map<Int, Int>) {
        binding.cartContainer.removeAllViews()
        if (products.isNotEmpty()) {
            var totalPrice = 0.0
            val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
            formatter.currency = Currency.getInstance("BRL")

            products.forEach { product ->
                val productBinding = CartProductCardBinding.inflate(layoutInflater)

                productBinding.txtCartProductName.text = product.nome
                productBinding.txtCartProductQuantity.text = (idToQuantity[product.id] ?: 1).toString()

                val totalProductPrice = ((idToQuantity[product.id]?.toFloat() ?: 2F) * product.preco)
                totalPrice += totalProductPrice

                productBinding.txtCartProductPrice.text = formatter.format(totalProductPrice)
                Picasso.get().load(product.linkImg).into(productBinding.imgCartProduct)

                productBinding.cardViewCart.setOnClickListener {
                    val intent = Intent(this, DetalheProdutoActivity::class.java)
                    intent.putExtra("product", product)
                    startActivity(intent)
                }

                productBinding.btnDecrementQuantity.setOnClickListener {
                    if (idToQuantity[product.id]!! > 1) {
                        productBinding.txtCartProductQuantity.text = (idToQuantity[product.id]!! - 1).toString()
                        Thread { refreshProducts(product.id, op = Operation.SUBTRACTION) }.start()
                    }
                }

                productBinding.btnIncrementQuantity.setOnClickListener {
                    productBinding.txtCartProductQuantity.text = (idToQuantity[product.id]!! + 1).toString()
                    Thread { refreshProducts(product.id, op = Operation.SUM) }.start()
                }

                binding.cartContainer.addView(productBinding.root)
            }
            binding.txtTotalPrice.text = formatter.format(totalPrice)

        }
        else {
            binding.txtEmptyCart.visibility = View.VISIBLE
            binding.txtTotalPrice.text = getString(R.string.cartTotalCostPlaceholder)
            binding.btnFinish.isEnabled = false
        }
    }

    private fun refreshProducts(productID: Int? = null, newValue: Int? = null , op: Operation) {
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
        cart = db.cartDAO().getCart() ?: Cart(products = mutableSetOf(), productIdToQuantity = mutableMapOf())
        if (cart.products.isNotEmpty()) {
            if (op != Operation.NONE) {
                productID?.let { id ->
                    cart.productIdToQuantity[id]?.let {
                        when (op) {
                            Operation.ATTRIBUTION -> cart.productIdToQuantity[id] = newValue ?: 1
                            Operation.SUBTRACTION -> cart.productIdToQuantity[id] = it - 1
                            Operation.SUM -> cart.productIdToQuantity[id] = it + 1
                        }
                    }
                }
                db.cartDAO().updateCart(cart)
            }
        }
        runOnUiThread {
            updateUI(cart.products, cart.productIdToQuantity)
        }
    }

    fun onClickPurchase(cart: Cart) {
        val items = cart.products.map {
            PurchaseItem(
                produto = it,
                quantidade = cart.productIdToQuantity.get(it.id)?: 1
            )
        }

        var totalValue = 0.0

        items.forEach { totalValue += it.quantidade * it.produto.preco }

        val purchase = Purchase(
            items = items,
            valorTotal = totalValue
        )

        try {
            onPurchase(database, purchase)
            Snackbar.make(binding.root, R.string.notifySuccessfulPurchase, Snackbar.LENGTH_LONG)
                .show()
        }
        catch (E: Exception){
            Snackbar.make(binding.root, R.string.notifyFailedPurchase, Snackbar.LENGTH_LONG)
                .show()
        }
    }

    fun deleteCart(){
        Thread{
            val db = Room.databaseBuilder(this, AppDatabase::class.java, "db").build()
            db.cartDAO().deleteCart(cart)
            runOnUiThread {
                updateUI(emptySet(), emptyMap())
            }
        }.start()
    }

    fun configDatabase() {
        val user = getCurrentUser()

        if (user != null) {
            database = Firebase.database.reference.child("users").child(user.uid)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                updateFinishButton()
            }
        }
    }
}