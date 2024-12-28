package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation

@Composable
fun PollScreen(navController: NavController) {
    Column(modifier = Modifier
        .fillMaxSize()
        .clickable {
            navController.navigate(
                ApplicationNavigation.Create.route)
        }) {

    }
}