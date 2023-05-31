package com.kekadoc.project.capybara.admin.di

import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepositoryImpl
import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSource
import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSourceImpl
import com.kekadoc.project.capybara.admin.ui.AdminAppViewModel
import com.kekadoc.project.capybara.admin.ui.form.auth.AuthViewModel
import com.kekadoc.project.capybara.admin.ui.form.groups.AllGroupsViewModel
import com.kekadoc.project.capybara.admin.ui.form.groups.CreateGroupsViewModel
import com.kekadoc.project.capybara.admin.ui.form.messages.AllMessagesViewModel
import com.kekadoc.project.capybara.admin.ui.form.messages.CreateMessageViewModel
import com.kekadoc.project.capybara.admin.ui.form.messages.SentMessagesViewModel
import com.kekadoc.project.capybara.admin.ui.form.users.AllUsersViewModel
import com.kekadoc.project.capybara.admin.ui.form.users.CreateUserViewModel
import com.kekadoc.project.capybara.admin.ui.form.users.RegistrationsUserViewModel
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val remoteDataSourcesModule = module {

    single<HttpClient> {
        HttpClient(OkHttp) {
            defaultRequest {
                url("http://127.0.0.1:8080/api/v1/")
                headers {
                    append("ApiKey", "ff9de788-c243-4350-9ed0-7bfb847c4c1b")
                }
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
                this.logger = object : Logger {
                    override fun log(message: String) {
                        println(message)
                    }
                }
            }
        }
    }

    single<AuthRemoteDataSource> {
        AuthRemoteDataSourceImpl(
            client = get(),
        )
    }

}

val repositoriesModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(
            remoteDataSource = get(),
        )
    }

}

val viewModelsModule = module {

    single<AuthViewModel> {
        AuthViewModel(
            authRepository = get(),
        )
    }

    single<AdminAppViewModel> {
        AdminAppViewModel()
    }

    single<AllGroupsViewModel> {
        AllGroupsViewModel()
    }

    single<CreateGroupsViewModel> {
        CreateGroupsViewModel()
    }

    single<AllMessagesViewModel> {
        AllMessagesViewModel()
    }

    single<CreateMessageViewModel> {
        CreateMessageViewModel()
    }

    single<SentMessagesViewModel> {
        SentMessagesViewModel()
    }

    single<AllUsersViewModel> {
        AllUsersViewModel()
    }

    single<CreateUserViewModel> {
        CreateUserViewModel()
    }

    single<RegistrationsUserViewModel> {
        RegistrationsUserViewModel()
    }

}