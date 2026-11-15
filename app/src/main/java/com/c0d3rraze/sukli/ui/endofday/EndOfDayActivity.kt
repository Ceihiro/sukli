package com.c0d3rraze.sukli.ui.endofday

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.store.StoreActivity
import com.c0d3rraze.sukli.ui.title.TitleActivity
import com.c0d3rraze.sukli.utils.GameStateManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class EndOfDayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_end_of_day)
        ImmersiveMode.setFullScreen(this)

        val dayNumber = intent.getIntExtra("DAY_NUMBER", 1)
        val nextDay = intent.getIntExtra("NEXT_DAY_NUMBER", dayNumber + 1)
        val grossSales = intent.getIntExtra("GROSS_SALES", 0)
        val wholesaleCost = intent.getIntExtra("WHOLESALE_COST", 0)
        val electricBill = intent.getIntExtra("ELECTRIC_BILL", 20)
        val utangGiven = intent.getIntExtra("UTANG_GIVEN", 0)
        val profit = intent.getIntExtra("NET_PROFIT", 0)
        val customersServed = intent.getIntExtra("CUSTOMERS_SERVED", 0)
        val firstTryCount = intent.getIntExtra("FIRST_TRY_COUNT", customersServed)

        // Ensure next day is saved immediately so exiting to title preserves Day 3 instead of repeating Day 2
        GameStateManager.setCurrentDay(this, nextDay)
        GameStateManager.clearMidDayProgress(this)

        val state = GameStateManager.getGameState(this)

        findViewById<TextView>(R.id.tvDayComplete).text = "🌙 Day $dayNumber Completed!"
        findViewById<TextView>(R.id.tvChapterSubhead).text = state.getChapterTitle()
        findViewById<TextView>(R.id.tvGrossSales).text = "₱$grossSales"
        findViewById<TextView>(R.id.tvWholesaleCost).text = "-₱$wholesaleCost"
        findViewById<TextView>(R.id.tvElectricBill).text = "-₱$electricBill"
        findViewById<TextView>(R.id.tvUtangGiven).text = "₱$utangGiven"
        findViewById<TextView>(R.id.tvProfit).text = "₱$profit"
        findViewById<TextView>(R.id.tvCustomers).text = "👥 Customers Served: $customersServed"

        findViewById<TextView>(R.id.tvHospitalFundProgressText).text =
            "Lola Carmen's Medical Fund: ₱${state.hospitalFund} / ₱${state.hospitalTarget} Goal"
        val pbHospital = findViewById<ProgressBar>(R.id.pbHospitalFundReview)
        pbHospital.max = state.hospitalTarget
        pbHospital.progress = state.hospitalFund

        val literacyRate = if (customersServed > 0) (firstTryCount * 100) / customersServed else 100
        findViewById<TextView>(R.id.tvReadingComprehensionScore).text =
            "📖 Reading Comprehension: $literacyRate% ($firstTryCount/$customersServed on first try)"

        val starCount = when {
            profit >= 50 && literacyRate >= 75 -> 3
            profit >= 25 -> 2
            profit > 0 -> 1
            else -> 0
        }
        val stars = when (starCount) {
            3 -> "★★★"
            2 -> "★★☆"
            1 -> "★☆☆"
            else -> "☆☆☆"
        }
        findViewById<TextView>(R.id.tvStars).text = stars
        findViewById<android.widget.ImageView>(R.id.ivStar1)?.setImageResource(if (starCount >= 1) R.drawable.ui_star_filled else R.drawable.ui_star_empty)
        findViewById<android.widget.ImageView>(R.id.ivStar2)?.setImageResource(if (starCount >= 2) R.drawable.ui_star_filled else R.drawable.ui_star_empty)
        findViewById<android.widget.ImageView>(R.id.ivStar3)?.setImageResource(if (starCount >= 3) R.drawable.ui_star_filled else R.drawable.ui_star_empty)

        val advice = when {
            literacyRate == 100 -> "Teacher Fe's Tip: 'Outstanding, Kiko! You understood your customers on the first try with no hesitation!'"
            literacyRate >= 75 -> "Teacher Fe's Tip: 'Good reading comprehension! Just pay attention to slang and fast speech from rush commuters.'"
            else -> "Teacher Fe's Tip: 'Do not hesitate to tap for hints if a customer order is confusing. Practice builds reading fluency!'"
        }
        findViewById<TextView>(R.id.tvPerformanceAdvice).text = advice

        findViewById<Button>(R.id.btnNextDay).setOnClickListener {
            SoundManager.playClick()
            GameStateManager.setCurrentDay(this, nextDay)
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.btnRestockKuyaJoms).setOnClickListener {
            SoundManager.playClick()
            showSupplierRestockDialog()
        }

        findViewById<Button>(R.id.btnReturnToTitle).setOnClickListener {
            SoundManager.playClick()
            GameStateManager.setCurrentDay(this, nextDay)
            val intent = Intent(this, TitleActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    private fun showThemedDialog(layoutResId: Int, binder: (Dialog, View) -> Unit) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(layoutResId, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        binder(dialog, view)
        dialog.show()
        ImmersiveMode.setFullScreen(this)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun showSupplierRestockDialog() {
        showThemedDialog(R.layout.dialog_supplier_restock) { dialog, view ->
            val state = GameStateManager.getGameState(this)
            view.findViewById<TextView>(R.id.tvSupplierCashOnHand).text = "Cash: ₱${state.cash}"

            fun tryPurchase(cost: Int, bundleName: String) {
                val currentState = GameStateManager.getGameState(this)
                if (currentState.cash >= cost) {
                    GameStateManager.saveGameState(this, currentState.copy(cash = currentState.cash - cost))
                    SoundManager.playSuccess()
                    view.findViewById<TextView>(R.id.tvSupplierCashOnHand).text = "Cash: ₱${currentState.cash - cost}"
                    Toast.makeText(this, "Purchased $bundleName! Delivered to shelves.", Toast.LENGTH_SHORT).show()
                } else {
                    SoundManager.playError()
                    Toast.makeText(this, "Not enough cash for this bundle!", Toast.LENGTH_SHORT).show()
                }
            }

            view.findViewById<Button>(R.id.btnBundleA).setOnClickListener {
                tryPurchase(80, "Bundle A (Coffee + Pandesal)")
            }
            view.findViewById<Button>(R.id.btnBundleB).setOnClickListener {
                tryPurchase(180, "Bundle B (Soda + Chips)")
            }
            view.findViewById<Button>(R.id.btnBundleC).setOnClickListener {
                tryPurchase(280, "Bundle C (Noodles + Rice)")
            }
            view.findViewById<Button>(R.id.btnBundleD).setOnClickListener {
                tryPurchase(60, "Bundle D (Candles + Candy)")
            }

            view.findViewById<Button>(R.id.btnSupplierClose).setOnClickListener {
                SoundManager.playClick()
                dialog.dismiss()
            }
        }
    }
}
