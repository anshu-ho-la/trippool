#!/usr/bin/env bash
# Checks the layer rules in docs/STRUCTURE.md and a few hard rules from AGENTS.md.
# Needs only bash and grep. Run from the repo root:  bash scripts/check-layers.sh
# Exit code 0 = no violations, 1 = at least one violation.

APP="app/src/main"
SRC="$APP/java/com/trippool/app"
P='com\.trippool\.app'
fail=0

if [ ! -d "$SRC" ]; then
  echo "No source folder at $SRC yet. Nothing to check."
  exit 0
fi

# bad "<message>" "<dir>" "<regex>"  : fails if any .kt file in <dir> has a line matching <regex>
bad() {
  local msg="$1" dir="$2" re="$3" hits
  [ -d "$dir" ] || return 0
  hits=$(grep -rnE --include='*.kt' "$re" "$dir" 2>/dev/null)
  if [ -n "$hits" ]; then
    echo "FAIL: $msg"; echo "$hits" | head -10 | sed 's/^/    /'; fail=1
  fi
}

# outside "<message>" "<regex>" "<allowed path fragment>" : fails if a match is found outside the allowed place
outside() {
  local msg="$1" re="$2" ok="$3" hits
  hits=$(grep -rnE --include='*.kt' "$re" "$SRC" 2>/dev/null | grep -v "$ok")
  if [ -n "$hits" ]; then
    echo "FAIL: $msg"; echo "$hits" | head -10 | sed 's/^/    /'; fail=1
  fi
}

# --- layer rules ---
bad "domain/ must not import Android, Compose, Room, Hilt, Nearby or org.json" \
    "$SRC/domain" '^import (android\.|androidx\.|com\.google\.android\.|dagger\.|org\.json)'
bad "domain/ must not import ui, data, transport, service or di" \
    "$SRC/domain" "^import $P\.(ui|data|transport|service|di)\."
bad "ui/ must import domain only (no data, transport, Room)" \
    "$SRC/ui" "^import ($P\.(data|transport)\.|androidx\.room)"
bad "data/ must not import ui, transport or service" \
    "$SRC/data" "^import $P\.(ui|transport|service)\."
bad "transport/ must not import ui, data or service" \
    "$SRC/transport" "^import $P\.(ui|data|service)\."
bad "service/ must not import ui" \
    "$SRC/service" "^import $P\.ui\."
outside "com.google.android.gms.nearby is allowed only in transport/" \
    '^import com\.google\.android\.gms\.nearby' '/transport/'
outside "androidx.room is allowed only in data/db/" \
    '^import androidx\.room' '/data/db/'
outside "MediaStore is allowed only in data/" \
    '^import android\.provider\.MediaStore' '/data/'

# --- hard rules ---
bad "no GlobalScope or runBlocking in app code" "$APP/java" '(GlobalScope|runBlocking)'
bad "no !! in app code" "$APP/java" '[]A-Za-z0-9_)]!!'
if [ -f "$APP/AndroidManifest.xml" ] && grep -q 'android.permission.INTERNET' "$APP/AndroidManifest.xml"; then
  echo "FAIL: INTERNET permission is not allowed"; fail=1
fi
if [ -f "$APP/AndroidManifest.xml" ] && ! grep -q 'allowBackup="false"' "$APP/AndroidManifest.xml"; then
  echo "FAIL: AndroidManifest.xml needs android:allowBackup=\"false\""; fail=1
fi

# --- design rules ---
hits=$(grep -rnE --include='*.kt' 'Color\(0x[0-9A-Fa-f]+\)' "$SRC" 2>/dev/null | grep -v '/ui/theme/')
if [ -n "$hits" ]; then
  echo "FAIL: colors may be written as hex only in ui/theme/"; echo "$hits" | head -10 | sed 's/^/    /'; fail=1
fi
STR="$APP/res/values/strings.xml"
if [ -f "$STR" ] && grep -nF -e $'\xe2\x80\x94' -e $'\xe2\x80\x93' "$STR" >/dev/null; then
  echo "FAIL: strings.xml must not contain em dashes or en dashes"; fail=1
fi

# --- placement rules ---
junk=$(find "$SRC" -type d \( -name utils -o -name util -o -name helpers -o -name common -o -name managers -o -name misc \) 2>/dev/null)
if [ -n "$junk" ]; then echo "FAIL: forbidden folder names:"; echo "$junk" | sed 's/^/    /'; fail=1; fi
for d in "$SRC"/*/; do
  [ -d "$d" ] || continue
  n=$(basename "$d")
  case "$n" in ui|domain|data|transport|service|di) ;; *) echo "FAIL: unknown top-level package '$n' (allowed: ui domain data transport service di)"; fail=1;; esac
done

# --- warning only (cannot be checked reliably by grep) ---
w=$(grep -rniE --include='*.kt' 'Log\.[a-z]\(.*(secret|nonce|proof|joincode|token|key)' "$SRC" 2>/dev/null | head -5)
if [ -n "$w" ]; then echo "WARN: possible logging of secrets. Check by hand:"; echo "$w" | sed 's/^/    /'; fi

if [ $fail -eq 0 ]; then echo "check-layers: OK"; else echo "check-layers: violations found"; fi
exit $fail
