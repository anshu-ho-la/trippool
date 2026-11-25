# Project structure

Where every file goes. If a file you need to create isn't covered here, stop and ask (see `AGENTS.md`). Package name is `com.trippool.app` (confirmed 2026-11-25; applicationId and namespace match it. If it ever changes, replace it everywhere in one commit).

One Gradle module (`:app`). No other modules.

## Repository root

```
trippool/
├── AGENTS.md                 rules for every AI tool (the single source)
├── CLAUDE.md                 one-line pointer to AGENTS.md
├── GEMINI.md                 one-line pointer to AGENTS.md
├── README.md                 what the project is, scope, how to build
├── .github/
│   ├── copilot-instructions.md   one-line pointer to AGENTS.md
│   ├── SETUP.md                  how the GitHub automation and signing secrets are set up
│   ├── dependabot.yml            weekly dependency update PRs
│   └── workflows/                ci.yml (checks every PR), android-apk.yml (signed release APK)
├── .gitignore
├── docs/                     all project documents (see below)
├── scripts/
│   └── check-layers.sh       checks the layer rules in this file; needs only bash
├── third_party/
│   └── licenses/             license texts for bundled fonts (IBM Plex, SIL OFL)
├── testmedia/                local test photos and videos. Git-ignored, never committed
├── gradle/
│   ├── libs.versions.toml    every dependency and its version lives here only
│   └── wrapper/
├── build.gradle.kts          root build file (plugins only)
├── settings.gradle.kts
├── gradle.properties
├── gradlew, gradlew.bat
└── app/                      the Android module
```

## Setting up from the Android Studio template

1. New Project, **Empty Activity** (Compose). Name `TripPool`, package `com.trippool.app`, language Kotlin, **Min SDK API 29**, Kotlin DSL build files.
2. Copy into the project root: `docs/`, `scripts/`, `AGENTS.md`, `CLAUDE.md`, `GEMINI.md`, `.github/`, `README.md`. Merge `.gitignore` with the template's.
3. Create the package folders from the tree below only when a file needs them. Git doesn't keep empty folders; don't add placeholder files.
4. Delete the template's default theme (its purple colors and generated `Theme.kt` content) and replace it with the tokens in `ui/theme/` from `docs/DESIGN.md`.
5. Put dependencies in `gradle/libs.versions.toml` (the template creates it). Add only what the README stack lists.
6. Run `bash scripts/check-layers.sh`, then commit: `chore(build): create android project from template`.

## Docs

```
docs/
├── STATUS.md          progress, next tasks, decisions, open questions. Update every session
├── ROADMAP.md         12-week plan, test scenarios, risks
├── ARCHITECTURE.md    layers, screens, data, sync flow, security basics
├── API_CONTRACT.md    phone-to-phone protocol and test vectors
├── DESIGN.md          look and feel: colors, type, components, copy rules
└── STRUCTURE.md       this file: where files go
```

Which doc decides what:

| Question | Doc that decides |
|---|---|
| What is in scope? | `README.md`, then the submitted proposal |
| What should I work on? | `docs/STATUS.md` |
| How is the code layered, what are the screens, tables, flows? | `docs/ARCHITECTURE.md` |
| What goes over the air, which crypto, test vectors? | `docs/API_CONTRACT.md` |
| How does it look, what does the text say? | `docs/DESIGN.md` |
| Where does this file go, what is it called? | `docs/STRUCTURE.md` |

If two docs disagree, stop and ask. Don't pick one yourself.

## The app module

