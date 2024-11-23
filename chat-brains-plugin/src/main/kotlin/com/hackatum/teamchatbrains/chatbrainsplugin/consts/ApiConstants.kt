package com.hackatum.teamchatbrains.chatbrainsplugin

object ApiConstants {
    const val BASE_URL = "http://localhost:8000/api"
    const val SIGN_IN = "$BASE_URL/user/sign-in"
    const val GET_CONVERSATIONS = "$BASE_URL/conversation"
    const val GET_CONVERSATION_DETAILS = "$BASE_URL/conversation?id="
}