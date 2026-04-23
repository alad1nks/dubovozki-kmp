package com.alad1nks.dubovozki.shared.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import com.alad1nks.dubovozki.shared.navigation.AppTopLevelDestination
import com.alad1nks.dubovozki.shared.navigation.routeSerialName
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun AppBottomBar(
    appTopLevelDestinations: List<AppTopLevelDestination>,
    onNavigateToDestination: (AppTopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
    ) {
        appTopLevelDestinations.forEach { topLevelDestination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(topLevelDestination)

            AppNavigationBarItem(
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
private fun RowScope.AppNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
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
