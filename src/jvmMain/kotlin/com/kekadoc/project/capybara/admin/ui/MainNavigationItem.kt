package com.kekadoc.project.capybara.admin.ui

sealed interface MainMenu {

    val all: List<Item>
    val defaultItem: Item

    sealed interface Item {

        val subMenu: SubMenu

    }

}

enum class MainNavigationItem(override val subMenu: SubMenu) : MainMenu.Item {

    USERS(UsersNavigation),
    GROUPS(GroupsNavigation),
    MESSAGES(MessagesNavigation);

    companion object : MainMenu {

        override val all: List<MainMenu.Item> = MainNavigationItem.values().toList()

        override val defaultItem: MainMenu.Item = USERS

    }

}

sealed interface SubMenu {

    val all: List<Item>
    val defaultItem: Item

    sealed interface Item
}

enum class UsersNavigation : SubMenu.Item {

    GET_ALL,//Удаление, редактирование, Доступы к профилям и группам
    CREATE,//Парсинг файла
    REGISTRATIONS;

    companion object : SubMenu {

        override val all: List<SubMenu.Item> = UsersNavigation.values().toList()

        override val defaultItem: SubMenu.Item = GET_ALL

    }

}

enum class GroupsNavigation : SubMenu.Item {

    GET_ALL, //  Удаление группы Наполнение группы
    CREATE; //  Парсинг файла


    companion object : SubMenu {

        override val all: List<SubMenu.Item> = GroupsNavigation.values().toList()

        override val defaultItem: SubMenu.Item = GET_ALL

    }
}

enum class MessagesNavigation : SubMenu.Item {

    SENT,
    GET_ALL, //  Удаление
    CREATE; //  Парсинг файла

    companion object : SubMenu {

        override val all: List<SubMenu.Item> = MessagesNavigation.values().toList()

        override val defaultItem: SubMenu.Item = GET_ALL

    }

}
