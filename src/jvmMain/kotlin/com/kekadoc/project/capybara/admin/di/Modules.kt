package com.kekadoc.project.capybara.admin.di

import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepositoryImpl
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepositoryImpl
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepositoryImpl
import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSource
import com.kekadoc.project.capybara.admin.data.source.remote.api.auth.AuthRemoteDataSourceImpl
import com.kekadoc.project.capybara.admin.data.source.remote.api.group.GroupRemoteDataSource
import com.kekadoc.project.capybara.admin.data.source.remote.api.group.GroupRemoteDataSourceImpl
import com.kekadoc.project.capybara.admin.data.source.remote.api.profile.ProfileRemoteDataSource
import com.kekadoc.project.capybara.admin.data.source.remote.api.profile.ProfileRemoteDataSourceImpl
import com.kekadoc.project.capybara.admin.ui.AdminAppViewModel
import com.kekadoc.project.capybara.admin.ui.form.auth.AuthViewModel
import com.kekadoc.project.capybara.admin.ui.form.groups.*
import com.kekadoc.project.capybara.admin.ui.form.users.*
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.util.prefs.Preferences

val remoteDataSourcesModule = module {

    single<HttpClient> {
        HttpClient(OkHttp) {
            defaultRequest {
                url("http://109.196.136.40:8600/api/v1/")
                contentType(ContentType.Application.Json)
                headers {
                    append("ApiKey", "ff9de788-c243-4350-9ed0-7bfb847c4c1b")
                    append("Authorization", Preferences.userRoot().node("n").get("a", ""))
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

    single<ProfileRemoteDataSource> {
        ProfileRemoteDataSourceImpl(
            client = get(),
        )
    }

    single<GroupRemoteDataSource> {
        GroupRemoteDataSourceImpl(
            httpClient = get(),
        )
    }

}

val repositoriesModule = module {

    single<AuthRepository> {
        AuthRepositoryImpl(
            remoteDataSource = get(),
        )
    }

    single<ProfileRepository> {
        ProfileRepositoryImpl(
            remoteDataSource = get(),
        )
    }

    single<GroupRepository> {
        GroupRepositoryImpl(
            remoteDataSource = get(),
        )
    }

}

val viewModelsModule = module {

    single<AuthViewModel> {
        AuthViewModel(
            authRepository = get(),
            profileRepository = get(),
        )
    }

    single<AdminAppViewModel> {
        AdminAppViewModel(
            authRepository = get(),
            profileRepository = get(),
        )
    }

    single<AllGroupsViewModel> {
        AllGroupsViewModel(
            groupsRepository = get(),
        )
    }

    single<CreateGroupsViewModel> {
        CreateGroupsViewModel(
            groupsRepository = get(),
        )
    }

    single<AllUsersViewModel> {
        AllUsersViewModel(
            usersRepository = get(),
        )
    }

    single<CreateUserViewModel> {
        CreateUserViewModel(
            profileRepository = get(),
        )
    }

    single<RegistrationsUserViewModel> {
        RegistrationsUserViewModel(
            authRepository = get(),
            groupRepository = get(),
        )
    }

    single<UserPickerViewModel> {
        UserPickerViewModel(
            usersRepository = get(),
        )
    }

    single<GroupPickerViewModel> {
        GroupPickerViewModel(
            groupRepository = get(),
        )
    }

    single<UserAccessViewModel> {
        UserAccessViewModel(
            profileRepository = get(),
        )
    }

    single<EditGroupViewModel> {
        EditGroupViewModel(
            groupRepository = get(),
            profileRepository = get(),
        )
    }

}