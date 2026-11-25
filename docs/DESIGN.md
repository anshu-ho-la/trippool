# Design

Design read: a photo-first utility for friends on a trip, used one-handed and outdoors. Warm neutral surfaces, one teal accent, flat, calm motion.

Scope: the Android app only. We use Material 3 components restyled with the tokens below. Where a rule here conflicts with a Material default, this file wins. Change tokens only with team agreement, in the same change as the code.

## 1. Atmosphere & Vibe

- The photos are the interface. Everything else stays quiet so the pictures carry the color.
- It should feel like a file manager that understands trips: dependable, plain, a little warm. Not a social app, not a dashboard.
- Three moments get real care: showing the QR and join code, watching a sync make progress, opening the pooled gallery.
- Not allowed: gradients, glass or blur, illustration headers, emoji as icons, onboarding carousels, confetti, sparkle icons, stock photos.

## 2. Color Palette & Functional Roles

One accent. No second hue. Pure black and pure white are never used as page fills.

| Role | Light | Dark | Material 3 slot |
|---|---|---|---|
| Background | `#FCFCFC` | `#0C0A09` | `background`, `surface` |
| Raised surface (rows, banner, sheets) | `#F2F1EF` | `#1C1917` | `surfaceContainer` |
| Divider (decorative) | `#E7E5E4` | `#292524` | `outlineVariant` |
| Outline (input borders, inactive icons) | `#78716C` | `#78716C` | `outline` |
| Text primary | `#0C0A09` | `#F5F5F4` | `onBackground`, `onSurface` |
| Text secondary | `#57534E` | `#A8A29E` | `onSurfaceVariant` |
| Accent | `#0F766E` | `#2DD4BF` | `primary` |
| On accent | `#FCFCFC` | `#0C0A09` | `onPrimary` |
| Error | `#B91C1C` | `#F87171` | `error` |

Measured contrast: text primary on background 19.3:1 (light) and 18.1:1 (dark); text secondary 7.4:1 and 7.8:1; accent with on-accent 5.3:1 and 10.6:1; outline 4.7:1 and 4.1:1. Text needs 4.5:1, borders 3:1.

Rules:
- Accent is for the primary button, the active tab indicator, progress, switches that are on, and links. Never for backgrounds or decoration.
- State is never shown by color alone. "Syncing", "Up to date" and problems are text plus an icon.
- Error red is only for failures the user must act on and for destructive confirmations.
- One exception to the no-pure-color rule: the QR code is `#000000` on `#FFFFFF` with a 16dp quiet zone, because scanners need it. It sits inside a card, never as a page fill.
- Photos and videos are never tinted, overlaid or recolored. The only overlay is the video duration label.
- Set `dynamicColor = false`. Android's wallpaper colors must not replace this palette.

## 3. Typography System

Two families, both SIL Open Font License, bundled in `res/font/` with their license files in the repo:
- **IBM Plex Sans** for all UI text (Regular 400, Medium 500, SemiBold 600).
- **IBM Plex Mono** (Medium 500) only for the join code, so similar characters are easy to tell apart when reading it aloud.

| Style | Size / line height | Weight | Use |
|---|---|---|---|
| Title large | 28sp / 34 | 600 | Trip name on the trip screen |
| Title | 20sp / 26 | 600 | Screen titles, dialog titles |
| Body | 16sp / 24 | 400 | Default text, list row title (500) |
| Body small | 14sp / 20 | 400 | Row subtitles, hints, warnings |
| Label | 14sp / 20 | 500 | Buttons, tabs |
| Caption | 12sp / 16 | 400 | Counts, durations, dates |
| Code | 22sp / 32 | 500 mono | Join code, letter spacing 0.5sp |

Rules:
- Sentence case everywhere. The join code is the only uppercase text.
- No text below 12sp and no weight below 400.
- Use tabular figures (`fontFeatureSettings = "tnum"`) for counts like "12 of 40" so they don't jitter while syncing.
- Text must survive the system font scale at 200%. Wrap, don't clip.

## 4. Component Stylings

All containers use the same 8dp radius (section 5). Media tiles are square-cornered because the photo fills the tile edge to edge.

