package com.example.vault

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.vault.ui.NewTransactionScreen
import com.example.vault.ui.HomeScreen
import com.example.vault.ui.InsightsScreen
import com.example.vault.ui.theme.VaultNavIndicator
import com.example.vault.ui.theme.VaultNavMuted
import com.example.vault.ui.theme.VaultNavy
import com.example.vault.ui.theme.VaultSurfaceWhite
import com.example.vault.utils.Screen
import com.example.vault.viewmodel.TransactionViewModel

@Composable
fun VaultApp(viewModel: TransactionViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel)
            }
            composable(Screen.Add.route) {
                NewTransactionScreen(
                    viewModel = viewModel,
                    navController = navController
                )
            }
            composable(Screen.Insights.route) {
                InsightsScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screen.Home,
        Screen.Add,
        Screen.Insights
    )

    NavigationBar(
        containerColor = VaultSurfaceWhite,
        contentColor = VaultNavy
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val selected =
                currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(screen.icon),
                        contentDescription = stringResource(screen.titleRes),
                        tint = if (selected) VaultNavy else VaultNavMuted
                    )
                },
                label = {
                    Text(
                        text = stringResource(screen.titleRes).uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = if (selected) VaultNavy else VaultNavMuted
                    )
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VaultNavy,
                    selectedTextColor = VaultNavy,
                    indicatorColor = VaultNavIndicator,
                    unselectedIconColor = VaultNavMuted,
                    unselectedTextColor = VaultNavMuted
                ),
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
