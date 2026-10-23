package com.phosky.antoniojuan.perplexityassistant.ui

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import com.phosky.antoniojuan.perplexityassistant.network.PerplexityClient
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.util.concurrent.TimeUnit
import javax.swing.*
import javax.swing.border.LineBorder

class PerplexityToolWindowPanel(val project: Project) : JPanel(BorderLayout()) {
    companion object {
        private const val MAX_CHAT_MESSAGES = 50
    }
    private var emptyStateLabel: JLabel? = null
    private val configureKeyButton = JButton("Open Settings")
    private val settings = PerplexitySettingsState.getInstance()

    private val settingsButton = JButton("Settings")
    private val apiKeyStatus = JLabel()
    private val limitStatus = JLabel()
    private val monthlyUsageStatus = JLabel()
    private val chatPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    private val chatScrollPane = JScrollPane(chatPanel)
    private val promptField = JTextField()
    private val sendButton = JButton("Send")

    init {
        val topPanel = JPanel(BorderLayout())
        topPanel.add(settingsButton, BorderLayout.WEST)

        val statusPanel = JPanel()
        statusPanel.layout = BoxLayout(statusPanel, BoxLayout.Y_AXIS)
        statusPanel.add(apiKeyStatus)
        statusPanel.add(limitStatus)
        statusPanel.add(monthlyUsageStatus)
        topPanel.add(statusPanel, BorderLayout.CENTER)

        updateStatus()

        // Settings action opens the IDE settings configurable
        settingsButton.addActionListener {
            com.intellij.openapi.options.ShowSettingsUtil.getInstance()
                .showSettingsDialog(project, "Perplexity Assistant")
        }

        chatScrollPane.preferredSize = JBUI.size(400, 300)

        emptyStateLabel = JLabel("Send a prompt to start", SwingConstants.CENTER)
        emptyStateLabel?.foreground = JBColor.gray
        chatPanel.add(emptyStateLabel)

        val promptPanel = JPanel(BorderLayout())
        promptPanel.add(promptField, BorderLayout.CENTER)
        promptPanel.add(sendButton, BorderLayout.EAST)

        add(topPanel, BorderLayout.NORTH)
        add(chatScrollPane, BorderLayout.CENTER)
        add(promptPanel, BorderLayout.SOUTH)

        sendButton.addActionListener {
            val prompt = getPromptText()
            val apiKey = PerplexityCredentials.getApiKey()

            if (prompt.isBlank()) {
                JOptionPane.showMessageDialog(this, "Enter a prompt before sending.")
                return@addActionListener
            }
            if (apiKey.isNullOrBlank()) {
                JOptionPane.showMessageDialog(this, "Configure the API Key first in Settings.")
                return@addActionListener
            }
            if (!settings.canMakeRequest(settings.estimatedCostPerRequestUsd)) {
                JOptionPane.showMessageDialog(this, "Monthly limit reached (${settings.monthlyLimitUsd} USD).")
                return@addActionListener
            }
            sendPromptToPerplexity(prompt, apiKey)
        }
    }

    private var onboardingShown = false

    private fun updateStatus() {
        val keyStatus = if (PerplexityCredentials.getApiKey().isNullOrBlank())
            "API Key: Not configured" else "API Key: OK"
        apiKeyStatus.text = keyStatus
        limitStatus.text = "Monthly limit: ${settings.monthlyLimitUsd} USD"
        monthlyUsageStatus.text = "Spent this month: ${String.format("%.4f", settings.usedUsdThisMonth)} USD"

        val keyMissing = PerplexityCredentials.getApiKey().isNullOrBlank()
        if (keyMissing) {
            if (!onboardingShown) {
                onboardingShown = true
                JOptionPane.showMessageDialog(
                    this,
                    "No API key configured. Your prompts and code are sent to Perplexity. Configure your key in Settings.",
                    "Perplexity Assistant",
                    JOptionPane.INFORMATION_MESSAGE
                )
            }
            if (configureKeyButton.parent == null) {
                chatPanel.add(Box.createVerticalStrut(8))
                chatPanel.add(configureKeyButton)
                chatPanel.revalidate()
                chatPanel.repaint()
            }
            configureKeyButton.addActionListener {
                com.intellij.openapi.options.ShowSettingsUtil.getInstance()
                    .showSettingsDialog(project, "Perplexity Assistant")
            }
        } else {
            chatPanel.remove(configureKeyButton)
        }
    }

    fun getPromptText(): String = promptField.text.orEmpty()

    fun showResponse(prompt: String, response: String) {
        addChatMessage(prompt, response)
        promptField.text = ""
        updateStatus()
    }

    fun addChatMessage(prompt: String, response: String) {
        emptyStateLabel?.let { chatPanel.remove(it); emptyStateLabel = null }
        while (chatPanel.componentCount >= MAX_CHAT_MESSAGES * 2) {
            chatPanel.remove(0)
        }
        val messagePanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
        messagePanel.border = JBUI.Borders.customLine(JBColor.border(), 1)
        messagePanel.add(JLabel("<html><b>Prompt:</b> $prompt</html>"))

        val responseArea = JTextArea(response).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }

        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val insertButton = JButton("Insert in editor")
        insertButton.addActionListener { insertTextInEditor(response) }
        val copyButton = JButton("Copy response")
        copyButton.addActionListener {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            clipboard.setContents(StringSelection(response), null)
        }

        buttonPanel.add(insertButton)
        buttonPanel.add(copyButton)

        messagePanel.add(responseArea)
        messagePanel.add(buttonPanel)

        chatPanel.add(Box.createVerticalStrut(8))
        chatPanel.add(messagePanel)
        chatPanel.revalidate()
        chatPanel.repaint()
        SwingUtilities.invokeLater { chatScrollPane.verticalScrollBar.value = chatScrollPane.verticalScrollBar.maximum }
    }

    private fun insertTextInEditor(text: String) {
        val editor = com.intellij.openapi.fileEditor.FileEditorManager.getInstance(project).selectedTextEditor ?: return
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(caretOffset, text)
        }
    }

    private fun sendPromptToPerplexity(prompt: String, apiKey: String) {
        sendButton.isEnabled = false
        try {
            ProgressManager.getInstance().runProcessWithProgressSynchronously(
                {
                    val result = PerplexityClient.chat(prompt, apiKey, settings.basePrompt, true)
                    if (result.errorMessage == null) {
                        settings.registerRequest(result.costUsd)
                    }
                    SwingUtilities.invokeLater {
                        if (result.errorMessage == null) {
                            showResponse(prompt, result.text)
                        } else {
                            JOptionPane.showMessageDialog(this, result.errorMessage)
                        }
                        updateStatus()
                    }
                },
                "Sending to Perplexity...",
                true,
                project
            )
        } finally {
            SwingUtilities.invokeLater { sendButton.isEnabled = true }
        }
    }

}
