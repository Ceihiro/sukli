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
import com.c0d3rraze.sukli.ui.title.TitleActivity
import com.c0d3rraze.sukli.utils.JsonHelper

class StoreActivity : AppCompatActivity() {

    private val TAG = "SukliStore"
    private var currentTotal = 0
    private var customerIndex = 0
    private var dailyProfit = 0

    private val customerDialogues = listOf(
        "Pabili ng Coke at Chippy. P500." to listOf("coke" to 1, "chippy" to 1),
        "Kuya, isang Kopiko. P20." to listOf("kopiko" to 1)
    )

    private val customerNames = listOf("Student Boy", "Mang Jerry")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        loadCustomer(0)

        val btnCoke = findViewById<Button>(R.id.btnCoke)
        val btnChippy = findViewById<Button>(R.id.btnChippy)
        val btnKopiko = findViewById<Button>(R.id.btnKopiko)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)

        btnCoke.setOnClickListener { addToTotal(25, "Coke") }
        btnChippy.setOnClickListener { addToTotal(12, "Chippy") }
        btnKopiko.setOnClickListener { addToTotal(7, "Kopiko") }

        val btnPay500 = findViewById<Button>(R.id.btnPay500)
        val tvChange = findViewById<TextView>(R.id.tvChange)

        btnPay500.setOnClickListener {
            val change = 500 - currentTotal
            tvChange.text = "Change: P$change"
            Log.d(TAG, "Paid P500. Change: $change")
        }

        // NEW: Serve button
        val btnServe = findViewById<Button>(R.id.btnServe)
        btnServe.setOnClickListener {
            if (currentTotal > 0) {
                dailyProfit += currentTotal
                Toast.makeText(this, "Served! Profit: P$dailyProfit", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Served customer $customerIndex. Total profit: $dailyProfit")

                customerIndex++
                if (customerIndex < customerDialogues.size) {
                    loadCustomer(customerIndex)
                } else {
                    // All customers served - end of day
                    Toast.makeText(this, "Day Complete! Total Profit: P$dailyProfit", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Day complete. Profit: $dailyProfit")

                    val intent = Intent(this, EndOfDayActivity::class.java)
                    intent.putExtra("DAY_NUMBER", 1)
                    intent.putExtra("PROFIT", dailyProfit)
                    intent.putExtra("CUSTOMERS_SERVED", customerDialogues.size)
                    startActivity(intent)
                    finish()
                }
            } else {
                Toast.makeText(this, "Add items first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCustomer(index: Int) {
        currentTotal = 0
        findViewById<TextView>(R.id.tvCustomerName).text = customerNames[index]
        findViewById<TextView>(R.id.tvDialogue).text = customerDialogues[index].first
        findViewById<TextView>(R.id.tvTotal).text = "Total: P0"
        findViewById<TextView>(R.id.tvChange).text = "Change: P0"
        Log.d(TAG, "Loaded customer: ${customerNames[index]}")
    }

    private fun addToTotal(price: Int, itemName: String) {
        currentTotal += price
        findViewById<TextView>(R.id.tvTotal).text = "Total: P$currentTotal"
        Log.d(TAG, "Added $itemName. Total: $currentTotal")
    }
}