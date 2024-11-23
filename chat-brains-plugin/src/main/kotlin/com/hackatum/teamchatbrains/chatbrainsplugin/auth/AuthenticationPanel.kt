package com.hackatum.teamchatbrains.chatbrainsplugin.auth

import com.intellij.openapi.diagnostic.Logger
import com.intellij.ui.components.JBPanel
import com.google.gson.Gson
import com.hackatum.teamchatbrains.chatbrainsplugin.ApiConstants.SIGN_IN
import com.hackatum.teamchatbrains.chatbrainsplugin.conversations.ConversationPanel
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Dimension
import javax.swing.*

object AuthenticationPanel {

    private val logger = Logger.getInstance(AuthenticationPanel::class.java)
    private val client = OkHttpClient()
    private val gson = Gson()

    fun create(project: Project, cardLayout: CardLayout, cardPanel: JPanel): JPanel {
        val authPanel = JBPanel<JBPanel<*>>(BorderLayout()).apply {
            border = BorderFactory.createEmptyBorder(20, 20, 20, 20)
        }

        val formPanel = JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            border = BorderFactory.createTitledBorder("Login to ChatBrains")
        }

        val emailField = JTextField().apply {
            maximumSize = Dimension(Int.MAX_VALUE, 60)
            border = BorderFactory.createTitledBorder("Email")
        }

        val passwordField = JPasswordField().apply {
            maximumSize = Dimension(Int.MAX_VALUE, 60)
            border = BorderFactory.createTitledBorder("Password")
        }

        val errorLabel = JLabel("").apply {
            foreground = JBColor.RED
        }

        val loginButton = JButton("Login").apply {
            addActionListener {
                val email = emailField.text.trim()
                val password = String(passwordField.password)

                if (email.isBlank() || password.isBlank()) {
                    errorLabel.text = "Email and password cannot be empty."
                    return@addActionListener
                }

                login(project, email, password, cardLayout, cardPanel, errorLabel)
            }
        }

        formPanel.add(Box.createVerticalStrut(10)) // Spacing
        formPanel.add(emailField)
        formPanel.add(Box.createVerticalStrut(10)) // Spacing
        formPanel.add(passwordField)
        formPanel.add(Box.createVerticalStrut(10)) // Spacing
        formPanel.add(loginButton)
        formPanel.add(Box.createVerticalStrut(10)) // Spacing
        formPanel.add(errorLabel)

        authPanel.add(formPanel, BorderLayout.CENTER)
        return authPanel
    }

    private fun login(project: Project, email: String, password: String, cardLayout: CardLayout, cardPanel: JPanel, errorLabel: JLabel) {
        val url = SIGN_IN
        val requestBody = gson.toJson(mapOf("email" to email, "password" to password))

        val request = Request.Builder()
            .url(url)
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()

        // Execute the request in a background thread
        Thread {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    SwingUtilities.invokeLater {
                        errorLabel.text = "Invalid credentials. Please try again."
                    }
                    return@Thread
                }

                val responseBody = response.body?.string() ?: ""
                val jsonResponse = gson.fromJson(responseBody, Map::class.java)

                if (jsonResponse["success"] as? Boolean == true) {
                    val token = jsonResponse["token"] as? String
                    logger.info("User authenticated. Token: $token")

                    // Save the token in the shared storage
                    AuthTokenStorage.bearerToken = token

                    // Refresh the ConversationPanel
                    SwingUtilities.invokeLater {
                        val conversationPanel = ConversationPanel.create(project, cardLayout, cardPanel)
                        cardPanel.add(conversationPanel, "Conversations")
                        cardLayout.show(cardPanel, "Conversations")
                    }
                } else {
                    SwingUtilities.invokeLater {
                        errorLabel.text = "Authentication failed. Please check your details."
                    }
                }
            } catch (e: Exception) {
                SwingUtilities.invokeLater {
                    errorLabel.text = "An error occurred: ${e.message}"
                }
                logger.error("Error during login", e)
            }
        }.start()
    }
}