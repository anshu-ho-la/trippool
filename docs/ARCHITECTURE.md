# Architecture

No server. Two phones talk directly. The only trust anchor is the secret shared in person via QR/code.

## Layers

```
ui (Compose + ViewModels) -> domain (plain Kotlin) <- data (Room, MediaStore, Keystore)
                                   ^
                             transport (Nearby)
```

Dependencies point to `domain`. `domain` has **no Android imports** so it can be tested on the JVM. Only `transport/` touches Nearby; only `data/` touches MediaStore/Room.

```
com.trippool.app
├── ui/         screens: TripList, Create, Join, Trip (gallery + share picker)
├── domain/
│   ├── model/      Trip, MediaItem, ManifestEntry
│   ├── crypto/     JoinToken, Hkdf, HmacAuth
│   ├── protocol/   ControlMessage (sealed class) + JSON codec
│   ├── sync/       SyncEngine, ManifestDiff, ChunkPlanner
│   └── port/       interfaces the domain needs from outside: NearbyTransport, Link, HashService, MediaSaver, repositories
├── data/       implements the ports: Room (db), MediaIndexer, HashService, MediaSaver, SecretVault
├── transport/  Nearby implementation of NearbyTransport + Link
├── service/    SyncForegroundService
└── di/         Hilt modules (connect ports to implementations)
```

One Gradle module (`:app`) is enough. The full folder and file layout, naming and import rules are in `docs/STRUCTURE.md`.

## Screens (keep it small)

Look, copy and states for every screen are in `docs/DESIGN.md`.

1. **Trip list**: your trips; New trip, Join trip.
2. **Create trip**: name -> shows QR + text code (with a warning: anyone who sees it can join).
3. **Join**: scan QR, or type or paste the code.
4. **Trip**: top banner with sync state (Looking for friends / Syncing 12 of 40 / Up to date), "Active" switch, tabs **Photos** (combined gallery, tap opens the system viewer) and **Share** (pick photos/videos to share).

## Key interfaces (declared in `domain/port/`)

```kotlin
interface NearbyTransport {              // implemented in transport/, faked in tests
    val events: Flow<TransportEvent>     // EndpointFound, LinkReady(link), LinkClosed
    suspend fun start(groupTag: String, localName: String)   // advertise + discover
    suspend fun stop()
    suspend fun connect(endpointId: String)
}
interface Link {
    val role: Role                        // INITIATOR or RESPONDER
    val rawAuthToken: ByteArray           // from Nearby; binds HMAC to this link
    val incoming: Flow<LinkMessage>       // Control(msg) or Chunk(meta, data)
    suspend fun send(msg: ControlMessage)
    suspend fun sendChunk(meta: ControlMessage.Chunk, data: ByteArray)
    suspend fun close()
}
interface HashService { suspend fun sha256(uri: String): String }     // cached by (uri, size, modified)
interface MediaSaver  { suspend fun save(part: File, entry: ManifestEntry): SaveResult }
class SyncEngine(...) { suspend fun run(link: Link, trip: Trip, keys: GroupKeys): SyncResult }
```

## Sync flow (one session)

```
connect -> HELLO both ways -> AUTH both ways (close silently if wrong)
-> MANIFEST_PAGE(s) both ways -> diff: what do I not have?
-> for each missing file: FETCH -> FILE_BEGIN -> (CHUNK + payload, CHUNK_ACK)* -> FILE_END -> FILE_RESULT
-> SYNC_DONE both ways -> disconnect
```

A `CHUNK` message and its data payload can arrive in either order. The receiver pairs them by `payloadId` and writes only when it has both.

Receiving a file: append chunk to `files/incoming/<tripId>/<hash>.part` (app-private `filesDir`, not `cacheDir`) -> save offset in Room -> send ack. After the last chunk: check size + SHA-256 -> save via MediaStore -> delete `.part`.

**Free space:** the `.part` file and the MediaStore copy exist at the same time, so check for about 2 x `size` free before sending `FETCH`, and answer `NO_SPACE` cleanly if it is not there.

**Resume:** offset = min(.part length, offset saved in Room). Works across sessions and app restarts.

**Duplicates:** hash already held -> skip (never fetched). Same name, different content -> rename the new file (append `_<first 6 hash chars>`). Never replace.

**Media scan:** `MediaSaver` writes the saved file's URI into `media`. `MediaIndexer` skips URIs it already knows, so a received file is not listed twice.

## Data (Room, version 1)

| Table | Columns |
|---|---|
| `trip` | id (gid hex, PK), name, secret_enc, secret_iv, created_at |
| `media` | id, trip_id, hash (null until hashed), name, size, mime, kind (PHOTO/VIDEO), taken_at, uri, source (LOCAL/RECEIVED), hash_size, hash_modified. Unique (trip_id, hash) |
| `share_selection` | trip_id, media_id (photos the user picked to share) |
| `transfer` | trip_id, hash, direction (IN/OUT), size, offset, state (QUEUED/IN_PROGRESS/INTERRUPTED/SAVED/FAILED), attempts |

Other settings (device id, display name, active trip id) in DataStore. The secret is wrapped with an Android Keystore AES-GCM key; never stored in plaintext. Set `allowBackup="false"`.

Manifest sent to a peer = local media the user picked to share (`share_selection`) that has a hash.

**Planned, only if date-range / auto-share is built:** a `share_rule` table (trip_id, kind DATE_RANGE or AUTO_NEW, from_ms, to_ms) and the manifest becomes picked files plus files matching a rule. Needs team approval because it changes the Room schema.

## Threads and background

- Hashing, file IO, Room: `Dispatchers.IO` (injected). Nothing heavy on Main.
- `SyncForegroundService` owns discovery and sessions (one coroutine per session, one file at a time per direction) and shows one ongoing notification. Needs the foreground service permission and a declared service type. Pick the type in the spike (`connectedDevice` or `dataSync`) and check the Android docs for time limits on newer versions; write the choice in `STATUS.md`.
- WorkManager is only for retrying an interrupted sync when the app is next allowed to run. Don't depend on it for a transfer that is in progress.
- Nearby callbacks are turned into Flows inside `transport/` only.

## Permissions (check official Nearby docs per Android version)

Bluetooth / nearby-devices permissions (differ for API 29-30, 31-32, 33+), Wi-Fi state, location on older versions, camera (QR), photo/video read access (`READ_EXTERNAL_STORAGE` or `READ_MEDIA_*`), notifications (API 33+), foreground service. **No INTERNET permission.**

In the spike, copy the exact permission list for each API range from the Nearby docs into the manifest and write it in `STATUS.md`. On Android 14+ the user may allow only some photos and videos; the indexer must work with whatever it can see and the Share tab must say when access is partial.

## Security basics

- Never send media before both sides pass the HMAC check.
- Validate everything from a peer: sizes, hash format (64 hex), counts, file name (strip `/`, `..`, control chars), MIME must be `image/*` or `video/*`.
- The QR/code is the key: anyone who sees it can join. No way to remove a member (make a new trip). If someone pastes the code into a chat app, that app has the key too.
- `groupTag` in the endpoint name is stable for a trip, so anyone nearby running TripPool can tell phones of one trip are around. It does not reveal the secret.
- Received files live in shared folders, so other apps with media access can read them.
