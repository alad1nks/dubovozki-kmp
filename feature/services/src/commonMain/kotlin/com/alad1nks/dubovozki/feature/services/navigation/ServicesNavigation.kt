package com.alad1nks.dubovozki.feature.services.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.feature.services.ui.ServicesRoute
import com.alad1nks.dubovozki.feature.services.ui.ServicesViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object ServicesRoute : Destination

fun NavController.navigateToServices(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = ServicesRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.servicesScreen(navigateToServicesSchedule: (ServicesScheduleType) -> Unit) {
    composable<ServicesRoute> {
        val viewModel =
            koinViewModel<ServicesViewModel>(
                parameters = { parametersOf() },
            )

        ServicesRoute(
            viewModel = viewModel,
            navigateToServicesSchedule = navigateToServicesSchedule,
        )
    }
}
