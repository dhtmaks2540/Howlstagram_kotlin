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
import kr.co.lee.howlstargram_kotlin.R

@Module
@InstallIn(ActivityComponent::class)
object GoogleModule {

    // Configure Google Sign In
    @Provides
    fun provideGso(@ActivityContext activityContext: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activityContext.getString(R.string.default_web_client_id)) // Google Play Services default_web_client_id
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
