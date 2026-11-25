package org.brutality.module.impl.pit.hypixel.utility;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class JumpBoostDetector extends Module {
    private static JumpBoostDetector instance;
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting detectOthersSetting = new BooleanSetting("Detect Others", this, true);
    private final Map<String, Long> lookupMap = new HashMap<>();
    private final Map<String, Boolean> lookupMap2 = new HashMap<>();
    private final Map<String, Integer> lookupMap3 = new HashMap<>();
    private final Map<String, Long> lookupMap4 = new HashMap<>();
    private final Map<String, Boolean> lookupMap5 = new HashMap<>();

    private static boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion && !ItemPotion.isSplash(stack.getItemDamage())) {
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("CustomPotionEffects", 9)) {
                NBTTagList tagList = stack.getTagCompound().getTagList("CustomPotionEffects", 10);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    if (compoundTagAt.getByte("Id") == 8 && compoundTagAt.getByte("Amplifier") == 3 && compoundTagAt.getInteger("Duration") > 0) {
                        return true;
                    }
                }

                return false;
            }
            if (!stack.hasDisplayName()) {
                return false;
            }
            String str = StringUtils.stripControlCodes(stack.getDisplayName()).toLowerCase(Locale.ROOT);
            return str.contains("potion of jump iv") || str.contains("jump boost iv") || str.contains("jump boost 4");
        }
        return false;
    }

    public static void handleEntityPlayer3(EntityPlayer player) {
        JumpBoostDetector jumpBoostDetector = instance;
        if (jumpBoostDetector != null && jumpBoostDetector.checkState44() && jumpBoostDetector.detectOthersSetting.isEnabled() && player != null && player != mc.thePlayer && player.isEntityAlive()) {
            long now = System.currentTimeMillis();
            Long val = jumpBoostDetector.lookupMap4.remove(player.getName());
            if (matchesItemStack(player.getHeldItem()) || val != null && now - val <= 1000L) {
                jumpBoostDetector.lookupMap.put(player.getName(), now + 30000L);
            }
        }
    }

    private void handleString(String str, ItemStack stack, long l) {
        boolean flag = stack != null && stack.getItem() == Items.glass_bottle;
        if (Boolean.TRUE.equals(this.lookupMap2.get(str)) && flag) {
            this.lookupMap.put(str, l + 30000L);
        }

        this.lookupMap2.put(str, matchesItemStack(stack));
    }

    public JumpBoostDetector() {
        super("Jump Boost Detector", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.detectOthersSetting});
        instance = this;
    }

    public boolean checkState() {
        return this.detectOthersSetting.isEnabled();
    }

    public void handleFlag(boolean flag) {
        this.detectOthersSetting.setEnabled(flag);
    }

    public static void handleEntityPlayer2(EntityPlayer player) {
        JumpBoostDetector jumpBoostDetector = instance;
        if (jumpBoostDetector != null && jumpBoostDetector.checkState44() && player != null && player != mc.thePlayer) {
            if (matchesItemStack(player.getHeldItem()) && (player.isEating() || player.isUsingItem())) {
                jumpBoostDetector.lookupMap4.put(player.getName(), System.currentTimeMillis());
            }
        }
    }

    public static void handleEntityPlayer(EntityPlayer player) {
        JumpBoostDetector jumpBoostDetector = instance;
        if (jumpBoostDetector != null && jumpBoostDetector.checkState44() && jumpBoostDetector.detectOthersSetting.isEnabled() && player != null && player != mc.thePlayer) {
            String name = player.getName();
            if (player.isEating()) {
                if (matchesItemStack(player.getHeldItem())) {
                    jumpBoostDetector.lookupMap3.putIfAbsent(name, player.ticksExisted);
                }
            } else {
                Integer integer = jumpBoostDetector.lookupMap3.remove(name);
                if (integer != null && player.ticksExisted - integer >= 30 && player.ticksExisted - integer <= 50) {
                    jumpBoostDetector.lookupMap.put(name, System.currentTimeMillis() + 30000L);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.lookupMap3.clear();
                this.lookupMap4.clear();
                this.lookupMap.clear();
                this.lookupMap2.clear();
                this.lookupMap5.clear();
            } else if (event instanceof GetEntityEvent) {
                EntityPlayer player = ((GetEntityEvent)event).getEntity() instanceof EntityPlayer ? (EntityPlayer)((GetEntityEvent)event).getEntity() : null;
                if (player != null) {
                    this.lookupMap3.remove(player.getName());
                    this.lookupMap4.remove(player.getName());
                    this.lookupMap.remove(player.getName());
                    this.lookupMap2.remove(player.getName());
                    this.lookupMap5.remove(player.getName());
                }
            } else if (event instanceof EventTick && mc.theWorld != null) {
                long now = System.currentTimeMillis();
                this.lookupMap3.keySet().removeIf(str -> mc.theWorld.playerEntities.stream().noneMatch(player2 -> player2.getName().equals(str)));
                this.lookupMap4.entrySet().removeIf(entry -> now - entry.getValue() > 1000L);
                this.lookupMap.entrySet().removeIf(entry2 -> entry2.getValue() <= now);
                this.lookupMap.keySet().removeIf(str2 -> mc.theWorld.playerEntities.stream().noneMatch(player3 -> player3.getName().equals(str2)));
                this.lookupMap5.keySet().removeIf(str3 -> mc.theWorld.playerEntities.stream().noneMatch(player4 -> player4.getName().equals(str3)));
                this.lookupMap2.keySet().removeIf(str4 -> mc.theWorld.playerEntities.stream().noneMatch(player5 -> player5.getName().equals(str4)));
                if (!this.detectOthersSetting.isEnabled()) {
                    this.lookupMap3.clear();
                    this.lookupMap2.clear();
                } else {
                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (playerEntity != mc.thePlayer && this.detectOthersSetting.isEnabled()) {
                            String name = playerEntity.getName();
                            boolean flag = FreshHelper.matchesWorld2(mc.theWorld, playerEntity);
                            if (flag && Boolean.FALSE.equals(this.lookupMap5.get(name))) {
                                this.lookupMap.remove(name);
                            }

                            this.lookupMap5.put(name, flag);
                            if (!playerEntity.isDead && !(playerEntity.getHealth() <= 0.0F)) {
                                handleEntityPlayer(playerEntity);
                                handleEntityPlayer2(playerEntity);
                                this.handleString(name, playerEntity.getHeldItem(), now);
                            } else {
                                this.lookupMap.remove(name);
                                this.lookupMap2.remove(name);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean matchesString(String str) {
        Long val = this.lookupMap.get(str);
        return val != null && val > System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap3.clear();
        this.lookupMap4.clear();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        this.lookupMap5.clear();
    }

    public static JumpBoostDetector getInstance() {
        return instance;
    }

    public int computeInt(String str) {
        Long val = this.lookupMap.get(str);
        return val == null ? 0 : Math.max(0, (int)((val - System.currentTimeMillis() + 999L) / 1000L));
    }
}
