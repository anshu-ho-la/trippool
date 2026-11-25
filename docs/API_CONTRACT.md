# API Contract (phone-to-phone protocol v1)

Draft. Values marked **[SPIKE]** may change after the Week 1-2 test. Change this file only with team agreement, in the same change as the code.

## Transport

- Google Nearby Connections. Strategy **[SPIKE]**: start with `P2P_CLUSTER`.
- Control messages: `Payload.BYTES`, UTF-8 JSON, max **28,000 bytes** each. This is our own conservative cap. Read Nearby's `MAX_BYTES_DATA_SIZE` in the spike and write it in `STATUS.md` (Google's reference lists about 1 MB, but we don't depend on it).
- File data: split into chunks. Each chunk = a `CHUNK` message, then one data payload whose id is in `payloadId` **[SPIKE: stream vs temp file]**. Default chunk size 1 MiB.
- Nearby already encrypts the link. TripPool adds a **group check** on top (below).
- **Ordering:** Nearby does not promise that a BYTES payload and a STREAM/FILE payload arrive in the order they were sent. The receiver keeps the `CHUNK` message and the data payload by `payloadId` and handles a chunk once it has both.

## Conventions

- In formulas, `|` means byte concatenation with no separators.
- Every message is JSON with `"v":1` and `"t":"<TYPE>"`. Use a kotlinx.serialization sealed class with `classDiscriminator = "t"` and `ignoreUnknownKeys = true`.
- Binary fields: base64url without padding. Hashes: 64 lowercase hex chars (SHA-256). Times: epoch ms. Sizes/offsets: bytes (Long).
- Before both sides pass AUTH, only `HELLO`, `AUTH` and `ERROR` are accepted. Anything else: close silently.

## Discovery and connecting

- Advertise and discover only while the trip is **Active** (one active trip per phone).
- Endpoint name: `TP1|<groupTag>|<deviceShort>`. `groupTag` = 12 hex chars (see crypto). Ignore endpoints with a different tag. This is only a filter; the HMAC check is the real gate. The tag is stable for a trip, so it shows that same-trip phones are nearby but reveals no secret.
- When two phones find each other, the one with the smaller `deviceShort` calls `requestConnection` (role `I`), the other is `R`. Both accept.
- Handshake deadline: 10 s to complete HELLO + AUTH, else disconnect.

## Messages

**HELLO** (both sides, first)
```json
{"v":1,"t":"HELLO","deviceId":"<uuid>","name":"Dilasha","nonce":"<16 bytes b64url>"}
```

