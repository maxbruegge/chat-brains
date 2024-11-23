package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import javax.swing.*
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileChange
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileDetails

object DetailsPanel {

    fun create(project: Project, cardLayout: CardLayout, cardPanel: JPanel): JPanel {
        val detailsPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding without visible border
        }

        val titlePanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        val summaryLabel = JBLabel("").apply {
            border = BorderFactory.createEmptyBorder(0, 0, 10, 0) // Space between title and summary
        }

        val summaryText = JBLabel("").apply {
            border = BorderFactory.createEmptyBorder(0, 0, 10, 0) // Space between summary and file changes
        }

        val fileChangesTitle = JBLabel("").apply {
            border = BorderFactory.createEmptyBorder(10, 0, 0, 0) // Space between summary and file changes
        }

        val fileChangesPanel = JBPanel<JBPanel<*>>().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
        }

        val scrollableFileChangesPanel = JBScrollPane(fileChangesPanel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
            border = null
        }

        titlePanel.add(summaryLabel)
        titlePanel.add(summaryText)
        titlePanel.add(fileChangesTitle)

        val buttonPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(JButton("Accept").apply {
                addActionListener {
                    FileContentHandler.applyFileChanges(project, fileChangesPanel)
                    cardLayout.show(cardPanel, "Conversations")
                }
                font = font.deriveFont(14f)
                preferredSize = Dimension(100, 30)
            })
            add(Box.createHorizontalStrut(10))
            add(JButton("Decline").apply {
                addActionListener {
                    cardLayout.show(cardPanel, "Conversations")
                }
                font = font.deriveFont(14f)
                preferredSize = Dimension(100, 30)
            })
        }

        detailsPanel.add(titlePanel, BorderLayout.NORTH)
        detailsPanel.add(scrollableFileChangesPanel, BorderLayout.CENTER)
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH)

        return detailsPanel
    }

    fun fetchAndShowDetails(
        project: Project,
        cardLayout: CardLayout,
        cardPanel: JPanel,
        conversationId: String
    ) {
        DetailsFetcher.fetchDetails(conversationId) { success, details ->
            if (success && details != null) {
                SwingUtilities.invokeLater {
                    val filesWithChanges = details.files.map { file ->
                        val localContent = FileContentHandler.getLocalFileContent(project, file.filename) ?: ""
                        val changes = FileContentComparator.computeFileChanges(localContent, file.content)
                        file to changes
                    }
                    showDetails(
                        cardPanel,
                        cardLayout,
                        details.title,
                        "This is a hardcoded summary for the conversation.",
                        filesWithChanges
                    )
                }
            } else {
                SwingUtilities.invokeLater {
                    Messages.showErrorDialog("Failed to fetch conversation details", "Error")
                }
            }
        }
    }

    private fun showDetails(
        cardPanel: JPanel,
        cardLayout: CardLayout,
        title: String,
        summary: String,
        filesWithChanges: List<Pair<FileDetails, List<FileChange>>>
    ) {
        val detailsPanel = cardPanel.getComponent(2) as JBPanel<*>
        val titlePanel = detailsPanel.getComponent(0) as JPanel
        val fileChangesPanel = (detailsPanel.getComponent(1) as JBScrollPane).viewport.view as JBPanel<*>

        val summaryLabel = titlePanel.getComponent(0) as JBLabel
        val summaryText = titlePanel.getComponent(1) as JBLabel
        val fileChangesTitle = titlePanel.getComponent(2) as JBLabel

        summaryLabel.text = "<html><b>Conversation:</b> $title</html>"
        summaryText.text = "<html><b>Summary:</b><i>$summary</i></html>"
        fileChangesTitle.text = "<html><b>File changes:</b></html>"

        fileChangesPanel.removeAll()
        filesWithChanges.forEach { (file, changes) ->
            val highlightedContent = HTMLHighlighter.highlightContent(changes)

            val textPane = JTextPane().apply {
                isEditable = false
                contentType = "text/html"
                text = highlightedContent

                size = Dimension(preferredSize.width, Int.MAX_VALUE)
                preferredSize = Dimension(preferredSize.width, getPreferredSize().height)
            }

            val scrollableFilePanel = JBScrollPane(textPane).apply {
                border = BorderFactory.createTitledBorder("Changes in file ${file.filename}")
                verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
                horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER

                val contentHeight = textPane.preferredSize.height
                maximumSize = Dimension(Int.MAX_VALUE, contentHeight + 25)
                preferredSize = Dimension(preferredSize.width, contentHeight + 25)
            }

            scrollableFilePanel.putClientProperty("filePath", file.filename)
            scrollableFilePanel.putClientProperty("fileContent", file.content)

            fileChangesPanel.add(Box.createVerticalStrut(20)) // Space between file changes
            fileChangesPanel.add(scrollableFilePanel)
        }

        fileChangesPanel.revalidate()
        fileChangesPanel.repaint()
        cardLayout.show(cardPanel, "Details")
    }
}