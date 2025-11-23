package com.phosky.antoniojuan.perplexityassistant.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class PerplexityToolWindowFactory : ToolWindowFactory, DumbAware {
    companion object {
        var instance: PerplexityToolWindowPanel? = null
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = PerplexityToolWindowPanel(project)
        instance = panel
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
}
