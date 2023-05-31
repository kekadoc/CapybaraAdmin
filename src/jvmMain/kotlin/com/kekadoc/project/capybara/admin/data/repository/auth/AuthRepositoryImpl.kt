package com.kekadoc.project.capybara.admin.data.repository.auth

import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSource
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.Tokens
import com.kekadoc.project.capybara.admin.domain.server.model.auth.registration.RegistrationRequestInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {

    override fun login(login: String, password: String): Flow<Tokens> = flowOf {
        remoteDataSource.login(login, password)
    }

    override fun registrationRequests(): Flow<List<RegistrationRequestInfo>> = flowOf {
        remoteDataSource.registrationRequests()
    }

    override fun confirmRegistration(regId: Identifier): Flow<Unit> = flowOf {
        remoteDataSource.confirmRegistration(regId)
    }

    override fun rejectRegistration(regId: Identifier): Flow<Unit> = flowOf {
        remoteDataSource.rejectRegistration(regId)
    }

}