package uk.ac.tees.mad.livepoll

import androidx.navigation.NavController

fun navigateWithBackStack(navController: NavController, route : String){
    navController.navigate(route)
}

fun navigateWithoutBackStack(navController: NavController, route : String){
    navController.navigate(route){
        popUpTo(0)
    }
}