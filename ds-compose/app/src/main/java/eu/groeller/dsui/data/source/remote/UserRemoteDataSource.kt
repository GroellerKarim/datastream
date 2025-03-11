package eu.groeller.dsui.data.source.remote

import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.V1_PATH
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.Serializable

/**
 * Remote data source for user-related API operations.
 * This handles all network communication with the user endpoints.
 */
class UserRemoteDataSource(
    private val httpClient: HttpClient
) {
    @Serializable
    private data class RegisterRequest(val username: String, val email: String, val password: String)
    
    /**
     * Registers a new user.
     * 
     * @param username The username
     * @param email The email address
     * @param password The password
     * @return The registered user
     */
    suspend fun registerUser(username: String, email: String, password: String): User {
        val response = httpClient.post("$V1_PATH/users/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, email, password))
        }
        
        if (!response.status.isSuccess()) {
            throw Exception("Failed to register user: ${response.status}")
        }
        
        return response.body()
    }
    
    @Serializable
    private data class LoginRequest(val email: String, val password: String)
    
    /**
     * Logs in a user with email and password.
     * 
     * @param email The email address
     * @param password The password
     * @return The logged-in user with token
     */
    suspend fun loginUser(email: String, password: String): User {
        val response = httpClient.post("$V1_PATH/users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to login user: ${response.status}")
        }
        
        return response.body()
    }
    
    @Serializable
    private data class TokenValidationResponse(val username: String, val email: String)
    
    /**
     * Validates a user token.
     * 
     * @param token The authentication token to validate
     * @return The user if token is valid
     */
    suspend fun validateToken(token: String): User {
        val response = httpClient.get("$V1_PATH/users/token") {
            bearerAuth(token)
        }
        
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Failed to validate token: ${response.status}")
        }
        
        val validationResponse: TokenValidationResponse = response.body()
        return User(validationResponse.username, validationResponse.email, token)
    }
} 