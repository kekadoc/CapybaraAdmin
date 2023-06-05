@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)

package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.group.GroupRepository
import com.kekadoc.project.capybara.admin.domain.model.Identifier
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationRequestInfo
import com.kekadoc.project.capybara.admin.domain.model.auth.registration.RegistrationStatus
import com.kekadoc.project.capybara.admin.domain.model.group.Group
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.groups.GroupCard
import com.kekadoc.project.capybara.admin.ui.kit.compose.ActionButton
import com.kekadoc.project.capybara.admin.ui.kit.compose.DisabledInteractionSource
import com.kekadoc.project.capybara.admin.ui.model.Resource
import com.kekadoc.project.capybara.admin.ui.model.asResource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription

data class RegistrationsUserViewState(
    val items: Resource<List<Pair<RegistrationRequestInfo, GroupWithMembersIds?>>> = Resource.Undefined,
    val confirmingList: List<Identifier> = emptyList(),
    val rejectingList: List<Identifier> = emptyList(),
)

class RefreshController : Flow<RefreshController.Options> {

    private val intent = MutableSharedFlow<RefreshController.Options>()

    suspend fun refresh(inBackground: Boolean = false) {
        intent.emit(Options(inBackground))
    }

    override suspend fun collect(collector: FlowCollector<RefreshController.Options>) {
        intent.collect(collector)
    }

    data class Options(
        val inBackground: Boolean = false,
    )

}

class RegistrationsUserViewModel(
    private val authRepository: AuthRepository,
    private val groupRepository: GroupRepository,
) : ViewModel<RegistrationsUserViewState>(RegistrationsUserViewState()) {

    private val refreshController = RefreshController()

    init {
        intent {
            refreshController
                .flowOn(Dispatchers.Default)
                .flatMapLatest { options ->
                    authRepository.registrationRequests()
                        .flowOn(Dispatchers.IO)
                        .filterElements { it.status == RegistrationStatus.WAIT_APPROVING }
                        .map { list ->
                            val groupIds = list.mapNotNull(RegistrationRequestInfo::groupId)
                            val groups = groupRepository.getGroups(groupIds)
                                .flowOn(Dispatchers.IO)
                                .single()
                            list.map { regReq ->
                                regReq to regReq.groupId?.let { groupId ->
                                    groups.find { group -> group.id == groupId }
                                }
                            }
                        }
                        .asResource(skipLoading = options.inBackground)
                }
                .flowOn(Dispatchers.Default)
                .onEach { reduce { state.copy(items = it) } }
                .collect()
        }
        intent {
            repeatOnSubscription {
                refreshController.refresh()
            }
        }
    }

    fun refresh() = intent { refreshController.refresh() }

    fun confirm(requestId: Identifier) = intent {
        authRepository.confirmRegistration(requestId)
            .flowOn(Dispatchers.IO)
            .collect()
        refreshController.refresh(inBackground = true)
    }

    fun reject(requestId: Identifier) = intent {
        authRepository.rejectRegistration(requestId)
            .flowOn(Dispatchers.IO)
            .collect()
        refreshController.refresh(inBackground = true)
    }

}

@Composable
fun RegistrationsUserForm(viewModel: RegistrationsUserViewModel = viewModel()) = Form(
    onRefresh = viewModel::refresh,
) {
    val viewState by viewModel.container.stateFlow.collectAsState()
    Column(
        modifier = Modifier.fillMaxHeight().widthIn(max = 500.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (val items = viewState.items) {
            is Resource.Data -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        horizontal = 64.dp,
                        vertical = 16.dp,
                    )
                ) {
                    items(
                        items = items.value,
                        key = { it.first.id },
                        itemContent = { (request, group) ->
                            val isConfirming by remember(viewState.confirmingList) {
                                derivedStateOf {
                                    viewState.confirmingList.contains(request.id)
                                }
                            }
                            val isRejecting by remember(viewState.rejectingList) {
                                derivedStateOf {
                                    viewState.rejectingList.contains(request.id)
                                }
                            }
                            val onConfirm: () -> Unit = remember(request) { { viewModel.confirm(request.id) } }
                            val onReject: () -> Unit = remember(request) { { viewModel.reject(request.id) } }
                            RegistrationRequestComponent(
                                modifier = Modifier.fillMaxWidth().animateItemPlacement(),
                                info = request,
                                group = group,
                                isConfirming = isConfirming,
                                isRejecting = isRejecting,
                                onConfirm = onConfirm,
                                onReject = onReject,
                            )
                        }
                    )
                }
            }
            is Resource.Error -> {
                Text("Ошибка загрузки")
                Button(onClick = viewModel::refresh) {
                    Text("Повторить")
                }
            }
            is Resource.Loading,
            is Resource.Undefined -> {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun RegistrationRequestComponent(
    modifier: Modifier = Modifier,
    info: RegistrationRequestInfo,
    group: GroupWithMembersIds?,
    isConfirming: Boolean,
    isRejecting: Boolean,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = 3.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val statusText = remember(info.status) {
                when (info.status) {
                    RegistrationStatus.WAIT_EMAIL_CONFIRMING -> "Ожидание подтверждения почты"
                    RegistrationStatus.WAIT_APPROVING -> "Ожидание подтверждения"
                    RegistrationStatus.REJECTED -> "Отклонено"
                    RegistrationStatus.CANCELLED -> "Отменено"
                    RegistrationStatus.COMPLETED -> "Завершена"
                }
            }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = statusText,
                onValueChange = {},
                label = { Text("Статус") },
                readOnly = true,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = info.surname,
                onValueChange = {},
                label = { Text("Фамилия") },
                readOnly = true,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = info.name,
                onValueChange = {},
                label = { Text("Имя") },
                readOnly = true,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = info.patronymic,
                onValueChange = {},
                label = { Text("Отчество") },
                readOnly = true,
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = info.email,
                onValueChange = {},
                label = { Text("E-mail") },
                readOnly = true,
                singleLine = true,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            ) {
                Checkbox(
                    checked = info.isStudent,
                    onCheckedChange = {},
                    interactionSource = DisabledInteractionSource,
                )
                Text("Студент")
            }
            if (group != null) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = group.name,
                    onValueChange = {},
                    label = { Text("Группа") },
                    readOnly = true,
                    singleLine = true,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Отклонить",
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = MaterialTheme.colors.error,
                    ),
                    isEnabled = !isConfirming && !isRejecting,
                    isLoading = isRejecting,
                    onClick = onReject,
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    text = "Подтвердить",
                    isEnabled = !isConfirming && !isRejecting,
                    isLoading = isConfirming,
                    onClick = onConfirm,
                )
            }
        }
    }
}