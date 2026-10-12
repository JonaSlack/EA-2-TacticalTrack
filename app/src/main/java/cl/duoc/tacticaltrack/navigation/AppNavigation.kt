package cl.duoc.tacticaltrack.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.tacticaltrack.data.repository.UserPreferencesRepository
import cl.duoc.tacticaltrack.ui.screens.HomeScreen
import cl.duoc.tacticaltrack.ui.screens.LoginScreen
import cl.duoc.tacticaltrack.viewmodel.LoginViewModel

/**
 * Navegación incremental de la Etapa 1.
 * En esta etapa solo se habilitan Login y Home para mantener main compilable.
 * Las demás rutas se integrarán cuando los otros módulos sean incorporados.
 */
@Composable
fun TacticalTrackApp() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val preferencesRepository = remember {
        UserPreferencesRepository(context.applicationContext)
    }
    val loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModel.Factory(preferencesRepository)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = { _, _ ->
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
    }
}
