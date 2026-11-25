package org.brutality.module.impl.visual;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.killeffects.BloodExplosionHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;

public class KillEffects extends Module {
    public final ModeSetting lightningBoltSetting = new ModeSetting("Kill Effect", this, "Blood Explosion", Arrays.asList("Blood Explosion", "Lightning Bolt"));
    private final Map<String, KillEffects.Entry3> lookupMap = new HashMap<>();
    private final BloodExplosionHandler bloodExplosionHandler = new BloodExplosionHandler();
    private final Map<EntityLivingBase, KillEffects.Entry> lookupMap2 = new WeakHashMap<>();
    private final Map<String, Long> lookupMap3 = new HashMap<>();
    private static final long timestamp = 3000L;
    private static final long timestamp2 = 10000L;
    private static final Pattern pattern = Pattern.compile("^([A-Za-z0-9_]{1,16})\\s+(?:(?:was\\s+)?(?:killed|slain)\\s+)?by\\s+[A-Za-z0-9_]{1,16}\\b", 2);
    private static final Pattern pattern2 = Pattern.compile("^KILL!\\s+(?:on\\s+)?([A-Za-z0-9_]{1,16})\\b", 2);
    private static KillEffects instance;

    @Override
    public void process2() {
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.lookupMap3.clear();
    }

    private void handleEntityPlayer(EntityPlayer player, KillEffects.Entry2 entry2) {
        String str = player.getName().toLowerCase(Locale.ROOT);
        this.lookupMap.remove(str);
        if (this.computeEntry(player) == null) {
            this.lookupMap2.put(player, new KillEffects.Entry(player, player.getHealth() <= 0.0F || player.deathTime > 0));
            this.lookupMap3.put(str, System.currentTimeMillis());
            this.bloodExplosionHandler.handleEntry2(entry2);
        }
    }

    private static KillEffects.Entry2 computeEntry2(EntityLivingBase living) {
        return new KillEffects.Entry2(living.posX, living.posY, living.posZ, living.getEyeHeight());
    }

    private KillEffects.Entry computeEntry(EntityLivingBase living) {
        KillEffects.Entry entry = this.lookupMap2.get(living);
        if (entry != null && entry.weakReference.get() != living) {
            this.lookupMap2.remove(living);
            return null;
        }
        return entry;
    }

    @Override
    public void onDisable() {
        this.process2();
        super.onDisable();
    }

    public KillEffects() {
        super("KillEffects", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.lightningBoltSetting});
        instance = this;
    }

    private void handleEntityLivingBase(EntityLivingBase living) {
        if (this.matchesEntityLivingBase(living)) {
            EntityPlayer player = (EntityPlayer)living;
            boolean flag = player.getHealth() <= 0.0F || player.deathTime > 0;
            KillEffects.Entry entry = this.computeEntry(player);
            if (flag) {
                if (entry != null) {
                    entry.flag = true;
                }

                this.handleEntityPlayer(player, computeEntry2(player));
            } else {
                if (entry != null && (entry.flag || System.currentTimeMillis() - entry.timestamp > 3000L)) {
                    this.lookupMap2.remove(player);
                }

                this.lookupMap.put(player.getName().toLowerCase(Locale.ROOT), new KillEffects.Entry3(player, computeEntry2(player)));
            }
        }
    }

    private boolean matchesEntityLivingBase(EntityLivingBase living) {
        return living instanceof EntityPlayer && living != mc.thePlayer && living.worldObj == mc.theWorld;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.process2();
            } else if (mc.theWorld != null && mc.thePlayer != null) {
                if (event instanceof GetEntityEvent) {
                    EntityLivingBase entity = ((GetEntityEvent)event).getEntity();
                    if (this.matchesEntityLivingBase(entity)) {
                        this.handleEntityPlayer((EntityPlayer)entity, computeEntry2(entity));
                    }
                } else if (event instanceof GetHealthEvent) {
                    this.handleEntityLivingBase(((GetHealthEvent)event).getEntity());
                } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    long now = System.currentTimeMillis();
                    this.lookupMap.entrySet().removeIf(entry -> now - entry.getValue().entry2.timestamp > 10000L);
                    this.lookupMap3.entrySet().removeIf(entry2 -> now - entry2.getValue() > 10000L);

                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        this.handleEntityLivingBase(playerEntity);
                    }
                } else if (event instanceof ChatEvent chatEvent) {
                    if (chatEvent.byteVal != 0 || chatEvent.message == null) {
                        return;
                    }

                    String str = StringUtils.stripControlCodes(chatEvent.message.getUnformattedText()).trim();
                    if (str.contains(":")) {
                        return;
                    }

                    Matcher matcher = pattern.matcher(str);
                    if (!matcher.find()) {
                        matcher = pattern2.matcher(str);
                        if (!matcher.find()) {
                            return;
                        }
                    }

                    String str2 = matcher.group(1).toLowerCase(Locale.ROOT);
                    Long val = this.lookupMap3.get(str2);
                    if (val != null && System.currentTimeMillis() - val <= 3000L) {
                        return;
                    }

                    KillEffects.Entry3 entry3 = this.lookupMap.get(str2);
                    if (entry3 != null && System.currentTimeMillis() - entry3.entry2.timestamp <= 10000L) {
                        this.handleEntityPlayer(entry3.entityPlayer, entry3.entry2);
                    }
                }
            }
        }
    }

    @Generated
    public static KillEffects getInstance() {
        return instance;
    }

    public static final class Entry {
        public final WeakReference<EntityLivingBase> weakReference;
        public final long timestamp = System.currentTimeMillis();
        public boolean flag;

        public Entry(EntityLivingBase living, boolean flag) {
            this.weakReference = new WeakReference<>(living);
            this.flag = flag;
        }
    }

    public static class Entry2 {
        public final double amount;
        public final double amount2;
        public final double amount3;
        public final double amount4;
        public final long timestamp = System.currentTimeMillis();

        public Entry2(double amount, double amount2, double amount3, double amount4) {
            this.amount = amount;
            this.amount2 = amount2;
            this.amount3 = amount3;
            this.amount4 = amount4;
        }
    }

    public static final class Entry3 {
        public final EntityPlayer entityPlayer;
        public final KillEffects.Entry2 entry2;

        public Entry3(EntityPlayer entityPlayer, KillEffects.Entry2 entry2) {
            this.entityPlayer = entityPlayer;
            this.entry2 = entry2;
        }
    }
}
