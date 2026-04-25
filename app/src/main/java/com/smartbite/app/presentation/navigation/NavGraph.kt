package com.smartbite.app.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.smartbite.app.AppState
import com.smartbite.app.presentation.screens.auth.*
import com.smartbite.app.presentation.screens.comparison.ComparisonScreen
import com.smartbite.app.presentation.screens.home.HomeScreen
import com.smartbite.app.presentation.screens.profile.ProfileScreen
import com.smartbite.app.presentation.screens.search.SearchScreen
import com.smartbite.app.presentation.screens.welcome.WelcomeScreen
import com.smartbite.app.presentation.screens.welcome.SuccessSplashScreen
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.smartbite.app.presentation.theme.PrimaryOrange
import com.smartbite.app.presentation.theme.TextGrey

sealed class Routes(val route: String) {
    object Welcome : Routes("welcome")
    object GuestName : Routes("guest_name")
    object Login : Routes("login/{fromRegister}") {
        fun createRoute(fromRegister: Boolean = false) = "login/$fromRegister"
    }
    object Register : Routes("register")
    object SuccessSplash : Routes("success_splash/{userName}") {
        fun createRoute(userName: String) = "success_splash/$userName"
    }
    object Home : Routes("home")
    object Search : Routes("search")
    object Profile : Routes("profile")
    object Comparison : Routes("comparison/{dishId}") {
        fun createRoute(dishId: Int) = "comparison/$dishId"
    }
}

@Composable
fun SmartBiteNavGraph() {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    val isSuccessSplash = currentRoute?.startsWith("success_splash") == true

    val showBottomNav = currentRoute in listOf(
        Routes.Home.route, Routes.Search.route, Routes.Profile.route
    )

    val startDest = remember {
        when {
            AppState.userRepository.isLoggedIn -> Routes.Home.route
            AppState.userRepository.guestName != null -> Routes.Home.route
            else -> Routes.Welcome.route
        }
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 600)) +
                        expandVertically(animationSpec = tween(800, delayMillis = 600)),
                exit = fadeOut(animationSpec = tween(300)) + 
                       shrinkVertically(animationSpec = tween(300))
            ) {
                SmartBiteBottomNav(navController, currentRoute)
            }
        },
        containerColor = if (isSuccessSplash) PrimaryOrange else MaterialTheme.colorScheme.background
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDest,
            modifier = Modifier.padding(if (showBottomNav) innerPadding else PaddingValues(0.dp)),
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            composable(Routes.Welcome.route) {
                WelcomeScreen(
                    onLoginClick = { navController.navigate(Routes.Login.createRoute()) },
                    onRegisterClick = { navController.navigate(Routes.Register.route) },
                    onGuestClick = { navController.navigate(Routes.GuestName.route) }
                )
            }
            composable(Routes.GuestName.route) {
                GuestNameScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        val name = AppState.userRepository.guestName ?: "Guest"
                        navController.navigate(Routes.SuccessSplash.createRoute(name)) {
                            popUpTo(Routes.Welcome.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(
                Routes.Login.route,
                arguments = listOf(navArgument("fromRegister") {
                    type = NavType.BoolType; defaultValue = false
                })
            ) { entry ->
                val fromReg = entry.arguments?.getBoolean("fromRegister") ?: false
                LoginScreen(
                    fromRegister = fromReg,
                    onBack = { navController.popBackStack() },
                    onLoginSuccess = {
                        val name = AppState.userRepository.currentUser?.name?.split(" ")?.first() ?: "there"
                        navController.navigate(Routes.SuccessSplash.createRoute(name)) {
                            popUpTo(Routes.Welcome.route) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.Register.route) },
                    onGuestClick = { navController.navigate(Routes.GuestName.route) }
                )
            }
            composable(Routes.Register.route) {
                RegisterScreen(
                    onBack = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate(Routes.Login.createRoute(fromRegister = true)) {
                            popUpTo(Routes.Register.route) { inclusive = true }
                        }
                    },
                    onLoginClick = { navController.popBackStack() }
                )
            }
            composable(
                Routes.SuccessSplash.route,
                arguments = listOf(navArgument("userName") { type = NavType.StringType }),
                exitTransition = {
                    fadeOut(animationSpec = tween(800)) + scaleOut(targetScale = 0.9f, animationSpec = tween(800))
                }
            ) { entry ->
                val name = entry.arguments?.getString("userName") ?: ""
                SuccessSplashScreen(userName = name, onFinished = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(entry.destination.route!!) { inclusive = true }
                    }
                })
            }
            composable(
                Routes.Home.route,
                enterTransition = {
                    fadeIn(animationSpec = tween(1000, delayMillis = 600)) +
                    slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(1000, delayMillis = 600))
                }
            ) {
                HomeScreen(
                    onDishClick = { dishId ->
                        navController.navigate(Routes.Comparison.createRoute(dishId))
                    },
                    onSearchClick = { navController.navigate(Routes.Search.route) }
                )
            }
            composable(Routes.Search.route) {
                SearchScreen(
                    onBack = { navController.popBackStack() },
                    onDishClick = { dishId ->
                        navController.navigate(Routes.Comparison.createRoute(dishId))
                    }
                )
            }
            composable(
                Routes.Comparison.route,
                arguments = listOf(navArgument("dishId") { type = NavType.IntType })
            ) { entry ->
                val dishId = entry.arguments?.getInt("dishId") ?: 0
                ComparisonScreen(
                    dishId = dishId,
                    onBack = { navController.popBackStack() },
                    onLoginRequired = { navController.navigate(Routes.Login.createRoute()) }
                )
            }
            composable(Routes.Profile.route) {
                ProfileScreen(
                    onLoginClick = { navController.navigate(Routes.Login.createRoute()) },
                    onRegisterClick = { navController.navigate(Routes.Register.route) },
                    onLogout = {
                        navController.navigate(Routes.Welcome.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SmartBiteBottomNav(navController: NavController, currentRoute: String?) {
    val items = listOf(
        Triple("home", "🏠", "Home") to Routes.Home.route,
        Triple("search", "🔍", "Search") to Routes.Search.route,
        Triple("profile", "👤", "Profile") to Routes.Profile.route
    )
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { (item, route) ->
            val (_, emoji, label) = item
            val selected = currentRoute == route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Text(
                        emoji,
                        fontSize = if (selected) 22.sp else 20.sp
                    )
                },
                label = {
                    Text(
                        label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryOrange,
                    selectedTextColor = PrimaryOrange,
                    unselectedIconColor = TextGrey,
                    unselectedTextColor = TextGrey,
                    indicatorColor = Color(0xFFFFF3EE)
                )
            )
        }
    }
}
