package uk.ac.tees.mad.livepoll.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.Timestamp
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@Composable
fun OfflinePolls(viewModel: PollViewModel, navController: NavHostController) {
    val offlinePollList = viewModel.offlinePollData.value
    Scaffold {
        Column(modifier = Modifier.padding(it)) {
            if (offlinePollList != null){
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(offlinePollList) { items ->
                        PollView(
                            Question = items.question,
                            items.option1,
                            items.option2,
                            items.endTime,
                            items.status
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PollView(
    Question: String,
    option1: Map<String, Any>,
    option2: Map<String, Any>,
    endTime: Timestamp,
    status: String
){
    Card(modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = Question)
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Option 1: ${option1["text"]}")
                Spacer(modifier = Modifier.weight(0.5f))
                Text(text = option1["votes"].toString().clipDecimalZero())
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Option 2: ${option2["text"]}")
                Spacer(modifier = Modifier.weight(0.5f))
                Text(text = option2["votes"].toString().clipDecimalZero())
            }
            Text(text = "End Time: ${endTime.toDate()}")
            Text(text = "Status: $status")
        }
    }
}

fun String.clipDecimalZero(): String {
    return if (this.endsWith(".0")) {
        this.dropLast(2)  // Remove ".0"
    } else {
        this
    }
}