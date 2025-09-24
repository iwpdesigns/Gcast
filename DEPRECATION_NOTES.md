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

References:
- Google Cast SDK migration guides: https://developers.google.com/cast/docs

Testing:
- Build and run app, exercise Cast chooser, perform load, and verify playback on receiver.
