package com.alad1nks.dubovozki.feature.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.feature.designsystem.TestTags

@Composable
fun LoadingState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.testTag(TestTags.COMMON_LOADING),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        CircularProgressIndicator()
        Text(text = message, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun MessageState(
    title: String,
    supportingText: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    stateTestTag: String = TestTags.COMMON_ERROR,
    actionTestTag: String = TestTags.COMMON_RETRY,
) {
    Column(
        modifier = modifier.testTag(stateTestTag).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        Text(
            text = supportingText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
        Button(
            onClick = onAction,
            modifier = Modifier.testTag(actionTestTag),
        ) {
            Text(text = actionLabel)
        }
    }
}

@Composable
fun OfflineBanner(
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.testTag(TestTags.COMMON_OFFLINE).fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
            )
            TextButton(onClick = onAction) {
                Text(text = actionLabel)
            }
        }
    }
}
