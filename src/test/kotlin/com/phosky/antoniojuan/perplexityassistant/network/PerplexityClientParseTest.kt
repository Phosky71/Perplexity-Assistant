package com.phosky.antoniojuan.perplexityassistant.network

import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PerplexityClientParseTest {

    @Test
    fun parse_validResponse() {
        val body = """
            {"choices":[{"message":{"content":"Hello"}}],"usage":{"prompt_tokens":10,"completion_tokens":5},"citations":["http://x"]}
        """.trimIndent()
        val r = PerplexityClient.parseResponse(200, body, true)
        assertTrue(r.isSuccess)
        assertTrue(r.text.contains("Hello"))
        assertTrue(r.text.contains("Citations"))
        assertEquals(10, r.promptTokens)
        assertEquals(5, r.completionTokens)
    }

    @Test
    fun parse_emptyChoices() {
        val body = """{"choices":[]}"""
        val r = PerplexityClient.parseResponse(200, body, true)
        assertTrue(r.isSuccess)
        assertTrue(r.text.contains("Empty response"))
    }

    @Test
    fun parse_http401() {
        val body = """{"error":{"message":"Unauthorized"}}"""
        val r = PerplexityClient.parseResponse(401, body, false)
        assertTrue(!r.isSuccess)
        assertTrue(r.errorMessage!!.contains("401"))
        assertTrue(r.errorMessage!!.contains("Unauthorized"))
    }

    @Test
    fun parse_http429() {
        val body = """{"message":"Rate limit exceeded"}"""
        val r = PerplexityClient.parseResponse(429, body, false)
        assertTrue(!r.isSuccess)
        assertTrue(r.errorMessage!!.contains("429"))
    }

    @Test
    fun parse_http500() {
        val body = "Internal Server Error"
        val r = PerplexityClient.parseResponse(500, body, false)
        assertTrue(!r.isSuccess)
        assertTrue(r.errorMessage!!.contains("500"))
    }

    @Test
    fun parse_invalidJson() {
        val r = PerplexityClient.parseResponse(200, "not json", true)
        assertTrue(!r.isSuccess)
        assertTrue(r.errorMessage!!.contains("parsing"))
    }

    @Test
    fun parse_usageAbsent() {
        val body = """{"choices":[{"message":{"content":"Hi"}}]}"""
        val r = PerplexityClient.parseResponse(200, body, true)
        assertTrue(r.isSuccess)
        assertEquals(0, r.promptTokens)
        assertEquals(0.0, r.costUsd, 0.0001)
    }
}