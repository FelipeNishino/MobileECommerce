package com.felipenishino.sobala.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.felipenishino.sobala.R
import com.felipenishino.sobala.databinding.ActivityAboutBinding


class AboutActivity : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(binding.root)

        binding.txtNishino.setOnClickListener { sendEmail("felipenishino@gmail.com") }
        binding.txtYamauchi.setOnClickListener { sendEmail("yamauchi_gu@hotmail.com") }
        binding.txtBarao.setOnClickListener { sendEmail("marcobaraoneves@gmail.com") }

        binding.imgbtnAdSenac.setOnClickListener {
                val uri = Uri.parse("https://www.sp.senac.br")
                CustomTabsIntent.Builder()
                        .build()
                        .launchUrl(this, uri)
        }
    }

    fun sendEmail(address: String) {
        val i = Intent(Intent.ACTION_SEND)
        i.type = "message/rfc822"
        i.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        i.putExtra(Intent.EXTRA_SUBJECT, "")
        i.putExtra(Intent.EXTRA_TEXT, "")
        try {
            startActivity(Intent.createChooser(i, getString(R.string.sendMail)))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, R.string.noEmailClient, Toast.LENGTH_SHORT).show()
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