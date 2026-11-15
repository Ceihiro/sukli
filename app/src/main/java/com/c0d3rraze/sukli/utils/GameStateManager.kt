package com.c0d3rraze.sukli.utils

import android.content.Context
import android.content.SharedPreferences
import com.c0d3rraze.sukli.data.model.GameState
import com.c0d3rraze.sukli.data.model.UtangRecord
import com.google.gson.Gson

object GameStateManager {
    private const val PREFS_NAME = "sukli_prefs"
    private const val KEY_GAME_STATE = "full_game_state_v2"
    private val gson = Gson()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    @Synchronized
    fun getGameState(context: Context): GameState {
        val json = getPrefs(context).getString(KEY_GAME_STATE, null)
        return if (json != null) {
            try {
                gson.fromJson(json, GameState::class.java) ?: GameState()
            } catch (e: Exception) {
                GameState()
            }
        } else {
            GameState()
        }
    }

    @Synchronized
    fun saveGameState(context: Context, state: GameState) {
        val json = gson.toJson(state)
        getPrefs(context).edit().putString(KEY_GAME_STATE, json).apply()
    }

    fun hasSavedGame(context: Context): Boolean {
        val state = getGameState(context)
        return state.hasActiveSave || state.dayNumber > 1 || state.totalCustomersServed > 0 || state.hospitalFund > 1500
    }

    fun saveMidDayProgress(context: Context, customerIndex: Int, profit: Int, gross: Int, wholesale: Int) {
        val state = getGameState(context)
        saveGameState(
            context,
            state.copy(
                savedCustomerIndex = customerIndex,
                savedDailyProfit = profit,
                savedDailyGrossSales = gross,
                savedDailyWholesaleCost = wholesale,
                hasActiveSave = true
            )
        )
    }

    fun clearMidDayProgress(context: Context) {
        val state = getGameState(context)
        saveGameState(
            context,
            state.copy(
                savedCustomerIndex = 0,
                savedDailyProfit = 0,
                savedDailyGrossSales = 0,
                savedDailyWholesaleCost = 0,
                hasActiveSave = false
            )
        )
    }

    fun getCurrentDay(context: Context): Int {
        return getGameState(context).dayNumber
    }

    fun setCurrentDay(context: Context, day: Int) {
        val state = getGameState(context)
        val chapter = when {
            day <= 3 -> 1
            day <= 6 -> 2
            day <= 9 -> 3
            day <= 12 -> 4
            else -> 5
        }
        saveGameState(context, state.copy(dayNumber = day, chapter = chapter, savedCustomerIndex = 0, hasActiveSave = false))
    }

    fun addProfit(context: Context, profit: Int) {
        val state = getGameState(context)
        val hospitalSavings = (profit * 0.4).toInt() // 40% goes directly to Lola's medical bill
        val cashKept = profit - hospitalSavings
        saveGameState(
            context,
            state.copy(
                cash = state.cash + cashKept,
                hospitalFund = state.hospitalFund + hospitalSavings,
                savedCustomerIndex = 0,
                hasActiveSave = false
            )
        )
    }

    fun addUtang(context: Context, record: UtangRecord) {
        val state = getGameState(context)
        val updated = state.utangRecords.toMutableList()
        updated.add(record)
        saveGameState(context, state.copy(utangRecords = updated))
    }

    fun settleUtang(context: Context, recordId: String): Boolean {
        val state = getGameState(context)
        var settledAmount = 0
        val updated = state.utangRecords.map {
            if (it.id == recordId && !it.isSettled) {
                settledAmount = it.amount
                it.copy(isSettled = true)
            } else {
                it
            }
        }
        if (settledAmount > 0) {
            saveGameState(context, state.copy(cash = state.cash + settledAmount, utangRecords = updated))
            return true
        }
        return false
    }

    fun purchaseUpgrade(context: Context, upgradeId: String, cost: Int): Boolean {
        val state = getGameState(context)
        if (state.cash < cost || state.purchasedUpgradeIds.contains(upgradeId)) {
            return false
        }
        val updatedIds = state.purchasedUpgradeIds + upgradeId
        val newTier = if (upgradeId.startsWith("tier")) {
            val tierDigit = upgradeId.filter { it.isDigit() }.toIntOrNull() ?: state.storeTier
            maxOf(state.storeTier, tierDigit)
        } else {
            state.storeTier
        }
        saveGameState(
            context,
            state.copy(
                cash = state.cash - cost,
                storeTier = newTier,
                purchasedUpgradeIds = updatedIds
            )
        )
        return true
    }

    fun recordComprehension(context: Context, isFirstTry: Boolean) {
        val state = getGameState(context)
        val firstTryCount = if (isFirstTry) state.firstTryComprehensions + 1 else state.firstTryComprehensions
        saveGameState(
            context,
            state.copy(
                firstTryComprehensions = firstTryCount,
                totalComprehensionChecks = state.totalComprehensionChecks + 1,
                totalCustomersServed = state.totalCustomersServed + 1
            )
        )
    }

    fun resetNewGame(context: Context) {
        getPrefs(context).edit().clear().apply()
        saveGameState(context, GameState())
    }

    fun reset(context: Context) {
        resetNewGame(context)
    }
}
