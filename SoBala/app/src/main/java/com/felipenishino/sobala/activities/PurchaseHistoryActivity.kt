package com.felipenishino.sobala.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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
                val purchases = dataSnapshot.children.forEach {
                    val map = it.getValue<HashMap<String, Any>>()


                    val items = map?.get("items") as List<*>
                    val purchaseItems = items.map { item: HashMap<String, *> -> {
                        val produto = item["produto"] as HashMap<*, *>

                        PurchaseItem(
                            produto = Product(
                                id = produto["id"] as Int,
                                nome = produto["nome"] as String,
                                marca = produto["id"] as String,
                                descricao = produto["descricao"] as String,
                                desconto = produto["desconto"] as Int,
                                linkImg = produto["linkImg"] as String,
                                preco = produto["preco"] as Double
                            ),
                            quantidade = item["quantidade"] as Int
                        )
                    } }

                    itemList.add(Purchase(id = it.key, valorTotal = map["valorTotal"] as Double, compradoEm = map["compradoEm"] as String?, items = itemList))
                }

                if (purchases != null) {
                    updateUI(purchases.values.toList())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("PurchaseHistoryActivity", "loadPurchases:onCancelled", databaseError.toException())
            }
        }
        database?.addValueEventListener(purchaseListener)
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
