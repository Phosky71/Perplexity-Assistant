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

    var quickActionPrompts: MutableMap<String, String> = defaultQuickActionPrompts()

    var selectedModel: String = "sonar-pro"

    var basePrompt: String = """
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

    fun canMakeRequest(estimatedCost: Double = estimatedCostPerRequestUsd): Boolean {
        synchronized(this) {
            val nowMonth = YearMonth.now().toString()

            if (nowMonth != currentMonth) {
                currentMonth = nowMonth
                usedUsdThisMonth = 0.0
            }

            return usedUsdThisMonth + estimatedCost <= monthlyLimitUsd
        }
    }

    fun registerRequest(cost: Double) {
        synchronized(this) {
            usedUsdThisMonth += cost
        }
    }

    fun resetUsage() {
        synchronized(this) {
            usedUsdThisMonth = 0.0
        }
    }

    fun resetQuickActionPrompts() {
        synchronized(this) {
            quickActionPrompts = defaultQuickActionPrompts()
        }
    }

    companion object {
        const val USAGE_WARNING_THRESHOLD = 0.8

        fun isNearLimit(used: Double, limit: Double, threshold: Double = USAGE_WARNING_THRESHOLD): Boolean {
            return limit > 0 && (used / limit) >= threshold
        }

        @JvmStatic
        fun defaultQuickActionPrompts(): MutableMap<String, String> = mutableMapOf(
            "explain" to "Explain the following code clearly:\n\n{code}",
            "review" to "Review the following code for bugs and improvements:\n\n{code}",
            "refactor" to "Refactor the following code for readability and maintainability:\n\n{code}",
            "tests" to "Generate unit tests for the following code:\n\n{code}",
            "docs" to "Generate documentation comments for the following code:\n\n{code}"
        )

        @JvmStatic
        fun getInstance(): PerplexitySettingsState {
            return ApplicationManager.getApplication().getService(PerplexitySettingsState::class.java)
        }
    }
}