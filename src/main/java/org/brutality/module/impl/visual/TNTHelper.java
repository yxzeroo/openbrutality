package org.brutality.module.impl.visual;

import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class TNTHelper extends Module {
    public double damage = 0.0;
    private ColorSetting explosionColorSetting = new ColorSetting("Explosion Color", this, 0.0F, 1.0F, 1.0F);
    private final BooleanSetting showFuseSetting = new BooleanSetting("Show Fuse", this, true);
    private static final long timestamp = -8284465565741918891L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                this.damage = 0.0;
                ResetColorHelper.handleDouble(this.damage, this.explosionColorSetting);
                if (this.showFuseSetting.isEnabled()) {
                    this.process();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.damage = 0.0;
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            float renderPartialTicks = mc.timer.renderPartialTicks;

            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityTNTPrimed entity2 && entity2.fuse > 0) {
                    String str = String.format(Locale.ROOT, "%.2fs", entity2.fuse / 20.0);
                    double d = entity2.lastTickPosX + (entity2.posX - entity2.lastTickPosX) * renderPartialTicks - mc.getRenderManager().viewerPosX;
                    double d2 = entity2.lastTickPosY + (entity2.posY - entity2.lastTickPosY) * renderPartialTicks - mc.getRenderManager().viewerPosY + 1.0;
                    double d3 = entity2.lastTickPosZ + (entity2.posZ - entity2.lastTickPosZ) * renderPartialTicks - mc.getRenderManager().viewerPosZ;
                    GL11.glPushMatrix();
                    GL11.glTranslated(d, d2, d3);
                    GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
                    float f = 0.025F;
                    GL11.glScalef(-f, -f, f);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    int textWidth = computeFontRenderer(TNTHelper.class).getStringWidth(str);
                    computeFontRenderer(TNTHelper.class).drawStringWithShadow(str, (~textWidth + 1) / 2, 0.0F, (int)timestamp);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    public TNTHelper() {
        super("TNT Helper", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(new Setting[]{this.explosionColorSetting, this.showFuseSetting});
    }
}
