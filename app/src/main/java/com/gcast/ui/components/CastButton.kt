package com.gcast.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastState
import com.google.android.gms.cast.framework.CastStateListener
import android.util.Log
import com.gcast.ui.theme.CastBlue
import com.gcast.ui.theme.CastConnected
import com.gcast.ui.theme.CastDisconnected

@Composable
fun CastButton(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var castState by remember { mutableIntStateOf(CastState.NO_DEVICES_AVAILABLE) }
    var castContext by remember { mutableStateOf<CastContext?>(null) }

    LaunchedEffect(context) {
        com.gcast.cast.CastCompat.getCastContext(context,
            onSuccess = { ctx ->
                castContext = ctx
                castState = ctx.castState
            },
            onFailure = { e -> e.printStackTrace() }
        )
    }

    DisposableEffect(castContext) {
        val listener = CastStateListener { state ->
            castState = state
        }
        
        castContext?.addCastStateListener(listener)
        
        onDispose {
            castContext?.removeCastStateListener(listener)
        }
    }

    val (icon, tint, contentDescription) = when (castState) {
        CastState.CONNECTED -> Triple(
            Icons.Filled.CastConnected,
            CastConnected,
            "Cast Connected"
        )
        CastState.CONNECTING -> Triple(
            Icons.Filled.Cast,
            CastBlue,
            "Cast Connecting"
        )
        CastState.NOT_CONNECTED -> Triple(
            Icons.Filled.Cast,
            CastBlue,
            "Cast Available"
        )
        else -> Triple(
            Icons.Filled.Cast,
            CastDisconnected,
            "Cast Not Available"
        )
    }

    IconButton(
        onClick = {
            Log.d("CastButton", "onClick invoked, castState=$castState, castContext=${castContext != null}")
            try {
                when (castState) {
                    CastState.CONNECTED -> {
                        Log.d("CastButton", "ending current session")
                        castContext?.sessionManager?.endCurrentSession(true)
                    }
                    CastState.NOT_CONNECTED -> {
                        Log.d("CastButton", "attempting to show MediaRoute chooser")
                        // Use modern Cast integration - let the Cast SDK handle the UI
                        try {
                            val activity = context as? androidx.fragment.app.FragmentActivity
                            activity?.let { fragmentActivity ->
                                // Create a MediaRouteButton programmatically and trigger its click
                                val mediaRouteButton = androidx.mediarouter.app.MediaRouteButton(context)
                                mediaRouteButton.routeSelector = androidx.mediarouter.media.MediaRouteSelector.Builder()
                                    .addControlCategory(com.google.android.gms.cast.CastMediaControlIntent
                                        .categoryForCast(com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                                    .build()
                                mediaRouteButton.performClick()
                                Log.d("CastButton", "MediaRouteButton.performClick() called")
                            }
                        } catch (e: Exception) {
                            Log.e("CastButton", "error showing chooser", e)
                        }
                    }
                    else -> {
                        Log.d("CastButton", "Cast not available or connecting")
                    }
                }
            } catch (e: Exception) {
                Log.e("CastButton", "error handling cast onClick", e)
            }
        },
        modifier = modifier,
        enabled = castState != CastState.NO_DEVICES_AVAILABLE
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}