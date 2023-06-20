@file:OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)

package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.common.DatabaseForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class AllUsersViewState(
    val isLoading: Boolean = true,
    val users: List<ExtendedProfile> = emptyList(),
    val editUser: ExtendedProfile? = null,
    val isBottomLoader: Boolean = false,
    val hasNext: Boolean = true,
    val query: String = "",
    val deleteUser: ExtendedProfile? = null,
)

class AllUsersViewModel(
    private val usersRepository: ProfileRepository,
) : ViewModel<AllUsersViewState>(AllUsersViewState()) {

    private val refreshController = RefreshController()

    init {
        intent {
            refreshController
                .onStart { emit(RefreshController.Options()) }
                .flatMapLatest {
                    reduce {
                        state.copy(
                            isLoading = true,
                            users = emptyList(),
                            isBottomLoader = false,
                            editUser = null,
                            hasNext = true,
                        )
                    }
                    usersRepository.getProfiles(
                        Range(from = 0, count = 10, query = state.query)
                    ).flowOn(Dispatchers.IO)
                }
                .onEach { users ->
                    reduce {
                        state.copy(
                            isLoading = false,
                            users = users,
                            hasNext = users.size >= 10,
                        )
                    }
                }
                .collect()
        }
    }

    fun onSetQuery(query: String) = blockingIntent {
        reduce { state.copy(query = query) }
    }

    fun onNext(index: Int) = intent {
        if (state.users.size - index < 10 && !state.isBottomLoader && state.hasNext) {
            reduce { state.copy(isBottomLoader = true) }
            val users = usersRepository.getProfiles(
                Range(from = state.users.size, count = 10, state.query)
            )
                .flowOn(Dispatchers.IO)
                .single()
            reduce {
                state.copy(
                    isLoading = false,
                    users = state.users + users,
                    isBottomLoader = false,
                    hasNext = users.size >= 10,
                )
            }
        }
    }

    fun delete(index: Int) = intent {
        reduce {
            val user = state.users.getOrNull(index)
            state.copy(deleteUser = user)
        }
    }

    fun deleteConfirm(isConfirm: Boolean) = intent {
        val user = state.deleteUser ?: return@intent
        if (!isConfirm) {
            reduce { state.copy(deleteUser = null) }
            return@intent
        }
        reduce { state.copy(isLoading = true) }
        usersRepository.deleteProfile(user.id)
            .flowOn(Dispatchers.IO)
            .collect()
        reduce {
            state.copy(
                isLoading = false,
                users = state.users.toMutableList().apply { remove(user) },
                deleteUser = null,
            )
        }
    }

    fun edit(index: Int) = intent {
        reduce { state.copy(editUser = state.users.getOrNull(index)) }
    }

    fun cancelEdit() = intent {
        reduce { state.copy(editUser = null) }
    }

    fun updateUser(newUser: ExtendedProfile) = intent {
        val editUser = state.editUser ?: return@intent
        reduce { state.copy(isLoading = true) }
        if (editUser.type != newUser.type) {
            usersRepository.updateProfileType(editUser.id, newUser.type)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        if (editUser.status != newUser.status) {
            usersRepository.updateProfileStatus(editUser.id, newUser.status)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        val isProfileChanged = listOf(
            editUser.name != newUser.name,
            editUser.surname != newUser.surname,
            editUser.patronymic != newUser.patronymic,
            editUser.about != newUser.about,
        ).any { it }
        if (isProfileChanged) {
            usersRepository.updateProfilePersonal(
                profileId = newUser.id,
                name = newUser.name,
                surname = newUser.surname,
                patronymic = newUser.patronymic,
                about = newUser.about,
            )
                .flowOn(Dispatchers.IO)
                .collect()
        }
        reduce {
            state.copy(
                isLoading = false,
                users = state.users.map { next ->
                    if (next.id == newUser.id) {
                        newUser
                    } else {
                        next
                    }
                },
                editUser = null,
            )
        }
    }

    fun refresh() = intent { refreshController.refresh() }

    fun search() = intent { refreshController.refresh() }

}

@Composable
fun AllUsersForm(viewModel: AllUsersViewModel = viewModel()) = Form(
    isRefreshEnabled = true,
    onRefresh = { viewModel.refresh() }
) {
    val viewState: AllUsersViewState by viewModel.container.stateFlow.collectAsState()

    viewState.deleteUser?.also { deleteUser ->
        AlertDialog(
            onDismissRequest = { viewModel.deleteConfirm(false) },
            confirmButton = {
                Button(onClick = { viewModel.deleteConfirm(true) }) {
                    Text("Да")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.deleteConfirm(false) }) {
                    Text("Нет")
                }
            },
            text = {
                Text("Вы уверены, что хотите удалить пользователя ${deleteUser.login}")
            }
        )
    }

    viewState.editUser?.also { editUser ->
        EditUserDialog(
            user = editUser,
            onCancel = { viewModel.cancelEdit() },
            onEdit = { viewModel.updateUser(it) },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            modifier = Modifier.width(400.dp)
                .onKeyEvent {
                    if (it.key == Key.Enter) {
                        viewModel.search()
                        true
                    } else {
                        false
                    }
                },
            value = viewState.query,
            onValueChange = { viewModel.onSetQuery(it) },
            label = { Text("Поиск") },
            trailingIcon = {
                IconButton(onClick = { viewModel.search() }) {
                    Icon(imageVector = Icons.Outlined.Search, contentDescription = null)
                }
            },
            singleLine = true,
            maxLines = 1,
            keyboardActions = KeyboardActions(onGo = { viewModel.search() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go)
        )
        CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body1) {
            DatabaseForm(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 64.dp),
                columns = 6,
                count = viewState.users.size,
                getText = { index, column ->
                    viewModel.onNext(index)
                    viewState.users[index].let {
                        val a: String = when (column) {
                            0 -> it.id
                            1 -> it.login
                            2 -> it.name
                            3 -> it.surname
                            4 -> it.patronymic
                            5 -> it.about.toString()
                            else -> ""
                        }
                        a
                    }
                },
                getHeader = {
                    when (it) {
                        0 -> "id"
                        1 -> "Логин"
                        2 -> "Имя"
                        3 -> "Фамилия"
                        4 -> "Отчество"
                        5 -> "Инфа"
                        else -> ""
                    }
                },
                onDelete = { viewModel.delete(it) },
                onEdit = { viewModel.edit(it) },
                isLoading = viewState.isLoading,
            )
        }
    }

}

