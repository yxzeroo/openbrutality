package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class ExplosionRadius extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Color color = new Color(170, 0, 170);
    private final BooleanSetting highlightPlayersSetting = new BooleanSetting("Highlight Players", this, true);

    private void handleList(List<double[]> list) {
        if (list.size() >= 2) {
            GlStateManager.pushMatrix();
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glLineWidth(2.0F);
            GlStateManager.color(1.0F, 0.4F, 1.0F, 0.85F);
            GL11.glBegin(GL11.GL_LINE_STRIP);

            for (double[] doubles : list) {
                GL11.glVertex3d(
                    doubles[0] - mc.getRenderManager().viewerPosX, doubles[1] - mc.getRenderManager().viewerPosY, doubles[2] - mc.getRenderManager().viewerPosZ
                );
            }

            GL11.glEnd();
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    private void handleWorldRenderer(WorldRenderer worldRenderer, double d, double d2, double d3, double d4, double d5, double d6) {
        double[] doubles = new double[]{d, d2, d2, d};
        double[] doubles2 = new double[]{d3, d3, d4, d4};
        double[] doubles3 = new double[]{d5, d6, d6, d5};

        for (int i = 0; i < 4; i++) {
            worldRenderer.func_181662_b(doubles[i], doubles2[i], doubles3[i]).func_181675_d();
        }
    }

    private void handleBlockPos(BlockPos pos) {
        byte b = 3;
        double d = pos.getX() - mc.getRenderManager().viewerPosX;
        double d2 = pos.getY() - mc.getRenderManager().viewerPosY;
        double d3 = pos.getZ() - mc.getRenderManager().viewerPosZ;
        AxisAlignedBB box = new AxisAlignedBB(d - b, d2 - b, d3 - b, d + b + 1.0, d2 + b + 1.0, d3 + b + 1.0);
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        float f = color.getRed() / 255.0F;
        float f2 = color.getGreen() / 255.0F;
        float f3 = color.getBlue() / 255.0F;
        GlStateManager.color(f, f2, f3, 0.35F);
        this.handleAxisAlignedBB(box);
        GlStateManager.color(f, f2, f3, 0.9F);
        this.handleAxisAlignedBB2(box);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public ExplosionRadius() {
        super("Explosion Radius", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.highlightPlayersSetting});
    }

    private void handleBlockPos2(BlockPos pos) {
        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player != mc.thePlayer && !player.isDead) {
                double d = Math.abs(player.posX - (pos.getX() + 0.5));
                double d2 = Math.abs(player.posY - (pos.getY() + 0.5));
                double d3 = Math.abs(player.posZ - (pos.getZ() + 0.5));
                if (d <= 3.0 && d2 <= 3.0 && d3 <= 3.0) {
                    this.handleEntityPlayer(player);
                }
            }
        }
    }

    private BlockPos computeBlockPos(float f, List<double[]> list) {
        float f2 = 0.05F;
        float f3 = f * 3.0F;
        double d = mc.thePlayer.posX - MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        double d2 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.1;
        double d3 = mc.thePlayer.posZ - MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
        double d4 = -MathHelper.sin(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
            * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI);
        double d5 = -MathHelper.sin(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI);
        double d6 = MathHelper.cos(mc.thePlayer.rotationYaw / 180.0F * (float) Math.PI)
            * MathHelper.cos(mc.thePlayer.rotationPitch / 180.0F * (float) Math.PI);
        double dist = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        if (dist < 1.0E-7) {
            dist = 1.0E-7;
        }

        double d7 = d4 / dist * f3;
        double d8 = d5 / dist * f3;
        double d9 = d6 / dist * f3;
        list.add(new double[]{d, d2, d3});

        for (int i = 0; i < 300; i++) {
            Vec3 vec = new Vec3(d, d2, d3);
            Vec3 vec2 = new Vec3(d + d7, d2 + d8, d3 + d9);
            MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(vec, vec2, false, true, false);
            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && hit.getBlockPos() != null) {
                list.add(new double[]{hit.getBlockPos().getX() + 0.5, hit.getBlockPos().getY(), hit.getBlockPos().getZ() + 0.5});
                return hit.getBlockPos();
            }

            d += d7;
            d2 += d8;
            d3 += d9;
            d7 *= 0.99;
            d5 = d8 * 0.99;
            d9 *= 0.99;
            d8 = d5 - f2;
            list.add(new double[]{d, d2, d3});
            if (d2 < 0.0) {
                break;
            }
        }

        return new BlockPos(d, d2, d3);
    }

    private void handleAxisAlignedBB(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        this.handleWorldRenderer(worldRenderer, box.minX, box.maxX, box.minY, box.minY, box.minZ, box.maxZ);
        this.handleWorldRenderer(worldRenderer, box.minX, box.maxX, box.maxY, box.maxY, box.minZ, box.maxZ);
        this.handleWorldRenderer(worldRenderer, box.minX, box.minX, box.minY, box.maxY, box.minZ, box.maxZ);
        this.handleWorldRenderer(worldRenderer, box.maxX, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ);
        this.handleWorldRenderer(worldRenderer, box.minX, box.maxX, box.minY, box.maxY, box.minZ, box.minZ);
        this.handleWorldRenderer(worldRenderer, box.minX, box.maxX, box.minY, box.maxY, box.maxZ, box.maxZ);
        tessellator.draw();
    }

    private int computeInt(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes") ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
            if (nbt.hasKey("CustomEnchants")) {
                NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                    if ("explosive_bow".equals(compoundTagAt.getString("Key"))) {
                        return compoundTagAt.getInteger("Level");
                    }
                }
            }

            return 0;
        }
        return 0;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    ItemStack heldItem = mc.thePlayer.getHeldItem();
                    if (heldItem != null && heldItem.getItem() instanceof ItemBow) {
                        if (this.computeInt(heldItem) > 0) {
                            float f = mc.thePlayer.getItemInUseCount() / 20.0F;
                            float f2 = (f * f + f * 2.0F) / 3.0F;
                            if (!(f2 < 0.1F)) {
                                if (f2 > 1.0F) {
                                    f2 = 1.0F;
                                }

                                ArrayList list = new ArrayList();
                                BlockPos pos = this.computeBlockPos(f2, list);
                                if (pos != null) {
                                    GL20.glUseProgram(0);
                                    this.handleList(list);
                                    this.handleBlockPos(pos);
                                    if (this.highlightPlayersSetting.isEnabled()) {
                                        this.handleBlockPos2(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleAxisAlignedBB2(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        double[][] doublesArray = new double[][]{
            {box.minX, box.minY, box.minZ},
            {box.maxX, box.minY, box.minZ},
            {box.maxX, box.maxY, box.minZ},
            {box.minX, box.maxY, box.minZ},
            {box.minX, box.minY, box.maxZ},
            {box.maxX, box.minY, box.maxZ},
            {box.maxX, box.maxY, box.maxZ},
            {box.minX, box.maxY, box.maxZ}
        };
        int[][] intsArray = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};

        for (int[] ints : intsArray) {
            worldRenderer.func_181662_b(doublesArray[ints[0]][0], doublesArray[ints[0]][1], doublesArray[ints[0]][2]).func_181675_d();
            worldRenderer.func_181662_b(doublesArray[ints[1]][0], doublesArray[ints[1]][1], doublesArray[ints[1]][2]).func_181675_d();
        }

        tessellator.draw();
    }

    private void handleEntityPlayer(EntityPlayer player) {
        double d = player.posX - mc.getRenderManager().viewerPosX;
        double d2 = player.posY - mc.getRenderManager().viewerPosY;
        double d3 = player.posZ - mc.getRenderManager().viewerPosZ;
        float f = player.width / 2.0F;
        AxisAlignedBB box = new AxisAlignedBB(d - f, d2, d3 - f, d + f, d2 + player.height, d3 + f);
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(0.0F, 1.0F, 1.0F, 0.6F);
        this.handleAxisAlignedBB2(box);
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
