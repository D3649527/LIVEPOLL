package uk.ac.tees.mad.livepoll.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PollDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoll(poll: List<Poll>)

    @Query("SELECT * FROM poll_table")
    suspend fun getAllPolls(): List<Poll>

    @Query("DELETE FROM poll_table")
    suspend fun deleteAllPolls()

}