- **Buttons.** Primary: accent fill, on-accent text, 48dp minimum height. Secondary: 1dp outline, primary text. Tertiary: text button. One primary button per screen. Labels are verbs in sentence case: "Create trip", "Join trip", "Copy code". No icon unless it adds meaning.
- **Text fields.** Outlined, 1dp outline, 2dp accent when focused. The join field uses the Code style, accepts pasted text with hyphens and spaces, and shows an exact message while typing, for example "28 of 40 characters".
- **Trip list.** Flat full-width rows, not cards. Title (Body, 500), subtitle in Body small secondary ("42 photos and videos"), 1dp divider between rows, 64dp minimum height. The row for the active trip shows its sync state as text on the right. The two actions sit in a bottom row: "Join trip" (secondary) and "Create trip" (primary).
- **Sync banner** (top of the trip screen). Raised surface, full width, 48dp, an icon and one line of text. States: "Looking for friends", "Syncing 12 of 40" with a 4dp determinate progress bar under it, "Up to date", and problems with a text button on the right: "Bluetooth is off" (Turn on), "Nearby devices are not allowed" (Allow), "Not enough space" (Free up space). The Active switch is in the top bar.
- **Tabs.** Two tabs, Photos and Share, with a 2dp accent indicator.
- **Gallery grid.** `GridCells.Adaptive(112.dp)`, 2dp gaps, square crop, no radius. Videos show their duration bottom-left in Caption on a solid 60% black label (no gradient). Tapping opens the system viewer.
- **Share picker.** The same grid. A selected tile gets a 3dp inset accent border and a check icon top-right. Partial photo access (Android 14+) shows a line above the grid: "You allowed access to some photos. Change this in Settings."
- **Create trip.** The QR is centered, at least 240dp, on its white card. Below it, the join code in 8 groups of 5, two lines of four, and a "Copy code" button. Warning in Body small: "Anyone who sees this code can join the trip. Only show it to people you trust." The screen stays awake while it is shown.
- **Join.** Full-screen camera preview with one line, "Point the camera at the QR code on the other phone", and a text button "Type the code instead".
- **Dialogs.** Material alert dialog with 8dp corners. Only for permission explanations and confirmations. A destructive confirm uses error-colored text, never a red filled button.
- **Progress.** Determinate wherever the amount is known (hashing, transfer). Indeterminate only while "Looking for friends". No spinners over content.
- **Empty states.** A title, one sentence, one or two buttons. No illustration.
  - Trip list: "No trips yet" / "Create a trip, or join one a friend made."
  - Photos tab: "Nothing here yet" / "Photos you share and photos friends send will show up here."
  - Share tab without permission: "Allow access to your photos and videos to choose what to share." with the button "Allow access".

## 5. Spacing & Layout Grid

- Base unit 4dp. Scale: 4, 8, 12, 16, 24, 32, 48.
- Screen side padding 16dp. Space between sections 24dp. List rows 16dp vertical padding.
- One radius for the whole app: 8dp for buttons, fields, banner, dialogs and sheet tops. Media tiles are 0dp. The system switch and checkbox keep their own shape.
- Small top app bar, title left-aligned. No bottom navigation bar and no floating action button.
- Single column. On wide screens content is capped at 600dp and centered; only the gallery gets more columns.
- Draw edge to edge and apply system bar insets.

## 6. Elevation & Depth

- Flat. `tonalElevation = 0.dp` and `shadowElevation = 0.dp` on every surface.
- Separation comes from the raised surface color and 1dp dividers or outlines.
- Scrims use the Material default (32% black). No blur, no glass.
- Motion: Material default fade-through between screens (200 to 300ms). Progress bars animate value changes in 200ms or less. No decorative or looping animation. Respect the system "remove animations" setting.

## 7. Design Do's & Don'ts

Do:
- Use real data in previews and screenshots: real counts, real file names, real test photos. No lorem ipsum, no gray placeholder boxes.
- Say what happened and what to do next: "Bluetooth is off. Turn it on to find friends."
- Keep one primary action per screen.
- Give every failure a next step or a plain reason.
- Keep protocol words out of the UI. Say "friends nearby", never "peers", "endpoints", "AUTH" or "HMAC".
- Show nothing to a stranger's failed check; the phone just doesn't sync with them.

Don't:
- Use the default Material purple theme, dynamic color, gradients, shadows, glass or blur.
- Use emoji or sparkle icons, stock images, or illustration headers.
- Write marketing copy ("seamless", "effortless", "magic"), exclamation marks, em dashes or en dashes. Use a comma or a full stop.
- Mix pill buttons with 8dp fields, or add a second accent color.
- Add a splash screen beyond the system one, an onboarding carousel, or a stats-card dashboard.
- Fake progress. If the amount is unknown, show indeterminate and say what is happening.

## 8. Responsive Protocols

- Phone portrait is the main layout. Landscape must work: screens scroll and the gallery adds columns.
- At 200% font scale the banner wraps to two lines, buttons grow, and error text is never cut off.
- Touch targets are at least 48dp. Gallery tiles are at least 96dp.
- Dark theme follows the system. Both palettes exist from the start, so dark is only a matter of selecting the dark scheme. Polishing it is an Extra, not a promise.
- Accessibility: every tile has a description such as "Photo, 14 Nov" or "Video, 0:42, 14 Nov". The sync banner is a polite live region. The QR has a description: "QR code for joining {trip name}. The join code is below."
- Outdoor use: text contrast at least 4.5:1, no thin weights.

## 9. Agent Prompt Directives

For AI coding tools that touch UI:
- Read this file first. Put every token in `ui/theme/` (`Color.kt`, `Type.kt`, `Shape.kt`, `Spacing.kt`) once, then only reference tokens. No hard-coded hex or dp in screens, except the adaptive grid cell size.
- Build the theme from the tables above: `lightColorScheme` and `darkColorScheme` from section 2, `Typography` from section 3, `Shapes` with all five sizes set to 8dp, `dynamicColor = false`, zero elevation.
- Use only Material 3 components and the outlined icon set. Don't add an icon or UI library without human approval.
- Don't download fonts. A human adds the Plex font files and license text.
- Every new screen or state needs the empty, loading and error versions written in sentence case with a next step.
- Add `@Preview` functions with real test data in light, dark and 200% font scale.
- If a screen isn't listed in `docs/ARCHITECTURE.md`, stop and ask.
- At the end, list which tokens or components you added or changed.
