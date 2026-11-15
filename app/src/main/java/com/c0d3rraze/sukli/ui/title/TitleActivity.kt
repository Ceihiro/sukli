package com.c0d3rraze.sukli.ui.title

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.c0d3rraze.sukli.R
import com.c0d3rraze.sukli.ui.settings.AboutActivity
import com.c0d3rraze.sukli.ui.store.StoreActivity
import com.c0d3rraze.sukli.utils.GameStateManager
import com.c0d3rraze.sukli.utils.HapticManager
import com.c0d3rraze.sukli.utils.ImmersiveMode
import com.c0d3rraze.sukli.utils.SoundManager

class TitleActivity : AppCompatActivity() {

    private lateinit var btnPlay: View
    private lateinit var tvPlayLabel: TextView
    private lateinit var btnNewGame: View
    private lateinit var btnChapters: View
    private lateinit var btnSettings: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(this)
        setContentView(R.layout.activity_title)
        ImmersiveMode.setFullScreen(this)

        btnPlay = findViewById(R.id.btnPlay)
        tvPlayLabel = findViewById(R.id.tvPlayLabel)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnChapters = findViewById(R.id.btnChapters)
        btnSettings = findViewById(R.id.btnSettings)

        refreshMenuOptions()

        btnPlay.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            it.animate()
                .scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .withEndAction {
                            startActivity(Intent(this, StoreActivity::class.java))
                        }.start()
                }.start()
        }

        btnNewGame.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            AlertDialog.Builder(this)
                .setTitle("Start New Game?")
                .setMessage("Are you sure you want to start fresh from Day 1? Your current earnings, day progress, and fund progress will be cleared.")
                .setPositiveButton("Reset & Start") { _, _ ->
                    GameStateManager.resetNewGame(this)
                    refreshMenuOptions()
                    Toast.makeText(this, "Started fresh save! Starting Day 1...", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, StoreActivity::class.java))
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnChapters.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            it.animate()
                .scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .withEndAction {
                            showChaptersDialog()
                        }.start()
                }.start()
        }

        btnSettings.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            it.animate()
                .scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .withEndAction {
                            startActivity(Intent(this, AboutActivity::class.java))
                        }.start()
                }.start()
        }

        findViewById<View>(R.id.btnAudioSettings)?.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            it.animate()
                .scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .withEndAction {
                            SoundManager.showAudioSettingsDialog(this)
                        }.start()
                }.start()
        }

        findViewById<View>(R.id.btnDevRoadmap)?.setOnClickListener {
            HapticManager.tap(it)
            SoundManager.playClick()
            it.animate()
                .scaleX(0.92f).scaleY(0.92f).setDuration(80)
                .withEndAction {
                    it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120)
                        .setInterpolator(OvershootInterpolator(2.0f))
                        .withEndAction {
                            showDevRoadmapDialog()
                        }.start()
                }.start()
        }
    }

    private fun refreshMenuOptions() {
        val state = GameStateManager.getGameState(this)
        val hasSave = GameStateManager.hasSavedGame(this)

        if (hasSave) {
            tvPlayLabel.text = "CONTINUE (DAY ${state.dayNumber})"
            btnNewGame.visibility = View.VISIBLE
        } else {
            tvPlayLabel.text = "PLAY (DAY 1)"
            btnNewGame.visibility = View.GONE
        }
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
            SoundManager.playClick()
            dialog.dismiss()
        }

        dialog.show()
        ImmersiveMode.setFullScreen(this)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    private fun showDevRoadmapDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = layoutInflater.inflate(R.layout.dialog_dev_roadmap, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        view.findViewById<View>(R.id.btnDevRoadmapClose)?.setOnClickListener {
            SoundManager.playClick()
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnDevRoadmapOk)?.setOnClickListener {
            SoundManager.playClick()
            dialog.dismiss()
        }

        dialog.show()
        ImmersiveMode.setFullScreen(this)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    override fun onResume() {
        super.onResume()
        ImmersiveMode.setFullScreen(this)
        refreshMenuOptions()
    }
}
