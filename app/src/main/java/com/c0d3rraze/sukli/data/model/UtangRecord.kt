package com.c0d3rraze.sukli.data.model

data class UtangRecord(
    val id: String,
    val customerId: String,
    val customerName: String,
    val amount: Int,
    val itemsDescription: String,
    val dayRecorded: Int,
    var isSettled: Boolean = false
)

