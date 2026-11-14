package com.c0d3rraze.sukli.data.model

data class Order(
    val customerId: String,
    val items: List<OrderLine>,
    val dialogueText: String,
    val difficulty: Int
)