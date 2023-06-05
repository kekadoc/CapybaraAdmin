package com.kekadoc.project.capybara.admin.ui.form.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.ui.resource.text.text
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.Serializable
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class AuthViewState(
    val login: String = "OlegAdmin",
    val password: String = "123",
    val isLoading: Boolean = false,
)

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel<AuthViewState>(AuthViewState()) {

    @Serializable
    private data class AuthRequestDto(
        val login: String,
        val password: String,
    )

    fun setLogin(login: String) = blockingIntent {
        reduce { state.copy(login = login) }
    }

    fun setPassword(password: String) = intent {
        reduce { state.copy(password = password) }
    }

    fun login() = intent {
        reduce { state.copy(isLoading = true) }
        authRepository.login(state.login, state.password)
            .catch {
                it.printStackTrace()
                reduce { state.copy(isLoading = false) }
            }
            .collect {
                println(it)
            }
        profileRepository.getProfile().collect()
        reduce { state.copy(isLoading = false) }
    }

}

@Composable
fun AuthContent(viewModel: AuthViewModel = viewModel()) {
    val viewState: AuthViewState by viewModel.container.stateFlow.collectAsState()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = viewState.login,
            onValueChange = viewModel::setLogin,
            label = { Text(text = text.form.auth.loginLabel) },
            keyboardOptions = KeyboardOptions()
        )
        OutlinedTextField(
            value = viewState.password,
            onValueChange = viewModel::setPassword,
            label = { Text(text = text.form.auth.passwordLabel) }
        )
        Button(
            enabled = !viewState.isLoading,
            onClick = viewModel::login,
        ) {
            if (viewState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                )
            } else {
                Text(text = text.form.auth.actionLogin)
            }
        }
    }
}