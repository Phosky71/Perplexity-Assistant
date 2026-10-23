package com.phosky.antoniojuan.perplexityassistant.settings

import org.junit.Assert.assertTrue
import org.junit.Test

class QuickActionPromptsTest {
    @Test
    fun defaultPromptsContainCodePlaceholder() {
        val prompts = PerplexitySettingsState.defaultQuickActionPrompts()
        assertTrue(prompts.size == 5)
        prompts.values.forEach { assertTrue(it.contains("{code}")) }
    }
}