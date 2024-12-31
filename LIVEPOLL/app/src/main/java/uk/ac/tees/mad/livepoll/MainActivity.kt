package uk.ac.tees.mad.livepoll

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import uk.ac.tees.mad.livepoll.domain.workmanager.schedulePollStatusUpdate
import uk.ac.tees.mad.livepoll.presentation.navigation.ApplicationNavigation
import uk.ac.tees.mad.livepoll.ui.theme.LivePollTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        requestNotificationPermission()
        schedulePollStatusUpdate(this)
        enableEdgeToEdge()
        setContent {
            LivePollTheme {
                ApplicationNavigation()
            }
        }
    }
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "poll_channel",
            "New Poll",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notification"
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}

