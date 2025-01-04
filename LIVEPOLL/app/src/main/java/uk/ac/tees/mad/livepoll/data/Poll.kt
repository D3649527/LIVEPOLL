package uk.ac.tees.mad.livepoll.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Entity(tableName = "poll_table")
data class Poll(
    @PrimaryKey(autoGenerate = true)
    val uid : Int = 0,
    val endTime: Timestamp = Timestamp.now(),
    val id: String = "",
    val option1: Map<String, Any> = mapOf("text" to "", "votes" to 0),
    val option2: Map<String, Any> = mapOf("text" to "", "votes" to 0),
    val question: String = "",
    val status: String = ""
)
