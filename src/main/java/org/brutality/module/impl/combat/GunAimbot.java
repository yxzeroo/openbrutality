package org.brutality.module.impl.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;

public class GunAimbot extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting holdRightClickSetting = new BooleanSetting("Hold Right Click", this, true);
    private final BooleanSetting spinBotSetting = new BooleanSetting("Spin Bot", this, false);
    private final NumberSetting predictionSetting = new NumberSetting("Prediction", this, 3.0, 0.0, 10.0, 1.0);
    private int count = Color.RED.getRGB();
    private int count2 = 1;
    private int count3 = 0;
    private float factor;
    private float factor2;
    private Entity target = null;
    private Vec3 vec3 = null;
    private boolean flag = false;
    private final Set<String> itemSet = new HashSet<>(
        Arrays.asList("wall_sign", "torch", "air", "ladder", "wheat", "fire", "water", "grass", "double_plant", "tallgrass")
    );
    private final Set<String> itemSet2 = new HashSet<>(Arrays.asList("golden_hoe", "stone_hoe", "diamond_hoe", "flint_and_steel", "iron_hoe", "wooden_hoe"));
    private final double amount = 1.3;
    private final double amount2 = 0.78;
    private final double amount3 = 2.3400000000000003;
    private final double amount4 = 0.39;
    private final double amount5 = 1.1700000000000002;

    private void onEventPreMotion(EventPreMotion event) {
        if (this.mc.currentScreen == null && this.getBedwarsStatus() == 3) {
            String str = this.target != null ? this.target.getName() : "";
            this.target = null;
            this.vec3 = null;
            EntityPlayerSP player = this.mc.thePlayer;
            ItemStack heldItem = player.getHeldItem();
            String str2 = heldItem != null ? this.computeString(heldItem) : "";
            int n = heldItem != null ? heldItem.getItemDamage() : 1;
            if (heldItem != null) {
                heldItem.getMaxDamage();
            } else {
            }

            this.count2 = (int)this.predictionSetting.computeDouble() + 1;
            boolean enabled = this.holdRightClickSetting.isEnabled();
            boolean keyDown = this.mc.gameSettings.keyBindUseItem.isKeyDown();
            boolean flag2 = n == 0 && this.itemSet2.contains(str2) && (!enabled || enabled && keyDown);
            if (!flag2) {
                this.count3 = 0;
            } else {
                for (Entity entity : this.getClosestEntities(4)) {
                    Object[] objs = this.computeObjectArray(entity);
                    boolean flag3 = (Boolean)objs[0];
                    if (flag3) {
                        this.target = entity;
                        this.vec3 = (Vec3)objs[1];
                        break;
                    }
                }

                String str3 = this.target != null ? this.target.getName() : "";
                if (this.target == null || !str.equals(str3)) {
                    this.count3 = 0;
                }

                float[] floats = new float[]{this.factor, this.factor2};
                if (this.target != null) {
                    floats = this.computeFloatArray(this.vec3);
                }

                int n2 = (int)Math.floor(this.factor / 360.0F);
                float f = floats[0] + 360 * n2;
                if (f < this.factor - 180.0F) {
                    f += 360.0F;
                } else if (f > this.factor + 180.0F) {
                    f -= 360.0F;
                }

                float f2 = f - this.factor;
                float f3 = floats[1] - this.factor2;
                if (this.target != null) {
                    float f4 = this.factor + (Math.abs(f2) >= 0.1 ? f2 : 0.0F);
                    float f5 = this.factor2 + (Math.abs(f3) >= 0.1 ? f3 : 0.0F);
                    player.rotationYaw = f4;
                    player.rotationPitch = f5;
                    if (this.count3++ > 0 && !this.flag && n == 0) {
                        this.mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, heldItem, 0.0F, 0.0F, 0.0F));
                    }
                } else if (this.spinBotSetting.isEnabled()) {
                    player.rotationYaw = (float)(Math.random() * 360.0 - 180.0);
                    player.rotationPitch = (float)(Math.random() * 180.0 - 90.0);
                }
            }
        }
    }

    private void process() {
        this.flag = false;
    }

    private double computeDouble() {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        if (heldItem != null && this.itemSet2.contains(this.computeString(heldItem))) {
            for (String str : heldItem.getTooltip(this.mc.thePlayer, false)) {
                String str2 = this.stripColorCodes(str);
                if (str2.contains("Range: ")) {
                    try {
                        return Double.parseDouble(str2.split("Range: ")[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            return 50.0;
        }
        return 0.0;
    }

    private void sendMessage(String str) {
        try {
            MessageUtils.x("[" + this.getName() + "] " + str);
        } catch (Exception ignored) {
        }
    }

    private String computeString(String str) {
        return str.length() >= 2 ? str.substring(0, 2) : "";
    }

    private Vec3[] computeVec3Array(Entity entity) {
        Vec3 positionVector = entity.getPositionVector();
        Vec3 vec = new Vec3(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ);
        Vec3 vec2 = positionVector.subtract(vec);
        BlockPos pos = new BlockPos(positionVector);
        boolean flag = this.mc.theWorld.getBlockState(pos).getBlock().getMaterial().isLiquid() || this.mc.theWorld.isAirBlock(pos);
        if (flag) {
            vec2 = new Vec3(vec2.xCoord * 0.91, vec2.yCoord * 0.91, vec2.zCoord * 0.91);
        }

        Vec3 vec3 = positionVector.addVector(vec2.xCoord * this.count2, vec2.yCoord * (this.count2 / 4.0), vec2.zCoord * this.count2);
        return new Vec3[]{vec3.addVector(-0.39, 0.0, -0.39), vec3.addVector(0.39, 2.3400000000000003, 0.39)};
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.target = null;
        this.vec3 = null;
        this.count3 = 0;
        this.sendMessage("Disabled");
    }

    private float[] computeFloatArray(Vec3 vec) {
        EntityPlayerSP player = this.mc.thePlayer;
        Vec3 vec2 = player.getPositionVector().addVector(0.0, player.getEyeHeight(), 0.0);
        double d = vec.xCoord - vec2.xCoord;
        double d2 = vec.yCoord - vec2.yCoord;
        double d3 = vec.zCoord - vec2.zCoord;
        double dist = Math.sqrt(d * d + d3 * d3);
        float f = (float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F;
        float deg = (float)Math.toDegrees(-Math.atan2(d2, dist));
        float f2 = (f % 360.0F + 360.0F) % 360.0F;
        if (f2 > 180.0F) {
            f2 -= 360.0F;
        }

        return new float[]{f2, deg};
    }

    private boolean matchesVec3(Vec3 vec, Vec3[] vecs, double d) {
        float[] floats = this.computeFloatArray(vec);

        for (Vec3 vec2 : this.computeList(d, floats[0], floats[1], 0.2)) {
            BlockPos pos = new BlockPos(vec2);
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            String str = Block.blockRegistry.getNameForObject(block).toString();
            if (this.matchesVec3(vec2, vecs)) {
                return true;
            }

            if (!this.itemSet.contains(str)) {
                return false;
            }
        }

        return false;
    }

    private boolean matchesVec3(Vec3 vec, Vec3[] vecs) {
        Vec3 vec2 = vecs[0];
        Vec3 vec3 = vecs[1];
        return vec.xCoord >= vec2.xCoord
            && vec.xCoord <= vec3.xCoord
            && vec.yCoord >= vec2.yCoord
            && vec.yCoord <= vec3.yCoord
            && vec.zCoord >= vec2.zCoord
            && vec.zCoord <= vec3.zCoord;
    }

    private List<Entity> getClosestEntities(int n) {
        EntityPlayerSP player = this.mc.thePlayer;
        Vec3 positionVector = player.getPositionVector();
        String str = this.computeString(player.getDisplayName().getUnformattedText());
        List<Object[]> list = new ArrayList<>();

        for (Object obj : EntityCache.getEntries()) {
            Entity entity = (Entity)obj;
            if (entity instanceof EntityPlayer player2) {
                String text = player2.getDisplayName().getUnformattedText();
                String str2 = player2.getUniqueID().toString();
                char ch = str2.charAt(14);
                if (entity != player
                    && (ch == '4' || ch == '1')
                    && !text.startsWith(str)
                    && (!entity.isInvisible() || !text.startsWith("§c") || text.contains(" "))) {
                    double d = positionVector.squareDistanceTo(entity.getPositionVector());
                    list.add(new Object[]{entity, d});
                }
            }
        }

        list.sort(Comparator.comparingDouble(objs -> (Double)objs[1]));
        List list2 = new ArrayList();

        for (int i = 0; i < Math.min(n, list.size()); i++) {
            list2.add((Entity)((Object[])list.get(i))[0]);
        }

        return list2;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion event2) {
                    if (event2.getState() == EventState.PRE) {
                        this.onEventPreMotion(event2);
                    }
                } else if (event instanceof EventPostMotion) {
                    this.process();
                } else if (event instanceof EventPacketSend) {
                    this.onEventPacketSend((EventPacketSend)event);
                } else if (event instanceof EventRender2D) {
                    this.onEventRender2D((EventRender2D)event);
                }
            }
        }
    }

    private String computeString(ItemStack stack) {
        String unlocalizedName = stack.getItem().getUnlocalizedName();
        if (unlocalizedName.startsWith("item.")) {
            unlocalizedName = unlocalizedName.substring(5);
        }

        return unlocalizedName;
    }

    public GunAimbot() {
        super("GunAimbot", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.holdRightClickSetting, this.spinBotSetting, this.predictionSetting});
    }

    private void onEventPacketSend(EventPacketSend event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer)event.getPacket();
            if (packet.getRotating()) {
                this.factor = packet.getYaw();
                this.factor2 = packet.getPitch();
            }
        } else if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            this.flag = true;
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            if (this.mc.thePlayer != null) {
                this.factor = this.mc.thePlayer.rotationYaw;
                this.factor2 = this.mc.thePlayer.rotationPitch;
            }

            this.count3 = 0;
            this.target = null;
            this.vec3 = null;
            this.flag = false;
            this.sendMessage("Enabled - Targeting guns in Armed Bedwars");
        }
    }

    private Vec3 computeVec3(Vec3 vec, ScaledResolution sr, float f) {
        EntityPlayerSP player = this.mc.thePlayer;
        double d = vec.xCoord - (player.lastTickPosX + (player.posX - player.lastTickPosX) * f);
        double d2 = vec.yCoord - (player.lastTickPosY + (player.posY - player.lastTickPosY) * f);
        double d3 = vec.zCoord - (player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * f);
        double dist = Math.sqrt(d * d + d2 * d2 + d3 * d3);
        if (dist > 100.0) {
            return null;
        }
        double d4 = sr.getScaledWidth() / 2.0 + d * sr.getScaledWidth() / (dist + 1.0);
        double d5 = sr.getScaledHeight() / 2.0 - d2 * sr.getScaledHeight() / (dist + 1.0);
        return new Vec3(d4, d5, 0.5);
    }

    private void onEventRender2D(EventRender2D event) {
        if (this.target != null && this.vec3 != null) {
            ScaledResolution scaledResolution = event.getScaledResolution();
            Vec3 vec = this.computeVec3(this.vec3, scaledResolution, event.getPartialTicks());
            if (vec != null && vec.zCoord >= 0.0 && vec.zCoord < 1.0) {
                double d = 3.0;
                double d2 = vec.xCoord - d;
                double d3 = vec.xCoord + d;
                double d4 = vec.yCoord - d;
                double d5 = vec.yCoord + d;
                GL11.glPushMatrix();
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glLineWidth(3.0F);
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
                GL11.glBegin(GL11.GL_LINES);
                GL11.glVertex2d(d2, vec.yCoord);
                GL11.glVertex2d(d3, vec.yCoord);
                GL11.glVertex2d(vec.xCoord, d4);
                GL11.glVertex2d(vec.xCoord, d5);
                GL11.glEnd();
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
                GL11.glPopMatrix();
            }
        }
    }

    private int getBedwarsStatus() {
        try {
            Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
            if (scoreboard == null) {
                return -1;
            }
            ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
            if (objectiveInDisplaySlot == null) {
                return -1;
            }
            String displayName = objectiveInDisplaySlot.getDisplayName();
            if (!this.stripColorCodes(displayName).startsWith("BED WARS")) {
                return -1;
            }
            ArrayList<String> list = new ArrayList<>();
            scoreboard.getSortedScores(objectiveInDisplaySlot).forEach(score -> list.add(score.getPlayerName()));

            for (String str : list) {
                String str2 = this.stripColorCodes(str);
                if (str2.contains("R Red:") && str2.contains("B Blue:")) {
                    return 3;
                }

                if (str2.equals("Waiting...") || str2.startsWith("Starting in")) {
                    return 2;
                }
            }

            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    private List<Vec3> computeList(double d, float f, float f2, double d2) {
        List list = new ArrayList();
        double rad = Math.toRadians(f);
        double rad2 = Math.toRadians(f2);
        double d3 = -Math.sin(rad) * Math.cos(rad2);
        double d4 = -Math.sin(rad2);
        double d5 = Math.cos(rad) * Math.cos(rad2);
        EntityPlayerSP player = this.mc.thePlayer;
        Vec3 vec = player.getPositionVector().addVector(0.0, player.getEyeHeight(), 0.0);
        double d6 = 0.0;

        while (d6 <= d) {
            list.add(vec.addVector(d3 * d6, d4 * d6, d5 * d6));
            d6 += d2;
        }

        return list;
    }

    private String stripColorCodes(String str) {
        return str.replaceAll("§.", "");
    }

    private Object[] computeObjectArray(Entity entity) {
        double d = this.computeDouble();
        if (d != 0.0 && !(entity.getPositionVector().distanceTo(this.mc.thePlayer.getPositionVector()) > d)) {
            Vec3[] vecs = this.computeVec3Array(entity);
            Vec3 vec = vecs[0].addVector(0.39, 0.0, 0.39);
            Vec3[] vecs2 = new Vec3[]{
                new Vec3(0.0, 2.0, 0.0),
                new Vec3(0.39, 1.1700000000000002, 0.0),
                new Vec3(0.0, 1.1700000000000002, 0.39),
                new Vec3(-0.39, 1.1700000000000002, 0.0),
                new Vec3(0.0, 1.1700000000000002, -0.39),
                new Vec3(0.0, 2.3400000000000003, 0.0),
                new Vec3(0.0, 0.0, 0.0)
            };

            for (Vec3 vec2 : vecs2) {
                Vec3 vec3 = vec.add(vec2);
                if (this.matchesVec3(vec3, vecs, d)) {
                    return new Object[]{true, vec3};
                }
            }

            return new Object[]{false};
        }
        return new Object[]{false};
    }
}
