package xyz.fartmap.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class HTTP {
    private val client = OkHttpClient()
    private val json = "application/json".toMediaType()
    private val api = "https://api.fartmap.xyz"

    /**
     * Log in a user.
     * @param username the username
     * @param password the password
     * @return the user's token
     */
    fun login(username: String, password: String): String {
        val requestBody = buildJsonObject {
            put("username", JsonPrimitive(username))
            put("password", JsonPrimitive(password))
        }.toString().toRequestBody(json)

        val request = Request.Builder()
            .url("$api/auth/sign-in")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute().use {
            // TODO: handle errors
            it.body?.string() ?: ""
        }

        val json = Json.parseToJsonElement(response)
        return json.jsonObject["token"]?.jsonPrimitive?.content ?: ""
    }

    /**
     * Sign up a new user.
     * @param username the username
     * @param password the password
     * @return true if the user was successfully signed up, false otherwise
     */
    fun signup(username: String, password: String): Boolean {
        val requestBody = buildJsonObject {
            put("username", JsonPrimitive(username))
            put("password", JsonPrimitive(password))
        }.toString().toRequestBody(json)

        val request = Request.Builder()
            .url("$api/auth/sign-up")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute().use {
            // TODO: handle errors
            it.body?.string() ?: ""
        }

        val json = try {
            Json.parseToJsonElement(response)
        } catch (e: Exception) {
            println("Failed to parse JSON response: ${e.message}")
            return false
        }

        if (json is JsonObject) {
            return json["success"]?.jsonPrimitive?.booleanOrNull ?: false
        } else {
            println("Received non-JSON object: $json")
            return false
        }
    }
}