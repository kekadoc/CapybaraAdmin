package com.kekadoc.project.capybara.admin.ui.form.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.data.source.remote.api.UnauthorizedException
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
    val incorrectCred: Boolean = false,
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
        reduce { state.copy(login = login, incorrectCred = false) }
    }

    fun setPassword(password: String) = blockingIntent {
        reduce { state.copy(password = password, incorrectCred = false) }
    }

    fun login() = intent {
        reduce { state.copy(isLoading = true) }
        authRepository.login(state.login, state.password)
            .catch { error ->
                when (error) {
                    is UnauthorizedException -> reduce { state.copy(incorrectCred = true) }
                }
                reduce { state.copy(isLoading = false) }
            }
            .collect()
        profileRepository.getProfile().collect()
        reduce { state.copy(isLoading = false) }
    }

}

@Composable
fun AuthContent(viewModel: AuthViewModel = viewModel()) {
    val viewState: AuthViewState by viewModel.container.stateFlow.collectAsState()
    var isShowPassword: Boolean by remember { mutableStateOf(false) }
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
            label = { Text(text = text.form.auth.passwordLabel) },
            visualTransformation = if (isShowPassword) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { isShowPassword = !isShowPassword }) {
                    Icon(
                        imageVector = if (isShowPassword) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.VisibilityOff
                        },
                        contentDescription = null,
                    )
                }
            }
        )
        if (viewState.incorrectCred) {
            Text(
                text = "Неверные логин/пароль",
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.error,
            )
        }
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