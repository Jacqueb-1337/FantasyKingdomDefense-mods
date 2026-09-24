# Fantasy Kingdom Defense Mods

Public Core, mod, and downloadable-content repository for the fan-maintained Fantasy Kingdom Defense HD 1.17.91 build.

## Install once

The APK contains only a small, stable FKD Bootstrap plus an offline fallback copy of FKD Core.

Users install the APK once. They do not need to replace the APK when FKD Core, the Mod Manager, hook APIs, or optional DLC are updated.

At startup Bootstrap:

1. loads the highest valid cached FKD Core package,
2. falls back to the Core package bundled with the APK when necessary,
3. starts the Core and Mod Manager,
4. checks this repository for a newer Core,
5. downloads and SHA-256 verifies a newer Core when available,
6. activates the downloaded Core on the next launch.

A failed or corrupt update never replaces the last valid Core.

## What stays in the APK

Only stable infrastructure:

- FKD Bootstrap
- stable game hook trampolines
- Core package loader/updater
- Android DocumentsProvider for the FKD Mods Files root
- bundled fallback `.fkdcore` package

The APK does not contain the active Core implementation classes directly.

## What updates from this repository

- FKD Core
- Mod Manager
- Content Manager
- Hook API implementations
- custom-unit framework
- pagination and other game integrations
- optional `.fkdmod` packages
- data-only DLC/content

## Repository layout

```
manifest.json
core/
  fkd-core-0.1.0.fkdcore
mods/
  custom-units-dlc/
    custom-units-dlc-0.1.0.fkdmod
    src/
content/
schemas/
docs/
```

## FKD Core package

A `.fkdcore` file is a ZIP container:

```
manifest.json
classes.dex
assets/
```

Bootstrap loads Core DEX only from private app storage and marks executable DEX read-only before class loading.

## Mod package

A `.fkdmod` file is also a ZIP container:

```
manifest.json
classes.dex
assets/
icon.png
```

Mods declare their minimum FKD Core version and Hook API. They do not depend on a particular APK build.

## Default repository

```
https://raw.githubusercontent.com/Jacqueb-1337/FantasyKingdomDefense-mods/main/manifest.json
```

## Current downloadable packages

- FKD Core 0.1.0
- Custom Units DLC 0.1.0
  - first unit: Alchemist
  - designed to accept additional custom units in later DLC updates
