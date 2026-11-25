package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.pit.hypixel.utility.PodDetection;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.WHelper;
import org.lwjgl.opengl.GL11;

public class PodESP extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final ModeSetting chromaSetting = new ModeSetting("Color Mode", this, "Custom", Arrays.asList("Custom", "Chroma"));
    private final ColorSetting colorSetting = new ColorSetting("Color", this, 0.55F).computeColorSetting(() -> this.chromaSetting.matchesString2("Custom"));
    private final NumberSetting radiusSetting = new NumberSetting("Radius", this, 0.55, 0.25, 1.5, 0.05);
    private final NumberSetting lineWidthSetting = new NumberSetting("Line Width", this, 2.0, 1.0, 5.0, 0.5);
    private static final long timestamp = 3500L;

    private void handleEntityPlayer(EntityPlayer player, float f) {
        double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * f - mc.getRenderManager().viewerPosX;
        double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * f - mc.getRenderManager().viewerPosY + 0.035;
        double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * f - mc.getRenderManager().viewerPosZ;
        Color color = this.chromaSetting.matchesString2("Chroma")
            ? Color.getHSBColor((float)(System.currentTimeMillis() % timestamp) / 3500.0F, 1.0F, 1.0F)
            : this.colorSetting.computeColor();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth((float)this.lineWidthSetting.computeDouble());
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.95F);
        this.handleDouble(d, d2, d3, this.radiusSetting.computeDouble(), 64);
        GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.35F);
        this.handleDouble(d, d2 + 0.018, d3, this.radiusSetting.computeDouble() * 0.76, 48);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    private void handleDouble(double d, double d2, double d3, double d4, int n) {
        GL11.glBegin(GL11.GL_LINE_LOOP);

        for (int i = 0; i < n; i++) {
            double d5 = (Math.PI * 2) * i / n;
            GL11.glVertex3d(d + Math.cos(d5) * d4, d2, d3 + Math.sin(d5) * d4);
        }

        GL11.glEnd();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventRender3D && mc.thePlayer != null && mc.theWorld != null) {
            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer && (!this.kosOnlySetting.isEnabled() || WHelper.matchesString(playerEntity.getName())) && this.matchesEntityPlayer(playerEntity)) {
                    this.handleEntityPlayer(playerEntity, ((EventRender3D)event).getFactor());
                }
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        return player != null && player.inventory != null && player.inventory.armorInventory != null && player.inventory.armorInventory.length > 1
            ? PodDetection.computeInt(player.inventory.armorInventory[1]) > 0
            : false;
    }

    public PodESP() {
        super("PodESP", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.kosOnlySetting, this.chromaSetting, this.colorSetting, this.radiusSetting, this.lineWidthSetting});
    }
}
