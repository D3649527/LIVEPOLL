package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@Composable
fun VotingScreen(viewModel: PollViewModel, navController: NavHostController, id: String?) {
    val response = viewModel.getPollById(id)
    Text(text = response.toString())
}