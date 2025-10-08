# GCast - Android Cast Application

A modern Android application for casting media content using Google Cast SDK, featuring a dark theme with colorful gradient effects.

## Features

- ✅ **Modern Cast SDK Integration**: Updated to use Cast Framework 21.5.0 with compatibility helpers
- ✅ **Dark Theme with Gradients**: Firebase-inspired dark design with colorful gradient text
- ✅ **Cast Media Controls**: Cast button with connection state indicators
- ✅ **Media Library**: Videos, photos, and music with cast capabilities
- ✅ **Modern APIs**: Uses latest Cast APIs with graceful fallbacks to legacy methods
- ✅ **CI/CD Ready**: GitHub Actions workflow for automated builds

## Architecture

### Cast SDK Modernization

The app has been modernized to use the latest Cast SDK APIs while maintaining backward compatibility:

- **CastCompat**: Compatibility helper for different Cast SDK versions
- **CastMediaHelper**: Centralized media loading with reflection-based API detection
- **Modern API Support**: Prefers `MediaLoadRequestData` → `MediaLoadOptions` → legacy `load()` methods

### UI Theme

- **Dark Theme**: Rich dark backgrounds (`#0F0F12`, `#121217`)
- **Gradient Accents**: Colorful gradients (`#FF8A00` → `#FF3D81` → `#7C4DFF`)
- **Material 3**: Latest Material Design components
- **Compose UI**: Modern declarative UI framework

## Project Structure

```
app/src/main/java/com/gcast/
├── MainActivity.kt                 # Main activity with Cast initialization
├── cast/
│   ├── CastCompat.kt              # Cast SDK compatibility helper
│   ├── CastMediaHelper.kt         # Centralized media loading utilities
│   └── CastOptionsProvider.kt     # Cast configuration
├── ui/
│   ├── components/
│   │   └── CastButton.kt          # Cast connection button
│   ├── screens/
│   │   └── MainScreen.kt          # Main content screen
│   └── theme/
│       ├── Color.kt               # Theme colors and gradients
│       ├── GradientText.kt        # Gradient text composable
│       ├── Theme.kt               # Material theme setup
│       └── Type.kt                # Typography definitions
└── data/
    └── MediaItem.kt               # Media data models
```

## Building

### Debug Build

```bash
./gradlew :app:assembleDebug
```

### Release Build (Unsigned)

```bash
./gradlew :app:bundleRelease
```

### Signed Release Build

For production builds, configure signing via environment variables:

```bash
export ANDROID_KEYSTORE_PATH=/path/to/your/keystore.jks
export ANDROID_KEYSTORE_PASSWORD=your_store_password
export ANDROID_KEY_ALIAS=your_key_alias  
export ANDROID_KEY_PASSWORD=your_key_password
./gradlew :app:bundleRelease
```

See [SECURE_SIGNING.md](SECURE_SIGNING.md) for detailed CI/CD setup instructions.

## Testing

```bash
# Run unit tests
./gradlew :app:testDebugUnitTest

# Run instrumentation tests
./gradlew :app:connectedDebugAndroidTest
```

## Cast SDK Integration

### Compatibility Strategy

The app uses a multi-tier approach for Cast SDK compatibility:

1. **Modern APIs** (Cast SDK 21.5.0+):
   - `MediaLoadRequestData` with builder pattern
   - Task-based `CastContext` initialization

2. **Intermediate APIs** (Cast SDK 20.0+):
   - `MediaLoadOptions` with autoplay configuration

3. **Legacy APIs** (Fallback):
   - Direct `RemoteMediaClient.load(MediaInfo, boolean)` calls

### Usage Example

```kotlin
// Use the centralized helper
val success = CastMediaHelper.loadMedia(remoteClient, mediaInfo, autoplay = true)
if (!success) {
    Log.w("Cast", "Failed to load media")
}
```

## Dependencies

- **Cast Framework**: `com.google.android.gms:play-services-cast-framework:21.5.0`
- **Compose**: Material 3 with Compose UI toolkit
- **Kotlin**: 2.0.21 with coroutines support
- **Android Gradle Plugin**: 8.13.0

## Development Notes

### Known Issues

- IDE lint may show "Unresolved reference" errors for Kotlin stdlib - these are IDE artifacts and don't affect compilation
- One Kotlin compiler warning about "Condition is always 'true'" in reflection code - this is expected behavior

### Cast SDK Deprecation Warnings

The following deprecation warnings have been addressed:

- ✅ `CastContext.getSharedInstance()` → Uses `CastCompat` helper
- ✅ `RemoteMediaClient.load(MediaInfo, boolean)` → Uses `CastMediaHelper` with modern API preferences

## Contributing

1. Ensure all builds pass: `./gradlew build`
2. Run tests: `./gradlew test`
3. Follow Material Design guidelines
4. Maintain Cast SDK compatibility across versions

## License

This project is for demonstration purposes. Replace with your license as needed.