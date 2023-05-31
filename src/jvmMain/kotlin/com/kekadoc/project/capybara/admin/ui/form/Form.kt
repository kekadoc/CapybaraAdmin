package com.kekadoc.project.capybara.admin.ui.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun Form(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().then(modifier),
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints,
        content = content,
    )
}