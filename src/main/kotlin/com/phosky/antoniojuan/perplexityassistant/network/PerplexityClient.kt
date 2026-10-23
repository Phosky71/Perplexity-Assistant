package com.phosky.antoniojuan.perplexityassistant.network

import com.phosky.antoniojuan.perplexityassistant.settings.PerplexityCostCalculator
import com.intellij.openapi.progress.ProcessCanceledException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class PerplexityResult(
    val text: String,
    val answer: String = "",
    val citations: List<String> = emptyList(),
    val promptTokens: Int = 0,
    val completionTokens: Int = 0,
    val costUsd: Double = 0.0,
    val httpCode: Int? = null,
    val errorMessage: String? = null
) {
    val isSuccess: Boolean get() = errorMessage == null
}

object PerplexityClient {

    const val API_URL = "https://api.perplexity.ai/chat/completions"
    const val MODEL = "sonar-pro"

    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun chat(prompt: String, apiKey: String, basePrompt: String = "", includeSystemPrompt: Boolean = true, baseUrl: String = API_URL): PerplexityResult {
        return try {
            val body = buildPayload(prompt, basePrompt, includeSystemPrompt).toRequestBody("application/json".toMediaType())
            val request = Request.Builder()
                .url(baseUrl)
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(request).execute().use { resp ->
                val respBody = resp.body?.string().orEmpty()
                parseResponse(resp.code, respBody, resp.isSuccessful)
            }
        } catch (e: ProcessCanceledException) {
            throw e
        } catch (e: kotlin.coroutines.cancellation.CancellationException) {
            throw e
        } catch (e: Exception) {
            PerplexityResult(text = "Error calling Perplexity: ${e.message}", errorMessage = "Error calling Perplexity: ${e.message}")
        }
    }

    internal fun buildPayload(prompt: String, basePrompt: String, includeSystemPrompt: Boolean): String {
        val messagesArray = JSONArray()
        if (includeSystemPrompt && basePrompt.isNotBlank()) {
            messagesArray.put(JSONObject().put("role", "system").put("content", basePrompt))
        }
        messagesArray.put(JSONObject().put("role", "user").put("content", prompt))
        val json = JSONObject()
        json.put("model", MODEL)
        json.put("messages", messagesArray)
        return json.toString()
    }

    internal fun parseResponse(httpCode: Int, body: String, isSuccessful: Boolean): PerplexityResult {
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
            val cost = PerplexityCostCalculator.estimateCost(promptTokens, completionTokens)

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