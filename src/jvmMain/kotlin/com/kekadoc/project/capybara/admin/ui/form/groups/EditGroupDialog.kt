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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.data.source.remote.model.profile.ProfileDto
import com.kekadoc.project.capybara.admin.di.DI
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.domain.model.profile.ShortProfile
import com.kekadoc.project.capybara.admin.domain.model.profile.short
import com.kekadoc.project.capybara.admin.ui.form.users.RefreshController
import com.kekadoc.project.capybara.admin.ui.form.users.UserPickerDialog
import com.kekadoc.project.capybara.admin.ui.model.Resource
import com.kekadoc.project.capybara.admin.ui.model.asResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.koin.core.component.get
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription


data class EditGroupViewState(
    val initialGroup: GroupWithMembersIds? = null,
    val editedName: String = "",
    val members: Resource<List<ShortProfile>> = Resource.Undefined,
)

class EditGroupViewModel(
    private val groupRepository: GroupRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel<EditGroupViewState>(EditGroupViewState()) {

    val membersIdsFlow: MutableStateFlow<List<Identifier>> = MutableStateFlow(emptyList())
    private val refreshMembers = RefreshController()

    init {
        intent {
            refreshMembers
                .flatMapLatest {
                    println("_______refreshMembers")
                    membersIdsFlow
                        .flatMapLatest { membersIds ->
                            println("_______membersIds $membersIds")
                            profileRepository.getProfiles(membersIds)
                                .flowOn(Dispatchers.IO)
                                .asResource()
                        }
                }
                .onEach { reduce { state.copy(members = it) } }
                .collect()
        }
        intent {
            repeatOnSubscription {
                refreshMembers.refresh()
            }
        }
    }

    fun refreshMembers() = intent { refreshMembers.refresh(inBackground = false) }

    fun setTargetGroup(group: GroupWithMembersIds) = intent {
        membersIdsFlow.emit(group.members)
        reduce {
            state.copy(
                initialGroup = group,
                editedName = group.name,
            )
        }
    }

    fun setName(name: String) = intent {
        reduce { state.copy(editedName = name) }
    }

    fun addMember(profile: ShortProfile) = intent {
        membersIdsFlow.emit(membersIdsFlow.value.toMutableList().apply { add(profile.id) })
    }

    fun removeMember(profile: ShortProfile) = intent {
        membersIdsFlow.emit(membersIdsFlow.value.toMutableList().apply { remove(profile.id) })
    }

}

@Composable
fun EditGroupDialog(
    group: GroupWithMembersIds,
    onCloseRequest: () -> Unit,
    onApply: (GroupWithMembersIds) -> Unit,
    viewModel: EditGroupViewModel = viewModel()
) {
    LaunchedEffect(Unit) { viewModel.setTargetGroup(group) }
    val viewState: EditGroupViewState by viewModel.container.stateFlow.collectAsState()

    var profilePicker by remember { mutableStateOf(false) }
    if (profilePicker) {
        UserPickerDialog(
            onCloseRequest = { profilePicker = false },
            onSelectRequest = { profilePicker = false; viewModel.addMember(it.short()) },
            excludeUserIds = viewModel.membersIdsFlow.value,
        )
    }

    Dialog(
        onCloseRequest = onCloseRequest, state = rememberDialogState(
            size = DpSize(width = 400.dp, height = 600.dp),
        ),
        title = "Редактирование группы"
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewState.initialGroup?.id ?: "-",
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(text = "ID") },
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = viewState.editedName,
                onValueChange = { viewModel.setName(it) },
                readOnly = true,
                singleLine = true,
                label = { Text(text = "Имя") },
            )
            when (val members = viewState.members) {
                is Resource.Data -> {
                    val list = members.value
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
                }
                is Resource.Error -> {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(text = "Ошибка загрузки")
                        Button(onClick = { viewModel.refreshMembers() }) {
                            Text("Повтор")
                        }
                    }
                }
                is Resource.Loading,
                is Resource.Undefined -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            val membersState = remember(viewState.members) {
                derivedStateOf {
                    (viewState.members as? Resource.Data)?.value?.map { it.id }
                }
            }
            val initialGroup = viewState.initialGroup
            val members = membersState.value
            Button(
                onClick = {
                    if (members != null && initialGroup != null) {
                        onApply.invoke(
                            GroupWithMembersIds(
                                id = initialGroup.id,
                                name = viewState.editedName,
                                members = members,
                            )
                        )
                    }
                },
                enabled = viewState.editedName.isNotEmpty() && members != null,
            ) {
                Text(text = "Применить")
            }
        }
    }
}

@Composable
private fun ProfileCard(
    modifier: Modifier = Modifier,
    profile: ShortProfile,
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