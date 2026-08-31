package com.alad1nks.dubovozki.feature.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alad1nks.dubovozki.core.model.Language
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.feature.designsystem.component.LoadingState
import com.alad1nks.dubovozki.feature.designsystem.component.Spinner
import com.alad1nks.dubovozki.feature.designsystem.TestTags
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
        onThemeModeSelect = viewModel::selectThemeMode,
        onLanguageSelect = viewModel::selectLanguage,
        modifier = modifier,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onLanguageSelect: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .widthIn(max = 720.dp),
        ) {
            SettingsTopAppBar()

            when (uiState) {
                is SettingsUiState.Loading ->
                    LoadingState(
                        message = stringResource(AppResource.String.common_loading),
                        modifier = Modifier.fillMaxSize(),
                    )
                is SettingsUiState.Content ->
                    SettingsContent(
                        themeMode = uiState.themeMode,
                        language = uiState.language,
                        onThemeModeSelect = onThemeModeSelect,
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
    themeMode: ThemeMode,
    language: Language,
    onThemeModeSelect: (ThemeMode) -> Unit,
    onLanguageSelect: (Language) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SettingsSpinner(
            title = stringResource(AppResource.String.settings_theme),
            selectedItem = themeMode,
            selectedText = stringResource(themeMode.stringResource),
            items = ThemeMode.entries,
            itemText = { stringResource(it.stringResource) },
            onSelect = onThemeModeSelect,
            testTag = TestTags.SETTINGS_THEME,
            itemTestTag = { TestTags.theme(it.name) },
        )
        SettingsSpinner(
            title = stringResource(AppResource.String.settings_language),
            selectedItem = language,
            selectedText = stringResource(language.stringResource),
            items = Language.entries,
            itemText = { stringResource(it.stringResource) },
            onSelect = onLanguageSelect,
            testTag = TestTags.SETTINGS_LANGUAGE,
            itemTestTag = { TestTags.language(it.name) },
        )
    }
}

@Composable
private fun <T> SettingsSpinner(
    title: String,
    selectedItem: T,
    selectedText: String,
    items: List<T>,
    itemText: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    testTag: String,
    itemTestTag: (T) -> String,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier.e2eTestTag(testTag).clickable { expanded = true },
        headlineContent = { Text(text = title) },
        trailingContent = {
            Spinner(
                expanded = expanded,
                content = { Text(text = selectedText) },
                dropdownMenuContent = {
                    items.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = itemText(item)) },
                            trailingIcon = {
                                if (item == selectedItem) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                    )
                                }
                            },
                            onClick = {
                                expanded = false
                                onSelect(item)
                            },
                            modifier = Modifier.e2eTestTag(itemTestTag(item)),
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

private val ThemeMode.stringResource: StringResource
    @Composable get() =
        when (this) {
            ThemeMode.SYSTEM -> AppResource.String.settings_theme_system
            ThemeMode.LIGHT -> AppResource.String.settings_theme_light
            ThemeMode.DARK -> AppResource.String.settings_theme_dark
        }

private val Language.stringResource: StringResource
    @Composable get() =
        when (this) {
            Language.SYSTEM -> AppResource.String.settings_language_system
            Language.ENGLISH -> AppResource.String.settings_language_english
            Language.RUSSIAN -> AppResource.String.settings_language_russian
            Language.KAZAKH -> AppResource.String.settings_language_kazakh
        }

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    AppTheme {
        SettingsScreen(
            uiState =
                SettingsUiState.Content(
                    themeMode = ThemeMode.SYSTEM,
                    language = Language.SYSTEM,
                ),
            onThemeModeSelect = {},
            onLanguageSelect = {},
        )
    }
}
