package com.jetbrains.chatbrains.networking

import com.jetbrains.chatbrains.getFileBytes
import com.jetbrains.chatbrains.uploadAudioFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import util.NetworkError
import util.Result
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

var authToken: String? = null

class NetworkClient(
    private val httpClient: HttpClient
) {
    suspend fun login(email: String, password: String): Result<String, NetworkError> {
        val requestBody = buildJsonObject {
            put("email", email)
            put("password", password)
        }
        val response = try {
            httpClient.post(
                urlString = "http://localhost:8000/api/user/sign-in"
            ) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
        } catch(e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> {
                val signInResponse = response.body<SignInResponse>()
                if (signInResponse.success) {
                    authToken = signInResponse.token
                    Result.Success(signInResponse.token)
                } else {
                    Result.Error(NetworkError.UNAUTHORIZED)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun conversations(): Result<Array<String>, NetworkError> {
        val response = try {
            httpClient.get(
                urlString = "http://localhost:8000/api/conversation"
            ) { }
        } catch(e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch(e: SerializationException) {
            return Result.Error(NetworkError.SERIALIZATION)
        }

        return when(response.status.value) {
            in 200..299 -> {
                val conversationResponse = response.body<ConversationResponse>()
                if (conversationResponse.success) {
                    Result.Success(conversationResponse.data)
                } else {
                    Result.Error(NetworkError.UNAUTHORIZED)
                }
            }
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            409 -> Result.Error(NetworkError.CONFLICT)
            408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }

    suspend fun answer(filePath: String): Result<Unit, NetworkError> {

        val response: HttpResponse;
        try {
            response = uploadAudioFile(filePath, "http://localhost:8000/api/ai", authToken ?: "")
        } catch (e: UnresolvedAddressException) {
            return Result.Error(NetworkError.NO_INTERNET)
        } catch (e: Exception) {
            println(e.message)
            return Result.Error(NetworkError.UNKNOWN)
        }

        return when (response.status.value) {
            in 200..299 -> Result.Success(Unit)
            401 -> Result.Error(NetworkError.UNAUTHORIZED)
            413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
            in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
            else -> Result.Error(NetworkError.UNKNOWN)
        }
    }
}