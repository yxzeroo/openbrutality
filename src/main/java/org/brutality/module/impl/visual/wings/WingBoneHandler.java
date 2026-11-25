package org.brutality.module.impl.visual.wings;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.module.impl.visual.CustomModel;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class WingBoneHandler extends ModelBase {
    private final ResourceLocation resourceLocation = new ResourceLocation("brutality/images/wings.png");
    private final ModelRenderer modelRenderer;
    private final ModelRenderer modelRenderer2;
    private static final long timestamp = 1500L;

    public void handleEntityPlayer(EntityPlayer player, float f, double d, Color color) {
        double d2 = this.computeFloat(MathHelper.wrapAngleTo180_float(player.prevRenderYawOffset), MathHelper.wrapAngleTo180_float(player.renderYawOffset), f);
        GL11.glPushMatrix();
        GL11.glScaled(-d, -d, d);
        GL11.glRotated(d2 + 180.0, 0.0, 1.0, 0.0);
        GL11.glTranslated(0.0, CustomModel.enabled ? -0.65 : -1.25 / d, 0.0);
        GL11.glTranslated(0.0, 0.0, 0.2 / d);
        if (player.isSneaking()) {
            GL11.glTranslated(0.0, 0.125 / d, 0.0);
        }

        ResetColorHelper.handleInt3(color.getRGB());
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.resourceLocation);

        for (int i = 0; i < 2; i++) {
            GL11.glEnable(GL11.GL_CULL_FACE);
            float f2 = (float)(System.currentTimeMillis() % timestamp) / 1500.0F * MathHelper.PI * 2.0F;
            this.modelRenderer2.rotateAngleX = -1.4F - (float)Math.cos(f2) * 0.2F;
            this.modelRenderer2.rotateAngleY = 0.35F + (float)Math.sin(f2) * 0.4F;
            this.modelRenderer2.rotateAngleZ = 0.35F;
            this.modelRenderer.rotateAngleZ = -((float)(Math.sin(f2 + 2.0F) + 0.5)) * 0.75F;
            this.modelRenderer2.render(0.0625F);
            GL11.glScalef(-1.0F, 1.0F, 1.0F);
            if (i == 0) {
                GL11.glCullFace(GL11.GL_FRONT);
            }
        }

        GL11.glCullFace(GL11.GL_BACK);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private float computeFloat(float f, float f2, float f3) {
        float f4 = (f + (f2 - f) * f3) % 360.0F;
        return f4 < 0.0F ? f4 + 360.0F : f4;
    }

    public WingBoneHandler() {
        this.setTextureOffset("wing.bone", 0, 0);
        this.setTextureOffset("wing.skin", -10, 8);
        this.setTextureOffset("wingtip.bone", 0, 5);
        this.setTextureOffset("wingtip.skin", -10, 18);
        (this.modelRenderer2 = new ModelRenderer(this, "wing")).setTextureSize(30, 30);
        this.modelRenderer2.setRotationPoint(-2.0F, 0.0F, 0.0F);
        this.modelRenderer2.addBox("bone", -10.0F, -1.0F, -1.0F, 10, 2, 2);
        this.modelRenderer2.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        (this.modelRenderer = new ModelRenderer(this, "wingtip")).setTextureSize(30, 30);
        this.modelRenderer.setRotationPoint(-10.0F, 0.0F, 0.0F);
        this.modelRenderer.addBox("bone", -10.0F, -0.5F, -0.5F, 10, 1, 1);
        this.modelRenderer.addBox("skin", -10.0F, 0.0F, 0.5F, 10, 0, 10);
        this.modelRenderer2.addChild(this.modelRenderer);
    }
}
