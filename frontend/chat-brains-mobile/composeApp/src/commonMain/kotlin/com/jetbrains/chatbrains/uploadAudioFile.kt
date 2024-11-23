package com.jetbrains.chatbrains

import io.ktor.client.statement.HttpResponse

expect suspend fun uploadAudioFile(
    filePath: String,
    serverUrl: String,
    authorizationToken: String
): HttpResponse