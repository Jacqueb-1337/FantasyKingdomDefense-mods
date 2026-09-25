package me.jacqueb.fkdmods.cheatextended;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import me.jacqueb.fkdcore.FKDCore;
import me.jacqueb.fkdcore.api.FKDMod;
import me.jacqueb.fkdcore.api.ModContext;
import me.jacqueb.fkdcore.api.UnitCatalog;

public final class CheatExtendedMod implements FKDMod {
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private Activity activity;
    private View trigger;
    private boolean menuShowing;

    @Override
    public void onLoad(ModContext context) {
        mainHandler.post(new Runnable() {
            @Override public void run() { attachWhenReady(); }
        });
    }

    private void attachWhenReady() {
        activity = FKDCore.getActivity();
        if (activity == null || activity.isFinishing()) {
            mainHandler.postDelayed(new Runnable() {
                @Override public void run() { attachWhenReady(); }
            }, 500L);
            return;
        }
        if (trigger == null) installCornerTrigger();
        mainHandler.post(watchActivation);
    }

    private final Runnable watchActivation = new Runnable() {
        @Override public void run() {
            try {
                boolean enabled = GameAccess.isCheatModeEnabled();
                if (trigger != null) trigger.setVisibility(enabled ? View.VISIBLE : View.GONE);
                if (enabled) GameAccess.closeVanillaKeypad();
            } catch (Throwable ignored) {
                if (trigger != null) trigger.setVisibility(View.GONE);
            }
            mainHandler.postDelayed(this, 200L);
        }
    };

