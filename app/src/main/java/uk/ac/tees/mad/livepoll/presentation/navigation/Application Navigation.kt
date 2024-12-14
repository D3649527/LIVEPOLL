package uk.ac.tees.mad.livepoll.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uk.ac.tees.mad.livepoll.presentation.ui.LoginScreen
import uk.ac.tees.mad.livepoll.presentation.ui.SignUp
import uk.ac.tees.mad.livepoll.presentation.ui.SplashScreen

sealed class ApplicationNavigation(val route : String){
    object Splash : ApplicationNavigation("splash")
    object Login : ApplicationNavigation("login")
    object Signup : ApplicationNavigation("signup")
    object Create : ApplicationNavigation("create")
    object Poll : ApplicationNavigation("home")
    object Vote : ApplicationNavigation("poll")
    object Result : ApplicationNavigation("result")
    object Profile : ApplicationNavigation("profile")
}

@Composable
fun ApplicationNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ApplicationNavigation.Splash.route) {
        composable(route = ApplicationNavigation.Splash.route){
            SplashScreen(navController)
        }
        composable(route = ApplicationNavigation.Login.route){
            LoginScreen(navController)
        }
        composable(route = ApplicationNavigation.Signup.route){
            SignUp(navController = navController)
        }
    }
}