package org.brutality.utils.hypixel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.brutality.module.Module;
import org.brutality.utils.MessageUtils;

public class PhaseHelper {
    private static final Set<String> itemSet = new HashSet<>();
    private static final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
    private static final AtomicLong atomicLong = new AtomicLong(0L);
    private static final AtomicLong atomicLong2 = new AtomicLong(0L);

    public static Set<String> getItemSet() {
        return itemSet;
    }

    public static void handleString(String str) {
        itemSet.remove(str);
    }

    static {
        itemSet.add("Phase");
        itemSet.add("Step");
        itemSet.add("Timer");
        itemSet.add("AntiVoid");
        itemSet.add("Longjump");
        itemSet.add("Jesus");
        itemSet.add("Spider");
        itemSet.add("Criticals");
        itemSet.add("GunAimbot");
        itemSet.add("ComboOneHit");
        itemSet.add("DamageBoost");
        itemSet.add("Super Knockback");
        itemSet.add("SuperKnockback");
        itemSet.add("FightBot");
        itemSet.add("FastBow");
        itemSet.add("AntiAura");
        itemSet.add("AntiAim");
        itemSet.add("Dolphin");
        itemSet.add("Fast Ladder");
        itemSet.add("FastLadder");
        itemSet.add("Jet Pack");
        itemSet.add("JetPack");
        itemSet.add("Parkour");
        itemSet.add("NoFall");
        itemSet.add("No Fall");
        itemSet.add("TPA Trap");
        itemSet.add("TPATrap");
        itemSet.add("Reset VL");
        itemSet.add("ResetVL");
        itemSet.add("Regeneration");
        itemSet.add("Disabler");
        itemSet.add("Crasher");
    }

    public static boolean matchesModule(Module module) {
        return HypixelNetHelper.checkState() && matchesModule2(module);
    }

    public static boolean matchesString(String str) {
        return itemSet.contains(str);
    }

    public static void handleModule(Module module) {
        MessageUtils.x(MessageUtils.text13 + module.getName() + MessageUtils.text8 + " is restricted on Hypixel due to anti-cheat detection risks.");
    }

    public static boolean matchesModule2(Module module) {
        return itemSet.contains(module.getName());
    }

    public static void handleString2(String str) {
        itemSet.add(str);
    }
}
