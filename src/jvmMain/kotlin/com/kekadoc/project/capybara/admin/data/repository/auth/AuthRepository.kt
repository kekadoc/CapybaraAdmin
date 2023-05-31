package com.kekadoc.project.capybara.admin.data.repository.auth

import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.Tokens
import com.kekadoc.project.capybara.admin.domain.server.model.auth.registration.RegistrationRequestInfo
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun login(login: String, password: String): Flow<Tokens>

    fun registrationRequests(): Flow<List<RegistrationRequestInfo>>

    fun confirmRegistration(regId: Identifier): Flow<Unit>

    fun rejectRegistration(regId: Identifier): Flow<Unit>

}