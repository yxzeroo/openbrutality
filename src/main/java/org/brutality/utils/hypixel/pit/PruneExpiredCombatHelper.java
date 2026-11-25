package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.utils.WHelper;
import org.brutality.utils.performance.EntityCache;

public final class PruneExpiredCombatHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("([\\d,]+(?:\\.\\d+)?)\\s*g\\s*$", 2);
    private static final String[] stringArray = new String[]{"BEAST", "UBER", "HERMIT", "OVRDRV", "HIGH", "MOON"};
    private static final long timestamp = 15000L;
    private static final long timestamp2 = 30000L;
    private static final double amount = 1000.0;
    private static int count = 0;
    private static boolean flag = true;
    private static final Map<Integer, Float> lookupMap = new HashMap<>();
    private static final Map<Integer, Long> lookupMap2 = new HashMap<>();
    private static int count2 = -1;

    public static void setFlag(boolean flag2) {
        flag = flag2;
    }

    private static void handleEntityPlayerSP(EntityPlayerSP player) {
        for (EntityPlayer player2 : EntityCache.getEntries()) {
            if (player2 != null && player2 != player && !player2.isDead && matchesEntityPlayer2(player2)) {
                handleEntityPlayer(player2);
            }
        }
    }

    public static void handleEntityPlayer2(EntityPlayer player) {
        if (player != null) {
            int entityId = player.getEntityId();
            lookupMap2.remove(entityId);
            lookupMap.remove(entityId);
        }
    }

    public static void process() {
        count++;
    }

    public static void release() {
        count = Math.max(0, count - 1);
        if (count == 0) {
            reset();
        }
    }

    public static double computeDouble(EntityPlayer player) {
        if (player == null) {
            return 0.0;
        }
        String str = stripColor(player.getDisplayName().getUnformattedText()).trim();
        Matcher matcher = pattern.matcher(str);
        if (!matcher.find()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(matcher.group(1).replace(",", ""));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public static void handleEntityPlayer3(EntityPlayer player) {
        if (player != null && matchesEntityPlayer2(player)) {
            long l = computeDouble(player) >= 1000.0 ? 30000L : 15000L;
            lookupMap2.put(player.getEntityId(), System.currentTimeMillis() + l);
        }
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        String str = stripColor(player.getDisplayName().getUnformattedText()).toUpperCase(Locale.ROOT);

        for (String str2 : stringArray) {
            if (str.startsWith(str2) || str.contains(" " + str2) || str.contains("[" + str2)) {
                return true;
            }
        }

        return false;
    }

    private static void pruneExpiredCombat() {
        long now = System.currentTimeMillis();
        lookupMap2.entrySet().removeIf(entry -> entry.getValue() <= now);
    }

    public static boolean matchesEntityPlayer2(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        return !flag ? true : WHelper.matchesString(player.getName());
    }

    private static String stripColor(String str) {
        return str == null ? "" : str.replaceAll("§[0-9a-fklmnor]", "");
    }

    private PruneExpiredCombatHelper() {
    }

    private static void handleEntityPlayer(EntityPlayer player) {
        int entityId = player.getEntityId();
        float health = player.getHealth();
        Float val = lookupMap.get(entityId);
        boolean flag = val != null && health < val - 0.001F;
        if (flag) {
            handleEntityPlayer3(player);
        }

        lookupMap.put(entityId, health);
    }

    public static long computeLong(EntityPlayer player) {
        if (player == null) {
            return 0L;
        }
        Long val = lookupMap2.get(player.getEntityId());
        return val == null ? 0L : Math.max(0L, val - System.currentTimeMillis());
    }

    public static void onEvent(Event event) {
        if (count > 0) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof RenderEvent4 renderEvent4) {
                    if (renderEvent4.world != renderEvent4.lastWorld) {
                        reset();
                    }
                } else if (event instanceof GetHealthEvent getHealthEvent) {
                    if (getHealthEvent.getEntity() instanceof EntityPlayer && getHealthEvent.getEntity() != mc.thePlayer) {
                        EntityPlayer entity = (EntityPlayer)getHealthEvent.getEntity();
                        if (matchesEntityPlayer2(entity)) {
                            if (getHealthEvent.getPreviousHealth() > 0.0F && getHealthEvent.getHealth() < getHealthEvent.getPreviousHealth() - 0.001F) {
                                handleEntityPlayer3(entity);
                            }

                            lookupMap.put(entity.getEntityId(), getHealthEvent.getHealth());
                        }
                    }
                } else {
                    if (event instanceof EventUpdate || event instanceof EventTick) {
                        EntityPlayerSP player = mc.thePlayer;
                        if (player == null) {
                            return;
                        }

                        if (player.ticksExisted == count2) {
                            return;
                        }

                        count2 = player.ticksExisted;
                        handleEntityPlayerSP(player);
                        pruneExpiredCombat();
                    }
                }
            }
        }
    }

    public static void reset() {
        lookupMap.clear();
        lookupMap2.clear();
        count2 = -1;
    }
}
