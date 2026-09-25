package me.jacqueb.fkdmods.customunits;

import com.tqm.fantasydefense.game.Particle;
import java.lang.reflect.Field;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import me.jacqueb.fkdcore.custom.CustomUnitRuntime;

final class AlchemistProjectile extends Particle {
    // 1x attack timeline, in game ticks:
    // 0-1  = frame 2, begin draw-back
    // 2-6  = frame 3, full draw-back hold
    // 7    = frame 6, fast forward throw
    // 8-10 = frame 7, follow-through hold
    // The flask releases as frame 7 begins.
    private static final int RELEASE_TICK = 8;
    private static final int ATTACK_END_TICK = 11;

    interface ImpactHandler {
        void onImpact(int x, int y) throws Exception;
    }

    private final Image image;
    private final int impactX;
    private final int impactY;
    private final ImpactHandler impactHandler;
    private final Object releaseDefender;
    private int attackTick;
    private boolean released;
    private boolean impacted;

    private AlchemistProjectile(int startX, int startY,
                                int destX, int destY,
                                int impactX, int impactY,
                                Image image,
                                Object releaseDefender,
                                ImpactHandler impactHandler) {
        super(startX, startY, destX, destY, 12, 0, true);
        this.image = image;
        this.impactX = impactX;
        this.impactY = impactY;
        this.releaseDefender = releaseDefender;
        this.impactHandler = impactHandler;
        if (image != null) {
            this._width = Math.max(1, image.getWidth() / 5);
            this._height = image.getHeight();
        } else {
            this._width = 1;
            this._height = 1;
        }
    }

    static void launch(CustomUnitRuntime unit, Object enemy,
                       ImpactHandler impactHandler) throws Exception {
        Object defender = defenderOf(unit);
        int speed = Math.max(1, staticInt(
                "com.tqm.fantasydefense.GameTemplate", "gameSpeedValue"));
        int startDelay = intField(defender, "_startAttackDelay");
        setIntField(defender, "_currentAttackDelay",
                Math.max(6, (startDelay / speed) - 1));

        // Start on attack frame 2. The custom timeline in think() takes
        // over from here instead of letting Core race through all six frames.
        setIntField(defender, "_hit", 6);

        int ux = intField(defender, "_x");
        int uy = intField(defender, "_y");
        int uw = intField(defender, "_width");
        int uh = intField(defender, "_height");
        boolean left = boolField(defender, "_left");

        int ex = intField(enemy, "_x");
        int ey = intField(enemy, "_y");
        int eh = intField(enemy, "_height");

        int startX = ux + (left ? -(uw / 3) : (uw / 3)) + 73;
        int startY = (uy - (uh >> 1)) + 47;
        int destX = ex + 73;
        int destY = (ey - (eh >> 1)) + 47;

        AlchemistProjectile projectile = new AlchemistProjectile(
                startX, startY, destX, destY, ex, ey,
                AlchemistAssets.projectileImage(), defender, impactHandler);

        Field particlesField = Class.forName(
                "com.tqm.fantasydefense.game.ParticleGroup")
                .getDeclaredField("particles");
        particlesField.setAccessible(true);
        Object value = particlesField.get(null);
        if (!(value instanceof Vector)) {
            throw new IllegalStateException("ParticleGroup.particles unavailable");
        }
        ((Vector)value).addElement(projectile);
    }

    @Override
    public void draw(Graphics graphics) {
        if (image == null || graphics == null) return;
        try {
            int viewX = staticInt("com.tqm.fantasydefense.GameTemplate", "viewX");
            int viewY = staticInt("com.tqm.fantasydefense.GameTemplate", "viewY");
            int frame = Math.min(4, Math.max(0,
                    (_life * 5) / Math.max(1, _lifeFinal)));
            graphics.drawRegion(image,
                    frame * _width, 0, _width, _height, 0,
                    (_x - (_width >> 1)) + viewX,
                    (_y - (_height >> 1)) + viewY,
                    0);
        } catch (Throwable ignored) {}
    }

    @Override
    public boolean visible() {
        return true;
    }

    @Override
    public boolean think() {
        try {
            if (attackTick < ATTACK_END_TICK) {
                int hitValue;
                if (attackTick < 2) {
                    hitValue = 6; // frame 2: begin draw-back
                } else if (attackTick < 7) {
                    hitValue = 5; // frame 3: hold at full draw-back
                } else if (attackTick < RELEASE_TICK) {
                    hitValue = 2; // frame 6: one fast forward-motion frame
                } else {
                    hitValue = 1; // frame 7: follow-through hold
                }
                setIntField(releaseDefender, "_hit", hitValue);

                if (!released && attackTick >= RELEASE_TICK) {
                    released = true;
                    AlchemistAssets.playThrow();
                }

                int speed = Math.max(1, staticInt(
                        "com.tqm.fantasydefense.GameTemplate", "gameSpeedValue"));
                attackTick += speed;

                if (!released) {
                    return true;
                }
            }
        } catch (Throwable ignored) {
            // If the defender disappears mid-windup, release rather than
            // leaving an invisible particle alive forever.
            if (!released) {
                released = true;
                AlchemistAssets.playThrow();
            }
        }

        if (!released) {
            released = true;
            AlchemistAssets.playThrow();
        }

        boolean alive = super.think();
        if (!alive && !impacted) {
            impacted = true;
            try {
                AlchemistAssets.playImpact();
                if (impactHandler != null) {
                    impactHandler.onImpact(impactX, impactY);
                }
            } catch (Throwable ignored) {}
        }
        return alive;
    }

    private static Object defenderOf(CustomUnitRuntime unit) throws Exception {
        Field field = CustomUnitRuntime.class.getDeclaredField("defender");
        field.setAccessible(true);
        return field.get(unit);
    }

    private static int intField(Object target, String name) throws Exception {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        return field.getInt(target);
    }

    private static boolean boolField(Object target, String name) throws Exception {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        return field.getBoolean(target);
    }

    private static void setIntField(Object target, String name, int value) throws Exception {
        Field field = findField(target.getClass(), name);
        field.setAccessible(true);
        field.setInt(target, value);
    }

    private static int staticInt(String className, String name) throws Exception {
        Field field = findField(Class.forName(className), name);
        field.setAccessible(true);
        return field.getInt(null);
    }

    private static Field findField(Class<?> type, String name) throws Exception {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
    }
}
