package org.brutality.module.impl.pit.hypixel.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class SoulripperDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 70.0, 0.0, 1200.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting showESPSetting = new BooleanSetting("Show ESP", this, true);
    private EntityPlayer entityPlayer = null;
    private int count = 0;
    private String text = "Soul: +0%";
    private String text2 = "";

    private void process4() {
        if (this.count > 0) {
            this.text = "+" + this.count * 15 + "%";
        } else {
            this.text = "+0%";
        }
    }

    private void process3() {
        if (this.entityPlayer != null && !this.entityPlayer.isDead) {
            Minecraft mc = Minecraft.getMinecraft();
            double d = this.entityPlayer.lastTickPosX + (this.entityPlayer.posX - this.entityPlayer.lastTickPosX) * mc.timer.renderPartialTicks;
            double d2 = this.entityPlayer.lastTickPosY + (this.entityPlayer.posY - this.entityPlayer.lastTickPosY) * mc.timer.renderPartialTicks;
            double d3 = this.entityPlayer.lastTickPosZ + (this.entityPlayer.posZ - this.entityPlayer.lastTickPosZ) * mc.timer.renderPartialTicks;
            double d4 = d - mc.getRenderManager().viewerPosX;
            double d5 = d2 - mc.getRenderManager().viewerPosY;
            double d6 = d3 - mc.getRenderManager().viewerPosZ;
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0F);
            float f = 0.6F + 0.3F * (float)Math.sin(System.currentTimeMillis() / 200.0);
            GL11.glColor4f(0.67F, 0.0F, 1.0F, f);
            AxisAlignedBB box = new AxisAlignedBB(d4 - 0.5, d5, d6 - 0.5, d4 + 0.5, d5 + this.entityPlayer.height, d6 + 0.5);
            RenderGlobal.func_181563_a(box, 0, 0, 1, (int)f);
            GL11.glColor4f(0.67F, 0.0F, 1.0F, f * 0.3F);
            this.handleAxisAlignedBB(box);
            GL11.glLineWidth(4.0F);
            GL11.glColor4f(0.67F, 0.0F, 1.0F, f * 0.5F);
            RenderGlobal.func_181563_a(box, 0, 0, 1, (int)(f * 0.5F));
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } else {
            this.process();
        }
    }

    private void handleAxisAlignedBB(AxisAlignedBB box) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.maxX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.maxZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.maxY, box.minZ).func_181675_d();
        worldRenderer.func_181662_b(box.minX, box.minY, box.minZ).func_181675_d();
        tessellator.draw();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof AttackEvent) {
                if (((AttackEvent)event).getEntityPlayer() == Minecraft.getMinecraft().thePlayer
                    && this.matchesItemStack(((AttackEvent)event).computeItemStack())
                    && ((Object)((AttackEvent)event).getEntityPlayer2()) instanceof EntityPlayer player) {
                    if (this.entityPlayer != null && this.entityPlayer.equals(player)) {
                        this.count = Math.min(this.count + 1, 5);
                    } else {
                        this.entityPlayer = player;
                        this.count = 1;
                        this.text2 = player.getName();
                    }

                    this.process4();
                }
            } else if (event instanceof ChatEvent) {
                String text = ((ChatEvent)event).message.getUnformattedText();
                if (text.contains("DEATH!")) {
                    this.process();
                }
            }
        }
    }

    private String computeString() {
        return this.count > 0 && !this.text2.isEmpty() ? "Soul: +" + this.count * 15 + "% (" + this.text2 + ")" : "Soul: +0%";
    }

    private void handleMinecraft(Minecraft mc, float f, float f2, boolean flag, boolean flag2) {
        if (flag) {
            String str = this.computeString();
            computeFontRenderer(SoulripperDamage.class).drawStringWithShadow("Soul: ", f, f2, 16777215);
            float textWidth = computeFontRenderer(SoulripperDamage.class).getStringWidth("Soul: ");
            String str2 = "+" + this.count * 15 + "%";
            computeFontRenderer(SoulripperDamage.class).drawStringWithShadow(str2, f + textWidth, f2, 11141375);
            if (!this.text2.isEmpty()) {
                float textWidth2 = computeFontRenderer(SoulripperDamage.class).getStringWidth(str2);
                computeFontRenderer(SoulripperDamage.class).drawStringWithShadow(" (" + this.text2 + ")", f + textWidth + textWidth2, f2, 8947848);
            }
        } else if (flag2 && this.showInChatSetting.isEnabled()) {
            computeFontRenderer(SoulripperDamage.class).drawStringWithShadow("Soul: ", f, f2, 16777215);
            computeFontRenderer(SoulripperDamage.class)
                .drawStringWithShadow("+0%", f + computeFontRenderer(SoulripperDamage.class).getStringWidth("Soul: "), f2, 11141375);
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.hasDisplayName()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null) {
                return tagCompound.toString().contains("Spireblade");
            }
        }

        return false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
        this.process();
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        float f = (float)this.xPosSetting.computeDouble();
        float f2 = (float)this.yPosSetting.computeDouble();
        boolean flag = this.checkState();
        boolean flag2 = mc.currentScreen instanceof GuiChat;
        boolean flag3 = flag || flag2 && this.showInChatSetting.isEnabled();
        if (!flag3) {
            HudDragHelper.handleObject(this);
        } else {
            if (flag2) {
                int[] ints = HudDragHelper.computeIntArray();
                int n = ints[0];
                int n2 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this)) {
                        String str = this.computeString();
                        int textWidth = computeFontRenderer(SoulripperDamage.class).getStringWidth(str);
                        int FONT_HEIGHT = computeFontRenderer(SoulripperDamage.class).FONT_HEIGHT;
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
                        f = ints2[0];
                        f2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            this.handleMinecraft(mc, f, f2, flag, flag2);
            if (this.showESPSetting.isEnabled() && this.entityPlayer != null && flag) {
                this.process3();
            }
        }
    }

    public SoulripperDamage() {
        super("SoulripperDamage", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showInChatSetting, this.showESPSetting});
    }

    private boolean checkState() {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        return player != null && this.matchesItemStack(player.getHeldItem());
    }

    private void process() {
        this.entityPlayer = null;
        this.count = 0;
        this.text2 = "";
        this.text = "+0%";
    }
}
