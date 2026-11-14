package com.c0d3rraze.sukli.data.model

data class Customer(
    val id: String,
    val name: String,
    val portraitResId: Int,
    val schedule: String,
    val difficulty: Int,
    val defaultOrder: List<OrderLine>,
    val specialTrait: String
)