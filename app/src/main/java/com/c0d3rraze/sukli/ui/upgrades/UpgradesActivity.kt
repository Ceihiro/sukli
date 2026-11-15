package com.c0d3rraze.sukli.ui.upgrades

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.data.model.StoreUpgradesCatalog
import com.c0d3rraze.sukli.utils.GameStateManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class UpgradesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_upgrades)
        ImmersiveMode.setFullScreen(this)

        findViewById<Button>(R.id.btnBackFromUpgrades).setOnClickListener {
            SoundManager.playClick()
            finish()
        }

        renderUpgradesList()
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    private fun renderUpgradesList() {
        val container = findViewById<LinearLayout>(R.id.layoutUpgradesContainer)
        container.removeAllViews()

        val state = GameStateManager.getGameState(this)
        val catalog = StoreUpgradesCatalog.getDefaultUpgrades()

        val tierName = when (state.storeTier) {
            1 -> "Tier 1: Lola Carmen's Original Kiosk"
            2 -> "Tier 2: Repainted Store with Fan"
            3 -> "Tier 3: Modern Sari-Sari (Cold Cooler)"
            4 -> "Tier 4: Corner Mini-Grocery"
            else -> "Tier 5: Barangay Store Chain"
        }
        findViewById<TextView>(R.id.tvCurrentStoreTier).text = "Current Store Tier: $tierName"
        findViewById<TextView>(R.id.tvCurrentCashAvailable).text = "Operating Cash: ₱${state.cash}"

        catalog.forEach { upgrade ->
            val isPurchased = state.purchasedUpgradeIds.contains(upgrade.id)

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

            val tvTitle = TextView(this).apply {
                text = "${upgrade.iconEmoji} ${upgrade.name}"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(Color.parseColor("#4A2E18"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val tvCost = TextView(this).apply {
                text = "₱${upgrade.cost}"
                textSize = 15f
                setTypeface(null, android.graphics.Typeface.BOLD)
                setTextColor(if (upgrade.isLocked) Color.parseColor("#757575") else Color.parseColor("#2E7D32"))
            }

            headerRow.addView(tvTitle)
            headerRow.addView(tvCost)
            card.addView(headerRow)

            val tvDesc = TextView(this).apply {
                text = upgrade.description
                textSize = 12f
                setTextColor(Color.parseColor("#6D594E"))
                setPadding(0, 4, 0, 2)
            }
            card.addView(tvDesc)

            val tvBenefit = TextView(this).apply {
                text = "⚡ Benefit: ${upgrade.benefit}"
                textSize = 12f
                setTextColor(Color.parseColor("#E65100"))
                setPadding(0, 0, 0, 6)
            }
            card.addView(tvBenefit)

            val btnBuy = Button(this).apply {
                when {
                    isPurchased -> {
                        text = "✅ PURCHASED (ACTIVE)"
                        isEnabled = false
                        setBackgroundColor(Color.parseColor("#9E9E9E"))
                    }
                    upgrade.isLocked -> {
                        text = "🔒 ${upgrade.lockReason}"
                        isEnabled = false
                        setBackgroundColor(Color.parseColor("#757575"))
                        setTextColor(Color.WHITE)
                    }
                    else -> {
                        text = "PURCHASE FOR ₱${upgrade.cost}"
                        setBackgroundColor(Color.parseColor("#1976D2"))
                        setTextColor(Color.WHITE)
                        setOnClickListener {
                            val success = GameStateManager.purchaseUpgrade(this@UpgradesActivity, upgrade.id, upgrade.cost)
                            if (success) {
                                SoundManager.playSuccess()
                                Toast.makeText(this@UpgradesActivity, "Purchased ${upgrade.name}!", Toast.LENGTH_SHORT).show()
                                renderUpgradesList()
                            } else {
                                SoundManager.playError()
                                Toast.makeText(this@UpgradesActivity, "Not enough cash!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            card.addView(btnBuy)

            container.addView(card)
        }
    }
}
