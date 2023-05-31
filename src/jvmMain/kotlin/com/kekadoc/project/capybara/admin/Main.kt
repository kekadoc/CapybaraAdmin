package com.kekadoc.project.capybara.admin

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kekadoc.project.capybara.admin.di.DI
import com.kekadoc.project.capybara.admin.ui.AdminApp


fun main() = application {

    DI.init()

    Window(onCloseRequest = ::exitApplication) {
        AdminApp()
    }
}
