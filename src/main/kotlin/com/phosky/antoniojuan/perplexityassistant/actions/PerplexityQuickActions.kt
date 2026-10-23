package com.phosky.antoniojuan.perplexityassistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.phosky.antoniojuan.perplexityassistant.network.PerplexityClient
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowFactory
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowPanel

abstract class PerplexityQuickAction(
    private val key: String,
    text: String
) : AnAction(text) {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selected = editor.selectionModel.selectedText
        if (selected.isNullOrBlank()) {
            Messages.showInfoMessage(project, "Select code first.", "Perplexity")
            return
        }

        val settings = PerplexitySettingsState.getInstance()
        val template = settings.quickActionPrompts[key] ?: "{code}"
        val prompt = template.replace("{code}", selected)

        val apiKey = PerplexityCredentials.getApiKey()
        if (apiKey.isNullOrBlank()) {
            Messages.showErrorDialog(project, "Configure the Perplexity API key first.", "Perplexity")
            return
        }
        if (!settings.canMakeRequest(settings.estimatedCostPerRequestUsd)) {
            Messages.showWarningDialog(project, "Monthly limit reached (${settings.monthlyLimitUsd} USD).", "Perplexity")
            return
        }

        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Perplexity")
        toolWindow?.show()
        val panel = toolWindow?.contentManager?.contents?.firstOrNull()?.component as? PerplexityToolWindowPanel

        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                val result = PerplexityClient.chat(prompt, apiKey, settings.basePrompt, true)
                if (result.errorMessage == null) settings.registerRequest(result.costUsd)
                ApplicationManager.getApplication().invokeLater {
                    if (result.errorMessage == null) panel?.showResponse(prompt, result.text)
                    else Messages.showErrorDialog(project, result.errorMessage, "Perplexity")
                }
            },
            "Sending to Perplexity...",
            true,
            project
        )
    }
}

class ExplainAction : PerplexityQuickAction("explain", "Explain with Perplexity")
class ReviewAction : PerplexityQuickAction("review", "Review with Perplexity")
class RefactorAction : PerplexityQuickAction("refactor", "Refactor with Perplexity")
class TestsAction : PerplexityQuickAction("tests", "Generate Tests with Perplexity")
class DocsAction : PerplexityQuickAction("docs", "Generate Docs with Perplexity")