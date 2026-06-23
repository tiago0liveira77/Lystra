package com.titos.lystra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.titos.lystra.data.repository.MockShoppingRepository
import com.titos.lystra.ui.components.BottomNavItem
import com.titos.lystra.ui.components.LystraBottomBar
import com.titos.lystra.ui.components.LystraTopBar
import com.titos.lystra.ui.navigation.LystraNavHost
import com.titos.lystra.ui.navigation.Screen
import com.titos.lystra.ui.theme.LystraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LystraTheme {
                LystraApp()
            }
        }
    }
}

@Composable
fun LystraApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.List.route

    // Determine if we're on a "detail" screen (hide top/bottom bars)
    val isDetailScreen = currentRoute.startsWith("edit/")

    // Use MockShoppingRepository for UI development.
    // Replace with FirestoreShoppingRepository() once google-services.json is added.
    val repository = remember { MockShoppingRepository() }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            if (!isDetailScreen) {
                LystraTopBar()
            }
        },
        bottomBar = {
            if (!isDetailScreen) {
                LystraBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { item ->
                        if (item.route != currentRoute) {
                            navController.navigate(item.route) {
                                // Pop up to the start destination to avoid building up
                                // a large back stack of bottom nav destinations
                                popUpTo(Screen.List.route) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LystraNavHost(
                navController = navController,
                repository = repository,
            )
        }
    }
}