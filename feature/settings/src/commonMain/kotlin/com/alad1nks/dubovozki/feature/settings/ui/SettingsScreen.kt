package com.alad1nks.dubovozki.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SettingsRoute(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    SettingsScreen(
        uiState = uiState,
        onDarkThemeCheckedChange = viewModel::changeDarkTheme,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onDarkThemeCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SettingsTopAppBar()

        when (uiState) {
            is SettingsUiState.Loading -> {}

            is SettingsUiState.Content -> {
                SettingsContent(
                    darkTheme = uiState.darkTheme,
                    onDarkThemeCheckedChange = onDarkThemeCheckedChange,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopAppBar(
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        title = { Text(text = stringResource(AppResource.String.settings_top_app_bar)) },
        modifier = modifier,
    )
}

@Composable
private fun SettingsContent(
    darkTheme: Boolean,
    onDarkThemeCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SettingsSwitch(
            title = stringResource(AppResource.String.settings_dark_theme),
            checked = darkTheme,
            onCheckedChange = onDarkThemeCheckedChange,
        )
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    ListItem(
        modifier =
            modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = ripple(),
                ) { onCheckedChange(!checked) },
        headlineContent = { Text(text = title) },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
                interactionSource = interactionSource,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    var darkTheme by remember { mutableStateOf(false) }
    val uiState = SettingsUiState.Content(darkTheme = darkTheme)

    AppTheme(
        darkTheme = darkTheme,
    ) {
        SettingsScreen(
            uiState = uiState,
            onDarkThemeCheckedChange = { darkTheme = it },
        )
    }
}
