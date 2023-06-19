@file:OptIn(ExperimentalMaterialApi::class)

package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.group.Group
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.resource.text.text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.blockingIntent
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import kotlin.time.Duration.Companion.seconds


data class UserPickerViewState(
    val isLoading: Boolean = true,
    val isBottomLoading: Boolean = false,
    val hasNext: Boolean = false,
    val query: String = "",
    val items: List<ExtendedProfile> = emptyList(),
)

class UserPickerViewModel(
    private val usersRepository: ProfileRepository,
) : ViewModel<UserPickerViewState>(UserPickerViewState()) {

    private val refreshController = RefreshController()

    private val excludeUserIdsFlow = MutableStateFlow<List<Identifier>>(emptyList())

    private val queryRequest = MutableSharedFlow<String>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    init {
        intent {
            val queryFlow = queryRequest
                .distinctUntilChanged()
                .debounce(1.seconds)
                .onStart { emit("") }
                .flowOn(Dispatchers.Default)

            refreshController.flatMapLatest { refOpt ->
                combine(queryFlow, excludeUserIdsFlow) { query, excludeUserIds ->
                    usersRepository
                        .getProfiles(Range(from = 0, count = 30, query = query))
                        .flowOn(Dispatchers.IO)
                }
                    .flattenConcat()
            }
                .onEach { users ->
                    reduce {
                        state.copy(
                            isLoading = false,
                            items = users.filter { !excludeUserIdsFlow.value.contains(it.id) },
                            isBottomLoading = false,
                            hasNext = users.size >= 10,
                        )
                    }
                }
                .collect()
        }
        intent {
            repeatOnSubscription {
                refreshController.refresh()
            }
        }
    }

    fun setExcludeIds(ids: List<Identifier>) = blockingIntent { excludeUserIdsFlow.emit(ids) }

    fun setQuery(query: String) = blockingIntent {
        reduce { state.copy(query = query) }
        queryRequest.emit(query)
    }

    fun onIndex(index: Int) = intent {
        if (state.items.size - index < 10 && !state.isBottomLoading && state.hasNext) {
            reduce { state.copy(isBottomLoading = true) }
            val users = usersRepository.getProfiles(
                Range(from = state.items.size, count = 10, state.query)
            ).flowOn(Dispatchers.IO).single()
            delay(2_000)
            reduce {
                state.copy(
                    isLoading = false,
                    items = (state.items + users).filter { !excludeUserIdsFlow.value.contains(it.id) },
                    isBottomLoading = false,
                    hasNext = users.size >= 10,
                )
            }
        }
    }

}

@Composable
fun UserPickerDialog(
    onCloseRequest: () -> Unit,
    onSelectRequest: (ExtendedProfile) -> Unit,
    excludeUserIds: List<Identifier> = emptyList(),
) {
    val viewModel: UserPickerViewModel = viewModel()
    LaunchedEffect(Unit) { viewModel.setExcludeIds(excludeUserIds) }
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
        title = "Выбор пользователя",
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
                        itemsIndexed(viewState.items) { index, item ->
                            viewModel.onIndex(index)
                            ProfileCard(
                                modifier = Modifier.fillMaxWidth(),
                                profile = item,
                                onClick = { onSelectRequest(item) },
                            )
                        }
                        if (viewState.isBottomLoading) {
                            item {
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun ProfileCard(
    modifier: Modifier = Modifier,
    profile: ExtendedProfile,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface.copy(0.33f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
        ) {
            Text(
                text = profile.id,
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = "${profile.surname} ${profile.name} ${profile.patronymic}",
                style = MaterialTheme.typography.subtitle1,
            )
            Text(
                text = "${profile.about}",
                style = MaterialTheme.typography.body2,
            )
        }
    }
}