**AUTH** (both sides, after receiving the peer's HELLO)
```json
{"v":1,"t":"AUTH","proof":"<32 bytes b64url>"}
```
`proof = HMAC-SHA256(k_auth, "TP1-AUTH" | role | nonceSelf | noncePeer | u16-BE(len(rat)) | rat | deviceIdSelf(16 bytes))`
where `rat` = `ConnectionInfo.getRawAuthenticationToken()`, `role` = ASCII `I` or `R`, `deviceIdSelf` = the sender's `deviceId` UUID as 16 raw bytes. Each side recomputes the *peer's* expected proof (opposite role, swapped nonces, peer's deviceId) and compares with `MessageDigest.isEqual`. Wrong proof: disconnect, send nothing.

**MANIFEST_PAGE** (repeat until `last:true`)
```json
{"v":1,"t":"MANIFEST_PAGE","page":0,"last":true,"entries":[
 {"hash":"<64 hex>","name":"IMG_2031.jpg","size":4182034,"mime":"image/jpeg","kind":"PHOTO","takenAt":1767000000000}]}
```
Max 100 entries and 28,000 bytes per page. `kind` = `PHOTO` or `VIDEO`. Empty manifest = one page with `entries:[]` and `last:true`. Contains only files the user chose to share.

**FETCH** (receiver asks; one file at a time)
```json
{"v":1,"t":"FETCH","hash":"<64 hex>","offset":0}
```
**FETCH_REJECT**: `{"v":1,"t":"FETCH_REJECT","hash":"...","reason":"NOT_FOUND"}` (`NOT_FOUND | BAD_OFFSET`)

**FILE_BEGIN** (sender): `{"v":1,"t":"FILE_BEGIN","hash":"...","size":4182034,"offset":0,"chunkSize":1048576}`
Receiver rejects a `size` that differs from the manifest.

**CHUNK** (sender), followed by the data payload
```json
{"v":1,"t":"CHUNK","hash":"...","index":0,"offset":0,"length":1048576,"payloadId":123456789}
```
Receiver checks `offset` equals its current `.part` length, then appends.

**CHUNK_ACK** (receiver): `{"v":1,"t":"CHUNK_ACK","hash":"...","nextOffset":1048576}`. Sent after the chunk is written and the offset saved. Sender waits for it before the next chunk (window = 1). No ack in 15 s: abort the file, retry (max 3). **[SPIKE]** the timeout may need to grow with chunk size on slow links.

**FILE_END** (sender): `{"v":1,"t":"FILE_END","hash":"..."}`

**FILE_RESULT** (receiver): `{"v":1,"t":"FILE_RESULT","hash":"...","ok":true}` or `ok:false` with `reason` = `HASH_MISMATCH | SIZE_MISMATCH | NO_SPACE | SAVE_FAILED`. On `HASH_MISMATCH`: delete `.part`, re-fetch once from offset 0.

**SYNC_DONE** (both sides when nothing more to fetch): `{"v":1,"t":"SYNC_DONE"}`. After both sent it, disconnect.

**ERROR** (either side, any time): `{"v":1,"t":"ERROR","code":"UNSUPPORTED_VERSION"}`. `UNSUPPORTED_VERSION` is the only code in v1. After sending or receiving `ERROR`, disconnect.

## Resume

Receiver keeps `cache/incoming/<tripId>/<hash>.part` and the saved offset (Room). Resume offset = `min(part length, saved offset)`; truncate the file to it; send `FETCH(hash, offset)`. Works in a later session and after an app restart, as long as the peer still offers that hash.

## Validate what you receive

Close the connection if: JSON doesn't parse, unknown message type after AUTH, page over limits, more than 50,000 manifest entries, `size` over 8 GiB, hash isn't 64 hex, `mime` isn't `image/*` or `video/*`, chunk longer than `chunkSize`, data length differs from `length`. Sanitize `name` (strip `/`, `\`, `..`, control characters, cap length) before saving.

## Join code and crypto

- `gid` = 8 random bytes, `secret` = 16 random bytes (`SecureRandom`).
- **Token** (25 bytes) = `0x01 | gid | secret`.
- **Text code** = Crockford Base32 of the token = exactly 40 chars, shown as 8 groups of 5 separated by `-`. Alphabet `0123456789ABCDEFGHJKMNPQRSTVWXYZ`. Decoding ignores case, `-` and spaces; maps `I,L -> 1`, `O -> 0`.
- **QR** content: `trippool://join/<40-char code without hyphens>`.
- **Keys** (HKDF-SHA256, RFC 5869, `IKM=secret`, `salt=gid`, 32 bytes): `k_auth = HKDF(..., info="trippool/v1/auth")`, `k_tag = HKDF(..., info="trippool/v1/tag")`.
- **groupTag** = hex of the first 6 bytes of `HMAC-SHA256(k_tag, "TP1-TAG")` (12 hex chars).
- Nonces: 16 bytes from `SecureRandom`, new for every connection.

### Test vectors (from a reference implementation; your code must match)

```
gid         = 0011223344556677
secret      = 101112131415161718191a1b1c1d1e1f
token       = 010011223344556677101112131415161718191a1b1c1d1e1f
text code   = 04012-8HK8H-APCXR-G2491-650N2-RBHG6-8T3CE-1T7GZ
k_auth      = 330ee0ed3fd57a0c37efa2ff85dd1097639f59434ebcf3077fd64ffa6c85b61c
k_tag       = beeb8f84782663db3219be54f3f4896255c7d23ea43aa336b665d53602d0384d
groupTag    = d1688f7134de

AUTH proof, role I:
  nonceSelf    = a0a1a2a3a4a5a6a7a8a9aaabacadaeaf
  noncePeer    = c0c1c2c3c4c5c6c7c8c9cacbcccdcecf
  rat          = deadbeef  (length prefix 0004)
  deviceIdSelf = 11111111111111111111111111111111
  proof        = ca55960b8c8da921ebf9391f23a56b0caffbc145096d39008488a597a2b3ed81
```

## Versioning

`v` is an int. If a peer's `v` differs, send `ERROR` with `UNSUPPORTED_VERSION` right after HELLO and disconnect. Adding optional fields doesn't bump `v`; receivers ignore unknown fields. Breaking changes bump `v` and update this file.
