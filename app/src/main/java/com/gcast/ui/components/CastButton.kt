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
        try {
            val result = CastContext.getSharedInstance(context)

            if (result is com.google.android.gms.cast.framework.CastContext) {
                castContext = result
                castState = castContext?.castState ?: CastState.NO_DEVICES_AVAILABLE
            } else {
                @Suppress("UNCHECKED_CAST")
                val task = result as com.google.android.gms.tasks.Task<com.google.android.gms.cast.framework.CastContext>
                task.addOnSuccessListener { ctx: com.google.android.gms.cast.framework.CastContext ->
                    castContext = ctx
                    castState = castContext?.castState ?: CastState.NO_DEVICES_AVAILABLE
                }
                task.addOnFailureListener { e: Exception ->
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            castContext?.let { cc ->
                try {
                    when (castState) {
                        CastState.CONNECTED -> {
                            cc.sessionManager.endCurrentSession(true)
                        }
                        CastState.NOT_CONNECTED -> {
                            try {
                                val fragment = androidx.mediarouter.app.MediaRouteChooserDialogFragment()
                                val selector = androidx.mediarouter.media.MediaRouteSelector.Builder()
                                    .addControlCategory(com.google.android.gms.cast.CastMediaControlIntent
                                        .categoryForCast(com.google.android.gms.cast.CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID))
                                    .build()
                                fragment.routeSelector = selector
                                val activity = context as? androidx.fragment.app.FragmentActivity
                                activity?.let {
                                    fragment.show(it.supportFragmentManager, "media_route_chooser")
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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