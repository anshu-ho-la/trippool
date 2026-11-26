# Status

**Last updated:** 2026-11-26
**Phase:** Project created, no features yet. Proposal submitted 2026-11-19. Next: Week 1-2 spike.

> Update at the end of every work session (humans and AI). Keep it short. Mark unverified things **untested**.

## Next up (put your name on one)

| # | Task | Owner | Status |
|---|---|---|---|
| 2 | Get 2-3 test phones; note model + Android version of each | | todo |
| 3 | Spike: Nearby advertise/discover/connect between 2 phones | | todo |
| 4 | Spike: send a 100 MB file, measure speed per Strategy; try chunk sizes | | todo |
| 5 | `JoinToken` (encode/decode) + HKDF + HMAC with unit tests (use vectors in `API_CONTRACT.md`); needs no phone | | todo |
| 6 | Read `docs/DESIGN.md` as a team; confirm or change the accent color and fonts before any UI code | | todo |
| 7 | Spike extras: read `MAX_BYTES_DATA_SIZE`, test CHUNK vs payload ordering, pick foreground service type, write permission list per Android version | | todo |
| 8 | Add the four signing secrets on GitHub (`.github/SETUP.md` steps 1-4). Until then `main` builds an unsigned debug APK and tags fail | | todo |

## Done

| Date | What |
|---|---|
| 2026-11-19 | Proposal submitted |
| 2026-11-23 | Starter docs written |
| 2026-11-24 | Docs reviewed against the final proposal; test vectors recomputed; `DESIGN.md` added |
| 2026-11-24 | `STRUCTURE.md`, `AGENTS.md` rewrite, pointer files and `scripts/check-layers.sh` added |
| 2026-11-25 | Android project created from the template as `com.trippool.app`, docs and `scripts/` moved in, theme tokens added (was Next-up task 1) |
| 2026-11-25 | GitHub automation: `ci.yml` (layer check, tests, lint, debug build on every PR), `android-apk.yml` (APK on push to `main` and tags), Dependabot |
| 2026-11-26 | PR template and `.gitattributes` added; type scale mapping and `surfaceTint` fixed |

## Decisions to confirm as a team

- **Duplicates:** skip a file if its hash is already held; if only the name clashes, rename the new file; never replace. (Proposal says "replace or rename"; this is safer and simpler.)
- ~~**Package name:** `com.trippool.app`?~~ Decided 2026-11-25: `com.trippool.app`.
- **License / repo host:** decide before sharing the repo.
- **Sharing modes:** start with "pick photos" only; add date range / auto-share later if time allows. The proposal promises all three, so if they are dropped, say so in the final report.
- **Join code length:** the text code is 40 characters (25-byte token). The proposal calls it "short". Option: drop `gid` and derive the group id from the secret, giving about 28 characters. That changes the protocol and the test vectors, so decide before coding `JoinToken`.
- **Old `.part` files:** delete after 7 days with no progress? (Proposed, not decided.)
- **Design direction:** accent color, fonts and the no-gradient, no-shadow rules in `docs/DESIGN.md` are a proposal. Fonts are IBM Plex Sans and Mono (SIL OFL), bundled in the app.

## Open technical questions (answered by the spike)

- Which Nearby `Strategy` is fastest for 2-3 phones? (default: `P2P_CLUSTER`)
- Chunk size (default 1 MiB) and how to send a chunk (default: `Payload.fromStream` over a byte range)
- Exact permissions per Android version
- Does syncing survive screen-off on our phones?

## Blockers

None yet.

## Unverified assumptions

- Nearby's `MAX_BYTES_DATA_SIZE` value. Google's reference lists about 1 MB; our 28,000-byte cap is below it either way. Read the constant in code and write it here.
- `ConnectionInfo.getRawAuthenticationToken()` exists (listed in Google's `ConnectionInfo` reference, 2026-11-24). **Untested:** that both phones get the same value on a real link.
- A `CHUNK` message and its data payload can arrive in either order (Google's sample code says BYTES and FILE payloads may arrive in any order). Test it.

## Verified

- All test vectors in `API_CONTRACT.md` (token, text code, `k_auth`, `k_tag`, `groupTag`, AUTH proof) were recomputed independently on 2026-11-24 and match.

## Session log (newest first)

- 2026-11-26: added `.github/pull_request_template.md` (AGENTS.md summary format and checklist) and `.gitattributes` (LF for `gradlew` and `.sh`, CRLF for `.bat`). Hand-over command in `AGENTS.md` and `README.md` is now `./gradlew testDebugUnitTest lintDebug assembleDebug`, matching CI. Committing and pushing directly to `main` is allowed (decided 2026-11-26; the branch and PR rules were removed from `AGENTS.md`). `SETUP.md` gained the no-secrets note. Theme: `Type.kt` now maps Title to `titleLarge` and `headlineSmall`, Title large to `headlineMedium` (the top app bar uses `titleLarge`, so titles were 28sp); mapping written into `DESIGN.md` section 3. `surfaceTint` is transparent in both schemes. Not compiled, not run.
- 2026-11-25: added GitHub automation: `ci.yml`, `android-apk.yml` (signed release APK with secrets, debug APK without), `dependabot.yml` and `.github/SETUP.md`. Untested until pushed to GitHub.

- 2026-11-25: moved the docs, pointer files and `scripts/` into the project. Renamed the template package from `com.personalhub.trippool` to `com.trippool.app` (namespace, applicationId, folders, package lines). Created the folder tree from `STRUCTURE.md` (empty folders, local only). Added `AGENTS.md` sections (team, outside content, environment, stuck). Set `allowBackup="false"` and made `backup_rules.xml` / `data_extraction_rules.xml` exclude everything. Replaced the template theme with the DESIGN.md tokens (`Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`, `Theme.kt`; no dynamic color, no purple). Not compiled, not run. Assumptions: Plex fonts not added yet, so system fonts are used (change `UiFontFamily` / `CodeFontFamily` in `Type.kt` once the files are in `res/font`); Material slots DESIGN.md does not name are mapped to the nearest token (see the comment in `Theme.kt`). The palette is still a proposal until the team confirms it (task 6).

- 2026-11-24: added `STRUCTURE.md`, rewrote `AGENTS.md` (docs first, ask before deviating, verify before handover, git rules), added pointer files and `scripts/check-layers.sh`. Fixed `ARCHITECTURE.md`: the Nearby/`Link` interfaces are declared in `domain/port/`, not `transport/`. No code yet.
- 2026-11-24: reviewed docs against the final proposal. Fixed scope labels, bytes-limit claim, missing ERROR message, `.part` location, free-space rule; added `DESIGN.md`. No code yet.
- 2026-11-23: wrote starter docs from the proposal. No code yet.
