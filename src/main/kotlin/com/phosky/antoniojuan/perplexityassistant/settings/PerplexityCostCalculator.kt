package com.phosky.antoniojuan.perplexityassistant.settings

object PerplexityCostCalculator {
    const val INPUT_PRICE_PER_TOKEN = 0.000003
    const val OUTPUT_PRICE_PER_TOKEN = 0.000015

    fun estimateCost(promptTokens: Int, completionTokens: Int): Double {
        return ModelPricing.estimateCost("sonar-pro", promptTokens, completionTokens)
    }

    fun estimateCost(model: String, promptTokens: Int, completionTokens: Int): Double {
        return ModelPricing.estimateCost(model, promptTokens, completionTokens)
    }
}