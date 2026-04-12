package com.alad1nks.dubovozki.feature.collections.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.feature.collections.ui.CollectionsRoute
import com.alad1nks.dubovozki.feature.collections.ui.CollectionsViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data object CollectionsRoute : Destination

fun NavController.navigateToCollections(
    navOptions: NavOptionsBuilder.() -> Unit = {},
) {
    navigate(
        route = CollectionsRoute,
        builder = navOptions,
    )
}

fun NavGraphBuilder.collectionsScreen() {
    composable<CollectionsRoute> {
        val viewModel =
            koinViewModel<CollectionsViewModel>(
                parameters = { parametersOf() },
            )

        CollectionsRoute(
            viewModel = viewModel,
        )
    }
}
