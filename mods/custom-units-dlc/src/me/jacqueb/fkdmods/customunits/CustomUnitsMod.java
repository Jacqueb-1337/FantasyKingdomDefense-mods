package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.api.FKDMod;
import me.jacqueb.fkdcore.api.ModContext;
import me.jacqueb.fkdcore.custom.CustomUnitDefinition;
import me.jacqueb.fkdcore.custom.CustomUnitRegistry;

public final class CustomUnitsMod implements FKDMod {
    public static final int TYPE_ALCHEMIST = 1000;

    @Override
    public void onLoad(ModContext context) throws Exception {
        CustomUnitRegistry.register(context, new CustomUnitDefinition(
                "custom-units.alchemist",
                TYPE_ALCHEMIST,
                "Alchemist",
                "Throws volatile flasks at ground enemies. Each flask deals impact damage and leaves a burning patch that damages ground enemies three times.",
                80,
                new int[] {90, 45, 90},
                new int[] {8, 12, 16},
                new int[] {0, 0, 0},
                new int[] {14, 15, 16},
                new int[] {16, 16, 12},
                new int[] {4, 5, 6},
                new int[] {3, 3, 3},
                new int[] {250, 275, 300},
                "units/alchemist/alchemist.png",
                45,
                45,
                "warlock",
                5,
                true,
                0,
                new AlchemistBehavior()));
    }
}