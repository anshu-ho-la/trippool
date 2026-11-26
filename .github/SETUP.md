# Automation setup

Two workflows run on GitHub:

- **CI** checks every pull request: unit tests, lint, debug build.
- **Build Android APK** runs on every push to `main` and on a tag such as `v1.0.0`. It runs the tests, builds a signed
  release APK and keeps it as a download on the run (30 days). A tag also publishes it as a GitHub Release.
  Until the signing secrets below are added, a push to `main` builds an unsigned debug APK instead, and a tag fails
  (so an unsigned app is never published).

Dependabot opens a pull request each week for outdated libraries and workflow actions.

## One-time setup

1. Make a signing key (keep the file and its passwords safe; losing them means phones cannot update the app):

   ```
   keytool -genkeypair -v -keystore trippool.jks -alias trippool -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Turn it into text (PowerShell) and copy it:

   ```
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("trippool.jks")) | Set-Clipboard
   ```

3. On GitHub: Settings > Secrets and variables > Actions > New repository secret. Add these four:

   | Secret | Value |
   | --- | --- |
   | `ANDROID_KEYSTORE_BASE64` | the text from step 2 |
   | `ANDROID_KEYSTORE_PASSWORD` | the keystore password |
   | `ANDROID_KEY_ALIAS` | `trippool` |
   | `ANDROID_KEY_PASSWORD` | the key password |

4. Settings > Actions > General > Workflow permissions: allow read and write (needed to publish a release).

Never commit the `.jks` file.

## Releasing

```
git tag v1.0.0
git push origin v1.0.0
```

The APK appears under Releases. Each build's version code is the run number, so every new build installs over the last.
