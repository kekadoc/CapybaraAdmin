package com.kekadoc.project.capybara.admin.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.SettingsAccessibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.data.repository.auth.AuthRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.ProfileRepository
import com.kekadoc.project.capybara.admin.data.repository.profile.UserStatus
import com.kekadoc.project.capybara.admin.domain.model.profile.AuthorizedProfile
import com.kekadoc.project.capybara.admin.ui.form.auth.AuthContent
import com.kekadoc.project.capybara.admin.ui.form.common.LocalSanckBarHostState
import com.kekadoc.project.capybara.admin.ui.form.groups.AllGroupsForm
import com.kekadoc.project.capybara.admin.ui.form.groups.CreateGroupsForm
import com.kekadoc.project.capybara.admin.ui.form.users.AllUsersForm
import com.kekadoc.project.capybara.admin.ui.form.users.CreateUserForm
import com.kekadoc.project.capybara.admin.ui.form.users.RegistrationsUserForm
import com.kekadoc.project.capybara.admin.ui.form.users.UserAccessForm
import kotlinx.coroutines.flow.*
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class MainViewState(
    val isLoading: Boolean = true,
    val selectedMenuItem: MainMenu.Item = MainNavigationItem.USERS,
    val selectedSubMenu: SubMenu = selectedMenuItem.subMenu,
    val selectedSubMenuItem: SubMenu.Item = selectedSubMenu.defaultItem,
    val authorizedProfile: AuthorizedProfile? = null,
)