```
app/
├── build.gradle.kts
├── proguard-rules.pro
├── schemas/                       exported Room schemas. Generated, but committed
└── src/
    ├── main/
    │   ├── AndroidManifest.xml    permissions, foreground service + its type. No INTERNET. allowBackup="false"
    │   ├── res/
    │   │   ├── values/strings.xml every user-visible string
    │   │   ├── font/              IBM Plex Sans + Mono files (a human adds them)
    │   │   ├── xml/               backup_rules.xml, data_extraction_rules.xml (exclude everything)
    │   │   └── mipmap-*/          launcher icon
    │   └── java/com/trippool/app/
    │       ├── TripPoolApp.kt         Application class, @HiltAndroidApp
    │       ├── MainActivity.kt        single activity, sets the theme and the nav host
    │       ├── ui/
    │       │   ├── theme/             Color.kt, Type.kt, Shape.kt, Spacing.kt, Theme.kt (tokens from DESIGN.md)
    │       │   ├── navigation/        Routes.kt, TripPoolNavHost.kt
    │       │   ├── components/        composables shared by 2+ screens (SyncBanner, MediaGrid, QrCard, JoinCodeField, EmptyState)
    │       │   ├── permissions/       permission request UI and the list per Android version
    │       │   ├── triplist/          TripListScreen.kt, TripListViewModel.kt, TripListUiState.kt
    │       │   ├── create/            CreateTripScreen.kt, CreateTripViewModel.kt, CreateTripUiState.kt
    │       │   ├── join/              JoinScreen.kt, QrScanner.kt, JoinViewModel.kt, JoinUiState.kt
    │       │   └── trip/              TripScreen.kt, PhotosTab.kt, ShareTab.kt, TripViewModel.kt, TripUiState.kt
    │       ├── domain/                plain Kotlin, runs on the JVM
    │       │   ├── model/             Trip, MediaItem, ManifestEntry, SyncState, ...
    │       │   ├── crypto/            JoinToken, Hkdf, HmacAuth
    │       │   ├── protocol/          ControlMessage (sealed class), JSON codec, validation of peer data
    │       │   ├── sync/              SyncEngine, ManifestDiff, ChunkPlanner
    │       │   └── port/              interfaces the domain needs from outside: NearbyTransport, Link, HashService, MediaSaver, and the repository interfaces
    │       ├── data/                  implements the ports; the only layer that touches Room, MediaStore, Keystore, DataStore
    │       │   ├── db/                TripPoolDatabase.kt, entity/, dao/, Mappers.kt, migrations/
    │       │   ├── media/             MediaIndexer.kt, MediaSaver implementation, PartFiles.kt (the .part temp files)
    │       │   ├── hash/              HashService implementation (SHA-256 with cache)
    │       │   ├── secret/            SecretVault.kt (Keystore AES-GCM wrapping)
    │       │   ├── settings/          DataStore wrapper (device id, display name, active trip)
    │       │   └── repository/        repository implementations
    │       ├── transport/             the only place that touches com.google.android.gms.nearby
    │       │                          NearbyTransport implementation, Link implementation, payload handling
    │       ├── service/               SyncForegroundService.kt, SyncNotification.kt, SyncRetryWorker.kt (only if retry is built)
    │       └── di/                    Hilt modules: AppModule.kt, DatabaseModule.kt, BindingsModule.kt (port -> implementation)
    ├── test/java/com/trippool/app/    JVM unit tests, same package path as the code they test
    │   ├── domain/crypto/             JoinTokenTest, HkdfTest, HmacAuthTest (use the vectors in API_CONTRACT.md)
    │   ├── domain/protocol/           ControlMessageJsonTest, PeerDataValidationTest
    │   ├── domain/sync/               ManifestDiffTest, ChunkPlannerTest, SyncEngineTest
    │   └── fakes/                     FakeLink, FakeNearbyTransport, FakeHashService, FakeMediaSaver
    └── androidTest/java/com/trippool/app/   on-device tests, only where the JVM can't (Room DAOs, MediaSaver)
        └── data/
```

## Where does it go?

