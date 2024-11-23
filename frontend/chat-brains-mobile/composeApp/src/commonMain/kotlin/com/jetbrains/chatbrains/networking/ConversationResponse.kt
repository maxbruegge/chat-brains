package com.jetbrains.chatbrains.networking

import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    val success: Boolean,
    val data: Array<String>
)