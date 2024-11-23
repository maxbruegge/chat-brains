package com.jetbrains.chatbrains

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform