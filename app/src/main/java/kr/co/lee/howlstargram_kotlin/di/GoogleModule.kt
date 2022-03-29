package kr.co.lee.howlstargram_kotlin.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object GoogleModule {

    @Provides
    fun provideGso(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("375438055963-8aervd706mmof7oublerf0rnnq097vlu.apps.googleusercontent.com") // Google Play Services default_web_client_id
            .requestEmail()
            .build()
    }

    @Provides
    fun provideGoogleSignInClient(
        gso: GoogleSignInOptions,
        @ActivityContext context: Context
    ): GoogleSignInClient {
        return GoogleSignIn.getClient(context, gso)
    }
}
