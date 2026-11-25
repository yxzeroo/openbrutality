package org.brutality.module.impl.visual;

import java.util.List;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public final class ExposedHitboxESP extends Module {
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 4.2, 1.0, 8.0, 0.1);
    private final List<EntityPlayer> entries = new java.util.ArrayList<>();
    private Object object;

    @Override
    public void onDisable() {
        this.object = null;
        this.entries.clear();
        super.onDisable();
    }

    public ExposedHitboxESP() {
        super("Exposed Hitbox ESP", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.rangeSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof EventTick event2 && event2.getState() == EventState.PRE) {
                this.entries.clear();
                this.object = mc.theWorld;
                double d = this.rangeSetting.computeDouble();

                for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                    if (playerEntity != mc.thePlayer
                        && playerEntity.isEntityAlive()
                        && !playerEntity.isSpectator()
                        && mc.thePlayer.getDistanceSqToEntity(playerEntity) <= (d + 2.0) * (d + 2.0)
                        && !mc.thePlayer.canEntityBeSeen(playerEntity)
                        && KillAura.matchesEntityLivingBase3(playerEntity, d)) {
                        this.entries.add(playerEntity);
                    }
                }
            } else if (event instanceof EventRender3D && this.object == mc.theWorld && !this.entries.isEmpty()) {
                RenderManager renderManager = mc.getRenderManager();
                double renderPartialTicks = mc.timer.renderPartialTicks;
                GL11.glPushAttrib(8197);

                try {
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glColor4f(0.3F, 1.0F, 0.82F, 1.0F);
                    GL11.glLineWidth(1.5F);

                    for (EntityPlayer player : this.entries) {
                        if (player.isEntityAlive()) {
                            AxisAlignedBB box = player.getEntityBoundingBox()
                                .offset(
                                    (player.lastTickPosX - player.posX) * (1.0 - renderPartialTicks) - renderManager.viewerPosX,
                                    (player.lastTickPosY - player.posY) * (1.0 - renderPartialTicks) - renderManager.viewerPosY,
                                    (player.lastTickPosZ - player.posZ) * (1.0 - renderPartialTicks) - renderManager.viewerPosZ
                                );
                            ResetColorHelper.handleAxisAlignedBB2(box);
                        }
                    }
                } finally {
                    GL11.glPopAttrib();
                }
            }
        }
    }
}
