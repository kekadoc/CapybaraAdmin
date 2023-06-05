package com.kekadoc.project.capybara.admin.ui.kit.compose

import androidx.compose.ui.Modifier

fun Modifier.andThen(modifier: Modifier) = modifier.then(this)