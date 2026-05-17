package com.alad1nks.dubovozki.feature.servicesschedule.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.alad1nks.dubovozki.core.model.ServicesScheduleType
import com.alad1nks.dubovozki.feature.servicesschedule.ui.ServicesScheduleRoute
import com.alad1nks.dubovozki.feature.servicesschedule.ui.ServicesScheduleViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class ServicesScheduleRoute(
    val type: ServicesScheduleType,
)

fun NavController.navigateToServicesSchedule(
    type: ServicesScheduleType,
    navOptions: NavOptions? = null,
) = navigate(ServicesScheduleRoute(type), navOptions)

fun NavGraphBuilder.servicesScheduleScreen(
    onBackClick: () -> Unit,
) {
    composable<ServicesScheduleRoute> { entry ->
        val servicesScheduleType = entry.toRoute<ServicesScheduleRoute>().type

        val viewModel =
            koinViewModel<ServicesScheduleViewModel>(
                parameters = { parametersOf(servicesScheduleType) },
            )

        ServicesScheduleRoute(
            viewModel = viewModel,
            onBackClick = onBackClick,
        )
    }
}
