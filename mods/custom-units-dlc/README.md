# Custom Units DLC

Expandable custom-unit pack for Fantasy Kingdom Defense.

The first unit is **Alchemist** (custom type ID 1000). Future units should be added to this same DLC rather than creating one APK patch per unit.

## Alchemist balance

| Level | Build / upgrade cost | Ground damage | Air damage | Range | Attack delay | Burn damage | Burn ticks | Fire radius |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1 | 90 | 8 | 0 | 14 | 16 | 4 | 3 | 2.50 tiles |
| 2 | 45 | 12 | 0 | 15 | 16 | 5 | 3 | 2.75 tiles |
| 3 | 90 | 16 | 0 | 16 | 12 | 6 | 3 | 3.00 tiles |

Secret unlock cost: **80 gems**.

The Alchemist targets ground enemies only. A flask lands at the target position, deals direct impact damage, and creates a three-tick fire patch. Flying enemies ignore the patch. If multiple patches overlap an enemy on the same tick, only the strongest burn applies.

## Alchemist replaceable assets

These files are owned entirely by the mod, so changing them does not require a Core or APK update:

- `assets/units/alchemist/alchemist.png` — unit animation sheet. It is a 181x181 PNG containing a 4x4 grid of 16 logical 45x45 frames. Frames 0-1 are front idle, 2-7 are front attack, 8-9 are rear idle, and 10-15 are rear attack. The final right/bottom pixel is transparent padding. Left/right facing is mirrored by the game automatically.
- `assets/projectiles/alchemist_flask.png` — projectile sprite sheet. The default sheet is 5 horizontal frames, 82x61 each (410x61 total). Keep that 5-frame horizontal layout when replacing it.
- `assets/audio/alchemist_throw.ogg` — played when the flask is thrown.
- `assets/audio/alchemist_impact.ogg` — played when the flask lands, immediately before AoE damage and the fire patch are created.

The projectile itself is now implemented by the DLC as a custom `Particle` subclass, so its visual lifetime and the impact/AoE timing stay locked together without using Core's vanilla projectile launcher.

