package com.c0d3rraze.sukli.ui.title

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.store.StoreActivity

class TitleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val btnPlay = findViewById<Button>(R.id.btnPlay)
        val btnSettings = findViewById<Button>(R.id.btnSettings)

        tvTitle.text = "Sukli"

        btnPlay.setOnClickListener {
            startActivity(Intent(this, StoreActivity::class.java))
        }

        btnSettings.setOnClickListener {
            // Settings screen coming in v0.2
        }
    }
}