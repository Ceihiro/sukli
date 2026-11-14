package com.c0d3rraze.sukli

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.ui.store.StoreActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Skip title screen for now - go straight to store
        startActivity(Intent(this, StoreActivity::class.java))
        finish()
    }
}