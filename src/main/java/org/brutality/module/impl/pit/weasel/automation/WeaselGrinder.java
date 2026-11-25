package org.brutality.module.impl.pit.weasel.automation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.timer.TimerUtil;
import org.lwjgl.input.Mouse;

public class WeaselGrinder extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil timerUtil = new TimerUtil();
    private final TimerUtil timerUtil2 = new TimerUtil();
    private final NumberSetting aimReachSetting = new NumberSetting("Aim Reach", this, 35.0, 10.0, 50.0, 1.0);
    private final NumberSetting attackReachSetting = new NumberSetting("Attack Reach", this, 4.0, 2.0, 6.0, 1.0);
    private final NumberSetting yawSmoothnessSetting = new NumberSetting("Yaw Smoothness", this, 7.9, 1.0, 10.0, 1.0);
    private final NumberSetting pitchSmoothnessSetting = new NumberSetting("Pitch Smoothness", this, 9.0, 1.0, 10.0, 1.0);
    private final NumberSetting jumpChanceSetting = new NumberSetting("Jump Chance", this, 0.02, 0.0, 0.1, 0.01);
    private final NumberSetting strafeChanceSetting = new NumberSetting("Strafe Chance", this, 0.02, 0.0, 0.1, 0.01);
    private final NumberSetting sprintResetTicksSetting = new NumberSetting("Sprint Reset Ticks", this, 50.0, 20.0, 100.0, 1.0);
    private final NumberSetting waitChanceSetting = new NumberSetting("Wait Chance", this, 0.3, 0.0, 1.0, 0.1);
    private final NumberSetting sessionStopMinSetting = new NumberSetting("Session Stop (min)", this, 20.0, 5.0, 60.0, 1.0);
    private final NumberSetting distanceSqSetting = new NumberSetting("Distance Sq", this, 10.0, 5.0, 20.0, 1.0);
    private final NumberSetting switchTargetRangeSetting = new NumberSetting("Switch Target Range", this, 1.0, 0.5, 3.0, 1.0);
    private final NumberSetting skipAimRangeSetting = new NumberSetting("Skip Aim Range", this, 1.0, 0.5, 3.0, 1.0);
    private final BooleanSetting quitOnPanicSetting = new BooleanSetting("Quit on Panic", this, false);
    private final NumberSetting hudxSetting = new NumberSetting("HUD X", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting hudySetting = new NumberSetting("HUD Y", this, 10.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showHUDSetting = new BooleanSetting("Show HUD", this, true);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final BooleanSetting lockTargetSetting = new BooleanSetting("Lock Target", this, true);
    private final BooleanSetting autoXCarrySetting = new BooleanSetting("AutoXCarry", this, false);
    private final NumberSetting xCarryHealthSetting = new NumberSetting("XCarry Health", this, 11.0, 1.0, 20.0, 1.0);
    private final BooleanSetting autoBuySetting = new BooleanSetting("AutoBuy", this, false);
    private final BooleanSetting autoUberSetting = new BooleanSetting("AutoUber", this, false);
    private final BooleanSetting autoSwordSetting = new BooleanSetting("AutoSword", this, false);
    private final NumberSetting swordSlotSetting = new NumberSetting("Sword Slot", this, 1.0, 1.0, 9.0, 1.0);
    private WeaselGrinder.WalkingMode walkingMode = WeaselGrinder.WalkingMode.WALKING;
    private BlockPos center;
    private int count = 0;
    private int count2 = 0;
    private double[] doubleValueArray;
    private int count3 = 0;
    private int count4;
    private int count5;
    private int count6 = 0;
    private int count7;
    private boolean flag = false;
    private boolean flag2 = false;
    private int count8;
    private int count9;
    private long timestamp;
    private EntityPlayer entityPlayer;
    private int count10 = 0;
    private boolean dragging;
    private int dragOffsetX;
    private int dragOffsetY;
    private boolean flag3 = false;
    private boolean flag4 = true;
    private int count11 = 0;
    private Map<String, Integer> lookupMap = new HashMap<>();
    private Set<String> itemSet = new HashSet<>();
    private boolean flag5 = false;
    private long timestamp2 = 0L;
    private static final long timestamp3 = 5000L;

    private boolean checkState() {
        return this.mc.gameSettings.keyBindForward.isKeyDown();
    }

    private String computeString(int n) {
        int n2 = n / 60;
        int n3 = n % 60;
        return n2 + (n3 < 10 ? ":0" + n3 : ":" + n3);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
    }

    private void process4() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), false);
        if (this.debugSetting.isEnabled()) {
            this.sendMessage(
                "Debug: stopWalking() called. Forward="
                    + this.mc.gameSettings.keyBindForward.isKeyDown()
                    + ", Sprint="
                    + this.mc.gameSettings.keyBindSprint.isKeyDown()
            );
        }
    }

    private int computeInt(int n) {
        return n / 60;
    }

    private void process7() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
        if (this.debugSetting.isEnabled()) {
            this.sendMessage(
                "Debug: startWalking() called. Forward="
                    + this.mc.gameSettings.keyBindForward.isKeyDown()
                    + ", Sprint="
                    + this.mc.gameSettings.keyBindSprint.isKeyDown()
            );
        }
    }

    private void process6() {
        if (this.count > 200 && this.count < 300) {
            float[] floats = this.computeFloatArray(
                this.computeFloatArray(this.computeFloatArray(new BlockPos(this.center.getX() + 1, this.center.getY() - 1, this.center.getZ())), 0.95, 0.95),
                new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
            );
            this.mc.thePlayer.rotationYaw = floats[0];
        } else if (this.count >= 350 && this.count <= 600) {
            float[] floats2 = this.computeFloatArray(
                this.computeFloatArray(this.computeFloatArray(new BlockPos(this.center.getX() - 1, this.center.getY() - 1, this.center.getZ())), 0.95, 0.95),
                new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
            );
            this.mc.thePlayer.rotationYaw = floats2[0];
            if (this.count == 360) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
            } else if (this.count == 600) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
            }
        } else if (this.count > 700) {
            if (this.quitOnPanicSetting.isEnabled()) {
                this.mc.thePlayer.sendChatMessage("/quit");
                this.process28();
            } else {
                this.flag = true;
                this.up();
            }
        }
    }

    private void process3() {
        if (this.mc.currentScreen instanceof GuiChat) {
            int n = Mouse.getX() / 2;
            int n2 = (this.mc.displayHeight - Mouse.getY()) / 2;
            String str = "§cWeaselGrinder";
            String str2 = "§fPhase: §7" + this.walkingMode.toString();
            String str3 = "§fLoop: §7" + this.count2;
            String str4 = "§fSession: §7" + this.computeString((int)((System.currentTimeMillis() - this.timestamp) / 1000L));
            String str5 = this.entityPlayer != null ? "§fTarget: §7" + this.entityPlayer.getDisplayName().getFormattedText() : null;
            String str6 = str.replaceAll("§[0-9a-fk-or]", "");
            String str7 = str2.replaceAll("§[0-9a-fk-or]", "");
            String str8 = str3.replaceAll("§[0-9a-fk-or]", "");
            String str9 = str4.replaceAll("§[0-9a-fk-or]", "");
            String str10 = str5 != null ? str5.replaceAll("§[0-9a-fk-or]", "") : null;
            int n3 = Math.max(
                computeFontRenderer(WeaselGrinder.class).getStringWidth(str6),
                Math.max(
                    computeFontRenderer(WeaselGrinder.class).getStringWidth(str7),
                    Math.max(computeFontRenderer(WeaselGrinder.class).getStringWidth(str8), computeFontRenderer(WeaselGrinder.class).getStringWidth(str9))
                )
            );
            if (str10 != null) {
                n3 = Math.max(n3, computeFontRenderer(WeaselGrinder.class).getStringWidth(str10));
            }

            n3 += 8;
            int n4 = (this.entityPlayer != null ? 5 : 4) * computeFontRenderer(WeaselGrinder.class).FONT_HEIGHT + 7;
            if (Mouse.isButtonDown(0)) {
                if (this.dragging) {
                    this.hudxSetting.handleDouble2(n - this.dragOffsetX);
                    this.hudySetting.handleDouble2(n2 - this.dragOffsetY);
                } else if (this.matchesInt(n, n2, (int)this.hudxSetting.computeDouble(), (int)this.hudySetting.computeDouble(), n3, n4)) {
                    this.dragging = true;
                    this.dragOffsetX = n - (int)this.hudxSetting.computeDouble();
                    this.dragOffsetY = n2 - (int)this.hudySetting.computeDouble();
                }
            } else {
                this.dragging = false;
            }
        } else {
            this.dragging = false;
        }
    }

    private float[] computeFloatArray(BlockPos pos) {
        if (pos != null && this.mc.theWorld != null && this.mc.thePlayer != null) {
            double d = pos.getX() + 0.5 - this.mc.thePlayer.posX;
            double d2 = pos.getY() + 0.5 - this.mc.thePlayer.posY - this.mc.thePlayer.getEyeHeight();
            double d3 = pos.getZ() + 0.5 - this.mc.thePlayer.posZ;
            float f = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F;
            float f2 = (float)(-Math.toDegrees(Math.atan2(d2, Math.sqrt(d * d + d3 * d3))));
            return new float[]{f, f2};
        }
        return new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch};
    }

    private void process() {
        if (this.autoUberSetting.isEnabled()) {
            long now = System.currentTimeMillis();
            if (now - this.timestamp2 >= 5000L) {
                this.process8();
                this.timestamp2 = now;
            }
        }

        if (this.autoSwordSetting.isEnabled()) {
            int n = (int)this.swordSlotSetting.computeDouble() - 1;
            if (this.mc.thePlayer.inventory.currentItem != n) {
                this.mc.thePlayer.inventory.currentItem = n;
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("AutoSword: Switched to slot " + (n + 1));
                }
            }
        }

        if (this.autoBuySetting.isEnabled() && FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
            Module module = Brutality.moduleManager.computeModule("AutoBuy");
            if (module == null) {
                this.sendMessage("AutoBuy module not found!");
            } else if (!module.checkState44() && !this.flag3 && this.flag4) {
                this.sendMessage("In spawn, enabling AutoBuy and pausing WeaselGrinder.");
                module.process28();
                this.flag3 = true;
                this.flag4 = false;
                this.process4();
                this.handleWalkingMode(WeaselGrinder.WalkingMode.WAITING);
                this.count3 = Integer.MAX_VALUE;
            } else {
                if (module.checkState44() || !this.flag3 || this.walkingMode != WeaselGrinder.WalkingMode.WAITING || this.count3 != Integer.MAX_VALUE) {
                    this.process4();
                    return;
                }

                this.sendMessage("AutoBuy has finished, resuming WeaselGrinder behavior.");
                this.flag3 = false;
                this.count3 = 0;
                this.handleWalkingMode(WeaselGrinder.WalkingMode.WALKING);
                this.process7();
            }
        } else if (this.autoBuySetting.isEnabled() && !FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer) && (this.flag3 || !this.flag4)) {
            this.flag3 = false;
            this.flag4 = true;
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: Left spawn, reset AutoBuy trigger and allowance.");
            }
        }

        if (FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
            this.entityPlayer = null;
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: In spawn, cleared target.");
            }
        }

        if (this.count % 20 == 0 && this.debugSetting.isEnabled()) {
            WeaselGrinder.WalkingMode walkingMode = this.walkingMode;
            String str = String.format("%.1f", this.mc.thePlayer.posY);
            var obj = this.entityPlayer != null ? this.entityPlayer.getDisplayName() : "None";
            boolean flag = this.checkState();
            var obj2 = obj;
            String str2 = str;
            WeaselGrinder.WalkingMode walkingMode2 = walkingMode;
            this.sendMessage("Debug: Phase=" + walkingMode2 + ", PosY=" + str2 + ", Target=" + obj2 + ", IsWalking=" + flag);
        }

        if (FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)
            && this.count > 50
            && this.flag2
            && this.walkingMode != WeaselGrinder.WalkingMode.WALKING
            && this.walkingMode != WeaselGrinder.WalkingMode.WAITING
            && this.walkingMode != WeaselGrinder.WalkingMode.PANIC) {
            this.sendMessage("Bot died or respawned!");
            this.process4();
            this.count2++;
            this.count = 0;
            this.count10 = 0;
            this.count9 = (int)this.computeDouble(5.0, 20.0);
            this.doubleValueArray = new double[]{
                new Random().nextInt(20), new Random().nextInt(30), new Random().nextInt(40), this.computeDouble(0.5, 0.95), this.computeDouble(0.85, 0.95)
            };
            this.center = new BlockPos(0.0, this.mc.thePlayer.posY - 8.0, 0.0);
            this.entityPlayer = null;
            this.flag2 = false;
            if (this.flag) {
                this.up();
            } else {
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Respawned, returning to WALKING phase.");
                }

                if (new Random().nextDouble() <= this.waitChanceSetting.computeDouble()) {
                    this.count3 = (int)this.computeDouble(250.0, 500.0);
                    this.handleWalkingMode(WeaselGrinder.WalkingMode.WAITING);
                } else {
                    this.handleWalkingMode(WeaselGrinder.WalkingMode.WALKING);
                    this.process7();
                }
            }
        } else {
            if (this.debugSetting.isEnabled() && FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer) && this.flag2) {
                this.sendMessage("Debug: In spawn with justRespawned=true, but phase=" + this.walkingMode + ", skipping respawn logic.");
            }

            this.count++;
            if (this.count4 > 0) {
                this.count4--;
            }

            if (this.computeInt((int)((System.currentTimeMillis() - this.timestamp) / 1000L)) >= this.count7) {
                this.count7 = this.count7 + (int)this.sessionStopMinSetting.computeDouble();
                this.flag = true;
            }

            if (this.mc.thePlayer.isCollidedHorizontally) {
                if (this.timerUtil2.matchesLong5(5000L)) {
                    this.process10();
                    this.timerUtil2.reset();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Collided for 5 seconds, executing /spawn.");
                    }
                }
            } else {
                this.timerUtil2.reset();
            }

            if (this.walkingMode == WeaselGrinder.WalkingMode.WALKING || this.walkingMode == WeaselGrinder.WalkingMode.FALLING) {
                Module module2 = Brutality.moduleManager.computeModule("Kill Aura");
                if (module2 != null && module2.checkState44()) {
                    try {
                        Entity entity = (Entity)module2.getClass().getField("target").get(module2);
                        if (entity instanceof EntityPlayer && !FreshHelper.matchesWorld3(this.mc.theWorld, (EntityPlayer)entity)) {
                            this.entityPlayer = (EntityPlayer)entity;

                            for (int i = 0; i < 9; i++) {
                                ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                                if (stack != null && stack.getItem() instanceof ItemSword) {
                                    this.mc.thePlayer.inventory.currentItem = i;
                                    break;
                                }
                            }

                            this.handleWalkingMode(WeaselGrinder.WalkingMode.FIGHTING);
                            this.process7();
                            return;
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        if (this.debugSetting.isEnabled()) {
                            this.sendMessage("Debug: Failed to access Killaura target: " + e.getMessage());
                        }
                    }
                }
            }

            switch (this.walkingMode) {
                case WALKING:
                    this.process5();
                    break;
                case WAITING:
                    this.uv();
                    break;
                case FALLING:
                    this.process11();
                    break;
                case FIGHTING:
                    this.process12();
                    break;
                case PANIC:
                    this.process6();
                    break;
                case MYSTIC:
                    this.mystic();
            }
        }
    }

    private void up() {
        this.mc.thePlayer.sendChatMessage("/l");
        this.count3 = (int)this.computeDouble(4500.0, 8000.0);
        this.handleWalkingMode(WeaselGrinder.WalkingMode.WAITING);
        if (this.debugSetting.isEnabled()) {
            this.sendMessage("Debug: Session paused, transitioning to WAITING phase.");
        }
    }

    private void handleWalkingMode(WeaselGrinder.WalkingMode walkingMode) {
        if (this.walkingMode != walkingMode && this.debugSetting.isEnabled()) {
            this.sendMessage("Debug: Phase changed from " + this.walkingMode + " to " + walkingMode);
        }

        this.walkingMode = walkingMode;
        this.count = 0;
    }

    private float[] computeFloatArray(float[] floats, float[] floats2) {
        float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f2 = f * f * f * 1.2F;
        float f3 = floats[0] - floats2[0];
        float f4 = floats[1] - floats2[1];
        float f5 = f3 - f3 % f2;
        float f6 = f4 - f4 % f2;
        return new float[]{floats2[0] + f5, floats2[1] + f6};
    }

    private void sendMessage(String str) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception e) {
            System.out.println("[WeaselGrinder] Failed to send chat message: " + str + " | Error: " + e.getMessage());
        }

        System.out.println("[WeaselGrinder] " + str);
    }

    private void mystic() {
        EntityItem entity = this.mc
            .theWorld
            .loadedEntityList
            .stream()
            .filter(entity2 -> entity2 instanceof EntityItem)
            .map(entity3 -> (EntityItem)entity3)
            .filter(
                entity4 -> {
                    ItemStack entityItem = entity4.getEntityItem();
                    return entityItem != null
                        && (
                            Item.getIdFromItem(entityItem.getItem()) == 300
                                || Item.getIdFromItem(entityItem.getItem()) == 283
                                || Item.getIdFromItem(entityItem.getItem()) == 261
                        );
                }
            )
            .min(Comparator.comparingDouble(entity5 -> this.mc.thePlayer.getDistanceToEntity(entity5)))
            .orElse(null);
        if (entity == null) {
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: No mystic item (leather armor, gold sword, or bow) found.");
            }

            if (this.count >= 100) {
                this.sendMessage("No mystic item found, returning to FIGHTING phase.");
                this.handleWalkingMode(WeaselGrinder.WalkingMode.FIGHTING);
            }

            this.process4();
        } else {
            float[] floats = this.computeFloatArray(entity);
            float[] floats2 = this.computeFloatArray(floats, this.yawSmoothnessSetting.computeDouble() / 10.0, this.pitchSmoothnessSetting.computeDouble() / 10.0);
            float[] floats3 = this.computeFloatArray(floats2, new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch});
            this.mc.thePlayer.rotationYaw = floats3[0];
            this.mc.thePlayer.rotationPitch = floats3[1];
            this.process7();
            if (this.mc.thePlayer.getDistanceToEntity(entity) <= 1.0 && !this.mc.theWorld.loadedEntityList.contains(entity)) {
                this.sendMessage("Mystic item picked up! Returning to FIGHTING phase.");
                this.handleWalkingMode(WeaselGrinder.WalkingMode.FIGHTING);
            }

            if (this.debugSetting.isEnabled()) {
                ItemStack entityItem2 = entity.getEntityItem();
                String str = Item.getIdFromItem(entityItem2.getItem()) == 300 ? "Leather Armor" : (Item.getIdFromItem(entityItem2.getItem()) == 283 ? "Gold Sword" : "Bow");
                this.sendMessage(
                    "Debug: Pathfinding to mystic item: " + str + ", Distance: " + String.format("%.1f", this.mc.thePlayer.getDistanceToEntity(entity))
                );
            }
        }
    }

    private void process12() {
        Module module = Brutality.moduleManager.computeModule("Kill Aura");
        if (module != null && module.checkState44()) {
            try {
                Entity entity = (Entity)module.getClass().getField("target").get(module);
                if (entity instanceof EntityPlayer && !FreshHelper.matchesWorld3(this.mc.theWorld, (EntityPlayer)entity)) {
                    this.entityPlayer = (EntityPlayer)entity;
                    this.count10++;
                } else {
                    this.entityPlayer = null;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Failed to access Killaura target: " + e.getMessage());
                }

                this.entityPlayer = null;
            }
        } else {
            this.entityPlayer = null;
        }

        if (this.count10 >= 500) {
            this.count10 = 0;
            this.entityPlayer = null;
        }

        Random random = new Random();
        if (random.nextDouble() <= this.jumpChanceSetting.computeDouble() && this.mc.thePlayer.onGround) {
            this.mc.thePlayer.jump();
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: Jump triggered in FIGHTING phase.");
            }
        }

        if (random.nextDouble() <= this.strafeChanceSetting.computeDouble()) {
            if (random.nextDouble() <= 0.5) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindRight.getKeyCode(), true);
                new Thread(() -> {
                    try {
                        Thread.sleep((int)this.computeDouble(200.0, 700.0));
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindRight.getKeyCode(), false);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }).start();
            } else {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindLeft.getKeyCode(), true);
                new Thread(() -> {
                    try {
                        Thread.sleep((int)this.computeDouble(200.0, 700.0));
                        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindLeft.getKeyCode(), false);
                    } catch (InterruptedException e3) {
                        e3.printStackTrace();
                    }
                }).start();
            }
        }

        if (this.mc.thePlayer.isUsingItem()) {
            this.mc.thePlayer.stopUsingItem();
        } else {
            if (this.entityPlayer == null) {
                BlockPos pos = new BlockPos(0.5, this.mc.thePlayer.posY - 8.0, 0.5);
                double distanceSqToCenter = this.mc.thePlayer.getDistanceSqToCenter(pos);
                if (distanceSqToCenter > 400.0) {
                    BlockPos pos2 = new BlockPos(0.0, this.mc.thePlayer.posY - 8.0, 0.0);
                    float[] floats = this.computeFloatArray(
                        this.computeFloatArray(
                            this.computeFloatArray(pos2), this.yawSmoothnessSetting.computeDouble() / 10.0, this.pitchSmoothnessSetting.computeDouble() / 10.0
                        ),
                        new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
                    );
                    this.mc.thePlayer.rotationYaw = floats[0];
                    this.mc.thePlayer.rotationPitch = floats[1];
                    this.process7();
                    if (this.debugSetting.isEnabled()) {
                        String str = String.format("%.1f", Math.sqrt(distanceSqToCenter));
                        String str2 = String.format("%.1f", floats[0]);
                        String str3 = String.format("%.1f", floats[1]);
                        String str4 = str2;
                        String str5 = str;
                        this.sendMessage("Debug: No target, FAR from center (dist=" + str5 + "), returning to 0,0 - Yaw: " + str4 + ", Pitch: " + str3);
                    }
                } else if (!FreshHelper.matchesWorld4(this.mc.theWorld, this.mc.thePlayer)) {
                    float[] floats2 = this.computeFloatArray(
                        this.computeFloatArray(
                            this.computeFloatArray(pos), this.yawSmoothnessSetting.computeDouble() / 10.0, this.pitchSmoothnessSetting.computeDouble() / 10.0
                        ),
                        new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
                    );
                    this.mc.thePlayer.rotationYaw = floats2[0];
                    this.mc.thePlayer.rotationPitch = floats2[1];
                    this.process7();
                    if (this.debugSetting.isEnabled()) {
                        String str6 = String.format("%.1f", Math.sqrt(distanceSqToCenter));
                        String str7 = String.format("%.1f", floats2[0]);
                        String str8 = String.format("%.1f", floats2[1]);
                        String str9 = str7;
                        String str10 = str6;
                        this.sendMessage("Debug: No target, CLOSE to center (dist=" + str10 + "), moving to actualMid - Yaw: " + str9 + ", Pitch: " + str8);
                    }
                } else {
                    this.process4();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: No target, at actualMid, stopping.");
                    }
                }
            } else if (this.mc.thePlayer.getDistanceToEntity(this.entityPlayer) >= this.skipAimRangeSetting.computeDouble()) {
                float[] floats3;
                if (this.lockTargetSetting.isEnabled()) {
                    floats3 = this.computeFloatArray(this.entityPlayer);
                } else {
                    floats3 = this.computeFloatArray(this.entityPlayer);
                    float f = floats3[0];
                    f = MathHelper.wrapAngleTo180_float(f) + 180.0F;
                    float f2 = Math.round(f / 90.0F) * 90;
                    float f3 = MathHelper.wrapAngleTo180_float(f2 - 180.0F);
                    floats3 = new float[]{f3, 0.0F};
                    if (this.debugSetting.isEnabled()) {
                        String str11 = f3 == 0.0F ? "South" : (f3 == 90.0F ? "West" : (f3 == -180.0F ? "North" : "East"));
                        this.sendMessage("Debug: General direction targeting - Direction: " + str11 + ", Yaw: " + String.format("%.1f", f3) + ", Pitch: 0.0");
                    }
                }

                float[] floats4 = this.computeFloatArray(
                    floats3, this.yawSmoothnessSetting.computeDouble() / 10.0, this.pitchSmoothnessSetting.computeDouble() / 10.0
                );
                float[] floats5 = this.computeFloatArray(floats4, new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch});
                this.mc.thePlayer.rotationYaw = floats5[0];
                this.mc.thePlayer.rotationPitch = floats5[1];
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Final rotations - Yaw: " + String.format("%.1f", floats5[0]) + ", Pitch: " + String.format("%.1f", floats5[1]));
                }

                this.process7();
            }

            if (this.entityPlayer != null
                && this.mc.thePlayer.getDistanceToEntity(this.entityPlayer) <= this.attackReachSetting.computeDouble()
                && this.count % this.count8 == 0) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
                this.count8 = (int)this.computeDouble(this.sprintResetTicksSetting.computeDouble() - 20.0, this.sprintResetTicksSetting.computeDouble() + 40.0);
                new Thread(() -> {
                    try {
                        Thread.sleep(20L);
                        this.process7();
                    } catch (InterruptedException e4) {
                        e4.printStackTrace();
                    }
                }).start();
            }
        }
    }

    private void renderHUD() {
        new ScaledResolution(this.mc);
        this.process3();
        int n = (int)this.hudxSetting.computeDouble();
        int n2 = (int)this.hudySetting.computeDouble();
        String str = "§cWeaselGrinder";
        String str2 = "§fPhase: §7" + this.walkingMode.toString();
        String str3 = "§fLoop: §7" + this.count2;
        String str4 = "§fSession: §7" + this.computeString((int)((System.currentTimeMillis() - this.timestamp) / 1000L));
        String str5 = this.entityPlayer != null ? "§fTarget: §7" + this.entityPlayer.getDisplayName().getFormattedText() : null;
        String str6 = this.autoUberSetting.isEnabled() ? "§fUbers: §d" + this.count11 : null;
        computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str, n, n2, Color.RED.getRGB());
        n2 += 12;
        computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str2, n, n2, Color.RED.getRGB());
        n2 += 12;
        computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str3, n, n2, Color.RED.getRGB());
        int n3 = n2 + 12;
        computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str4, n, n3, Color.RED.getRGB());
        if (str5 != null) {
            n3 += 12;
            computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str5, n, n3, Color.RED.getRGB());
        }

        if (str6 != null) {
            n2 = n3 + 12;
            computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str6, n, n2, Color.RED.getRGB());
            if (!this.lookupMap.isEmpty()) {
                int n4 = n2 + 12;
                computeFontRenderer(WeaselGrinder.class).drawStringWithShadow("§fUberstreak Loot:", n, n4, Color.RED.getRGB());
                java.util.List<Map.Entry<String, Integer>> list = new ArrayList<>(this.lookupMap.entrySet());
                list.sort((entry, entry2) -> {
                    int n5 = ((Integer)entry2.getValue()).compareTo((Integer)entry.getValue());
                    return n5 != 0 ? n5 : ((String)entry.getKey()).compareTo((String)entry2.getKey());
                });

                for (Entry entry3 : ((Iterable<Entry>)(Object)(list))) {
                    n4 += 10;
                    String str7 = "§7  " + entry3.getValue() + "x §b" + (String)entry3.getKey();
                    computeFontRenderer(WeaselGrinder.class).drawStringWithShadow(str7, n, n4, Color.RED.getRGB());
                }
            }
        }
    }

    private void process10() {
        this.count3 = (int)this.computeDouble(640.0, 800.0);
        this.handleWalkingMode(WeaselGrinder.WalkingMode.WALKING);
        new Thread(
                () -> {
                    try {
                        Thread.sleep(this.count3 - 40);
                        this.mc.thePlayer.sendChatMessage("/spawn");
                        if (!FreshHelper.matchesWorld4(this.mc.theWorld, this.mc.thePlayer)) {
                            BlockPos pos = new BlockPos(0.5, this.mc.thePlayer.posY - 8.0, 0.5);
                            float[] floats = this.computeFloatArray(
                                this.computeFloatArray(this.computeFloatArray(pos), this.doubleValueArray[3], this.doubleValueArray[4]),
                                new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
                            );
                            this.mc.thePlayer.rotationYaw = floats[0];
                            if (this.mc.thePlayer.onGround) {
                                this.mc.thePlayer.rotationPitch = floats[1];
                            }

                            this.process7();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            )
            .start();
        if (this.debugSetting.isEnabled()) {
            this.sendMessage("Debug: Issued /spawn command.");
        }
    }

    private void process11() {
        if (this.count >= this.count9) {
            this.handleWalkingMode(WeaselGrinder.WalkingMode.FIGHTING);
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: Transitioning to FIGHTING phase from FALLING.");
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.mc.theWorld != null && !this.mc.thePlayer.isDead) {
                if (event instanceof EventUpdate) {
                    this.process();
                } else if (event instanceof EventRender2D && this.showHUDSetting.isEnabled()) {
                    this.renderHUD();
                } else if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S02PacketChat packet) {
                    String text = packet.getChatComponent().getFormattedText();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: S02PacketChat received. Raw message: \"" + text + "\"");
                    }

                    if (text.contains(this.mc.thePlayer.getDisplayName().getFormattedText())) {
                        this.count6++;
                        if (this.debugSetting.isEnabled()) {
                            this.sendMessage("You were mentioned in chat!");
                        }
                    }

                    if (text.contains("MYSTIC ITEM!") && !text.contains(":") && text.contains("dropped")) {
                        this.sendMessage("Detected MYSTIC ITEM! Switching to MYSTIC phase.");
                        this.handleWalkingMode(WeaselGrinder.WalkingMode.MYSTIC);
                    }

                    if (this.autoUberSetting.isEnabled() && text.contains("STREAK!")) {
                        try {
                            String str = text.replaceAll("§[0-9a-fk-or]", "");
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug AutoUber: Raw message: " + text);
                                this.sendMessage("Debug AutoUber: Clean message: " + str);
                            }

                            if (str.contains(" kills by ")) {
                                String name = this.mc.thePlayer.getName();
                                if (this.debugSetting.isEnabled()) {
                                    this.sendMessage("Debug AutoUber: Streak message detected: " + str);
                                    this.sendMessage("Debug AutoUber: Current player: '" + name + "'");
                                }

                                if (str.contains(name)) {
                                    Pattern pattern = Pattern.compile("STREAK! of (\\d+) kills");
                                    Matcher matcher = pattern.matcher(str);
                                    if (matcher.find()) {
                                        int n = Integer.parseInt(matcher.group(1));
                                        if (this.debugSetting.isEnabled()) {
                                            this.sendMessage("Debug AutoUber: Parsed streak kills: " + n);
                                        }

                                        if (n >= 410) {
                                            this.sendMessage("AutoUber: Streak of " + n + " kills reached, executing /oof");
                                            this.mc.thePlayer.sendChatMessage("/oof");
                                            this.count11++;
                                            this.flag5 = true;
                                        }
                                    } else if (this.debugSetting.isEnabled()) {
                                        this.sendMessage("Debug AutoUber: Failed to parse streak number from: " + str);
                                    }
                                } else if (this.debugSetting.isEnabled()) {
                                    this.sendMessage("Debug AutoUber: Streak message is for another player, ignoring");
                                }
                            }
                        } catch (Exception e) {
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug AutoUber: Error processing streak message: " + e.getMessage());
                            }
                        }
                    }

                    if (this.autoUberSetting.isEnabled()) {
                        String str2 = text.replaceAll("§[0-9a-fk-or]", "");
                        if (this.flag5) {
                            this.sendMessage("§7[AutoUber Debug] §fWaiting for Contains... Message: §e" + str2);
                        }

                        if (str2.contains("UBERDROP!") && str2.contains("obtained an Uberdrop")) {
                            this.flag5 = true;
                            this.sendMessage("§d[AutoUber] §fUberdrop message detected! Scanning inventory...");
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug AutoUber: UBERDROP message detected, will scan inventory");
                            }

                            this.process8();
                            this.flag5 = false;
                        }

                        if (str2.contains("Uberdrop") && str2.contains("[B]")) {
                            this.flag5 = true;
                            this.sendMessage("§d[AutoUber] §fUberdrop detected! Waiting for item info...");
                            if (this.debugSetting.isEnabled()) {
                                this.sendMessage("Debug AutoUber: Uberdrop detected, waiting for Contains line...");
                            }
                        }

                        if (this.flag5 && str2.toLowerCase().contains("contains")) {
                            try {
                                this.sendMessage("§d[AutoUber] §fContains line detected: §e" + str2);
                                Pattern[] patterns = new Pattern[]{
                                    Pattern.compile("Contains:\\s*(\\d+)\\s+(.+?)\\s*$"),
                                    Pattern.compile("-\\s*Contains:\\s*(\\d+)\\s+(.+?)\\s*$"),
                                    Pattern.compile("[Cc]ontains[:\\s]+(\\d+)\\s+(.+?)\\s*$"),
                                    Pattern.compile("[Cc]ontains[:\\s]+(.+?)\\s*$")
                                };
                                boolean flag = false;

                                for (int i = 0; i < patterns.length; i++) {
                                    Pattern pattern2 = patterns[i];
                                    Matcher matcher2 = pattern2.matcher(str2);
                                    if (matcher2.find()) {
                                        int n2;
                                        String str3;
                                        if (i < 3) {
                                            n2 = Integer.parseInt(matcher2.group(1));
                                            str3 = matcher2.group(2).trim();
                                        } else {
                                            n2 = 1;
                                            str3 = matcher2.group(1).trim();
                                        }

                                        String str4 = str3.replaceAll("§[0-9a-fk-or]", "").trim();
                                        this.lookupMap.put(str4, this.lookupMap.getOrDefault(str4, 0) + n2);
                                        this.sendMessage("§d[AutoUber] §fTracked: §a" + n2 + "x §b" + str4);
                                        this.sendMessage("§d[AutoUber] §fTotal unique items: §e" + this.lookupMap.size());
                                        if (this.debugSetting.isEnabled()) {
                                            this.sendMessage("Debug AutoUber: Successfully tracked uberdrop - " + n2 + "x " + str4);
                                            this.sendMessage("Debug AutoUber: Used pattern " + i + ": " + pattern2.pattern());
                                        }

                                        this.flag5 = false;
                                        flag = true;
                                        break;
                                    }
                                }

                                if (!flag) {
                                    this.sendMessage("§c[AutoUber] §fFailed to parse Contains line!");
                                    this.sendMessage("§c[AutoUber] §fPlease report this message: §e" + str2);
                                }
                            } catch (Exception e2) {
                                this.sendMessage("§c[AutoUber] §fError: " + e2.getMessage());
                                if (this.debugSetting.isEnabled()) {
                                    e2.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process4();
        this.reset();
    }

    private float[] computeFloatArray(float[] floats, double d, double d2) {
        float f = MathHelper.wrapAngleTo180_float(floats[0] - this.mc.thePlayer.rotationYaw);
        float f2 = MathHelper.wrapAngleTo180_float(floats[1] - this.mc.thePlayer.rotationPitch);
        double d3 = (1.0 - d) * (1.0 + 0.1 * Math.abs(f) / 180.0);
        double d4 = (1.0 - d2) * (1.0 + 0.1 * Math.abs(f2) / 180.0);
        return new float[]{(float)(this.mc.thePlayer.rotationYaw + f * d3), (float)(this.mc.thePlayer.rotationPitch + f2 * d4)};
    }

    private void process5() {
        if (this.mc.thePlayer != null
            && this.mc.theWorld != null
            && this.doubleValueArray != null
            && this.jumpChanceSetting != null
            && this.doubleValueArray.length >= 5) {
            boolean flag = FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer);
            BlockPos pos = new BlockPos(0.5, this.mc.thePlayer.posY - 8.0, 0.5);
            double distanceSqToCenter = this.mc.thePlayer.getDistanceSqToCenter(pos);
            if (this.count % 20 == 0 && this.debugSetting.isEnabled()) {
                String str = String.format("%.1f", this.mc.thePlayer.posY);
                String str2 = String.format("%.1f", distanceSqToCenter);
                String str3 = this.center != null ? this.center.toString() : "null";
                String str4 = this.mc.currentScreen != null ? this.mc.currentScreen.getClass().getSimpleName() : "None";
                boolean flag2 = this.checkState();
                String str5 = str3;
                String str6 = str2;
                String str7 = str;
                this.sendMessage(
                    "Debug: Walking, AtSpawn="
                        + flag
                        + ", PosY="
                        + str7
                        + ", DistToMid="
                        + str6
                        + ", Center="
                        + str5
                        + ", IsWalking="
                        + flag2
                        + ", CurrentScreen="
                        + str4
                );
            }

            if (flag) {
                if (distanceSqToCenter > 1.0) {
                    float[] floats = this.computeFloatArray(
                        this.computeFloatArray(this.computeFloatArray(pos), this.doubleValueArray[3], this.doubleValueArray[4]),
                        new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
                    );
                    this.mc.thePlayer.rotationYaw = floats[0];
                    if (this.mc.thePlayer.onGround) {
                        this.mc.thePlayer.rotationPitch = floats[1];
                    }

                    this.process7();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage(
                            "Debug: In spawn, moving to actualMid - Yaw: " + String.format("%.1f", floats[0]) + ", Pitch: " + String.format("%.1f", floats[1])
                        );
                    }
                } else {
                    this.process4();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Reached actualMid in spawn, stopping movement.");
                    }
                }
            } else {
                if (this.center == null || this.count == 1) {
                    this.center = new BlockPos(this.computeDouble(-3.0, 3.0), this.mc.thePlayer.posY - 8.0, this.computeDouble(-3.0, 3.0));
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Left spawn or null center, new center=" + this.center.toString());
                    }
                }

                double distanceSqToCenter2 = this.mc.thePlayer.getDistanceSqToCenter(this.center);
                if (this.entityPlayer != null) {
                    this.handleWalkingMode(WeaselGrinder.WalkingMode.FIGHTING);
                    this.process7();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Target found, transitioning to FIGHTING phase.");
                    }

                    return;
                }

                if (distanceSqToCenter2 > this.distanceSqSetting.computeDouble()) {
                    float[] floats2 = this.computeFloatArray(
                        this.computeFloatArray(this.computeFloatArray(this.center), this.doubleValueArray[3], this.doubleValueArray[4]),
                        new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch}
                    );
                    this.mc.thePlayer.rotationYaw = floats2[0];
                    if (this.mc.thePlayer.onGround) {
                        this.mc.thePlayer.rotationPitch = floats2[1];
                    }

                    this.process7();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage(
                            "Debug: No target, moving to center - Yaw: " + String.format("%.1f", floats2[0]) + ", Pitch: " + String.format("%.1f", floats2[1])
                        );
                    }
                } else {
                    this.process4();
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Reached center, stopping movement.");
                    }
                }

                if (this.timerUtil2.matchesLong5(2500L)) {
                    this.center = new BlockPos(this.computeDouble(-3.0, 3.0), this.mc.thePlayer.posY - 8.0, this.computeDouble(-3.0, 3.0));
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Player stuck, resetting center to " + this.center.toString());
                    }
                }
            }

            Random random = new Random();
            if (random.nextDouble() <= this.jumpChanceSetting.computeDouble() && this.mc.thePlayer.onGround && !flag) {
                this.mc.thePlayer.jump();
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Random jump triggered.");
                }
            }

            if (this.mc.thePlayer.fallDistance > 10.0F) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), false);
                this.handleWalkingMode(WeaselGrinder.WalkingMode.FALLING);
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Transitioning to FALLING phase due to fall distance.");
                }
            } else {
                if (this.count > 1200 && !flag) {
                    this.process4();
                    this.handleWalkingMode(WeaselGrinder.WalkingMode.PANIC);
                    if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Transitioning to PANIC phase due to prolonged distance from center.");
                    }
                }
            }
        } else {
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: Walking aborted due to null checks.");
            }

            this.process4();
        }
    }

    private void uv() {
        if (this.checkState()) {
            this.process4();
            if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: stopWalking() called in WAITING phase.");
            }
        }

        if (this.flag && this.count3 < this.count) {
            this.mc.thePlayer.sendChatMessage("/play pit");
            this.flag = false;
            this.count3 = this.count + 200;
        } else {
            if (this.count3 < this.count && this.walkingMode == WeaselGrinder.WalkingMode.WAITING && this.count3 != Integer.MAX_VALUE) {
                this.handleWalkingMode(WeaselGrinder.WalkingMode.WALKING);
                this.process7();
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Transitioning to WALKING phase from WAITING.");
                }
            }
        }
    }

    private void process8() {
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            try {
                for (int i = 0; i < this.mc.thePlayer.inventory.getSizeInventory(); i++) {
                    ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() != null && stack.hasTagCompound()) {
                        NBTTagCompound tagCompound = stack.getTagCompound();
                        if (tagCompound.hasKey("display")) {
                            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                            if (compoundTag.hasKey("Name")) {
                                String str = compoundTag.getString("Name");
                                if (!str.contains("Uberdrop")) {
                                    continue;
                                }
                            }

                            String str2 = null;
                            if (tagCompound.hasKey("pit-item-uuid")) {
                                str2 = tagCompound.getString("pit-item-uuid");
                            }

                            if (compoundTag.hasKey("Lore")) {
                                NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                                for (int j = 0; j < tagList.tagCount(); j++) {
                                    String stringTagAt = tagList.getStringTagAt(j);
                                    String str3 = stringTagAt.replaceAll("§[0-9a-fk-or]", "");
                                    if (str3.toLowerCase().contains("contains")) {
                                        Pattern[] patterns = new Pattern[]{
                                            Pattern.compile("[Cc]ontains[:\\s]+(\\d+)\\s+(.+?)\\s*$"), Pattern.compile("[Cc]ontains[:\\s]+(.+?)\\s*$")
                                        };
                                        boolean flag = false;

                                        for (int k = 0; k < patterns.length; k++) {
                                            Pattern pattern = patterns[k];
                                            Matcher matcher = pattern.matcher(str3);
                                            if (matcher.find()) {
                                                int n;
                                                String str4;
                                                if (k == 0) {
                                                    n = Integer.parseInt(matcher.group(1));
                                                    str4 = matcher.group(2).trim();
                                                } else {
                                                    n = 1;
                                                    str4 = matcher.group(1).trim();
                                                }

                                                String str5 = str2 != null ? str2 : "slot_" + i + "_" + str4 + "_" + n;
                                                if (!this.itemSet.contains(str5)) {
                                                    this.lookupMap.put(str4, this.lookupMap.getOrDefault(str4, 0) + n);
                                                    this.itemSet.add(str5);
                                                    this.sendMessage("§d[AutoUber Scan] §fFound uberdrop: §a" + n + "x §b" + str4);
                                                    if (this.debugSetting.isEnabled()) {
                                                        this.sendMessage("§7[AutoUber Scan] §fSlot " + i + ", UUID: " + (str2 != null ? str2 : "none"));
                                                    }
                                                }

                                                flag = true;
                                                break;
                                            }
                                        }

                                        if (!flag && this.debugSetting.isEnabled()) {
                                            this.sendMessage("§c[AutoUber Scan] §fFailed to parse Contains line: §e" + str3);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("§c[AutoUber Scan] §fError scanning inventory: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    private void reset() {
        this.walkingMode = WeaselGrinder.WalkingMode.WALKING;
        this.center = new BlockPos(0.0, this.mc.thePlayer.posY - 8.0, 0.0);
        this.doubleValueArray = new double[]{
            new Random().nextInt(15), new Random().nextInt(15), new Random().nextInt(15), this.computeDouble(0.5, 0.95), this.computeDouble(0.85, 0.95)
        };
        this.timestamp = System.currentTimeMillis();
        this.count9 = (int)this.computeDouble(5.0, 30.0);
        this.count5 = 95;
        this.flag = false;
        this.count8 = (int)this.computeDouble(this.sprintResetTicksSetting.computeDouble() - 20.0, this.sprintResetTicksSetting.computeDouble() + 40.0);
        this.count7 = (int)this.sessionStopMinSetting.computeDouble();
        this.count = 0;
        this.count2 = 0;
        this.count3 = 0;
        this.count6 = 0;
        this.entityPlayer = null;
        this.count10 = 0;
        this.flag2 = true;
        this.dragging = false;
        this.flag3 = false;
        this.flag4 = true;
        this.timerUtil.reset();
        this.timerUtil2.reset();
        this.count11 = 0;
        this.lookupMap.clear();
        this.itemSet.clear();
        this.flag5 = false;
        this.timestamp2 = 0L;
        this.process7();
        boolean enabled = this.debugSetting.isEnabled();
        boolean enabled2 = this.autoXCarrySetting.isEnabled();
        boolean enabled3 = this.autoBuySetting.isEnabled();
        boolean enabled4 = this.autoUberSetting.isEnabled();
        boolean flag = enabled3;
        boolean flag2 = enabled2;
        boolean flag3 = enabled;
        this.sendMessage("WeaselGrinder enabled. Debug: " + flag3 + ", AutoXCarry: " + flag2 + ", AutoBuy: " + flag + ", AutoUber: " + enabled4);
    }

    private boolean matchesInt(int n, int n2, int n3, int n4, int n5, int n6) {
        return n >= n3 && n <= n3 + n5 && n2 >= n4 && n2 <= n4 + n6;
    }

    private float[] computeFloatArray(Entity entity) {
        double d = entity.posX - this.mc.thePlayer.posX;
        double d2;
        if (entity instanceof EntityPlayer player) {
            d2 = player.posY + player.getEyeHeight() * 0.9 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        } else {
            d2 = (entity.getEntityBoundingBox().minY + entity.getEntityBoundingBox().maxY) / 2.0 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        }

        double d3 = entity.posZ - this.mc.thePlayer.posZ;
        double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
        float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
        float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
        float f3 = f2 + 11.0F;
        return new float[]{f, f3};
    }

    public WeaselGrinder() {
        super("WeaselGrinder", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.aimReachSetting,
                this.attackReachSetting,
                this.yawSmoothnessSetting,
                this.pitchSmoothnessSetting,
                this.jumpChanceSetting,
                this.strafeChanceSetting,
                this.sprintResetTicksSetting,
                this.waitChanceSetting,
                this.sessionStopMinSetting,
                this.distanceSqSetting,
                this.switchTargetRangeSetting,
                this.skipAimRangeSetting,
                this.quitOnPanicSetting,
                this.hudxSetting,
                this.hudySetting,
                this.showHUDSetting,
                this.debugSetting,
                this.lockTargetSetting,
                this.autoXCarrySetting,
                this.xCarryHealthSetting,
                this.autoBuySetting,
                this.autoUberSetting,
                this.autoSwordSetting,
                this.swordSlotSetting
            }
        );
    }

    private double computeDouble(double d, double d2) {
        Random random = new Random();
        return d + (d2 - d) * random.nextDouble();
    }

    public static enum WalkingMode {
        WALKING,
        FALLING,
        FIGHTING,
        WAITING,
        PANIC,
        MYSTIC;

        private static WeaselGrinder.WalkingMode[] computeWalkingModeArray() {
            return new WeaselGrinder.WalkingMode[]{WALKING, FALLING, FIGHTING, WAITING, PANIC, MYSTIC};
        }
    }
}