    private void installCornerTrigger() {
        final ViewGroup decor = (ViewGroup)activity.getWindow().getDecorView();
        trigger = new View(activity);
        trigger.setBackgroundColor(Color.TRANSPARENT);
        trigger.setVisibility(View.GONE);
        trigger.setContentDescription("Cheat Extended");
        trigger.setOnTouchListener(new View.OnTouchListener() {
            @Override public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                    GameAccess.closeVanillaKeypad();
                    showMainMenu();
                }
                return true;
            }
        });
        int width = Math.max(dp(90), activity.getResources().getDisplayMetrics().widthPixels / 4);
        int height = Math.max(dp(48), activity.getResources().getDisplayMetrics().heightPixels / 12);
        decor.addView(trigger, new ViewGroup.LayoutParams(width, height));
        trigger.setX(0f);
        trigger.setY(0f);
        trigger.bringToFront();
    }

    private void showMainMenu() {
        if (activity == null || activity.isFinishing() || menuShowing) return;
        menuShowing = true;
        final String[] items = {
                "Resources",
                "Units",
                "Elixirs",
                "Castle Upgrades",
                "Battle",
                "Daily Reward",
                "World Progress"
        };
        new AlertDialog.Builder(activity)
                .setTitle("Cheat Extended")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        menuShowing = false;
                        switch (which) {
                            case 0: showResources(); break;
                            case 1: showUnits(); break;
                            case 2: showElixirs(); break;
                            case 3: showCastleUpgrades(); break;
                            case 4: showBattle(); break;
                            case 5: showDailyReward(); break;
                            case 6: promptWorld(); break;
                            default: break;
                        }
                    }
                })
                .setNegativeButton("Close", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) { menuShowing = false; }
                })
                .show();
    }

    private void showResources() {
        final String[] items = {"Set Gems", "Set Battle Money"};
        new AlertDialog.Builder(activity)
                .setTitle("Resources")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            promptNumber("Set Gems", new NumberAction() {
                                @Override public void apply(int value) throws Exception { GameAccess.setGems(value); }
                            });
                        } else {
                            promptNumber("Set Battle Money", new NumberAction() {
                                @Override public void apply(int value) throws Exception { GameAccess.setMoney(value); }
                            });
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void showUnits() {
        final List<UnitCatalog.UnitInfo> units = UnitCatalog.all();
        final String[] labels = new String[units.size() + 2];
        labels[0] = "Unlock All Units";
        labels[1] = "Lock All Units";
        for (int i = 0; i < units.size(); i++) {
            UnitCatalog.UnitInfo unit = units.get(i);
            boolean unlocked = UnitCatalog.isUnlocked(unit.namespace, unit.id);
            labels[i + 2] = (unlocked ? "[Unlocked] " : "[Locked] ")
                    + unit.displayName + "  [" + unit.getCanonicalId() + "]";
        }

        new AlertDialog.Builder(activity)
                .setTitle("Units")
                .setItems(labels, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        if (which == 0 || which == 1) {
                            boolean unlock = which == 0;
                            int changed = 0;
                            for (UnitCatalog.UnitInfo unit : units) {
                                if (UnitCatalog.setUnlocked(unit, unlock)) changed++;
                            }
                            toast((unlock ? "Unlocked " : "Locked ") + changed + " units");
                            showUnits();
                            return;
                        }
                        UnitCatalog.UnitInfo unit = units.get(which - 2);
                        boolean current = UnitCatalog.isUnlocked(unit.namespace, unit.id);
                        if (UnitCatalog.setUnlocked(unit, !current)) {
                            toast((current ? "Locked " : "Unlocked ") + unit.displayName);
                        } else {
                            toast("Could not update " + unit.displayName);
                        }
                        showUnits();
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void showElixirs() {
        final String[] items = {"Unlock All Elixirs", "Lock All Elixirs"};
        new AlertDialog.Builder(activity)
                .setTitle("Elixirs")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            GameAccess.setAllElixirs(which == 0);
                            toast(which == 0 ? "Elixirs unlocked" : "Elixirs locked");
                        } catch (Throwable error) {
                            toast("Elixir update failed");
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void showCastleUpgrades() {
        final String[] items = {"Unlock All Castle Upgrades", "Lock All Castle Upgrades"};
        new AlertDialog.Builder(activity)
                .setTitle("Castle Upgrades")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            GameAccess.setCastleUpgrades(which == 0);
                            toast(which == 0 ? "Castle upgrades unlocked" : "Castle upgrades locked");
                        } catch (Throwable error) {
                            toast("Castle update failed");
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void showBattle() {
        final String[] items = {"Force Win", "Force Loss"};
        new AlertDialog.Builder(activity)
                .setTitle("Battle")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (which == 0) GameAccess.forceWin();
                            else GameAccess.forceLoss();
                            toast(which == 0 ? "Win forced" : "Loss forced");
                        } catch (Throwable error) {
                            toast("Battle action unavailable here");
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void showDailyReward() {
        final String[] days = {"Day 1", "Day 2", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7"};
        new AlertDialog.Builder(activity)
                .setTitle("Daily Reward")
                .setItems(days, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            GameAccess.setDailyReward(which + 1);
                            toast("Daily reward set to " + days[which]);
                        } catch (Throwable error) {
                            toast("Daily reward unavailable");
                        }
                    }
                })
                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface d, int w) { showMainMenu(); }
                }).show();
    }

    private void promptWorld() {
        promptNumber("World Number", new NumberAction() {
            @Override public void apply(final int world) {
                if (world <= 0) {
                    toast("World must be 1 or higher");
                    return;
                }
                final String[] shields = {"1 Shield", "2 Shields", "3 Shields"};
                new AlertDialog.Builder(activity)
                        .setTitle("World " + world + " Completion")
                        .setItems(shields, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {
                                try {
                                    GameAccess.unlockWorld(world, which + 1);
                                    toast("World " + world + " unlocked");
                                } catch (Throwable error) {
                                    toast("World update unavailable");
                                }
                            }
                        })
                        .setNegativeButton("Back", null)
                        .show();
            }
        });
    }

    private void promptNumber(String title, final NumberAction action) {
        final EditText input = new EditText(activity);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setGravity(Gravity.CENTER_HORIZONTAL);
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setView(input)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            String valueText = input.getText().toString().trim();
                            if (valueText.length() == 0) return;
                            action.apply(Integer.parseInt(valueText));
                            toast("Applied");
                        } catch (Throwable error) {
                            toast("Invalid value");
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private interface NumberAction {
        void apply(int value) throws Exception;
    }

    private int dp(int value) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.max(1, (int)(value * density + 0.5f));
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private static final class GameAccess {
        private static Object cheat() throws Exception {
            Class<?> cls = Class.forName("com.tqm.fantasydefense.menu.Cheat");
            return cls.getMethod("getInstance").invoke(null);
        }

        static boolean isCheatModeEnabled() throws Exception {
            Object instance = cheat();
            Field field = instance.getClass().getDeclaredField("cheatModeEnabled");
            field.setAccessible(true);
            return field.getBoolean(instance);
        }

        static void closeVanillaKeypad() {
            try {
                Object instance = cheat();
                Field cheating = instance.getClass().getDeclaredField("cheating");
                cheating.setAccessible(true);
                cheating.setBoolean(instance, false);
                Field buffer = instance.getClass().getDeclaredField("buffer");
                buffer.setAccessible(true);
                Object value = buffer.get(instance);
                if (value instanceof StringBuffer) {
                    StringBuffer sb = (StringBuffer)value;
                    sb.delete(0, sb.length());
                }
            } catch (Throwable ignored) {}
        }

        static void setGems(int value) throws Exception {
            Class<?> gems = Class.forName("com.tqm.fantasydefense.shop.secret.SecretGems");
            gems.getMethod("setGemsAmount", int.class).invoke(null, value);
        }

        static void setMoney(int value) throws Exception {
            Class<?> game = Class.forName("com.tqm.fantasydefense.GameTemplate");
            game.getMethod("setMoneyAmount", int.class).invoke(null, value);
        }

        static void setAllElixirs(boolean unlocked) throws Exception {
            Object manager = secretManager();
            Object values = manager.getClass().getMethod("getElixirs").invoke(manager);
            int[] ids = {0, 6, 2, 4, 9, 8, 10, 11};
            for (int id : ids) {
                Object item = Array.get(values, id);
                if (unlocked) {
                    item.getClass().getMethod("grantElixirAsReward").invoke(item);
                } else {
                    item.getClass().getMethod("lockAndDeactivate").invoke(item);
                    item.getClass().getMethod("saveDefaultToRMS").invoke(item);
                }
            }
        }

        static void setCastleUpgrades(boolean unlocked) throws Exception {
            Object manager = secretManager();
            String[] getters = {"getCatapults", "getDragons", "getGates"};
            for (String getter : getters) {
                Object values = manager.getClass().getMethod(getter).invoke(manager);
                for (int i = 0; i < Array.getLength(values); i++) {
                    Object item = Array.get(values, i);
                    item.getClass().getMethod(
                            unlocked ? "unlockDefender" : "lockDefender").invoke(item);
                }
            }
        }

        static void forceWin() throws Exception {
            Class<?> game = Class.forName("com.tqm.fantasydefense.GameTemplate");
            Object waves = game.getField("enemyWaves").get(null);
            waves.getClass().getMethod("forceWin").invoke(waves);
        }

        static void forceLoss() throws Exception {
            Class<?> game = Class.forName("com.tqm.fantasydefense.GameTemplate");
            Object castle = game.getField("myCastle").get(null);
            Field hp = castle.getClass().getField("_castleHp");
            hp.setInt(castle, 0);
        }

        static void setDailyReward(int day) throws Exception {
            Object instance = cheat();
            Field field = instance.getClass().getDeclaredField("dailyReward");
            field.setAccessible(true);
            Object reward = field.get(instance);
            if (reward == null) throw new IllegalStateException("DailyReward unavailable");
            int storedDay = day == 1 ? 7 : day - 1;
            reward.getClass().getMethod("setReward", int.class).invoke(reward, storedDay);
        }

        static void unlockWorld(int worldNumber, int shieldNumber) throws Exception {
            Object instance = cheat();
            Field field = instance.getClass().getDeclaredField("worldMap");
            field.setAccessible(true);
            Object worldMap = field.get(instance);
            if (worldMap == null) throw new IllegalStateException("WorldMap unavailable");

            int internalShield = shieldNumber == 1 ? 3 : (shieldNumber == 2 ? 2 : 1);
            int worldIndex = worldNumber - 1;
            Method unlockCastle = worldMap.getClass().getMethod("unlockCastle", int.class);
            Method gainCastle = worldMap.getClass().getMethod("gainCastle", int.class, int.class);
            for (int i = 0; i < 6; i++) {
                int castleIndex = i + (worldIndex * 7);
                unlockCastle.invoke(worldMap, castleIndex);
                gainCastle.invoke(worldMap, castleIndex, internalShield);
            }
        }

        private static Object secretManager() throws Exception {
            Class<?> cls = Class.forName("com.tqm.fantasydefense.shop.secret.SecretItemsManager");
            return cls.getMethod("getInstance").invoke(null);
        }
    }
}
