package uk.ac.tees.mad.livepoll

import androidx.navigation.NavController

const val USER = "user"
const val POLLS = "polls"
const val USER_VOTES = "user_votes"



fun navigateWithBackStack(navController: NavController, route : String){
    navController.navigate(route)
}

fun navigateWithoutBackStack(navController: NavController, route : String){
    navController.navigate(route){
        popUpTo(0)
    }
}