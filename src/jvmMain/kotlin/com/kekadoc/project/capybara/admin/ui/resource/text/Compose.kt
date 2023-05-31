package com.kekadoc.project.capybara.admin.ui.resource.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.kekadoc.project.capybara.admin.ui.resource.text.locales.RuText

private val LocalText = staticCompositionLocalOf { RuText }

val text: Text
    @Composable get() = LocalText.current