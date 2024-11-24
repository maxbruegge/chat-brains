package com.jetbrains.chatbrains

actual suspend fun playAudio(base64String: String) {
    throw Exception()
}
