Deprecation notes: Migrate Cast SDK usage

Files to update:
- `app/src/main/java/com/gcast/ui/components/CastButton.kt`
- `app/src/main/java/com/gcast/ui/screens/MainScreen.kt`

Summary:
- Current code uses deprecated Cast APIs such as `CastContext.getSharedInstance(context)` and `RemoteMediaClient.load(...)`.
- The Cast SDK now recommends the new `SessionManager` APIs and `Session` lifecycle management patterns, and potentially using `CastSession` and `MediaLoadOptions` with updated client methods.

Suggested steps:
1. Replace `CastContext.getSharedInstance(context)` calls with the task-based or `CastContext.getSharedInstance` equivalents that return `ListenableFuture` or use the recommended factory in the latest SDK.
2. Replace direct `RemoteMediaClient.load` calls with the updated `RemoteMediaClient` or `MediaClient` APIs, creating `MediaLoadRequestData`/`MediaLoadOptions` as appropriate.
3. Add robust null checks around `sessionManager.currentCastSession` and handle session lost events gracefully.
4. Run full unit tests and manual device tests.
---

PR Summary (for `chore/modernize-cast-sdk`):

- Replaced fragile CastContext usage with task-aware initialization and safe fallbacks in several UI entry points.
- Updated per-item casting in `MainScreen.kt` to prefer `MediaLoadRequestData` where available (uses reflection to support multiple SDK versions) and fall back to legacy `RemoteMediaClient.load(mediaInfo, autoplay)`.
- Modernized `CastButton.kt` to attach CastState listeners and open the MediaRoute chooser safely from a `FragmentActivity`.
- Added `CastReflection.kt` improvements to attach listeners to `Task` instances when possible and fallback to reflection-based listeners.

Testing steps:

1. Build and install debug APK:

```bash
./gradlew :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.gcast/.MainActivity
```

2. On device: open the app, tap the Cast icon or a media item, choose a Cast receiver, and verify playback starts.

3. If playback fails, capture logs and share:

```bash
adb logcat -s Cast* RemoteMediaClient ActivityManager *:S
```

Notes:
- The code uses reflection to call newer Cast SDK builders when present so the changes remain compatible with older SDK versions. The project compiles successfully locally but runtime behavior depends on the Cast SDK version on the build system.

References:
- Google Cast SDK migration guides: https://developers.google.com/cast/docs

Testing:
- Build and run app, exercise Cast chooser, perform load, and verify playback on receiver.
