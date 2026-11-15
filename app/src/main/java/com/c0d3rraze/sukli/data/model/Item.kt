package com.c0d3rraze.sukli.data.model

data class Item(
    val id: String,
    val name: String,
    val price: Int,
    val supplierCost: Int,
    val category: String, // drinks, snacks, breakfast, food, tingi, emergency
    val iconEmoji: String = "📦",
    val description: String = "",
    val iconDrawable: String = "",
    var stock: Int = 10
) {
    fun getDrawableResName(): String {
        return when (id) {
            "coke" -> "ic_prod_coke"
            "chippy" -> "ic_prod_chippy"
            "kopiko" -> "ic_prod_kopiko"
            "noodles" -> "ic_prod_noodles"
            "pandesal" -> "ic_prod_pandesal"
            "chocnut" -> "ic_prod_chocnut"
            "shampoo" -> "ic_prod_shampoo"
            "soap" -> "ic_prod_soap"
            "energy_drink" -> "ic_prod_energy"
            "candle" -> "ic_prod_candle"
            "skyflakes" -> "ic_prod_crackers"
            "rice" -> "ic_prod_rice"
            "milo" -> "ic_prod_milo"
            "sardines" -> "ic_prod_sardines"
            "toyo" -> "ic_prod_toyo"
            else -> "ic_prod_chippy"
        }
    }
}
