package uk.ac.tees.mad.livepoll.presentation.viewmodel

import android.content.Context
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import uk.ac.tees.mad.livepoll.POLLS
import uk.ac.tees.mad.livepoll.USER
import uk.ac.tees.mad.livepoll.USER_VOTES
import uk.ac.tees.mad.livepoll.data.PollData
import uk.ac.tees.mad.livepoll.data.userData
import java.io.IOException
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
    val user = mutableStateOf<userData?>(null)

    init {
        isLoggedIn.value = auth.currentUser != null
        if (isLoggedIn.value) {
            Log.d("Init", "init: ${auth.currentUser?.uid}")
            fetchPollData()
            fetchUserData()
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
            subscribeToNotifications()
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
            subscribeToNotifications()
        }.addOnFailureListener {
            isLoading.value = false
            Log.d("TAG", "signUp: ${it.message}")
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun fetchUserData() {
        firestore.collection(USER).document(auth.currentUser!!.uid).get().addOnSuccessListener {
            user.value = it.toObject(userData::class.java)
        }.addOnFailureListener {
            Log.d("FetchFailed", "fetchUserData: ${it.message}")
        }
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
                        fetchPollData()

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

    private fun subscribeToNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("allUsers")
            .addOnCompleteListener { task ->
                var msg = "Subscribed to notifications"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                println(msg)
            }
    }
    fun sendNotificationToAllUsers(title : String, body : String){
        val url = "https://fcm.googleapis.com/fcm/send"
        val serverKey = "dlVMWTXzb6pJJOHS9qazvp3kK0MhTJVZ6BSE8ux9q2o"
        val notification = JSONObject().apply {
            put("title", title)
            put("body", body)
        }

        val message = JSONObject().apply {
            put("to", "/topics/allUsers")
            put("notification", notification)
        }

        val client = OkHttpClient()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            message.toString()
        )

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "key=$serverKey")
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    println("Notification sent successfully")
                } else {
                    println("Failed to send notification")
                }
            }
        })
    }

    suspend fun voteForOption(pollId: String, option: String,failed:()-> Unit) {
        val userId = auth.currentUser?.uid ?: return

        val userVoteDoc = firestore.collection(USER_VOTES).document("$userId-$pollId")

        val userVoteSnapshot = userVoteDoc.get().await()
        if (userVoteSnapshot.exists()) {
            Log.d("TAG", "User has already voted for this poll")
            failed()
            return
        }

        firestore.runTransaction { transaction ->
            val pollRef = firestore.collection(POLLS).document(pollId)
            val snapshot = transaction.get(pollRef)

            val currentVotes = snapshot.getLong("$option.votes") ?: 0
            transaction.update(pollRef, "$option.votes", currentVotes + 1)

            transaction.set(userVoteDoc, mapOf("votedOption" to option))
        }.await()
    }

    suspend fun getPollById(id: String?): PollData? {
        return try {
            val documentSnapshot = firestore.collection(POLLS).document(id!!).get().await()
            documentSnapshot.toObject(PollData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun checkAndArchiveExpiredPolls() {
        viewModelScope.launch {
            try {
                val currentTime = com.google.firebase.Timestamp.now()
                Log.d("PollWorker", "Current time: $currentTime")

                val activePolls = firestore.collection("polls")
                    .whereEqualTo("status", "active")
                    .whereLessThan("endTime", currentTime)
                    .get()
                    .await()

                Log.d("PollWorker", "Fetched ${activePolls.documents.size} active polls")

                activePolls.documents.forEach { document ->
                    Log.d("PollWorker", "Archiving poll with id: ${document.id}")
                    firestore.collection("polls")
                        .document(document.id)
                        .update("status", "archive")
                        .await()
                    Log.d("PollWorker", "Poll ${document.id} archived successfully")
                }

                Log.d("PollWorker", "Worker completed successfully")
            } catch (e: Exception) {
                Log.e("PollWorker", "Error during check and archive: ${e.localizedMessage}", e)
            }
        }
    }

    fun uploadProfile(context: Context, imageUri: Uri) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val storageRef = firebaseStorage.reference
        val profileRef = storageRef.child("profile_images/$userId")

        profileRef.putFile(imageUri)
            .addOnSuccessListener { snapshot ->
                profileRef.downloadUrl.addOnSuccessListener { uri ->
                    firestore.collection(USER).document(userId)
                        .update("profileImage", uri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Profile image updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to update profile image in Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }.addOnFailureListener { e ->
                    Toast.makeText(context, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun logOut() {
        auth.signOut()
        isLoggedIn.value = false
    }

    fun updateUserData(context : Context, editableName: String, editableEmail: String) {
        isLoading.value = true
        firestore.collection(USER).document(auth.currentUser!!.uid).update(
            "name", editableName,
            "email", editableEmail
        ).addOnSuccessListener {
            fetchUserData()
            Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
            isLoading.value = false
        }.addOnFailureListener {
            Log.d("FetchFailed", "fetchUserData: ${it.message}")
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            isLoading.value = false
        }
    }
}