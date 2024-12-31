package uk.ac.tees.mad.livepoll.presentation.ui

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import uk.ac.tees.mad.livepoll.R
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.presentation.viewmodel.PollViewModel

@Composable
fun PollScreen(vm : PollViewModel,navController: NavController) {
    val context = LocalContext.current
    Scaffold(topBar = { TopAppBar(title = { androidx.compose.material3.Text(
        text = "Home",
        fontSize = 30.sp,
        fontWeight = FontWeight.SemiBold
    ) })}) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            val pollList = vm.pollData.value
            if (pollList!!.isNotEmpty()) {
                LazyColumn {
                    items(pollList) { idtem ->
                        Text(text = idtem.question)
                    }
                }
            } else {
                Text(text = "No Data")
            }
            Button(onClick = { showNotification(context) }) {
                Text(text = "Create temp notification")
            }
        }
    }
}
private fun showNotification(context: Context) {
    val notificationId = 1
    val builder = NotificationCompat.Builder(context, "poll_channel")
        .setSmallIcon(R.drawable.designer)
        .setContentTitle("New Poll Added")
        .setContentText("A New Poll have been added.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.notify(notificationId, builder.build())
}

