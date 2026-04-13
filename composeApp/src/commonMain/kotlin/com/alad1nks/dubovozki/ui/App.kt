package com.alad1nks.dubovozki.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.alad1nks.dubovozki.feature.designsystem.theme.AppTheme
import com.alad1nks.dubovozki.getCommonModules
import com.alad1nks.dubovozki.getPlatformModules
import com.alad1nks.dubovozki.navigation.AppNavHost
import org.koin.compose.KoinApplication

@Composable
fun App() {
    val appState = rememberAppState()

    KoinApplication(
        application = {
            modules(getPlatformModules() + getCommonModules())
        },
    ) {
        AppTheme {
            AppContent(appState = appState)
        }
    }
}

@Composable
private fun AppContent(
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar =
            {
                if (appState.shouldShowBottomBar) {
                    AppBottomBar(
                        appTopLevelDestinations = appState.appTopLevelDestinations,
                        onNavigateToDestination = appState::navigateToTopLevelDestination,
                        currentDestination = appState.currentDestination,
                    )
                }
            },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        AppNavHost(
            appState = appState,
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        )
    }
}
