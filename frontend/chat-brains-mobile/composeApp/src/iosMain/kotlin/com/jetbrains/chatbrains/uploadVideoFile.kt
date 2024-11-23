package com.jetbrains.chatbrains

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.posix.memcpy

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import platform.Foundation.*
import kotlinx.coroutines.*
import kotlinx.cinterop.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


@OptIn(InternalAPI::class)
actual suspend fun uploadAudioFile(filePath: String, serverUrl: String, authorizationToken: String): HttpResponse {
    // Initialize Ktor client with Darwin engine for iOS
    val client = HttpClient(Darwin) {
        install(ContentNegotiation) {
            json() // Install JSON serialization
        }
        install(Logging) {
            level = LogLevel.ALL
        }
    }

    // Read file data as NSData
    val fileUrl = NSURL.fileURLWithPath(filePath)
    val fileData = NSData.dataWithContentsOfURL(fileUrl)
        ?: throw IllegalArgumentException("Unable to read file at path: $filePath")

    val base64String = fileData.base64EncodedStringWithOptions(0u)
    val requestBody = buildJsonObject {
        put("file", base64String)
    }

    // Make the HTTP POST request with JSON body
    return client.post(serverUrl) {
        contentType(ContentType.Application.Json)
        setBody(requestBody)
        headers {
            append(HttpHeaders.Authorization, "Bearer $authorizationToken")
            append(HttpHeaders.Accept, "application/json")
        }
    }
}

