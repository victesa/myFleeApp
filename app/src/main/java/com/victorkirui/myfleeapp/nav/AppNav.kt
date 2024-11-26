package com.victorkirui.myfleeapp.nav

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.victorkirui.myfleeapp.NavigationItem
import com.victorkirui.myfleeapp.ui.profile.ProfileScreen
import com.victorkirui.myfleeapp.ui.cart.CartScreen
import com.victorkirui.myfleeapp.ui.productDetails.DetailedProductScreen
import com.victorkirui.myfleeapp.ui.authentication.SignInScreen
import com.victorkirui.myfleeapp.ui.authentication.SignUpScreen
import com.victorkirui.myfleeapp.ui.favourites.FavouritesScreen
import com.victorkirui.myfleeapp.ui.home.HomeScreen

@Composable
fun AppNav(firstScreen: String) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = firstScreen
    ) {
        authenticationGraph(
            navigateToSignUpScreen = { navController.navigate(Routes.SignUpScreen.route) },
            navigateToSignInScreen = { navController.popBackStack() },
            navigateToHomeScreen = {
                navController.navigate(Routes.AuthenticatedNavGraph.route) {
                    popUpTo(Routes.AuthenticationNavGraph.route) {
                        inclusive = true
                    }
                }
            }
        )
        authenticatedGraph(
            navigateToSelectedScreen = {
                navController.navigate(it) {
                    launchSingleTop = true
                    restoreState = true
                }
            },
            navigateToProductDetailsScreen = {
                navController.navigate(Routes.ProductDetailsScreen.route + "/$it")
            },
            navigateBackToHomeScreen = {
                navController.navigate(Routes.HomeScreen.route){
                    popUpTo(Routes.HomeScreen.route) {
                        inclusive = true
                    }
                }
            },
            navigateToAuthenticationGraph = {
                navController.navigate(Routes.AuthenticationNavGraph.route) {
                    popUpTo(Routes.AuthenticatedNavGraph.route) {
                        inclusive = true
                    }
                }
            }

        )
    }
}

fun NavGraphBuilder.authenticationGraph(
    navigateToSignUpScreen: () -> Unit,
    navigateToSignInScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit
) {
    navigation(startDestination = Routes.SignInScreen.route, route = Routes.AuthenticationNavGraph.route) {
        composable(route = Routes.SignInScreen.route) {
            SignInScreen(
                navigateToSignUpScreen = { navigateToSignUpScreen() },
                navigateToHomeScreen = {
                    navigateToHomeScreen()
                }
            )
        }

        composable(route = Routes.SignUpScreen.route) {
            SignUpScreen(
                navigateToSignInScreen = { navigateToSignInScreen() },
                navigateToHomeScreen = navigateToHomeScreen
            )
        }
    }
}

fun NavGraphBuilder.authenticatedGraph(
    navigateToSelectedScreen: (String) -> Unit,
    navigateToProductDetailsScreen: (String) -> Unit,
    navigateBackToHomeScreen: () -> Unit,
    navigateToAuthenticationGraph: () -> Unit
) {
    val navItems = listOf(
        NavigationItem("Home", Icons.Outlined.Home, Icons.Filled.Home, Routes.HomeScreen.route),
        NavigationItem("Cart", Icons.Outlined.ShoppingCart, Icons.Filled.ShoppingCart, Routes.CartScreen.route),
        NavigationItem("Favourites", Icons.Outlined.FavoriteBorder, Icons.Filled.Favorite, Routes.FavouritesScreen.route),
        NavigationItem("Profile", Icons.Outlined.Person, Icons.Filled.Person, Routes.ProfileScreen.route)
    )

    navigation(
        startDestination = Routes.HomeScreen.route,
        route = Routes.AuthenticatedNavGraph.route
    ) {
        composable(route = Routes.HomeScreen.route) {
            ScreenWithBottomNavBar(
                currentRoute = Routes.HomeScreen.route,
                navItems = navItems,
                navigateToSelectedScreen = navigateToSelectedScreen
            ) {
                HomeScreen(navigateToProductDetailsScreen = navigateToProductDetailsScreen,
                    padding = it)
            }
        }

        composable(route = Routes.CartScreen.route) {
            CartScreen(navigateBackToHomeScreen)
        }

        composable(route = Routes.FavouritesScreen.route) {
            ScreenWithBottomNavBar(
                currentRoute = Routes.FavouritesScreen.route,
                navItems = navItems,
                navigateToSelectedScreen = navigateToSelectedScreen
            ) {
                FavouritesScreen()
            }
        }

        composable(route = Routes.ProfileScreen.route) {
            ProfileScreen(navigateToAuthenticationGraph = {navigateToAuthenticationGraph()},
                navigateBackToHomeScreen = navigateBackToHomeScreen)
        }

        composable(route = Routes.ProductDetailsScreen.route + "/{productId}",
            arguments = listOf(navArgument("productId"){type = NavType.StringType})){backStackEntry->
                val product = backStackEntry.arguments!!.getString("productId")
            DetailedProductScreen(productId = product!!, navigateBackToHomeScreen = {
                navigateBackToHomeScreen()
            })
        }
    }
}


//Functions for adding a bottom nav bar to the right screens
@Composable
fun ScreenWithBottomNavBar(
    currentRoute: String,
    navItems: List<NavigationItem>,
    navigateToSelectedScreen: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            if (currentRoute in navItems.map {
                it.route }) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    navItems = navItems,
                    navigateToSelectedScreen = navigateToSelectedScreen
                )
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    navItems: List<NavigationItem>,
    navigateToSelectedScreen: (String) -> Unit
) {
    NavigationBar {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navigateToSelectedScreen(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}
