package com.sergiolopez.voicecalltranslator

import androidx.activity.compose.BackHandler
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
import com.sergiolopez.voicecalltranslator.feature.call.ui.navigation.NavigationCallExtra
import com.sergiolopez.voicecalltranslator.feature.contactlist.ui.ContactListScreen
import com.sergiolopez.voicecalltranslator.feature.login.ui.LoginScreen
import com.sergiolopez.voicecalltranslator.feature.settings.account.domain.model.ThemeOption
import com.sergiolopez.voicecalltranslator.feature.settings.account.ui.AccountSettingsScreen
import com.sergiolopez.voicecalltranslator.feature.settings.account.ui.navigation.NavigationAccountSettings
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.VoiceSettingsScreen
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.navigation.NavigationVoiceSettings
import com.sergiolopez.voicecalltranslator.feature.signup.ui.SignUpScreen
import com.sergiolopez.voicecalltranslator.feature.splash.ui.SplashScreen
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_DEFAULT_ID
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_ID
import com.sergiolopez.voicecalltranslator.navigation.CALLEE_ID_ARG
import com.sergiolopez.voicecalltranslator.navigation.FIRST_START_UP
import com.sergiolopez.voicecalltranslator.navigation.FIRST_START_UP_ARG
import com.sergiolopez.voicecalltranslator.navigation.FIRST_START_UP_DEFAULT_VALUE
import com.sergiolopez.voicecalltranslator.navigation.NavigationAction
import com.sergiolopez.voicecalltranslator.navigation.NavigationRoute
import com.sergiolopez.voicecalltranslator.navigation.NavigationState

@Composable
fun VoiceCallTranslatorApp(
    navigationCallExtra: NavigationCallExtra,
    themeConfiguration: (ThemeOption) -> Unit
) {
    val navigationState = rememberNavigationState()

    Scaffold { innerPaddingModifier ->
        NavHost(
            navController = navigationState.navController,
            startDestination = if (navigationCallExtra.hasCallData) {
                NavigationRoute.CALL.navigationName
            } else {
                NavigationRoute.SPLASH.navigationName
            },
            modifier = Modifier.padding(innerPaddingModifier)
        ) {
            notesGraph(
                navigationState = navigationState,
                themeConfiguration = themeConfiguration
            )
        }
        // Note : Snackbar flow has been implemented in a different Scaffold per Screen to directly
        // attach the Snackbar visibility to every ViewModel without any extra configuration
    }
}

@Composable
fun rememberNavigationState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        NavigationState(navController)
    }

fun NavGraphBuilder.notesGraph(
    navigationState: NavigationState,
    themeConfiguration: (ThemeOption) -> Unit
) {
    composable(NavigationAction.SplashNavigation.route) {
        SplashScreen(
            navigateAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            },
            themeConfiguration = themeConfiguration
        )
    }

    composable(NavigationAction.LoginNavigation.route) {
        LoginScreen(
            navigateAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            },
            navigate = { navigationParams ->
                navigationState.navigate(
                    route = navigationParams.route
                )
            }
        )
    }

    composable(NavigationAction.SignUpNavigation.route) {
        SignUpScreen(
            clearAndNavigate = { navigationParams ->
                navigationState.clearAndNavigate(
                    route = navigationParams.route
                )
            }
        )
    }

    composable(NavigationAction.ContactListNavigation.route) {
        ContactListScreen(
            navigateAndPopUp = { navigationParams ->
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
        BackHandler(true) {
            // DO NOTHING
        }

        CallScreen(
            navigateAndPopUp = { navigationParams ->
                navigationState.navigateAndPopUp(
                    navigationParams = navigationParams
                )
            },
            calleeId = it.arguments?.getString(CALLEE_ID) ?: CALLEE_DEFAULT_ID
        )
    }

    composable(
        route = "${NavigationAction.VoiceSettingsNavigation.route}$FIRST_START_UP_ARG",
        arguments = listOf(
            navArgument(FIRST_START_UP) {
                defaultValue = FIRST_START_UP_DEFAULT_VALUE
            }
        )
    ) {
        VoiceSettingsScreen(
            navigationVoiceSettings = NavigationVoiceSettings(
                navigatePopBackStack = { navigationState.popBackStack() },
                clearAndNavigate = { navigationParams ->
                    navigationState.clearAndNavigate(
                        route = navigationParams.route
                    )
                }
            ),
            firstStartUp = it.arguments?.getBoolean(FIRST_START_UP) ?: FIRST_START_UP_DEFAULT_VALUE,
        )
    }

    composable(
        route = "${NavigationAction.AccountSettingsNavigation.route}$FIRST_START_UP_ARG",
        arguments = listOf(
            navArgument(FIRST_START_UP) {
                defaultValue = FIRST_START_UP_DEFAULT_VALUE
            }
        )
    ) {
        AccountSettingsScreen(
            navigationAccountSettings = NavigationAccountSettings(
                navigatePopBackStack = { navigationState.popBackStack() },
                setThemeConfiguration = themeConfiguration,
                clearAndNavigate = { navigationParams ->
                    navigationState.clearAndNavigate(
                        route = navigationParams.route
                    )
                },
            ),
            firstStartUp = it.arguments?.getBoolean(FIRST_START_UP) ?: FIRST_START_UP_DEFAULT_VALUE,
        )
    }
}