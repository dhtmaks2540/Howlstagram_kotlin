package kr.co.lee.howlstargram_kotlin.di

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
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

//    @Provides
//    fun provideSignInClient(@ActivityContext context: Context): SignInClient {
//        return Identity.getSignInClient(context)
//    }
//
//    @Provides
//    fun provideBeginSignInRequest(@ActivityContext context: Context): BeginSignInRequest {
//        return BeginSignInRequest.builder()
//            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
//                .setSupported(true)
//                .build())
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    // Your server's client ID, not your Android client ID.
//                    .setServerClientId(context.getString(R.string.default_web_client_id))
//                    // Only show accounts previously used to sign in.
//                    .setFilterByAuthorizedAccounts(true)
//                    .build())
//            // Automatically sign in when exactly one credential is retrieved.
//            .setAutoSelectEnabled(true)
//            .build()
//    }

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
