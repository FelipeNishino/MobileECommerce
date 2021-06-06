package com.felipenishino.sobala.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityAccountBinding
import com.felipenishino.sobala.databinding.ActivityPurchaseHistoryBinding

class PurchaseHistoryActivity : AppCompatActivity() {
    lateinit var binding: ActivityPurchaseHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseHistoryBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)
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