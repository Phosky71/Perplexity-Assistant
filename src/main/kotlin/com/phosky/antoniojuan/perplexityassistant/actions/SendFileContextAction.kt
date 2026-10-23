package com.phosky.antoniojuan.perplexityassistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.phosky.antoniojuan.perplexityassistant.network.PerplexityClient
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCostCalculator
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowFactory
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowPanel
import java.awt.BorderLayout
import java.io.File
import javax.swing.*

class SendFileContextAction : AnAction() {

    companion object {
        const val MAX_CONTEXT_CHARS = 12000
        private val SECRET_PATTERNS = listOf(
            Regex("(?i)(api[_-]?key|secret|token|password)\\s*="),
            Regex("BEGIN (RSA )?PRIVATE KEY")
        )
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
        val editor = e.getData(CommonDataKeys.EDITOR)

        val relativePath = getRelativePath(project, file)
        val language = file.extension ?: "text"
        val fullContent = try { String(file.contentsToByteArray()) } catch (t: Throwable) { "" }
        val selection = editor?.selectionModel?.selectedText

        var context = buildContext(relativePath, language, fullContent)
        if (context.length > MAX_CONTEXT_CHARS) {
            val choice = JOptionPane.showOptionDialog(
                null,
                "Context is ${context.length} chars (limit $MAX_CONTEXT_CHARS). Choose how to proceed.",
                "Perplexity",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                arrayOf("Use selection only", "Truncate", "Cancel"),
                "Truncate"
            )
            when (choice) {
                0 -> {
                    if (selection.isNullOrBlank()) {
                        Messages.showWarningDialog(project, "No selection available.", "Perplexity")
                        return
                    }
                    context = buildContext(relativePath, language, selection)
                }
                1 -> context = context.take(MAX_CONTEXT_CHARS)
                else -> return
            }
        }

        val secretWarning = if (SECRET_PATTERNS.any { it.containsMatchIn(context) }) {
            "⚠ Possible secrets detected in context. Review before sending."
        } else null

        val panel = showPreviewDialog(project, context, secretWarning) ?: return

        val prompt = panel.text
        if (prompt.isBlank()) return

        val apiKey = PerplexityCredentials.getApiKey()
        if (apiKey.isNullOrBlank()) {
            Messages.showErrorDialog(project, "Configure the Perplexity API key first.", "Perplexity")
            return
        }
        val settings = PerplexitySettingsState.getInstance()
        if (!settings.canMakeRequest(settings.estimatedCostPerRequestUsd)) {
            Messages.showWarningDialog(project, "Monthly limit reached (${settings.monthlyLimitUsd} USD).", "Perplexity")
            return
        }

        val toolWindow = com.intellij.openapi.wm.ToolWindowManager.getInstance(project).getToolWindow("Perplexity")
        toolWindow?.show()
        val uiPanel = toolWindow?.contentManager?.contents?.firstOrNull()?.component as? PerplexityToolWindowPanel

        com.intellij.openapi.progress.ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                val result = PerplexityClient.chat(prompt, apiKey, settings.basePrompt, true)
                if (result.errorMessage == null) settings.registerRequest(result.costUsd)
                com.intellij.openapi.application.ApplicationManager.getApplication().invokeLater {
                    if (result.errorMessage == null) uiPanel?.showResponse(prompt, result.text)
                    else Messages.showErrorDialog(project, result.errorMessage, "Perplexity")
                }
            },
            "Sending to Perplexity...",
            true,
            project
        )
    }

    private fun getRelativePath(project: Project, file: VirtualFile): String {
        val base = project.basePath
        return if (base != null) {
            val baseFile = File(base)
            File(file.path).relativeToOrNull(baseFile)?.path ?: file.path
        } else file.path
    }

    internal fun buildContext(path: String, language: String, content: String): String {
        return "File: $path\nLanguage: $language\n\n```\n$content\n```"
    }

    private fun showPreviewDialog(project: Project, context: String, secretWarning: String?): JTextArea? {
        val dialog = JDialog(null as java.awt.Frame?, "Send to Perplexity — Preview", true)
        dialog.layout = BorderLayout()
        val textArea = JTextArea(context)
        textArea.rows = 18
        textArea.columns = 80
        val scroll = JScrollPane(textArea)
        dialog.add(scroll, BorderLayout.CENTER)

        val info = JPanel()
        info.layout = BoxLayout(info, BoxLayout.Y_AXIS)
        val chars = context.length
        val tokens = chars / 4
        val cost = PerplexityCostCalculator.estimateCost(tokens, 0)
        info.add(JLabel("Characters: $chars  |  Estimated tokens: $tokens  |  Input cost: ${String.format("%.6f", cost)} USD"))
        if (secretWarning != null) {
            val w = JLabel(secretWarning)
            w.foreground = java.awt.Color.RED
            info.add(w)
        }
        dialog.add(info, BorderLayout.NORTH)

        val buttons = JPanel()
        val ok = JButton("Send")
        val cancel = JButton("Cancel")
        buttons.add(ok)
        buttons.add(cancel)
        dialog.add(buttons, BorderLayout.SOUTH)

        var result: JTextArea? = null
        ok.addActionListener { result = textArea; dialog.dispose() }
        cancel.addActionListener { dialog.dispose() }

        dialog.pack()
        dialog.setLocationRelativeTo(null)
        dialog.isVisible = true

        return result
    }
}