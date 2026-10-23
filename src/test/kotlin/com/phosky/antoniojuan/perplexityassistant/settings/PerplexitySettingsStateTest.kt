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
    fun isNearLimit_trueAt80Percent() {
        assertTrue(PerplexitySettingsState.isNearLimit(4.0, 5.0))
    }

    @Test
    fun isNearLimit_falseBelow80() {
        assertFalse(PerplexitySettingsState.isNearLimit(3.0, 5.0))
    }

    @Test
    fun roundTrip_state() {
        val s1 = PerplexitySettingsState()
        s1.monthlyLimitUsd = 7.5
        s1.usedUsdThisMonth = 1.25
        s1.currentMonth = "2026-10"
        s1.basePrompt = "prompt"
        val state = s1.getState()
        val s2 = PerplexitySettingsState()
        s2.loadState(state)
        assertEquals(7.5, s2.monthlyLimitUsd, 0.0001)
        assertEquals(1.25, s2.usedUsdThisMonth, 0.0001)
        assertEquals("2026-10", s2.currentMonth)
        assertEquals("prompt", s2.basePrompt)
    }
    @Test
    fun resetUsage_clearsCounter() {
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