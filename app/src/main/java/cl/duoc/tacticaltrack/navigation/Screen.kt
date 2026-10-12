package cl.duoc.tacticaltrack.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object NewVisit : Screen("new_visit")
    data object Evidence : Screen("evidence")
    data object History : Screen("history")
    data object Supervisor : Screen("supervisor")
    data object Detail : Screen("detail/{visitId}") {
        fun createRoute(visitId: Long) = "detail/$visitId"
    }
}
