package com.sergiolopez.voicecalltranslator

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sergiolopez.voicecalltranslator.login.ui.LoginScreen
import com.sergiolopez.voicecalltranslator.navigation.NavigationAction
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.navigation.NavigationState
import com.sergiolopez.voicecalltranslator.signup.ui.SignUpScreen
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorTheme

@Composable
fun VoiceCallTranslatorApp() {
    VoiceCallTranslatorTheme {
        Surface {
            val navigationState = rememberNavigationState()

            Scaffold { innerPaddingModifier ->
                NavHost(
                    navController = navigationState.navController,
                    startDestination = NavigationRoute.LOGIN.navigationName,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    notesGraph(navigationState)
                }
            }
        }
    }
}

@Composable
fun rememberNavigationState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        NavigationState(navController)
    }

fun NavGraphBuilder.notesGraph(navigationState: NavigationState) {
    composable(NavigationAction.Login.route) {
        LoginScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            })
    }

    composable(NavigationAction.SignUp.route) {
        SignUpScreen(
            openAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            })
    }
}