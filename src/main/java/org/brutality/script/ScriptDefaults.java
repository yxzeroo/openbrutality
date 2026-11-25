package org.brutality.script;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import org.brutality.Brutality;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.MessageUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ScriptDefaults {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    public static final ExecutorService executorService = Executors.newCachedThreadPool();
    private final List<Setting> entries = new ArrayList<>();
    public static final ScriptDefaults.ClientAPI client = new ScriptDefaults.ClientAPI();
    public static final ScriptDefaults.WorldAPI world = new ScriptDefaults.WorldAPI();
    public static final ScriptDefaults.PacketAPI packet = new ScriptDefaults.PacketAPI();
    public static final ScriptDefaults.RenderAPI render = new ScriptDefaults.RenderAPI();
    public static final ScriptDefaults.InventoryAPI inventory = new ScriptDefaults.InventoryAPI();
    public static final ScriptDefaults.KeybindsAPI keybinds = new ScriptDefaults.KeybindsAPI();
    public static final ScriptDefaults.MathAPI math = new ScriptDefaults.MathAPI();
    public static final ScriptDefaults.TimerAPI timer = new ScriptDefaults.TimerAPI();
    public static final ScriptDefaults.ModulesAPI modules = new ScriptDefaults.ModulesAPI();
    public static final ScriptDefaults.PlayerAPI player = new ScriptDefaults.PlayerAPI();
    public static final ScriptDefaults.UtilAPI util = new ScriptDefaults.UtilAPI();
    public static final ScriptDefaults.GlAPI gl = new ScriptDefaults.GlAPI();

    protected BooleanSetting booleanSetting(String name, boolean defaultValue) {
        BooleanSetting boolSetting = new BooleanSetting(name, null, defaultValue);
        this.entries.add(boolSetting);
        return boolSetting;
    }

    protected NumberSetting numberSetting(String name, double defaultValue, double min, double max, double increment) {
        NumberSetting numberSetting = new NumberSetting(name, null, defaultValue, min, max, increment);
        this.entries.add(numberSetting);
        return numberSetting;
    }

    protected ModeSetting modeSetting(String name, String defaultMode, String... modes) {
        ModeSetting modeSetting = new ModeSetting(name, null, defaultMode, Arrays.asList(modes));
        this.entries.add(modeSetting);
        return modeSetting;
    }

    protected StringSetting stringSetting(String name, String defaultValue) {
        StringSetting stringSetting = new StringSetting(name, null, defaultValue);
        this.entries.add(stringSetting);
        return stringSetting;
    }

    protected boolean getBoolean(String name) {
        for (Setting setting : this.entries) {
            if (setting instanceof BooleanSetting && setting.getName().equalsIgnoreCase(name)) {
                return ((BooleanSetting)setting).isEnabled();
            }
        }

        return false;
    }

    protected double computeDouble(String name) {
        for (Setting setting : this.entries) {
            if (setting instanceof NumberSetting && setting.getName().equalsIgnoreCase(name)) {
                return ((NumberSetting)setting).computeDouble();
            }
        }

        return 0.0;
    }

    protected String computeString(String name) {
        for (Setting setting : this.entries) {
            if (setting instanceof ModeSetting && setting.getName().equalsIgnoreCase(name)) {
                return ((ModeSetting)setting).getText();
            }
        }

        return "";
    }

    protected String getString(String name) {
        for (Setting setting : this.entries) {
            if (setting instanceof StringSetting && setting.getName().equalsIgnoreCase(name)) {
                return ((StringSetting)setting).getValue();
            }
        }

        return "";
    }

    public List<Setting> getEntries() {
        return this.entries;
    }

    public static class ClientAPI {
        public void handleString2(String message) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.sendChatMessage(message);
            }
        }

        public void handleString3(String message) {
            MessageUtils.x(message.replace("&", "§"));
        }

        public void handleObject(Object obj) {
        }

        public void sendPacket(Packet<?> packet) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.sendQueue.addToSendQueue(packet);
            }
        }

        public EntityPlayerSP getPlayer() {
            return ScriptDefaults.mc.thePlayer;
        }

        public double getX() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.posX : 0.0;
        }

        public double getY() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.posY : 0.0;
        }

        public double getZ() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.posZ : 0.0;
        }

        public double computeDouble4() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.prevPosX : 0.0;
        }

        public double computeDouble7() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.prevPosY : 0.0;
        }

        public double computeDouble3() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.prevPosZ : 0.0;
        }

        public double computeDouble6() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.lastTickPosX : 0.0;
        }

        public double computeDouble8() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.lastTickPosY : 0.0;
        }

        public double computeDouble5() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.lastTickPosZ : 0.0;
        }

        public float getYaw() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rotationYaw : 0.0F;
        }

        public float getPitch() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rotationPitch : 0.0F;
        }

        public void setYaw(float yaw) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.rotationYaw = yaw;
            }
        }

        public void setPitch(float pitch) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.rotationPitch = pitch;
            }
        }

        public void setRotation(float yaw, float pitch) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.rotationYaw = yaw;
                ScriptDefaults.mc.thePlayer.rotationPitch = pitch;
            }
        }

        public float getHealth() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getHealth() : 0.0F;
        }

        public float getMaxHealth() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getMaxHealth() : 0.0F;
        }

        public float computeFloat4() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getAbsorptionAmount() : 0.0F;
        }

        public int b() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getFoodStats().getFoodLevel() : 0;
        }

        public float computeFloat() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getFoodStats().getSaturationLevel() : 0.0F;
        }

        public int computeInt2() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getTotalArmorValue() : 0;
        }

        public int computeInt() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.experienceTotal : 0;
        }

        public int getLevel() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.experienceLevel : 0;
        }

        public double computeDouble2() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.motionX : 0.0;
        }

        public double computeDouble() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.motionY : 0.0;
        }

        public double computeDouble9() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.motionZ : 0.0;
        }

        public void handleDouble(double x, double y, double z) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.motionX = x;
                ScriptDefaults.mc.thePlayer.motionY = y;
                ScriptDefaults.mc.thePlayer.motionZ = z;
            }
        }

        public void handleDouble2(double x) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.motionX = x;
            }
        }

        public void handleDouble(double y) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.motionY = y;
            }
        }

        public void handleDouble3(double z) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.motionZ = z;
            }
        }

        public void setPosition(double x, double y, double z) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.setPosition(x, y, z);
            }
        }

        public void setSpeed(double speed) {
            if (ScriptDefaults.mc.thePlayer != null) {
                double rad = Math.toRadians(ScriptDefaults.mc.thePlayer.rotationYaw);
                float moveForward = ScriptDefaults.mc.thePlayer.moveForward;
                float moveStrafing = ScriptDefaults.mc.thePlayer.moveStrafing;
                if (moveForward == 0.0F && moveStrafing == 0.0F) {
                    ScriptDefaults.mc.thePlayer.motionX = 0.0;
                    ScriptDefaults.mc.thePlayer.motionZ = 0.0;
                } else {
                    double d = rad;
                    if (moveForward != 0.0F) {
                        if (moveStrafing > 0.0F) {
                            d = rad - (Math.PI / 4);
                        } else if (moveStrafing < 0.0F) {
                            d = rad + (Math.PI / 4);
                        }

                        if (moveForward < 0.0F) {
                            d += Math.PI;
                        }
                    } else if (moveStrafing > 0.0F) {
                        d = rad - (Math.PI / 2);
                    } else if (moveStrafing < 0.0F) {
                        d = rad + (Math.PI / 2);
                    }

                    ScriptDefaults.mc.thePlayer.motionX = -Math.sin(d) * speed;
                    ScriptDefaults.mc.thePlayer.motionZ = Math.cos(d) * speed;
                }
            }
        }

        public boolean isOnGround() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.onGround;
        }

        public void setOnGround(boolean ground) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.onGround = ground;
            }
        }

        public boolean isMoving() {
            return ScriptDefaults.mc.thePlayer == null
                ? false
                : ScriptDefaults.mc.thePlayer.moveForward != 0.0F || ScriptDefaults.mc.thePlayer.moveStrafing != 0.0F;
        }

        public boolean isSprinting() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isSprinting();
        }

        public void setSprinting(boolean sprinting) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.setSprinting(sprinting);
            }
        }

        public boolean isSneaking() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isSneaking();
        }

        public boolean isInWater() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isInWater();
        }

        public boolean isInLava() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isInLava();
        }

        public boolean isBurning() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isBurning();
        }

        public boolean isFlying() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.capabilities.isFlying;
        }

        public void setFlying(boolean flying) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.capabilities.isFlying = flying;
            }
        }

        public boolean isCreative() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.capabilities.isCreativeMode;
        }

        public float computeFloat7() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.fallDistance : 0.0F;
        }

        public void handleFloat(float distance) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.fallDistance = distance;
            }
        }

        public int computeInt7() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.hurtTime : 0;
        }

        public int computeInt6() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.ticksExisted : 0;
        }

        public String getName() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getName() : "";
        }

        public void jump() {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.jump();
            }
        }

        public void swingItem() {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.swingItem();
            }
        }

        public String computeString() {
            return ScriptDefaults.mc.getCurrentServerData() != null ? ScriptDefaults.mc.getCurrentServerData().serverIP : "singleplayer";
        }

        public int g() {
            return Minecraft.getDebugFPS();
        }

        public int getPing() {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.getNetHandler() != null) {
                NetworkPlayerInfo playerInfo = ScriptDefaults.mc.getNetHandler().getPlayerInfo(ScriptDefaults.mc.thePlayer.getUniqueID());
                if (playerInfo != null) {
                    return playerInfo.getResponseTime();
                }
            }

            return -1;
        }

        public boolean checkState() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null;
        }

        public long computeLong() {
            return System.currentTimeMillis();
        }

        public void sleep(long ms) {
            try {
                Thread.sleep(ms);
            } catch (InterruptedException ignored) {
            }
        }

        public void handleRunnable(Runnable task) {
            ScriptDefaults.executorService.submit(task);
        }

        public void handleEntity(Entity entity) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.playerController != null) {
                ScriptDefaults.mc.playerController.attackEntity(ScriptDefaults.mc.thePlayer, entity);
            }
        }

        public ItemStack getHeldItem() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getHeldItem() : null;
        }

        public int computeInt5() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.inventory.currentItem : 0;
        }

        public void handleInt2(int slot) {
            if (ScriptDefaults.mc.thePlayer != null && slot >= 0 && slot <= 8) {
                ScriptDefaults.mc.thePlayer.inventory.currentItem = slot;
            }
        }

        public FontRenderer getFontRenderer() {
            return ScriptDefaults.mc.fontRendererObj;
        }

        public void process2() {
            if (ScriptDefaults.mc.theWorld != null) {
                ScriptDefaults.mc.theWorld.sendQuittingDisconnectingPacket();
                ScriptDefaults.mc.loadWorld(null);
            }
        }

        public void closeScreen() {
            ScriptDefaults.mc.displayGuiScreen(null);
        }

        public int computeInt4() {
            return new ScaledResolution(ScriptDefaults.mc).getScaledWidth();
        }

        public int computeInt3() {
            return new ScaledResolution(ScriptDefaults.mc).getScaledHeight();
        }

        public float computeFloat3() {
            if (ScriptDefaults.mc.thePlayer == null) {
                return 0.0F;
            }
            float yaw = ScriptDefaults.mc.thePlayer.rotationYaw;
            float moveForward = ScriptDefaults.mc.thePlayer.moveForward;
            float moveStrafing = ScriptDefaults.mc.thePlayer.moveStrafing;
            if (moveForward < 0.0F) {
                yaw += 180.0F;
            }

            float f = moveForward > 0.0F ? 1.0F : (moveForward < 0.0F ? -1 : 0);
            if (moveStrafing > 0.0F) {
                yaw -= 90.0F * f;
            }

            if (moveStrafing < 0.0F) {
                yaw += 90.0F * f;
            }

            return yaw;
        }

        public double computeDouble10() {
            double d = 0.2873;
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                int amplifier = ScriptDefaults.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
                d *= 1.0 + 0.2 * (amplifier + 1);
            }

            return d;
        }

        public boolean matchesInt(int potionId) {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isPotionActive(potionId);
        }

        public int computeInt(int potionId) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isPotionActive(potionId)) {
                PotionEffect activePotionEffect = ScriptDefaults.mc.thePlayer.getActivePotionEffect(Potion.potionTypes[potionId]);
                return activePotionEffect != null ? activePotionEffect.getAmplifier() : -1;
            }
            return -1;
        }

        public float getEyeHeight() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getEyeHeight() : 0.0F;
        }

        public MovingObjectPosition computeMovingObjectPosition3(double distance) {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rayTrace(distance, 1.0F) : null;
        }

        public Entity computeEntity() {
            return ScriptDefaults.mc.pointedEntity;
        }

        public void handleFloat5(float timer) {
            ScriptDefaults.mc.timer.timerSpeed = timer;
        }

        public float computeFloat10() {
            return ScriptDefaults.mc.timer.timerSpeed;
        }

        public void removePotionEffect(int id) {
            if (ScriptDefaults.mc.thePlayer != null) {
                PotionEffect activePotionEffect = ScriptDefaults.mc.thePlayer.getActivePotionEffect(Potion.potionTypes[id]);
                if (activePotionEffect != null) {
                    ScriptDefaults.mc.thePlayer.removePotionEffect(id);
                }
            }
        }

        public void process() {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.capabilities.allowFlying = true;
            }
        }

        public void handleFlag3(boolean sneak) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.setSneaking(sneak);
            }
        }

        public boolean checkState2() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isSneaking();
        }

        public float computeFloat9() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.moveForward : 0.0F;
        }

        public void handleFloat3(float forward) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.moveForward = forward;
            }
        }

        public float computeFloat6() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.moveStrafing : 0.0F;
        }

        public void handleFloat4(float strafe) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.moveStrafing = strafe;
            }
        }

        public void handleFlag(boolean jump) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.movementInput.jump = jump;
            }
        }

        public float computeFloat5() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rotationPitch : 0.0F;
        }

        public void handleFloat6(float pitch) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.rotationPitch = pitch;
            }
        }

        public float computeFloat8() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rotationYaw : 0.0F;
        }

        public void handleFloat2(float yaw) {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.rotationYaw = yaw;
            }
        }

        public void handleFlag2(boolean fixed) {
        }

        public void handleInt(int count) {
            if (ScriptDefaults.mc.thePlayer != null) {
                try {
                    Field declaredField = EntityPlayer.class.getDeclaredField("itemInUseCount");
                    declaredField.setAccessible(true);
                    declaredField.set(ScriptDefaults.mc.thePlayer, count);
                } catch (Exception ignored) {
                }
            }
        }

        public int getItemInUseCount() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getItemInUseCount() : 0;
        }

        public int getItemInUseDuration() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getItemInUseDuration() : 0;
        }

        public void resetEquippedProgress() {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.thePlayer.renderArmYaw = 0.0F;
                ScriptDefaults.mc.thePlayer.renderArmPitch = 0.0F;
            }
        }

        public float computeFloat2() {
            if (ScriptDefaults.mc.thePlayer == null) {
                return 0.0F;
            }
            ItemStack heldItem = ScriptDefaults.mc.thePlayer.getHeldItem();
            return heldItem == null ? 0.0F : heldItem.getMaxItemUseDuration() - ScriptDefaults.mc.thePlayer.getItemInUseDuration();
        }

        public MovingObjectPosition computeMovingObjectPosition(double distance) {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.rayTrace(distance, 1.0F) : null;
        }

        public MovingObjectPosition computeMovingObjectPosition2(double distance) {
            return ScriptDefaults.mc.thePlayer == null ? null : ScriptDefaults.mc.thePlayer.rayTrace(distance, 1.0F);
        }

        public boolean matchesInt(int x, int y, int z) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                BlockPos pos = new BlockPos(x, y, z);
                IBlockState blockState = ScriptDefaults.mc.theWorld.getBlockState(pos);
                return blockState.getBlock().isFullBlock();
            }
            return false;
        }

        public boolean matchesInt2(int x, int y, int z) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.playerController != null) {
                BlockPos pos = new BlockPos(x, y, z);
                return ScriptDefaults.mc
                    .playerController
                    .onPlayerRightClick(
                        ScriptDefaults.mc.thePlayer,
                        ScriptDefaults.mc.theWorld,
                        ScriptDefaults.mc.thePlayer.getHeldItem(),
                        pos,
                        EnumFacing.UP,
                        new Vec3(x + 0.5, y + 0.5, z + 0.5)
                    );
            }
            return false;
        }

        public void handleString6(String name) {
        }

        public void handleString(String name) {
        }

        public void handleString5(String name) {
        }

        public void handleString4(String name) {
        }

        public boolean matchesString2(String name) {
            return false;
        }

        public boolean matchesString(String name) {
            return false;
        }

        public GuiScreen computeGuiScreen() {
            return ScriptDefaults.mc.currentScreen;
        }

        public List<Entity> computeList() {
            return (List<Entity>)(ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.loadedEntityList : new ArrayList<>());
        }
    }

    public static class GlAPI {
        private static final String text = "Brutality" + File.separator + "Scripts" + File.separator + "script_config.txt";
        private final Map<String, String> lookupMap = new HashMap<>();

        public boolean matchesString(String key, String value) {
            try {
                this.loadConfig();
                this.lookupMap.put(key, value);
                this.process();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public String get(String key) {
            if (this.lookupMap.isEmpty()) {
                this.loadConfig();
            }

            return this.lookupMap.get(key);
        }

        public boolean has(String key) {
            if (this.lookupMap.isEmpty()) {
                this.loadConfig();
            }

            return this.lookupMap.containsKey(key);
        }

        public void remove(String key) {
            this.loadConfig();
            this.lookupMap.remove(key);
            this.process();
        }

        public void clear() {
            this.lookupMap.clear();
            this.process();
        }

        public Map<String, String> computeMap() {
            if (this.lookupMap.isEmpty()) {
                this.loadConfig();
            }

            return new HashMap<>(this.lookupMap);
        }

        private void loadConfig() {
            File file = new File(text);
            if (!file.exists()) {
                File parentFile = file.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
            } else {
                String str;
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    while ((str = reader.readLine()) != null) {
                        String str2 = str.trim();
                        if (!str2.isEmpty() && !str2.startsWith("#")) {
                            int n = str2.indexOf("=");
                            if (n > 0) {
                                String str3 = str2.substring(0, n).trim();
                                String str4 = str2.substring(n + 1).trim();
                                this.lookupMap.put(str3, str4);
                            }
                        }
                    }
                } catch (IOException ignored) {
                }
            }
        }

        private void process() {
            File file = new File(text);
            File parentFile = file.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Entry entry : this.lookupMap.entrySet()) {
                    writer.println((String)entry.getKey() + "=" + (String)entry.getValue());
                }
            } catch (IOException ignored) {
            }
        }
    }

    public static class InventoryAPI {
        public final int count = 1;
        public final int count2 = 3;
        public final int count3 = 2;
        public final int count4 = 4;
        public final int count5 = 5;
        public final int count6 = 6;
        public final int count7 = 7;

        public void pushMatrix() {
            GlStateManager.pushMatrix();
        }

        public void popMatrix() {
            GlStateManager.popMatrix();
        }

        public void translate(double x, double y, double z) {
            GlStateManager.translate(x, y, z);
        }

        public void rotate(float angle, float x, float y, float z) {
            GlStateManager.rotate(angle, x, y, z);
        }

        public void scale(double x, double y, double z) {
            GlStateManager.scale(x, y, z);
        }

        public void enableBlend() {
            GlStateManager.enableBlend();
        }

        public void disableBlend() {
            GlStateManager.disableBlend();
        }

        public void enableDepth() {
            GlStateManager.enableDepth();
        }

        public void disableDepth() {
            GlStateManager.disableDepth();
        }

        public void process() {
            GlStateManager.enableTexture2D();
        }

        public void process2() {
            GlStateManager.disableTexture2D();
        }

        public void enableAlpha() {
            GlStateManager.enableAlpha();
        }

        public void disableAlpha() {
            GlStateManager.disableAlpha();
        }

        public void color(float r, float g, float b, float a) {
            GlStateManager.color(r, g, b, a);
        }

        public void resetColor() {
            GlStateManager.resetColor();
        }

        public void handleFloat(float width) {
            GL11.glLineWidth(width);
        }

        public void handleInt(int mode) {
            GL11.glBegin(mode);
        }

        public void end() {
            GL11.glEnd();
        }

        public void handleDouble(double x, double y, double z) {
            GL11.glVertex3d(x, y, z);
        }

        public void handleDouble(double x, double y) {
            GL11.glVertex2d(x, y);
        }

        public void depthMask(boolean flag) {
            GlStateManager.depthMask(flag);
        }

        public void blendFunc(int src, int dst) {
            GlStateManager.tryBlendFuncSeparate(src, dst, GL11.GL_ONE, GL11.GL_ZERO);
        }

        public void process4() {
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
        }

        public void process5() {
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
        }

        public void handleInt(int x, int y, int width, int height) {
            ScaledResolution sr = new ScaledResolution(ScriptDefaults.mc);
            int scaleFactor = sr.getScaleFactor();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(x * scaleFactor, (sr.getScaledHeight() - y - height) * scaleFactor, width * scaleFactor, height * scaleFactor);
        }

        public void process3() {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    public static class KeybindsAPI {
        public ItemStack computeItemStack5(int slot) {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.inventoryContainer != null
                ? ScriptDefaults.mc.thePlayer.inventoryContainer.getSlot(slot).getStack()
                : null;
        }

        public ItemStack computeItemStack3(int slot) {
            return ScriptDefaults.mc.thePlayer != null && slot >= 0 && slot <= 8 ? ScriptDefaults.mc.thePlayer.inventory.mainInventory[slot] : null;
        }

        public ItemStack computeItemStack2(int slot) {
            return ScriptDefaults.mc.thePlayer != null && slot >= 0 && slot <= 3 ? ScriptDefaults.mc.thePlayer.inventory.armorInventory[slot] : null;
        }

        public int computeInt2() {
            return ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.inventory.currentItem : 0;
        }

        public void handleInt(int slot) {
            if (ScriptDefaults.mc.thePlayer != null && slot >= 0 && slot <= 8) {
                ScriptDefaults.mc.thePlayer.inventory.currentItem = slot;
            }
        }

        public void handleInt(int slot, int button, int mode) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.playerController != null) {
                ScriptDefaults.mc
                    .playerController
                    .windowClick(ScriptDefaults.mc.thePlayer.openContainer.windowId, slot, button, mode, ScriptDefaults.mc.thePlayer);
            }
        }

        public void swap(int from, int to) {
            this.handleInt(from, 0, 0);
            this.handleInt(to, 0, 0);
        }

        public void handleInt2(int slot) {
            this.handleInt(slot, 1, 4);
        }

        public void handleInt3(int slot) {
            this.handleInt(slot, 0, 1);
        }

        public int getSize() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.inventoryContainer != null
                ? ScriptDefaults.mc.thePlayer.inventoryContainer.inventorySlots.size()
                : 0;
        }

        public int computeInt(String name) {
            if (ScriptDefaults.mc.thePlayer == null) {
                return -1;
            }
            for (int i = 0; i < 36; i++) {
                ItemStack stack = ScriptDefaults.mc.thePlayer.inventory.mainInventory[i];
                if (stack != null && stack.getDisplayName().toLowerCase().contains(name.toLowerCase())) {
                    return i;
                }
            }

            return -1;
        }

        public int computeInt(int itemId) {
            if (ScriptDefaults.mc.thePlayer == null) {
                return -1;
            }
            for (int i = 0; i < 36; i++) {
                ItemStack stack = ScriptDefaults.mc.thePlayer.inventory.mainInventory[i];
                if (stack != null && Item.getIdFromItem(stack.getItem()) == itemId) {
                    return i;
                }
            }

            return -1;
        }

        public boolean matchesItemStack2(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemSword;
        }

        public boolean matchesItemStack3(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemArmor;
        }

        public boolean matchesItemStack5(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemTool;
        }

        public boolean matchesItemStack4(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemBow;
        }

        public boolean matchesItemStack(ItemStack stack) {
            return stack != null && stack.getItem() instanceof ItemPotion;
        }

        public int computeInt() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.openContainer != null
                ? ScriptDefaults.mc.thePlayer.openContainer.inventorySlots.size()
                : 0;
        }

        public boolean checkState() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.openContainer != ScriptDefaults.mc.thePlayer.inventoryContainer;
        }

        public String computeString() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.openContainer != null
                ? ScriptDefaults.mc.thePlayer.openContainer.getClass().getSimpleName()
                : "";
        }

        public ItemStack computeItemStack(int slot) {
            return ScriptDefaults.mc.thePlayer != null
                    && ScriptDefaults.mc.thePlayer.openContainer != null
                    && slot >= 0
                    && slot < ScriptDefaults.mc.thePlayer.openContainer.inventorySlots.size()
                ? ScriptDefaults.mc.thePlayer.openContainer.getSlot(slot).getStack()
                : null;
        }

        public ItemStack computeItemStack4(int slot) {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.openContainer != null
                ? ScriptDefaults.mc.thePlayer.openContainer.getSlot(slot).getStack()
                : null;
        }

        public ItemStack computeItemStack() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.openContainer != null
                ? ScriptDefaults.mc.thePlayer.openContainer.getSlot(0).getStack()
                : null;
        }

        public String computeString2() {
            ItemStack stack = ScriptDefaults.mc.thePlayer != null ? ScriptDefaults.mc.thePlayer.getHeldItem() : null;
            if (stack != null && stack.getItem() instanceof ItemEditableBook) {
                ItemEditableBook item = (ItemEditableBook)stack.getItem();
                return "";
            }
            return "";
        }

        public void open() {
            if (ScriptDefaults.mc.thePlayer != null) {
                ScriptDefaults.mc.displayGuiScreen(new GuiInventory(ScriptDefaults.mc.thePlayer));
            }
        }
    }

    public static class MathAPI {
        public final int count = 1;
        public final int count2 = 2;
        public final int count3 = 3;
        public final int count4 = 4;
        public final int count5 = 5;
        public final int count6 = 6;
        public final int count7 = 7;
        public final int count8 = 8;
        public final int count9 = 9;
        public final int count10 = 10;
        public final int count11 = 11;
        public final int count12 = 16;
        public final int count13 = 17;
        public final int count14 = 18;
        public final int count15 = 19;
        public final int count16 = 20;
        public final int count17 = 30;
        public final int count18 = 31;
        public final int count19 = 32;
        public final int count20 = 33;
        public final int count21 = 34;
        public final int count22 = 35;
        public final int count23 = 44;
        public final int count24 = 45;
        public final int count25 = 46;
        public final int count26 = 47;
        public final int count27 = 48;
        public final int count28 = 57;
        public final int count29 = 42;
        public final int count30 = 54;
        public final int count31 = 29;
        public final int count32 = 15;

        public boolean matchesInt2(int keyCode) {
            return Keyboard.isKeyDown(keyCode);
        }

        public boolean matchesInt(int button) {
            return Mouse.isButtonDown(button);
        }

        public void process() {
            KeyBinding.onTick(ScriptDefaults.mc.gameSettings.keyBindAttack.getKeyCode());
        }

        public void process2() {
            KeyBinding.onTick(ScriptDefaults.mc.gameSettings.keyBindUseItem.getKeyCode());
        }

        public int computeInt(String keyName) {
            return Keyboard.getKeyIndex(keyName.toUpperCase());
        }

        public String computeString(int keyCode) {
            return Keyboard.getKeyName(keyCode);
        }

        public boolean checkState2() {
            return ScriptDefaults.mc.gameSettings.keyBindForward.isKeyDown();
        }

        public boolean checkState3() {
            return ScriptDefaults.mc.gameSettings.keyBindBack.isKeyDown();
        }

        public boolean isLeft() {
            return ScriptDefaults.mc.gameSettings.keyBindLeft.isKeyDown();
        }

        public boolean isRight() {
            return ScriptDefaults.mc.gameSettings.keyBindRight.isKeyDown();
        }

        public boolean checkState5() {
            return ScriptDefaults.mc.gameSettings.keyBindJump.isKeyDown();
        }

        public boolean checkState4() {
            return ScriptDefaults.mc.gameSettings.keyBindSneak.isKeyDown();
        }

        public boolean checkState() {
            return ScriptDefaults.mc.gameSettings.keyBindSprint.isKeyDown();
        }

        public boolean matchesString(String key) {
            int n = this.computeInt(key);
            return this.matchesInt2(n);
        }

        public void handleString(String key, boolean pressed) {
        }

        public void handleString(String key) {
            int n = this.computeInt(key);
            KeyBinding.onTick(n);
        }

        public int[] computeIntArray() {
            ScaledResolution sr = new ScaledResolution(ScriptDefaults.mc);
            int n = (int)((double)(Mouse.getX() * sr.getScaledWidth()) / ScriptDefaults.mc.displayWidth);
            int n2 = (int)(sr.getScaledHeight() - (double)(Mouse.getY() * sr.getScaledHeight()) / ScriptDefaults.mc.displayHeight - 1.0);
            return new int[]{n, n2};
        }

        public int getScroll() {
            return Mouse.getDWheel();
        }
    }

    public static class ModulesAPI {
        public double computeDouble(double x1, double z1, double x2, double z2) {
            double d = x2 - x1;
            double d2 = z2 - z1;
            return Math.sqrt(d * d + d2 * d2);
        }

        public double computeDouble(double x1, double y1, double z1, double x2, double y2, double z2) {
            double d = x2 - x1;
            double d2 = y2 - y1;
            double d3 = z2 - z1;
            return Math.sqrt(d * d + d2 * d2 + d3 * d3);
        }

        public float computeFloat(float angle) {
            float f = angle % 360.0F;
            if (f >= 180.0F) {
                f -= 360.0F;
            }

            if (f < -180.0F) {
                f += 360.0F;
            }

            return f;
        }

        public float[] computeFloatArray(Entity entity) {
            if (ScriptDefaults.mc.thePlayer != null && entity != null) {
                double d = entity.posX - ScriptDefaults.mc.thePlayer.posX;
                double d2 = entity.posY + entity.getEyeHeight() - (ScriptDefaults.mc.thePlayer.posY + ScriptDefaults.mc.thePlayer.getEyeHeight());
                double d3 = entity.posZ - ScriptDefaults.mc.thePlayer.posZ;
                double dist = Math.sqrt(d * d + d3 * d3);
                float deg = (float)Math.toDegrees(-Math.atan2(d, d3));
                float f = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
                return new float[]{deg, f};
            }
            return new float[]{0.0F, 0.0F};
        }

        public float[] computeFloatArray(double x, double y, double z) {
            if (ScriptDefaults.mc.thePlayer == null) {
                return new float[]{0.0F, 0.0F};
            }
            double d = x - ScriptDefaults.mc.thePlayer.posX;
            double d2 = y - (ScriptDefaults.mc.thePlayer.posY + ScriptDefaults.mc.thePlayer.getEyeHeight());
            double d3 = z - ScriptDefaults.mc.thePlayer.posZ;
            double dist = Math.sqrt(d * d + d3 * d3);
            float deg = (float)Math.toDegrees(-Math.atan2(d, d3));
            float f = (float)(-Math.toDegrees(Math.atan2(d2, dist)));
            return new float[]{deg, f};
        }

        public double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }

        public float clamp(float value, float min, float max) {
            return Math.max(min, Math.min(max, value));
        }

        public double lerp(double start, double end, double t) {
            return start + (end - start) * t;
        }

        public float computeFloat(float start, float end, float t) {
            return start + (end - start) * t;
        }

        public double computeDouble(double degrees) {
            return Math.toRadians(degrees);
        }

        public double computeDouble2(double radians) {
            return Math.toDegrees(radians);
        }
    }

    public static class PacketAPI {
        public void handleString3(String name) {
            Module module = Brutality.moduleManager.computeModule(name);
            if (module != null && !module.checkState44()) {
                module.handleFlag3(true);
            }
        }

        public void handleString(String name) {
            Module module = Brutality.moduleManager.computeModule(name);
            if (module != null && module.checkState44()) {
                module.handleFlag3(false);
            }
        }

        public boolean matchesString(String name) {
            Module module = Brutality.moduleManager.computeModule(name);
            return module != null && module.checkState44();
        }

        public void handleString2(String name) {
            Module module = Brutality.moduleManager.computeModule(name);
            if (module != null) {
                module.process28();
            }
        }

        public Module computeModule(String name) {
            return Brutality.moduleManager.computeModule(name);
        }

        public List<String> computeList3() {
            ArrayList list = new ArrayList();

            for (Module module : ModuleManager.getEntries2()) {
                list.add(module.getName());
            }

            return list;
        }

        public List<String> computeList() {
            ArrayList list = new ArrayList();

            for (Module module : ModuleManager.getEntries2()) {
                if (module.checkState44()) {
                    list.add(module.getName());
                }
            }

            return list;
        }

        public void handleString(String moduleName, String settingName, Object value) {
            Module module = Brutality.moduleManager.computeModule(moduleName);
            if (module != null) {
                Setting setting = Brutality.settingsManager.computeSetting(module, settingName);
                if (setting != null) {
                    if (setting instanceof BooleanSetting && value instanceof Boolean) {
                        ((BooleanSetting)setting).setEnabled((Boolean)value);
                    } else if (setting instanceof NumberSetting && value instanceof Number) {
                        ((NumberSetting)setting).handleDouble2(((Number)value).doubleValue());
                    } else if (setting instanceof ModeSetting && value instanceof String) {
                        ((ModeSetting)setting).handleString((String)value);
                    }
                }
            }
        }

        public Object computeObject(String moduleName, String settingName) {
            Module module = Brutality.moduleManager.computeModule(moduleName);
            if (module == null) {
                return null;
            }
            Setting setting = Brutality.settingsManager.computeSetting(module, settingName);
            if (setting == null) {
                return null;
            }
            if (setting instanceof BooleanSetting) {
                return ((BooleanSetting)setting).isEnabled();
            }
            if (setting instanceof NumberSetting) {
                return ((NumberSetting)setting).computeDouble();
            }
            return setting instanceof ModeSetting ? ((ModeSetting)setting).getText() : null;
        }

        public Map<String, Object> computeMap(String moduleName) {
            HashMap map = new HashMap();
            Module module = Brutality.moduleManager.computeModule(moduleName);
            return module == null ? map : map;
        }

        public List<String> computeList2() {
            ArrayList list = new ArrayList();

            for (Category category : Category.values()) {
                if (category != Category.HIDDEN) {
                    list.add(category.name());
                }
            }

            return list;
        }

        public void handleString(String moduleName, String name, double min, double max, double value) {
            Module module = Brutality.moduleManager.computeModule(moduleName);
            if (module != null) {
                Brutality.settingsManager.addSetting(new NumberSetting(name, module, min, max, value, 0.1));
            }
        }

        public void handleString(String moduleName, String name, boolean value) {
            Module module = Brutality.moduleManager.computeModule(moduleName);
            if (module != null) {
                Brutality.settingsManager.addSetting(new BooleanSetting(name, module, value));
            }
        }

        public void handleString(String moduleName, String name, List<String> modes, String defaultMode) {
            Module module = Brutality.moduleManager.computeModule(moduleName);
            if (module != null) {
                ModeSetting modeSetting = new ModeSetting(name, module, defaultMode, modes);
                Brutality.settingsManager.addSetting(modeSetting);
            }
        }

        public void handleString2(String moduleName, String name) {
        }

        public void handleString(String moduleName, String description) {
        }

        public Entity computeEntity() {
            return null;
        }

        public Object computeObject() {
            return null;
        }
    }

    public static class PlayerAPI {
        public double getSpeed() {
            return ScriptDefaults.mc.thePlayer == null
                ? 0.0
                : Math.sqrt(
                    ScriptDefaults.mc.thePlayer.motionX * ScriptDefaults.mc.thePlayer.motionX
                        + ScriptDefaults.mc.thePlayer.motionZ * ScriptDefaults.mc.thePlayer.motionZ
                );
        }

        public double computeDouble() {
            return this.getSpeed() * 20.0;
        }

        public boolean matchesEntity(Entity entity, float fov) {
            if (ScriptDefaults.mc.thePlayer != null && entity != null) {
                double d = entity.posX - ScriptDefaults.mc.thePlayer.posX;
                double d2 = entity.posZ - ScriptDefaults.mc.thePlayer.posZ;
                float deg = (float)Math.toDegrees(-Math.atan2(d, d2));
                float f = Math.abs(MathHelper.wrapAngleTo180_float(ScriptDefaults.mc.thePlayer.rotationYaw - deg));
                return f <= fov / 2.0F;
            }
            return false;
        }

        public boolean checkState2() {
            return ScriptDefaults.mc.thePlayer != null
                && ScriptDefaults.mc.thePlayer.getHeldItem() != null
                && ScriptDefaults.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
        }

        public boolean checkState() {
            return ScriptDefaults.mc.thePlayer != null
                && ScriptDefaults.mc.thePlayer.getHeldItem() != null
                && ScriptDefaults.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow;
        }

        public boolean isUsingItem() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isUsingItem();
        }

        public boolean isBlocking() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isBlocking();
        }

        public boolean isEating() {
            return ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.thePlayer.isEating();
        }

        public float computeFloat() {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                double y = ScriptDefaults.mc.thePlayer.posY;

                for (double i = y; i > 0.0; i--) {
                    if (!ScriptDefaults.mc.theWorld.isAirBlock(new BlockPos(ScriptDefaults.mc.thePlayer.posX, i - 1.0, ScriptDefaults.mc.thePlayer.posZ))) {
                        return (float)(y - i);
                    }
                }

                return (float)y;
            }
            return 0.0F;
        }

        public void handleFloat(float speed) {
            if (ScriptDefaults.mc.thePlayer != null) {
                float rad = (float)Math.toRadians(ScriptDefaults.mc.thePlayer.rotationYaw);
                ScriptDefaults.mc.thePlayer.motionX = -MathHelper.sin(rad) * speed;
                ScriptDefaults.mc.thePlayer.motionZ = MathHelper.cos(rad) * speed;
            }
        }
    }

    public static class RenderAPI {
        private static final long timestamp = 3000L;

        public void handleDouble2(double x, double y, double width, double height, int color) {
            Gui.drawRect((int)x, (int)y, (int)(x + width), (int)(y + height), color);
        }

        public void handleDouble(double x, double y, double width, double height, int color) {
            this.handleDouble2(x, y, width, 1.0, color);
            this.handleDouble2(x, y + height - 1.0, width, 1.0, color);
            this.handleDouble2(x, y, 1.0, height, color);
            this.handleDouble2(x + width - 1.0, y, 1.0, height, color);
        }

        public void handleDouble(double x, double y, double width, double height, int topColor, int bottomColor) {
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            float f = (topColor >> 24 & 0xFF) / 255.0F;
            float f2 = (topColor >> 16 & 0xFF) / 255.0F;
            float f3 = (topColor >> 8 & 0xFF) / 255.0F;
            float f4 = (topColor & 0xFF) / 255.0F;
            float f5 = (bottomColor >> 24 & 0xFF) / 255.0F;
            float f6 = (bottomColor >> 16 & 0xFF) / 255.0F;
            float f7 = (bottomColor >> 8 & 0xFF) / 255.0F;
            float f8 = (bottomColor & 0xFF) / 255.0F;
            worldRenderer.func_181662_b(x + width, y, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
            worldRenderer.func_181662_b(x, y, 0.0).func_181666_a(f2, f3, f4, f).func_181675_d();
            worldRenderer.func_181662_b(x, y + height, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
            worldRenderer.func_181662_b(x + width, y + height, 0.0).func_181666_a(f6, f7, f8, f5).func_181675_d();
            tessellator.draw();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
        }

        public void handleDouble(double x, double y, double width, double height, double radius, int color) {
            this.handleDouble2(x + radius, y, width - radius * 2.0, height, color);
            this.handleDouble2(x, y + radius, radius, height - radius * 2.0, color);
            this.handleDouble2(x + width - radius, y + radius, radius, height - radius * 2.0, color);
        }

        public void handleString3(String text, float x, float y, int color) {
            ScriptDefaults.mc.fontRendererObj.drawStringWithShadow(text, x, y, color);
        }

        public void handleString(String text, float x, float y, int color) {
            ScriptDefaults.mc.fontRendererObj.drawString(text, (int)x, (int)y, color);
        }

        public void handleString2(String text, float x, float y, int color) {
            float textWidth = ScriptDefaults.mc.fontRendererObj.getStringWidth(text);
            ScriptDefaults.mc.fontRendererObj.drawStringWithShadow(text, x - textWidth / 2.0F, y, color);
        }

        public int getStringWidth(String text) {
            return ScriptDefaults.mc.fontRendererObj.getStringWidth(text);
        }

        public int computeInt3() {
            return ScriptDefaults.mc.fontRendererObj.FONT_HEIGHT;
        }

        public int computeInt(int r, int g, int b, int a) {
            return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }

        public int computeInt(int r, int g, int b) {
            return this.computeInt(r, g, b, 255);
        }

        public int computeInt(float h, float s, float b) {
            return Color.HSBtoRGB(h, s, b);
        }

        public int computeInt(long offset) {
            float f = (float)((System.currentTimeMillis() + offset) % timestamp) / 3000.0F;
            return Color.HSBtoRGB(f, 0.7F, 1.0F);
        }

        public int computeInt(int color, float alpha) {
            int n = color >> 16 & 0xFF;
            int n2 = color >> 8 & 0xFF;
            int n3 = color & 0xFF;
            return this.computeInt(n, n2, n3, (int)(alpha * 255.0F));
        }

        public int computeInt2() {
            return new ScaledResolution(ScriptDefaults.mc).getScaledWidth();
        }

        public int computeInt() {
            return new ScaledResolution(ScriptDefaults.mc).getScaledHeight();
        }

        public void handleDouble(double x1, double y1, double x2, double y2, float width, int color) {
            float f = (color >> 24 & 0xFF) / 255.0F;
            float f2 = (color >> 16 & 0xFF) / 255.0F;
            float f3 = (color >> 8 & 0xFF) / 255.0F;
            float f4 = (color & 0xFF) / 255.0F;
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GL11.glLineWidth(width);
            GL11.glBegin(GL11.GL_LINES);
            GlStateManager.color(f2, f3, f4, f);
            GL11.glVertex2d(x1, y1);
            GL11.glVertex2d(x2, y2);
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }

        public void handleString(String text, double x, double y, double z, int color) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                float f = 1.6F;
                float f2 = 0.016666668F * f;
                double dist = ScriptDefaults.mc.thePlayer.getDistance(x, y, z);
                float f3 = (float)(f2 * dist);
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)x + 0.5F, (float)y + 1.5F, (float)z + 0.5F);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-ScriptDefaults.mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(ScriptDefaults.mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                GlStateManager.scale(-f3, -f3, f3);
                GlStateManager.disableLighting();
                GlStateManager.depthMask(false);
                GlStateManager.disableDepth();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                ScriptDefaults.mc.fontRendererObj.drawStringWithShadow(text, -ScriptDefaults.mc.fontRendererObj.getStringWidth(text) / 2, 0.0F, color);
                GlStateManager.enableDepth();
                GlStateManager.depthMask(true);
                GlStateManager.enableLighting();
                GlStateManager.popMatrix();
            }
        }

        public void handleDouble(double x, double y, double z, int color, float lineWidth) {
            double d = x + 1.0;
            double d2 = y + 1.0;
            double d3 = z + 1.0;
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(lineWidth);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            float f = (color >> 24 & 0xFF) / 255.0F;
            float f2 = (color >> 16 & 0xFF) / 255.0F;
            float f3 = (color >> 8 & 0xFF) / 255.0F;
            float f4 = (color & 0xFF) / 255.0F;
            GlStateManager.color(f2, f3, f4, f);
            worldRenderer.func_181668_a(3, DefaultVertexFormats.field_181706_f);
            worldRenderer.func_181662_b(x, y, z).func_181675_d();
            worldRenderer.func_181662_b(d, y, z).func_181675_d();
            worldRenderer.func_181662_b(d, y, d3).func_181675_d();
            worldRenderer.func_181662_b(x, y, d3).func_181675_d();
            worldRenderer.func_181662_b(x, y, z).func_181675_d();
            worldRenderer.func_181662_b(x, d2, z).func_181675_d();
            worldRenderer.func_181662_b(d, d2, z).func_181675_d();
            worldRenderer.func_181662_b(d, d2, d3).func_181675_d();
            worldRenderer.func_181662_b(x, d2, d3).func_181675_d();
            worldRenderer.func_181662_b(x, d2, z).func_181675_d();
            tessellator.draw();
            GL11.glBegin(GL11.GL_LINES);
            GL11.glVertex3d(x, y, z);
            GL11.glVertex3d(x, d2, z);
            GL11.glVertex3d(d, y, z);
            GL11.glVertex3d(d, d2, z);
            GL11.glVertex3d(d, y, d3);
            GL11.glVertex3d(d, d2, d3);
            GL11.glVertex3d(x, y, d3);
            GL11.glVertex3d(x, d2, d3);
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.disableBlend();
        }

        public void handleEntity(Entity ent, int color) {
            if (ent != null) {
                GlStateManager.disableDepth();
                GlStateManager.disableTexture2D();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                float f = (color >> 24 & 0xFF) / 255.0F;
                float f2 = (color >> 16 & 0xFF) / 255.0F;
                float f3 = (color >> 8 & 0xFF) / 255.0F;
                float f4 = (color & 0xFF) / 255.0F;
                GlStateManager.color(f2, f3, f4, f);
                ScriptDefaults.mc.getRenderManager().renderEntityStatic(ent, 0.0F, false);
                GlStateManager.enableTexture2D();
                GlStateManager.enableDepth();
                GlStateManager.disableBlend();
            }
        }

        public void handleEntity(Entity ent, int x, int y, float scale) {
            if (ent != null) {
                GlStateManager.enableColorMaterial();
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)x, (float)y, 50.0F);
                GlStateManager.scale(-scale, scale, scale);
                GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.disableLighting();
                ScriptDefaults.mc.getRenderManager().renderEntityStatic(ent, 0.0F, false);
                GlStateManager.popMatrix();
                GlStateManager.disableColorMaterial();
            }
        }

        public void handleDouble(double x1, double y1, double z1, double x2, double y2, double z2, int color, float width) {
            float f = (color >> 24 & 0xFF) / 255.0F;
            float f2 = (color >> 16 & 0xFF) / 255.0F;
            float f3 = (color >> 8 & 0xFF) / 255.0F;
            float f4 = (color & 0xFF) / 255.0F;
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(width);
            GL11.glBegin(GL11.GL_LINES);
            GlStateManager.color(f2, f3, f4, f);
            GL11.glVertex3d(x1, y1, z1);
            GL11.glVertex3d(x2, y2, z2);
            GL11.glEnd();
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
        }

        public float[] computeFloatArray(double x, double y, double z) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.getRenderManager() != null) {
                float fovSetting = ScriptDefaults.mc.gameSettings.fovSetting;
                Entity renderViewEntity = ScriptDefaults.mc.getRenderViewEntity();
                if (renderViewEntity == null) {
                    return null;
                }
                double d = x - renderViewEntity.posX;
                double d2 = y - renderViewEntity.posY;
                double d3 = z - renderViewEntity.posZ;
                double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                float yaw = renderViewEntity.rotationYaw;
                float pitch = renderViewEntity.rotationPitch;
                double cos = Math.cos(Math.toRadians(yaw + 90.0F));
                double sin = Math.sin(Math.toRadians(yaw + 90.0F));
                double cos2 = Math.cos(Math.toRadians(-pitch));
                double sin2 = Math.sin(Math.toRadians(-pitch));
                double d4 = d * cos + d3 * sin;
                double d5 = d2 * cos2 - (d * sin - d3 * cos) * sin2;
                double d6 = dist * cos2;
                if (d6 <= 0.0) {
                    return null;
                }
                ScaledResolution sr = new ScaledResolution(ScriptDefaults.mc);
                float f = sr.getScaledWidth() / 2.0F;
                float f2 = sr.getScaledHeight() / 2.0F;
                float f3 = f + (float)(d4 / d6 * f / Math.tan(Math.toRadians(fovSetting / 2.0F)));
                float f4 = f2 - (float)(d5 / d6 * f2 / Math.tan(Math.toRadians(fovSetting / 2.0F)));
                return new float[]{f3, f4};
            }
            return null;
        }

        public boolean matchesEntity(Entity entity) {
            return ScriptDefaults.mc.thePlayer != null && entity != null ? ScriptDefaults.mc.thePlayer.canEntityBeSeen(entity) : false;
        }

        public boolean matchesDouble(double x, double y, double z) {
            if (ScriptDefaults.mc.thePlayer == null) {
                return false;
            }
            float[] floats = this.computeFloatArray(x, y, z);
            if (floats == null) {
                return false;
            }
            ScaledResolution sr = new ScaledResolution(ScriptDefaults.mc);
            return floats[0] >= 0.0F && floats[0] <= sr.getScaledWidth() && floats[1] >= 0.0F && floats[1] <= sr.getScaledHeight();
        }

        public void process() {
        }

        public void process2() {
        }

        public void process3() {
        }

        public void process4() {
        }
    }

    public static class TimerAPI {
        private final Map<String, Long> lookupMap = new HashMap<>();
        private static final long timestamp = -1L;

        public void handleString2(String name) {
            this.lookupMap.put(name, System.currentTimeMillis());
        }

        public long computeLong(String name) {
            Long val = this.lookupMap.get(name);
            return val == null ? timestamp : System.currentTimeMillis() - val;
        }

        public boolean matchesString(String name, long ms) {
            Long val = this.lookupMap.get(name);
            return val == null ? true : System.currentTimeMillis() - val >= ms;
        }

        public void handleString(String name) {
            this.lookupMap.put(name, System.currentTimeMillis());
        }

        public void remove(String name) {
            this.lookupMap.remove(name);
        }
    }

    public static class UtilAPI {
        public final String text = "§";

        public String color(String message) {
            return message.replace("&", "§");
        }

        public String computeString(String string) {
            return string.replaceAll("§[0-9a-fk-or]", "");
        }

        public double computeDouble(double value, int places) {
            double d = Math.pow(10.0, places);
            return Math.round(value * d) / d;
        }

        public int computeInt(int min, int max) {
            return min + (int)(Math.random() * (max - min + 1));
        }

        public double computeDouble(double min, double max) {
            return min + Math.random() * (max - min);
        }

        public String format(String pattern, Object... args) {
            return String.format(pattern, args);
        }

        public String computeString(String str, int count) {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < count; i++) {
                sb.append(str);
            }

            return sb.toString();
        }

        public boolean isNullOrEmpty(String str) {
            return str == null || str.isEmpty();
        }
    }

    public static class WorldAPI {
        public WorldClient getWorld() {
            return ScriptDefaults.mc.theWorld;
        }

        public IBlockState computeIBlockState(int x, int y, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getBlockState(new BlockPos(x, y, z)) : null;
        }

        public Block getBlock(int x, int y, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock() : null;
        }

        public boolean matchesInt2(int x, int y, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.isAirBlock(new BlockPos(x, y, z)) : true;
        }

        public boolean matchesInt(int x, int y, int z) {
            if (ScriptDefaults.mc.theWorld != null) {
                IBlockState blockState = ScriptDefaults.mc.theWorld.getBlockState(new BlockPos(x, y, z));
                return blockState.getBlock().isFullBlock();
            }
            return false;
        }

        public int computeInt2(int x, int y, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getLight(new BlockPos(x, y, z)) : 0;
        }

        public List<Entity> computeList2() {
            return (List<Entity>)(ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.loadedEntityList : new ArrayList<>());
        }

        public List<EntityPlayer> computeList3() {
            return (List<EntityPlayer>)(ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.playerEntities : new ArrayList<>());
        }

        public List<EntityLivingBase> computeList4() {
            ArrayList list = new ArrayList();
            if (ScriptDefaults.mc.theWorld != null) {
                for (Entity entity : ScriptDefaults.mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityLivingBase) {
                        list.add((EntityLivingBase)entity);
                    }
                }
            }

            return list;
        }

        public EntityPlayer computeEntityPlayer(String name) {
            if (ScriptDefaults.mc.theWorld != null) {
                for (EntityPlayer playerEntity : ScriptDefaults.mc.theWorld.playerEntities) {
                    if (playerEntity.getName().equalsIgnoreCase(name)) {
                        return playerEntity;
                    }
                }
            }

            return null;
        }

        public Entity computeEntity(int id) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getEntityByID(id) : null;
        }

        public EntityLivingBase computeEntityLivingBase(double range) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                EntityLivingBase living = null;
                double d = range;

                for (Entity entity : ScriptDefaults.mc.theWorld.loadedEntityList) {
                    if (entity instanceof EntityLivingBase && entity != ScriptDefaults.mc.thePlayer) {
                        double dist = ScriptDefaults.mc.thePlayer.getDistanceToEntity(entity);
                        if (dist < d) {
                            d = dist;
                            living = (EntityLivingBase)entity;
                        }
                    }
                }

                return living;
            }
            return null;
        }

        public EntityPlayer computeEntityPlayer(double range) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                EntityPlayer player = null;
                double d = range;

                for (EntityPlayer playerEntity : ScriptDefaults.mc.theWorld.playerEntities) {
                    if (playerEntity != ScriptDefaults.mc.thePlayer) {
                        double dist = ScriptDefaults.mc.thePlayer.getDistanceToEntity(playerEntity);
                        if (dist < d) {
                            d = dist;
                            player = playerEntity;
                        }
                    }
                }

                return player;
            }
            return null;
        }

        public long computeLong() {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getWorldTime() : 0L;
        }

        public int getDimension() {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.provider.getDimensionId() : 0;
        }

        public double computeDouble(Entity a, Entity b) {
            return a != null && b != null ? a.getDistanceToEntity(b) : -1.0;
        }

        public double computeDouble(double x, double y, double z) {
            return ScriptDefaults.mc.thePlayer == null ? -1.0 : ScriptDefaults.mc.thePlayer.getDistance(x, y, z);
        }

        public boolean matchesEntity(Entity entity) {
            return ScriptDefaults.mc.thePlayer != null && entity != null ? ScriptDefaults.mc.thePlayer.canEntityBeSeen(entity) : false;
        }

        public int computeInt() {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.playerEntities.size() : 0;
        }

        public List<NetworkPlayerInfo> computeList() {
            ArrayList list = new ArrayList();
            if (ScriptDefaults.mc.getNetHandler() != null) {
                for (Object obj : ScriptDefaults.mc.getNetHandler().getPlayerInfoMap()) {
                    list.add((NetworkPlayerInfo)obj);
                }
            }

            return list;
        }

        public String computeString2() {
            return ScriptDefaults.mc.getNetHandler() != null && ScriptDefaults.mc.getNetHandler().getPlayerInfoMap() != null ? "" : "";
        }

        public String computeString() {
            return ScriptDefaults.mc.getNetHandler() != null && ScriptDefaults.mc.getNetHandler().getPlayerInfoMap() != null ? "" : "";
        }

        public int computeInt(int x, int y, int z) {
            if (ScriptDefaults.mc.theWorld != null) {
                Block block = ScriptDefaults.mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
                return Item.getIdFromItem(Item.getItemFromBlock(block));
            }
            return 0;
        }

        public boolean matchesInt(int x, int y, int z, int blockId) {
            if (ScriptDefaults.mc.thePlayer != null && ScriptDefaults.mc.theWorld != null) {
                BlockPos pos = new BlockPos(x, y, z);
                Block blockById = Block.getBlockById(blockId);
                return ScriptDefaults.mc.theWorld.setBlockState(pos, blockById.getDefaultState());
            }
            return false;
        }

        public List<Object> computeList5() {
            ArrayList list = new ArrayList();
            if (ScriptDefaults.mc.theWorld != null && ScriptDefaults.mc.theWorld.getScoreboard() != null) {
                list.addAll(ScriptDefaults.mc.theWorld.getScoreboard().getTeams());
            }

            return list;
        }

        public List<Object> getTileEntities() {
            ArrayList list = new ArrayList();
            if (ScriptDefaults.mc.theWorld != null) {
                list.addAll(ScriptDefaults.mc.theWorld.loadedTileEntityList);
            }

            return list;
        }

        public Object computeObject(int x, int y, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getTileEntity(new BlockPos(x, y, z)) : null;
        }

        public boolean isChunkLoaded(int x, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.isBlockLoaded(new BlockPos(x, 0, z)) : false;
        }

        public int computeInt(int x, int z) {
            return ScriptDefaults.mc.theWorld != null ? ScriptDefaults.mc.theWorld.getHeight(new BlockPos(x, 0, z)).getY() : 0;
        }
    }
}
