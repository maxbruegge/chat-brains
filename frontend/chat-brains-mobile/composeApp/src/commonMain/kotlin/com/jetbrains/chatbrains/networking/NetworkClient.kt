package com.jetbrains.chatbrains.networking

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import util.NetworkError
import util.Result

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
}