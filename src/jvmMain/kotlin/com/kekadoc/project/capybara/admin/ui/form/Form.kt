package com.kekadoc.project.capybara.admin.ui.form

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kekadoc.project.capybara.admin.ui.kit.compose.andThen

@Composable
fun Form(
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.Center,
    propagateMinConstraints: Boolean = false,
    isRefreshEnabled: Boolean = true,
    onRefresh: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .andThen(modifier),
        contentAlignment = contentAlignment,
        propagateMinConstraints = propagateMinConstraints,
        content = {
            content.invoke(this)
            if (onRefresh != null) {
                IconButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = onRefresh,
                    enabled = isRefreshEnabled,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                    )
                }
            }
        },
    )
}