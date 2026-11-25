package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.Brutality;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class RenderEntityItem extends Render<EntityItem> {
    private final RenderItem itemRenderer;
    private Random field_177079_e = new Random();

    public RenderEntityItem(RenderManager renderManagerIn, RenderItem p_i46167_2_) {
        super(renderManagerIn);
        this.itemRenderer = p_i46167_2_;
        this.shadowSize = 0.15F;
        this.shadowOpaque = 0.75F;
    }

    private int func_177077_a(EntityItem itemIn, double p_177077_2_, double p_177077_4_, double p_177077_6_, float p_177077_8_, IBakedModel p_177077_9_) {
        ItemStack entityItem = itemIn.getEntityItem();
        Item item = entityItem.getItem();
        if (item == null) {
            return 0;
        }
        boolean gui3d = p_177077_9_.isGui3d();
        int n = this.func_177078_a(entityItem);
        float f2 = MathHelper.sin((itemIn.getAge() + p_177077_8_) / 10.0F + itemIn.hoverStart) * 0.1F + 0.1F;
        float f3 = p_177077_9_.getItemCameraTransforms().func_181688_b(TransformType.GROUND).scale.y;
        GlStateManager.translate((float)p_177077_2_, (float)p_177077_4_ + f2 + 0.25F * f3, (float)p_177077_6_);
        if (gui3d || this.renderManager.options != null) {
            float f4 = ((itemIn.getAge() + p_177077_8_) / 20.0F + itemIn.hoverStart) * (180.0F / (float)Math.PI);
            GlStateManager.rotate(f4, 0.0F, 1.0F, 0.0F);
        }

        if (!gui3d) {
            float f5 = -0.0F * (n - 1) * 0.5F;
            float f6 = -0.0F * (n - 1) * 0.5F;
            float f7 = -0.046875F * (n - 1) * 0.5F;
            GlStateManager.translate(f5, f6, f7);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        return n;
    }

    private int func_177078_a(ItemStack stack) {
        byte b = 1;
        if (stack.stackSize > 48) {
            b = 5;
        } else if (stack.stackSize > 32) {
            b = 4;
        } else if (stack.stackSize > 16) {
            b = 3;
        } else if (stack.stackSize > 1) {
            b = 2;
        }

        return b;
    }

    public void doRender(EntityItem entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (Brutality.moduleManager.computeModule("Item Physics").checkState44()) {
            ResetColorHelper.handleEntity(entity, x, y, z, entityYaw, partialTicks);
        } else {
            ItemStack entityItem = entity.getEntityItem();
            this.field_177079_e.setSeed(187L);
            boolean flag = false;
            if (this.bindEntityTexture(entity)) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).setBlurMipmap(false, false);
                flag = true;
            }

            GlStateManager.enableRescaleNormal();
            GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.pushMatrix();
            IBakedModel itemModel = this.itemRenderer.getItemModelMesher().getItemModel(entityItem);
            int n = this.func_177077_a(entity, x, y, z, partialTicks, itemModel);

            for (int i = 0; i < n; i++) {
                if (itemModel.isGui3d()) {
                    GlStateManager.pushMatrix();
                    if (i > 0) {
                        float f = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f2 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        float f3 = (this.field_177079_e.nextFloat() * 2.0F - 1.0F) * 0.15F;
                        GlStateManager.translate(f, f2, f3);
                    }

                    GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    itemModel.getItemCameraTransforms().func_181689_a(TransformType.GROUND);
                    this.itemRenderer.renderItem(entityItem, itemModel);
                    GlStateManager.popMatrix();
                } else {
                    GlStateManager.pushMatrix();
                    itemModel.getItemCameraTransforms().func_181689_a(TransformType.GROUND);
                    this.itemRenderer.renderItem(entityItem, itemModel);
                    GlStateManager.popMatrix();
                    float f4 = itemModel.getItemCameraTransforms().field_181699_o.scale.x;
                    float f5 = itemModel.getItemCameraTransforms().field_181699_o.scale.y;
                    float f6 = itemModel.getItemCameraTransforms().field_181699_o.scale.z;
                    GlStateManager.translate(0.0F * f4, 0.0F * f5, 0.046875F * f6);
                }
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
            this.bindEntityTexture(entity);
            if (flag) {
                this.renderManager.renderEngine.getTexture(this.getEntityTexture(entity)).restoreLastBlurMipmap();
            }

            super.doRender(entity, x, y, z, entityYaw, partialTicks);
        }
    }

    protected ResourceLocation getEntityTexture(EntityItem entity) {
        return TextureMap.locationBlocksTexture;
    }
}
