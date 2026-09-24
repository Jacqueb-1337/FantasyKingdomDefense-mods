# FKD Mod Architecture

## Goal

Ship Fantasy Kingdom Defense with a complete, working mod platform already built into the APK.

A fresh install must require no separate mod-loader download, no bootstrap package, and no manual folder setup. The user installs FKD, launches it, and the Mod Manager is already available.

## Built-in core

The APK contains a non-removable FKD Core layer.

FKD Core includes:

1. Bootstrap
   - Runs during FKD startup.
   - Creates private storage.
   - Initializes logging.
   - Initializes the HookBus.
   - Starts the built-in Mod Manager.
   - Starts the built-in Content Manager.
   - Loads enabled external mods.

2. HookBus
   - Stable API used by external mods.
   - Planned hooks include game startup, menu creation, battle start, wave start, enemy spawn, enemy damage, enemy death, defender creation, defender attack, castle damage, level victory, shop population, drawing, resource lookup, and pointer input.
   - External mods should use HookBus instead of permanently patching the base game.

3. Built-in Mod Manager
   - Always present.
   - Cannot be disabled or uninstalled from inside FKD.
   - Works offline for installed local mods.
   - Shows Core separately from user-installed mods.
   - Scans installed .fkdmod packages.
   - Reads manifests.
   - Resolves dependencies.
   - Exposes enable and disable state.
   - Reports compatibility and load failures.
   - Loads verified DEX entry points through Android class loading.
   - Browses configured repositories when internet is available.
   - Downloads, updates, imports, exports, and removes optional mods.
   - Can restore the default repository list.

4. Built-in Content Manager
   - Always present.
   - Loads cached content before any network request.
   - Downloads the repository manifest when internet is available.
   - Compares versions and SHA-256 hashes.
   - Downloads into a staging directory.
   - Verifies content before activation.
   - Atomically promotes verified files.
   - Keeps executable mods separate from data-only DLC.
   - Continues using the last valid cache when offline.

5. DocumentsProvider
   - Exposes a user-visible FKD Mods root to AOSP Files.
   - The physical files remain inside FKD's private app sandbox.
   - Planned folders:
     - Mods
     - Content
     - Logs
     - Imports
     - Exports

## First launch

On a clean installation FKD Core creates this private structure automatically:

```
files/FKDMods/
  Mods/
  Content/
    cache/
    active/
    staging/
  Logs/
  Imports/
  Exports/
  state/
```

No files need to be copied by the user.

The default GitHub repository is embedded in the APK configuration so Browse works immediately when a network connection is available.

If the repository is unreachable, the built-in manager still opens and installed mods still load.

## Core versioning

The built-in platform has its own version, independent of individual mods.

Example:

```
FKD Core 0.1.0
Mod Manager 0.1.0
Content Manager 0.1.0
Hook API 1
```

External packages declare the minimum FKD Core version and Hook API they require.

The remote repository may advertise a newer FKD build or Core version, but the repository is never required to make the bundled manager function.

## Mod package

A .fkdmod file is a ZIP archive.

```
manifest.json
classes.dex
assets/
icon.png
```

The writable package is never executed in-place.

Installation flow:

1. Copy or download the package into staging.
2. Parse and validate its manifest.
3. Verify its SHA-256 hash when one is supplied by the repository.
4. Extract executable DEX into the app's private code area.
5. Make the executable copy read-only before class loading.
6. Promote the package to the installed Mods directory.
7. Load its declared entry class on the next safe load point.

## Repository layout

```
manifest.json
mods/
content/
  troops/
  enemies/
  worlds/
  castle/
schemas/
docs/
```

The repository contains optional downloadable material. It does not contain the only copy of the Mod Manager.

## Default repository

The shipped manager starts with this repository configured:

```
https://raw.githubusercontent.com/Jacqueb-1337/FantasyKingdomDefense-mods/main/manifest.json
```

Additional repositories can be added later.

## CNR design carried forward

The architecture keeps the useful parts of CNRModManager and CNR ContentManager:

- one bootstrap entry point
- a built-in manager that is available without external files
- a version registry
- a manager UI separated from individual mods
- manifest-driven remote content
- cached downloads
- hash verification
- explicit dependency and compatibility checks
- stable hook interfaces so individual mods do not patch the base game themselves

The Unity-specific pieces are replaced with Android and Java equivalents.

Assembly.Load becomes DEX class loading. Unity scene hooks become patched Java hook points in FKD's compiled classes.