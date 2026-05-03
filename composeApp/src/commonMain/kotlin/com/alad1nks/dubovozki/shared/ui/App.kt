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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.designsystem.isTablet
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.shared.CommonModules
import com.alad1nks.dubovozki.shared.PlatformModules
import com.alad1nks.dubovozki.shared.navigation.AppNavHost
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    val appState = rememberAppState()

    KoinApplication(
        configuration =
            koinConfiguration {
                modules(CommonModules + PlatformModules)
            },
    ) {
        val viewModel =
            koinViewModel<MainViewModel>(
                parameters = { parametersOf() },
            )
        val darkTheme by viewModel.darkTheme.collectAsState(isSystemInDarkTheme())
        val language by viewModel.language.collectAsState(null)

        AppTheme(
            darkTheme = darkTheme,
        ) {
            CompositionLocalProvider(
                LocalAppLocale provides language?.code,
            ) {
                key(language) {
                    AppContent(appState = appState)
                }
            }
        }
    }
}

@Composable
private fun AppContent(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val isTablet = isTablet()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
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

            AppNavHost(appState = appState)
        }
    }
}
