package com.kekadoc.project.capybara.admin.ui.form.groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.users.UserPickerDialog
import com.kekadoc.project.capybara.admin.ui.kit.compose.ActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class CreateGroupsViewState(
    val name: String = "",
    val members: List<ExtendedProfile> = emptyList(),
    val isCreateProcess: Boolean = false,
    val createdGroup: GroupWithMembersIds? = null,
)

class CreateGroupsViewModel(
    private val groupsRepository: GroupRepository,
) : ViewModel<CreateGroupsViewState>(CreateGroupsViewState()) {


    fun setName(name: String) = intent {
        reduce { state.copy(name = name) }
    }

    fun addMember(member: ExtendedProfile) = intent {
        reduce { state.copy(members = state.members + member) }
    }

    fun removeMember(member: ExtendedProfile) = intent {
        reduce { state.copy(members = state.members - member) }
    }

    fun create() = intent {
        val name = state.name
        val members = state.members
        groupsRepository.createGroup(name)
            .flowOn(Dispatchers.IO)
            .flatMapConcat { group ->
                groupsRepository.addMembersToGroup(
                    id = group.id,
                    members = members.map { it.id },
                )
            }
            .flowOn(Dispatchers.IO)
            .onEach { reduce { state.copy(createdGroup = it, isCreateProcess = false) } }
            .onStart { reduce { state.copy(isCreateProcess = true) } }
            .collect()
    }

    fun restart() = intent {
        reduce { CreateGroupsViewState() }
    }

}

@Composable
fun CreateGroupsForm(viewModel: CreateGroupsViewModel = viewModel()) = Form {
    val viewState by viewModel.container.stateFlow.collectAsState()
    var profilePicker by remember { mutableStateOf(false) }
    val memberIds by remember(viewState.members) {
        derivedStateOf {
            viewState.members.map { it.id }
        }
    }
    if (profilePicker) {
        UserPickerDialog(
            onCloseRequest = { profilePicker = false },
            onSelectRequest = { profilePicker = false; viewModel.addMember(it) },
            excludeUserIds = memberIds,
        )
    }
    Card(
        modifier = Modifier
            .fillMaxHeight()
            .widthIn(max = 400.dp)
            .padding(vertical = 36.dp),
    ) {
        val createdGroup = viewState.createdGroup
        if (createdGroup != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    space = 8.dp,
                    alignment = Alignment.CenterVertically,
                ),
            ) {
                Text(
                    text = "Группа успешно создана",
                    style = MaterialTheme.typography.subtitle1,
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = createdGroup.id,
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    label = { Text("Идентификатор") },
                )
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = createdGroup.name,
                    onValueChange = {},
                    singleLine = true,
                    readOnly = true,
                    label = { Text("Идентификатор") },
                )
                Button(onClick = { viewModel.restart() }) {
                    Text(text = "Далее")
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.name,
                    onValueChange = viewModel::setName,
                    singleLine = true,
                    label = {
                        Text("Имя")
                    }
                )
                val list = viewState.members
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    items(
                        items = list,
                        key = { it.id },
                        contentType = { it::class },
                        itemContent = { profile ->
                            ProfileCard(
                                modifier = Modifier.fillMaxWidth(),
                                profile = profile,
                                onDelete = { viewModel.removeMember(profile) },
                            )
                        },
                    )
                    item(key = "Add", contentType = "Add") {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { profilePicker = true }
                        ) {
                            Text("Добавить")
                        }
                    }
                }
                ActionButton(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    text = "Создать",
                    onClick = viewModel::create,
                    isEnabled = !viewState.isCreateProcess && viewState.name.isNotEmpty(),
                    isLoading = viewState.isCreateProcess,
                )
            }
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    profile: ExtendedProfile,
    onDelete: () -> Unit,
) {
    Card(
        modifier = modifier,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface.copy(0.33f),
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = profile.id,
                    style = MaterialTheme.typography.caption,
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "${profile.surname} ${profile.name} ${profile.patronymic}",
                    style = MaterialTheme.typography.subtitle1,
                )
            }
            IconButton(
                onClick = onDelete,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                )
            }
        }
    }
}