| I need to... | Put it in |
|---|---|
| Add a screen | New folder under `ui/` with `XScreen.kt`, `XViewModel.kt`, `XUiState.kt`. Only if `ARCHITECTURE.md` lists the screen |
| Add a composable used by one screen | In that screen's folder |
| Add a composable used by two or more screens | `ui/components/` |
| Change a color, font size, radius or spacing | `ui/theme/` and `docs/DESIGN.md` together |
| Add a user-visible string | `res/values/strings.xml` |
| Add a protocol message | `domain/protocol/` and `docs/API_CONTRACT.md` (needs approval) |
| Add crypto | `domain/crypto/` (needs approval) |
| Add sync logic | `domain/sync/` |
| Add a Room table, column or query | `data/db/` and `docs/ARCHITECTURE.md` (needs approval) |
| Read or write photos or videos | `data/media/` |
| Call a Nearby API | `transport/` only |
| Need something from Android or the outside world inside `domain/` | Declare an interface in `domain/port/`, implement it in `data/` or `transport/`, bind it in `di/` |
| Add a permission | `AndroidManifest.xml` and `ui/permissions/` (needs approval) |
| Add a dependency | `gradle/libs.versions.toml` (needs approval) |
| Add a test | Mirror path under `src/test/`. A fake goes in `fakes/` |

Helper files that support a class already listed here live in the same package and are named for what they do. They don't need approval. A new package or folder does.

## Layer rules (who may import whom)

```
ui -> domain <- data
       ^
   transport, service
```

- `domain/` imports no Android, Compose, Room, Hilt, Nearby or `org.json` classes, and nothing from `ui`, `data`, `transport`, `service` or `di`. `javax.inject.Inject` is allowed.
- `ui/` imports `domain/` only. No `data/`, no `transport/`, no Room. ViewModels get what they need through interfaces in `domain/port/`.
- `data/` imports `domain/`. Not `ui/`, `transport/` or `service/`.
- `transport/` imports `domain/`. Not `ui/`, `data/` or `service/`.
- `service/` imports `domain/`. Not `ui/`.
- `di/` may import everything. It is the only place that connects ports to implementations.
- `com.google.android.gms.nearby` appears only in `transport/`. `androidx.room` only in `data/db/`. `MediaStore` only in `data/`.
- Colors are written as hex only in `ui/theme/`.

`scripts/check-layers.sh` checks these rules and a few hard rules from `AGENTS.md`. Run it before you hand work over.

## Naming

| Thing | Convention | Example |
|---|---|---|
| Kotlin file | Name of its main class | `ManifestDiff.kt` |
| Composable screen | `XScreen` | `TripListScreen` |
| ViewModel | `XViewModel`, exposes `StateFlow<XUiState>` | `TripViewModel` |
| UI state | `XUiState`, an immutable data class | `TripUiState` |
| Room entity / DAO | `XEntity` / `XDao` | `MediaEntity`, `MediaDao` |
| Interface | A plain noun, no `I` prefix | `Link`, `HashService` |
| Implementation | Interface name plus what it uses, or `Default` | `Sha256HashService`, `NearbyLink` |
| Test | Class under test plus `Test` | `ManifestDiffTest` |
| Test method | What it checks, in backticks | ``fun `skips files whose hash is already held`()`` |
| String resource | `screen_element` in snake_case | `join_type_code_instead` |
| Navigation route | Constants in `Routes.kt` | `Routes.TRIP` |

One top-level class per file, except small sealed-class families that belong together (for example `ControlMessage` and its subtypes).

## Do not create

- Folders or packages named `utils`, `util`, `helpers`, `common`, `managers` or `misc`. Put the code with the thing it serves.
- New top-level packages beyond `ui`, `domain`, `data`, `transport`, `service`, `di`.
- More Gradle modules.
- Files outside the places listed here without asking.
- Generated files, build output, keystores, real photos or secrets in git.

## Adding a new file: check

1. Is it listed or covered above? If not, ask.
2. Does its package follow the layer rules?
3. Does the file name follow the naming table?
4. Did you add or update its test under `src/test/` (or write down why it can't be unit-tested)?
5. Did you update the doc that describes it, if it changes behavior, protocol, schema or structure?
