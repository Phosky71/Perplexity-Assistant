package com.phosky.antoniojuan.perplexityassistant.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PerplexitySettingsStateTest {

    @Test
    fun canMakeRequest_newMonth_resetsCounter() {
        val s = PerplexitySettingsState()
        s.currentMonth = "2020-01"
        s.usedUsdThisMonth = 100.0
        s.estimatedCostPerRequestUsd = 0.02
        s.monthlyLimitUsd = 5.0
        // Force "new month" by setting currentMonth to past; YearMonth.now() differs
        assertTrue(s.canMakeRequest())
        assertEquals(0.0, s.usedUsdThisMonth, 0.0001)
    }

    @Test
    fun canMakeRequest_limitReached_returnsFalse() {
        val s = PerplexitySettingsState()
        s.currentMonth = java.time.YearMonth.now().toString()
        s.usedUsdThisMonth = 4.99
        s.estimatedCostPerRequestUsd = 0.02
        s.monthlyLimitUsd = 5.0
        assertFalse(s.canMakeRequest())
    }

    @Test
    fun canMakeRequest_zeroOrNegativeLimit_returnsFalse() {
        val s = PerplexitySettingsState()
        s.currentMonth = java.time.YearMonth.now().toString()
        s.usedUsdThisMonth = 0.0
        s.estimatedCostPerRequestUsd = 0.02
        s.monthlyLimitUsd = 0.0
        assertFalse(s.canMakeRequest())
        s.monthlyLimitUsd = -1.0
        assertFalse(s.canMakeRequest())
    }

    @Test
    fun registerRequest_accumulates() {
        val s = PerplexitySettingsState()
        s.usedUsdThisMonth = 0.0
        s.registerRequest(0.5)
        s.registerRequest(0.25)
        assertEquals(0.75, s.usedUsdThisMonth, 0.0001)
    }

    @Test
    fun resetUsage_clearsCounter() {
        val s = PerplexitySettingsState()
        s.usedUsdThisMonth = 1.23
        s.resetUsage()
        assertEquals(0.0, s.usedUsdThisMonth, 0.0001)
    }
}

class PerplexityCostCalculatorTest {
    @Test
    fun estimateCost_knownTokens() {
        // 1000 input * 0.000003 + 500 output * 0.000015
        val expected = 1000 * 0.000003 + 500 * 0.000015
        assertEquals(expected, PerplexityCostCalculator.estimateCost(1000, 500), 0.0000001)
    }

    @Test
    fun estimateCost_zeroTokens() {
        assertEquals(0.0, PerplexityCostCalculator.estimateCost(0, 0), 0.0000001)
    }
}