package com.kekadoc.project.capybara.admin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CircleNotifications
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.outlined.AppRegistration
import androidx.compose.material.icons.outlined.Ballot
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.common.viewmodel.ViewModel
import com.kekadoc.project.capybara.admin.common.viewmodel.viewModel
import com.kekadoc.project.capybara.admin.ui.form.auth.AuthContent
import com.kekadoc.project.capybara.admin.ui.form.groups.AllGroupsForm
import com.kekadoc.project.capybara.admin.ui.form.groups.CreateGroupsForm
import com.kekadoc.project.capybara.admin.ui.form.messages.AllMessagesForm
import com.kekadoc.project.capybara.admin.ui.form.messages.CreateMessageForm
import com.kekadoc.project.capybara.admin.ui.form.messages.SentMessagesForm
import com.kekadoc.project.capybara.admin.ui.form.users.AllUsersForm
import com.kekadoc.project.capybara.admin.ui.form.users.CreateUserForm
import com.kekadoc.project.capybara.admin.ui.form.users.RegistrationsUserForm
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce

data class MainViewState(
    val selectedMenuItem: MainMenu.Item? = null,
    val selectedSubMenu: SubMenu? = null,
    val selectedSubMenuItem: SubMenu.Item? = null,
)

class AdminAppViewModel : ViewModel<MainViewState>(MainViewState()) {

    init {
        setMenuItem(MainNavigationItem.USERS)
    }

    fun setMenuItem(item: MainMenu.Item?) = intent {
        reduce {
            state.copy(
                selectedMenuItem = item,
                selectedSubMenu = item?.subMenu,
                selectedSubMenuItem = item?.subMenu?.defaultItem,
            )
        }
    }

    fun setSubMenuItem(item: SubMenu.Item?) = intent {
        reduce {
            state.copy(
                selectedSubMenuItem = item,
            )
        }
    }

}

@Composable
fun AdminApp(viewModel: AdminAppViewModel = viewModel()) {
    val viewState by viewModel.container.stateFlow.collectAsState()
    val (selectedMenuItem, selectedSubMenu, selectedSubMenuItem) = viewState
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    ) {
        if (selectedMenuItem == null || selectedSubMenu == null || selectedSubMenuItem == null) {
            AuthContent()
        } else {
            AdminContent(
                selectedMenuItem = selectedMenuItem,
                selectedSubMenu = selectedSubMenu,
                selectedSubMenuItem = selectedSubMenuItem,
                onMenuSelected = viewModel::setMenuItem,
                onSubMenuSelected = viewModel::setSubMenuItem,
            )
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
) {
    Row(
        modifier = Modifier.fillMaxSize(),
    ) {
        MainMenu(
            modifier = Modifier.fillMaxHeight(),
            menu = MainNavigationItem,
            selectedMenu = selectedMenuItem,
            onSelected = onMenuSelected,
        )
        SubMenu(
            modifier = Modifier.fillMaxWidth(0.3f),
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
                MessagesNavigation.SENT -> SentMessagesForm()
                MessagesNavigation.GET_ALL -> AllMessagesForm()
                MessagesNavigation.CREATE -> CreateMessageForm()
                UsersNavigation.GET_ALL -> AllUsersForm()
                UsersNavigation.CREATE -> CreateUserForm()
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
                    MainNavigationItem.MESSAGES -> {
                        NavigationRailItem(
                            selected = selectedMenu.hashCode() == MainNavigationItem.MESSAGES.hashCode(),
                            onClick = { onSelected(MainNavigationItem.MESSAGES) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.CircleNotifications,
                                    contentDescription = null
                                )
                            },
                        )
                    }
                }
            }
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
                is MessagesNavigation.Companion -> {
                    "Информирования"
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
                    MessagesNavigation.SENT -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == MessagesNavigation.SENT.hashCode(),
                            icon = Icons.Outlined.Send,
                            text = "Отправленные",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    MessagesNavigation.GET_ALL -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == MessagesNavigation.GET_ALL.hashCode(),
                            icon = Icons.Outlined.Ballot,
                            text = "Все",
                            onClick = { onSelected.invoke(item) }
                        )
                    }
                    MessagesNavigation.CREATE -> {
                        SubMenuItemComponent(
                            enabled = true,
                            selected = selectedItem.hashCode() == MessagesNavigation.CREATE.hashCode(),
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