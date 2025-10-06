package com.example.projetorole.network

import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <reified T> getRemote(path: String): T =
    ApiClient.client.get("${ApiClient.BASE_URL}$path").body()

suspend fun <T> safeRemoteCall(
    request: suspend () -> T
): T = withContext(Dispatchers.IO) {
    request()
}