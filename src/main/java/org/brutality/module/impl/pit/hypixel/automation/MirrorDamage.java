package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MirrorDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static MirrorDamage instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 40.0, 0.0, 1200.0, 1.0);
    private int count = 0;
    private float lastHealth = 0.0F;
    private float factor = 0.0F;
    private final List<MirrorDamage.Entry> entries = new ArrayList<>();
    private static final double amount = 1.0;
    private static final double amount2 = 1.5;
    private static final double amount3 = 1.8;
    private static final long timestamp = 150L;
    private static final double amount4 = 2.5;
    private static final double amount5 = 0.7;
    private static final int count2 = new Color(255, 255, 255).getRGB();
    private static final List<String> entries2 = Arrays.asList("Gamble", "Perun's Wrath", "True Shot", "Devil Chicks", "Explosive");

    private EntityPlayer computeEntityPlayer() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            EntityPlayer player = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (EntityPlayer player2 : EntityCache.getEntries()) {
                if (player2 != this.mc.thePlayer && this.mc.thePlayer.hurtTime >= 8) {
                    double dist = this.mc.thePlayer.getDistanceToEntity(player2);
                    if (dist < 6.0 && dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                        player = player2;
                    }
                }
            }

            return player;
        }
        return null;
    }

    private void handleString(String str, Vec3 vec, float f, int n, float f2) {
        RenderManager renderManager = this.mc.getRenderManager();
        FontRenderer fontRenderer = computeFontRenderer(MirrorDamage.class);
        double d = vec.xCoord - renderManager.viewerPosX;
        double d2 = vec.yCoord - renderManager.viewerPosY;
        double d3 = vec.zCoord - renderManager.viewerPosZ;
        float f3 = this.mc.thePlayer.prevRotationYaw + (this.mc.thePlayer.rotationYaw - this.mc.thePlayer.prevRotationYaw) * f2;
        float f4 = this.mc.thePlayer.prevRotationPitch + (this.mc.thePlayer.rotationPitch - this.mc.thePlayer.prevRotationPitch) * f2;
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d2, d3);
        GlStateManager.rotate(-f3, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(f4, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-f / 25.0F, -f / 25.0F, f / 25.0F);
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        int textWidth = fontRenderer.getStringWidth(str);
        int n2 = textWidth / 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        int n3 = (0xFF | ~(n >> 24)) - ~(n >> 24);
        int n4 = n3 / 2 << 24;
        worldRenderer.func_181662_b(-n2 - 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
        worldRenderer.func_181662_b(-n2 - 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
        worldRenderer.func_181662_b(n2 + 1, 8.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
        worldRenderer.func_181662_b(n2 + 1, -1.0, 0.0).func_181666_a(0.0F, 0.0F, 0.0F, n4 >> 24).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        fontRenderer.drawString(str, -n2, 0, (16777215 | ~n) - ~n + (536870912 & ~((16777215 | ~n) - ~n)));
        fontRenderer.drawString(str, -n2, 0, n);
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lastHealth = this.mc.thePlayer != null ? this.mc.thePlayer.getHealth() + this.mc.thePlayer.getAbsorptionAmount() : 0.0F;
        this.factor = 0.0F;
        this.entries.clear();
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            boolean flag2 = this.count > 0;
            if (!flag2 && !flag) {
                HudDragHelper.handleObject(this);
            } else {
                String str;
                if (flag2) {
                    EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                    EnumChatFormatting WHITE2 = EnumChatFormatting.WHITE;
                    String str2 = String.format("%.1f", this.factor);
                    EnumChatFormatting enumChatFormatting = WHITE2;
                    str = WHITE + "Mirror: " + enumChatFormatting + str2;
                } else {
                    str = EnumChatFormatting.WHITE + "Mirror: " + EnumChatFormatting.GRAY + "Not Equipped";
                }

                if (flag) {
                    this.handleFloat(f, f2, str);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                computeFontRenderer(MirrorDamage.class).drawStringWithShadow(str, f, f2, 16777215);
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack heldItem = player.getHeldItem();
        if (heldItem == null) {
            return false;
        }
        if (!(heldItem.getItem() instanceof ItemSword) && !(heldItem.getItem() instanceof ItemBow)) {
            return false;
        }
        NBTTagCompound tagCompound = heldItem.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("display", 10)) {
            NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
            if (compoundTag.hasKey("Lore", 9)) {
                NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    String stringTagAt = tagList.getStringTagAt(i);

                    for (String str : entries2) {
                        if (stringTagAt.contains(str)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.entries.clear();
        this.factor = 0.0F;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (this.mc.thePlayer == null) {
                    return;
                }

                this.count = this.computeInt(this.mc.thePlayer);
                float f = this.mc.thePlayer.getHealth() + this.mc.thePlayer.getAbsorptionAmount();
                if (this.lastHealth > 0.0F && f < this.lastHealth && this.count > 0) {
                    float f2 = this.lastHealth - f;
                    EntityPlayer player = this.computeEntityPlayer();
                    if (player != null && this.matchesEntityPlayer(player)) {
                        float f3 = 0.0F;
                        if (this.count == 2) {
                            f3 = f2 * 0.25F;
                        } else if (this.count == 3) {
                            f3 = f2 * 0.5F;
                        }

                        if (f3 > 0.0F) {
                            this.factor += f3;
                            this.handleEntityPlayer(player, f3);
                        }
                    }
                }

                this.lastHealth = f;
            } else if (event instanceof EventUpdate) {
                this.handleUpdate();
            } else if (event instanceof EventRender3D) {
                this.handleFloat(((EventRender3D)event).getFactor());
            }
        }
    }

    private void handleUpdate() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            Vec3 vec = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
            long now = System.currentTimeMillis();
            long l = 1500L;
            Iterator it = this.entries.iterator();

            while (it.hasNext()) {
                MirrorDamage.Entry entry = (MirrorDamage.Entry)it.next();
                long l2 = now - entry.time;
                if (l2 > l + 150L) {
                    it.remove();
                } else {
                    entry.amount2 = entry.amount;
                    entry.amount = vec.distanceTo(entry.position);
                }
            }

            this.entries.sort((entry2, entry3) -> Double.compare(entry3.amount, entry2.amount));
        }
    }

    @Generated
    public static MirrorDamage getInstance() {
        return instance;
    }

    private void handleEntityPlayer(EntityPlayer player, float f) {
        if (player != null) {
            Vec3 vec = new Vec3(player.posX, player.posY + 1.8, player.posZ);
            Vec3 vec2 = new Vec3(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ);
            double d = vec2.distanceTo(vec);
            String str = this.computeString(Math.round(f * 10.0) / 10.0);
            if (str.endsWith("0") && str.contains(".")) {
                str = str.substring(0, str.length() - 2);
            }

            MirrorDamage.Entry entry = new MirrorDamage.Entry(vec, System.currentTimeMillis(), str, d);
            this.entries.add(entry);
        }
    }

    private void handleFloat(float f) {
        if (!this.entries.isEmpty()) {
            long now = System.currentTimeMillis();
            long l = 1500L;
            Iterator it = this.entries.iterator();

            while (it.hasNext()) {
                MirrorDamage.Entry entry = (MirrorDamage.Entry)it.next();
                long l2 = now - entry.time;
                if (l2 > l + 150L) {
                    it.remove();
                } else {
                    double amount = entry.amount;
                    if (!(amount > 25.0)) {
                        double d = entry.amount2 + (amount - entry.amount2) * f;
                        int n = 255;
                        if (l2 > l) {
                            double d2 = 1.0 - (l2 - l) / 150.0;
                            n = (int)(230.0 * d2 + 25.0);
                        }

                        if (n < 26) {
                            it.remove();
                        } else {
                            if (d < 2.5) {
                                double d3 = (d - 0.7) / 1.8;
                                int n2 = (int)(25.0 + 230.0 * Math.max(d3, 0.0));
                                n = Math.min(n, n2);
                            }

                            int n3 = (n << 24) + ((16777215 | ~count2) - ~count2 & ~(n << 24));
                            this.handleString(entry.text, entry.position, 1.0F, n3, f);
                        }
                    }
                }
            }
        }
    }

    private String computeString(double d) {
        return d == (long)d ? Long.toString((long)d) : Double.toString(d);
    }

    public MirrorDamage() {
        super("Mirror Damage", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting});
        instance = this;
    }

    private int computeInt(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);
                        if (stringTagAt.contains("Mirror")) {
                            for (int j = i; j < Math.min(i + 3, tagList.tagCount()); j++) {
                                String stringTagAt2 = tagList.getStringTagAt(j);
                                if (stringTagAt2.contains("Level: 3")) {
                                    return 3;
                                }

                                if (stringTagAt2.contains("Level: 2")) {
                                    return 2;
                                }

                                if (stringTagAt2.contains("Level: 1")) {
                                    return 1;
                                }
                            }

                            return 1;
                        }
                    }
                }
            }
        }

        return 0;
    }

    private void handleFloat(float f, float f2, String str) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = computeFontRenderer(MirrorDamage.class).getStringWidth(str);
                int FONT_HEIGHT = computeFontRenderer(MirrorDamage.class).FONT_HEIGHT;
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                    HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
            }
        }
    }

    public static class Entry {
        public Vec3 position;
        public long time;
        public String text;
        public double amount;
        public double amount2;

        public Entry(Vec3 position, long time, String text, double amount) {
            this.position = position;
            this.time = time;
            this.text = text;
            this.amount = amount;
            this.amount2 = amount;
        }
    }
}
