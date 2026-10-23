package com.phosky.antoniojuan.perplexityassistant.ui

import org.junit.Assert.assertTrue
import org.junit.Test

class MarkdownRenderTest {
    @Test
    fun toHtml_escapesAndFormats() {
        val html = MarkdownRenderer.toHtml("**bold** and `code`")
        assertTrue(html.contains("<b>bold</b>"))
        assertTrue(html.contains("<code>code</code>"))
    }

    @Test
    fun escapeHtml_sanitizes() {
        val out = MarkdownRenderer.escapeHtml("<script>")
        assertTrue(out.contains("&lt;script&gt;"))
    }
}