package com.phosky.antoniojuan.perplexityassistant.ui

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.awt.*
import java.awt.datatransfer.StringSelection
import javax.swing.*
import javax.swing.border.LineBorder

class PerplexityToolWindowPanel(val project: Project) : JPanel(BorderLayout()) {
    private val settings = PerplexitySettingsState.getInstance()

    private val settingsButton = JButton("Ajustes")
    private val apiKeyStatus = JLabel()
    private val limitStatus = JLabel()
    private val monthlyUsageStatus = JLabel()
    private val chatPanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
    private val chatScrollPane = JScrollPane(chatPanel)
    private val promptField = JTextField()
    private val sendButton = JButton("Enviar")
    private val client = OkHttpClient()

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
        // Acción "Ajustes" con opción para resetear el gasto
        settingsButton.addActionListener {
            val dialog = JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Ajustes Perplexity",
                Dialog.ModalityType.APPLICATION_MODAL
            )
            dialog.layout = BorderLayout()
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)

            val apiKeyField = JPasswordField(30)
            apiKeyField.text = PerplexityCredentials.getApiKey() ?: ""
            val apiKeySave = JButton("Guardar API Key")
            apiKeySave.addActionListener {
                val key = String(apiKeyField.password)
                if (key.isNotBlank()) {
                    PerplexityCredentials.saveApiKey(key)
                    JOptionPane.showMessageDialog(dialog, "API Key guardada correctamente.")
                    updateStatus()
                }
            }
            val limitField = JTextField(settings.monthlyLimitUsd.toString(), 8)
            val limitSave = JButton("Guardar límite mensual")
            limitSave.addActionListener {
                val value = limitField.text.toDoubleOrNull()
                if (value != null && value >= 0.01) {
                    settings.monthlyLimitUsd = value
                    JOptionPane.showMessageDialog(dialog, "Límite guardado correctamente.")
                    updateStatus()
                }
            }
            val resetUsageBtn = JButton("Resetear gasto este mes")
            resetUsageBtn.addActionListener {
                settings.resetUsage()
                JOptionPane.showMessageDialog(dialog, "Contador de gasto mensual reseteado.")
                updateStatus()
            }
            panel.add(JLabel("API Key:"))
            panel.add(apiKeyField)
            panel.add(apiKeySave)
            panel.add(Box.createVerticalStrut(10))
            panel.add(JLabel("Límite mensual (USD):"))
            panel.add(limitField)
            panel.add(limitSave)
            panel.add(Box.createVerticalStrut(10))
            panel.add(resetUsageBtn)
            dialog.add(panel, BorderLayout.CENTER)
            dialog.pack()
            dialog.setLocationRelativeTo(this)
            dialog.isVisible = true
        }

        chatScrollPane.preferredSize = Dimension(500, 350)
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
                JOptionPane.showMessageDialog(this, "Introduce un prompt antes de enviar.")
                return@addActionListener
            }
            if (apiKey.isNullOrBlank()) {
                JOptionPane.showMessageDialog(this, "Configura la API Key primero en Ajustes.")
                return@addActionListener
            }
            if (!settings.canMakeRequest()) {
                JOptionPane.showMessageDialog(this, "Límite mensual alcanzado (${settings.monthlyLimitUsd} USD).")
                return@addActionListener
            }
            sendPromptToPerplexity(prompt, apiKey)
        }
    }

    private fun updateStatus() {
        val keyStatus = if (PerplexityCredentials.getApiKey().isNullOrBlank())
            "API Key: NO configurada" else "API Key: OK"
        apiKeyStatus.text = keyStatus
        limitStatus.text = "Límite mensual: ${settings.monthlyLimitUsd} USD"
        monthlyUsageStatus.text = "Gastado este mes: ${settings.usedUsdThisMonth} USD"
    }

    fun getPromptText(): String = promptField.text.orEmpty()
    fun showResponse(prompt: String, response: String) {
        addChatMessage(prompt, response)
        promptField.text = ""
        updateStatus()
    }

    fun addChatMessage(prompt: String, response: String) {
        val messagePanel = JPanel().apply { layout = BoxLayout(this, BoxLayout.Y_AXIS) }
        messagePanel.border = LineBorder(Color.GRAY, 1)
        messagePanel.add(JLabel("<html><b>Prompt:</b> $prompt</html>"))
        val responseArea = JTextArea(response).apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
        }
        val buttonPanel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val insertButton = JButton("Insertar en editor")
        insertButton.addActionListener { insertTextInEditor(response) }
        val copyButton = JButton("Copiar respuesta")
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
        val editor = EditorFactory.getInstance().allEditors
            .firstOrNull { it.project == project } ?: return
        val document = editor.document
        val caretOffset = editor.caretModel.offset
        WriteCommandAction.runWriteCommandAction(project) {
            document.insertString(caretOffset, text)
        }
    }

    private fun sendPromptToPerplexity(prompt: String, apiKey: String) {
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                val (response, cost) = callPerplexityApiWithCost(prompt, apiKey)
                settings.registerRequest(cost)
                SwingUtilities.invokeLater {
                    showResponse(prompt, response)
                }
            },
            "Enviando a Perplexity...",
            true,
            project
        )
    }

    // Devuelve respuesta y coste real (double)
    private fun callPerplexityApiWithCost(prompt: String, apiKey: String): Pair<String, Double> {
        return try {
            val url = "https://api.perplexity.ai/chat/completions"
            val mediaType = "application/json".toMediaType()
            val json = JSONObject()
            json.put("model", "sonar-pro")
            json.put(
                "messages", listOf(
                    JSONObject().put("role", "system").put("content", settings.basePrompt),
                    JSONObject().put("role", "user").put("content", prompt)
                )
            )
            val body = json.toString().toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()
            client.newCall(request).execute().use { resp ->
                if (!resp.isSuccessful) {
                    return Pair("Error de Perplexity: HTTP ${resp.code} - ${resp.message}", 0.0)
                }
                val respBody = resp.body?.string().orEmpty()
                val obj = JSONObject(respBody)
                val choices = obj.optJSONArray("choices")
                val answer = if (choices != null && choices.length() > 0) {
                    choices.getJSONObject(0).getJSONObject("message").optString("content", "Sin respuesta.")
                } else {
                    "Respuesta vacía de Perplexity."
                }

                val usage = obj.optJSONObject("usage")
                val costObj = usage?.optJSONObject("cost")
                val totalCost = costObj?.optDouble("total_cost", 0.0) ?: 0.0
                val promptTokens = usage?.optInt("prompt_tokens", 0) ?: 0
                val completionTokens = usage?.optInt("completion_tokens", 0) ?: 0
                val tokens = promptTokens + completionTokens

                val citations = obj.optJSONArray("citations")
                val citationsList = mutableListOf<String>()
                if (citations != null) {
                    for (i in 0 until citations.length()) {
                        citationsList.add(citations.getString(i))
                    }
                }

                val responseInfo = buildString {
                    append(answer)
                    append("\n\n─── Info petición ───\n")
                    append("Total tokens: $tokens\n")
                    append("Coste total: $totalCost USD\n")
                    if (citationsList.isNotEmpty()) {
                        append("Citas:\n")
                        citationsList.forEach { cite -> append("- $cite\n") }
                    }
                }
                Pair(responseInfo, totalCost)
            }
        } catch (t: Throwable) {
            Pair("Error llamando a Perplexity: ${t.message}", 0.0)
        }
    }
}
