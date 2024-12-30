package uk.ac.tees.mad.livepoll.data
import android.util.Log
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

private val client = OkHttpClient()

fun sendNotificationToTopic(pollQuestion: String) {
    val json = JSONObject()
    val notification = JSONObject()
    notification.put("title", "New Poll Available!")
    notification.put("body", pollQuestion)

    json.put("to", "/topics/new_polls")
    json.put("notification", notification)

    val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json.toString())

    val request = Request.Builder()
        .url("https://fcm.googleapis.com/fcm/send")
        .post(body)
        .addHeader("Authorization", "key=YOUR_SERVER_KEY")  // Replace with your FCM server key
        .addHeader("Content-Type", "application/json")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("FCM", "Failed to send notification: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                Log.d("FCM", "Notification sent successfully")
            } else {
                Log.e("FCM", "Error sending notification: ${response.body?.string()}")
            }
        }
    })
}
