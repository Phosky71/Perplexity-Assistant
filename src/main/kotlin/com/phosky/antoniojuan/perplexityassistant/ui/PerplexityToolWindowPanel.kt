package com.phosky.antoniojuan.perplexityassistant.ui

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import java.awt.BorderLayout
import javax.swing.*

class PerplexityToolWindowPanel(private val project: Project) : JPanel(BorderLayout()) {

    private val responseArea = JTextArea()
    private val extraPromptField = JTextField()
    private val insertButton = JButton("Insertar en cursor")
    private val replaceButton = JButton("Reemplazar selección")

    init {
        // Panel superior: prompt adicional
        val top = JPanel(BorderLayout())
        top.add(JLabel("Prompt adicional:"), BorderLayout.WEST)
        top.add(extraPromptField, BorderLayout.CENTER)

        // Área para botones de inserción
        val buttons = JPanel()
        buttons.add(insertButton)
        buttons.add(replaceButton)

        responseArea.isEditable = false

        add(top, BorderLayout.NORTH)
        add(JScrollPane(responseArea), BorderLayout.CENTER)
        add(buttons, BorderLayout.SOUTH)

        // Acción para insertar el texto en el cursor
        insertButton.addActionListener { insertAtCursor(false) }
        // Acción para reemplazar la selección por el texto de la respuesta
        replaceButton.addActionListener { insertAtCursor(true) }
    }

    fun setResponseText(text: String) {
        responseArea.text = text
    }

    fun getExtraPrompt(): String = extraPromptField.text.orEmpty()

    fun appendToResponse(text: String) {
        responseArea.append(text)
    }

    private fun insertAtCursor(replaceSelection: Boolean) {
        val editor = EditorFactory.getInstance().allEditors
            .firstOrNull { it.project == project } ?: return

        val document = editor.document
        val selectionModel = editor.selectionModel
        val text = responseArea.text

        WriteCommandAction.runWriteCommandAction(project) {
            if (replaceSelection && selectionModel.hasSelection()) {
                document.replaceString(selectionModel.selectionStart, selectionModel.selectionEnd, text)
            } else {
                val caretOffset = editor.caretModel.offset
                document.insertString(caretOffset, text)
            }
        }
    }
}
