package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.layout

import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.DetailsFetcher
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.FileContentComparator
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.FileContentHandler
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.filechanges.ScrollableFileChangesPanel
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileChange
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileDetails
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.*

object DetailsPanel {

    fun create(project: Project, cardLayout: CardLayout, cardPanel: JPanel) {
        val detailsPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(0, 10, 0, 10)
        }

        val titlePanel = TitlePanel.create()
        val scrollableFileChangesPanel = ScrollableFileChangesPanel.create()
        val buttonPanel = ButtonPanel.create(
            project, cardLayout, cardPanel
        )

        detailsPanel.add(titlePanel, BorderLayout.NORTH)
        detailsPanel.add(scrollableFileChangesPanel, BorderLayout.CENTER)
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH)
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
                        filesWithChanges,
                        conversationId
                    )
                }
            } else {
                SwingUtilities.invokeLater {
                    JOptionPane.showMessageDialog(
                        null,
                        "Failed to fetch conversation details",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
    }

    private fun showDetails(
        cardPanel: JPanel,
        cardLayout: CardLayout,
        title: String,
        summary: String,
        filesWithChanges: List<Pair<FileDetails, List<FileChange>>>,
        conversationId: String
    ) {
        val detailsPanel = cardPanel.getComponent(2) as JBPanel<*>
        TitlePanel.update(detailsPanel, title, summary)
        ButtonPanel.setConversationId(conversationId)
        val fileChangesPanel = ScrollableFileChangesPanel.update(detailsPanel, filesWithChanges)
        ButtonPanel.setFileChangesPanel(fileChangesPanel)
        cardLayout.show(cardPanel, "Details")
    }
}