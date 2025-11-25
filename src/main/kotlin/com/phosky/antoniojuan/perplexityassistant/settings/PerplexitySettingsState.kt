package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import java.time.YearMonth

@State(
    name = "PerplexitySettingsState",
    storages = [Storage("perplexity-assistant.xml")]
)
@Service(Service.Level.APP)
class PerplexitySettingsState : PersistentStateComponent<PerplexitySettingsState> {

    var estimatedCostPerRequestUsd: Double = 0.02
    var monthlyLimitUsd: Double = 5.0
    var usedUsdThisMonth: Double = 0.0
    var currentMonth: String = YearMonth.now().toString()

    var basePrompt: String = """
        You are an expert programming assistant specialized in code refactoring, technical explanation, and best practices.
        You must analyze the content sent to you and respond clearly, organized, and professionally.
        Your main job is to help understand code, suggest improvements, find bugs, and optimize both logic and style according to the detected language.

        - Always briefly explain the problem, the solution, and the recommended steps.
        - If the user requests a rewrite, refactor the code with clear comments and improve readability.
        - If you detect bugs or insecure practices, point them out and fix them.
        - If the user asks for documentation, generate appropriate Javadoc/KDoc/Python docstring comments.
        - Summarize in bullet points the most relevant information if your response exceeds 15 lines.
        - Never provide irrelevant content, external links, or unsolicited information.
    """.trimIndent()

    override fun getState(): PerplexitySettingsState = this

    override fun loadState(state: PerplexitySettingsState) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun canMakeRequest(): Boolean {
        val nowMonth = YearMonth.now().toString()
        
        // If it's a new month, reset the usage counter
        if (nowMonth != currentMonth) {
            currentMonth = nowMonth
            usedUsdThisMonth = 0.0
        }
        
        // Check if adding another request would exceed the monthly limit
        return usedUsdThisMonth + estimatedCostPerRequestUsd <= monthlyLimitUsd
    }

    fun registerRequest(cost: Double) {
        usedUsdThisMonth += cost
    }

    fun resetUsage() {
        usedUsdThisMonth = 0.0
    }

    companion object {
        @JvmStatic
        fun getInstance(): PerplexitySettingsState {
            return ApplicationManager.getApplication().getService(PerplexitySettingsState::class.java)
        }
    }
}
