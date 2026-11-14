package com.c0d3rraze.sukli.data.model

data class Item(
    val id: String,
    val name: String,
    val price: Int,
    val iconResId: Int,
    val category: String,
    val supplierCost: Int
)