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
        
        // Initialize Cast Context using task-based API with safe fallback for older SDKs
        try {
            try {
                val task = CastContext.getSharedInstance(this) as? com.google.android.gms.tasks.Task<com.google.android.gms.cast.framework.CastContext>
                task?.addOnSuccessListener { ctx ->
                    // CastContext available
                }
                task?.addOnFailureListener { e -> e.printStackTrace() }
            } catch (e: ClassCastException) {
                // Older SDK returned CastContext directly
                try {
                    val ctx = CastContext.getSharedInstance(this) as? com.google.android.gms.cast.framework.CastContext
                    // ctx ready
                } catch (ex: Exception) {
                    ex.printStackTrace()
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