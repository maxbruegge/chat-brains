package com.jetbrains.chatbrains.networking

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponse(
    val success: Boolean,
    val token: String
)