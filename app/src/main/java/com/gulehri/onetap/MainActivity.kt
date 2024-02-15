package com.gulehri.onetap

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInCredential
import com.gulehri.onetap.ui.theme.OneTapTheme
import com.gulehri.onetap.ui.theme.Pink40
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    val oneTapClient by lazy { Identity.getSignInClient(this) }
    private val user = MutableStateFlow<SignInCredential?>(null)

    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
            user.value = credential
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OneTapTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current

                    val currentUser = user.collectAsState()
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        // SigInButton { signInWithGoogle(context) }

                        currentUser.value?.let {

                            AsyncImage(
                                model = it.profilePictureUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .border(4.dp, color = Pink40, shape = CircleShape)
                                    .size(100.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        Button(onClick = {
                            singInWithGoogle(context, oneTapClient, onSuccess = {
                                intentLauncher.launch(
                                    IntentSenderRequest.Builder(it.pendingIntent).build()
                                )
                            }, onFailure = {
                                Toast.makeText(
                                    context, it.localizedMessage.orEmpty(), Toast.LENGTH_SHORT
                                ).show()
                            })
                        }) {
                            Text(text = currentUser.value?.displayName ?: "Sign With Google")
                        }
                    }


                }
            }
        }
    }
}

@Composable
fun SigInButton(modifier: Modifier = Modifier, onClick: () -> Unit) {

    val user = userFlow.collectAsState()
    Button(onClick = { onClick() }, modifier) {
        Text(text = user.value?.displayName ?: "Sign With Google")
    }
}

