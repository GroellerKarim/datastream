package eu.groeller.datastreamui.data.datasource

import eu.groeller.datastreamui.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserNetworkDataSource(private val httpClient: HttpClient) {


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


}