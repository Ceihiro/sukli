package com.c0d3rraze.sukli

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.utils.JsonHelper

class MainActivity : AppCompatActivity() {

    private val TAG = "SukliTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val items = JsonHelper.loadItems(this)
        val customers = JsonHelper.loadCustomers(this)

        Log.d(TAG, "Loaded ${items.size} items")
        Log.d(TAG, "First item: ${items.firstOrNull()?.name}")
        Log.d(TAG, "Loaded ${customers.size} customers")
        Log.d(TAG, "First customer: ${customers.firstOrNull()?.name}")
    }
}