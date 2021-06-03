package com.felipenishino.sobala.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

fun getCurrentUser(): FirebaseUser? {
    return FirebaseAuth.getInstance().currentUser
}
