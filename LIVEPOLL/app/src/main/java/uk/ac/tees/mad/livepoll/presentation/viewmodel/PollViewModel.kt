package uk.ac.tees.mad.livepoll.presentation.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.ac.tees.mad.livepoll.POLLS
import uk.ac.tees.mad.livepoll.USER
import uk.ac.tees.mad.livepoll.data.PollData
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isLoggedIn = mutableStateOf(false)
    val pollData = mutableStateOf<List<PollData>?>(null)

    init {
        isLoggedIn.value = auth.currentUser != null
        if (isLoggedIn.value) {
            fetchPollData()
        }
    }

    private fun fetchPollData(){
        firestore.collection(POLLS).get().addOnSuccessListener {
            pollData.value = it.toObjects(PollData::class.java)
            Log.d("POLLS", "fetchPollData: ${pollData.value}")
        }.addOnFailureListener {
            Log.d("POLLS", "fetchPollData: ${it.message}")
        }
    }

    fun signUp(context: Context, email: String, password: String) {
        isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            firestore.collection(USER).document(auth.currentUser!!.uid).set(
                hashMapOf(
                    "email" to email,
                    "password" to password
                )
            )
            fetchUserData()
            isLoading.value = false
            isLoggedIn.value = true
        }.addOnFailureListener {
            isLoading.value = false
            Log.d("TAG", "signUp: ${it.message}")
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun login(context: Context, email: String, password: String) {
        isLoading.value = true
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            isLoading.value = false
            isLoggedIn.value = true
            fetchUserData()
        }.addOnFailureListener {
            isLoading.value = false
            Log.d("TAG", "signUp: ${it.message}")
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchUserData() {
    }

    @OptIn(ExperimentalMaterial3Api::class)
    fun createPoll(
        question: String,
        option1: String,
        option2: String,
        selectedDateMillis: Long?,
        selectedTime: TimePickerState?,
        onValidationError: (String) -> Unit,
        onSuccess: () -> Unit,
    ) {
        isLoading.value = true
        if (selectedDateMillis == null || selectedTime == null) {
            onValidationError("Please select a valid date and time.")
            isLoading.value = false
            return
        }
        val selectedCalendar = Calendar.getInstance().apply {
            timeInMillis = selectedDateMillis
            set(Calendar.HOUR_OF_DAY, selectedTime.hour)
            set(Calendar.MINUTE, selectedTime.minute)
        }
        val currentCalendar = Calendar.getInstance()
        if (selectedCalendar.before(currentCalendar)) {
            onValidationError("Selected date and time cannot be in the past.")
            isLoading.value = false
            return
        }
        val pollData = mapOf(
            "question" to question,
            "option1" to mapOf("text" to option1, "votes" to 0),
            "option2" to mapOf("text" to option2, "votes" to 0),
            "endTime" to selectedCalendar.time,
            "status" to "active"
        )
        viewModelScope.launch {
            firestore.collection(POLLS)
                .add(pollData)
                .addOnSuccessListener {
                    val id = it.id
                    firestore.collection(POLLS).document(id).update("id", id).addOnSuccessListener {
                        isLoading.value = false
                        onSuccess()
                        notifyUsersAboutNewPoll(question)
                    }
                        .addOnFailureListener {
                            isLoading.value = false
                            onValidationError("Failed to create poll. Please try again.")
                        }
                }
                .addOnFailureListener {
                    onValidationError("Failed to create poll. Please try again.")
                }
        }
    }

    fun notifyUsersAboutNewPoll(pollQuestion: String) {
        // Fetch all user FCM tokens from Firestore
        firestore.collection(USER).get().addOnSuccessListener { users ->
            for (user in users.documents) {
                val fcmToken = user.getString("fcmToken") ?: continue
                if (fcmToken != FirebaseAuth.getInstance().currentUser?.uid) {
                    sendNotificationToUser(fcmToken, pollQuestion)
                }
            }
        }.addOnFailureListener {
            Log.e("FCM", "Error fetching users: ${it.message}")
        }
    }

    private fun sendNotificationToUser(token: String, pollQuestion: String) {
        val message = RemoteMessage.Builder("$token@fcm.googleapis.com")
            .setMessageId("poll_${System.currentTimeMillis()}")
            .addData("title", "New Poll Available!")
            .addData("body", pollQuestion)
            .build()

        FirebaseMessaging.getInstance().subscribeToTopic("new_polls")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to new_polls topic")
                } else {
                    Log.e("FCM", "Subscription failed", task.exception)
                }
            }
    }

}