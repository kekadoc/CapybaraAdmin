package com.kekadoc.project.capybara.admin.data.source.remote.api

import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.client.statement.HttpResponse

open class HttpException(response: HttpResponse) : RuntimeException("Response with status ${response.status} $response")

class UnauthorizedException(response: HttpResponse) : HttpException(response)

suspend inline fun <reified T> HttpResponse.bodyOrError(): T {
    return when (status.value) {
        HttpStatusCode.OK.value -> body<T>()
        HttpStatusCode.Unauthorized.value -> throw UnauthorizedException(this)
        else -> throw HttpException(this)
    }
}