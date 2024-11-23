package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails

import com.google.gson.Gson
import com.hackatum.teamchatbrains.chatbrainsplugin.auth.AuthTokenStorage
import com.hackatum.teamchatbrains.chatbrainsplugin.models.ConversationDetails
import com.hackatum.teamchatbrains.chatbrainsplugin.models.ConversationDetailsResponse
import com.hackatum.teamchatbrains.chatbrainsplugin.ApiConstants.GET_CONVERSATION_DETAILS
import okhttp3.*
import java.io.IOException

object DetailsFetcher {
    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchDetails(conversationId: String, callback: (Boolean, ConversationDetails?) -> Unit) {
        val token = AuthTokenStorage.bearerToken
        if (token == null) {
            callback(false, null)
            return
        }

        val request = Request.Builder()
            .url("$GET_CONVERSATION_DETAILS$conversationId")
            .addHeader("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback(false, null)
                    return
                }
                val responseBody = response.body?.string() ?: ""
                val detailsResponse = gson.fromJson(responseBody, ConversationDetailsResponse::class.java)
                callback(detailsResponse.success, detailsResponse.data)
            }
        })
    }
}