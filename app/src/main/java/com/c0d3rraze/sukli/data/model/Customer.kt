package com.c0d3rraze.sukli.data.model

data class Customer(
    val id: String,
    val name: String,
    val role: String,
    val schedule: String, // Morning Rush, School Rush, Afternoon Lull, Evening Crowd, Special Visit
    val avatarEmoji: String = "👤",
    val dialogues: List<String>, // [0]: authentic colloquial, [1]: clarified Tagalog, [2]: itemized
    val expectedItems: List<OrderLine>,
    val paymentAmount: Int,
    val paysExact: Boolean = false,
    val asksForUtang: Boolean = false,
    val utangAmount: Int = 0,
    var relationshipHearts: Int = 1,
    val specialTrait: String = "",
    val storyNotes: String = "",
    val drunkSpeech: Boolean = false
)
