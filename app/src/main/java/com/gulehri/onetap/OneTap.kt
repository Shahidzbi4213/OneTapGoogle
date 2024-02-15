package com.gulehri.onetap

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

/*
 * Created by Shahid Iqbal on 2/15/2024.
 */

const val TAG = "Auth"

val userFlow = MutableStateFlow<GoogleIdTokenCredential?>(null)

fun signInWithGoogle(context: Context) {

    try {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption =
            GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(true)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(true)
                .build()

        val request: GetCredentialRequest =
            GetCredentialRequest.Builder().setCredentialOptions(listOf(googleIdOption)).build()


        CoroutineScope(Dispatchers.IO).launch {

            try {
                val response = credentialManager.getCredential(
                    request = request,
                    context = context,
                )

                handleSignInRequest(response)
            } catch (e: Exception) {
                Log.e(TAG, e.localizedMessage.orEmpty())
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, e.localizedMessage.orEmpty())
    }
}

suspend fun handleSignInRequest(
    response: GetCredentialResponse,
) {

    when (val credential = response.credential) {

        is CustomCredential -> {

            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {

                try {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    Log.e(TAG, "Name is ${googleIdTokenCredential.displayName}")
                    userFlow.emit(googleIdTokenCredential)

                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "Received an invalid google id token response", e)

                }
            }
        }
    }
}
