package com.felipenishino.sobala.utils

import com.felipenishino.sobala.R
import com.felipenishino.sobala.model.Cart
import com.felipenishino.sobala.model.Purchase
import com.felipenishino.sobala.model.PurchaseItem
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

fun getCurrentUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}

fun onPurchase(database: DatabaseReference?, purchase: Purchase) {
    val purchasesRef = database?.child("purchases")?.push()
    purchase.id = purchasesRef?.key
    purchasesRef?.setValue(purchase)

}