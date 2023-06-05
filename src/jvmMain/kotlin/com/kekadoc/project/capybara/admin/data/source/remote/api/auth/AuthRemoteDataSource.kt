package com.kekadoc.project.capybara.admin.data.source.remote.api.auth

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.Tokens
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationRequestInfo

interface AuthRemoteDataSource {

    suspend fun login(login: String, password: String): Tokens

    suspend fun registrationRequests(): List<RegistrationRequestInfo>

    suspend fun confirmRegistration(regId: Identifier)

    suspend fun rejectRegistration(regId: Identifier)

}