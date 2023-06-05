@file:OptIn(ExperimentalMaterialApi::class)

package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToGroup
import com.kekadoc.project.capybara.admin.domain.model.UserAccessToUser
import com.kekadoc.project.capybara.admin.domain.model.group.GroupWithMembersIds
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.groups.GroupCard
import com.kekadoc.project.capybara.admin.ui.form.groups.GroupPickerDialog
import com.kekadoc.project.capybara.admin.ui.kit.compose.ActionButton
import com.kekadoc.project.capybara.admin.ui.model.Resource
import com.kekadoc.project.capybara.admin.ui.model.asResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class UserAccessViewState(
    val isLoading: Boolean = true,
    val targetUser: ExtendedProfile? = null,
    val sourceUser: ExtendedProfile? = null,
    val targetGroup: GroupWithMembersIds? = null,
    val userAccessToUserApplyingLoading: Boolean = false,
    val userAccessToUser: Resource<UserAccessToUser> = Resource.Undefined,
    val userAccessToGroupApplyingLoading: Boolean = false,
    val userAccessToGroup: Resource<UserAccessToGroup> = Resource.Undefined,
)

class UserAccessViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel<UserAccessViewState>(UserAccessViewState()) {

    fun setSourceUser(profile: ExtendedProfile) = intent {
        reduce { state.copy(sourceUser = profile) }
        loadAccessToUser()
        loadAccessToGroup()
    }

    fun setTargetUser(profile: ExtendedProfile) = intent {
        reduce {
            state.copy(
                targetUser = profile,
            )
        }
        loadAccessToUser()
    }

    fun setTargetGroup(group: GroupWithMembersIds) = intent {
        reduce {
            state.copy(
                targetGroup = group,
            )
        }
        loadAccessToGroup()
    }

    fun applyUserAccessToUser(
        readProfile: Boolean? = null,
        sentNotification: Boolean? = null,
        contactInfo: Boolean? = null,
    ) = intent {
        val fromProfileId = state.sourceUser?.id ?: return@intent
        val toProfileId = state.targetUser?.id ?: return@intent
        profileRepository.updateUserAccessToUser(
            fromUserId = fromProfileId,
            toUserId = toProfileId,
            access = UserAccessToUser.Updater(
                readProfile = readProfile,
                sentNotification = sentNotification,
                contactInfo = contactInfo,
            )
        )
            .onStart {
                reduce { state.copy(userAccessToUserApplyingLoading = true) }
            }
            .onEach { access ->
                delay(2_000)
                reduce {
                    state.copy(
                        userAccessToUserApplyingLoading = false,
                        userAccessToUser = Resource.Data(access),
                    )
                }
            }
            .collect()
    }

    fun applyUserAccessToGroup(
        readInfo: Boolean? = null,
        readMembers: Boolean? = null,
        sentNotification: Boolean? = null,
    ) = intent {
        profileRepository.updateUserAccessToGroup(
            fromUserId = state.sourceUser?.id!!,
            toGroupId = state.targetGroup?.id!!,
            access = UserAccessToGroup.Updater(
                readInfo = readInfo,
                readMembers = readMembers,
                sentNotification = sentNotification,
            )
        )
            .onStart {
                reduce { state.copy(userAccessToGroupApplyingLoading = true) }
            }
            .onEach { group ->
                delay(2_000)
                reduce {
                    state.copy(
                        userAccessToGroupApplyingLoading = false,
                        userAccessToGroup = Resource.Data(group),
                    )
                }
            }
            .collect()
    }

    fun loadAccessToUser() = intent {
        val sourceUser = state.sourceUser ?: return@intent
        val targetUser = state.targetUser ?: return@intent
        profileRepository.getUserAccessToUser(
            fromProfileId = sourceUser.id,
            toProfileId = targetUser.id,
        )
            .asResource()
            .collect { reduce { state.copy(userAccessToUser = it) } }
    }

    fun loadAccessToGroup() = intent {
        val sourceUser = state.sourceUser ?: return@intent
        val targetGroup = state.targetGroup ?: return@intent
        profileRepository.getUserAccessToGroup(
            fromProfileId = sourceUser.id,
            toGroupId = targetGroup.id,
        )
            .asResource()
            .collect { reduce { state.copy(userAccessToGroup = it) } }
    }

}

