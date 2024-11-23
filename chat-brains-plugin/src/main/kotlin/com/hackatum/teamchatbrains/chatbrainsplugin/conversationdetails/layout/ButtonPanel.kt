package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.layout

import com.hackatum.teamchatbrains.chatbrainsplugin.api.ConversationApi
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.FileContentHandler
import com.hackatum.teamchatbrains.chatbrainsplugin.conversations.Conversation
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBPanel
import java.awt.CardLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel
import com.intellij.openapi.ui.Messages

object ButtonPanel {

    private var conversationId: String? = null
    private var fileChangesPanel: JBPanel<*>? = null

    fun setConversationId(id: String) {
        conversationId = id
    }

    fun setFileChangesPanel(panel: JBPanel<*>) {
        fileChangesPanel = panel
    }

    fun create(
        project: Project,
        cardLayout: CardLayout,
        cardPanel: JPanel
    ): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            border = BorderFactory.createEmptyBorder(10, 0, 0, 0) // Padding above the buttons

            add(JButton("Accept").apply {
                addActionListener {
                    fileChangesPanel?.let { it1 -> FileContentHandler.applyFileChanges(project, it1) }
                    cardLayout.show(cardPanel, "Conversations") // Navigate back to Conversations
                }
                font = font.deriveFont(14f)
                preferredSize = Dimension(100, 30)
            })
            add(javax.swing.Box.createHorizontalStrut(10)) // Space between buttons
            add(JButton("Decline").apply {
                addActionListener {
                    conversationId?.let { id ->
                        // Run the delete method and navigate back to Conversations
                        ConversationApi.deleteConversation(id) { success, message ->
                            if (success) {
                                Messages.showInfoMessage("Conversation declined and deleted successfully.", "Conversation Deleted")
                                cardLayout.show(cardPanel, "Conversations")
                            } else {
                                Messages.showErrorDialog("Failed to delete conversation: $message", "Deletion Error")
                            }
                        }
                    } ?: run {
                        // Handle case where conversation ID is not set
                        Messages.showErrorDialog(
                            "Conversation ID is not set. Cannot perform delete operation.",
                            "Deletion Error"
                        )
                    }
                }
                font = font.deriveFont(14f)
                preferredSize = Dimension(100, 30)
            })
        }
    }
}