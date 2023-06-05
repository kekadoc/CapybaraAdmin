package com.kekadoc.project.capybara.admin.data.repository.auth

import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSource
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.Tokens
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationRequestInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import java.util.prefs.Preferences

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
) : AuthRepository {

    override fun login(login: String, password: String): Flow<Tokens> = flowOf {
        remoteDataSource.login(login, password)
    }
        .onEach {
            Preferences.userRoot().node("n").apply {
                put("r", it.refreshToken)
                put("a", it.accessToken)
            }
        }

    override fun logout(): Flow<Unit> = flowOf {
        Preferences.userRoot().node("n").removeNode()
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