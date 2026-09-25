package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.custom.CustomUnitBehavior;
import me.jacqueb.fkdcore.custom.CustomUnitDefinition;
import me.jacqueb.fkdcore.custom.CustomUnitEffects;
import me.jacqueb.fkdcore.custom.CustomUnitRuntime;

public final class AlchemistBehavior implements CustomUnitBehavior {
    private static final int[] BURN_DAMAGE = {4, 5, 6};
    private static final int[] BURN_TICKS = {3, 3, 3};
    private static final int[] FIRE_RADIUS = {250, 275, 300};

    @Override
    public void think(final CustomUnitRuntime unit) throws Exception {
        if (unit.consumeCooldown()) return;

        Object target = unit.findNearestGroundTarget();
        if (target == null) {
            unit.faceIdle();
            return;
        }

        unit.faceTarget(target);
        final int level = unit.getLevel();
        final CustomUnitDefinition def = unit.getDefinition();

        AlchemistProjectile.launch(unit, target, new AlchemistProjectile.ImpactHandler() {
            @Override
            public void onImpact(int x, int y) throws Exception {
                int directDamage = def.atLevel(def.groundDamage, level);
                int burnDamage = atLevel(BURN_DAMAGE, level);
                int burnTicks = atLevel(BURN_TICKS, level);
                int radius = atLevel(FIRE_RADIUS, level);

                CustomUnitEffects.damageGroundEnemiesInRadius(
                        x, y, radius, directDamage);
                CustomUnitEffects.applyFireToGroundEnemiesInRadius(
                        x, y, radius, burnDamage);
                CustomUnitEffects.addWorldEffect(new AlchemistFirePatch(
                        x, y, radius, burnDamage, burnTicks));
            }
        });
    }

    private static int atLevel(int[] values, int level) {
        int index = Math.max(1, Math.min(3, level)) - 1;
        return values[index];
    }
}
