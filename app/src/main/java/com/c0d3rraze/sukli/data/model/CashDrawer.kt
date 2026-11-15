package com.c0d3rraze.sukli.data.model

data class ChangeBreakdown(
    val bills500: Int = 0,
    val bills100: Int = 0,
    val bills50: Int = 0,
    val bills20: Int = 0,
    val coins10: Int = 0,
    val coins5: Int = 0,
    val coins1: Int = 0,
    val candies: Int = 0
) {
    fun totalValue(): Int {
        return (bills500 * 500) + (bills100 * 100) + (bills50 * 50) + (bills20 * 20) +
                (coins10 * 10) + (coins5 * 5) + (coins1 * 1) + (candies * 1)
    }

    fun hasAnyChange(): Boolean {
        return bills500 > 0 || bills100 > 0 || bills50 > 0 || bills20 > 0 ||
                coins10 > 0 || coins5 > 0 || coins1 > 0 || candies > 0
    }
}

data class CashDrawer(
    var bills500: Int = 1,
    var bills100: Int = 2,
    var bills50: Int = 3,
    var bills20: Int = 5,
    var coins10: Int = 8,
    var coins5: Int = 10,
    var coins1: Int = 15,
    var candies: Int = 20
) {
    fun totalValue(): Int {
        return (bills500 * 500) + (bills100 * 100) + (bills50 * 50) + (bills20 * 20) +
                (coins10 * 10) + (coins5 * 5) + (coins1 * 1) + (candies * 1)
    }

    /**
     * Checks if exact change can be formed with current cash drawer inventory
     */
    fun canMakeChange(amount: Int, allowCandy: Boolean = false): Boolean {
        if (amount == 0) return true
        if (amount < 0) return false

        var remaining = amount

        val b500 = minOf(remaining / 500, bills500)
        remaining -= b500 * 500

        val b100 = minOf(remaining / 100, bills100)
        remaining -= b100 * 100

        val b50 = minOf(remaining / 50, bills50)
        remaining -= b50 * 50

        val b20 = minOf(remaining / 20, bills20)
        remaining -= b20 * 20

        val c10 = minOf(remaining / 10, coins10)
        remaining -= c10 * 10

        val c5 = minOf(remaining / 5, coins5)
        remaining -= c5 * 5

        val c1 = minOf(remaining / 1, coins1)
        remaining -= c1 * 1

        if (remaining == 0) return true

        if (allowCandy) {
            val candyUsed = minOf(remaining, candies)
            remaining -= candyUsed
        }

        return remaining == 0
    }

    /**
     * Greedy suggested change breakdown
     */
    fun suggestChange(amount: Int, allowCandy: Boolean = true): ChangeBreakdown? {
        if (amount == 0) return ChangeBreakdown()
        if (amount < 0) return null

        var rem = amount
        val b500 = minOf(rem / 500, bills500)
        rem -= b500 * 500

        val b100 = minOf(rem / 100, bills100)
        rem -= b100 * 100

        val b50 = minOf(rem / 50, bills50)
        rem -= b50 * 50

        val b20 = minOf(rem / 20, bills20)
        rem -= b20 * 20

        val c10 = minOf(rem / 10, coins10)
        rem -= c10 * 10

        val c5 = minOf(rem / 5, coins5)
        rem -= c5 * 5

        val c1 = minOf(rem / 1, coins1)
        rem -= c1 * 1

        var candyUsed = 0
        if (rem > 0 && allowCandy) {
            candyUsed = minOf(rem, candies)
            rem -= candyUsed
        }

        return if (rem == 0) {
            ChangeBreakdown(b500, b100, b50, b20, c10, c5, c1, candyUsed)
        } else {
            null
        }
    }

    fun applyChange(breakdown: ChangeBreakdown): Boolean {
        if (bills500 < breakdown.bills500 ||
            bills100 < breakdown.bills100 ||
            bills50 < breakdown.bills50 ||
            bills20 < breakdown.bills20 ||
            coins10 < breakdown.coins10 ||
            coins5 < breakdown.coins5 ||
            coins1 < breakdown.coins1 ||
            candies < breakdown.candies
        ) {
            return false
        }

        bills500 -= breakdown.bills500
        bills100 -= breakdown.bills100
        bills50 -= breakdown.bills50
        bills20 -= breakdown.bills20
        coins10 -= breakdown.coins10
        coins5 -= breakdown.coins5
        coins1 -= breakdown.coins1
        candies -= breakdown.candies
        return true
    }

    fun addCustomerPayment(amount: Int) {
        // Add bill to drawer according to denomination
        when (amount) {
            1000 -> {
                // Customer gave 1000, we accept it as big bill
                // Drawer can track 500s or convert
                bills500 += 2
            }
            500 -> bills500++
            100 -> bills100++
            50 -> bills50++
            20 -> bills20++
            10 -> coins10++
            5 -> coins5++
            1 -> coins1++
            else -> {
                // Break down arbitrary payment amount
                var rem = amount
                val b500 = rem / 500
                bills500 += b500
                rem %= 500

                val b100 = rem / 100
                bills100 += b100
                rem %= 100

                val b50 = rem / 50
                bills50 += b50
                rem %= 50

                val b20 = rem / 20
                bills20 += b20
                rem %= 20

                val c10 = rem / 10
                coins10 += c10
                rem %= 10

                val c5 = rem / 5
                coins5 += c5
                rem %= 5

                coins1 += rem
            }
        }
    }
}
