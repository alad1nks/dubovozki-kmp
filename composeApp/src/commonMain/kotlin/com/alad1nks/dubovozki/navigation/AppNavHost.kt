package com.alad1nks.dubovozki.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.alad1nks.dubovozki.feature.busschedule.navigation.busScheduleScreen
import com.alad1nks.dubovozki.feature.services.navigation.ServicesRoute
import com.alad1nks.dubovozki.feature.services.navigation.servicesScreen
import com.alad1nks.dubovozki.feature.settings.navigation.settingsScreen
import com.alad1nks.dubovozki.ui.AppState

@Composable
internal fun AppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier,
    startDestination: Any = ServicesRoute,
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        servicesScreen()
        busScheduleScreen()
        settingsScreen()
    }
}
