package com.c0d3rraze.sukli.ui.cinematic

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.store.StoreActivity
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class IntroCinematicActivity : AppCompatActivity() {

    private data class CinematicScene(
        val emoji: String,
        val badgeText: String,
        val subtitleText: String,
        val isFlash: Boolean = false,
        val playSuccessSound: Boolean = false
    )

    private val scenes = listOf(
        CinematicScene(
            emoji = "🏥",
            badgeText = "DISTRICT HOSPITAL • 3:00 AM",
            subtitleText = "\"Kiko, your grandmother Lola Carmen is critically ill. You need ₱10,000 for her medical bills and dialysis before the month ends...\""
        ),
        CinematicScene(
            emoji = "🎒📜",
            badgeText = "19 YEARS OLD • COLLEGE DROPOUT",
            subtitleText = "Kiko made the hard choice to drop out of college. Holding the medical prescription and Lola's rosary, he walks through the rainy barrio streets."
        ),
        CinematicScene(
            emoji = "🏚️🔒",
            badgeText = "BARANGAY SAN ROQUE • CLOSED KIOSK",
            subtitleText = "Lola Carmen's small sari-sari store. Silent, dusty, and locked tight with a heavy rusted padlock and chain."
        ),
        CinematicScene(
            emoji = "🗝️💥",
            badgeText = "TURNING THE KEY",
            subtitleText = "*CLICK! CLATTER!* Kiko turns the brass key. The heavy padlock snaps open and the chain crashes to the wooden floor!",
            playSuccessSound = false
        ),
        CinematicScene(
            emoji = "🚪🌅",
            badgeText = "LIFTING THE WOODEN SHUTTER",
            subtitleText = "Kiko heaves the heavy wooden counter shutter open to the cool morning air..."
        ),
        CinematicScene(
            emoji = "☀️🐓",
            badgeText = "DAWN BREAK • 5:30 AM",
            subtitleText = "\"COCK-A-DOODLE-DOO!\" Golden morning sunlight floods the counter! The fan starts spinning, Love Radio 90.7 clicks to life on the shelf!",
            isFlash = true,
            playSuccessSound = true
        ),
        CinematicScene(
            emoji = "🏪🚐",
            badgeText = "LOLA'S STORE IS OPEN!",
            subtitleText = "Mang Jerry: \"Hey Kiko! Glad you opened up! One hot coffee and pandesal bread before my morning passenger route!\"",
            playSuccessSound = true
        )
    )

    private var currentSceneIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private var isFinished = false

    private val advanceRunnable = Runnable {
        advanceScene()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_intro_cinematic)
        ImmersiveMode.setFullScreen(this)

        findViewById<Button>(R.id.btnSkipCinematic).setOnClickListener {
            finishCinematic()
        }

        findViewById<View>(R.id.layoutCinematicRoot).setOnClickListener {
            advanceScene()
        }

        renderScene(0)
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
    }

    private fun renderScene(index: Int) {
        if (index >= scenes.size) {
            finishCinematic()
            return
        }

        currentSceneIndex = index
        val scene = scenes[index]

        val tvEmoji = findViewById<TextView>(R.id.tvSceneEmoji)
        val tvBadge = findViewById<TextView>(R.id.tvSceneBadge)
        val tvSubtitle = findViewById<TextView>(R.id.tvCinematicSubtitle)
        val pb = findViewById<ProgressBar>(R.id.pbCinematicProgress)
        val flashView = findViewById<View>(R.id.viewSunlightFlash)

        tvEmoji.text = scene.emoji
        tvBadge.text = scene.badgeText
        tvSubtitle.text = scene.subtitleText
        pb.progress = index

        tvEmoji.scaleX = 0.7f
        tvEmoji.scaleY = 0.7f
        tvEmoji.alpha = 0f
        tvEmoji.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .alpha(1f)
            .setDuration(400)
            .withEndAction {
                tvEmoji.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }.start()

        if (scene.isFlash) {
            flashView.visibility = View.VISIBLE
            flashView.alpha = 1f
            flashView.animate().alpha(0f).setDuration(800).withEndAction {
                flashView.visibility = View.GONE
            }.start()
        }

        if (scene.playSuccessSound) {
            SoundManager.playSuccess()
        } else {
            SoundManager.playClick()
        }

        handler.removeCallbacks(advanceRunnable)
        handler.postDelayed(advanceRunnable, 3400)
    }

    private fun advanceScene() {
        if (isFinished) return
        if (currentSceneIndex < scenes.size - 1) {
            renderScene(currentSceneIndex + 1)
        } else {
            finishCinematic()
        }
    }

    private fun finishCinematic() {
        if (isFinished) return
        isFinished = true
        handler.removeCallbacks(advanceRunnable)
        val intent = Intent(this, StoreActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(advanceRunnable)
    }
}
