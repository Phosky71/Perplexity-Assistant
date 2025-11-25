package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.openapi.options.Configurable
import java.awt.BorderLayout
import java.awt.GridLayout
import java.time.YearMonth
import javax.swing.*

class PerplexitySettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private lateinit var apiKeyField: JPasswordField
    private lateinit var promptArea: JTextArea
    private lateinit var costField: JTextField
    private lateinit var limitField: JTextField
    private lateinit var statusLabel: JLabel
    private lateinit var startMonthButton: JButton

    override fun getDisplayName(): String = "Perplexity Assistant"

    override fun createComponent(): JComponent {
        val settings = PerplexitySettingsState.getInstance()

        if (panel == null) {
            panel = JPanel(BorderLayout())
            val form = JPanel(GridLayout(0, 1, 4, 4))

            apiKeyField = JPasswordField()
            promptArea = JTextArea(4, 40)
            costField = JTextField()
            limitField = JTextField()
            statusLabel = JLabel()
            startMonthButton = JButton("Start month")

            form.add(JLabel("Perplexity API Key:"))
            form.add(apiKeyField)
            form.add(JLabel("Base prompt:"))
            form.add(JScrollPane(promptArea))
            form.add(JLabel("Estimated cost per request (USD):"))
            form.add(costField)
            form.add(JLabel("Monthly limit (USD):"))
            form.add(limitField)
            form.add(statusLabel)
            form.add(startMonthButton)

            panel!!.add(form, BorderLayout.NORTH)

            startMonthButton.addActionListener {
                val s = PerplexitySettingsState.getInstance()
//                s.isMonthStarted = true
                s.currentMonth = YearMonth.now().toString()
                updateStatus()
            }

            reset()
        }
        return panel!!
    }

    private fun updateStatus() {
        val s = PerplexitySettingsState.getInstance()
        val month = s.currentMonth
        val used = s.usedUsdThisMonth
        val limit = s.monthlyLimitUsd
//        val started = if (s.isMonthStarted) "Active" else "Inactive (click 'Start month')"
        statusLabel.text = "Month: $month | Used: $used USD / $limit USD "
    }

    override fun isModified(): Boolean {
        val s = PerplexitySettingsState.getInstance()
        val key = String(apiKeyField.password)
        return key != (PerplexityCredentials.getApiKey() ?: "") ||
                promptArea.text != s.basePrompt ||
                costField.text.toDoubleOrNull() != s.estimatedCostPerRequestUsd ||
                limitField.text.toDoubleOrNull() != s.monthlyLimitUsd
    }

    override fun apply() {
        val s = PerplexitySettingsState.getInstance()
        val key = String(apiKeyField.password)
        PerplexityCredentials.saveApiKey(key.ifBlank { null })
        s.basePrompt = promptArea.text
        s.estimatedCostPerRequestUsd = costField.text.toDoubleOrNull() ?: s.estimatedCostPerRequestUsd
        s.monthlyLimitUsd = limitField.text.toDoubleOrNull() ?: s.monthlyLimitUsd
        updateStatus()
    }

    override fun reset() {
        val s = PerplexitySettingsState.getInstance()
        apiKeyField.text = PerplexityCredentials.getApiKey() ?: ""
        promptArea.text = s.basePrompt
        costField.text = s.estimatedCostPerRequestUsd.toString()
        limitField.text = s.monthlyLimitUsd.toString()
        updateStatus()
    }

    override fun disposeUIResources() {
        panel = null
    }
}
