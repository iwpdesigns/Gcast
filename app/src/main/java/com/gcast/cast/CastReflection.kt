package com.gcast.cast

import com.google.android.gms.cast.framework.CastContext

object CastReflection {
    fun attachCastTaskListeners(raw: Any, onSuccess: (CastContext) -> Unit, onFailure: (Exception) -> Unit) {
        try {
            val clazz = raw::class.java

            val onSuccessCls = Class.forName("com.google.android.gms.tasks.OnSuccessListener")
            val onFailureCls = Class.forName("com.google.android.gms.tasks.OnFailureListener")

            val addOnSuccess = clazz.getMethod("addOnSuccessListener", onSuccessCls)
            val addOnFailure = clazz.getMethod("addOnFailureListener", onFailureCls)

            val successProxy = java.lang.reflect.Proxy.newProxyInstance(
                onSuccessCls.classLoader,
                arrayOf(onSuccessCls)
            ) { _, _, args ->
                val ctxAny = args?.getOrNull(0)
                if (ctxAny is com.google.android.gms.cast.framework.CastContext) {
                    onSuccess(ctxAny)
                }
                null
            }

            val failureProxy = java.lang.reflect.Proxy.newProxyInstance(
                onFailureCls.classLoader,
                arrayOf(onFailureCls)
            ) { _, _, args ->
                val ex = args?.getOrNull(0) as? Exception
                if (ex != null) onFailure(ex)
                null
            }

            addOnSuccess.invoke(raw, successProxy)
            addOnFailure.invoke(raw, failureProxy)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
