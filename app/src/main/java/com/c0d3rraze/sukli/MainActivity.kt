package com.c0d3rraze.sukli

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.ui.title.TitleActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, TitleActivity::class.java))
        finish()
    }
}