@Composable
fun UserAccessForm(viewModel: UserAccessViewModel = viewModel()) = Form {
    val viewState: UserAccessViewState by viewModel.container.stateFlow.collectAsState()

    Column(
        modifier = Modifier.width(600.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SourceUserCard(
            sourceUser = viewState.sourceUser,
            onSelectUser = viewModel::setSourceUser,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TargetUserCard(
                modifier = Modifier.weight(1f),
                targetUser = viewState.targetUser,
                isApplyProcess = viewState.userAccessToUserApplyingLoading,
                access = viewState.userAccessToUser,
                onSelectTargetUser = viewModel::setTargetUser,
                onApply = viewModel::applyUserAccessToUser,
                onRetryLoadingAccess = viewModel::loadAccessToUser,
            )
            TargetGroupCard(
                modifier = Modifier.weight(1f),
                targetGroup = viewState.targetGroup,
                isApplyProcess = viewState.userAccessToGroupApplyingLoading,
                access = viewState.userAccessToGroup,
                onSelectTargetGroup = viewModel::setTargetGroup,
                onApply = viewModel::applyUserAccessToGroup,
                onRetryLoadingAccess = viewModel::loadAccessToGroup,
            )
        }
    }

}

@Composable
private fun SourceUserCard(
    sourceUser: ExtendedProfile?,
    onSelectUser: (ExtendedProfile) -> Unit,
) {
    var selection by remember { mutableStateOf(false) }
    if (selection) {
        UserPickerDialog(
            onCloseRequest = { selection = false },
            onSelectRequest = { selection = false; onSelectUser(it) }
        )
    }
    Card(
        elevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = "Настройка доступов для пользователя:",
                style = MaterialTheme.typography.h6,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (sourceUser == null) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { selection = true }
                ) {
                    Text("Выбрать")
                }
            } else {
                ProfileCard(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .wrapContentWidth(),
                    profile = sourceUser,
                    onClick = { selection = true }
                )
            }
        }
    }
}

@Composable
private fun TargetUserCard(
    modifier: Modifier = Modifier,
    targetUser: ExtendedProfile?,
    isApplyProcess: Boolean,
    access: Resource<UserAccessToUser>,
    onSelectTargetUser: (ExtendedProfile) -> Unit,
    onApply: (readProfile: Boolean, contactInfo: Boolean, sentNotification: Boolean) -> Unit,
    onRetryLoadingAccess: () -> Unit,
) {

    var targetUserSelection by remember { mutableStateOf(false) }
    if (targetUserSelection) {
        UserPickerDialog(
            onCloseRequest = { targetUserSelection = false },
            onSelectRequest = { targetUserSelection = false; onSelectTargetUser(it) }
        )
    }
    Card(
        modifier = modifier,
        elevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Доступы для пользователя:",
                style = MaterialTheme.typography.subtitle1,
            )
            if (targetUser == null) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { targetUserSelection = true }
                ) {
                    Text("Выбрать пользователя")
                }
            } else {
                ProfileCard(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    profile = targetUser,
                    onClick = { targetUserSelection = true }
                )
            }
            UserAccessToUserComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                access = access,
                isApplyProcess = isApplyProcess,
                onApply = onApply,
                onRetryLoadingAccess = onRetryLoadingAccess,
            )
        }
    }
}

