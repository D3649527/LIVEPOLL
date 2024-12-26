package uk.ac.tees.mad.livepoll

import androidx.navigation.NavController

const val USER = "user"

fun navigateWithBackStack(navController: NavController, route : String){
    navController.navigate(route)
}

fun navigateWithoutBackStack(navController: NavController, route : String){
    navController.navigate(route){
        popUpTo(0)
    }
}