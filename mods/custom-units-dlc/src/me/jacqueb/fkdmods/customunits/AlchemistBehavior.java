package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.custom.CustomUnitBehavior;
import me.jacqueb.fkdcore.custom.CustomUnitDefinition;
import me.jacqueb.fkdcore.custom.CustomUnitEffects;
import me.jacqueb.fkdcore.custom.CustomUnitImpact;
import me.jacqueb.fkdcore.custom.CustomUnitImpactHandler;
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
        unit.throwProjectile(target, new CustomUnitImpactHandler() {
            @Override
            public void onImpact(CustomUnitImpact impact) throws Exception {
                CustomUnitDefinition def = impact.getUnit().getDefinition();
                int directDamage = def.atLevel(def.groundDamage, level);
                int burnDamage = atLevel(BURN_DAMAGE, level);
                int burnTicks = atLevel(BURN_TICKS, level);
                int radius = atLevel(FIRE_RADIUS, level);

                CustomUnitEffects.damageGroundEnemiesInRadius(
                        impact.getX(), impact.getY(), radius, directDamage);
                CustomUnitEffects.applyFireToGroundEnemiesInRadius(
                        impact.getX(), impact.getY(), radius, burnDamage);
                CustomUnitEffects.addWorldEffect(new AlchemistFirePatch(
                        impact.getX(), impact.getY(), radius, burnDamage, burnTicks));
            }
        });
    }

    private static int atLevel(int[] values, int level) {
        int index = Math.max(1, Math.min(3, level)) - 1;
        return values[index];
    }
}
