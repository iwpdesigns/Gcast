# Secure Signing and CI Guide

This project previously contained a locally generated keystore for convenience. That was removed to avoid committing secrets.

This document explains how to securely provide a signing keystore for building Play-ready signed AABs using CI (GitHub Actions example).

## Overview

- Do NOT commit your keystore or passwords to the repository.
- Use your CI provider's secret storage (GitHub Secrets, GitLab CI variables, etc.) to store the keystore (base64-encoded) and passwords.
- In the CI workflow, decode the base64 keystore into a file, export environment variables, and run Gradle to build a signed AAB.

## Recommended GitHub Actions workflow (example)

1. Add the following secrets to your GitHub repository (Settings -> Secrets):
   - `ANDROID_KEYSTORE_BASE64` - base64 of your `.jks` file
   - `ANDROID_KEYSTORE_PASSWORD`
   - `ANDROID_KEY_ALIAS`
   - `ANDROID_KEY_PASSWORD`

2. Sample workflow file (place in `.github/workflows/release.yml`):

```yaml
name: Build Release AAB
on:
  push:
    tags:
      - 'v*'

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Decode keystore
        env:
          KEYSTORE_BASE64: ${{ secrets.ANDROID_KEYSTORE_BASE64 }}
        run: |
          echo "$KEYSTORE_BASE64" | base64 --decode > release.jks

      - name: Build signed AAB
        env:
          ANDROID_KEYSTORE_PATH: ${{ github.workspace }}/release.jks
          ANDROID_KEYSTORE_PASSWORD: ${{ secrets.ANDROID_KEYSTORE_PASSWORD }}
          ANDROID_KEY_ALIAS: ${{ secrets.ANDROID_KEY_ALIAS }}
          ANDROID_KEY_PASSWORD: ${{ secrets.ANDROID_KEY_PASSWORD }}
        run: |
          ./gradlew :app:bundleRelease --no-daemon

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: app-release.aab
          path: app/build/outputs/bundle/release/*.aab
```

## Local testing

If you need to test locally, set environment variables before running Gradle. Example:

```bash
export ANDROID_KEYSTORE_PATH=/path/to/your/release.jks
export ANDROID_KEYSTORE_PASSWORD=yourpassword
export ANDROID_KEY_ALIAS=youralias
export ANDROID_KEY_PASSWORD=yourkeypassword
./gradlew :app:bundleRelease
```

## Notes

- For Play Store distribution, consider using Play App Signing where Google manages the app signing key and you upload an upload key.
- Rotate keys and revoke access if a key is compromised.

If you want, I can add the `.github/workflows/release.yml` example directly to the repo.
