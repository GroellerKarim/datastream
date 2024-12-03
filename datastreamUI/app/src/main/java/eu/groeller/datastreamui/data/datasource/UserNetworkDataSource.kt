package eu.groeller.datastreamui.data.datasource

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import eu.groeller.datastreamui.User
import eu.groeller.datastreamui.data.user.UserState
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserNetworkDataSource(private val httpClient: HttpClient) {

    private lateinit var user: User
    private data class RegisterRequest(val username: String, val email: String, val password: String)

    suspend fun registerUser(username: String, email: String, password: String) {
        val response = httpClient.post("/users/regiser") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(username, email, password))
        }
        // TODO Handle Exceptions
    }

    private data class LoginRequest(val email: String, val password: String)

    suspend fun loginUser(email: String, password: String): User {

        val response = httpClient.post("/users/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }

        if (response.status == HttpStatusCode.OK) {
            // TODO Handle NoTransformationFoundException
            return response.body()
        }
        // TODO, handle if backend not available, user with email does not exist or wrong password
        throw Error()
    }

    private data class FetchUserWithTokenResponse(val username: String, val email: String)
    suspend fun fetchUserWithToken(token: String): UserState {
        if(this::user.isInitialized) return UserState.Success(user)
        val response = httpClient.get("/users") {
            bearerAuth(token)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val body: FetchUserWithTokenResponse = response.body()
                this.user = User(body.username, body.email, token)
                return UserState.Success(user)
            }
            else -> return UserState.NetworkFailed(response.body())
        }
    }

}