package com.c0d3rraze.sukli.ui.store

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.utils.JsonHelper

class StoreActivity : AppCompatActivity() {

    private val TAG = "SukliStore"
    private var currentTotal = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        val items = JsonHelper.loadItems(this)
        val customers = JsonHelper.loadCustomers(this)
        val firstCustomer = customers.first()

        Log.d(TAG, "Store opened with ${items.size} items")
        Log.d(TAG, "Serving customer: ${firstCustomer.name}")

        // Set customer name and dialogue
        findViewById<TextView>(R.id.tvCustomerName).text = firstCustomer.name
        findViewById<TextView>(R.id.tvDialogue).text = "Pabili ng Coke at Chippy. P500."

        // Item buttons
        val btnCoke = findViewById<Button>(R.id.btnCoke)
        val btnChippy = findViewById<Button>(R.id.btnChippy)
        val btnKopiko = findViewById<Button>(R.id.btnKopiko)
        val tvTotal = findViewById<TextView>(R.id.tvTotal)

        btnCoke.setOnClickListener {
            currentTotal += 25
            tvTotal.text = "Total: P$currentTotal"
            Log.d(TAG, "Added Coke. Total: $currentTotal")
        }

        btnChippy.setOnClickListener {
            currentTotal += 12
            tvTotal.text = "Total: P$currentTotal"
            Log.d(TAG, "Added Chippy. Total: $currentTotal")
        }

        btnKopiko.setOnClickListener {
            currentTotal += 7
            tvTotal.text = "Total: P$currentTotal"
            Log.d(TAG, "Added Kopiko. Total: $currentTotal")
        }

        // Cash drawer buttons
        val btnPay500 = findViewById<Button>(R.id.btnPay500)
        val tvChange = findViewById<TextView>(R.id.tvChange)

        btnPay500.setOnClickListener {
            val change = 500 - currentTotal
            tvChange.text = "Change: P$change"
            Log.d(TAG, "Paid P500. Change: $change")
        }
    }
}