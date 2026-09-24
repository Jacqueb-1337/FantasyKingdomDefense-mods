# Fantasy Kingdom Defense Mods

Public mod and downloadable-content repository for the fan-maintained Fantasy Kingdom Defense HD 1.17.91 build.

Fantasy Kingdom Defense ships with FKD Core and a working Mod Manager already built into the APK. Users do not need to install a separate loader or manager. This repository supplies optional mods, DLC, metadata, and updates that the built-in manager can browse and install.

## Built-in experience

A clean FKD install already includes:

- FKD Core bootstrap
- Mod Manager UI
- Content Manager
- Hook API
- local .fkdmod installer
- private mod storage
- an Android DocumentsProvider that exposes FKD Mods in AOSP Files
- this repository as the default Browse source

The manager still opens and installed local mods still load when the device is offline.

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
  mod-manifest.schema.json
docs/
  architecture.md
```

## Package model

Executable mods use the .fkdmod extension. A package is a ZIP container with a manifest plus optional DEX code and assets.

```
manifest.json
classes.dex
assets/
icon.png
```

The Android game verifies packages and loads executable code only from its private app storage.

User-facing mod files and downloaded content are exposed through an Android DocumentsProvider so AOSP Files can show an FKD Mods root in the sidebar without broad storage access.

## Content delivery

The built-in Content Manager downloads manifest.json, compares versions and SHA-256 hashes, stages changed files, verifies them, then atomically promotes them into the active content cache.

Large binary assets can move to GitHub Releases later without changing the manifest model.

## Default repository URL

```
https://raw.githubusercontent.com/Jacqueb-1337/FantasyKingdomDefense-mods/main/manifest.json
```

## Status

Repository scaffold is active. The game-side FKD Core, DocumentsProvider, Hook API, Mod Manager, and Content Manager are being integrated into the APK.