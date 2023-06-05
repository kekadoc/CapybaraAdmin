package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationRequestInfo
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationStatus
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.CreateProfileResponse
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.groups.GroupCard
import com.kekadoc.project.capybara.admin.ui.form.groups.GroupPickerDialog
import com.kekadoc.project.capybara.admin.ui.kit.compose.ActionButton
import com.kekadoc.project.capybara.admin.ui.kit.compose.DisabledInteractionSource
import com.kekadoc.project.capybara.admin.ui.model.Resource
import com.kekadoc.project.capybara.admin.ui.model.asResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

data class CreateUserViewState(
    val type: ExtendedProfile.Type = ExtendedProfile.Type.USER,
    val name: String = "",
    val surname: String = "",
    val patronymic: String = "",
    val about: String = "",
    val email: String = "",
    val isCreateEnabled: Boolean = false,
    val createdUser: Resource<CreateProfileResponse> = Resource.Undefined,
)

class CreateUserViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel<CreateUserViewState>(CreateUserViewState()) {

    fun onEmailChanged(email: String) = blockingIntent { reduce { state.copy(email = email) } }

    fun create() = intent {
        profileRepository.createProfile(
            type = state.type,
            name = state.name,
            surname = state.surname,
            patronymic = state.patronymic,
            about = state.about,
            emailForInvite = state.email,
            login = null,
            password = null,
        )
            .flowOn(Dispatchers.IO)
            .asResource()
            .collect { resource -> reduce { state.copy(createdUser = resource) } }
    }

    fun onSurnameChanged(surname: String) = blockingIntent {
        reduce { state.copy(surname = surname) }
        reduce { state.copy(isCreateEnabled = isCreateAvailable(state)) }
    }

    fun onNameChanged(name: String) = blockingIntent {
        reduce { state.copy(name = name) }
        reduce { state.copy(isCreateEnabled = isCreateAvailable(state)) }
    }

    fun onPatronymicChanged(patronymic: String) = blockingIntent {
        reduce { state.copy(patronymic = patronymic) }
        reduce { state.copy(isCreateEnabled = isCreateAvailable(state)) }
    }

    fun onAboutChanged(about: String) = blockingIntent {
        reduce { state.copy(about = about) }
        reduce { state.copy(isCreateEnabled = isCreateAvailable(state)) }
    }

    fun setProfileType(type: ExtendedProfile.Type) = intent {
        if (type == state.type) return@intent
        reduce { state.copy(type = type) }
    }

    fun restart() = intent {
        reduce { CreateUserViewState() }
    }

    private fun isCreateAvailable(state: CreateUserViewState): Boolean = with(state) {
        (surname.isNotBlank() && name.isNotBlank() && patronymic.isNotBlank())
    }

}

@Composable
fun CreateUserForm(viewModel: CreateUserViewModel = viewModel()) = Form {
    val viewState by viewModel.container.stateFlow.collectAsState()
    Card(
        modifier = Modifier.wrapContentHeight().widthIn(max = 400.dp),
        elevation = 3.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val createdUser = viewState.createdUser
            if (createdUser is Resource.Data) {
                Text(
                    text = "Пользователь успешно создан",
                    style = MaterialTheme.typography.body1,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = createdUser.value.profile.login,
                    onValueChange = {},
                    label = { Text("Логин") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val selection = StringSelection(createdUser.value.profile.login)
                                Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = true,
                    readOnly = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = createdUser.value.tempPassword,
                    onValueChange = {},
                    label = { Text("Пароль") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val selection = StringSelection(createdUser.value.tempPassword)
                                Toolkit.getDefaultToolkit().systemClipboard.setContents(selection, selection)
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                            )
                        }
                    },
                    singleLine = true,
                    readOnly = true,
                )
                Button(
                    onClick = viewModel::restart,
                ) {
                    Text(text = "Далее")
                }
            } else {
                Text(
                    "Создание нового пользователя",
                    style = MaterialTheme.typography.h6,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.surname,
                    onValueChange = viewModel::onSurnameChanged,
                    label = { Text("Фамилия") },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.name,
                    onValueChange = viewModel::onNameChanged,
                    label = { Text("Имя") },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.patronymic,
                    onValueChange = viewModel::onPatronymicChanged,
                    label = { Text("Отчество") },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.about,
                    onValueChange = viewModel::onAboutChanged,
                    label = { Text("Информация") },
                    singleLine = true,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                ) {
                    RadioButton(
                        selected = viewState.type == ExtendedProfile.Type.ADMIN,
                        onClick = { viewModel.setProfileType(ExtendedProfile.Type.ADMIN) },
                    )
                    Text("Администратор")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                ) {
                    RadioButton(
                        selected = viewState.type == ExtendedProfile.Type.SPEAKER,
                        onClick = { viewModel.setProfileType(ExtendedProfile.Type.SPEAKER) },
                    )
                    Text("Спикер")
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                ) {
                    RadioButton(
                        selected = viewState.type == ExtendedProfile.Type.USER,
                        onClick = { viewModel.setProfileType(ExtendedProfile.Type.USER) },
                    )
                    Text("Пользователь")
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("E-mail") },
                    singleLine = true,
                )
                Spacer(modifier = Modifier.height(16.dp))
                ActionButton(
                    modifier = Modifier.fillMaxWidth(0.75f),
                    text = "Создать",
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                    ),
                    isEnabled = viewState.isCreateEnabled,
                    isLoading = createdUser is Resource.Loading,
                    onClick = viewModel::create,
                )
            }
        }
    }
}
