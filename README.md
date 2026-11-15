# 🏪 Sukli: Sari-Sari Stories
### *Functional Literacy & Financial Skills Simulator for Filipino Learners*
**Version:** `v0.9.5 DEMO BUILD` • **Target OS:** Android 8.0+ (API 26+) • **Orientation:** Landscape Locked

---

## 📖 About the Game

**Sukli: Sari-Sari Stories** is an educational story-driven simulation game built to address the **Philippine functional literacy crisis**. According to the World Bank, DepEd, and PISA (2022) findings, **9 out of 10 Filipino children struggle to read and understand simple texts**, with the Philippines ranking 77th out of 81 countries in reading comprehension.

Rather than using repetitive multiple-choice quizzes, *Sukli* embeds reading comprehension and mental arithmetic into an authentic cultural context: running a family neighborhood **sari-sari store** in a vibrant Philippine barangay.

The player steps into the shoes of **Kiko**, a young boy minding his grandmother Lola Carmen's store to raise ₱10,000 for her medical treatments while navigating the daily lives, slang, and financial realities of his community.

---

## 🎮 Core Gameplay Mechanics (Inspired by *I Have No Change* & *Good Pizza, Great Pizza*)

### 1. 📖 Contextual Reading Comprehension
- Customers speak in everyday colloquial Filipino and Taglish (e.g., *"Kuya Kiko, pasuyo po! Isang Chippy tsaka Coke Swakto pambaon sa recess"*).
- Players must decipher context clues, slang, and quantities without automated hints.
- Players can tap **"Tanungin Ulit" (Ask for Hint)** if needed, which slightly lowers the comprehension score to encourage independent reading.

### 2. 🛒 Physical Counter & Item Placement
- Products (**Coke, Chippy, Kopiko, Lucky Me! Pancit Canton**) sit on the right-side store shelf.
- Tap or drag items to place them onto the physical wooden counter with bouncy overshoot animations and real-time total calculation.

### 3. 💵 Physical Barya Box (Cash Drawer) & Sukli Counting
- Customers pay with realistic Philippine peso bills (**₱50, ₱100, ₱500**).
- When payment is received, the physical **Barya Box (Cash Drawer)** opens on the counter:
  - 💵 **₱20 Bill**
  - 🪙 **₱10 Coin**
  - 🪙 **₱5 Coin**
  - 🪙 **₱1 Coin**
  - 🍬 **Sukling Kendi (Candy ₱1)** — traditional Philippine substitute for short change!
- Real-time accounting: Displays `Kailangan: ₱X` vs `Ibinigay: ₱Y`.
- **Child-Friendly Undo:** Features a prominent **`↺ Bawiin (Reset)`** button so children can quickly reset their change tray if they miscount.
- The **"IBIGAY" (SERVE)** button unlocks when exact change is provided.

### 4. 🧮 Built-In Side Calculator
- Built with a portrait-safe, responsive `TableLayout` grid that guarantees proper button proportions on landscape mobile screens.
- Includes quick sari-sari helper shortcuts (`Due`, `Paid`, `=Sukli`).
- Collapsible on the left side via the register icon without obstructing the counter.

### 5. 🌙 End of Day Accounting & Progression
- Complete daily financial breakdown: Gross Sales, Cost of Goods Sold (COGS), Electric Bill, and Net Store Profit.
- 40% of daily profits automatically transfer to **Lola Carmen's Medical Fund**.
- Evaluates **Reading Comprehension Fluency (First-Try %)** with tips from Teacher Fe.
- Automatic day progression (Day 1 → Day 2 → Day 3) with persistent mid-day save & resume support.

---

## 📋 Planned Roadmap (For Team Presentation)

1. **30-Day Narrative Arc:** 5 full story chapters covering floods, school openings, and Lola Carmen's recovery.
2. **Barangay Environmental Audio:** Field recordings of 2-stroke tricycles, taho & ice cream bells, and chapel novenas.
3. **Interactive Transistor Radio:** AM/FM stations (DZBB News, Love Radio dramas, retro OPM hits).
4. **Utang Ledger Mini-Game:** Spiral notebook interface for managing neighborhood store credit.
5. **Supplier Restock Logistics:** Restock van delivery mini-game with Kuya Joms.
6. **16+ Authentic Philippine Products:** Adding Silver Swan Toyo, Datu Puti, Sardinas 555, Palmolive, Bear Brand, and Milo.

---

## 🛠️ Tech Stack & Architecture

- **Language:** Kotlin 1.9+
- **Platform:** Android SDK (API 26 to API 34)
- **UI Architecture:** AndroidX ConstraintLayout, Custom Canvas Drawables, ValueAnimator / ObjectAnimator
- **State Persistence:** `GameStateManager` with SharedPreferences & Gson JSON serialization
- **Audio & Haptics:** Custom `SoundManager` (MediaPlayer sound pool) & `HapticManager`
- **Unit Testing:** JUnit 4 (`SukliGameUnitTest`)

---

## 🚀 How to Build & Run

### Prerequisites:
- Android Studio Iguana / Jellyfish / Koala (or newer)
- JDK 17+ (e.g., Android Studio Embedded JBR)
- Android Device or Emulator running Android 8.0+

### In Android Studio:
1. Open the project directory in Android Studio.
2. Allow Gradle sync to complete.
3. Select an emulator or connected device and click **Run 'app'** (`Shift + F10`).

### Via Command Line (Gradle):
```powershell
# Run unit tests
.\gradlew testDebugUnitTest

# Build debug APK
.\gradlew assembleDebug
```
Output APK location: `app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
Created with ❤️ for Filipino learners and DepEd literacy development initiatives.
