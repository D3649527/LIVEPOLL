package uk.ac.tees.mad.livepoll.data

import com.google.firebase.Timestamp

data class PollData(
    val endTime: Timestamp = Timestamp.now(),
    val id: String = "",
    val option1: Map<String, Any> = mapOf("text" to "", "votes" to 0),
    val option2: Map<String, Any> = mapOf("text" to "", "votes" to 0),
    val question: String = "",
    val status : String = ""
)
