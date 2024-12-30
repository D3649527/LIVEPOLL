package uk.ac.tees.mad.livepoll

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
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
        enableEdgeToEdge()
        schedulePollStatusUpdate(this)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM", "FCM Token: $token")
            storeFCMToken(token)
        }

        setContent {
            LivePollTheme {
                ApplicationNavigation()
            }
        }
    }

    private fun storeFCMToken(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance().collection(USER).document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                Log.d("FCM", "FCM Token stored successfully")
            }
            .addOnFailureListener { e ->
                Log.d("FCM", "Error storing FCM Token: ${e.message}")
            }
    }
}

