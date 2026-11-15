package com.c0d3rraze.sukli.data.model

data class StoreUpgrade(
    val id: String,
    val tier: Int,
    val name: String,
    val cost: Int,
    val iconEmoji: String,
    val description: String,
    val benefit: String,
    val isLocked: Boolean = false,
    val lockReason: String = "",
    var isPurchased: Boolean = false
)

object StoreUpgradesCatalog {
    fun getDefaultUpgrades(): List<StoreUpgrade> = listOf(
        StoreUpgrade(
            id = "tier2_shelves",
            tier = 2,
            name = "Repainted Metal Shelves",
            cost = 1200,
            iconEmoji = "🪜",
            description = "Replace Lola's old wooden planks with clean, rust-free metal shelves.",
            benefit = "Increases store capacity and displays 4 more item slots cleanly.",
            isPurchased = false
        ),
        StoreUpgrade(
            id = "fan_industrial",
            tier = 2,
            name = "Industrial Stand Fan",
            cost = 850,
            iconEmoji = "🌀",
            description = "Heavy duty clip fan to blow cool air right at the counter window.",
            benefit = "Customers stop complaining about afternoon heat; patience lasts longer.",
            isPurchased = false
        ),
        StoreUpgrade(
            id = "cash_organizer",
            tier = 2,
            name = "Wooden Cash Drawer Divider",
            cost = 450,
            iconEmoji = "💵",
            description = "Organizes ₱500, ₱100, ₱50, and ₱20 bills with coin slots.",
            benefit = "Reduces change counting errors and clarifies mental math.",
            isPurchased = false
        ),
        StoreUpgrade(
            id = "tier3_fridge",
            tier = 3,
            name = "Cold Beverage Cooler (Ref)",
            cost = 3500,
            iconEmoji = "🧊",
            description = "Serve ice-cold soda, bottled water, and energy drinks.",
            benefit = "Cold drink markup: earn +₱5 extra profit on cold beverages!",
            isLocked = true,
            lockReason = "Locked in Chapter 1 Demo (Unlocks in Chapter 2)",
            isPurchased = false
        ),
        StoreUpgrade(
            id = "tier4_solar_generator",
            tier = 4,
            name = "Solar Backup Generator",
            cost = 6500,
            iconEmoji = "⚡",
            description = "Emergency backup battery when neighborhood power cuts strike.",
            benefit = "Zero brownout penalty: keeps lights and cooler running during storms!",
            isLocked = true,
            lockReason = "Locked in Chapter 1 Demo (Unlocks in Chapter 4)",
            isPurchased = false
        ),
        StoreUpgrade(
            id = "tier5_delivery_bike",
            tier = 5,
            name = "Neighborhood Delivery Sidecar",
            cost = 12000,
            iconEmoji = "🚲",
            description = "Sidecar bicycle for home deliveries across the barangay.",
            benefit = "Unlocks secondary revenue stream and Chapter 5 expansion!",
            isLocked = true,
            lockReason = "Locked in Chapter 1 Demo (Unlocks in Chapter 5)",
            isPurchased = false
        )
    )
}
