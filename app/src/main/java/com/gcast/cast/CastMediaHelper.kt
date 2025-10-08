package com.gcast.cast

import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.framework.media.RemoteMediaClient

/**
 * Utility class that centralizes Cast SDK reflection logic for loading media.
 * This handles different Cast SDK versions that may or may not have newer APIs like
 * MediaLoadRequestData, with graceful fallbacks to older APIs.
 */
object CastMediaHelper {

    /**
     * Load media using the most modern available API, with fallbacks.
     * 
     * @param remoteClient The RemoteMediaClient to load media on
     * @param mediaInfo The MediaInfo to load
     * @param autoplay Whether to autoplay the media
     * @return true if any load method was successfully invoked, false otherwise
     */
    fun loadMedia(remoteClient: RemoteMediaClient?, mediaInfo: MediaInfo, autoplay: Boolean = true): Boolean {
        if (remoteClient == null) return false

        // Try MediaLoadRequestData (newest API) via reflection
        if (tryLoadWithMediaLoadRequestData(remoteClient, mediaInfo, autoplay)) {
            return true
        }

        // Try MediaLoadOptions (newer API) via reflection  
        if (tryLoadWithMediaLoadOptions(remoteClient, mediaInfo, autoplay)) {
            return true
        }

        // Fallback to legacy API
        return tryLoadLegacy(remoteClient, mediaInfo, autoplay)
    }

    /**
     * Attempt to use MediaLoadRequestData API via reflection.
     */
    private fun tryLoadWithMediaLoadRequestData(remoteClient: RemoteMediaClient, mediaInfo: MediaInfo, autoplay: Boolean): Boolean {
        return try {
            val mlrdClass = Class.forName("com.google.android.gms.cast.MediaLoadRequestData")
            val builderClass = Class.forName("com.google.android.gms.cast.MediaLoadRequestData\$Builder")
            
            val builder = builderClass.getConstructor().newInstance()
            
            // Set MediaInfo
            builder.javaClass.getMethod("setMediaInfo", MediaInfo::class.java)
                .invoke(builder, mediaInfo)
            
            // Set autoplay if method exists
            try {
                builder.javaClass.getMethod("setAutoplay", Boolean::class.javaPrimitiveType)
                    .invoke(builder, autoplay)
            } catch (_: Throwable) {
                // Some versions might not have setAutoplay, continue without it
            }
            
            val mlrd = builder.javaClass.getMethod("build").invoke(builder)
            
            // Find and invoke the load method that takes MediaLoadRequestData
            val loadMethod = remoteClient.javaClass.methods.firstOrNull { method ->
                val params = method.parameterTypes
                params.size == 1 && params[0].name == mlrd.javaClass.name
            }
            
            if (loadMethod != null) {
                loadMethod.invoke(remoteClient, mlrd)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Attempt to use MediaLoadOptions API via reflection.
     */
    private fun tryLoadWithMediaLoadOptions(remoteClient: RemoteMediaClient, mediaInfo: MediaInfo, autoplay: Boolean): Boolean {
        return try {
            val mediaLoadOptionsClass = Class.forName("com.google.android.gms.cast.MediaLoadOptions")
            val builderClass = Class.forName("com.google.android.gms.cast.MediaLoadOptions\$Builder")
            
            val builder = builderClass.getConstructor().newInstance()
            builder.javaClass.getMethod("setAutoplay", Boolean::class.javaPrimitiveType)
                .invoke(builder, autoplay)
            
            val options = builder.javaClass.getMethod("build").invoke(builder)
            
            // Call remote.load(mediaInfo, options)
            val loadMethod = remoteClient.javaClass.getMethod("load", MediaInfo::class.java, options.javaClass)
            loadMethod.invoke(remoteClient, mediaInfo, options)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Use the legacy load API as final fallback.
     */
    private fun tryLoadLegacy(remoteClient: RemoteMediaClient, mediaInfo: MediaInfo, autoplay: Boolean): Boolean {
        return try {
            @Suppress("DEPRECATION")
            remoteClient.load(mediaInfo, autoplay)
            true
        } catch (e: Exception) {
            false
        }
    }
}