package com.felipenishino.sobala.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityAccountBinding
import android.util.Log
import com.felipenishino.sobala.databinding.ActivityPurchaseHistoryBinding
import com.felipenishino.sobala.databinding.PurchaseHistoryCardBinding
import com.felipenishino.sobala.model.Product
import com.felipenishino.sobala.model.Purchase
import com.felipenishino.sobala.model.PurchaseItem
import com.felipenishino.sobala.utils.getCurrentUser
import com.felipenishino.sobala.utils.onPurchase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class PurchaseHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityPurchaseHistoryBinding
    var database: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        configDatabase()

        listPurchases()
    }

    fun configDatabase() {
        val user = getCurrentUser()

        if (user != null) {
            database = Firebase.database.reference.child("users").child(user.uid)
        }
    }

    fun listPurchases() {
        val purchaseListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val itemList = arrayListOf<Purchase>()
                dataSnapshot.children.forEach {
                    val map = it.getValue<HashMap<String, Any>>()


                    val items = map?.get("items") as List<HashMap<String, *>>
                    val purchaseItems = items.map { item ->
                        PurchaseItem(
                            produto = Product(
                                id = ((item["produto"] as HashMap<String, *>)["id"] as Long).toInt(),
                                nome = (item["produto"] as HashMap<String, *>)["nome"] as String,
                                marca = (item["produto"] as HashMap<String, *>)["marca"] as String,
                                descricao = (item["produto"] as HashMap<String, *>)["descricao"] as String,
                                desconto = ((item["produto"] as HashMap<String, *>)["desconto"] as Long).toInt(),
                                linkImg = (item["produto"] as HashMap<String, *>)["linkImg"] as String,
                                preco = (item["produto"] as HashMap<String, *>)["preco"] as Double
                            ),
                            quantidade = (item["quantidade"] as Long).toInt()
                        )
                    }

                    itemList.add(Purchase(id = it.key, valorTotal = map["valorTotal"] as Double, compradoEm = map["compradoEm"] as String?, items = purchaseItems))
                }

                if (itemList.isNotEmpty()) {
                    updateUI(itemList)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("PurchaseHistoryActivity", "loadPurchases:onCancelled", databaseError.toException())
            }
        }
        database?.child("purchases")?.addValueEventListener(purchaseListener)
    }

    fun updateUI(purchases: List<Purchase>) {
        binding.purchaseContainer.removeAllViews()
        purchases.forEach { purchase ->
            val purchaseBinding = PurchaseHistoryCardBinding.inflate(layoutInflater)

            purchaseBinding.txtPurchaseId.text = purchase.id
            purchaseBinding.txtPurchasedAt.text = purchase.compradoEm
            val items = purchase.items.map { item -> "${item.produto.nome} (${item.quantidade})" }

            purchaseBinding.txtProductList.text = items.joinToString(separator = ", ")

            purchaseBinding.btnPurchaseAgain.setOnClickListener { onPurchase(database, Purchase(items = purchase.items, valorTotal = purchase.valorTotal)) }

            binding.purchaseContainer.addView(purchaseBinding.root)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        menu?.let {
            menu.findItem(R.id.barbtnSearch).isVisible = false
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
