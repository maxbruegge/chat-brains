package com.jetbrains.chatbrains.networking

import kotlinx.serialization.Serializable

@Serializable
data class BranchesResponse(
    val success: Boolean,
    val branches: Array<String>
)