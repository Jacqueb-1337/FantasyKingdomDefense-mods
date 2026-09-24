package me.jacqueb.fkdmods.customunits;

import me.jacqueb.fkdcore.custom.CustomUnitBehavior;
import me.jacqueb.fkdcore.custom.CustomUnitRuntime;

public final class AlchemistBehavior implements CustomUnitBehavior {
    @Override
    public void think(CustomUnitRuntime unit) throws Exception {
        if (unit.consumeCooldown()) return;

        Object target = unit.findNearestGroundTarget();
        if (target == null) {
            unit.faceIdle();
            return;
        }

        unit.faceTarget(target);
        unit.throwProjectile(target);
    }
}