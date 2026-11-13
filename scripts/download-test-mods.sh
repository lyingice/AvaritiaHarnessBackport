#!/usr/bin/env bash
# Downloads the optional compatibility mods used by the local GameTests
# and the Re:Avaritia jar required by the 1.20.1 Forge build.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

mkdir -p "$ROOT/neoforge-1.21.1/libs" "$ROOT/forge-1.20.1/libs"

echo "Downloading NeoForge 1.21.1 test mods..."
curl -L --fail --retry 3 \
  -o "$ROOT/neoforge-1.21.1/libs/VanillaBackport-neoforge-1.21.1-1.2.2.jar" \
  "https://cdn.modrinth.com/data/6xwxDTgf/versions/mGS7BxD4/VanillaBackport-neoforge-1.21.1-1.2.2.jar"

curl -L --fail --retry 3 \
  -o "$ROOT/neoforge-1.21.1/libs/Platform-neoforge-1.21.1-1.4.2.jar" \
  "https://cdn.modrinth.com/data/i6fiqm5y/versions/rh4p4ixM/Platform-neoforge-1.21.1-1.4.2.jar"

curl -L --fail --retry 3 \
  -o "$ROOT/neoforge-1.21.1/libs/happy_ghast_legacy-1.8-1.21.1-neoforge.jar" \
  "https://cdn.modrinth.com/data/zi5opIfD/versions/TNIdu1Im/happy_ghast_legacy-1.8-1.21.1-neoforge.jar"

echo "Downloading Re:Avaritia 1.20.1 Forge compile dependency..."
curl -L --fail --retry 3 \
  -o "$ROOT/forge-1.20.1/libs/Re-Avaritia-forge-1.20.1-1.4.4-release-1.0.jar" \
  "https://cdn.modrinth.com/data/QeB3NRC5/versions/PIZk2hki/Re-Avaritia-forge-1.20.1-1.4.4-release.jar"

echo "Done."
