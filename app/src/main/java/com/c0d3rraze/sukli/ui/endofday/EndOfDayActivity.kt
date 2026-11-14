package com.c0d3rraze.sukli.ui.endofday

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.store.StoreActivity

class EndOfDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_of_day)

        val dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        val profit = intent.getIntExtra("PROFIT", 0)
        val customersServed = intent.getIntExtra("CUSTOMERS_SERVED", 0)

        findViewById<TextView>(R.id.tvDayComplete).text = "Day $dayNumber Complete!"
        findViewById<TextView>(R.id.tvProfit).text = "Profit: P$profit"
        findViewById<TextView>(R.id.tvCustomers).text = "Customers Served: $customersServed"

        // Star rating based on profit
        val stars = when {
            profit >= 100 -> "★★★"
            profit >= 50 -> "★★☆"
            profit > 0 -> "★☆☆"
            else -> "☆☆☆"
        }
        findViewById<TextView>(R.id.tvStars).text = stars

        findViewById<Button>(R.id.btnNextDay).setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            intent.putExtra("DAY_NUMBER", dayNumber + 1)
            startActivity(intent)
            finish()
        }
    }
}