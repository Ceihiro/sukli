package com.c0d3rraze.sukli.data.model

data class GameState(
    val dayNumber: Int = 1,
    val cash: Int = 500,
    val inventory: Map<String, Int> = emptyMap(),
    val customerRelationships: Map<String, Int> = emptyMap(),
    val currentStoreTier: Int = 1
)