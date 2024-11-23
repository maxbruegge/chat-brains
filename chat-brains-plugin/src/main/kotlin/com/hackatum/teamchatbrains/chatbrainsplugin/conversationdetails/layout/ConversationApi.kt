package com.hackatum.teamchatbrains.chatbrainsplugin.api

import com.google.gson.Gson
import com.hackatum.teamchatbrains.chatbrainsplugin.ApiConstants.GET_CONVERSATIONS
import com.hackatum.teamchatbrains.chatbrainsplugin.auth.AuthTokenStorage
import com.hackatum.teamchatbrains.chatbrainsplugin.conversations.Conversation
import okhttp3.*
import java.io.IOException
import javax.swing.SwingUtilities

object ConversationApi {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun deleteConversation(conversationId: String, callback: (Boolean, String?) -> Unit) {
        val token = AuthTokenStorage.bearerToken
        if (token == null) {
            callback(false, "Authorization token missing. Please log in.")
            return
        }

        val request = Request.Builder()
            .url("$GET_CONVERSATIONS/$conversationId")
            .delete()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                SwingUtilities.invokeLater {
                    callback(false, e.message)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val success = response.isSuccessful
                val message = if (success) "Deleted successfully" else response.body?.string()
                SwingUtilities.invokeLater {
                    callback(success, message)
                }
            }
        })
    }

    fun fetchConversationsFromApi(callback: (List<Conversation>) -> Unit) {
        val token = AuthTokenStorage.bearerToken
        if (token == null) {
            SwingUtilities.invokeLater {
                callback(emptyList())
            }
            return
        }

        val request = Request.Builder()
            .url(GET_CONVERSATIONS)
            .get()
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                SwingUtilities.invokeLater {
                    callback(emptyList()) // Return an empty list on failure
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    SwingUtilities.invokeLater {
                        callback(emptyList()) // Return an empty list if the response is not successful
                    }
                    return
                }

                val responseBody = response.body?.string() ?: ""
                val conversations = try {
                    gson.fromJson(responseBody, Array<Conversation>::class.java).toList()
                } catch (e: Exception) {
                    emptyList()
                }

                SwingUtilities.invokeLater {
                    callback(conversations)
                }
            }
        })
    }
}