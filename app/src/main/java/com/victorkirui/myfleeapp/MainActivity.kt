package com.victorkirui.myfleeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.victorkirui.myfleeapp.nav.AppNav
import com.victorkirui.myfleeapp.ui.theme.MyFleeAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            installSplashScreen()
                .setKeepVisibleCondition { viewModel.isLoading.value }

            MyFleeAppTheme {
                val systemUiController = rememberSystemUiController()
                systemUiController.isNavigationBarVisible = false // Customize if needed

                // Observe first screen (where the app should start)
                val firstScreen = viewModel.firstScreen.collectAsState()

                // Main App Navigation
                AppNav(
                    firstScreen = firstScreen.value
                )
            }
        }
    }

}











data class NavigationItem(val label: String, val unselectedIcon: ImageVector, val selectedIcon: ImageVector, val route: String)