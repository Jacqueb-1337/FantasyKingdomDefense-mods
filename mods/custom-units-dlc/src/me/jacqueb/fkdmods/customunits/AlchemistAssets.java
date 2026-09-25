package me.jacqueb.fkdmods.customunits;

import android.media.AudioManager;
import android.media.SoundPool;
import java.io.File;
import java.io.FileInputStream;
import javax.microedition.lcdui.Image;
import me.jacqueb.fkdcore.api.ModContext;

final class AlchemistAssets {
    private static Image projectileImage;
    private static SoundPool soundPool;
    private static int throwSoundId;
    private static int impactSoundId;

    private AlchemistAssets() {}

    static void initialize(ModContext context) {
        File projectileFile = context.getAssetFile("projectiles/alchemist_flask.png");
        File throwSoundFile = context.getAssetFile("audio/alchemist_throw.ogg");
        File impactSoundFile = context.getAssetFile("audio/alchemist_impact.ogg");

        try {
            if (projectileFile.isFile()) {
                FileInputStream in = new FileInputStream(projectileFile);
                try {
                    projectileImage = Image.createImage(in);
                } finally {
                    in.close();
                }
            }
        } catch (Throwable ignored) {
            projectileImage = null;
        }

        try {
            soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
            if (throwSoundFile.isFile()) {
                throwSoundId = soundPool.load(throwSoundFile.getAbsolutePath(), 1);
            }
            if (impactSoundFile.isFile()) {
                impactSoundId = soundPool.load(impactSoundFile.getAbsolutePath(), 1);
            }
        } catch (Throwable ignored) {
            soundPool = null;
            throwSoundId = 0;
            impactSoundId = 0;
        }
    }

    static Image projectileImage() {
        return projectileImage;
    }

    static void playThrow() {
        play(throwSoundId);
    }

    static void playImpact() {
        play(impactSoundId);
    }

    private static void play(int soundId) {
        if (soundPool == null || soundId == 0) return;
        try {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
        } catch (Throwable ignored) {}
    }
}
