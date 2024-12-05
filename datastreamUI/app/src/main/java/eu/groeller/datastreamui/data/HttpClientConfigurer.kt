package eu.groeller.datastreamui.data

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

val V1_PATH = "/api/v1"
class HttpClientConfigurer {

    val port = 8080
    private val baseUrl = "http://192.168.0.81:$port"

    val v1HttpClient: HttpClient = HttpClient() {
        install(ContentNegotiation) {
            json()
        }
        install(DefaultRequest) {
            url(baseUrl)
        }
    }

}