package co.wawand.mobile.park_solution

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.wawand.mobile.park_solution.ui.MainScreen
import co.wawand.mobile.park_solution.ui.auth.SignInScreen
import co.wawand.mobile.park_solution.ui.companySettings.CreateCompanyFlow
import co.wawand.mobile.park_solution.ui.companySettings.JoinCompanyScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object SignInScreen : Screen()

    @Serializable
    data object MainScreen : Screen()

    @Serializable
    data object CreateCompanySettingsScreen : Screen()

    @Serializable
    data object JoinCompanyScreen : Screen()
}

@Composable
fun Navigation(startDestination: Screen = Screen.SignInScreen) {
    val navController = rememberNavController()

    NavHost(startDestination = startDestination, navController = navController) {
        composable<Screen.SignInScreen> {
            SignInScreen(
                navigateToHome = {
                    navController.navigate(Screen.MainScreen) {
                        popUpTo<Screen.SignInScreen> { inclusive = true }
                    }
                },
                navigateToCreateCompany = {
                    navController.navigate(Screen.CreateCompanySettingsScreen) {
                        popUpTo<Screen.SignInScreen> { inclusive = true }
                    }
                },
                navigateToJoinCompany = {
                    navController.navigate(Screen.JoinCompanyScreen) {
                        popUpTo<Screen.SignInScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.CreateCompanySettingsScreen> {
            CreateCompanyFlow(
                navigateToHome = {
                    navController.navigate(Screen.MainScreen) {
                        popUpTo<Screen.SignInScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.JoinCompanyScreen> {
            JoinCompanyScreen(
                navigateToHome = {
                    navController.navigate(Screen.MainScreen) {
                        popUpTo<Screen.SignInScreen> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.MainScreen> {
            MainScreen(
                navigateToSignIn = {
                    navController.navigate(Screen.SignInScreen) {
                        popUpTo<Screen.MainScreen> { inclusive = true }
                    }
                }
            )
        }
    }
}