package com.phosky.antoniojuan.perplexityassistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.phosky.antoniojuan.perplexityassistant.network.PerplexityClient
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowPanel

class SendSelectionAction : AnAction() {

    private val logger = Logger.getInstance(SendSelectionAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return

        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Perplexity")
        toolWindow?.show()

        val panel = toolWindow?.contentManager?.contents?.firstOrNull()?.component as? PerplexityToolWindowPanel
            ?: return

        val selectedText = editor.selectionModel.selectedText
        val prompt = if (!selectedText.isNullOrBlank()) selectedText else panel.getPromptText()
        if (prompt.isBlank()) {
            Messages.showInfoMessage(project, "Enter a prompt or select text in the editor.", "Perplexity")
            return
        }

        val apiKey = PerplexityCredentials.getApiKey()
        if (apiKey.isNullOrBlank()) {
            Messages.showErrorDialog(project, "Configure the Perplexity API key first.", "Perplexity")
            return
        }

        val settings = PerplexitySettingsState.getInstance()
        if (!settings.canMakeRequest(settings.estimatedCostPerRequestUsd)) {
            Messages.showWarningDialog(
                project,
                "You have reached the configured monthly limit (${settings.monthlyLimitUsd} USD).",
                "Perplexity"
            )
            return
        }

        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                val result = PerplexityClient.chat(prompt, apiKey, "", false, PerplexityClient.API_URL, ProgressManager.getInstance().progressIndicator)
                if (result.canceled) return@runProcessWithProgressSynchronously
                if (result.errorMessage == null) {
                    settings.registerRequest(result.costUsd)
                }
                ApplicationManager.getApplication().invokeLater {
                    if (result.canceled) return@invokeLater
                    if (result.errorMessage == null) {
                        panel.showResponse(prompt, result.text)
                    } else {
                        Messages.showErrorDialog(project, result.errorMessage, "Perplexity")
                    }
                }
            },
            "Sending to Perplexity...",
            true,
            project
        )
    }
}