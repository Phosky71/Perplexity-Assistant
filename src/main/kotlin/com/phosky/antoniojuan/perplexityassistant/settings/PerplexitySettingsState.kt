package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.time.YearMonth

@State(
    name = "PerplexitySettingsState",
    storages = [Storage("perplexity-sonar-pro.xml")]
)
class PerplexitySettingsState : PersistentStateComponent<PerplexitySettingsState> {
    var basePrompt: String =
        "Actúa como asistente experto de código. Analiza el siguiente contenido y responde de forma útil."
    var estimatedCostPerRequestUsd: Double = 0.02
    var monthlyLimitUsd: Double = 5.0
    var currentMonth: String = YearMonth.now().toString()
    var usedUsdThisMonth: Double = 0.0
    var isMonthStarted: Boolean = false

    override fun getState(): PerplexitySettingsState = this

    override fun loadState(state: PerplexitySettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun canMakeRequest(): Boolean {
        val nowMonth = YearMonth.now().toString()
        if (nowMonth != currentMonth) {
            currentMonth = nowMonth
            usedUsdThisMonth = 0.0
            isMonthStarted = false
        }
        if (!isMonthStarted) return false
        return usedUsdThisMonth + estimatedCostPerRequestUsd <= monthlyLimitUsd
    }

    fun registerRequest() {
        usedUsdThisMonth += estimatedCostPerRequestUsd
    }

    companion object {
        fun getInstance(): PerplexitySettingsState =
            com.intellij.openapi.application.ApplicationManager
                .getApplication()
                .getService(PerplexitySettingsState::class.java)
    }
}
