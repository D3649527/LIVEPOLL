package uk.ac.tees.mad.livepoll.domain.di

import android.content.Context
import androidx.room.Room
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.ac.tees.mad.livepoll.data.PollDao
import uk.ac.tees.mad.livepoll.data.PollDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesAuth() : FirebaseAuth = Firebase.auth

    @Provides
    fun providesFirestore() : FirebaseFirestore = Firebase.firestore

    @Provides
    fun provideFirebaseStorage() : FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }
    @Provides
    @Singleton
    fun providePollDatabase(@ApplicationContext appContext: Context): PollDatabase {
        return Room.databaseBuilder(
            appContext,
            PollDatabase::class.java,
            "poll_database"
        ).build()
    }

    @Provides
    fun providePollDao(database: PollDatabase): PollDao {
        return database.pollDao()
    }
}