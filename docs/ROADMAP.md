# Roadmap (12 weeks)

Follows the Gantt chart in the proposal. **Core** = the demo cannot work without it. **Promised, cut first** = in the proposal, but dropped first if we fall behind; say so in the final report. **Extra** = not in the proposal. Test every wireless feature on real phones **the week it's built**, not at the end.

| Weeks | Phase | Core | Promised (cut first) / Extra |
|---|---|---|---|
| 1-2 | Planning + spike | Repo, 2-3 test phones, two phones connect via Nearby and send a 100 MB file, measure speed, pick Strategy + chunk size. Also: read `MAX_BYTES_DATA_SIZE`, check payload ordering, choose the foreground service type, write the permission list per Android version | |
| 2-3 | Design | Project skeleton, interfaces from `ARCHITECTURE.md` with fakes, theme tokens and fonts from `DESIGN.md`, screen sketches (trip list, create, join, trip/gallery) | |
| 3-4 | Group + QR | `JoinToken`, HKDF, secret stored with Keystore, create trip (QR + code), join by scan or pasted code | |
| 5-6 | Discovery + auth | Nearby wrapper behind interfaces, permission flow, HELLO/AUTH (HMAC), stranger gets nothing, foreground service skeleton | |
| 6-8 | Media sync | Scan local media, SHA-256 with cache, pick-photos sharing, manifest pages + diff, FETCH + chunks + ack, `.part` file + resume | |
| 8-10 | Gallery + save | Save via MediaStore (skip same hash, rename name clash), one combined gallery, foreground notification | Promised: date-range / auto-share, retry worker |
| 10-12 | Test + finish | Run all test scenarios below on real phones, fix bugs, demo rehearsal, final report | Extra: dark theme polish |

Documentation runs all 12 weeks: update `STATUS.md` every session.

## Test scenarios (real phones)

1. Create on A, join B by QR.
2. A and B share photos; both end with the same set.
3. Third phone without the secret nearby gets nothing.
4. Disconnect mid-video (airplane mode or walk away): resumes, final hash matches.
5. Kill the app mid-transfer, reopen: resumes, nothing corrupt.
6. Same photo on both phones: not duplicated.
7. Two different photos with the same filename: both kept.
8. Screen off with the foreground notification: transfer continues.
9. Join by typing the code (cover the camera): joins, same trip as scanning.
10. Receiver nearly out of space: refuses the file cleanly, no half-saved file, no crash.
11. Android 14 phone with partial photo access: the Share tab shows only allowed items and says so.

## Risks

| Risk | Plan |
|---|---|
| Slow transfer | Measure in the spike and pick the best Strategy/chunk size |
| Few phones | Arrange in Week 1; unit-test the logic with fake links |
| Android differences | Test on different versions; use a foreground service |
| Interrupted transfers | Save progress per chunk; test scenarios 4 and 5 |
| Big videos / low storage | Stream in chunks; check for about 2 x file size free before fetching (the `.part` file and the saved copy exist together) |
| Partial photo access (Android 14+) | Work with what the indexer can see; say so on the Share tab |
| No Google Play Services | State it as a requirement |

## If we fall behind

Cut in this order: date-range/auto-share, retry worker, dark theme, thumbnails polish. Never cut: HMAC check, hash verify, resume, MediaStore save.
