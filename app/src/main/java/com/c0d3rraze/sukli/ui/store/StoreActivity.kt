package com.c0d3rraze.sukli.ui.store

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.endofday.EndOfDayActivity

class StoreActivity : AppCompatActivity() {

    private val TAG = "SukliStore"
    private var currentTotal = 0
    private var customerIndex = 0
    private var dailyProfit = 0
    private var tapToAskCount = 0
    private var comprehensionScore = 100
    private var cashDrawer500 = 1
    private var cashDrawer100 = 2
    private var cashDrawer50 = 3
    private var cashDrawer20 = 5

    data class CustomerData(
        val name: String,
        val dialogues: List<String>,
        val expectedItems: List<Pair<String, Int>>
    )

    private val customers = listOf(
        CustomerData(
            "Student Boy",
            listOf(
                "Pabili ng Coke at Chippy. P500.",
                "Gusto ko ng Coke. Tapos Chippy. P500.",
                "Coke = 1. Chippy = 1."
            ),
            listOf("coke" to 1, "chippy" to 1)
        ),
        CustomerData(
            "Mang Jerry",
            listOf(
                "Paps, kape tsaka dalawang pandesal. P100.",
                "Kape = 1. Pandesal = 2. P100.",
                "Coffee = 1. Bread = 2."
            ),
            listOf("kopiko" to 1, "chippy" to 2)
        ),
        CustomerData(
            "Ate Girl",
            listOf(
                "Kuya, noodles at snack! P500 ito, bilisan mo please!",
                "Noodles = 1. Snack = 1. P500.",
                "Noodles: 1. Snack: 1."
            ),
            listOf("noodles" to 1, "chippy" to 1)
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        loadCustomer(0)

        val btnCoke = findViewById<Button>(R.id.btnCoke)
        val btnChippy = findViewById<Button>(R.id.btnChippy)
        val btnKopiko = findViewById<Button>(R.id.btnKopiko)
        val btnNoodles = findViewById<Button>(R.id.btnNoodles)

        btnCoke.setOnClickListener { addToTotal(25, "Coke") }
        btnChippy.setOnClickListener { addToTotal(12, "Chippy") }
        btnKopiko.setOnClickListener { addToTotal(7, "Kopiko") }
        btnNoodles.setOnClickListener { addToTotal(15, "Noodles") }

        val btnPay500 = findViewById<Button>(R.id.btnPay500)
        val tvChange = findViewById<TextView>(R.id.tvChange)

        btnPay500.setOnClickListener {
            val change = 500 - currentTotal
            if (canMakeChange(change)) {
                tvChange.text = "Change: P$change"
                tvChange.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // green
                Log.d(TAG, "Paid P500. Change: $change")
            } else {
                tvChange.text = "WALA AKONG SUKLI!"
                tvChange.setTextColor(android.graphics.Color.parseColor("#C62828")) // red
                Toast.makeText(this, "Not enough bills to make P$change change!", Toast.LENGTH_LONG).show()
                Log.d(TAG, "NO CHANGE for P$change")
            }
        }

        val btnServe = findViewById<Button>(R.id.btnServe)
        btnServe.setOnClickListener {
            if (currentTotal > 0) {
                dailyProfit += currentTotal
                Toast.makeText(this, "Served! Profit: P$dailyProfit", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Served customer $customerIndex. Total profit: $dailyProfit")

                customerIndex++
                if (customerIndex < customers.size) {
                    loadCustomer(customerIndex)
                } else {
                    val intent = Intent(this, EndOfDayActivity::class.java)
                    intent.putExtra("DAY_NUMBER", 1)
                    intent.putExtra("PROFIT", dailyProfit)
                    intent.putExtra("CUSTOMERS_SERVED", customers.size)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Add items first!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnTapToAsk = findViewById<Button>(R.id.btnTapToAsk)
        btnTapToAsk.setOnClickListener {
            if (tapToAskCount < customers[customerIndex].dialogues.size - 1) {
                tapToAskCount++
                comprehensionScore -= 20
                updateComprehensionText()
                findViewById<TextView>(R.id.tvDialogue).text = customers[customerIndex].dialogues[tapToAskCount]
                Toast.makeText(this, "Customer rephrased (${tapToAskCount}/2)", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Tap to Ask used. Level: $tapToAskCount")

                if (tapToAskCount >= customers[customerIndex].dialogues.size - 1) {
                    btnTapToAsk.isEnabled = false
                    btnTapToAsk.text = "No more hints"
                }
            }
        }
    }

    private fun loadCustomer(index: Int) {
        currentTotal = 0
        tapToAskCount = 0
        comprehensionScore = 100

        val customer = customers[index]
        findViewById<TextView>(R.id.tvCustomerName).text = customer.name
        findViewById<TextView>(R.id.tvDialogue).text = customer.dialogues[0]
        findViewById<TextView>(R.id.tvTotal).text = "Total: P0"
        findViewById<TextView>(R.id.tvChange).text = "Change: P0"

        val btnTapToAsk = findViewById<Button>(R.id.btnTapToAsk)
        btnTapToAsk.isEnabled = true
        btnTapToAsk.text = "? Tap to Ask"

        updateComprehensionText()
        Log.d(TAG, "Loaded customer: ${customer.name}")
    }

    private fun addToTotal(price: Int, itemName: String) {
        currentTotal += price
        findViewById<TextView>(R.id.tvTotal).text = "Total: P$currentTotal"
        Log.d(TAG, "Added $itemName. Total: $currentTotal")
    }

    private fun canMakeChange(changeAmount: Int): Boolean {
        var remaining = changeAmount
        val fiveHundreds = minOf(remaining / 500, cashDrawer500)
        remaining -= fiveHundreds * 500
        val oneHundreds = minOf(remaining / 100, cashDrawer100)
        remaining -= oneHundreds * 100
        val fifties = minOf(remaining / 50, cashDrawer50)
        remaining -= fifties * 50
        val twenties = minOf(remaining / 20, cashDrawer20)
        remaining -= twenties * 20
        return remaining == 0
    }

    private fun updateComprehensionText() {
        val text = when {
            comprehensionScore >= 100 -> "Comprehension: First Try"
            comprehensionScore >= 80 -> "Comprehension: Asked Once"
            comprehensionScore >= 60 -> "Comprehension: Asked Twice"
            else -> "Comprehension: Needed Help"
        }
        findViewById<TextView>(R.id.tvComprehension).text = text
    }
}