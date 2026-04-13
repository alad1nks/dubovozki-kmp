package com.alad1nks.dubovozki.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.core.navigation.serialName
import com.alad1nks.dubovozki.feature.busschedule.navigation.BusScheduleRoute
import com.alad1nks.dubovozki.feature.home.navigation.HomeRoute
import com.alad1nks.dubovozki.feature.settings.navigation.SettingsRoute
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
    BUS_SCHEDULE(
        selectedIcon = Icons.Filled.DirectionsBus,
        unselectedIcon = Icons.Outlined.DirectionsBus,
        labelStringResource = AppResource.String.bus_schedule_tab_label,
        route = BusScheduleRoute,
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelStringResource = AppResource.String.settings_tab_label,
        route = SettingsRoute,
    ),
}

internal fun AppTopLevelDestination.routeSerialName(): String? {
    return when (this.route) {
        HomeRoute -> HomeRoute.serialName()
        BusScheduleRoute -> BusScheduleRoute.serialName()
        SettingsRoute -> SettingsRoute.serialName()
        else -> null
    }
}
