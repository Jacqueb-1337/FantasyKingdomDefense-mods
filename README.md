# Fantasy Kingdom Defense Mods

Modding framework and package repository for the fan-maintained Fantasy Kingdom Defense HD 1.17.91 build.

## Contents

- [How it works](#how-it-works)
- [Mod Manager](#mod-manager)
- [Repositories](#repositories)
- [Dependencies](#dependencies)
- [Package formats](#package-formats)
- [Making a mod](#making-a-mod)
  - [1. Pick a namespace and ID](#1-pick-a-namespace-and-id)
  - [2. Write the manifest](#2-write-the-manifest)
  - [3. Write the mod](#3-write-the-mod)
    - [Register custom units](#register-custom-units)
  - [4. Build the package](#4-build-the-package)
  - [5. Publish it](#5-publish-it)
- [Repository layout](#repository-layout)

## How it works

The APK contains a small Bootstrap and a bundled fallback copy of FKD Core.

Bootstrap loads the newest valid Core it has, falls back to the bundled Core if needed, and checks the configured repository for Core updates. New Core versions are downloaded, SHA-256 checked, and activated on the next game launch.

Most modding work lives outside the APK:

- **FKD Core** handles the Mod Manager, repositories, dependency resolution, hooks, custom units, and other runtime features.
- **.fkdmod packages** add optional mods.
- **Repositories** advertise Core and mod packages through a JSON manifest.

Normal Core and mod updates do not require replacing the APK.

## Mod Manager

The in-game Mod Manager has pages for installed mods, browsing repositories, repository management, and the FKD Mods file area.

It checks for updates when Core starts. The floating **Mods** button shows the number of available updates.

Installed mods can be updated directly from the **Installed** page. Updates go through the same dependency checks as fresh installs.

Core is also listed as an updateable package. A downloaded Core update activates after restarting FKD.

## Repositories

The built-in repository is:

```text
https://raw.githubusercontent.com/Jacqueb-1337/FantasyKingdomDefense-mods/main/manifest.json
```

Users can add more repository manifest URLs from the Mod Manager.

Every repository declares a stable alias:

```json
{
  "repositoryAlias": "official"
}
```

Dependency entries can pin themselves to a repository alias. If no repository is specified, Core searches all enabled repositories for the newest compatible package.

Two different URLs cannot claim the same repository alias.

## Dependencies

Mods use a namespaced identity:

```text
namespace:id
```

For example:

```text
me.jacqueb:custom-units-dlc
```

A normal mod dependency looks like this:

```json
{
  "type": "mod",
  "namespace": "example.author",
  "id": "shared-library",
  "minimumVersion": "1.2.0",
  "repository": "community"
}
```

A Core dependency looks like this:

```json
{
  "type": "core",
  "minimumVersion": "0.1.3",
  "repository": "official"
}
```

Before installing or updating a mod, Core resolves the full dependency tree first.

If a dependency is already installed at a high enough version, it is reused. Otherwise Core looks for a compatible version in the requested repository or across all enabled repositories.

If a mod requires a newer Core, the Core package is downloaded first and FKD asks for a restart before continuing with the mod install.

`minimumCoreVersion` is still supported as shorthand for older manifests.

## Package formats

### FKD Core

A `.fkdcore` file is a ZIP containing:

```text
manifest.json
classes.dex
assets/
```

### Mods

A `.fkdmod` file is a ZIP containing:

```text
manifest.json
classes.dex
assets/
```

Assets are optional.

## Making a mod

The existing [Custom Units DLC](mods/custom-units-dlc/) is a working example.

### 1. Pick a namespace and ID

Use a namespace you control and a short mod ID.

```json
{
  "namespace": "com.example",
  "id": "my-mod"
}
```

The full identity becomes `com.example:my-mod`.

Namespaces prevent unrelated mods from colliding just because they use the same ID.

### 2. Write the manifest

Basic example:

```json
{
  "namespace": "com.example",
  "id": "my-mod",
  "name": "My Mod",
  "version": "1.0.0",
  "description": "Example FKD mod.",
  "entryClass": "com.example.mymod.MyMod",
  "minimumCoreVersion": "0.1.3",
  "minimumHookApi": 1,
  "dependencies": []
}
```

Add entries to `dependencies` when your mod needs another mod or a specific Core version.

The current schema is in [schemas/mod-manifest.schema.json](schemas/mod-manifest.schema.json).

### 3. Write the mod

The entry class can implement FKD Core's `FKDMod` API and receives a `ModContext` when loaded.

A minimal entry point looks like:

```java
public final class MyMod implements FKDMod {
    @Override
    public void onLoad(ModContext context) {
        // Register hooks, content, units, etc.
    }
}
```

Use the public Core APIs and hooks instead of patching game classes from the mod package.

#### Register custom units

Core 0.1.8+ lets mods register custom units without choosing numeric type IDs or hardcoding shop tiles. Registered units are appended to the custom-unit pages automatically in registration order.

A unit can be registered with the AUTO-type constructor:

```java
CustomUnitRegistry.register(context, new CustomUnitDefinition(
    "my-unit",
    "My Unit",
    "Short description.",
    50,
    new int[] {60, 75, 90},
    new int[] {8, 12, 16},
    new int[] {0, 0, 0},
    new int[] {14, 15, 16},
    new int[] {16, 14, 12},
    "units/my-unit.png",
    45,
    45,
    "warlock",
    5,
    true,
    0,
    new MyUnitBehavior()
));
```

Core assigns a stable custom type ID and remembers it for that mod/unit pair. Installing another unit mod simply adds its registered units to the available pages.

If your mod uses this API, require Core 0.1.8 or newer in the manifest.

### 4. Build the package

Compile the mod to Android DEX, then put `classes.dex`, `manifest.json`, and any assets into a ZIP and rename it to `.fkdmod`.

Example layout:

```text
my-mod-1.0.0.fkdmod
  manifest.json
  classes.dex
  assets/
```

### 5. Publish it

A repository's `mods` array advertises downloadable packages:

```json
{
  "namespace": "com.example",
  "id": "my-mod",
  "name": "My Mod",
  "latestVersion": "1.0.0",
  "latestUrl": "mods/my-mod/my-mod-1.0.0.fkdmod",
  "sha256": "..."
}
```

Use the package's real SHA-256. Relative URLs are resolved against that repository's manifest URL.

## Repository layout

This repository currently looks roughly like:

```text
manifest.json
core/
  fkd-core-*.fkdcore
mods/
  custom-units-dlc/
    manifest.json
    src/
    custom-units-dlc-*.fkdmod
schemas/
docs/
```

The APK build/source-of-truth files and signing material are intentionally not part of the public repository.
