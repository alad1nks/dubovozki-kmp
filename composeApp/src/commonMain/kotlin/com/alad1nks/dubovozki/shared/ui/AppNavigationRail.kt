package com.alad1nks.dubovozki.shared.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import com.alad1nks.dubovozki.shared.navigation.AppTopLevelDestination
import com.alad1nks.dubovozki.shared.navigation.routeSerialName
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AppNavigationRail(
    appTopLevelDestinations: List<AppTopLevelDestination>,
    onNavigateToDestination: (AppTopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationRail(
        modifier = modifier,
    ) {
        appTopLevelDestinations.forEach { topLevelDestination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(topLevelDestination)

            AppNavigationRailItem(
                selected = selected,
                onClick = { onNavigateToDestination(topLevelDestination) },
                icon = {
                    Icon(
                        imageVector = topLevelDestination.unselectedIcon,
                        contentDescription = null,
                    )
                },
                selectedIcon = {
                    Icon(
                        imageVector = topLevelDestination.selectedIcon,
                        contentDescription = null,
                    )
                },
                label = { Text(stringResource(topLevelDestination.labelStringResource)) },
            )
        }
    }
}

@Composable
private fun AppNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
    )
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: AppTopLevelDestination) =
    this?.route == destination.routeSerialName()
