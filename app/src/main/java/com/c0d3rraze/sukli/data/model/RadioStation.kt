package com.c0d3rraze.sukli.data.model

data class RadioStation(
    val id: String,
    val name: String,
    val frequency: String,
    val tagline: String,
    val newsTicker: String,
    val gameplayPerk: String,
    val isEmergencyAlert: Boolean = false
)

object RadioStationsData {
    val STATIONS = listOf(
        RadioStation(
            id = "dzmm",
            name = "DZMM Radyo Patrol 630",
            frequency = "AM 630 kHz",
            tagline = "First in News, First in Public Service!",
            newsTicker = "WEATHER ADVISORY: PAGASA tracking tropical depression approaching PAR. Heavy rain expected. Stock up on candles and canned goods!",
            gameplayPerk = "Actionable Weather & Market Price Intelligence",
            isEmergencyAlert = true
        ),
        RadioStation(
            id = "love_radio",
            name = "Love Radio 90.7",
            frequency = "FM 90.7 MHz",
            tagline = "Feel-good hits and easy listening all day!",
            newsTicker = "Now Playing: 'Tadhana' - Up Dharma Down. Keep smiling, stay chill at Kiko's sari-sari store!",
            gameplayPerk = "Good Vibes: Customer patience depletes 25% slower",
            isEmergencyAlert = false
        ),
        RadioStation(
            id = "barangay_ls",
            name = "Barangay LS 97.1",
            frequency = "FM 97.1 MHz",
            tagline = "Neighborhood beats and community updates!",
            newsTicker = "TRAFFIC REPORT: Heavy commuter backlog on the main highway due to broken jeepney. Evening rush delayed!",
            gameplayPerk = "Traffic Alerts: Evening rush delayed, extra time to stock shelves",
            isEmergencyAlert = false
        ),
        RadioStation(
            id = "veritas",
            name = "Radio Veritas 846",
            frequency = "AM 846 kHz",
            tagline = "Community Reflections & Gospel Hope",
            newsTicker = "Reflection: 'Blessed are the generous.' Lola Remedios and neighborhood elders smile warmly upon hearing this station.",
            gameplayPerk = "Community Grace: Elder customer satisfaction +15%",
            isEmergencyAlert = false
        )
    )
}
