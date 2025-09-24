package com.gcast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.cast.framework.CastContext
import com.gcast.ui.theme.GCastTheme
import com.gcast.ui.components.CastButton
import com.gcast.ui.screens.MainScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Cast Context using Task-based API only
        try {
            val result = CastContext.getSharedInstance(this)

            if (result is com.google.android.gms.cast.framework.CastContext) {
                // CastContext already available synchronously
            } else {
                @Suppress("UNCHECKED_CAST")
                val task = result as com.google.android.gms.tasks.Task<com.google.android.gms.cast.framework.CastContext>
                task.addOnSuccessListener { ctx: com.google.android.gms.cast.framework.CastContext ->
                    // success: ctx is available
                }
                task.addOnFailureListener { e: Exception ->
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            GCastTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}