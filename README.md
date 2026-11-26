# TripPool

Share original-quality trip photos and videos directly between friends' Android phones. No cloud, no account, no quality loss.

Kathmandu University, Dept. of Computer Science and Engineering, COMP 206. Team: Sankalpa Adhikari, Sauhard Bajracharya, Dilasha Baral, Shayuna Basnet, Anshu Bhandari.

**Status:** see [`docs/STATUS.md`](docs/STATUS.md).

## How it works

1. One person **creates a trip**; the app shows a QR code and a text join code.
2. Friends **join in person** by scanning the QR or typing the code. No internet.
3. Each person **picks photos/videos to share**.
4. When two phones are near each other they **verify** they share the trip secret (HMAC, tied to the Nearby link), **swap file lists** (hash, size, name), and send only the **missing originals in resumable chunks** over Google Nearby Connections.
5. Received files are checked with **SHA-256** and saved to `Pictures/TripPool` / `Movies/TripPool` via MediaStore. One gallery shows everything.

## Scope

The submitted proposal (2026-11-19) is the source of truth for what we promised.

**Core (build first):** create/join trip (QR + code), pick files to share, Nearby discovery, HMAC check, manifest diff, resumable chunked transfer, SHA-256 verify, MediaStore save, one combined gallery, foreground service while syncing.

**Promised in the proposal, cut first if we fall behind:** share by date range, auto-share new media, automatic retry of interrupted syncs. If any of these is dropped, say so in the final report.

**Extra (not in the proposal):** dark theme polish.

**Out of scope:** internet sync, iOS, cloud, editing, removing members, relaying other people's files, leave-trip, friends list screen.

## Stack

Kotlin, Jetpack Compose with Material 3 components and the TripPool theme ([`docs/DESIGN.md`](docs/DESIGN.md)), Navigation Compose, MVVM, Coroutines/Flow, Hilt, Room, DataStore, Nearby Connections (`play-services-nearby`), CameraX + ML Kit (scan QR), ZXing (make QR), Coil, kotlinx.serialization, WorkManager (retry only). Min SDK 29. Needs Google Play Services. Wireless features need **2-3 real phones** (emulators can't test them).

## Build

```bash
bash scripts/check-layers.sh
./gradlew testDebugUnitTest lintDebug assembleDebug   # same as CI
./gradlew installDebug
```

On Windows use `gradlew.bat` and run the `.sh` script in Git Bash. Work on a branch and open a pull request into `main`; CI must be green before merge. Setup of the GitHub automation and signing secrets: [`.github/SETUP.md`](.github/SETUP.md).

## Docs

| File | What |
|---|---|
| `AGENTS.md` | Rules for every AI coding tool (`CLAUDE.md`, `GEMINI.md` and `.github/copilot-instructions.md` point to it). Read first. |
| `docs/STATUS.md` | Progress, next tasks, open questions. Update every session. |
| `docs/ROADMAP.md` | 12-week plan |
| `docs/ARCHITECTURE.md` | Packages, data, flows |
| `docs/API_CONTRACT.md` | Phone-to-phone protocol + test vectors |
| `docs/DESIGN.md` | Look and feel: colors, type, shapes, components, copy rules |
| `docs/STRUCTURE.md` | Where every file goes, naming, import rules |
| `scripts/check-layers.sh` | Checks the layer rules; run before handing work over |
