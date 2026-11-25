package org.brutality.module.impl.visual;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.opengl.GL11;

public final class PlayerFOV extends Module {
    private final NumberSetting viewAngleSetting = new NumberSetting("View Angle", this, 70.0, 10.0, 180.0, 1.0);
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 12.0, 3.0, 32.0, 1.0);
    private final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 32.0, 5.0, 80.0, 1.0);

    public PlayerFOV() {
        super("Player FOV", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.viewAngleSetting, this.rangeSetting, this.opacitySetting});
    }

    private static void handleEntityPlayer2(EntityPlayer player, int n, double d, double d2, double d3) {
        AxisAlignedBB box = player.getEntityBoundingBox().expand(0.04, 0.04, 0.04).offset(-d, -d2, -d3);
        float f = (n >> 16 & 0xFF) / 255.0F;
        float f2 = (n >> 8 & 0xFF) / 255.0F;
        float f3 = (n & 0xFF) / 255.0F;
        GlStateManager.color(f, f2, f3, 0.9F);
        RenderGlobal.func_181561_a(box);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof EventRender3D && mc.thePlayer != null && mc.theWorld != null) {
            this.process();
        }
    }

    private void handleEntityPlayer(EntityPlayer player, int n, double d, double d2, double d3) {
        double d4 = this.rangeSetting.computeDouble();
        double d5 = player.posX - d;
        double d6 = player.posY + player.getEyeHeight() - d2;
        double d7 = player.posZ - d3;
        double rad = Math.toRadians(player.rotationYaw - this.viewAngleSetting.computeDouble() * 0.5);
        double rad2 = Math.toRadians(this.viewAngleSetting.computeDouble());
        int n2 = Math.max(8, (int)this.viewAngleSetting.computeDouble() / 4);
        float f = (n >> 16 & 0xFF) / 255.0F;
        float f2 = (n >> 8 & 0xFF) / 255.0F;
        float f3 = ((0xFF | ~n) - ~n) / 255.0F;
        float f4 = (float)this.opacitySetting.computeDouble() / 100.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(6, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(d5, d6, d7).func_181666_a(f, f2, f3, f4).func_181675_d();

        for (int i = 0; i <= n2; i++) {
            double d8 = rad + rad2 * i / n2;
            worldRenderer.func_181662_b(d5 - Math.sin(d8) * d4, d6, d7 + Math.cos(d8) * d4)
                .func_181666_a(f, f2, f3, f4 * 0.55F)
                .func_181675_d();
        }

        tessellator.draw();
    }

    private void process() {
        double viewerPosX = mc.getRenderManager().viewerPosX;
        double viewerPosY = mc.getRenderManager().viewerPosY;
        double viewerPosZ = mc.getRenderManager().viewerPosZ;
        double d = this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();

        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glLineWidth(1.5F);

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != mc.thePlayer && playerEntity.isEntityAlive() && !(playerEntity.getDistanceSqToEntity(mc.thePlayer) > d)) {
                    boolean flag = this.matchesEntityPlayer(playerEntity);
                    int n = flag ? -46261 : -11150966;
                    this.handleEntityPlayer(playerEntity, n, viewerPosX, viewerPosY, viewerPosZ);
                    handleEntityPlayer2(playerEntity, n, viewerPosX, viewerPosY, viewerPosZ);
                }
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        Vec3 lookVec = player.getLookVec();
        Vec3 vec = new Vec3(
            mc.thePlayer.posX - player.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - player.posY - player.getEyeHeight(), mc.thePlayer.posZ - player.posZ
        );
        double dist = Math.sqrt(vec.xCoord * vec.xCoord + vec.yCoord * vec.yCoord + vec.zCoord * vec.zCoord);
        if (dist < 1.0E-6) {
            return true;
        }
        double d = (lookVec.xCoord * vec.xCoord + lookVec.yCoord * vec.yCoord + lookVec.zCoord * vec.zCoord) / dist;
        return Math.toDegrees(Math.acos(Math.max(-1.0, Math.min(1.0, d)))) <= this.viewAngleSetting.computeDouble() * 0.5;
    }
}
