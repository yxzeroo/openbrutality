package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoBuy extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final TimerUtil timerUtil = new TimerUtil();
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);
    private final KeybindListSetting obsidian64Setting = new KeybindListSetting(
        "Shop",
        this,
        new BooleanSetting("Diamond Sword", this, false),
        new BooleanSetting("Diamond ChestPlate", this, false),
        new BooleanSetting("Diamond Leggings", this, false),
        new BooleanSetting("Diamond Boots", this, false),
        new BooleanSetting("FirstAid Egg", this, false),
        new BooleanSetting("Combat Spade", this, false),
        new BooleanSetting("Iron Pack", this, false),
        new BooleanSetting("Obsidian (8)", this, false),
        new BooleanSetting("Obsidian (64)", this, false)
    );
    private AutoBuy.WalkingMode walkingMode = AutoBuy.WalkingMode.WALKING;
    private BlockPos blockPos = new BlockPos(3, 95, 11);
    private BlockPos blockPos2 = new BlockPos(2, 95, 12);
    private BlockPos blockPos3 = new BlockPos(3, 95, 6);
    private List<BlockPos> entries = new ArrayList<>();
    private int count = 0;
    private int count2 = 0;
    private boolean flag = false;
    private int count3 = 0;
    private EntityVillager entityVillager;
    private List<Integer> entries2 = new ArrayList<>();
    private boolean flag2 = false;
    private int count4 = 0;
    private static final long timestamp = -950161720674877410L;

    @Override
    public void onDisable() {
        super.onDisable();
        this.reset();
    }

    private void handleWalkingMode(AutoBuy.WalkingMode walkingMode) {
        this.walkingMode = walkingMode;
        this.count3 = 0;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.reset();
        this.process10();
    }

    public AutoBuy() {
        super("AutoBuy", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.debugSetting, this.obsidian64Setting});
        this.process();
    }

    private float[] computeFloatArray(float[] floats, float f, float f2) {
        float f3 = MathHelper.wrapAngleTo180_float(floats[0] - this.mc.thePlayer.rotationYaw);
        float f4 = MathHelper.wrapAngleTo180_float(floats[1] - this.mc.thePlayer.rotationPitch);
        double d = f;
        double d2 = f2;
        return new float[]{this.mc.thePlayer.rotationYaw + (float)(f3 * d), this.mc.thePlayer.rotationPitch + (float)(f4 * d2)};
    }

    private boolean matchesBlockPos(BlockPos pos, float f) {
        float[] floats = this.computeFloatArray(pos);
        float f2 = Math.abs(MathHelper.wrapAngleTo180_float(floats[0] - this.mc.thePlayer.rotationYaw));
        float f3 = Math.abs(floats[1] - this.mc.thePlayer.rotationPitch);
        boolean flag = f2 <= f && f3 <= f;
        if (this.debugSetting.isEnabled() && !flag && this.count3 % 20 == 0) {
            String str = String.format("%.1f", f2);
            String str2 = String.format("%.1f", f3);
            String str3 = str;
            this.sendMessage("Debug: isLookingAt failed. YawDiff=" + str3 + ", PitchDiff=" + str2 + ", Tolerance=" + f);
        }

        return flag;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && this.mc.thePlayer != null && this.mc.theWorld != null && !this.mc.thePlayer.isDead) {
                if (event instanceof EventUpdate) {
                    this.process4();
                }
            }
        }
    }

    private boolean matchesBlockPos(BlockPos pos, double d) {
        return this.mc.thePlayer.getDistanceSq(pos) <= d * d;
    }

    private void process6() {
        if (!this.matchesBlockPos(this.blockPos, 1.0)) {
            this.handleBlockPos(this.blockPos);
            if (this.debugSetting.isEnabled() && this.count3 % 20 == 0) {
                this.sendMessage(
                    "Debug: Pathfinding to "
                        + this.blockPos.toString()
                        + ", Distance: "
                        + String.format("%.1f", this.mc.thePlayer.getDistanceSq(this.blockPos))
                );
            }
        } else {
            this.process3();
            this.handleWalkingMode(AutoBuy.WalkingMode.INTERACTING);
            this.sendMessage("Reached target location, switching to INTERACTING phase");
        }
    }

    private boolean matchesString(String str, String str2) {
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            String str3 = str.toLowerCase();
            byte b;
            switch (str3) {
                case "helmet":
                    b = 3;
                    break;
                case "chestplate":
                    b = 2;
                    break;
                case "leggings":
                    b = 1;
                    break;
                case "boots":
                    b = 0;
                    break;
                default:
                    return false;
            }

            ItemStack stack = this.mc.thePlayer.inventory.armorInventory[b];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                String str4 = Item.itemRegistry.getNameForObject(stack.getItem()).toString();
                boolean flag = str4.contains(str2) && str4.contains(str.toLowerCase());
                if (this.debugSetting.isEnabled() && flag) {
                    this.sendMessage("Debug: Wearing " + str2 + " " + str + ": " + str4);
                }

                return flag;
            }
            return false;
        }
        return false;
    }

    private void process5() {
        if (!this.matchesBlockPos(this.blockPos3, 1.0)) {
            this.handleBlockPos(this.blockPos3);
            if (this.debugSetting.isEnabled() && this.count3 % 20 == 0) {
                this.sendMessage(
                    "Debug: Pathfinding to carpet at "
                        + this.blockPos3.toString()
                        + ", Distance: "
                        + String.format("%.1f", this.mc.thePlayer.getDistanceSq(this.blockPos3))
                );
            }
        } else {
            this.sendMessage("Reached carpet, walking forward until Y value changes");
            this.process7();
            this.handleWalkingMode(AutoBuy.WalkingMode.FALLING);
        }
    }

    private void process3() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindJump.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindRight.getKeyCode(), false);
    }

    private boolean matchesString(String str) {
        if (this.mc.thePlayer == null) {
            return false;
        }
        switch (str) {
            case "Diamond Sword":
                return this.matchesString2("diamond_sword");
            case "Diamond ChestPlate":
                return this.matchesString("chestplate", "diamond") || this.matchesString2("diamond_chestplate");
            case "Diamond Leggings":
                return this.matchesString("leggings", "diamond") || this.matchesString2("diamond_leggings");
            case "Diamond Boots":
                return this.matchesString("boots", "diamond") || this.matchesString2("diamond_boots");
            case "FirstAid Egg":
                return this.matchesString2("spawn_egg");
            case "Combat Spade":
                return this.matchesString2("diamond_shovel");
            case "Iron Pack":
                return this.matchesString("chestplate", "iron") && this.matchesString("leggings", "iron") && this.matchesString("boots", "iron");
            case "Obsidian (8)":
            case "Obsidian (64)":
                return false;
            default:
                return false;
        }
    }

    private void process4() {
        this.count3++;
        if (!FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer) && this.walkingMode == AutoBuy.WalkingMode.WALKING) {
            this.process3();
        } else {
            switch (this.walkingMode) {
                case WALKING:
                    this.process6();
                    break;
                case INTERACTING:
                    this.interacting();
                    break;
                case CARPET:
                    this.process5();
                    break;
                case FALLING:
                    this.process8();
            }
        }
    }

    private void reset() {
        this.walkingMode = AutoBuy.WalkingMode.WALKING;
        this.count = 0;
        this.count2 = 0;
        this.flag = false;
        this.count3 = 0;
        this.entityVillager = null;
        this.entries2.clear();
        this.timerUtil.reset();
        this.flag2 = false;
        this.count4 = 0;
        this.sendMessage("AutoBuy enabled. Debug: " + this.debugSetting.isEnabled());
    }

    private void sendMessage(String str) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception e) {
            System.out.println("[AutoBuy] Failed to send chat message: " + str + " | Error: " + e.getMessage());
        }

        System.out.println("[AutoBuy] " + str);
    }

    private void handleBlockPos(BlockPos pos) {
        double distanceSq = this.mc.thePlayer.getDistanceSq(pos);
        if (distanceSq < 2.0) {
            float[] floats = this.computeFloatArray(pos);
            float[] floats2 = this.computeFloatArray(floats, 0.7F, 0.9F);
            float[] floats3 = this.computeFloatArray(floats2, new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch});
            this.mc.thePlayer.rotationYaw = floats3[0];
            this.mc.thePlayer.rotationPitch = floats3[1];
            this.process7();
        } else {
            double MAX_VALUE = Double.MAX_VALUE;
            BlockPos pos2 = null;

            for (BlockPos pos3 : this.entries) {
                double distanceSq2 = this.mc.thePlayer.getDistanceSq(pos3);
                if (distanceSq2 < MAX_VALUE) {
                    MAX_VALUE = distanceSq2;
                    pos2 = pos3;
                }
            }

            if (pos2 != null && MAX_VALUE < 4.0 && distanceSq > 4.0) {
                double d = this.mc.thePlayer.posX - pos2.getX();
                double d2 = this.mc.thePlayer.posZ - pos2.getZ();
                float f = (float)Math.toDegrees(Math.atan2(d2, d)) - 90.0F;
                this.mc.thePlayer.rotationYaw = f;
                if (this.debugSetting.isEnabled() && this.count3 % 20 == 0) {
                    this.sendMessage("Debug: Avoiding pit location " + pos2.toString());
                }
            } else {
                float[] floats4 = this.computeFloatArray(pos);
                float[] floats5 = this.computeFloatArray(floats4, 0.7F, 0.9F);
                float[] floats6 = this.computeFloatArray(floats5, new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch});
                this.mc.thePlayer.rotationYaw = floats6[0];
                this.mc.thePlayer.rotationPitch = floats6[1];
            }

            this.process7();
        }
    }

    private void process() {
        String[] parts = new String[]{
            "0,8",
            "1,8",
            "2,8",
            "3,8",
            "3,7",
            "4,7",
            "5,7",
            "5,6",
            "6,6",
            "6,5",
            "7,5",
            "7,4",
            "7,3",
            "8,3",
            "8,2",
            "8,1",
            "8,0",
            "8,-1",
            "8,-2",
            "7,-2",
            "7,-3",
            "7,-4",
            "6,-4",
            "6,-5",
            "5,-5",
            "5,-6",
            "4,-6",
            "3,-6",
            "3,-7",
            "2,-7",
            "1,-7",
            "0,-7",
            "-1,-7",
            "-2,-7",
            "-2,-6",
            "-3,-6",
            "-4,-6",
            "-4,-5",
            "-5,-5",
            "-5,-4",
            "-6,-4",
            "-6,-3",
            "-6,-2",
            "-7,-2",
            "-7,-1",
            "-7,0",
            "-7,1",
            "-7,2",
            "-7,3",
            "-6,3",
            "-6,4",
            "-6,5",
            "-5,-5",
            "-5,6",
            "-4,6",
            "-4,7",
            "-3,7",
            "-2,7",
            "-2,8",
            "-1,8",
            "-0,8"
        };

        for (String str : parts) {
            String[] parts2 = str.split(",");
            int n = Integer.parseInt(parts2[0]);
            int n2 = Integer.parseInt(parts2[1]);
            this.entries.add(new BlockPos(n, 95, n2));
        }
    }

    private void process10() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Diamond Sword", 11);
        map.put("Diamond ChestPlate", 14);
        map.put("Diamond Leggings", 31);
        map.put("Diamond Boots", 15);
        map.put("FirstAid Egg", 38);
        map.put("Combat Spade", 32);
        map.put("Iron Pack", 29);
        map.put("Obsidian (8)", 12);
        map.put("Obsidian (64)", 30);
        List<String> list = new ArrayList<>();

        for (int i = 0; i < this.obsidian64Setting.getEntries().size(); i++) {
            if (this.obsidian64Setting.getEntries().get(i) instanceof BooleanSetting) {
                BooleanSetting boolSetting = (BooleanSetting)this.obsidian64Setting.getEntries().get(i);
                if (boolSetting.isEnabled()) {
                    String name = boolSetting.getName();
                    if (!this.matchesString(name)) {
                        list.add(name);
                    } else if (this.debugSetting.isEnabled()) {
                        this.sendMessage("Debug: Already have " + name + ", skipping purchase");
                    }
                }
            }
        }

        this.entries2 = list.stream().map(map::get).collect(Collectors.toList());
        if (this.debugSetting.isEnabled()) {
            this.sendMessage("Debug: Items to buy: " + list);
            this.sendMessage("Debug: Slots to click: " + this.entries2);
        }

        if (this.entries2.isEmpty()) {
            this.sendMessage("No items to buy (already have all selected items). Disabling AutoBuy.");
            this.process28();
        }
    }

    private void process8() {
        this.process7();
        if (!FreshHelper.matchesWorld3(this.mc.theWorld, this.mc.thePlayer)) {
            this.sendMessage("Left spawn area, disabling AutoBuy");
            this.process28();
        }
    }

    private float[] computeFloatArray(BlockPos pos) {
        double d = pos.getX() + 0.5 - this.mc.thePlayer.posX;
        double d2 = pos.getY() + 0.5 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        double d3 = pos.getZ() + 0.5 - this.mc.thePlayer.posZ;
        double dist = Math.sqrt(d * d + d3 * d3);
        float f = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F;
        float f2 = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
        return new float[]{f, f2};
    }

    private void handleInt(int n) {
        try {
            if (this.mc.thePlayer.openContainer != null) {
                this.mc.playerController.windowClick(this.mc.thePlayer.openContainer.windowId, n, 0, 0, this.mc.thePlayer);
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Clicked slot " + n + " with windowID=" + this.mc.thePlayer.openContainer.windowId);
                }
            } else if (this.debugSetting.isEnabled()) {
                this.sendMessage("Debug: No open container, cannot click slot " + n);
            }
        } catch (Exception e) {
            this.sendMessage("Error clicking slot " + n + ": " + e.getMessage());
        }
    }

    private float[] computeFloatArray(Entity entity) {
        double d = entity.posX - this.mc.thePlayer.posX;
        double d2 = entity.posY + entity.getEyeHeight() * 0.9 - (this.mc.thePlayer.posY + this.mc.thePlayer.getEyeHeight());
        double d3 = entity.posZ - this.mc.thePlayer.posZ;
        double d4 = MathHelper.sqrt_double(d * d + d3 * d3);
        float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
        float f2 = (float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI));
        return new float[]{f, f2};
    }

    private void interacting() {
        if (!this.flag) {
            if (this.entityVillager == null || !this.mc.theWorld.loadedEntityList.contains(this.entityVillager)) {
                this.entityVillager = this.mc
                    .theWorld
                    .loadedEntityList
                    .stream()
                    .filter(entity -> entity instanceof EntityVillager)
                    .map(entity2 -> (EntityVillager)entity2)
                    .filter(entity3 -> this.mc.thePlayer.getDistanceToEntity(entity3) <= 4.0F)
                    .filter(entity4 -> this.mc.thePlayer.canEntityBeSeen(entity4))
                    .min(Comparator.comparingDouble(entity5 -> this.mc.thePlayer.getDistanceToEntity(entity5)))
                    .orElse(null);
                if (this.entityVillager == null) {
                    this.sendMessage("No suitable villager found within range, retrying...");
                    return;
                }

                if (this.debugSetting.isEnabled()) {
                    this.sendMessage(
                        "Debug: Locked onto villager at "
                            + this.entityVillager.getPosition().toString()
                            + ", Distance: "
                            + String.format("%.1f", this.mc.thePlayer.getDistanceToEntity(this.entityVillager))
                    );
                }
            }

            float[] floats = this.computeFloatArray(this.entityVillager);
            float[] floats2 = this.computeFloatArray(floats, 0.3F, 0.3F);
            float[] floats3 = this.computeFloatArray(floats2, new float[]{this.mc.thePlayer.rotationYaw, this.mc.thePlayer.rotationPitch});
            this.mc.thePlayer.rotationYaw = floats3[0];
            this.mc.thePlayer.rotationPitch = floats3[1];
            if (this.debugSetting.isEnabled() && this.count3 % 20 == 0) {
                float[] floats4 = this.computeFloatArray(this.entityVillager.getPosition());
                float f = Math.abs(MathHelper.wrapAngleTo180_float(floats4[0] - this.mc.thePlayer.rotationYaw));
                float f2 = Math.abs(floats4[1] - this.mc.thePlayer.rotationPitch);
                String str = String.format("%.1f", f);
                String str2 = String.format("%.1f", f2);
                long l = this.timerUtil.computeLong();
                String str3 = str2;
                String str4 = str;
                this.sendMessage("Debug: Looking at villager. YawDiff=" + str4 + ", PitchDiff=" + str3 + ", Timer=" + l);
            }

            if (this.count3 > 20 && this.matchesBlockPos(this.entityVillager.getPosition(), 40.0F)) {
                if (this.timerUtil.matchesLong5(750L)) {
                    try {
                        this.mc.playerController.interactWithEntitySendPacket(this.mc.thePlayer, this.entityVillager);
                        this.flag = true;
                        this.count = 0;
                        this.count2 = (int)timestamp;
                        this.timerUtil.reset();
                        this.sendMessage("Right-clicked villager to open shop menu");
                        if (this.debugSetting.isEnabled()) {
                            this.sendMessage("Debug: Sent interaction packet for villager at " + this.entityVillager.getPosition().toString());
                        }
                    } catch (Exception e) {
                        this.sendMessage("Error interacting with villager: " + e.getMessage());
                        this.entityVillager = null;
                        this.flag = false;
                        this.timerUtil.reset();
                    }
                } else if (this.debugSetting.isEnabled() && this.count3 % 20 == 0) {
                    this.sendMessage("Debug: Waiting for click timer. Elapsed=" + this.timerUtil.computeLong());
                }
            } else if (this.count3 > 100) {
                this.sendMessage("Failed to look at villager, resetting villager target");
                this.entityVillager = null;
                this.count3 = 0;
            }
        } else {
            if (this.count2 > 0) {
                this.count2--;
                if (this.debugSetting.isEnabled() && this.count2 % 5 == 0) {
                    this.sendMessage("Debug: Waiting for chest click delay. Remaining=" + this.count2);
                }

                return;
            }

            if (this.mc.currentScreen == null) {
                this.count4++;
                if (this.count4 >= 60) {
                    this.sendMessage("Chest GUI not open after delay, resetting interaction");
                    this.flag = false;
                    this.count = 0;
                    this.entityVillager = null;
                    this.timerUtil.reset();
                    this.count4 = 0;
                    this.count3 = 0;
                }

                return;
            }

            this.count4 = 0;
            if (this.entries2.isEmpty()) {
                this.sendMessage("No items to purchase. Disabling AutoBuy.");
                this.process28();
                return;
            }

            if (this.mc.thePlayer.openContainer == null) {
                if (this.debugSetting.isEnabled()) {
                    this.sendMessage("Debug: Container is null, waiting...");
                }

                return;
            }

            if (!this.flag2 && this.debugSetting.isEnabled()) {
                this.sendMessage(
                    "Debug: Container windowId="
                        + this.mc.thePlayer.openContainer.windowId
                        + ", Type="
                        + this.mc.thePlayer.openContainer.getClass().getSimpleName()
                );
                this.flag2 = true;
            }

            if (this.timerUtil.matchesLong5(600L)) {
                if (this.count < this.entries2.size()) {
                    int n = this.entries2.get(this.count);
                    this.handleInt(n);
                    int n2 = this.count + 1;
                    int size = this.entries2.size();
                    int n3 = n2;
                    this.sendMessage("Clicked slot " + n + " (" + n3 + "/" + size + ")");
                    this.count++;
                    this.timerUtil.reset();
                } else {
                    this.mc.thePlayer.closeScreen();
                    this.sendMessage("Finished buying items, closing menu and disabling AutoBuy");
                    this.process28();
                }
            }
        }
    }

    private boolean matchesString2(String str) {
        if (this.mc.thePlayer != null && this.mc.thePlayer.inventory != null) {
            for (int i = 0; i < 36; i++) {
                ItemStack stack = this.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() != null) {
                    String str2 = Item.itemRegistry.getNameForObject(stack.getItem()).toString();
                    if (str2.contains(str)) {
                        if (this.debugSetting.isEnabled()) {
                            this.sendMessage("Debug: Found " + str + " in inventory slot " + i);
                        }

                        return true;
                    }
                }
            }

            return false;
        }
        return false;
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

    private void process7() {
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), true);
    }

    public static enum WalkingMode {
        WALKING,
        INTERACTING,
        CARPET,
        FALLING;

        private static AutoBuy.WalkingMode[] computeWalkingModeArray() {
            return new AutoBuy.WalkingMode[]{WALKING, INTERACTING, CARPET, FALLING};
        }
    }
}
