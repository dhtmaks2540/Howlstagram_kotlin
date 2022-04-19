package kr.co.lee.howlstargram_kotlin.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseSingletonModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}

@Module
@InstallIn(ActivityComponent::class)
object FirebaseActivityModule {
    @Provides
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}
