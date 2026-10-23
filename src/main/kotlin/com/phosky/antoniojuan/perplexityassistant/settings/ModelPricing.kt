package com.phosky.antoniojuan.perplexityassistant.settings

object ModelPricing {
    data class Price(val inputPerToken: Double, val outputPerToken: Double, val verified: Boolean)

    val prices: Map<String, Price> = mapOf(
        "sonar" to Price(0.000001, 0.000001, true),
        "sonar-pro" to Price(0.000003, 0.000015, true),
        "sonar-reasoning-pro" to Price(0.000002, 0.000008, true)
    )

    fun priceFor(model: String): Price = prices[model] ?: prices["sonar-pro"]!!

    fun estimateCost(model: String, promptTokens: Int, completionTokens: Int): Double {
        val p = priceFor(model)
        return (promptTokens * p.inputPerToken) + (completionTokens * p.outputPerToken)
    }
}