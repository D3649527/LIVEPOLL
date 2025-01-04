package uk.ac.tees.mad.livepoll.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Poll::class], version = 1)
@TypeConverters(Converters::class)

abstract class PollDatabase : RoomDatabase() {
    abstract fun pollDao() : PollDao
}