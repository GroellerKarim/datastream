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

    private data class LoginRequest(private val email: String, private val password: String)

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