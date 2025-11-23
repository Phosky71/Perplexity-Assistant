package com.phosky.antoniojuan.perplexityassistant.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.wm.ToolWindowManager
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCredentials
import com.phosky.antoniojuan.perplexityassistant.settings.PerplexitySettingsState
import com.phosky.antoniojuan.perplexityassistant.ui.PerplexityToolWindowFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SendSelectionAction : AnAction() {

    private val logger = Logger.getInstance(SendSelectionAction::class.java)
    private val client = OkHttpClient()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(CommonDataKeys.EDITOR) ?: return
        val selection = editor.selectionModel.selectedText
        if (selection.isNullOrBlank()) {
            Messages.showInfoMessage(project, "No hay texto seleccionado.", "Perplexity")
            return
        }
        val apiKey = PerplexityCredentials.getApiKey()
        if (apiKey.isNullOrBlank()) {
            Messages.showErrorDialog(project, "Configura primero la API key de Perplexity en Settings.", "Perplexity")
            return
        }
        val settings = PerplexitySettingsState.getInstance()
        if (!settings.canMakeRequest()) {
            Messages.showWarningDialog(
                project,
                "Has alcanzado el límite mensual configurado (${settings.monthlyLimitUsd} USD).",
                "Perplexity"
            )
            return
        }
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Perplexity")
        toolWindow?.show()
        val panel = PerplexityToolWindowFactory.instance

        val extraPrompt = panel?.getExtraPrompt().orEmpty()
        val finalPrompt = buildString {
            append(settings.basePrompt)
            if (extraPrompt.isNotBlank()) {
                append("\n\nInstrucciones adicionales: ")
                append(extraPrompt)
            }
        }
        ProgressManager.getInstance().runProcessWithProgressSynchronously(
            {
                val responseText = callPerplexity(apiKey, finalPrompt, selection)
                settings.registerRequest()
                ApplicationManager.getApplication().invokeLater {
                    panel?.setResponseText(responseText)
                }
            },
            "Enviando a Perplexity...",
            true,
            project
        )
    }

    private fun callPerplexity(apiKey: String, prompt: String, selection: String): String {
        return try {
            val url = "https://api.perplexity.ai/chat/completions"
            val mediaType = "application/json".toMediaType()
            val json = JSONObject()
            json.put("model", "sonar-pro")
            json.put(
                "messages", listOf(
                    JSONObject().put("role", "system").put("content", prompt),
                    JSONObject().put("role", "user").put("content", selection)
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
                    return "Error de Perplexity: HTTP ${resp.code} - ${resp.message}"
                }
                val respBody = resp.body?.string().orEmpty()
                val obj = JSONObject(respBody)
                val choices = obj.optJSONArray("choices")
                if (choices == null || choices.length() == 0) {
                    return "Respuesta vacía de Perplexity."
                }
                val message = choices.getJSONObject(0).getJSONObject("message")
                message.getString("content")
            }
        } catch (t: Throwable) {
            logger.warn("Error llamando a Perplexity", t)
            "Error llamando a Perplexity: ${t.message}"
        }
    }
}
