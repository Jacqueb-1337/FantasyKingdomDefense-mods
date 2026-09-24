package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.custom.CustomUnitEffects;
import me.jacqueb.fkdcore.custom.CustomUnitWorldEffect;

final class AlchemistFirePatch implements CustomUnitWorldEffect {
    private final int x;
    private final int y;
    private final int radius;
    private final int fireDamage;
    private int applicationsRemaining;
    private int clock;

    AlchemistFirePatch(int x, int y, int radius, int fireDamage, int applications) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.fireDamage = fireDamage;
        this.applicationsRemaining = Math.max(1, applications);
    }

    @Override
    public boolean tick() throws Exception {
        int interval = Math.max(1, 6 / CustomUnitEffects.getGameSpeed());
        clock++;
        if (clock < interval) return true;
        clock = 0;

        CustomUnitEffects.applyFireToGroundEnemiesInRadius(
                x, y, radius, fireDamage);
        applicationsRemaining--;
        return applicationsRemaining > 0;
    }

    @Override
    public void draw(Object graphics) throws Exception {
        CustomUnitEffects.drawVanillaFireAt(graphics, x, y);
    }
}
