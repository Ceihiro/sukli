package com.c0d3rraze.sukli.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import com.c0d3rraze.sukli.R

object SoundManager {
    private const val PREFS_NAME = "sukli_audio_prefs"
    private const val KEY_MASTER = "master_volume"
    private const val KEY_SFX = "sfx_volume"
    private const val KEY_AMBIENCE = "ambience_volume"
    private const val KEY_HAPTICS = "haptics_enabled"

    var masterVolume = 0.8f
        private set
    var sfxVolume = 0.9f
        private set
    var ambienceVolume = 0.75f
        private set
    var hapticsEnabled = true
        private set

    private var clickPlayer: MediaPlayer? = null
    private var coinPlayer: MediaPlayer? = null
    private var successPlayer: MediaPlayer? = null
    private var errorPlayer: MediaPlayer? = null
    private var ambiencePlayer: MediaPlayer? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        val prefs = getPrefs(context)
        masterVolume = prefs.getFloat(KEY_MASTER, 0.8f)
        sfxVolume = prefs.getFloat(KEY_SFX, 0.9f)
        ambienceVolume = prefs.getFloat(KEY_AMBIENCE, 0.75f)
        hapticsEnabled = prefs.getBoolean(KEY_HAPTICS, true)

        try {
            clickPlayer = MediaPlayer.create(context, R.raw.click_sound)
            coinPlayer = MediaPlayer.create(context, R.raw.coin_sound)
            successPlayer = MediaPlayer.create(context, R.raw.success_sound)
            errorPlayer = MediaPlayer.create(context, R.raw.error_sound)
            applyVolumes()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveSettings(context: Context, master: Float, sfx: Float, ambience: Float, haptics: Boolean) {
        masterVolume = master
        sfxVolume = sfx
        ambienceVolume = ambience
        hapticsEnabled = haptics

        getPrefs(context).edit()
            .putFloat(KEY_MASTER, master)
            .putFloat(KEY_SFX, sfx)
            .putFloat(KEY_AMBIENCE, ambience)
            .putBoolean(KEY_HAPTICS, haptics)
            .apply()

        applyVolumes()
    }

    private fun applyVolumes() {
        val effectiveSfx = sfxVolume * masterVolume
        clickPlayer?.setVolume(effectiveSfx, effectiveSfx)
        coinPlayer?.setVolume(effectiveSfx, effectiveSfx)
        successPlayer?.setVolume(effectiveSfx, effectiveSfx)
        errorPlayer?.setVolume(effectiveSfx, effectiveSfx)

        val effectiveAmbience = ambienceVolume * masterVolume
        ambiencePlayer?.setVolume(effectiveAmbience, effectiveAmbience)
    }

    fun playClick() {
        try {
            if (masterVolume > 0f && sfxVolume > 0f) {
                clickPlayer?.start()
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playCoin() {
        try {
            if (masterVolume > 0f && sfxVolume > 0f) {
                coinPlayer?.start()
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playSuccess() {
        try {
            if (masterVolume > 0f && sfxVolume > 0f) {
                successPlayer?.start()
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    fun playError() {
        try {
            if (masterVolume > 0f && sfxVolume > 0f) {
                errorPlayer?.start()
            }
        } catch (e: Exception) {
            // Safe fallback
        }
    }

    // Dynamic Street Ambience Cue (Safely plays audio if present in res/raw, ignores if not added yet)
    fun playAmbienceCue(context: Context, cueName: String) {
        if (masterVolume <= 0f || ambienceVolume <= 0f) return
        try {
            val resId = context.resources.getIdentifier(cueName, "raw", context.packageName)
            if (resId != 0) {
                ambiencePlayer?.release()
                ambiencePlayer = MediaPlayer.create(context, resId)?.apply {
                    val vol = ambienceVolume * masterVolume
                    setVolume(vol, vol)
                    setOnCompletionListener {
                        it.release()
                        ambiencePlayer = null
                    }
                    start()
                }
            }
        } catch (e: Exception) {
            // Graceful fallback when custom sound asset is not yet imported
        }
    }

    fun showAudioSettingsDialog(activity: Activity) {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val view = activity.layoutInflater.inflate(R.layout.dialog_audio_settings, null)
        dialog.setContentView(view)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        val sbMaster = view.findViewById<SeekBar>(R.id.sbMaster)
        val tvMasterVal = view.findViewById<TextView>(R.id.tvMasterVal)
        val sbSfx = view.findViewById<SeekBar>(R.id.sbSfx)
        val tvSfxVal = view.findViewById<TextView>(R.id.tvSfxVal)
        val sbAmbience = view.findViewById<SeekBar>(R.id.sbAmbience)
        val tvAmbienceVal = view.findViewById<TextView>(R.id.tvAmbienceVal)
        val btnToggleHaptics = view.findViewById<Button>(R.id.btnToggleHaptics)

        sbMaster.progress = (masterVolume * 100).toInt()
        tvMasterVal.text = "${sbMaster.progress}%"
        sbSfx.progress = (sfxVolume * 100).toInt()
        tvSfxVal.text = "${sbSfx.progress}%"
        sbAmbience.progress = (ambienceVolume * 100).toInt()
        tvAmbienceVal.text = "${sbAmbience.progress}%"

        var currentHaptics = hapticsEnabled
        fun updateHapticBtn() {
            btnToggleHaptics.text = if (currentHaptics) "ON" else "OFF"
            btnToggleHaptics.setBackgroundResource(if (currentHaptics) R.drawable.ui_btn_orange else R.drawable.ui_btn_gray)
        }
        updateHapticBtn()

        btnToggleHaptics.setOnClickListener {
            currentHaptics = !currentHaptics
            updateHapticBtn()
            playClick()
        }

        sbMaster.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvMasterVal.text = "$p1%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) { playClick() }
        })

        sbSfx.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvSfxVal.text = "$p1%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) { playCoin() }
        })

        sbAmbience.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                tvAmbienceVal.text = "$p1%"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) { playClick() }
        })

        view.findViewById<View>(R.id.btnResetAudio)?.setOnClickListener {
            playClick()
            sbMaster.progress = 80
            sbSfx.progress = 90
            sbAmbience.progress = 75
            currentHaptics = true
            updateHapticBtn()
        }

        view.findViewById<View>(R.id.btnSaveAudio)?.setOnClickListener {
            playClick()
            saveSettings(
                activity,
                sbMaster.progress / 100f,
                sbSfx.progress / 100f,
                sbAmbience.progress / 100f,
                currentHaptics
            )
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btnAudioClose)?.setOnClickListener {
            playClick()
            dialog.dismiss()
        }

        dialog.show()
        ImmersiveMode.setFullScreen(activity)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
    }

    fun release() {
        try {
            clickPlayer?.release()
            coinPlayer?.release()
            successPlayer?.release()
            errorPlayer?.release()
            ambiencePlayer?.release()
            clickPlayer = null
            coinPlayer = null
            successPlayer = null
            errorPlayer = null
            ambiencePlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
