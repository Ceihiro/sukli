package com.c0d3rraze.sukli.ui.settings

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.utils.HapticManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_about)
        ImmersiveMode.setFullScreen(this)

        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            finish()
        }

        findViewById<View>(R.id.btnAboutBack)?.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }
}
