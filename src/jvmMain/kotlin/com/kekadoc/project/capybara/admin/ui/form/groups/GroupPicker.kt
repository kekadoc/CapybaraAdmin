@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.kekadoc.project.capybara.admin.ui.form.groups

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import kotlin.time.Duration.Companion.milliseconds

data class GroupPickerViewState(
    val isLoading: Boolean = true,
    val query: String = "",
    val items: List<GroupWithMembersIds> = emptyList(),
    val selected: GroupWithMembersIds? = null,
)

class GroupPickerViewModel(
    private val groupRepository: GroupRepository,
) : ViewModel<GroupPickerViewState>(GroupPickerViewState()) {

    private val queryFlow = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        intent {
            val allGroups = groupRepository.getAllGroups()
                .flowOn(Dispatchers.IO)
                .onEach {
                    println("______$it")
                }

            val queryFlow = queryFlow
                .distinctUntilChanged()
                .debounce(600L.milliseconds)
                .flowOn(Dispatchers.Default)
                .onStart { emit("") }

            combine(
                allGroups,
                queryFlow,
            ) { groups, query -> groups.filter { it.name.contains(query, true) } }
                .onEach { items ->
                    println("____+++ $items")
                    reduce { state.copy(items = items, isLoading = false) }
                }
                .collect()
        }
    }

    fun setQuery(query: String) = blockingIntent {
        reduce { state.copy(query = query) }
        queryFlow.emit(query)
    }

}

@Composable
fun GroupPickerDialog(
    onCloseRequest: () -> Unit,
    onSelectRequest: (GroupWithMembersIds) -> Unit,
) {
    val viewModel: GroupPickerViewModel = viewModel()
    val viewState by viewModel.container.stateFlow.collectAsState()

    Dialog(
        onCloseRequest = onCloseRequest,
        state = rememberDialogState(
            size = DpSize(
                width = 400.dp,
                height = 600.dp,
            )
        ),
        resizable = false,
        title = "Выбор группы",
    ) {
        Box(
            modifier = Modifier.padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewState.query,
                    onValueChange = viewModel::setQuery,
                    leadingIcon = {
                        Icon(Icons.Outlined.Search, null)
                    },
                    trailingIcon = {
                        if (viewState.query.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.setQuery("") },
                            ) {
                                Icon(Icons.Outlined.Clear, null)
                            }
                        }
                    },
                    singleLine = true,
                )
                Spacer(
                    Modifier.height(16.dp)
                )
                if (viewState.isLoading) {
                   Box(
                       Modifier.weight(1f),
                       contentAlignment = Alignment.Center,
                   ) {
                       CircularProgressIndicator(
                           modifier = Modifier
                       )
                   }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = viewState.items,
                            key = { group -> group.id },
                        ) { item ->
                            GroupCard(
                                modifier = Modifier.fillMaxWidth(),
                                group = item,
                                onClick = { onSelectRequest(item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupCard(
    modifier: Modifier = Modifier,
    group: GroupWithMembersIds,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface.copy(0.33f),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(6.dp),
        ) {
            Text(
                text = group.id,
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = group.name,
                style = MaterialTheme.typography.subtitle1,
            )
            Text(
                text = "${group.members.size} участников",
                style = MaterialTheme.typography.body2,
            )
        }
    }
}