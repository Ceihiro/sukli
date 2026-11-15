package com.c0d3rraze.sukli.data.model

data class GameState(
    val dayNumber: Int = 1,
    val chapter: Int = 1,
    val cash: Int = 750,
    val hospitalFund: Int = 1500,
    val hospitalTarget: Int = 10000,
    val fanSpeed: Int = 1, // 0 = Off, 1 = Low, 2 = High
    val radioStationIndex: Int = 1, // Love Radio default
    val isBrownout: Boolean = false,
    val isCandleLit: Boolean = false,
    val storeTier: Int = 1,
    val purchasedUpgradeIds: List<String> = emptyList(),
    val utangRecords: List<UtangRecord> = emptyList(),
    val inventory: Map<String, Int> = mapOf(
        "coke" to 12,
        "chippy" to 15,
        "kopiko" to 20,
        "noodles" to 15,
        "rice" to 8,
        "shampoo" to 14,
        "energy_drink" to 10,
        "chocnut" to 25,
        "pandesal" to 16,
        "candle" to 10
    ),
    val cashDrawer: CashDrawer = CashDrawer(),
    val customerRelationships: Map<String, Int> = emptyMap(),
    val totalCustomersServed: Int = 0,
    val firstTryComprehensions: Int = 0,
    val totalComprehensionChecks: Int = 0,
    val savedCustomerIndex: Int = 0,
    val savedDailyProfit: Int = 0,
    val savedDailyGrossSales: Int = 0,
    val savedDailyWholesaleCost: Int = 0,
    val hasActiveSave: Boolean = false
) {
    fun getChapterTitle(): String {
        return when (chapter) {
            1 -> "Chapter 1: 'I Have No Change' (Wala Akong Barya)"
            2 -> "Chapter 2: 'Debt of Gratitude' (Utang ng Loob) [Locked in Demo]"
            3 -> "Chapter 3: 'Christmas in the Barangay' [Locked in Demo]"
            4 -> "Chapter 4: 'Incoming Typhoon' [Locked in Demo]"
            else -> "Chapter 5: 'The Expansion' [Locked in Demo]"
        }
    }
}
