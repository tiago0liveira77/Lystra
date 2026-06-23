package com.titos.lystra.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.titos.lystra.data.repository.ShoppingRepository
import com.titos.lystra.ui.screens.add.AddScreen
import com.titos.lystra.ui.screens.edit.EditScreen
import com.titos.lystra.ui.screens.history.HistoryScreen
import com.titos.lystra.ui.screens.list.ListScreen
import com.titos.lystra.ui.screens.profile.ProfileScreen

/**
 * Main navigation host for the Lystra app.
 * Uses slide + fade transitions between screens.
 */
@Composable
fun LystraNavHost(
    navController: NavHostController,
    repository: ShoppingRepository,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route,
        modifier = modifier,
        enterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }
        },
        exitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 }
        },
        popEnterTransition = {
            fadeIn(tween(300)) + slideInHorizontally(tween(300)) { -it / 4 }
        },
        popExitTransition = {
            fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { it / 4 }
        }
    ) {
        // Shopping List
        composable(Screen.List.route) {
            ListScreen(
                repository = repository,
                onNavigateToAdd = {
                    navController.navigate(Screen.Add.route) {
                        popUpTo(Screen.List.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onNavigateToEdit = { itemId ->
                    navController.navigate(Screen.Edit(itemId).route)
                }
            )
        }

        // Quick Add
        composable(Screen.Add.route) {
            AddScreen(
                repository = repository,
                onNavigateToEdit = { itemId ->
                    navController.navigate(Screen.Edit(itemId).route)
                }
            )
        }

        // History
        composable(Screen.History.route) {
            HistoryScreen(
                repository = repository
            )
        }

        // Profile
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        // Edit Item
        composable(
            route = Screen.Edit.ROUTE_TEMPLATE,
            arguments = listOf(
                navArgument(Screen.Edit.ARG_ITEM_ID) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString(Screen.Edit.ARG_ITEM_ID) ?: ""
            EditScreen(
                itemId = itemId,
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