@Composable
private fun UserAccessToUserComponent(
    modifier: Modifier = Modifier,
    access: Resource<UserAccessToUser>,
    isApplyProcess: Boolean,
    onApply: (readProfile: Boolean, contactInfo: Boolean, sentNotification: Boolean) -> Unit,
    onRetryLoadingAccess: () -> Unit,
) = when (access) {
    is Resource.Data -> {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start,
        ) {
            var readProfile by remember(access.value) { mutableStateOf(access.value.readProfile) }
            var contactInfo by remember(access.value) { mutableStateOf(access.value.contactInfo) }
            var sentNotification by remember(access.value) { mutableStateOf(access.value.sentNotification) }
            val isChanged by remember(access.value) {
                derivedStateOf {
                    readProfile != access.value.readProfile
                            || contactInfo != access.value.contactInfo
                            || sentNotification != access.value.sentNotification
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = readProfile,
                    onCheckedChange = { readProfile = it },
                )
                Text(text = "Профиль")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = contactInfo,
                    onCheckedChange = { contactInfo = it },
                )
                Text(text = "Контакты")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = sentNotification,
                    onCheckedChange = { sentNotification = it },
                )
                Text(text = "Оповещения")
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            ActionButton(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                text = "Применить",
                isEnabled = isChanged,
                isLoading = isApplyProcess,
                onClick = {
                    onApply(
                        readProfile,
                        sentNotification,
                        contactInfo,
                    )
                }
            )
        }
    }
    is Resource.Error -> {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Ошибка загрузки")
            Button(onClick = onRetryLoadingAccess) {
                Text("Повторить")
            }
        }
    }
    is Resource.Loading -> {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
    }
    is Resource.Undefined -> {
        Column(modifier = modifier) {}
    }
}

@Composable
private fun TargetGroupCard(
    modifier: Modifier = Modifier,
    targetGroup: GroupWithMembersIds?,
    isApplyProcess: Boolean,
    access: Resource<UserAccessToGroup>,
    onSelectTargetGroup: (GroupWithMembersIds) -> Unit,
    onApply: (readInfo: Boolean, readMembers: Boolean, sentNotification: Boolean,) -> Unit,
    onRetryLoadingAccess: () -> Unit,
) {

    var selection by remember { mutableStateOf(false) }
    if (selection) {
        GroupPickerDialog(
            onCloseRequest = { selection = false },
            onSelectRequest = { selection = false; onSelectTargetGroup(it) }
        )
    }
    Card(
        modifier = modifier,
        elevation = 6.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Доступы для группы:",
                style = MaterialTheme.typography.subtitle1,
            )
            if (targetGroup == null) {
                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = { selection = true }
                ) {
                    Text("Выбрать группу")
                }
            } else {
                GroupCard(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    group = targetGroup,
                    onClick = { selection = true },
                )
            }
            UserAccessToGroupComponent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                access = access,
                isApplyProcess = isApplyProcess,
                onApply = onApply,
                onRetryLoadingAccess = onRetryLoadingAccess,
            )
        }
    }
}

@Composable
private fun UserAccessToGroupComponent(
    modifier: Modifier = Modifier,
    isApplyProcess: Boolean,
    access: Resource<UserAccessToGroup>,
    onApply: (readInfo: Boolean, readMembers: Boolean, sentNotification: Boolean,) -> Unit,
    onRetryLoadingAccess: () -> Unit,
) = when (access) {
    is Resource.Data -> {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.Start,
        ) {
            var readInfo by remember(access.value) { mutableStateOf(access.value.readInfo) }
            var readMembers by remember(access.value) { mutableStateOf(access.value.readMembers) }
            var sentNotification by remember(access.value) { mutableStateOf(access.value.sentNotification) }
            val isChanged by remember(access.value) {
                derivedStateOf {
                    readInfo != access.value.readInfo
                            || readMembers != access.value.readMembers
                            || sentNotification != access.value.sentNotification
                }
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = readInfo,
                    onCheckedChange = { readInfo = it },
                )
                Text(text = "Информация")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = readMembers,
                    onCheckedChange = { readMembers = it },
                )
                Text(text = "Участники")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = sentNotification,
                    onCheckedChange = { sentNotification = it },
                )
                Text(text = "Оповещения")
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            ActionButton(
                modifier = Modifier.align(Alignment.CenterHorizontally).fillMaxWidth(0.75f),
                text = "Применить",
                isEnabled = isChanged,
                isLoading = isApplyProcess,
                onClick = {
                    onApply(
                        readInfo,
                        readMembers,
                        sentNotification,
                    )
                }
            )
        }
    }
    is Resource.Error -> {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("Ошибка загрузки")
            Button(onClick = onRetryLoadingAccess) {
                Text("Повторить")
            }
        }
    }
    is Resource.Loading -> {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
        }
    }
    is Resource.Undefined -> {
        Column(modifier = modifier) {}
    }
}