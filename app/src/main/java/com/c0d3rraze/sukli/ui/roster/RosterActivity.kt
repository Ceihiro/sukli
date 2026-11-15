package com.c0d3rraze.sukli.ui.roster

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.JsonHelper
import com.c0d3rraze.sukli.utils.SoundManager

class RosterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_roster)
        ImmersiveMode.setFullScreen(this)

        findViewById<Button>(R.id.btnBackFromRoster).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        renderRoster()
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    private fun renderRoster() {
        val container = findViewById<LinearLayout>(R.id.layoutRosterContainer)
        container.removeAllViews()

        val customers = JsonHelper.loadCustomers(this)

        customers.forEach { customer ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = getDrawable(R.drawable.bg_card_tindahan)
                elevation = 2f
                setPadding(16, 14, 16, 14)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 10) }
            }

            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val tvAvatar = TextView(this).apply {
                text = customer.avatarEmoji
                textSize = 22f
            }

            val tvName = TextView(this).apply {
                text = " ${customer.name}"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(Color.parseColor("#4A2E18"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvSchedule = TextView(this).apply {
                text = customer.schedule
                textSize = 11f
                setTextColor(Color.parseColor("#E65100"))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            headerRow.addView(tvAvatar)
            headerRow.addView(tvName)
            headerRow.addView(tvSchedule)
            card.addView(headerRow)

            val tvRole = TextView(this).apply {
                text = "Role: ${customer.role}"
                textSize = 11.5f
                setTextColor(Color.parseColor("#6D594E"))
                setPadding(0, 3, 0, 1)
            }
            card.addView(tvRole)

            val tvTrait = TextView(this).apply {
                text = "Trait: ${customer.specialTrait}"
                textSize = 11.5f
                setTextColor(Color.parseColor("#2E7D32"))
                setPadding(0, 1, 0, 1)
            }
            card.addView(tvTrait)

            val tvStory = TextView(this).apply {
                text = "\"${customer.storyNotes}\""
                textSize = 11f
                setTextColor(Color.parseColor("#795548"))
                setTypeface(null, android.graphics.Typeface.ITALIC)
                setPadding(0, 3, 0, 0)
            }
            card.addView(tvStory)

            container.addView(card)
        }
    }
}
