package com.gcast.cast

/**
 * Minimal, single-source implementation restored to unblock builds.
 */
object CastReflection {
    fun attachCastTaskListeners(
        raw: Any,
        onSuccess: (com.google.android.gms.cast.framework.CastContext) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            if (raw is com.google.android.gms.tasks.Task<*>) {
                try {
                    @Suppress("UNCHECKED_CAST")
                    val task = raw as com.google.android.gms.tasks.Task<com.google.android.gms.cast.framework.CastContext>
                    task.addOnSuccessListener { ctx -> onSuccess(ctx) }
                    task.addOnFailureListener { e -> onFailure(Exception(e)) }
                } catch (_: Throwable) { }
            }
        } catch (e: Exception) {
            try { onFailure(e) } catch (_: Throwable) { }
        }
    }
}
