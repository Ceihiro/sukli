package com.c0d3rraze.sukli.ui.utang

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.utils.GameStateManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class UtangActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_utang)
        ImmersiveMode.setFullScreen(this)

        findViewById<Button>(R.id.btnBackFromUtang).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        renderUtangList()
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    private fun renderUtangList() {
        val container = findViewById<LinearLayout>(R.id.layoutUtangContainer)
        container.removeAllViews()

        val state = GameStateManager.getGameState(this)
        val records = state.utangRecords

        val uncollectedTotal = records.filter { !it.isSettled }.sumOf { it.amount }
        findViewById<TextView>(R.id.tvTotalUtangCollectibles).text =
            "Total Uncollected Credit: ₱$uncollectedTotal"

        if (records.isEmpty()) {
            val emptyTv = TextView(this).apply {
                text = "No active credit records. All customer accounts are settled!"
                textSize = 14f
                setTextColor(Color.parseColor("#757575"))
                setPadding(0, 32, 0, 0)
                gravity = android.view.Gravity.CENTER
            }
            container.addView(emptyTv)
            return
        }

        records.forEach { record ->
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

            val topRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            val tvCustomer = TextView(this).apply {
                text = "👤 ${record.customerName}"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(Color.parseColor("#4A2E18"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvAmount = TextView(this).apply {
                text = "₱${record.amount}"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(if (record.isSettled) Color.parseColor("#757575") else Color.parseColor("#C62828"))
            }

            topRow.addView(tvCustomer)
            topRow.addView(tvAmount)
            card.addView(topRow)

            val tvItems = TextView(this).apply {
                text = "Items: ${record.itemsDescription} (Day ${record.dayRecorded})"
                textSize = 12f
                setTextColor(Color.parseColor("#6D594E"))
                setPadding(0, 4, 0, 6)
            }
            card.addView(tvItems)

            if (!record.isSettled) {
                val btnSettle = Button(this).apply {
                    text = "💵 COLLECT PAYMENT (PAYDAY SETTLEMENT)"
                    textSize = 12f
                    setBackgroundColor(Color.parseColor("#2E7D32"))
                    setTextColor(Color.WHITE)
                    setOnClickListener {
                        val settled = GameStateManager.settleUtang(this@UtangActivity, record.id)
                        if (settled) {
                            SoundManager.playSuccess()
                            Toast.makeText(this@UtangActivity, "Collected ₱${record.amount} from ${record.customerName}!", Toast.LENGTH_SHORT).show()
                            renderUtangList()
                        }
                    }
                }
                card.addView(btnSettle)
            } else {
                val tvPaid = TextView(this).apply {
                    text = "✅ PAID IN FULL"
                    textSize = 12f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setTextColor(Color.parseColor("#2E7D32"))
                }
                card.addView(tvPaid)
            }

            container.addView(card)
        }
    }
}
