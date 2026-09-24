# FKD Mod Architecture

## Goal

Keep the original game intact while adding a stable bootstrap layer that can load external mods and downloadable content.

## Game-side layers

1. Bootstrap
   - Runs once during FKD startup.
   - Initializes storage, logging, the hook bus, the mod manager, and the content manager.
   - Loads enabled mods after verification.

2. HookBus
   - Central event and interception API.
   - Planned hooks include game startup, menu creation, battle start, wave start, enemy spawn, enemy damage, enemy death, defender creation, defender attack, castle damage, level victory, shop population, drawing, resource lookup, and pointer input.

3. ModManager
   - Scans installed `.fkdmod` packages.
   - Reads manifests, resolves dependencies, exposes enable/disable state, and reports compatibility.
   - Loads verified DEX entry points through Android class loading.

4. ContentManager
   - Downloads the repository manifest.
   - Compares versions and SHA-256 hashes.
   - Downloads into a staging directory.
   - Verifies content before activation.
   - Keeps executable mods separate from data-only DLC.

5. DocumentsProvider
   - Exposes a user-visible `FKD Mods` root to AOSP Files.
   - Planned folders:
     - Mods
     - Content
     - Logs
     - Imports
     - Exports
   - Physical files remain inside the game's private sandbox.

## Mod package

A `.fkdmod` file is a ZIP archive.

```
manifest.json
classes.dex
assets/
icon.png
```

The writable package is never executed in-place. The game verifies the package, copies executable DEX to a private code location, makes the executable copy read-only, then loads it.

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

## CNR design carried forward

The architecture keeps the useful parts of CNRModManager and CNR ContentManager:

- one bootstrap entry point
- a version registry
- a manager UI separated from the core loader
- manifest-driven remote content
- cached downloads
- hash verification
- explicit dependency and compatibility checks
- stable hook interfaces so individual mods do not patch the base game themselves

The Unity-specific pieces are replaced with Android/Java equivalents. Assembly.Load becomes DEX class loading. Unity scene hooks become patched Java hook points in FKD's compiled classes.
