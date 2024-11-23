package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.filechanges

import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.HTMLHighlighter
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileChange
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileDetails
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import java.awt.Color
import java.awt.Dimension
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JPanel

object ScrollableFileChangesPanel {

    fun create(): JBScrollPane {
        val fileChangesPanel = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }
        return JBScrollPane(fileChangesPanel).apply {
            verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            border = null
            name = "fileChangesPanel"
        }
    }

    fun update(detailsPanel: JPanel, filesWithChanges: List<Pair<FileDetails, List<FileChange>>>): JBPanel<*> {
        val fileChangesPanel = (detailsPanel.getComponent(1) as JBScrollPane).viewport.view as JBPanel<*>
        fileChangesPanel.removeAll()

        filesWithChanges.forEach { (file, changes) ->
            val highlightedContent = HTMLHighlighter.highlightContent(changes)
            val textPane = javax.swing.JTextPane().apply {
                isEditable = false
                contentType = "text/html"
                text = highlightedContent
                size = Dimension(preferredSize.width, Int.MAX_VALUE)
                preferredSize = Dimension(preferredSize.width, getPreferredSize().height)
            }

            val scrollableFilePanel = JBScrollPane(textPane).apply {
                border = javax.swing.BorderFactory.createTitledBorder("Changes in file ${file.filename}")
                verticalScrollBarPolicy = JBScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
                background = JBColor(Color(0x2a2d31), Color(0x2a2d31))
                viewport.background = JBColor(Color(0x2a2d31), Color(0x2a2d31))
                maximumSize = Dimension(Int.MAX_VALUE, textPane.preferredSize.height + 25)
                preferredSize = Dimension(preferredSize.width, textPane.preferredSize.height + 25)
            }

            scrollableFilePanel.putClientProperty("filePath", file.filename)
            scrollableFilePanel.putClientProperty("fileContent", file.content)
            fileChangesPanel.add(scrollableFilePanel)
            fileChangesPanel.add(Box.createVerticalStrut(20))
        }

        fileChangesPanel.revalidate()
        fileChangesPanel.repaint()

        return fileChangesPanel
    }
}