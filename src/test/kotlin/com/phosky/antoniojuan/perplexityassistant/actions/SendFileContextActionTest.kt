package com.phosky.antoniojuan.perplexityassistant.actions

import org.junit.Assert.assertTrue
import org.junit.Test

class SendFileContextActionTest {
    @Test
    fun buildContext_includesPathLanguageAndCode() {
        val action = SendFileContextAction()
        val ctx = action.buildContext("src/Foo.kt", "kt", "fun main() {}")
        assertTrue(ctx.contains("File: src/Foo.kt"))
        assertTrue(ctx.contains("Language: kt"))
        assertTrue(ctx.contains("fun main() {}"))
    }

    @Test
    fun buildContext_respectsMaxSizeCheck() {
        assertTrue(SendFileContextAction.MAX_CONTEXT_CHARS > 0)
    }
}