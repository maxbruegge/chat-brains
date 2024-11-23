package com.hackatum.teamchatbrains.chatbrainsplugin

import com.hackatum.teamchatbrains.chatbrainsplugin.auth.AuthenticationPanel
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.DetailsPanel
import com.hackatum.teamchatbrains.chatbrainsplugin.conversations.ConversationPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBPanel
import java.awt.BorderLayout
import java.awt.CardLayout
import javax.swing.JPanel

class ChatBrainsToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val mainPanel = JBPanel<JBPanel<*>>(BorderLayout())
        val cardLayout = CardLayout()
        val cardPanel = JPanel(cardLayout)

        // Initialize panels
        val authPanel = AuthenticationPanel.create(project, cardLayout, cardPanel)
        val conversationPanel = ConversationPanel.create(project, cardLayout, cardPanel)
        val detailsPanel = DetailsPanel.create(project, cardLayout, cardPanel)

        // Add panels to CardLayout
        cardPanel.add(authPanel, "Authentication")
        cardPanel.add(conversationPanel, "Conversations")
        cardPanel.add(detailsPanel, "Details")

        mainPanel.add(cardPanel, BorderLayout.CENTER)

        // Add the main panel to the tool window
        val contentFactory = com.intellij.ui.content.ContentFactory.getInstance()
        val content = contentFactory.createContent(mainPanel, "", false)
        toolWindow.contentManager.addContent(content)

        // Show the authentication screen initially
        cardLayout.show(cardPanel, "Authentication")
    }
}