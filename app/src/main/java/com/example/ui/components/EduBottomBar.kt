package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.example.ui.viewmodel.MainNavTab

@Composable
fun EduBottomBar(
    currentDestination: NavDestination?,
    onTabSelected: (MainNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("edu_bottom_nav_bar"),
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        MainNavTab.bottomBarTabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == tab.route } == true

            val (filledIcon, outlinedIcon) = when (tab) {
                MainNavTab.HOME -> Icons.Filled.Home to Icons.Outlined.Home
                MainNavTab.LECTURES -> Icons.Filled.PlayCircle to Icons.Outlined.PlayCircleOutline
                MainNavTab.PRACTICE -> Icons.Filled.AssignmentTurnedIn to Icons.Outlined.Assignment
                MainNavTab.PROFILE -> Icons.Filled.Person to Icons.Outlined.PersonOutline
                MainNavTab.BATCHES -> Icons.Filled.School to Icons.Outlined.School
            }

            NavigationBarItem(
                modifier = Modifier.testTag("nav_tab_${tab.route}"),
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) filledIcon else outlinedIcon,
                        contentDescription = tab.label
                    )
                },
                label = {
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
