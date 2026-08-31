package com.alad1nks.dubovozki.shared.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.core.model.ThemeMode
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.feature.designsystem.e2eTestTag
import com.alad1nks.dubovozki.feature.designsystem.isTablet
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.shared.CommonModules
import com.alad1nks.dubovozki.shared.PlatformModules
import com.alad1nks.dubovozki.shared.navigation.AppNavHost
import org.koin.compose.KoinApplication
import org.koin.compose.KoinIsolatedContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinConfiguration
import org.koin.core.KoinApplication as CoreKoinApplication

@Composable
fun App(
    modules: List<Module> = CommonModules + PlatformModules,
    isolatedKoinApplication: CoreKoinApplication? = null,
) {
    val appState = rememberAppState()

    if (isolatedKoinApplication == null) {
        KoinApplication(
            configuration =
                koinConfiguration {
                    allowOverride(true)
                    modules(modules)
                },
        ) {
            AppWithDependencies(appState)
        }
    } else {
        KoinIsolatedContext(context = isolatedKoinApplication) {
            AppWithDependencies(appState)
        }
    }
}

@Composable
private fun AppWithDependencies(appState: AppState) {
    val viewModel =
        koinViewModel<MainViewModel>(
            parameters = { parametersOf() },
        )
    val themeMode by viewModel.themeMode.collectAsState(ThemeMode.SYSTEM)
    val language by viewModel.language.collectAsState(null)
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkTheme =
        when (themeMode) {
            ThemeMode.SYSTEM -> systemInDarkTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

    AppTheme(darkTheme = darkTheme) {
        CompositionLocalProvider(LocalAppLocale provides language?.code) {
            key(language) {
                AppContent(
                    appState = appState,
                    darkTheme = darkTheme,
                )
            }
        }
    }
}

@Composable
private fun AppContent(
    appState: AppState,
    darkTheme: Boolean,
    modifier: Modifier = Modifier,
) {
    val isTablet = isTablet()
    Scaffold(
        modifier = modifier.e2eTestTag(TestTags.APP_CONTENT),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar =
            {
                if (appState.shouldShowNavigationBar && !isTablet) {
                    AppNavigationBar(
                        appTopLevelDestinations = appState.appTopLevelDestinations,
                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                        currentDestination = appState.currentDestination,
                    )
                }
            },
    ) { padding ->
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .e2eTestTag(TestTags.appTheme(darkTheme))
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        ) {
            if (appState.shouldShowNavigationBar && isTablet) {
                AppNavigationRail(
                    appTopLevelDestinations = appState.appTopLevelDestinations,
                    onNavigateToDestination = appState::navigateToTopLevelDestination,
                    currentDestination = appState.currentDestination,
                )
            }

            AppNavHost(
                appState = appState,
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxSize(),
            )
        }
    }
}
