package eu.groeller.datastreamui.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import java.net.InetSocketAddress
import java.net.Proxy

class HttpClientConfigurer {

    val port = 7974

    val v1HttpClient: HttpClient = HttpClient() {
        engine {
            proxy = ProxyBuilder.http("84.115.221.183:$port/api/v1")
        }
    }

}