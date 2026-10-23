package com.phosky.antoniojuan.perplexityassistant.network

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PerplexityClientHttpTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun http200_valid() {
        server.enqueue(MockResponse().setBody("""{"choices":[{"message":{"content":"Hi"}}],"usage":{"prompt_tokens":1,"completion_tokens":1}}""").setResponseCode(200))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(r.isSuccess)
    }

    @Test
    fun http401() {
        server.enqueue(MockResponse().setBody("""{"error":{"message":"Unauthorized"}}""").setResponseCode(401))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(!r.isSuccess)
        assertTrue(r.errorMessage!!.contains("401"))
    }

    @Test
    fun http429() {
        server.enqueue(MockResponse().setBody("""{"message":"Rate limit"}""").setResponseCode(429))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(!r.isSuccess)
    }

    @Test
    fun http500() {
        server.enqueue(MockResponse().setBody("boom").setResponseCode(500))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(!r.isSuccess)
    }

    @Test
    fun httpInvalidJson() {
        server.enqueue(MockResponse().setBody("not json").setResponseCode(200))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(!r.isSuccess)
    }

    @Test
    fun httpUsageAbsent() {
        server.enqueue(MockResponse().setBody("""{"choices":[{"message":{"content":"Hi"}}]}""").setResponseCode(200))
        val url = server.url("/").toString()
        val r = PerplexityClient.chat("p", "key", "", true, url)
        assertTrue(r.isSuccess)
        assertTrue(r.costUsd == 0.0)
    }
}