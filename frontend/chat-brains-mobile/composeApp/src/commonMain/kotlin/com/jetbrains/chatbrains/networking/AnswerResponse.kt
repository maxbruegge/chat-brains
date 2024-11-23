package com.jetbrains.chatbrains.networking

@kotlinx.serialization.Serializable
data class AnswerResponse(val success: Boolean, val answer: String)