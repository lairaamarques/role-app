package com.example.projetorole.network

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.content.PartData
import io.ktor.client.request.forms.*
import io.ktor.http.contentType
import io.ktor.utils.io.core.use
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend inline fun <reified T> getRemote(path: String): T =
    ApiClient.client.get("${ApiClient.BASE_URL}$path").body()

suspend fun <T> safeRemoteCall(
    request: suspend () -> T
): T = withContext(Dispatchers.IO) {
    request()
}

suspend inline fun <reified T> postRemote(path: String, body: Any): T =
    ApiClient.client.post("${ApiClient.BASE_URL}$path") {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

suspend inline fun <reified T> putRemote(path: String, body: Any): T =
    ApiClient.client.put("${ApiClient.BASE_URL}$path") {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

suspend inline fun <reified T> deleteRemote(path: String): T =
    ApiClient.client.delete("${ApiClient.BASE_URL}$path").body()

suspend inline fun <reified T> postMultipart(path: String, formParts: List<PartData>): T =
    ApiClient.client.post("${ApiClient.BASE_URL}$path") {
        setBody(MultiPartFormDataContent(formData {
            formParts.forEach { part ->
                when (part) {
                    is PartData.FormItem -> append(part.name ?: "", part.value)
                    is PartData.FileItem -> {
                    }
                    else -> {
                    }
                }
            }
        }))
    }.body()