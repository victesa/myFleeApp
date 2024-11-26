package com.victorkirui.myfleeapp.nav

sealed class Routes(val route: String) {
    data object SignInScreen : Routes("signInScreen")
    data object SignUpScreen: Routes("signUpScreen")
    data object HomeScreen: Routes("HomeScreen")
    data object AuthenticationNavGraph: Routes("authenticationNavGraph")
    data object AuthenticatedNavGraph: Routes("authenticatedNavGraph")
    data object CartScreen: Routes("cartScreen")
    data object FavouritesScreen: Routes("favouritesScreen")
    data object ProfileScreen: Routes("profileScreen")
    data object ProductDetailsScreen:Routes("productDetailsScreen")
}
