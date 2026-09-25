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
        drawFire(graphics, 0, 0);

        int outerX = (radius * 70) / 100;
        int outerY = (outerX * 94) / 101;
        int diagonalX = (radius * 50) / 100;
        int diagonalY = (diagonalX * 94) / 101;
        int midX = (radius * 60) / 100;
        int midY = ((radius * 35) / 100 * 94) / 101;

        drawFire(graphics, outerX, 0);
        drawFire(graphics, -outerX, 0);
        drawFire(graphics, 0, outerY);
        drawFire(graphics, 0, -outerY);

        drawFire(graphics, diagonalX, diagonalY);
        drawFire(graphics, diagonalX, -diagonalY);
        drawFire(graphics, -diagonalX, diagonalY);
        drawFire(graphics, -diagonalX, -diagonalY);

        drawFire(graphics, midX, midY);
        drawFire(graphics, midX, -midY);
        drawFire(graphics, -midX, midY);
        drawFire(graphics, -midX, -midY);
    }

    private void drawFire(Object graphics, int offsetX, int offsetY) throws Exception {
        CustomUnitEffects.drawVanillaFireAt(graphics, x + offsetX, y + offsetY);
    }
}