@Composable
fun EditUserDialog(
    user: ExtendedProfile,
    onCancel: () -> Unit,
    onEdit: (ExtendedProfile) -> Unit,
) {
    var surname by remember(user) { mutableStateOf(user.surname) }
    var name by remember(user) { mutableStateOf(user.name) }
    var patronymic by remember(user) { mutableStateOf(user.patronymic) }
    var about by remember(user) { mutableStateOf(user.about) }
    var type by remember(user) { mutableStateOf(user.type) }
    Dialog(
        onCloseRequest = onCancel,
        title = "Редактирование пользователя ${user.login}",
        state = rememberDialogState(width = 400.dp, height = 600.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = surname,
                onValueChange = { surname = it },
                label = { Text("Фамилия") },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = patronymic,
                onValueChange = { patronymic = it },
                label = { Text("Отчество") },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = about.orEmpty(),
                onValueChange = { about = it },
                label = { Text("Информация") },
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                RadioButton(
                    selected = type == ExtendedProfile.Type.ADMIN,
                    onClick = { type = ExtendedProfile.Type.ADMIN },
                )
                Text("Администратор")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                RadioButton(
                    selected = type == ExtendedProfile.Type.SPEAKER,
                    onClick = { type = (ExtendedProfile.Type.SPEAKER) },
                )
                Text("Спикер")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                RadioButton(
                    selected = type == ExtendedProfile.Type.USER,
                    onClick = { type = (ExtendedProfile.Type.USER) },
                )
                Text("Пользователь")
            }
            Spacer(Modifier.weight(1f))
            Button(
                modifier = Modifier.fillMaxWidth(0.7f),
                onClick = {
                    val newUser = user.copy(
                        name = name,
                        surname = surname,
                        patronymic = patronymic,
                        about = about,
                        type = type,
                    )
                    onEdit.invoke(newUser)
                }
            ) {
                Text("Сохранить")
            }
        }
    }
}