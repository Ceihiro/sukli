package com.c0d3rraze.sukli.data.model

data class CashDrawer(
    val bills20: Int = 5,
    val bills50: Int = 3,
    val bills100: Int = 2,
    val bills500: Int = 1,
    val coins1: Int = 10,
    val coins5: Int = 5,
    val coins25c: Int = 10
) {
    fun totalValue(): Int {
        return (bills20 * 20) + (bills50 * 50) + (bills100 * 100) +
                (bills500 * 500) + (coins1 * 1) + (coins5 * 5)
    }
}