package dev.ishubhamsingh.splashy

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun getPlatform() : String = "iOS"

actual fun getHttpClient(): HttpClient {
    val httpClient =
        HttpClient(Darwin) {
            engine {
                configureRequest {
                    setTimeoutInterval(60.0)
                    setAllowsCellularAccess(true)
                }
            }
        }

    return  httpClient
}