package com.phosky.antoniojuan.perplexityassistant.network

import com.phosky.antoniojuan.perplexityassistant.settings.ModelPricing
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.progress.ProcessCanceledException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

data class PerplexityResult(
    val text: String,
    val answer: String = "",
    val citations: List<String> = emptyList(),
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val costUsd: Double = 0.0,
    val httpCode: Int? = null,
    val errorMessage: String? = null,
    val canceled: Boolean = false
) {
    val isSuccess: Boolean get() = errorMessage == null
}

object PerplexityClient {

    const val API_URL = "https://api.perplexity.ai/chat/completions"
    const val MODEL = "sonar-pro"

    private val LOG = Logger.getInstance(PerplexityClient::class.java)

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Resuelve la URL base para la API.
     * Si existe la propiedad de sistema `perplexity.api.url` y su host es localhost o 127.0.0.1 (http/https),
     * se usa esa URL. En cualquier otro caso se ignora y se registra un aviso.
     * Esto evita que alguien redirija la API key a un servidor ajeno.
     */
    internal fun resolveApiUrl(): String {
        val overrideUrl = System.getProperty("perplexity.api.url")
        if (overrideUrl.isNullOrBlank()) return API_URL
        return try {
            val url = URL(overrideUrl)
            val host = url.host.lowercase()
            if (host == "localhost" || host == "127.0.0.1") {
                if (url.protocol == "http" || url.protocol == "https") {
                    overrideUrl
                } else {
                    LOG.warn("perplexity.api.url ignorada: protocolo no soportado (${url.protocol}). Se usa la URL por defecto.")
                    API_URL
                }
            } else {
                LOG.warn("perplexity.api.url ignorada: host no permitido ($host). Solo localhost/127.0.0.1 en desarrollo.")
                API_URL
            }
        } catch (e: Exception) {
            LOG.warn("perplexity.api.url inválida: ${e.message}. Se usa la URL por defecto.")
            API_URL
        }
    }

    fun chat(prompt: String, apiKey: String, basePrompt: String = "", includeSystemPrompt: Boolean = true, baseUrl: String = API_URL, progress: com.intellij.openapi.progress.ProgressIndicator? = null, model: String = MODEL): PerplexityResult {
        val effectiveUrl = if (baseUrl == API_URL) resolveApiUrl() else baseUrl
        return try {
            val body = buildPayload(prompt, basePrompt, includeSystemPrompt, model).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(effectiveUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            val call = client.newCall(request)
            val watcher = if (progress != null) {
                Thread {
                    while (!call.isCanceled() && !progress.isCanceled) {
                        try { Thread.sleep(50) } catch (e: InterruptedException) { break }
                        if (progress.isCanceled) call.cancel()
                    }
                }.apply { isDaemon = true; start() }
            } else null

            val result = try {
                call.execute().use { resp ->
                    val respBody = resp.body?.string().orEmpty()
                    parseResponse(resp.code, respBody, resp.isSuccessful, model)
                }
            } catch (e: java.io.IOException) {
                if (call.isCanceled()) PerplexityResult(text = "Canceled", canceled = true)
                else PerplexityResult(text = "Error calling Perplexity: ${e.message}", errorMessage = "Error calling Perplexity: ${e.message}")
            }
            watcher?.interrupt()
            result
        } catch (e: com.intellij.openapi.progress.ProcessCanceledException) {
            throw e
        } catch (e: kotlin.coroutines.cancellation.CancellationException) {
            throw e
        } catch (e: Exception) {
            PerplexityResult(text = "Error calling Perplexity: ${e.message}", errorMessage = "Error calling Perplexity: ${e.message}")
        }
    }

    internal fun buildPayload(prompt: String, basePrompt: String, includeSystemPrompt: Boolean, model: String = MODEL): String {
        val messagesArray = JSONArray()
        if (includeSystemPrompt && basePrompt.isNotBlank()) {
            messagesArray.put(JSONObject().put("role", "system").put("content", basePrompt))
        }
        messagesArray.put(JSONObject().put("role", "user").put("content", prompt))
        val json = JSONObject()
        json.put("model", model)
        json.put("messages", messagesArray)
        return json.toString()
    }

    internal fun parseResponse(httpCode: Int, body: String, isSuccessful: Boolean, model: String = MODEL): PerplexityResult {
        if (!isSuccessful) {
            val message = extractErrorMessage(body)
            val err = "Perplexity error: HTTP $httpCode - ${message ?: ""}".trim()
            return PerplexityResult(text = err, httpCode = httpCode, errorMessage = err)
        }
        return try {
            val obj = JSONObject(body)
            val choices = obj.optJSONArray("choices")
            val messageObj = if (choices != null && choices.length() > 0) {
                choices.optJSONObject(0)?.optJSONObject("message")
            } else null
            val answer = messageObj?.optString("content", "")?.takeIf { it.isNotBlank() }
                ?: if (choices == null || choices.length() == 0) "Empty response from Perplexity." else "No response."

            val usage = obj.optJSONObject("usage")
            val promptTokens = usage?.optInt("prompt_tokens", 0) ?: 0
            val completionTokens = usage?.optInt("completion_tokens", 0) ?: 0
            val cost = ModelPricing.estimateCost(model, promptTokens, completionTokens)

            val citations = mutableListOf<String>()
            val citationsArr = obj.optJSONArray("citations")
            if (citationsArr != null) {
                for (i in 0 until citationsArr.length()) {
                    citations.add(citationsArr.optString(i))
                }
            }

            val display = buildString {
                append(answer)
                append("\n\n--- Request Info ---\n")
                append("Total tokens: ${promptTokens + completionTokens}\n")
                append("Estimated cost: ${String.format("%.6f", cost)} USD\n")
                if (citations.isNotEmpty()) {
                    append("Citations:\n")
                    citations.forEach { append("- $it\n") }
                }
            }

            PerplexityResult(
                text = display,
                answer = answer,
                citations = citations,
                promptTokens = promptTokens,
                completionTokens = completionTokens,
                costUsd = cost,
                httpCode = httpCode
            )
        } catch (e: JSONException) {
            val err = "Error parsing Perplexity response: ${e.message}"
            PerplexityResult(text = err, httpCode = httpCode, errorMessage = err)
        }
    }

    private fun extractErrorMessage(body: String): String? {
        return try {
            val obj = JSONObject(body)
            obj.optJSONObject("error")?.optString("message")?.takeIf { it.isNotBlank() }
                ?: obj.optString("message").takeIf { it.isNotBlank() }
                ?: obj.optString("detail").takeIf { it.isNotBlank() }
        } catch (e: JSONException) {
            body.takeIf { it.isNotBlank() }
        }
    }
}