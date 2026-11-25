package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.clickgui.classic.KillAuraComponent;
import org.brutality.gui.GuiComponent2;

public class GuiInventory extends InventoryEffectRenderer {
    private float oldMouseX;
    private float oldMouseY;

    public GuiInventory(EntityPlayer p_i1094_1_) {
        super(p_i1094_1_.inventoryContainer);
        this.allowUserInput = true;
    }

    @Override
    public void updateScreen() {
        if (mc.playerController.isInCreativeMode()) {
            mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
        }

        this.updateActivePotionEffects();
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        if (mc.playerController.isInCreativeMode()) {
            mc.displayGuiScreen(new GuiContainerCreative(mc.thePlayer));
        } else {
            super.initGui();
            this.buttonList.add(new GuiComponent2(9001, "Kill Aura", "Kill Aura"));
            this.buttonList.add(new GuiComponent2(9002, "Chest Stealer", "Chest Stealer"));
            this.buttonList.add(new GuiComponent2(9003, "Inventory Manager", "Inventory Manager"));
            KillAuraComponent.handleList(this.buttonList, this.width);
        }
    }

    @Override
    protected boolean clickContainerControls(int mouseX, int mouseY, int mouseButton) {
        for (GuiButton button : this.buttonList) {
            if (button instanceof GuiComponent2 gui && gui.matchesInt(mouseX, mouseY)) {
                if (mouseButton == 0) {
                    gui.playPressSound(mc.getSoundHandler());
                    gui.process();
                }

                return true;
            }
        }

        return false;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.fontRendererObj.drawString(I18n.format("container.crafting"), 86, 16, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        KillAuraComponent.handleList(this.buttonList, this.width);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.oldMouseX = mouseX;
        this.oldMouseY = mouseY;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(inventoryBackground);
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
        drawEntityOnScreen(guiLeft + 51, guiTop + 75, 30, guiLeft + 51 - this.oldMouseX, guiTop + 75 - 50 - this.oldMouseY, mc.thePlayer);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX, (float)posY, 50.0F);
        GlStateManager.scale((float)(-scale), (float)scale, (float)scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float renderYawOffset = ent.renderYawOffset;
        float yaw = ent.rotationYaw;
        float pitch = ent.rotationPitch;
        float prevRotationYawHead = ent.prevRotationYawHead;
        float rotationYawHead = ent.rotationYawHead;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
        renderManager.setPlayerViewY(180.0F);
        renderManager.setRenderShadow(false);
        renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0F, 1.0F);
        renderManager.setRenderShadow(true);
        ent.renderYawOffset = renderYawOffset;
        ent.rotationYaw = yaw;
        ent.rotationPitch = pitch;
        ent.prevRotationYawHead = prevRotationYawHead;
        ent.rotationYawHead = rotationYawHead;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(new GuiAchievements(this, mc.thePlayer.getStatFileWriter()));
        }

        if (button.id == 1) {
            mc.displayGuiScreen(new GuiStats(this, mc.thePlayer.getStatFileWriter()));
        }
    }
}
