package com.alad1nks.dubovozki.feature.services.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalLaundryService
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ServiceItem(
    onClick: () -> Unit,
    headlineText: String,
    supportingText: String,
    leadingImageVector: ImageVector,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = { Text(text = headlineText) },
            supportingContent = { Text(text = supportingText) },
            leadingContent = { Icon(imageVector = leadingImageVector, contentDescription = null) },
        )
    }
}

@Preview
@Composable
fun ServiceItemPreview() {
    ServiceItem(
        onClick = {},
        headlineText = "Расписание кастелянной",
        supportingText = "Выдача постельного белья",
        leadingImageVector = Icons.Outlined.LocalLaundryService,
    )
}
