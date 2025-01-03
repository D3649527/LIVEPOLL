import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import uk.ac.tees.mad.livepoll.data.PollData
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@Composable
fun VotingScreen(viewModel: PollViewModel, navController: NavHostController, id: String?) {
    val pollData = remember { mutableStateOf<PollData?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        pollData.value = viewModel.getPollById(id)
    }

    var showToast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(showToast) {
        showToast?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            showToast = null
        }
    }

    pollData.value?.let { poll ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = poll.question,
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (poll.status == "archive") {
                val option1Name = poll.option1["text"] as String
                val option2Name = poll.option2["text"] as String
                val option1Votes = poll.option1["votes"] as Long
                val option2Votes = poll.option2["votes"] as Long
                val winner = if (option1Votes > option2Votes) {
                    poll.option1["text"] as String
                } else {
                    poll.option2["text"] as String
                }

                BarChart(option1Votes = option1Votes, option1Name = option1Name , option2Votes = option2Votes, option2Name = option2Name)

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Poll has ended and the winner is $winner",
                    fontSize = 20.sp,
                    color = Color.Blue,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else{
                PollOptionCardWithButton(
                    optionText = poll.option1["text"] as String,
                    votes = poll.option1["votes"] as Long,
                    onVoteClick = {
                        scope.launch {
                            try {
                                viewModel.voteForOption(
                                    id!!,
                                    "option1",
                                    failed = { showToast = "You have already voted for this poll" })
                                pollData.value = viewModel.getPollById(id)
                            } catch (e: Exception) {
                                showToast = "You have already voted for this poll"
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PollOptionCardWithButton(
                    optionText = poll.option2["text"] as String,
                    votes = poll.option2["votes"] as Long,
                    onVoteClick = {
                        scope.launch {
                            try {
                                viewModel.voteForOption(
                                    id!!,
                                    "option2",
                                    failed = { showToast = "You have already voted for this poll" })
                                pollData.value = viewModel.getPollById(id)
                            } catch (e: Exception) {
                                showToast = "You have already voted for this poll"
                            }
                        }
                    }
                )
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}


@Composable
fun PollOptionCardWithButton(optionText: String, votes: Long, onVoteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = optionText,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Votes: $votes",
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Button(
                onClick = onVoteClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Vote")
            }
        }
    }
}

@Composable
fun BarChart(option1Name: String, option1Votes: Long, option2Name: String, option2Votes: Long) {
    val totalVotes = if (option1Votes + option2Votes > 0) option1Votes + option2Votes else 1
    val option1Percentage = (option1Votes.toFloat() / totalVotes)
    val option2Percentage = (option2Votes.toFloat() / totalVotes)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "$option1Name: $option1Votes votes", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(vertical = 4.dp),
            color = Color.LightGray,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(option1Percentage)
                        .background(Color.Blue)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        Text(text = "${String.format("%.1f", option1Percentage * 100)}%", color = Color.Blue)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "$option2Name: $option2Votes votes", fontWeight = FontWeight.Bold)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .padding(vertical = 4.dp),
            color = Color.LightGray,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(option2Percentage)
                        .background(Color.Red)
                        .height(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
        Text(text = "${String.format("%.1f", option2Percentage * 100)}%", color = Color.Red)
    }
}

