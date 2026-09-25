package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.api.FKDMod;
import me.jacqueb.fkdcore.api.ModContext;
import me.jacqueb.fkdcore.custom.CustomUnitDefinition;
import me.jacqueb.fkdcore.custom.CustomUnitRegistry;

public final class CustomUnitsMod implements FKDMod {
    @Override
    public void onLoad(ModContext context) throws Exception {
        AlchemistAssets.initialize(context);
        CustomUnitRegistry.register(context, new CustomUnitDefinition(
                "custom-units.alchemist",
                "Alchemist",
                "Throws volatile flasks at ground enemies. Impact damages enemies in an area and leaves burning ground that applies the game's native fire debuff.",
                80,
                new int[] {90, 45, 90},
                new int[] {8, 12, 16},
                new int[] {0, 0, 0},
                new int[] {14, 15, 16},
                new int[] {20, 18, 16},
                "alchemist.png",
                181,
                181,
                "warlock",
                5,
                true,
                0,
                new AlchemistBehavior()));
    }
}
