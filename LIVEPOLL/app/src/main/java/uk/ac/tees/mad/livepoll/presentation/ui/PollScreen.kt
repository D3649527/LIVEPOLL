import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import uk.ac.tees.mad.livepoll.R
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PollScreen(vm: PollViewModel, navController: NavController) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("active") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Home",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFF6200EE)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedTab = "active" },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedTab == "active") Color.Gray else Color.LightGray
                    )
                ) {
                    Text(text = "Active Polls")
                }

                Button(
                    onClick = { selectedTab = "archive" },
                    colors =  ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedTab == "archive") Color.Gray else Color.LightGray
                    )
                ) {
                    Text(text = "Archived Polls")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val pollList = when (selectedTab) {
                "active" -> vm.pollData.value?.filter { it.status == "active" }
                "archive" -> vm.pollData.value?.filter { it.status == "archive" }
                else -> emptyList()
            }

            if (!pollList.isNullOrEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(pollList) { item ->
                        PollCard(
                            question = item.question,
                            onItemClicked = {
                                if (item.id.isNotEmpty()) {
                                    navController.navigate(ApplicationNavigation.Vote.createRoute(item.id))
                                }
                            }
                        )
                    }
                }
            } else {
                Text(
                    text = "No Data",
                    fontSize = 20.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { vm.checkAndArchiveExpiredPolls() },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Create Temp Notification")
            }
        }
    }
}

@Composable
fun PollCard(question: String, onItemClicked: () -> Unit) {
    Card(
        onClick = onItemClicked,
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
    ) {
        Text(
            text = question,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            fontWeight = FontWeight.Medium
        )
    }
}

private fun showNotification(context: Context) {
    val notificationId = 1
    val builder = NotificationCompat.Builder(context, "poll_channel")
        .setSmallIcon(R.drawable.designer)
        .setContentTitle("New Poll Added")
        .setContentText("A new poll has been added.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}
