package com.gcast.cast

import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean
import android.content.ContextWrapper

class CastCompatTest {
    @Test
    fun `getCastContext calls onFailure when provider returns unsupported type`() {
        val called = AtomicBoolean(false)

        // Override provider to return an unsupported type
        CastCompat.instanceProvider = { _ -> "unsupported" }

        // Use a lightweight ContextWrapper since CastCompat only passes the context to the provider
        val fakeContext = ContextWrapper(null)

        CastCompat.getCastContext(
            context = fakeContext,
            onSuccess = { _ -> throw AssertionError("should not succeed") },
            onFailure = { _ -> called.set(true) }
        )

        if (!called.get()) throw AssertionError("onFailure was not invoked")
    }
}
