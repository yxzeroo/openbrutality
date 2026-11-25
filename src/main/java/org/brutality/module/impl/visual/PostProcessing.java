package org.brutality.module.impl.visual;

import java.util.Arrays;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.shader.HighShader;
import org.lwjgl.opengl.GL11;

public class PostProcessing extends Module {
    private static final boolean flag = System.getProperty("os.name").toLowerCase().contains("mac");
    public final BooleanSetting blurSetting = new BooleanSetting("Blur", this, true);
    public final BooleanSetting bloomSetting = new BooleanSetting("Bloom", this, false);
    private final ModeSetting highSetting = new ModeSetting("Quality", this, "Performance", Arrays.asList("Performance", "Balanced", "High"));
    private final NumberSetting blurIterationsSetting = new NumberSetting("Blur Iterations", this, 1.0, 1.0, 3.0, 1.0);
    private final NumberSetting blurOffsetSetting = new NumberSetting("Blur Offset", this, 1.5, 1.0, 6.0, 0.5);
    private final NumberSetting blurStrengthSetting = new NumberSetting("Blur Strength", this, 0.6, 0.1, 1.0, 0.05);
    private final NumberSetting bloomRadiusSetting = new NumberSetting("Bloom Radius", this, 1.0, 1.0, 3.0, 1.0);
    private final NumberSetting bloomOffsetSetting = new NumberSetting("Bloom Offset", this, 1.0, 1.0, 6.0, 0.5);
    private final NumberSetting bloomStrengthSetting = new NumberSetting("Bloom Strength", this, 0.18, 0.05, 0.6, 0.01);
    private final SetShaderTypeEvent setShaderTypeEvent = new SetShaderTypeEvent(SetShaderTypeEvent.BlurMode.BLUR);
    private final SetShaderTypeEvent setShaderTypeEvent2 = new SetShaderTypeEvent(SetShaderTypeEvent.BlurMode.GLOW);
    private HighShader highShader;
    private boolean flag2;

    private boolean onBlurMode2(SetShaderTypeEvent.BlurMode blurMode) {
        for (Module module : ModuleManager.getModuleArray()) {
            if (module.checkState44() && module.onBlurMode(blurMode) && !EditableModulesHelper.matchesModule(module)) {
                return true;
            }
        }

        return false;
    }

    public void process() {
        if (this.checkState44()
            && !this.flag2
            && !flag
            && mc.thePlayer != null
            && mc.theWorld != null
            && OpenGlHelper.isFramebufferEnabled()
            && OpenGlHelper.shadersSupported) {
            boolean flag2 = this.blurSetting.isEnabled() && this.onBlurMode2(SetShaderTypeEvent.BlurMode.BLUR);
            boolean flag3 = this.bloomSetting.isEnabled() && this.onBlurMode2(SetShaderTypeEvent.BlurMode.GLOW);
            if (flag2 || flag3) {
                try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                    if (this.highShader == null) {
                        this.highShader = new HighShader();
                    }

                    GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.disableAlpha();
                    GlStateManager.depthMask(false);
                    GlStateManager.colorMask(true, true, true, true);
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    if (flag2) {
                        this.highShader
                            .handleSetShaderTypeEvent(
                                this.setShaderTypeEvent,
                                this.highSetting.getText(),
                                (int)this.blurIterationsSetting.computeDouble(),
                                (float)this.blurOffsetSetting.computeDouble(),
                                (float)this.blurStrengthSetting.computeDouble()
                            );
                    }

                    if (flag3) {
                        this.highShader
                            .handleSetShaderTypeEvent(
                                this.setShaderTypeEvent2,
                                this.highSetting.getText(),
                                (int)this.bloomRadiusSetting.computeDouble(),
                                (float)this.bloomOffsetSetting.computeDouble(),
                                (float)this.bloomStrengthSetting.computeDouble()
                            );
                    }
                } catch (Throwable e) {
                    this.flag2 = true;
                    if (this.highShader != null) {
                        this.highShader.process();
                    }

                    MessageUtils.x("§b[PostProcessing] §7Effect stopped after a rendering error; toggle to retry.");
                    e.printStackTrace();
                }
            }
        }
    }

    public PostProcessing() {
        super("PostProcessing", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.highSetting,
                this.blurSetting,
                this.blurIterationsSetting,
                this.blurOffsetSetting,
                this.blurStrengthSetting,
                this.bloomSetting,
                this.bloomRadiusSetting,
                this.bloomOffsetSetting,
                this.bloomStrengthSetting
            }
        );
        this.blurIterationsSetting.setConditionalVisibility(() -> this.blurSetting.isEnabled() && !this.highSetting.matchesString2("Performance"));
        this.blurOffsetSetting.setConditionalVisibility(this.blurSetting::isEnabled);
        this.blurStrengthSetting.setConditionalVisibility(this.blurSetting::isEnabled);
        this.bloomRadiusSetting.setConditionalVisibility(() -> this.bloomSetting.isEnabled() && !this.highSetting.matchesString2("Performance"));
        this.bloomOffsetSetting.setConditionalVisibility(this.bloomSetting::isEnabled);
        this.bloomStrengthSetting.setConditionalVisibility(this.bloomSetting::isEnabled);
    }

    public void process3() {
        this.process();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.flag2 = false;
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onDisable() {
        if (this.highShader != null) {
            this.highShader.process();
        }

        super.onDisable();
    }
}
