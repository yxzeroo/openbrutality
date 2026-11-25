package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.StopwatchTimer;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;

public class BedrockAlerts extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final NumberSetting intervalSecondsSetting = new NumberSetting("Interval (seconds)", this, 5.0, 1.0, 120.0, 1.0);
    private final BooleanSetting alertHeldBedrockSetting = new BooleanSetting("Alert Held Bedrock", this, true);
    private final BooleanSetting espSetting = new BooleanSetting("ESP", this, true);
    private final NumberSetting espRangeSetting = new NumberSetting("ESP Range", this, 16.0, 4.0, 32.0, 1.0)
        .computeNumberSetting(() -> this.espSetting.isEnabled());
    private final Set<BlockPos> itemSet = new HashSet<>();
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final Map<String, Long> lookupMap = new HashMap<>();
    private final Map<String, Float> lookupMap2 = new HashMap<>();

    public BedrockAlerts() {
        super("Bedrock Alerts", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{this.kosOnlySetting, this.intervalSecondsSetting, this.alertHeldBedrockSetting, this.espSetting, this.espRangeSetting}
        );
    }

    private void process3() {
        this.itemSet.clear();
        double d = this.espRangeSetting.computeDouble() * this.espRangeSetting.computeDouble();

        for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
            if (playerEntity != mc.thePlayer
                && !playerEntity.isDead
                && playerEntity.isEntityAlive()
                && !(playerEntity.getDistanceSqToEntity(mc.thePlayer) > d)
                && !FreshHelper.matchesWorld3(mc.theWorld, playerEntity)
                && (!this.kosOnlySetting.isEnabled() || WHelper.matchesString(playerEntity.getName()))) {
                ArrayList list = new ArrayList(6);

                for (BlockPos pos : this.computeBlockPosArray(playerEntity)) {
                    if (mc.theWorld.isBlockLoaded(pos) && mc.theWorld.getBlockState(pos).getBlock() == Blocks.bedrock) {
                        list.add(pos);
                    }
                }

                if (list.size() >= 4) {
                    this.itemSet.addAll(list);
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.lookupMap.clear();
                this.lookupMap2.clear();
                this.itemSet.clear();
            } else if (event instanceof GetEntityEvent) {
                if (((GetEntityEvent)event).getEntity() instanceof EntityPlayer) {
                    this.lookupMap.remove(((GetEntityEvent)event).getEntity().getName());
                    this.lookupMap2.remove(((GetEntityEvent)event).getEntity().getName());
                    this.itemSet.clear();
                }
            } else if (event instanceof EventRender3D) {
                if (this.espSetting.isEnabled() && mc.theWorld != null && mc.thePlayer != null) {
                    this.process();
                }
            } else if (event instanceof EventTick) {
                if (((EventTick)event).getState() == EventState.PRE) {
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        if (!this.espSetting.isEnabled()) {
                            this.itemSet.clear();
                        } else {
                            this.process3();
                        }

                        if (this.utilHelper8.matchesLong((long)this.intervalSecondsSetting.computeDouble() * 1000L)) {
                            this.utilHelper8.reset();
                            HashSet set = new HashSet();

                            for (EntityPlayer player : EntityCache.getEntries()) {
                                if (player != mc.thePlayer && !player.isDead && player.isEntityAlive()) {
                                    set.add(player.getName());
                                    Float val = this.lookupMap2.put(player.getName(), player.getHealth());
                                    if (!FreshHelper.matchesWorld3(mc.theWorld, player)
                                        && (val == null || !(val < player.getMaxHealth()) || !(player.getHealth() >= player.getMaxHealth()))) {
                                        if ((!this.kosOnlySetting.isEnabled() || WHelper.matchesString(player.getName()))
                                            && (this.matchesEntityPlayer(player) || this.alertHeldBedrockSetting.isEnabled() && this.matchesEntityPlayer2(player))) {
                                            long now = System.currentTimeMillis();
                                            Long val2 = this.lookupMap.get(player.getName());
                                            if (val2 == null || val2 <= now) {
                                                String str = FreshHelper.computeString(player, mc.theWorld).replaceAll("§[0-9a-fk-or]", "").trim();
                                                BlockPos pos = player.getPosition();
                                                EntityPlayerSP player2 = mc.thePlayer;
                                                String name = player.getName();
                                                int x = pos.getX();
                                                int z = pos.getZ();
                                                int y = pos.getY();
                                                int n = x;
                                                String str2 = name;
                                                player2.sendChatMessage(
                                                    "/gc TNT needed for " + str2 + " in " + str + " (" + n + ", " + y + ", " + z + ")"
                                                );
                                                this.lookupMap.put(player.getName(), now + 10000L);
                                            }
                                        }
                                    } else {
                                        this.lookupMap.remove(player.getName());
                                    }
                                }
                            }

                            this.lookupMap.keySet().removeIf(str3 -> !set.contains(str3));
                            this.lookupMap2.keySet().removeIf(str4 -> !set.contains(str4));
                        }
                    } else {
                        this.itemSet.clear();
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        this.itemSet.clear();
        this.lookupMap.clear();
        this.lookupMap2.clear();
        super.onDisable();
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        return heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Blocks.bedrock);
    }

    private void process() {
        Color hsbColor = Color.getHSBColor((float)(System.currentTimeMillis() % 5000L) / 5000.0F, 1.0F, 1.0F);

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            for (BlockPos pos : this.itemSet) {
                if (!(
                        pos.distanceSq(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                            > this.espRangeSetting.computeDouble() * this.espRangeSetting.computeDouble()
                    )
                    && mc.theWorld.getBlockState(pos).getBlock() == Blocks.bedrock) {
                    ResetColorHelper.handleBlockPos2(pos, new Color(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), 45), false, true, 1.5F);
                    ResetColorHelper.handleBlockPos2(pos, hsbColor, true, false, 1.5F);
                }
            }
        }
    }

    private BlockPos[] computeBlockPosArray(EntityPlayer player) {
        BlockPos pos = player.getPosition();
        return new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        int n = 0;

        for (BlockPos pos : this.computeBlockPosArray(player)) {
            if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.bedrock) {
                n++;
            }
        }

        return n >= 4;
    }
}
