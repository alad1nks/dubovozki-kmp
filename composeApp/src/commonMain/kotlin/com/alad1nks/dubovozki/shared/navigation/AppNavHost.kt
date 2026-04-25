package com.alad1nks.dubovozki.shared.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.alad1nks.dubovozki.feature.busschedule.navigation.BusScheduleRoute
import com.alad1nks.dubovozki.feature.busschedule.navigation.busScheduleScreen
import com.alad1nks.dubovozki.feature.services.navigation.servicesScreen
import com.alad1nks.dubovozki.feature.settings.navigation.settingsScreen
import com.alad1nks.dubovozki.shared.ui.AppState

@Composable
internal fun AppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier,
    startDestination: Any = BusScheduleRoute,
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
