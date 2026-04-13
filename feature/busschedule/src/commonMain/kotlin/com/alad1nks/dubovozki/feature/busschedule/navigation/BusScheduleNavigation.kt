package com.alad1nks.dubovozki.feature.busschedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.feature.busschedule.ui.BusScheduleRoute
import com.alad1nks.dubovozki.feature.busschedule.ui.BusScheduleViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object BusScheduleRoute : Destination

fun NavController.navigateToBusSchedule(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = BusScheduleRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.busScheduleScreen() {
    composable<BusScheduleRoute> {
        val viewModel =
            koinViewModel<BusScheduleViewModel>(
                parameters = { parametersOf() },
            )

        BusScheduleRoute(
            viewModel = viewModel,
        )
    }
}
