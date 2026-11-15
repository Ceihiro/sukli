package com.c0d3rraze.sukli

import com.c0d3rraze.sukli.data.model.*
import org.junit.Assert.*
import org.junit.Test

class SukliGameUnitTest {

    @Test
    fun testCashDrawerTotalValue() {
        val drawer = CashDrawer(
            bills500 = 1,
            bills100 = 2,
            bills50 = 3,
            bills20 = 5,
            coins10 = 8,
            coins5 = 10,
            coins1 = 15,
            candies = 20
        )
        // 500 + 200 + 150 + 100 + 80 + 50 + 15 + 20 = 1115
        assertEquals(1115, drawer.totalValue())
    }

    @Test
    fun testCanMakeExactChange() {
        val drawer = CashDrawer(
            bills500 = 1,
            bills100 = 1,
            bills50 = 1,
            bills20 = 2,
            coins10 = 2,
            coins5 = 2,
            coins1 = 5,
            candies = 0
        )

        // ₱482 change: impossible without sufficient small bills
        assertFalse(drawer.canMakeChange(482, allowCandy = false))

        // ₱70 change: 50 + 20 -> should succeed
        assertTrue(drawer.canMakeChange(70, allowCandy = false))
    }

    @Test
    fun testSuklingCandySubstitution() {
        // Drawer has no 1-peso coins, but has Candies (Choc-Nut)
        val drawer = CashDrawer(
            bills500 = 0,
            bills100 = 0,
            bills50 = 1,
            bills20 = 0,
            coins10 = 0,
            coins5 = 0,
            coins1 = 0,
            candies = 5
        )

        // ₱53 change: cannot make without candy
        assertFalse(drawer.canMakeChange(53, allowCandy = false))

        // with candy (50 bill + 3 candies): should succeed!
        assertTrue(drawer.canMakeChange(53, allowCandy = true))

        val suggested = drawer.suggestChange(53, allowCandy = true)
        assertNotNull(suggested)
        assertEquals(1, suggested!!.bills50)
        assertEquals(3, suggested.candies)
        assertEquals(53, suggested.totalValue())
    }

    @Test
    fun testDrawerDeductionAndCustomerPayment() {
        val drawer = CashDrawer(
            bills500 = 1,
            bills100 = 2,
            bills50 = 3,
            bills20 = 5,
            coins10 = 10,
            coins5 = 10,
            coins1 = 10,
            candies = 10
        )

        // Customer pays with ₱500 bill
        drawer.addCustomerPayment(500)
        assertEquals(2, drawer.bills500)

        // Give ₱120 change: 100 + 20
        val changeToGive = ChangeBreakdown(bills100 = 1, bills20 = 1)
        val applied = drawer.applyChange(changeToGive)
        assertTrue(applied)
        assertEquals(1, drawer.bills100)
        assertEquals(4, drawer.bills20)
    }

    @Test
    fun testChapterProgressionTitles() {
        val stateCh1 = GameState(dayNumber = 2, chapter = 1)
        assertEquals("Chapter 1: 'I Have No Change' (Wala Akong Barya)", stateCh1.getChapterTitle())

        val stateCh2 = GameState(dayNumber = 5, chapter = 2)
        assertEquals("Chapter 2: 'Debt of Gratitude' (Utang ng Loob) [Locked in Demo]", stateCh2.getChapterTitle())

        val stateCh3 = GameState(dayNumber = 8, chapter = 3)
        assertEquals("Chapter 3: 'Christmas in the Barangay' [Locked in Demo]", stateCh3.getChapterTitle())

        val stateCh4 = GameState(dayNumber = 11, chapter = 4)
        assertEquals("Chapter 4: 'Incoming Typhoon' [Locked in Demo]", stateCh4.getChapterTitle())

        val stateCh5 = GameState(dayNumber = 14, chapter = 5)
        assertEquals("Chapter 5: 'The Expansion' [Locked in Demo]", stateCh5.getChapterTitle())
    }

    @Test
    fun testUtangRecordSettlement() {
        val record = UtangRecord(
            id = "utang_test",
            customerId = "kuya_bodjie",
            customerName = "Kuya Bodjie",
            amount = 38,
            itemsDescription = "Cobra + SkyFlakes",
            dayRecorded = 2,
            isSettled = false
        )

        assertFalse(record.isSettled)
        record.isSettled = true
        assertTrue(record.isSettled)
    }

    @Test
    fun testStoreUpgradesCatalog() {
        val catalog = StoreUpgradesCatalog.getDefaultUpgrades()
        assertTrue(catalog.size >= 5)

        val fridge = catalog.find { it.id == "tier3_fridge" }
        assertNotNull(fridge)
        assertEquals(3, fridge!!.tier)
        assertTrue(fridge.isLocked)
    }

    @Test
    fun testRadioStationsAvailable() {
        val stations = RadioStationsData.STATIONS
        assertEquals(4, stations.size)
        assertEquals("DZMM Radyo Patrol 630", stations[0].name)
        assertEquals("Love Radio 90.7", stations[1].name)
        assertEquals("Barangay LS 97.1", stations[2].name)
        assertEquals("Radio Veritas 846", stations[3].name)
    }
}