class AdminAppViewModel(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel<MainViewState>(MainViewState()) {

    init {
        intent {
            profileRepository.currentProfile
                .onStart { reduce { state.copy(isLoading = true) } }
                .filter { it !is UserStatus.Unknown }
                .map { (it as? UserStatus.Authorized)?.user }
                .collect { reduce { state.copy(isLoading = false, authorizedProfile = it) } }
        }
    }

    fun setMenuItem(item: MainMenu.Item) = intent {
        if (state.selectedMenuItem == item) return@intent
        reduce {
            state.copy(
                selectedMenuItem = item,
                selectedSubMenu = item.subMenu,
                selectedSubMenuItem = item.subMenu.defaultItem,
            )
        }
    }

    fun setSubMenuItem(item: SubMenu.Item) = intent {
        if (state.selectedSubMenuItem == item) return@intent
        reduce {
            state.copy(
                selectedSubMenuItem = item,
            )
        }
    }

    fun logout() = intent {
        authRepository.logout().collect()
        profileRepository.getProfile().collect()
    }

}

@Composable
fun AdminApp(viewModel: AdminAppViewModel = viewModel()) {
    val viewState by viewModel.container.stateFlow.collectAsState()
    val (isLoading, selectedMenuItem, selectedSubMenu, selectedSubMenuItem, profile) = viewState
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        snackbarHost = { state ->
            SnackbarHost(
                hostState = state,
                modifier = Modifier.wrapContentWidth(),
                snackbar = { snackbarData ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Snackbar(
                            snackbarData = snackbarData,
                            modifier = Modifier.width(200.dp),
                        )
                    }
                }
            )
        },
    ) {
        CompositionLocalProvider(
            LocalSanckBarHostState provides scaffoldState.snackbarHostState,
        ) {
            Box(
                Modifier.fillMaxSize().padding(it),
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (profile == null) {
                    AuthContent()
                } else {
                    AdminContent(
                        selectedMenuItem = selectedMenuItem,
                        selectedSubMenu = selectedSubMenu,
                        selectedSubMenuItem = selectedSubMenuItem,
                        onMenuSelected = viewModel::setMenuItem,
                        onSubMenuSelected = viewModel::setSubMenuItem,
                        onLogout = viewModel::logout,
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminContent(
    selectedMenuItem: MainMenu.Item,
    selectedSubMenu: SubMenu,
    selectedSubMenuItem: SubMenu.Item,
    onMenuSelected: (MainMenu.Item) -> Unit,
    onSubMenuSelected: (SubMenu.Item) -> Unit,
    onLogout: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        MainMenu(
            modifier = Modifier.fillMaxHeight(),
            menu = MainNavigationItem,
            selectedMenu = selectedMenuItem,
            onSelected = onMenuSelected,
            onLogout = onLogout,
        )
        SubMenu(
            modifier = Modifier.width(200.dp),
            menu = selectedSubMenu,
            selectedItem = selectedSubMenuItem,
            onSelected = onSubMenuSelected,
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            when (selectedSubMenuItem) {
                GroupsNavigation.GET_ALL -> AllGroupsForm()
                GroupsNavigation.CREATE -> CreateGroupsForm()
                UsersNavigation.GET_ALL -> AllUsersForm()
                UsersNavigation.CREATE -> CreateUserForm()
                UsersNavigation.ACCESS -> UserAccessForm()
                UsersNavigation.REGISTRATIONS -> RegistrationsUserForm()
            }
        }
    }
}

@Composable
private fun MainMenu(
    modifier: Modifier,
    menu: MainMenu,
    selectedMenu: MainMenu.Item,
    onSelected: (MainMenu.Item) -> Unit,
    onLogout: () -> Unit
) {
    NavigationRail(
        modifier = modifier,
        content = {
            MainNavigationItem.values().forEach { menuItem ->
                when (menuItem) {
                    MainNavigationItem.USERS -> {
                        NavigationRailItem(
                            selected = selectedMenu.hashCode() == MainNavigationItem.USERS.hashCode(),
                            onClick = { onSelected(MainNavigationItem.USERS) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.SupervisedUserCircle,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                    MainNavigationItem.GROUPS -> {
                        NavigationRailItem(
                            selected = selectedMenu.hashCode() == MainNavigationItem.GROUPS.hashCode(),
                            onClick = { onSelected(MainNavigationItem.GROUPS) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            NavigationRailItem(
                selected = false,
                onClick = { onLogout() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null
                    )
                },
            )
        }
    )
}

@Composable
private fun SubMenu(
    modifier: Modifier,
    menu: SubMenu,
    selectedItem: SubMenu.Item,
    onSelected: (SubMenu.Item) -> Unit,
) {
    Card(
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxHeight().padding(16.dp).then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val header: String = when (menu) {
                is GroupsNavigation.Companion -> {
                    "Группы"
                }
                is UsersNavigation.Companion -> {
                    "Пользователи"
                }
                else -> throw RuntimeException("Unknown menu")
            }
            Text(
                modifier = Modifier.padding(16.dp),
                text = header,
                style = MaterialTheme.typography.h6,
            )
            menu.all.forEach { item ->
                when (item) {
                    GroupsNavigation.GET_ALL -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == GroupsNavigation.GET_ALL.hashCode(),
                            icon = Icons.Outlined.Ballot,
                            text = "Все",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    GroupsNavigation.CREATE -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == GroupsNavigation.CREATE.hashCode(),
                            icon = Icons.Outlined.Create,
                            text = "Создать",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    UsersNavigation.GET_ALL -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == UsersNavigation.GET_ALL.hashCode(),
                            icon = Icons.Outlined.Ballot,
                            text = "Все",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    UsersNavigation.CREATE -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == UsersNavigation.CREATE.hashCode(),
                            icon = Icons.Outlined.Create,
                            text = "Создать",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    UsersNavigation.REGISTRATIONS -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == UsersNavigation.REGISTRATIONS.hashCode(),
                            icon = Icons.Outlined.AppRegistration,
                            text = "Регистрации",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    UsersNavigation.ACCESS -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == UsersNavigation.ACCESS.hashCode(),
                            icon = Icons.Outlined.SettingsAccessibility,
                            text = "Доступы",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubMenuItemComponent(
    enabled: Boolean,
    selected: Boolean,
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        enabled = enabled,
        border = BorderStroke(
            width = 4.dp,
            color = if (selected) {
                MaterialTheme.colors.onSurface.copy(alpha = 0.33f)
            } else {
                Color.Transparent
            }
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Text(text = text)
        }
    }
}