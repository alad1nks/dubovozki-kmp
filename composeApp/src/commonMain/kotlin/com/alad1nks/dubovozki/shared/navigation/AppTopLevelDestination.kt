package com.alad1nks.dubovozki.shared.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.HolidayVillage
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.alad1nks.dubovozki.core.navigation.Destination
import com.alad1nks.dubovozki.core.navigation.serialName
import com.alad1nks.dubovozki.feature.busschedule.navigation.BusScheduleRoute
import com.alad1nks.dubovozki.feature.services.navigation.ServicesRoute
import com.alad1nks.dubovozki.feature.settings.navigation.SettingsRoute
import com.alad1nks.dubovozki.feature.designsystem.TestTags
import com.alad1nks.dubovozki.resources.AppResource
import org.jetbrains.compose.resources.StringResource

internal enum class AppTopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelStringResource: StringResource,
    val route: Destination,
    val testTag: String,
) {
    SERVICES(
        selectedIcon = Icons.Filled.HolidayVillage,
        unselectedIcon = Icons.Outlined.HolidayVillage,
        labelStringResource = AppResource.String.services_navigation_bar_label,
        route = ServicesRoute,
        testTag = TestTags.NAV_SERVICES,
    ),
    BUS_SCHEDULE(
        selectedIcon = Icons.Filled.DirectionsBus,
        unselectedIcon = Icons.Outlined.DirectionsBus,
        labelStringResource = AppResource.String.bus_schedule_navigation_bar_label,
        route = BusScheduleRoute,
        testTag = TestTags.NAV_SCHEDULE,
    ),
    SETTINGS(
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Outlined.Settings,
        labelStringResource = AppResource.String.settings_navigation_bar_label,
        route = SettingsRoute,
        testTag = TestTags.NAV_SETTINGS,
    ),
}

internal fun AppTopLevelDestination.routeSerialName(): String? {
    return when (this.route) {
        ServicesRoute -> ServicesRoute.serialName()
        BusScheduleRoute -> BusScheduleRoute.serialName()
        SettingsRoute -> SettingsRoute.serialName()
        else -> null
    }
}
