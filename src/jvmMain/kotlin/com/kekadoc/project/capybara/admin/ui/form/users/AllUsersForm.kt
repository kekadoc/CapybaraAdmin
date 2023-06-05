package com.kekadoc.project.capybara.admin.ui.form.users

import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.domain.model.Range
import com.kekadoc.project.capybara.admin.domain.model.profile.ExtendedProfile
import com.kekadoc.project.capybara.admin.ui.form.Form
import com.kekadoc.project.capybara.admin.ui.form.common.DatabaseForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class AllUsersViewState(
    val isLoading: Boolean = true,
    val users: List<ExtendedProfile> = emptyList(),
    val editUser: ExtendedProfile? = null,
    val isBottomLoader: Boolean = false,
    val hasNext: Boolean = true,
    val query: String = "",
)

class AllUsersViewModel(
    private val usersRepository: ProfileRepository,
) : ViewModel<AllUsersViewState>(AllUsersViewState()) {

    init {
        intent {
            val users = usersRepository.getProfiles(
                Range(from = 0, count = 10, query = state.query)
            )
                .flowOn(Dispatchers.IO)
                .single()
            reduce {
                //println("LOADED ${users.size} ${users.size >= 10} ")
                state.copy(
                    isLoading = false,
                    users = state.users + users,
                    isBottomLoader = false,
                    hasNext = users.size >= 10,
                )
            }
        }
    }

    fun onNext(index: Int) = intent {
        println("onNext $index ${state.users.size} ${state.isBottomLoader} ${state.hasNext}")
        if (state.users.size - index < 10 && !state.isBottomLoader && state.hasNext) {
            reduce { state.copy(isBottomLoader = true) }
            val users = usersRepository.getProfiles(
                Range(from = state.users.size, count = 10, state.query)
            )
                .flowOn(Dispatchers.IO)
                .single()
            println("LOADED ${users.size} ${users.size >= 10} ")
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
        reduce { state.copy(isLoading = true) }
        val user = state.users[index]
        usersRepository.deleteProfile(user.id)
            .flowOn(Dispatchers.IO)
        reduce {
            state.copy(
                isLoading = false,
                users = state.users.toMutableList().apply { removeAt(index) },
            )
        }
    }

    fun edit(index: Int) = intent {
        reduce { state.copy(editUser = state.users[index]) }
    }

    fun updateUser(user: ExtendedProfile) = intent {
        val editUser = state.editUser ?: return@intent
        reduce { state.copy(isLoading = true) }
        if (editUser.type != user.type) {
            usersRepository.updateProfileType(editUser.id, user.type)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        if (editUser.status != user.status) {
            usersRepository.updateProfileStatus(editUser.id, user.status)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        if (editUser.status != user.status) {
            usersRepository.updateProfileStatus(editUser.id, user.status)
                .flowOn(Dispatchers.IO)
                .collect()
        }
        reduce { state.copy(isLoading = false) }
    }

    fun refresh() {

    }

}

@Composable
fun AllUsersForm(viewModel: AllUsersViewModel = viewModel()) = Form(
    isRefreshEnabled = true,
    onRefresh = { viewModel.refresh() }
) {
    val viewState: AllUsersViewState by viewModel.container.stateFlow.collectAsState()

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.body1) {
        DatabaseForm(
            modifier = Modifier
                .align(Alignment.Center)
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
            onDelete = {},
            onEdit = {},
            isLoading = viewState.isLoading,
        )
    }

}