package com.kekadoc.project.capybara.admin.ui.kit.compose

import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
@NonRestartableComposable
fun Iconss(
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
) {
    Icon(
        imageVector = imageVector,
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}