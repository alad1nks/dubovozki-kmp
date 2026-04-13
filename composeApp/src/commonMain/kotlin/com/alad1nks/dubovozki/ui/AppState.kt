package com.alad1nks.dubovozki.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alad1nks.dubovozki.feature.collections.navigation.navigateToCollections
import com.alad1nks.dubovozki.feature.home.navigation.navigateToHome
import com.alad1nks.dubovozki.navigation.AppTopLevelDestination
import com.alad1nks.dubovozki.navigation.AppTopLevelDestination.COLLECTIONS
import com.alad1nks.dubovozki.navigation.AppTopLevelDestination.HOME
import com.alad1nks.dubovozki.navigation.routeSerialName

@Composable
internal fun rememberAppState(
    navController: NavHostController = rememberNavController(),
): AppState {
    return remember(navController) {
        AppState(
            navController = navController,
        )
    }
}

internal class AppState(
    val navController: NavHostController,
) {
    val currentDestination: NavDestination? @Composable get() =
        navController.currentBackStackEntryAsState().value?.destination

    val appTopLevelDestinations: List<AppTopLevelDestination> = AppTopLevelDestination.entries

    val shouldShowBottomBar: Boolean @Composable get() {
        val currentDestinationRoute = currentDestination?.route
        return appTopLevelDestinations.any {
            it.routeSerialName() == currentDestinationRoute
        }
    }

    fun navigateToTopLevelDestination(appTopLevelDestination: AppTopLevelDestination) {
        val topLevelNavOptions: NavOptionsBuilder.() -> Unit = {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }

        when (appTopLevelDestination) {
            HOME -> navController.navigateToHome(topLevelNavOptions)
            COLLECTIONS -> navController.navigateToCollections(topLevelNavOptions)
        }
    }
}
