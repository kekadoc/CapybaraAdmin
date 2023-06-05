package com.kekadoc.project.capybara.admin.ui.form.groups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.common.DatabaseForm
import com.kekadoc.project.capybara.admin.ui.form.users.RefreshController
import com.kekadoc.project.capybara.admin.ui.model.Resource
import com.kekadoc.project.capybara.admin.ui.model.asResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import kotlin.time.Duration.Companion.milliseconds

data class AllGroupsViewState(
    val query: String = "",
    val groups: Resource<List<GroupWithMembersIds>> = Resource.Undefined,
    val editGroup: GroupWithMembersIds? = null,
    val isGroupsEditLoading: Boolean = false,
)

class AllGroupsViewModel(
    private val groupsRepository: GroupRepository,
) : ViewModel<AllGroupsViewState>(AllGroupsViewState()) {

    private val refreshController = RefreshController()
    private val queryFlow = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        intent {
            val queryFlow = this@AllGroupsViewModel.queryFlow
                .debounce(600.milliseconds)
                .onStart { emit("") }
                .flowOn(Dispatchers.Default)

            val allGroups = groupsRepository.getAllGroups()
                .flowOn(Dispatchers.IO)

            val allGroupsFlow = refreshController
                .flatMapLatest { refOpt ->
                    combine(queryFlow, allGroups) { query, groups ->
                        groups.filter { group -> group.name.contains(query, true) }
                    }
                        .asResource(skipLoading = refOpt.inBackground)
                }

                combine(queryFlow, allGroupsFlow) { query, groups ->
                    reduce { state.copy(groups = groups, query = query) }
                }.collect()
        }
        intent {
            repeatOnSubscription {
                refreshController.refresh()
            }
        }
    }

    fun delete(index: Int) = intent {
        val groups = (state.groups as? Resource.Data)?.value ?: return@intent
        reduce { state.copy(isGroupsEditLoading = true) }
        val group = groups[index]

        groupsRepository.deleteGroup(group.id)
            .flowOn(Dispatchers.IO)
            .onEach { reduce { state.copy(isGroupsEditLoading = false) } }
            .onEach { refreshController.refresh(inBackground = true) }
            .flowOn(Dispatchers.Default)
            .collect()
    }

    fun edit(index: Int) = intent {
        val groups = (state.groups as? Resource.Data)?.value ?: return@intent
        val group = groups.getOrNull(index)
        reduce { state.copy(editGroup = group) }
    }

    fun updateGroup(group: GroupWithMembersIds) = intent {
        val editGroup = state.editGroup ?: return@intent
        reduce { state.copy(isGroupsEditLoading = true, editGroup = null) }

        if (group.name != editGroup.name) {
            groupsRepository.updateGroupName(editGroup.id, group.name)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        val newMembers = group.members.filter { !editGroup.members.contains(it) }
        if (newMembers.isNotEmpty()) {
            groupsRepository.addMembersToGroup(editGroup.id, newMembers)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        val deletedMembers = editGroup.members.filter { !group.members.contains(it) }
        if (deletedMembers.isNotEmpty()) {
            groupsRepository.removeMembersFromGroup(editGroup.id, newMembers)
                .flowOn(Dispatchers.IO)
                .collect()
        }

        reduce { state.copy(isGroupsEditLoading = false) }
        refreshController.refresh(inBackground = true)
    }

    fun refresh() = intent {
        refreshController.refresh()
    }

}

@Composable
fun AllGroupsForm(viewModel: AllGroupsViewModel = viewModel()) = Form(
    isRefreshEnabled = true,
    onRefresh = { viewModel.refresh() },
) {
    val viewState: AllGroupsViewState by viewModel.container.stateFlow.collectAsState()

    //var editGroup: GroupWithMembersIds? by remember { mutableStateOf(null) }
    viewState.editGroup?.let {
        EditGroupDialog(
            group = it,
            onCloseRequest = { viewModel.edit(-1) },
            onApply = { newGroup -> viewModel.updateGroup(newGroup) }
        )
    }

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body1) {
        when (val groups = viewState.groups) {
            is Resource.Data -> {
                val list = groups.value
                DatabaseForm(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 36.dp),
                    columns = 3,
                    count = list.size,
                    getHeader = { column ->
                        when (column) {
                            0 -> "id"
                            1 -> "Имя"
                            2 -> "Кол-во участников"
                            else -> ""
                        }
                    },
                    getText = { index, column ->
                        list.get(index).let {
                            when (column) {
                                0 -> it.id
                                1 -> it.name
                                2 -> it.members.size.toString()
                                else -> ""
                            }
                        }
                    },
                    onDelete = viewModel::delete,
                    onEdit = { viewModel.edit(it) },
                    isLoading = viewState.isGroupsEditLoading,
                )
            }
            is Resource.Error -> {
                Column {
                    Text("Ошибка загрузки")
                    Button(onClick = viewModel::refresh) {
                        Text("Повторить")
                    }
                }
            }
            is Resource.Loading,
            is Resource.Undefined -> {
                CircularProgressIndicator()
            }
        }
    }
}