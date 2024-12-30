package uk.ac.tees.mad.livepoll.domain.workmanager

import android.content.Context
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
            val currentTime = Calendar.getInstance().timeInMillis

            val activePolls = firestore.collection("polls")
                .whereEqualTo("status", "active")
                .whereLessThan("endTime", currentTime)
                .get()
                .await()
            activePolls.documents.forEach { document ->
                firestore.collection("polls")
                    .document(document.id)
                    .update("status", "archive")
                    .await()
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}