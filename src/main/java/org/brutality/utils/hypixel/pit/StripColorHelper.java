package org.brutality.utils.hypixel.pit;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.utils.performance.EntityCache;

public final class StripColorHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("Bounty:\\s*([\\d,]+(?:\\.\\d+)?)\\s*g", 2);
    private static final Pattern pattern2 = Pattern.compile("HOLD\\s*UP\\s*!.*?\\(?\\s*(\\d+)\\s*(?:s|sec(?:ond)?s?)\\s*(?:left|remaining)\\)?", 34);
    private static final long timestamp = 15000L;
    private static final long timestamp2 = 30000L;
    private static final long timestamp3 = 10000L;
    private static final double amount = 1000.0;
    private static final long timestamp4 = 1500L;
    private static int count = 0;
    private static long timestamp5 = 0L;
    private static long timestamp6 = 0L;
    private static float factor = -1.0F;
    private static final Map<Integer, Float> lookupMap = new HashMap<>();
    private static final Map<Integer, Long> lookupMap2 = new HashMap<>();
    private static int count2 = -1;

    private static void handleLong(long l) {
        Iterator it = lookupMap2.entrySet().iterator();

        while (it.hasNext()) {
            if (l - (Long)((Entry)it.next()).getValue() > 1500L) {
                it.remove();
            }
        }
    }

    public static long computeLong2() {
        return Math.max(computeLong3(), computeLong());
    }

    public static void process4() {
        count++;
    }

    public static void process5() {
        timestamp6 = 0L;
    }

    private static void clear() {
        timestamp5 = 0L;
        timestamp6 = 0L;
        factor = -1.0F;
        lookupMap.clear();
        lookupMap2.clear();
        count2 = -1;
    }

    private static void process6() {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null) {
            long now = System.currentTimeMillis();
            handleLong(now);
            float health = player.getHealth();
            if (factor > 0.0F && health < factor - 0.001F) {
                process3();
            }

            factor = health;

            for (EntityPlayer player2 : EntityCache.getEntries()) {
                if (player2 != player && !player2.isDead) {
                    handleEntityLivingBase(player2, now);
                }
            }
        }
    }

    public static boolean checkState() {
        return computeLong2() > 0L;
    }

    public static void process() {
        timestamp5 = 0L;
    }

    private static String stripColor(String str) {
        return str == null
            ? ""
            : str.replaceAll("(?i)§[0-9a-fk-orx]", "").replace('\u00a0', ' ').replaceAll("[\\p{Z}\\t\\r\\n]", " ").replaceAll("[^\\x20-\\x7E]", "");
    }

    private StripColorHelper() {
    }

    public static void reset() {
        clear();
    }

    public static void onEvent(Event event) {
        if (count > 0) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof RenderEvent4 renderEvent4) {
                    if (renderEvent4.world != renderEvent4.lastWorld) {
                        clear();
                    }
                } else if (event instanceof GetHealthEvent) {
                    onGetHealthEvent((GetHealthEvent)event);
                } else if (event instanceof GetDamageEvent) {
                    onGetDamageEvent((GetDamageEvent)event);
                } else if (event instanceof EventUpdate) {
                    EntityPlayerSP player = mc.thePlayer;
                    if (player != null) {
                        if (player.ticksExisted != count2) {
                            count2 = player.ticksExisted;
                            process6();
                        }
                    }
                } else {
                    if (event instanceof ChatEvent chatEvent && chatEvent.message != null) {
                        computeInt2(chatEvent.message.getUnformattedText());
                    }
                }
            }
        }
    }

    private static boolean checkState2() {
        return computeDouble() >= 1000.0;
    }

    public static void release() {
        count = Math.max(0, count - 1);
        if (count == 0) {
            clear();
        }
    }

    public static void handleInt2(int n) {
        handleInt(n);
    }

    public static int computeInt2(String str) {
        int n = computeInt(str);
        if (n < 0) {
            return -1;
        }
        handleInt(n);
        process5();
        return n;
    }

    public static void process2() {
        timestamp6 = System.currentTimeMillis() + 10000L;
    }

    public static void process3() {
        long l = checkState2() ? 30000L : 15000L;
        timestamp5 = System.currentTimeMillis() + l;
    }

    private static void handleEntityLivingBase(EntityLivingBase living, long l) {
        int entityId = living.getEntityId();
        float health = living.getHealth();
        Float val = lookupMap.get(entityId);
        if (val != null && health < val - 0.001F) {
            Long val2 = lookupMap2.get(entityId);
            if (val2 != null && l - val2 <= 1500L) {
                process3();
            }
        }

        lookupMap.put(entityId, health);
    }

    private static void onGetDamageEvent(GetDamageEvent getDamageEvent) {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && getDamageEvent.getEntity() == player) {
            if (getDamageEvent.getEntity2() instanceof EntityLivingBase) {
                lookupMap2.put(getDamageEvent.getEntity2().getEntityId(), System.currentTimeMillis());
                lookupMap.putIfAbsent(getDamageEvent.getEntity2().getEntityId(), ((EntityLivingBase)getDamageEvent.getEntity2()).getHealth());
            }
        }
    }

    private static void onGetHealthEvent(GetHealthEvent getHealthEvent) {
        EntityLivingBase entity = getHealthEvent.getEntity();
        float previousHealth = getHealthEvent.getPreviousHealth();
        float health = getHealthEvent.getHealth();
        if (!Float.isNaN(previousHealth)) {
            boolean flag = previousHealth > 0.0F && health < previousHealth - 0.001F;
            if (entity == mc.thePlayer) {
                if (flag) {
                    process3();
                }

                factor = health;
            } else {
                Long val = lookupMap2.get(entity.getEntityId());
                if (flag && val != null && System.currentTimeMillis() - val <= 1500L) {
                    process3();
                }

                lookupMap.put(entity.getEntityId(), health);
            }
        }
    }

    public static long computeLong3() {
        return Math.max(0L, timestamp5 - System.currentTimeMillis());
    }

    public static int computeInt(String str) {
        if (str == null) {
            return -1;
        }
        String str2 = stripColor(str.trim());
        Matcher matcher = pattern2.matcher(str2);
        if (!matcher.find()) {
            return -1;
        }
        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void handleInt(int n) {
        if (n > 0) {
            timestamp5 = System.currentTimeMillis() + n * 1000L;
        }
    }

    private static double computeDouble() {
        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return 0.0;
            }

            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return 0.0;
            }

            for (Score score : scoreboard.getSortedScores(objectiveInDisplaySlot)) {
                String str = stripColor(score.getPlayerName());
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    return Double.parseDouble(matcher.group(1).replace(",", ""));
                }
            }
        } catch (Exception ignored) {
        }

        return 0.0;
    }

    public static long computeLong() {
        return Math.max(0L, timestamp6 - System.currentTimeMillis());
    }
}
