package com.jetbrains.chatbrains

import io.ktor.client.statement.HttpResponse

actual suspend fun uploadAudioFile(filePath: String, serverUrl: String, authorizationToken: String): HttpResponse {
    throw Exception()
}