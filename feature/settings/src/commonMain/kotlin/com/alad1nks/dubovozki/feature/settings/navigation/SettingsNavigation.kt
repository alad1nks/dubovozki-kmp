package com.alad1nks.dubovozki.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.feature.settings.ui.SettingsRoute
import com.alad1nks.dubovozki.feature.settings.ui.SettingsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object SettingsRoute : Destination

fun NavController.navigateToSettings(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = SettingsRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.settingsScreen() {
    composable<SettingsRoute> {
        val viewModel =
            koinViewModel<SettingsViewModel>(
                parameters = { parametersOf() },
            )

        SettingsRoute(
            viewModel = viewModel,
        )
    }
}
