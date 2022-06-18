package kr.co.lee.howlstargram_kotlin.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseSingletonModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun providesFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }
}

@Module
@InstallIn(ActivityRetainedComponent::class)
object FirebaseUidModule {
    @Provides
    @CurrentUserUid
    @ActivityRetainedScoped
    fun getUid() = FirebaseAuth.getInstance().currentUser?.uid!!
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class CurrentUserUid