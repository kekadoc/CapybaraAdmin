package com.kekadoc.project.capybara.admin.data.source.remote.api.auth

import com.kekadoc.project.capybara.admin.data.source.remote.api.bodyOrError
import com.kekadoc.project.capybara.admin.data.source.remote.model.auth.*
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.Tokens
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationRequestInfo
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationStatus
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthRemoteDataSourceImpl(
    private val client: HttpClient
) : AuthRemoteDataSource {

    override suspend fun login(login: String, password: String): Tokens {
        return client.post("auth") {
            contentType(ContentType.Application.Json)
            setBody(AuthorizationRequest(login, password))
            this.method = HttpMethod.Post
        }.bodyOrError<AuthorizationResponse>().let { response ->
            Tokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
            )
        }
    }

    override suspend fun registrationRequests(): List<RegistrationRequestInfo> {
        return client.get("auth/registration/all")
            .bodyOrError<GetAllRegistrationRequestsResponseDto>()
            .let {
                it.items.map { dto ->
                    RegistrationRequestInfo(
                        id = dto.id,
                        status = RegistrationStatus.valueOf(dto.status),
                        name = dto.name,
                        surname = dto.surname,
                        patronymic = dto.patronymic,
                        email = dto.email,
                        isStudent = dto.isStudent,
                        groupId = dto.groupId,
                    )
                }
        }
    }

    override suspend fun confirmRegistration(regId: Identifier) {
        client.patch("auth/registration/${regId}") {
            contentType(ContentType.Application.Json)
            setBody(UpdateRegistrationStatusRequestDto(status = RegistrationStatusDto.COMPLETED))
        }
    }

    override suspend fun rejectRegistration(regId: Identifier) {
        client.patch("auth/registration/${regId}") {
            contentType(ContentType.Application.Json)
            setBody(UpdateRegistrationStatusRequestDto(status = RegistrationStatusDto.REJECTED))
        }
    }

}