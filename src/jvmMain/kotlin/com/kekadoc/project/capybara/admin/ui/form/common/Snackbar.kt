package com.kekadoc.project.capybara.admin.ui.form.common

import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf

val LocalSanckBarHostState = compositionLocalOf { SnackbarHostState() }