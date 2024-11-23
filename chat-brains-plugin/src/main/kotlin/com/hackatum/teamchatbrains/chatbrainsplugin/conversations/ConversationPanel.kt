package com.hackatum.teamchatbrains.chatbrainsplugin.conversations

import com.google.gson.Gson
import com.hackatum.teamchatbrains.chatbrainsplugin.auth.AuthTokenStorage
import com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.DetailsPanel
import com.intellij.openapi.project.Project
import com.hackatum.teamchatbrains.chatbrainsplugin.ApiConstants.GET_CONVERSATIONS
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import com.intellij.util.ui.JBUI
import okhttp3.*
import java.awt.CardLayout
import java.awt.Dimension
import java.io.IOException
import javax.swing.*

object ConversationPanel {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun create(project: Project, cardLayout: CardLayout, cardPanel: JPanel): JBScrollPane {
        val conversationTilesPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Choose a Conversation")
        }

        val scrollableConversationsPanel = JBScrollPane(conversationTilesPanel).apply {
            verticalScrollBarPolicy = JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
            horizontalScrollBarPolicy = JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        }

        // Fetch conversations from the API
        fetchConversations { conversations ->
            SwingUtilities.invokeLater {
                conversationTilesPanel.removeAll()
                conversations.forEach { conversation ->
                    val button = JButton(conversation.title).apply {
                        addActionListener {
                            DetailsPanel.fetchAndShowDetails(project, cardLayout, cardPanel, conversation._id)
                        }
                        font = font.deriveFont(14f)
                        maximumSize = Dimension(Int.MAX_VALUE, 40)
                        horizontalAlignment = SwingConstants.LEFT
                        margin = JBUI.insetsLeft(10)
                    }
                    conversationTilesPanel.add(Box.createVerticalStrut(10))
                    conversationTilesPanel.add(button)
                }
                conversationTilesPanel.revalidate()
                conversationTilesPanel.repaint()
            }
        }

        return scrollableConversationsPanel
    }

    private fun fetchConversations(onSuccess: (List<Conversation>) -> Unit) {
        val token = AuthTokenStorage.bearerToken ?: return

        val request = Request.Builder()
            .url(GET_CONVERSATIONS)
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                SwingUtilities.invokeLater {
                    Messages.showErrorDialog("Failed to fetch conversations: ${e.message}", "Error")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    SwingUtilities.invokeLater {
                        Messages.showErrorDialog("Error fetching conversations: ${response.message}", "Error")
                    }
                    return
                }
                val responseBody = response.body?.string() ?: ""
                val conversationsResponse = gson.fromJson(responseBody, ConversationsResponse::class.java)
                if (conversationsResponse.success) {
                    onSuccess(conversationsResponse.data)
                } else {
                    SwingUtilities.invokeLater {
                        Messages.showErrorDialog("Failed to fetch conversations", "Error")
                    }
                }
            }
        })
    }
}

// Data classes for API response mapping
data class ConversationsResponse(val success: Boolean, val data: List<Conversation>)
data class Conversation(val _id: String, val title: String)