package com.alad1nks.dubovozki.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.core.navigation.serialName
import com.alad1nks.dubovozki.feature.collections.navigation.CollectionsRoute
import com.alad1nks.dubovozki.feature.home.navigation.HomeRoute
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.StringResource

internal enum class AppTopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelStringResource: StringResource,
    val route: Destination,
) {
    HOME(
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        labelStringResource = AppResource.String.home_tab_label,
        route = HomeRoute,
    ),
    COLLECTIONS(
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.Favorite,
        labelStringResource = AppResource.String.collections_tab_label,
        route = CollectionsRoute,
    ),
}

internal fun AppTopLevelDestination.routeSerialName(): String? {
    return when (this.route) {
        HomeRoute -> HomeRoute.serialName()
        CollectionsRoute -> CollectionsRoute.serialName()
        else -> null
    }
}
