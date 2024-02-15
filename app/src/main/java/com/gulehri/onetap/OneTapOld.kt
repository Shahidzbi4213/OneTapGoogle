package com.gulehri.onetap;

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient

/*
 * Created by Shahid Iqbal on 2/15/2024.
 */


fun singInWithGoogle(
    context: Context,
    oneTapClient: SignInClient,
    onSuccess: (BeginSignInResult) -> Unit,
    onFailure: (Exception) -> Unit
) {

    val beginSignInRequestOption =
        BeginSignInRequest.PasswordRequestOptions.builder().setSupported(true).build()

    val beginGoogleRequestOption =
        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
            .setSupported(true)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setFilterByAuthorizedAccounts(false)
            .build()

    val signInRequest =
        BeginSignInRequest.builder()
            .setPasswordRequestOptions(beginSignInRequestOption)
            .setGoogleIdTokenRequestOptions(beginGoogleRequestOption).setAutoSelectEnabled(true)
            .build()

    oneTapClient.beginSignIn(signInRequest).addOnSuccessListener {
        onSuccess(it)
    }.addOnFailureListener {
        onFailure(it)
    }
}