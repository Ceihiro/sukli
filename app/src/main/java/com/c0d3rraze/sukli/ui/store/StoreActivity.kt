package com.c0d3rraze.sukli.ui.store

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Dialog
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.data.model.GameState
import com.c0d3rraze.sukli.ui.endofday.EndOfDayActivity
import com.c0d3rraze.sukli.ui.title.TitleActivity
import com.c0d3rraze.sukli.utils.GameStateManager
import com.c0d3rraze.sukli.utils.HapticManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class StoreActivity : AppCompatActivity() {

    // ══════════════════════════════════════════════════════════════
    // DATA CLASSES
    // ══════════════════════════════════════════════════════════════

    data class StoreCustomer(
        val name: String,
        val drawableRes: Int,
        val dialogues: List<String>,
        val expected: Map<String, Int>,
        val payment: Int,
        val ambientSoundCue: String = "",
        val ambientStoryNotice: String = ""
    )

    // ══════════════════════════════════════════════════════════════
    // STATE
    // ══════════════════════════════════════════════════════════════

    private lateinit var gameState: GameState

    private val itemPrices = mapOf(
        "coke" to 25,
        "chippy" to 12,
        "kopiko" to 7,
        "noodles" to 15
    )
    private val itemCosts = mapOf(
        "coke" to 20,
        "chippy" to 9,
        "kopiko" to 5,
        "noodles" to 11
    )
    private val itemDisplayNames = mapOf(
        "coke" to "Coke",
        "chippy" to "Chippy",
        "kopiko" to "Kopiko",
        "noodles" to "Noodles"
    )

    // Cash denominations: key -> piso value
    private val denomValues = mapOf(
        "bill20" to 20,
        "coin10" to 10,
        "coin5" to 5,
        "coin1" to 1,
        "candy" to 1
    )

    private var customers = listOf<StoreCustomer>()
    private var customerIndex = 0

    // Basket (items on the counter tray)
    private val currentBasket = mutableMapOf<String, Int>()
    private var basketTotal = 0

    // Change-giving mechanic (I Have No Change style)
    private val changeTray = mutableMapOf<String, Int>()
    private var changeGiven = 0

    // Comprehension tracking
    private var tapToAskCount = 0
    private var comprehensionScore = 100
    private var hasCustomerPaid = false

    // Day tracking
    private var dailyProfit = 0
    private var dailyGrossSales = 0
    private var dailyWholesaleCost = 0
    private var firstTryComprehensionCount = 0

    // Idle animation
    private var characterIdleAnimator: ObjectAnimator? = null

    // Calculator state
    private var calcDisplayStr = "0"
    private var calcHistoryStr = ""
    private var calcStoredVal = 0.0
    private var calcPendingOp = ""
    private var calcIsNewInput = true

    // ══════════════════════════════════════════════════════════════
    // VIEWS
    // ══════════════════════════════════════════════════════════════

    private lateinit var tvDayNumber: TextView
    private lateinit var tvTotalProfit: TextView
    private lateinit var btnHeaderCalc: View
    private lateinit var btnPause: View

    private lateinit var ivCharacter: ImageView
    private lateinit var speechBubble: View
    private lateinit var tvCustomerName: TextView
    private lateinit var tvDialogue: TextView
    private lateinit var tvComprehension: TextView
    private lateinit var btnAsk: Button

    private lateinit var counterSurface: View
    private lateinit var ivCashRegister: ImageView
    private lateinit var tvOrderDue: TextView
    private lateinit var tvChange: TextView
    private lateinit var layoutCounterTrayArea: View
    private lateinit var scrollCounterTray: View
    private lateinit var layoutCounterItems: LinearLayout
    private lateinit var btnServe: Button

    private lateinit var itemShelf: View
    private lateinit var tvTotal: TextView
    private lateinit var btnPay500: Button

    private lateinit var btnCoke: View
    private lateinit var btnChippy: View
    private lateinit var btnKopiko: View
    private lateinit var btnNoodles: View

    // Cash drawer views
    private lateinit var layoutCashDrawer: View
    private lateinit var tvChangeDue: TextView
    private lateinit var tvChangeGiven: TextView
    private lateinit var tvChangeStatus: TextView
    private lateinit var tvChangeTrayContents: TextView
    private lateinit var btnBill20: View
    private lateinit var btnCoin10: View
    private lateinit var btnCoin5: View
    private lateinit var btnCoin1: View
    private lateinit var btnCandyDenom: View

    // Side calculator views
    private lateinit var layoutSideCalculator: View
    private lateinit var tvSideCalcDisplay: TextView
    private lateinit var tvSideCalcHistory: TextView

    // ══════════════════════════════════════════════════════════════
    // LIFECYCLE
    // ══════════════════════════════════════════════════════════════

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_store)
        ImmersiveMode.setFullScreen(this)

        gameState = GameStateManager.getGameState(this)

        initViews()
        setupCustomers()
        setupListeners()
        setupDragAndDrop()

        // Resume mid-day if save exists
        if (gameState.hasActiveSave && gameState.savedCustomerIndex < customers.size) {
            customerIndex = gameState.savedCustomerIndex
            dailyProfit = gameState.savedDailyProfit
            dailyGrossSales = gameState.savedDailyGrossSales
            dailyWholesaleCost = gameState.savedDailyWholesaleCost
            showGameToast("Balik na! Day ${gameState.dayNumber}, Customer ${customerIndex + 1} 🏪")
        } else {
            customerIndex = 0
            dailyProfit = 0
            dailyGrossSales = 0
            dailyWholesaleCost = 0
        }

        loadCustomer(customerIndex)
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        characterIdleAnimator?.cancel()
    }

    // ══════════════════════════════════════════════════════════════
    // HELPER: TACTILE BOUNCE ANIMATION
    // ══════════════════════════════════════════════════════════════

    private fun View.bounce() {
        HapticManager.tap(this)
        this.animate()
            .scaleX(0.90f).scaleY(0.90f)
            .setDuration(60)
            .withEndAction {
                this.animate()
                    .scaleX(1.0f).scaleY(1.0f)
                    .setDuration(140)
                    .setInterpolator(OvershootInterpolator(2.5f))
                    .start()
            }.start()
    }

    private fun showGameToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    // ══════════════════════════════════════════════════════════════
    // INIT VIEWS
    // ══════════════════════════════════════════════════════════════

    private fun initViews() {
        tvDayNumber = findViewById(R.id.tvDayNumber)
        tvTotalProfit = findViewById(R.id.tvTotalProfit)
        btnHeaderCalc = findViewById(R.id.btnHeaderCalc)
        btnPause = findViewById(R.id.btnPause)

        ivCharacter = findViewById(R.id.ivCharacter)
        speechBubble = findViewById(R.id.speechBubble)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvDialogue = findViewById(R.id.tvDialogue)
        tvComprehension = findViewById(R.id.tvComprehension)
        btnAsk = findViewById(R.id.btnAsk)

        counterSurface = findViewById(R.id.counterSurface)
        ivCashRegister = findViewById(R.id.ivCashRegister)
        tvOrderDue = findViewById(R.id.tvOrderDue)
        tvChange = findViewById(R.id.tvChange)
        layoutCounterTrayArea = findViewById(R.id.layoutCounterTrayArea)
        scrollCounterTray = findViewById(R.id.scrollCounterTray)
        layoutCounterItems = findViewById(R.id.layoutCounterItems)
        btnServe = findViewById(R.id.btnServe)

        itemShelf = findViewById(R.id.itemShelf)
        tvTotal = findViewById(R.id.tvTotal)
        btnPay500 = findViewById(R.id.btnPay500)

        btnCoke = findViewById(R.id.btnCoke)
        btnChippy = findViewById(R.id.btnChippy)
        btnKopiko = findViewById(R.id.btnKopiko)
        btnNoodles = findViewById(R.id.btnNoodles)

        // Cash drawer
        layoutCashDrawer = findViewById(R.id.layoutCashDrawer)
        tvChangeDue = findViewById(R.id.tvChangeDue)
        tvChangeGiven = findViewById(R.id.tvChangeGiven)
        tvChangeStatus = findViewById(R.id.tvChangeStatus)
        tvChangeTrayContents = findViewById(R.id.tvChangeTrayContents)
        btnBill20 = findViewById(R.id.btnBill20)
        btnCoin10 = findViewById(R.id.btnCoin10)
        btnCoin5 = findViewById(R.id.btnCoin5)
        btnCoin1 = findViewById(R.id.btnCoin1)
        btnCandyDenom = findViewById(R.id.btnCandyDenom)

        // Side calculator
        layoutSideCalculator = findViewById(R.id.layoutSideCalculator)
        tvSideCalcDisplay = findViewById(R.id.tvSideCalcDisplay)
        tvSideCalcHistory = findViewById(R.id.tvSideCalcHistory)

        // Set initial header text
        tvDayNumber.text = "☀️ Day ${gameState.dayNumber} • 7:00 AM"
        tvTotalProfit.text = "₱$dailyProfit"

        // SERVE starts disabled until items placed
        btnServe.isEnabled = false
        btnServe.alpha = 0.5f
    }

    // ══════════════════════════════════════════════════════════════
    // CUSTOMER DATA
    // ══════════════════════════════════════════════════════════════

    private fun setupCustomers() {
        customers = listOf(
            StoreCustomer(
                name = "Popoy (Grade 4)",
                drawableRes = R.drawable.char_student_boy_neutral,
                dialogues = listOf(
                    "Kuya Kiko, pabili ng Coke tsaka Chippy. Eto ₱50.",
                    "Isang Coke at isang Chippy pambaon.",
                    "1x Coke (₱25) + 1x Chippy (₱12) = ₱37. Sukli: ₱13."
                ),
                expected = mapOf("coke" to 1, "chippy" to 1),
                payment = 50,
                ambientSoundCue = "ambience_school_bell",
                ambientStoryNotice = "🔔 Narinig ang school bell mula sa kanto!"
            ),
            StoreCustomer(
                name = "Mang Jerry (Trike Driver)",
                drawableRes = R.drawable.char_mang_jerry_neutral,
                dialogues = listOf(
                    "Kiko boy! Pabili ng kape tsaka noodles. Eto ₱50.",
                    "Isang Kopiko at isang noodles lang, pre.",
                    "1x Kopiko (₱7) + 1x Noodles (₱15) = ₱22. Sukli: ₱28."
                ),
                expected = mapOf("kopiko" to 1, "noodles" to 1),
                payment = 50,
                ambientSoundCue = "ambience_tricycle",
                ambientStoryNotice = "🛺 Tricycle engine putters sa labas!"
            ),
            StoreCustomer(
                name = "Ate Michelle (Reviewer)",
                drawableRes = R.drawable.char_ate_girl_neutral,
                dialogues = listOf(
                    "Kuya Kiko, dalawang Chippy tsaka Coke. Eto ₱100.",
                    "Dalawang Chippy po at isang Coke.",
                    "2x Chippy (₱24) + 1x Coke (₱25) = ₱49. Sukli: ₱51."
                ),
                expected = mapOf("chippy" to 2, "coke" to 1),
                payment = 100,
                ambientSoundCue = "ambience_ice_cream_bell",
                ambientStoryNotice = "🍦 Dirty ice cream bell sa eskinita!"
            ),
            StoreCustomer(
                name = "Aling Myrna (Kumare ni Lola)",
                drawableRes = R.drawable.char_ate_girl_neutral,
                dialogues = listOf(
                    "Kiko, dalawang noodles at isang kape. Eto ₱500.",
                    "Dalawang noodles at isang Kopiko lang.",
                    "2x Noodles (₱30) + 1x Kopiko (₱7) = ₱37. Sukli: ₱463."
                ),
                expected = mapOf("noodles" to 2, "kopiko" to 1),
                payment = 500,
                ambientSoundCue = "ambience_church_bells",
                ambientStoryNotice = "⛪ Kampana ng chapel para sa morning novena!"
            )
        )
    }

    // ══════════════════════════════════════════════════════════════
    // LISTENERS
    // ══════════════════════════════════════════════════════════════

    private fun setupListeners() {
        // Shelf item taps
        btnCoke.setOnClickListener { it.bounce(); addItem("coke") }
        btnChippy.setOnClickListener { it.bounce(); addItem("chippy") }
        btnKopiko.setOnClickListener { it.bounce(); addItem("kopiko") }
        btnNoodles.setOnClickListener { it.bounce(); addItem("noodles") }

        // Pay button
        btnPay500.setOnClickListener { it.bounce(); handleCustomerPayment() }

        // Ask for hint
        btnAsk.setOnClickListener { it.bounce(); handleAsk() }

        // Serve customer
        btnServe.setOnClickListener { it.bounce(); handleServe() }

        // Cash drawer denomination buttons
        btnBill20.setOnClickListener { it.bounce(); addDenomToChangeTray("bill20") }
        btnCoin10.setOnClickListener { it.bounce(); addDenomToChangeTray("coin10") }
        btnCoin5.setOnClickListener { it.bounce(); addDenomToChangeTray("coin5") }
        btnCoin1.setOnClickListener { it.bounce(); addDenomToChangeTray("coin1") }
        btnCandyDenom.setOnClickListener { it.bounce(); addDenomToChangeTray("candy") }

        // Clear / Reset change tray button (for children and easy undo)
        findViewById<View>(R.id.btnResetChange)?.setOnClickListener {
            it.bounce()
            clearChangeTray()
            SoundManager.playClick()
            showGameToast("Ibinalik ang lahat ng barya sa drawer! ↺")
        }

        // Long-press to remove denomination (take coin back)
        btnBill20.setOnLongClickListener { removeDenomFromChangeTray("bill20"); true }
        btnCoin10.setOnLongClickListener { removeDenomFromChangeTray("coin10"); true }
        btnCoin5.setOnLongClickListener { removeDenomFromChangeTray("coin5"); true }
        btnCoin1.setOnLongClickListener { removeDenomFromChangeTray("coin1"); true }
        btnCandyDenom.setOnLongClickListener { removeDenomFromChangeTray("candy"); true }

        // Calculator toggle
        btnHeaderCalc.setOnClickListener { it.bounce(); SoundManager.playClick(); toggleSideCalculator() }
        ivCashRegister.setOnClickListener { it.bounce(); SoundManager.playClick(); toggleSideCalculator() }
        findViewById<View>(R.id.btnSideCalcClose)?.setOnClickListener {
            SoundManager.playClick(); toggleSideCalculator()
        }

        // Calculator keypad
        setupSideCalculatorKeys()

        // Pause
        btnPause.setOnClickListener { it.bounce(); SoundManager.playClick(); showPauseDialog() }
    }

    // ══════════════════════════════════════════════════════════════
    // DRAG AND DROP
    // ══════════════════════════════════════════════════════════════

    private fun setupDragAndDrop() {
        val shelfItems = mapOf(
            btnCoke to "coke",
            btnChippy to "chippy",
            btnKopiko to "kopiko",
            btnNoodles to "noodles"
        )

        // Start drag from shelf (long press)
        shelfItems.forEach { (view, itemId) ->
            view.setOnLongClickListener { v ->
                val clipData = ClipData.newPlainText("item_id", itemId)
                val shadow = View.DragShadowBuilder(v)
                v.startDragAndDrop(clipData, shadow, itemId, 0)
                HapticManager.tap(v)
                true
            }
        }

        // Drop target: counter surface and tray
        val counterDragListener = View.OnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> true
                DragEvent.ACTION_DRAG_ENTERED -> { counterSurface.alpha = 0.80f; true }
                DragEvent.ACTION_DRAG_EXITED -> { counterSurface.alpha = 1.0f; true }
                DragEvent.ACTION_DROP -> {
                    counterSurface.alpha = 1.0f
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        val itemId = clipData.getItemAt(0).text.toString()
                        if (itemPrices.containsKey(itemId)) {
                            addItem(itemId)
                            HapticManager.tap(counterSurface)
                        }
                    }
                    true
                }
                DragEvent.ACTION_DRAG_ENDED -> { counterSurface.alpha = 1.0f; true }
                else -> false
            }
        }

        counterSurface.setOnDragListener(counterDragListener)
        scrollCounterTray.setOnDragListener(counterDragListener)

        // Drop target: shelf (return items)
        itemShelf.setOnDragListener { _, event ->
            when (event.action) {
                DragEvent.ACTION_DROP -> {
                    val clipData = event.clipData
                    if (clipData != null && clipData.itemCount > 0) {
                        val itemId = clipData.getItemAt(0).text.toString()
                        if (itemPrices.containsKey(itemId) && (currentBasket[itemId] ?: 0) > 0) {
                            removeItem(itemId)
                            HapticManager.tap(itemShelf)
                        }
                    }
                    true
                }
                else -> true
            }
        }
    }

    // ══════════════════════════════════════════════════════════════
    // BASKET LOGIC
    // ══════════════════════════════════════════════════════════════

    private fun addItem(itemId: String) {
        currentBasket[itemId] = (currentBasket[itemId] ?: 0) + 1
        SoundManager.playClick()
        updateBasketUI()
    }

    private fun removeItem(itemId: String) {
        val qty = currentBasket[itemId] ?: 0
        if (qty > 1) currentBasket[itemId] = qty - 1 else currentBasket.remove(itemId)
        SoundManager.playClick()
        updateBasketUI()
    }

    private fun updateBasketUI() {
        // Recalculate total
        basketTotal = 0
        currentBasket.forEach { (id, qty) -> basketTotal += (itemPrices[id] ?: 0) * qty }

        tvTotal.text = "Total: ₱$basketTotal"
        tvOrderDue.text = "Order: ₱$basketTotal"

        // Enable SERVE only if: items placed AND (paid OR no change needed)
        val hasItems = currentBasket.isNotEmpty()
        if (!hasCustomerPaid) {
            btnServe.isEnabled = hasItems
            btnServe.alpha = if (hasItems) 1.0f else 0.5f
        }

        // Update pay button label
        if (customerIndex < customers.size) {
            val customer = customers[customerIndex]
            btnPay500.text = "Bayad ₱${customer.payment}"
        }

        renderCounterTray()
        updateChangeTrayUI()
    }

    // ══════════════════════════════════════════════════════════════
    // CHANGE DRAWER MECHANIC (I Have No Change style)
    // ══════════════════════════════════════════════════════════════

    private fun clearChangeTray() {
        changeTray.clear()
        changeGiven = 0
        updateChangeTrayUI()
    }

    private fun addDenomToChangeTray(denom: String) {
        val value = denomValues[denom] ?: return
        changeTray[denom] = (changeTray[denom] ?: 0) + 1
        changeGiven += value
        SoundManager.playCoin()
        HapticManager.tap(layoutCashDrawer)
        updateChangeTrayUI()
    }

    private fun removeDenomFromChangeTray(denom: String) {
        val current = changeTray[denom] ?: 0
        if (current > 0) {
            val value = denomValues[denom] ?: 0
            if (current == 1) changeTray.remove(denom) else changeTray[denom] = current - 1
            changeGiven -= value
            SoundManager.playClick()
            HapticManager.tap(layoutCashDrawer)
            updateChangeTrayUI()
        }
    }

    private fun updateChangeTrayUI() {
        if (customerIndex >= customers.size) return
        val customer = customers[customerIndex]
        val changeDue = maxOf(0, customer.payment - basketTotal)
        val diff = changeGiven - changeDue

        tvChangeDue.text = "Kailangan: ₱$changeDue"
        tvChangeGiven.text = "Ibinigay: ₱$changeGiven"

        when {
            changeDue == 0 -> {
                // Exact payment scenario
                tvChangeStatus.text = "✓ Walang sukli!"
                tvChangeStatus.setTextColor(Color.parseColor("#4CAF50"))
                btnServe.isEnabled = currentBasket.isNotEmpty()
                btnServe.alpha = if (currentBasket.isNotEmpty()) 1.0f else 0.5f
            }
            diff == 0 && changeGiven > 0 -> {
                tvChangeStatus.text = "✓ Tama ang sukli!"
                tvChangeStatus.setTextColor(Color.parseColor("#4CAF50"))
                btnServe.isEnabled = true
                btnServe.alpha = 1.0f
            }
            diff > 0 -> {
                tvChangeStatus.text = "Sobra ₱$diff! Pwede pa."
                tvChangeStatus.setTextColor(Color.parseColor("#FF9800"))
                btnServe.isEnabled = true
                btnServe.alpha = 0.90f
            }
            changeGiven == 0 -> {
                tvChangeStatus.text = "Ibigay ang sukli..."
                tvChangeStatus.setTextColor(Color.parseColor("#F44336"))
                btnServe.isEnabled = false
                btnServe.alpha = 0.5f
            }
            else -> {
                // diff < 0: kulang pa
                tvChangeStatus.text = "Kulang ₱${-diff}"
                tvChangeStatus.setTextColor(Color.parseColor("#F44336"))
                btnServe.isEnabled = false
                btnServe.alpha = 0.5f
            }
        }

        // Build change tray summary text
        if (changeTray.isEmpty()) {
            tvChangeTrayContents.text = "I-tap ang bills/coins para ibigay ang sukli. Long-press para bawiin."
        } else {
            val parts = mutableListOf<String>()
            changeTray["bill20"]?.let { if (it > 0) parts.add("₱20x$it") }
            changeTray["coin10"]?.let { if (it > 0) parts.add("₱10x$it") }
            changeTray["coin5"]?.let { if (it > 0) parts.add("₱5x$it") }
            changeTray["coin1"]?.let { if (it > 0) parts.add("₱1x$it") }
            changeTray["candy"]?.let { if (it > 0) parts.add("🍬x$it") }
            tvChangeTrayContents.text = "Sukli tray: " + parts.joinToString(" + ")
        }
    }

    // ══════════════════════════════════════════════════════════════
    // RENDER COUNTER TRAY (physical items on table)
    // ══════════════════════════════════════════════════════════════

    private fun renderCounterTray() {
        layoutCounterItems.removeAllViews()

        if (currentBasket.isEmpty()) {
            val tvPlaceholder = TextView(this).apply {
                text = "I-tap o i-drag ang mga item dito"
                setTextColor(Color.parseColor("#D7CCC8"))
                textSize = 10f
                setTypeface(null, android.graphics.Typeface.ITALIC)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                ).apply { gravity = Gravity.CENTER_VERTICAL }
            }
            layoutCounterItems.addView(tvPlaceholder)
            return
        }

        currentBasket.forEach { (itemId, qty) ->
            val dp = resources.displayMetrics.density
            val container = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    (46 * dp).toInt(), LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { marginEnd = (8 * dp).toInt() }
            }

            // Top icon container with quantity badge
            val iconFrame = FrameLayout(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    (40 * dp).toInt(), (40 * dp).toInt()
                )
            }

            val icon = ImageView(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.FIT_CENTER
                setImageResource(
                    when (itemId) {
                        "coke" -> R.drawable.icon_coke
                        "chippy" -> R.drawable.icon_chippy
                        "kopiko" -> R.drawable.icon_kopiko
                        "noodles" -> R.drawable.icon_noodles
                        else -> R.drawable.circle_background
                    }
                )
            }
            iconFrame.addView(icon)

            if (qty > 1) {
                val badge = TextView(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply { gravity = Gravity.TOP or Gravity.END }
                    text = "×$qty"
                    setTextColor(Color.WHITE)
                    textSize = 8.5f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    setBackgroundResource(R.drawable.ui_btn_orange)
                    setPadding((5 * dp).toInt(), 0, (5 * dp).toInt(), 0)
                }
                iconFrame.addView(badge)
            }
            container.addView(iconFrame)

            // Price label cleanly placed BELOW the icon (no overlapping)
            val priceLabel = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = (2 * dp).toInt() }
                val price = (itemPrices[itemId] ?: 0) * qty
                text = "₱$price"
                setTextColor(Color.parseColor("#FFD54F"))
                textSize = 9.5f
                setTypeface(null, android.graphics.Typeface.BOLD)
            }
            container.addView(priceLabel)

            // Pop-in bounce animation for satisfying feel
            container.scaleX = 0.6f
            container.scaleY = 0.6f
            container.alpha = 0f
            container.animate()
                .scaleX(1f).scaleY(1f).alpha(1f)
                .setDuration(180)
                .setInterpolator(OvershootInterpolator(2.2f))
                .start()

            // Tap to remove from tray
            container.setOnClickListener { it.bounce(); removeItem(itemId) }

            // Long-press to drag back to shelf
            container.setOnLongClickListener { v ->
                val clip = ClipData.newPlainText("item_id", itemId)
                v.startDragAndDrop(clip, View.DragShadowBuilder(v), itemId, 0)
                HapticManager.tap(v)
                true
            }

            layoutCounterItems.addView(container)
        }
    }

    // ══════════════════════════════════════════════════════════════
    // CUSTOMER FLOW
    // ══════════════════════════════════════════════════════════════

    private fun loadCustomer(index: Int) {
        if (index >= customers.size) {
            finishDay()
            return
        }

        customerIndex = index
        currentBasket.clear()
        tapToAskCount = 0
        comprehensionScore = 100
        hasCustomerPaid = false
        clearChangeTray()

        val customer = customers[index]

        // Update pay button
        btnPay500.text = "Bayad ₱${customer.payment}"
        btnPay500.isEnabled = true
        btnPay500.alpha = 1.0f

        // Reset serve button
        btnServe.isEnabled = false
        btnServe.alpha = 0.5f

        // Reset drawer and show counter tray
        layoutCashDrawer.visibility = View.GONE
        layoutCashDrawer.alpha = 1f
        layoutCounterTrayArea.visibility = View.VISIBLE
        layoutCounterTrayArea.alpha = 1f

        // Ambient cue
        if (customer.ambientStoryNotice.isNotEmpty()) {
            showGameToast(customer.ambientStoryNotice)
        }
        if (customer.ambientSoundCue.isNotEmpty()) {
            SoundManager.playAmbienceCue(this, customer.ambientSoundCue)
        }

        // Character slide-in
        ivCharacter.setImageResource(customer.drawableRes)
        ivCharacter.translationX = -80f
        ivCharacter.alpha = 0f
        ivCharacter.animate()
            .translationX(0f).alpha(1f)
            .setDuration(380)
            .setInterpolator(OvershootInterpolator(1.0f))
            .withEndAction { startCharacterIdleAnimation() }
            .start()

        // Speech bubble pop
        speechBubble.scaleX = 0.80f
        speechBubble.scaleY = 0.80f
        speechBubble.alpha = 0f
        speechBubble.animate()
            .scaleX(1f).scaleY(1f).alpha(1f)
            .setDuration(260)
            .setInterpolator(OvershootInterpolator(1.5f))
            .start()

        // Set dialogue
        tvCustomerName.text = customer.name
        tvDialogue.text = customer.dialogues[0]
        tvComprehension.text = "📖 First Try (100%)"
        btnAsk.text = "Tanungin"
        btnAsk.isEnabled = true
        btnAsk.alpha = 1.0f

        updateBasketUI()
    }

    private fun startCharacterIdleAnimation() {
        characterIdleAnimator?.cancel()
        characterIdleAnimator = ObjectAnimator.ofFloat(ivCharacter, "translationY", 0f, -5f, 0f).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun handleAsk() {
        if (customerIndex >= customers.size) return
        val customer = customers[customerIndex]

        if (tapToAskCount < customer.dialogues.size - 1) {
            tapToAskCount++
            comprehensionScore = maxOf(60, comprehensionScore - 20)

            // Smooth dialogue swap
            tvDialogue.animate().alpha(0f).setDuration(90).withEndAction {
                tvDialogue.text = customer.dialogues[tapToAskCount]
                tvDialogue.animate().alpha(1f).setDuration(130).start()
            }.start()

            val remaining = (customer.dialogues.size - 1) - tapToAskCount
            btnAsk.text = if (remaining > 0) "Ulitin ($remaining)" else "Walang Dagdag"
            if (remaining <= 0) {
                btnAsk.isEnabled = false
                btnAsk.alpha = 0.5f
            }

            tvComprehension.text = when {
                comprehensionScore >= 100 -> "📖 First Try (100%)"
                comprehensionScore >= 80 -> "📖 Isang tanong (80%)"
                else -> "📖 Kailangan ng tulong (60%)"
            }

            SoundManager.playClick()
        }
    }

    private fun handleCustomerPayment() {
        if (customerIndex >= customers.size) return
        val customer = customers[customerIndex]

        if (currentBasket.isEmpty()) {
            showGameToast("Mag-lagay muna ng items sa counter!")
            speechBubble.animate().translationX(8f).setDuration(50).withEndAction {
                speechBubble.animate().translationX(-8f).setDuration(50).withEndAction {
                    speechBubble.animate().translationX(0f).setDuration(50).start()
                }.start()
            }.start()
            return
        }

        hasCustomerPaid = true
        clearChangeTray()

        val changeDue = maxOf(0, customer.payment - basketTotal)

        SoundManager.playCoin()
        HapticManager.tap(btnPay500)

        // Disable pay button (already paid)
        btnPay500.isEnabled = false
        btnPay500.alpha = 0.5f

        // Show cash drawer panel cleanly on counter (hiding tray, zero overlap with character/dialogue)
        if (changeDue > 0) {
            layoutCounterTrayArea.visibility = View.GONE
            layoutCashDrawer.visibility = View.VISIBLE
            layoutCashDrawer.alpha = 0f
            layoutCashDrawer.animate()
                .alpha(1f)
                .setDuration(220)
                .start()
            btnServe.isEnabled = false
            btnServe.alpha = 0.5f
            showGameToast("${customer.name} nagbayad ng ₱${customer.payment}! Ibigay ang ₱$changeDue sukli.")
        } else {
            // Exact payment - no change needed
            btnServe.isEnabled = true
            btnServe.alpha = 1.0f
            showGameToast("${customer.name} nagbayad nang eksakto! ₱${customer.payment}")
        }

        updateChangeTrayUI()
    }

    private fun handleServe() {
        if (customerIndex >= customers.size) return
        val customer = customers[customerIndex]

        // Check basket matches order
        var matches = (customer.expected.size == currentBasket.size)
        if (matches) {
            for ((item, qty) in customer.expected) {
                if ((currentBasket[item] ?: 0) != qty) { matches = false; break }
            }
        }

        if (!matches) {
            HapticManager.error(this)
            SoundManager.playError()
            ivCharacter.animate().translationXBy(16f).setDuration(55).withEndAction {
                ivCharacter.animate().translationXBy(-32f).setDuration(55).withEndAction {
                    ivCharacter.animate().translationX(0f).setDuration(55).start()
                }.start()
            }.start()
            tvDialogue.text = "\"Kuya, teka po! Hindi ito ang order ko! Pakitingnan po ulit.\""
            return
        }

        // Warn if no payment for orders that need it
        if (!hasCustomerPaid) {
            // Allow serving without payment only if basket is correct
            // (treat as cash-on-hand scenario)
        }

        // Calculate profit
        var wholesaleCost = 0
        currentBasket.forEach { (id, qty) -> wholesaleCost += (itemCosts[id] ?: 0) * qty }
        val orderProfit = basketTotal - wholesaleCost
        dailyProfit += orderProfit
        dailyGrossSales += basketTotal
        dailyWholesaleCost += wholesaleCost

        if (comprehensionScore >= 100) {
            firstTryComprehensionCount++
            GameStateManager.recordComprehension(this, true)
        } else {
            GameStateManager.recordComprehension(this, false)
        }

        tvTotalProfit.text = "₱$dailyProfit"
        HapticManager.success(this)
        SoundManager.playSuccess()

        // Character celebrate jump
        characterIdleAnimator?.cancel()
        ivCharacter.animate()
            .translationY(-28f).setDuration(150)
            .withEndAction {
                ivCharacter.animate().translationY(0f).setDuration(200)
                    .setInterpolator(AccelerateDecelerateInterpolator()).start()
            }.start()

        showGameToast("Serbisyo kay ${customer.name}! +₱$orderProfit kita 🎉")

        // Hide cash drawer and restore counter tray
        if (layoutCashDrawer.visibility == View.VISIBLE) {
            layoutCashDrawer.animate().alpha(0f).setDuration(180).withEndAction {
                layoutCashDrawer.visibility = View.GONE
                layoutCashDrawer.alpha = 1f
                layoutCounterTrayArea.visibility = View.VISIBLE
                layoutCounterTrayArea.alpha = 1f
            }.start()
        }

        // Slide out counter tray → next customer
        layoutCounterItems.animate().translationY(-40f).alpha(0f).setDuration(200).withEndAction {
            layoutCounterItems.translationY = 0f
            layoutCounterItems.alpha = 1f
            loadCustomer(customerIndex + 1)
        }.start()
    }

    private fun finishDay() {
        characterIdleAnimator?.cancel()
        GameStateManager.clearMidDayProgress(this)

        val completedDay = gameState.dayNumber
        val nextDay = completedDay + 1
        val netProfit = maxOf(0, dailyProfit - 15) // ₱15 electric bill
        
        GameStateManager.addProfit(this, netProfit)
        // Advance to next day in persistent storage so returning to title shows CONTINUE (DAY X+1)
        GameStateManager.setCurrentDay(this, nextDay)

        startActivity(Intent(this, EndOfDayActivity::class.java).apply {
            putExtra("DAY_NUMBER", completedDay)
            putExtra("NEXT_DAY_NUMBER", nextDay)
            putExtra("CHAPTER", gameState.chapter)
            putExtra("GROSS_SALES", dailyGrossSales)
            putExtra("WHOLESALE_COST", dailyWholesaleCost)
            putExtra("ELECTRIC_BILL", 15)
            putExtra("UTANG_GIVEN", 0)
            putExtra("NET_PROFIT", netProfit)
            putExtra("CUSTOMERS_SERVED", customers.size)
            putExtra("FIRST_TRY_COUNT", firstTryComprehensionCount)
        })
        finish()
    }

    // ══════════════════════════════════════════════════════════════
    // SIDE CALCULATOR
    // ══════════════════════════════════════════════════════════════

    private fun toggleSideCalculator() {
        if (layoutSideCalculator.visibility == View.VISIBLE) {
            layoutSideCalculator.animate()
                .translationX(-50f).alpha(0f)
                .setDuration(180)
                .withEndAction { layoutSideCalculator.visibility = View.GONE }
                .start()
        } else {
            layoutSideCalculator.visibility = View.VISIBLE
            layoutSideCalculator.translationX = -50f
            layoutSideCalculator.alpha = 0f
            layoutSideCalculator.animate()
                .translationX(0f).alpha(1f)
                .setDuration(230)
                .setInterpolator(OvershootInterpolator(1.4f))
                .start()
        }
    }

    private fun setupSideCalculatorKeys() {
        fun updateUI() {
            tvSideCalcDisplay.text = calcDisplayStr
            tvSideCalcHistory.text = calcHistoryStr
        }

        fun onDigit(d: String) {
            SoundManager.playClick()
            if (calcIsNewInput || calcDisplayStr == "0") {
                calcDisplayStr = d; calcIsNewInput = false
            } else {
                if (calcDisplayStr.length < 10) calcDisplayStr += d
            }
            updateUI()
        }

        fun onOperator(op: String) {
            SoundManager.playClick()
            calcStoredVal = calcDisplayStr.toDoubleOrNull() ?: 0.0
            calcPendingOp = op
            calcHistoryStr = "$calcDisplayStr $op"
            calcIsNewInput = true
            updateUI()
        }

        fun onEquals() {
            SoundManager.playClick()
            if (calcPendingOp.isNotEmpty()) {
                val cur = calcDisplayStr.toDoubleOrNull() ?: 0.0
                val result = when (calcPendingOp) {
                    "+" -> calcStoredVal + cur
                    "-" -> calcStoredVal - cur
                    "×" -> calcStoredVal * cur
                    "÷" -> if (cur != 0.0) calcStoredVal / cur else 0.0
                    else -> cur
                }
                calcHistoryStr = "$calcStoredVal $calcPendingOp $cur ="
                calcDisplayStr = if (result % 1.0 == 0.0) result.toLong().toString()
                    else String.format("%.2f", result)
                calcPendingOp = ""; calcIsNewInput = true
                updateUI()
            }
        }

        val digits = mapOf(
            R.id.btnSideCalc0 to "0", R.id.btnSideCalc00 to "00",
            R.id.btnSideCalc1 to "1", R.id.btnSideCalc2 to "2",
            R.id.btnSideCalc3 to "3", R.id.btnSideCalc4 to "4",
            R.id.btnSideCalc5 to "5", R.id.btnSideCalc6 to "6",
            R.id.btnSideCalc7 to "7", R.id.btnSideCalc8 to "8",
            R.id.btnSideCalc9 to "9"
        )
        digits.forEach { (id, d) -> findViewById<Button>(id)?.setOnClickListener { onDigit(d) } }

        findViewById<Button>(R.id.btnSideCalcDot)?.setOnClickListener {
            if (!calcDisplayStr.contains(".")) {
                calcDisplayStr += "."; calcIsNewInput = false; updateUI()
            }
        }
        findViewById<Button>(R.id.btnSideCalcAdd)?.setOnClickListener { onOperator("+") }
        findViewById<Button>(R.id.btnSideCalcSub)?.setOnClickListener { onOperator("-") }
        findViewById<Button>(R.id.btnSideCalcMul)?.setOnClickListener { onOperator("×") }
        findViewById<Button>(R.id.btnSideCalcDiv)?.setOnClickListener { onOperator("÷") }
        findViewById<Button>(R.id.btnSideCalcEq)?.setOnClickListener { onEquals() }
        findViewById<Button>(R.id.btnSideCalcC)?.setOnClickListener {
            SoundManager.playClick()
            calcDisplayStr = "0"; calcHistoryStr = ""
            calcStoredVal = 0.0; calcPendingOp = ""; calcIsNewInput = true; updateUI()
        }
        findViewById<Button>(R.id.btnSideCalcDel)?.setOnClickListener {
            SoundManager.playClick()
            calcDisplayStr = if (calcDisplayStr.length > 1) calcDisplayStr.dropLast(1)
                else { calcIsNewInput = true; "0" }
            updateUI()
        }

        // Quick helper buttons
        findViewById<View>(R.id.btnSideCalcHelperTotal)?.setOnClickListener {
            SoundManager.playClick()
            calcDisplayStr = basketTotal.toString(); calcIsNewInput = true; updateUI()
        }
        findViewById<View>(R.id.btnSideCalcHelperPaid)?.setOnClickListener {
            SoundManager.playClick()
            if (customerIndex < customers.size) {
                calcDisplayStr = customers[customerIndex].payment.toString()
                calcIsNewInput = true; updateUI()
            }
        }
        findViewById<View>(R.id.btnSideCalcHelperSukli)?.setOnClickListener {
            SoundManager.playCoin()
            if (customerIndex < customers.size) {
                val bill = customers[customerIndex].payment
                val change = maxOf(0, bill - basketTotal)
                calcHistoryStr = "₱$bill - ₱$basketTotal ="
                calcDisplayStr = change.toString(); calcIsNewInput = true; updateUI()
            }
        }

        updateUI()
    }

    // ══════════════════════════════════════════════════════════════
    // PAUSE DIALOG
    // ══════════════════════════════════════════════════════════════

    private fun showPauseDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_pause, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        view.findViewById<TextView>(R.id.tvPauseStatus)?.text =
            "Day ${gameState.dayNumber} • Customer ${customerIndex + 1}/${customers.size} • Kita: ₱$dailyProfit"

        view.findViewById<View>(R.id.btnPauseResume)?.setOnClickListener {
            SoundManager.playClick(); dialog.dismiss()
        }
        view.findViewById<View>(R.id.btnPauseChapters)?.setOnClickListener {
            SoundManager.playClick(); dialog.dismiss(); showChaptersDialog()
        }
        view.findViewById<View>(R.id.btnPauseAudio)?.setOnClickListener {
            SoundManager.playClick()
            SoundManager.showAudioSettingsDialog(this)
        }
        view.findViewById<View>(R.id.btnPauseExit)?.setOnClickListener {
            SoundManager.playClick()
            dialog.dismiss()
            GameStateManager.saveMidDayProgress(this, customerIndex, dailyProfit, dailyGrossSales, dailyWholesaleCost)
            showGameToast("Nai-save! Day ${gameState.dayNumber}, Customer ${customerIndex + 1}")
            startActivity(Intent(this, TitleActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            })
            finish()
        }

        dialog.show()
        ImmersiveMode.setFullScreen(this)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun showChaptersDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_chapters, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        view.findViewById<View>(R.id.btnChaptersClose)?.setOnClickListener {
            SoundManager.playClick(); dialog.dismiss()
        }
        dialog.show()
        ImmersiveMode.setFullScreen(this)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }
}
