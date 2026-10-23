package com.phosky.antoniojuan.perplexityassistant.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.Configurable
import java.awt.BorderLayout
import java.awt.GridLayout
import java.time.YearMonth
import com.phosky.antoniojuan.perplexityassistant.ui.ChatHistoryStore
import javax.swing.*

class PerplexitySettingsConfigurable : Configurable {

    private var panel: JPanel? = null
    private lateinit var apiKeyField: JPasswordField
    private lateinit var promptArea: JTextArea
    private lateinit var costField: JTextField
    private lateinit var limitField: JTextField
    private lateinit var statusLabel: JLabel
    private lateinit var startMonthButton: JButton
    private lateinit var resetUsageButton: JButton

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
            resetUsageButton = JButton("Reset monthly spending")

            form.add(JLabel("Perplexity API Key:"))
            form.add(apiKeyField)
            form.add(JLabel("Base prompt:"))
            form.add(JScrollPane(promptArea))
            form.add(JLabel("Estimated cost per request (USD):"))
            form.add(costField)
            form.add(JLabel("Monthly limit (USD):"))
            val modelSelector = JComboBox(arrayOf("sonar", "sonar-pro", "sonar-reasoning-pro"))
            modelSelector.selectedItem = settings.selectedModel
            modelSelector.addActionListener { settings.selectedModel = modelSelector.selectedItem as String }
            form.add(JLabel("Model:"))
            form.add(modelSelector)
            form.add(statusLabel)
            form.add(startMonthButton)
            form.add(resetUsageButton)

            val resetPromptsButton = JButton("Reset quick action prompts")
            resetPromptsButton.addActionListener {
                PerplexitySettingsState.getInstance().resetQuickActionPrompts()
                JOptionPane.showMessageDialog(panel, "Quick action prompts reset.")
            }
            form.add(resetPromptsButton)

            val historyCheckbox = JCheckBox("Enable chat history (opt-in)")
            historyCheckbox.isSelected = settings.historyEnabled
            historyCheckbox.addActionListener { settings.historyEnabled = historyCheckbox.isSelected }
            form.add(historyCheckbox)

            val clearHistoryButton = JButton("Clear history")
            clearHistoryButton.addActionListener {
                ChatHistoryStore.clear()
                JOptionPane.showMessageDialog(panel, "History cleared.")
            }
            form.add(clearHistoryButton)

            panel!!.add(form, BorderLayout.NORTH)

            startMonthButton.addActionListener {
                val s = PerplexitySettingsState.getInstance()
                s.currentMonth = YearMonth.now().toString()
                updateStatus()
            }

            resetUsageButton.addActionListener {
                PerplexitySettingsState.getInstance().resetUsage()
                JOptionPane.showMessageDialog(panel, "Monthly spending counter reset.")
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
        ApplicationManager.getApplication().executeOnPooledThread {
            PerplexityCredentials.saveApiKey(key.ifBlank { null })
            ApplicationManager.getApplication().invokeLater { updateStatus() }
        }
        s.basePrompt = promptArea.text
        s.estimatedCostPerRequestUsd = costField.text.toDoubleOrNull() ?: s.estimatedCostPerRequestUsd
        s.monthlyLimitUsd = limitField.text.toDoubleOrNull() ?: s.monthlyLimitUsd
        updateStatus()
    }

    override fun reset() {
        val s = PerplexitySettingsState.getInstance()
        ApplicationManager.getApplication().executeOnPooledThread {
            val key = PerplexityCredentials.getApiKey() ?: ""
            ApplicationManager.getApplication().invokeLater {
                apiKeyField.text = key
            }
        }
        promptArea.text = s.basePrompt
        costField.text = s.estimatedCostPerRequestUsd.toString()
        limitField.text = s.monthlyLimitUsd.toString()
        updateStatus()
    }

    override fun disposeUIResources() {
        panel = null
    }
}