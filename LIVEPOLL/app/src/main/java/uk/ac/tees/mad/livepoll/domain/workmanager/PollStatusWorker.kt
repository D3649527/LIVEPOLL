package uk.ac.tees.mad.livepoll.domain.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await
import java.util.Calendar

@HiltWorker
class PollStatusWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
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
            Result.success()
        } catch (e: Exception) {
            Log.e("PollWorker", "Error in worker", e)
            Result.failure()
        }
    }
}