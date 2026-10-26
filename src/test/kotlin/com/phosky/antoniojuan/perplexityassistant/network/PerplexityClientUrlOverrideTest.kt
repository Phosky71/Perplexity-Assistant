package com.phosky.antoniojuan.perplexityassistant.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PerplexityClientUrlOverrideTest {

    @Test
    fun defaultUrl_whenNoProperty() {
        // Ensure no system property is set
        System.clearProperty("perplexity.api.url")
        val url = PerplexityClient.resolveApiUrl()
        assertEquals(PerplexityClient.API_URL, url)
    }

    @Test
    fun validLocalhostOverride() {
        System.setProperty("perplexity.api.url", "http://localhost:8765/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals("http://localhost:8765/chat/completions", url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun valid127Override() {
        System.setProperty("perplexity.api.url", "https://127.0.0.1:8443/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals("https://127.0.0.1:8443/chat/completions", url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedExternalHost() {
        System.setProperty("perplexity.api.url", "https://evil.com/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedDifferentIp() {
        System.setProperty("perplexity.api.url", "http://192.168.1.100:8765/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedUnsupportedProtocol() {
        System.setProperty("perplexity.api.url", "ftp://localhost:8765/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedMalformedUrl() {
        System.setProperty("perplexity.api.url", "not-a-url")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun emptyPropertyUsesDefault() {
        System.setProperty("perplexity.api.url", "")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedLocalhostEvilHostname() {
        // URL.parse(host) == "localhost" but the hostname is localhost.evil.com
        System.setProperty("perplexity.api.url", "http://localhost.evil.com/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }

    @Test
    fun rejectedLocalhostAtSignAuthInfo() {
        // "localhost@evil.com" parses host as "evil.com", not localhost
        System.setProperty("perplexity.api.url", "http://localhost@evil.com/chat/completions")
        try {
            val url = PerplexityClient.resolveApiUrl()
            assertEquals(PerplexityClient.API_URL, url)
        } finally {
            System.clearProperty("perplexity.api.url")
        }
    }
}