package com.alad1nks.dubovozki.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DropdownMenuItem
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
import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.feature.designsystem.component.Spinner
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.feature.settings.model.SettingsUiState
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.StringResource
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
        onLanguageSelect = viewModel::selectLanguage,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onDarkThemeCheckedChange: (Boolean) -> Unit,
    onLanguageSelect: (Language) -> Unit,
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
                    language = uiState.language,
                    onDarkThemeCheckedChange = onDarkThemeCheckedChange,
                    onLanguageSelect = onLanguageSelect,
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
    language: Language,
    onDarkThemeCheckedChange: (Boolean) -> Unit,
    onLanguageSelect: (Language) -> Unit,
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

        SettingsSpinnerLanguage(
            selectedLanguage = language,
            onLanguageSelect = onLanguageSelect,
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

@Composable
private fun SettingsSpinnerLanguage(
    selectedLanguage: Language,
    onLanguageSelect: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier,
        headlineContent = { Text(text = stringResource(AppResource.String.settings_language)) },
        trailingContent = {
            Spinner(
                expanded = expanded,
                content = { Text(text = stringResource(selectedLanguage.stringResource)) },
                dropdownMenuContent = {
                    Language.entries.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(text = stringResource(language.stringResource)) },
                            onClick =
                                {
                                    expanded = false
                                    onLanguageSelect(language)
                                },
                        )
                    }
                },
                onClick = { expanded = true },
                onDismissRequest = { expanded = false },
                fillMaxWidth = false,
            )
        },
    )
}

private val Language.stringResource: StringResource @Composable get() =
    when (this) {
        Language.SYSTEM -> AppResource.String.settings_language_system
        Language.ENGLISH -> AppResource.String.settings_language_english
        Language.RUSSIAN -> AppResource.String.settings_language_russian
        Language.KAZAKH -> AppResource.String.settings_language_kazakh
    }

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    var darkTheme by remember { mutableStateOf(false) }
    var language by remember { mutableStateOf(Language.SYSTEM) }
    val uiState =
        SettingsUiState.Content(
            darkTheme = darkTheme,
            language = language,
        )

    AppTheme(
        darkTheme = darkTheme,
    ) {
        SettingsScreen(
            uiState = uiState,
            onDarkThemeCheckedChange = { darkTheme = it },
            onLanguageSelect = { language = it },
        )
    }
}
