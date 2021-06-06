package com.felipenishino.sobala.model

import java.text.SimpleDateFormat
import java.util.*


data class Purchase(
    var id: String? = null,
    val items: List<PurchaseItem>,
    val valorTotal: Double,
    val compradoEm: String? = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ROOT).format(Date())
)