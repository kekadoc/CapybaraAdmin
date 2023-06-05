package com.kekadoc.project.capybara.admin

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.kekadoc.project.capybara.admin.di.DI
import com.kekadoc.project.capybara.admin.ui.AdminApp
import com.kekadoc.project.capybara.admin.ui.theme.AppTheme

fun main() = application {
    System.setProperty("skiko.renderApi", "OPENGL")
    Thread.setDefaultUncaughtExceptionHandler { t, e -> e.printStackTrace() }
    DI.init()
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            width = 1280.dp,
            height = 720.dp,
        ),
    ) {
        AppTheme {
            AdminApp()
        }
    }
}
