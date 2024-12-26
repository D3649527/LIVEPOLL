package uk.ac.tees.mad.livepoll.presentation.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.ac.tees.mad.livepoll.USER
import javax.inject.Inject

@HiltViewModel
class PollViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
) : ViewModel() {

    val isLoading = mutableStateOf(false)
    val isLoggedIn = mutableStateOf(false)

    init {
        isLoggedIn.value = auth.currentUser != null

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

}