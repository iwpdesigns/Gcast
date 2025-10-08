package com.gcast.cast

import android.content.Context
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.tasks.Task

/**
 * Small compatibility shim to obtain a CastContext in a way that works
 * across multiple Cast SDK versions (some return a Task, some return
 * the CastContext directly).
 */
object CastCompat {
    /**
     * Provider function used to obtain the raw return of the CastContext accessor.
     * Tests may override this to simulate different SDK behaviours.
     */
    @Volatile
    var instanceProvider: (android.content.Context?) -> Any? = { ctx ->
        // Default uses reflection to avoid hard dependency in signatures
        try {
            val clazz = Class.forName("com.google.android.gms.cast.framework.CastContext")
            val method = clazz.getMethod("getSharedInstance", Class.forName("android.content.Context"))
            method.invoke(null, ctx)
        } catch (e: Throwable) {
            null
        }
    }

    // Tests may override `instanceProvider` directly (no explicit setter to avoid JVM signature clashes)

    /**
     * Obtain a CastContext instance using the provider. This method calls
     * onSuccess with the castContext object if available, otherwise calls
     * onFailure with an Exception.
     */
    fun getCastContext(
        context: android.content.Context,
        onSuccess: (com.google.android.gms.cast.framework.CastContext) -> Unit,
        onFailure: (Exception) -> Unit = { it.printStackTrace() }
    ) {
        try {
            val raw = instanceProvider(context)

            if (raw == null) {
                onFailure(Exception("CastContext provider returned null"))
                return
            }

            // If it's already a CastContext, accept it
            if (raw is com.google.android.gms.cast.framework.CastContext) {
                onSuccess(raw)
                return
            }

            // If it's a Task, attach listeners
            if (raw is com.google.android.gms.tasks.Task<*>) {
                try {
                    raw.addOnSuccessListener { result ->
                        if (result is com.google.android.gms.cast.framework.CastContext) {
                            onSuccess(result)
                        } else {
                            onFailure(Exception("Task produced unexpected result type: ${result?.javaClass}"))
                        }
                    }
                    raw.addOnFailureListener { e -> onFailure(Exception(e)) }
                    return
                } catch (e: Exception) {
                    // fallthrough to failure
                }
            }

            onFailure(Exception("Unsupported CastContext.getSharedInstance return type: ${raw.javaClass}"))
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
