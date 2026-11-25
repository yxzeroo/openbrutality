package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import org.brutality.Brutality;
import org.brutality.module.impl.visual.EnchantGlint;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
    protected static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    protected T field_177189_c;
    protected T field_177186_d;
    private final RendererLivingEntity<?> renderer;
    private float alpha = 1.0F;
    private float colorR = 1.0F;
    private float colorG = 1.0F;
    private float colorB = 1.0F;
    private boolean field_177193_i;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public LayerArmorBase(RendererLivingEntity<?> rendererIn) {
        this.renderer = rendererIn;
        this.initArmor();
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 4);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 3);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 2);
        this.renderLayer(entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale, 1);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    private void renderLayer(EntityLivingBase entitylivingbaseIn, float p_177182_2_, float p_177182_3_, float p_177182_4_, float p_177182_5_, float p_177182_6_, float p_177182_7_, float p_177182_8_, int armorSlot) {
        ItemStack currentArmor = this.getCurrentArmor(entitylivingbaseIn, armorSlot);
        if (currentArmor != null && currentArmor.getItem() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)currentArmor.getItem();
            ModelBase modelBase = this.func_177175_a(armorSlot);
            modelBase.setModelAttributes(this.renderer.getMainModel());
            modelBase.setLivingAnimations(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_4_);
            if (Reflector.ForgeHooksClient.exists()) {
                modelBase = this.getArmorModelHook(entitylivingbaseIn, currentArmor, armorSlot, (T)modelBase);
            }

            this.func_177179_a((T)modelBase, armorSlot);
            boolean slotForLeggings = this.isSlotForLeggings(armorSlot);
            if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, (String)null)) {
                if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
                    this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, slotForLeggings ? 2 : 1, (String)null));
                } else {
                    this.renderer.bindTexture(this.getArmorResource(item, slotForLeggings));
                }
            }

            if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
                if (ReflectorForge.armorHasOverlay(item, currentArmor)) {
                    int color = item.getColor(currentArmor);
                    float f = (color >> 16 & 0xFF) / 255.0F;
                    float f2 = (color >> 8 & 0xFF) / 255.0F;
                    float f3 = (color & 0xFF) / 255.0F;
                    GlStateManager.color(this.colorR * f, this.colorG * f2, this.colorB * f3, this.alpha);
                    modelBase.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                    if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, "overlay")) {
                        this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, slotForLeggings ? 2 : 1, "overlay"));
                    }
                }

                GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                modelBase.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                if (!this.field_177193_i
                    && currentArmor.hasEffect()
                    && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, modelBase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_))) {
                    this.func_177183_a(entitylivingbaseIn, (T)modelBase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                }

                return;
            }

            switch (item.getArmorMaterial()) {
                case LEATHER:
                    int color2 = item.getColor(currentArmor);
                    float f4 = (color2 >> 16 & 0xFF) / 255.0F;
                    float f5 = (color2 >> 8 & 0xFF) / 255.0F;
                    float f6 = (color2 & 0xFF) / 255.0F;
                    GlStateManager.color(this.colorR * f4, this.colorG * f5, this.colorB * f6, this.alpha);
                    modelBase.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
                    if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, slotForLeggings ? 2 : 1, "overlay")) {
                        this.renderer.bindTexture(this.getArmorResource(item, slotForLeggings, "overlay"));
                    }
                case CHAIN:
                case IRON:
                case GOLD:
                case DIAMOND:
                    GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
                    modelBase.render(entitylivingbaseIn, p_177182_2_, p_177182_3_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
            }

            if (!this.field_177193_i
                && currentArmor.isItemEnchanted()
                && (!Config.isCustomItems() || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, modelBase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_))) {
                this.func_177183_a(entitylivingbaseIn, (T)modelBase, p_177182_2_, p_177182_3_, p_177182_4_, p_177182_5_, p_177182_6_, p_177182_7_, p_177182_8_);
            }
        }
    }

    public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot) {
        return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
    }

    public T func_177175_a(int p_177175_1_) {
        return this.isSlotForLeggings(p_177175_1_) ? this.field_177189_c : this.field_177186_d;
    }

    private boolean isSlotForLeggings(int armorSlot) {
        return armorSlot == 2;
    }

    private void func_177183_a(EntityLivingBase entitylivingbaseIn, T modelbaseIn, float p_177183_3_, float p_177183_4_, float p_177183_5_, float p_177183_6_, float p_177183_7_, float p_177183_8_, float p_177183_9_) {
        if (!Config.isShaders() || !Shaders.isShadowPass) {
            float f = entitylivingbaseIn.ticksExisted + p_177183_5_;
            this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintBegin();
            }

            GlStateManager.enableBlend();
            GlStateManager.depthFunc(GL11.GL_EQUAL);
            GlStateManager.depthMask(false);
            float f2 = 0.5F;
            GlStateManager.color(f2, f2, f2, 1.0F);

            for (int i = 0; i < 2; i++) {
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                float f3 = 0.76F;
                EnchantGlint enchantGlint = (EnchantGlint)Brutality.moduleManager.computeModule("Enchant Glint");
                if (enchantGlint.checkState44()) {
                    int rgb = enchantGlint.computeColor().getRGB();
                    ResetColorHelper.handleInt(rgb, 1.0F);
                } else {
                    GlStateManager.color(0.5F * f3, 0.25F * f3, 0.8F * f3, 1.0F);
                }

                GlStateManager.matrixMode(GL11.GL_TEXTURE);
                GlStateManager.loadIdentity();
                float f4 = 0.33333334F;
                GlStateManager.scale(f4, f4, f4);
                GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + i * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                modelbaseIn.render(entitylivingbaseIn, p_177183_3_, p_177183_4_, p_177183_6_, p_177183_7_, p_177183_8_, p_177183_9_);
            }

            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }
    }

    private ResourceLocation getArmorResource(ItemArmor p_177181_1_, boolean p_177181_2_) {
        return this.getArmorResource(p_177181_1_, p_177181_2_, (String)null);
    }

    private ResourceLocation getArmorResource(ItemArmor p_177178_1_, boolean p_177178_2_, String p_177178_3_) {
        String str = String.format(
            "textures/models/armor/%s_layer_%d%s.png", p_177178_1_.getArmorMaterial().getName(), p_177178_2_ ? 2 : 1, p_177178_3_ == null ? "" : String.format("_%s", p_177178_3_)
        );
        ResourceLocation location = ARMOR_TEXTURE_RES_MAP.get(str);
        if (location == null) {
            location = new ResourceLocation(str);
            ARMOR_TEXTURE_RES_MAP.put(str, location);
        }

        return location;
    }

    protected abstract void initArmor();

    protected abstract void func_177179_a(T p_177179_1_, int p_177179_2_);

    protected T getArmorModelHook(EntityLivingBase p_getArmorModelHook_1_, ItemStack p_getArmorModelHook_2_, int p_getArmorModelHook_3_, T p_getArmorModelHook_4_) {
        return (T)p_getArmorModelHook_4_;
    }

    public ResourceLocation getArmorResource(Entity p_getArmorResource_1_, ItemStack p_getArmorResource_2_, int p_getArmorResource_3_, String p_getArmorResource_4_) {
        ItemArmor item = (ItemArmor)p_getArmorResource_2_.getItem();
        String name = item.getArmorMaterial().getName();
        String str = "minecraft";
        int n = name.indexOf(58);
        if (n != -1) {
            str = name.substring(0, n);
            name = name.substring(n + 1);
        }

        String str2 = String.format(
            "%s:textures/models/armor/%s_layer_%d%s.png", str, name, this.isSlotForLeggings(p_getArmorResource_3_) ? 2 : 1, p_getArmorResource_4_ == null ? "" : String.format("_%s", p_getArmorResource_4_)
        );
        String str3 = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, p_getArmorResource_1_, p_getArmorResource_2_, str2, p_getArmorResource_3_, p_getArmorResource_4_);
        ResourceLocation location = ARMOR_TEXTURE_RES_MAP.get(str3);
        if (location == null) {
            location = new ResourceLocation(str3);
            ARMOR_TEXTURE_RES_MAP.put(str3, location);
        }

        return location;
    }
}
