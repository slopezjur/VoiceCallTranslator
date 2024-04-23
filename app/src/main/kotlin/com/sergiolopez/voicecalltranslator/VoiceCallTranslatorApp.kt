package com.sergiolopez.voicecalltranslator

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.sergiolopez.voicecalltranslator.feature.call.ui.CallScreen
import com.sergiolopez.voicecalltranslator.feature.contactlist.ui.ContactListScreen
import com.sergiolopez.voicecalltranslator.feature.login.ui.LoginScreen
import com.sergiolopez.voicecalltranslator.feature.signup.ui.SignUpScreen
import com.sergiolopez.voicecalltranslator.feature.splash.ui.SplashScreen
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_DEFAULT_ID
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_ID
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_ID_ARG
import com.sergiolopez.voicecalltranslator.navigation.NavigationAction
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.navigation.NavigationState

@Composable
fun VoiceCallTranslatorApp() {
    val navigationState = rememberNavigationState()

    Scaffold { innerPaddingModifier ->
        NavHost(
            navController = navigationState.navController,
            startDestination = NavigationRoute.SPLASH.navigationName,
            modifier = Modifier.padding(innerPaddingModifier)
        ) {
            notesGraph(
                navigationState = navigationState
            )
        }
    }
}

@Composable
fun rememberNavigationState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        NavigationState(navController)
    }

fun NavGraphBuilder.notesGraph(
    navigationState: NavigationState
) {
    composable(NavigationAction.SplashNavigation.route) {
        SplashScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            }
        )
    }

    composable(NavigationAction.LoginNavigation.route) {
        LoginScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigate(
                    route = navigationParams.route
                )
            }
        )
    }

    composable(NavigationAction.SignUpNavigation.route) {
        SignUpScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            }
        )
    }

    composable(NavigationAction.ContactListNavigation.route) {
        ContactListScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigate(
                    route = navigationParams.route
                )
            }
        )
    }

    composable(
        route = "${NavigationAction.CallNavigation.route}$CALLEE_ID_ARG",
        arguments = listOf(navArgument(CALLEE_ID) { defaultValue = CALLEE_DEFAULT_ID })
    ) {
        CallScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            },
            calleeId = it.arguments?.getString(CALLEE_ID) ?: CALLEE_DEFAULT_ID
        )
    }
}