# Fantasy Kingdom Defense Mods

Public mod and downloadable-content repository for the fan-maintained Fantasy Kingdom Defense HD 1.17.91 build.

This repository is designed to back an in-game mod manager and content manager. The game-side loader will live in the FKD project, while this repository hosts manifests, downloadable content, schemas, and optional mod packages.

## Planned layout

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

Executable mods will use the `.fkdmod` extension. A package is a ZIP container with a manifest plus optional DEX code and assets.

```
manifest.json
classes.dex
assets/
icon.png
```

The Android game will load verified executable code from its private app storage. User-facing mod files and downloaded content will be exposed through an Android DocumentsProvider so AOSP Files can show an `FKD Mods` root in the sidebar without broad storage access.

## Content delivery

The game downloads `manifest.json`, compares versions and SHA-256 hashes, stages changed files, verifies them, then atomically promotes them into the active content cache.

Large binary assets can move to GitHub Releases later without changing the manifest format.

## Status

Initial repository scaffold. Loader, DocumentsProvider, hook API, mod manager, and content manager are under development.
