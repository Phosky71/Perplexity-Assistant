package com.phosky.antoniojuan.perplexityassistant.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelPricingTest {
    @Test
    fun estimateCost_sonarPro() {
        val cost = ModelPricing.estimateCost("sonar-pro", 1000, 500)
        assertEquals(1000 * 0.000003 + 500 * 0.000015, cost, 0.0000001)
    }

    @Test
    fun estimateCost_unknownModelFallsBack() {
        val cost = ModelPricing.estimateCost("unknown", 1000, 500)
        assertTrue(cost > 0)
    }

    @Test
    fun verifiedFlag() {
        assertTrue(ModelPricing.priceFor("sonar-pro").verified)
    }
}