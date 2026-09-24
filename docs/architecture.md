# FKD Mod Architecture

## Principle

The APK is a permanent bootstrap, not the update vehicle.

Once the final bootstrap-capable APK is installed, ordinary FKD development must not require users to replace the APK. FKD Core and mods update independently from the repository.

## Layer 1: APK Bootstrap

Bootstrap version 1 is intentionally small and stable.

It owns only:

- startup entry point
- stable game hook trampolines
- dynamic Core package loading
- Core update checks
- SHA-256 verification
- fallback/rollback selection
- Android DocumentsProvider
- the bundled fallback Core package

Game smali calls only the stable Bootstrap hooks. It never calls implementation classes inside a particular Core release.

## Layer 2: FKD Core

FKD Core is distributed as a `.fkdcore` package.

Current entry class:

```
me.jacqueb.fkdcore.RuntimeCore
```

Core implements the Bootstrap `CoreRuntime` interface and provides:

- Mod Manager
- Content Manager
- HookBus
- mod loading
- repository browsing
- custom-unit registry
- custom-unit runtime
- battle-menu pagination
- shop pagination
- future hook implementations

A new Core can replace all of those systems without replacing the APK, provided it remains compatible with the installed Bootstrap version.

## Core startup/update flow

1. Bootstrap creates `files/FKDMods/`.
2. Bootstrap ensures the APK-bundled fallback `.fkdcore` has been copied to private storage.
3. Bootstrap validates cached Core packages.
4. Bootstrap selects the highest compatible valid Core version.
5. It extracts that package's `classes.dex` into private code cache.
6. The DEX copy is made read-only.
7. Bootstrap loads the Core entry class with `DexClassLoader`.
8. Core initializes and loads enabled mods.
9. Bootstrap checks the repository in the background.
10. If a newer compatible Core exists, Bootstrap downloads it to staging.
11. SHA-256 and package metadata are verified.
12. The package is atomically promoted into the Core cache.
13. It becomes active on the next launch.

If networking or verification fails, the current Core continues running unchanged.

## Private layout

```
files/FKDMods/
  Core/
    fkd-core-bundled.fkdcore
    fkd-core-<version>.fkdcore
    staging/
  Mods/
  ModData/
  Content/
    cache/
    active/
    staging/
  Logs/
  Imports/
  Exports/
  state/
```

## Repository manifest

The root manifest advertises Bootstrap compatibility, current Core, and optional mods separately.

Example:

```json
{
  "bootstrap": {
    "minimumVersion": 1
  },
  "core": {
    "latestVersion": "0.1.0",
    "minimumBootstrapVersion": 1,
    "hookApi": 1,
    "latestUrl": "core/fkd-core-0.1.0.fkdcore",
    "sha256": "..."
  },
  "mods": []
}
```

## Custom Units DLC

Custom Units DLC is an ordinary `.fkdmod`, not part of the APK.

Its first registered unit is Alchemist. Future units are added by updating the same DLC package.

The generic custom-unit integration lives in FKD Core, including:

- IDs outside vanilla range
- unit registration
- per-unit behavior/think methods
- custom stats
- runtime sprites/assets
- projectiles/effects
- custom unlocks
- shop pagination
- battlefield unit-selection pagination

Alchemist has its own `AlchemistBehavior.think()` method. Future units can provide their own behavior classes without APK changes.

## CNR design carried forward

The FKD architecture follows the same important rule as CNR:

- stable bootstrap
- versioned Core
- repo-driven updates
- manager available without manual installation
- cached fallback
- optional downloadable mods
- dependencies/version checks

The Android implementation uses DEX packages instead of Unity assemblies.
