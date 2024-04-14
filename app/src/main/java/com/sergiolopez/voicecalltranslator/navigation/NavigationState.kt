package com.sergiolopez.voicecalltranslator.navigation

import androidx.navigation.NavHostController

class NavigationState(val navController: NavHostController) {

    fun popBackStack() {
        navController.popBackStack()
    }

    fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    fun navigateAndPopUp(navigationParams: NavigationParams) {
        navController.navigate(navigationParams.route) {
            launchSingleTop = true
            popUpTo(navigationParams.popUp) { inclusive = true }
        }
    }

    fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }
}
