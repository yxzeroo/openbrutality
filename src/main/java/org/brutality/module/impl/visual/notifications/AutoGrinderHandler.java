package org.brutality.module.impl.visual.notifications;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.lwjgl.opengl.GL11;

public final class AutoGrinderHandler {
    private static final int count = 10;
    private static final int count2 = 6;
    private static final int count3 = 8;
    private static final int count4 = 0;
    private static final int count5 = 2;
    private static final int count6 = 4;
    private static final int count7 = -602136279;
    private static final int count8 = -3618616;
    private static final int count9 = -11141291;
    private static final int count10 = -171;
    private static final int count11 = -43691;
    private static final int count12 = -43521;
    private final Minecraft mc = Minecraft.getMinecraft();
    private CustomFontRenderer drawStringWithShadowComponent;
    private String text;
    private final NumberSetting numberSetting;
    private final ModeSetting modeSetting;
    private final BooleanSetting booleanSetting;
    private final BooleanSetting booleanSetting2;
    private final BooleanSetting booleanSetting3;
    private final BooleanSetting booleanSetting4;
    private final BooleanSetting booleanSetting5;
    private final BooleanSetting booleanSetting6;
    private final BooleanSetting booleanSetting7;
    private final BooleanSetting booleanSetting8;
    private final List<Module> entries = new ArrayList<>();

    private int computeInt(Module module) {
        String simpleName = module.getClass().getSimpleName();
        String name = module.getClass().getPackage().getName();
        if (module.getPitCategory() != PitCategory.HUD && !name.contains(".hud.") && !simpleName.endsWith("ESP")) {
            if (module.getPitCategory() != PitCategory.SWAP && !name.contains(".swap.")) {
                switch (simpleName) {
                    case "AutoGrinder":
                    case "WeaselGrinder":
                    case "AutoHunt":
                    case "AutoAura":
                    case "AutoBullet":
                    case "AutoVolley":
                    case "OneTickVenom":
                    case "AutoFeast":
                    case "AutoSpire":
                    case "BowStreak":
                    case "AntiVenom":
                    case "Beast":
                    case "Squads":
                    case "TDM":
                        return -11141291;
                    case "AutoPit":
                    case "AutoPitSpawn":
                    case "AutoOof":
                    case "AutoTelebow":
                    case "AutoTacticalInsertion":
                    case "AutoSewer":
                    case "AntiLauncher":
                    case "TelebowFix":
                    case "SpireFix":
                    case "Pizza":
                        return -171;
                    case "JumpBoostDetector":
                    case "PodDetection":
                    case "ItemStatTracker":
                    case "MapDetection":
                    case "NPCFinder":
                    case "PitAlerts":
                    case "ScammerAlerts":
                    case "BedrockAlerts":
                    case "WipeAlert":
                    case "VanishDebug":
                    case "AntiVanish":
                        return -43521;
                    default:
                        return -43691;
                }
            } else {
                return -11141291;
            }
        } else {
            return -43521;
        }
    }

    public int computeInt2(Module module) {
        Category category = module.getCategory();
        if (category == Category.PIT) {
            return this.computeInt(module);
        }
        if (category == Category.COMBAT) {
            return -11141291;
        }
        if (category == Category.MOVEMENT) {
            return -171;
        }
        return category == Category.VISUAL ? -43521 : -43691;
    }

    public AutoGrinderHandler(
        NumberSetting numberSetting,
        ModeSetting modeSetting,
        BooleanSetting booleanSetting,
        BooleanSetting booleanSetting2,
        BooleanSetting booleanSetting3,
        BooleanSetting booleanSetting4,
        BooleanSetting booleanSetting5,
        BooleanSetting booleanSetting6,
        BooleanSetting booleanSetting7,
        BooleanSetting booleanSetting8
    ) {
        this.numberSetting = numberSetting;
        this.modeSetting = modeSetting;
        this.booleanSetting = booleanSetting;
        this.booleanSetting2 = booleanSetting2;
        this.booleanSetting3 = booleanSetting3;
        this.booleanSetting4 = booleanSetting4;
        this.booleanSetting5 = booleanSetting5;
        this.booleanSetting6 = booleanSetting6;
        this.booleanSetting7 = booleanSetting7;
        this.booleanSetting8 = booleanSetting8;
    }

    private boolean matchesCategory(Category category) {
        if (category == null) {
            return false;
        }
        switch (category) {
            case COMBAT:
                return this.booleanSetting.isEnabled();
            case MOVEMENT:
                return this.booleanSetting2.isEnabled();
            case PLAYER:
                return this.booleanSetting3.isEnabled();
            case VISUAL:
                return this.booleanSetting4.isEnabled();
            case MISCELLANEOUS:
                return this.booleanSetting5.isEnabled();
            case EXPLOIT:
                return this.booleanSetting6.isEnabled();
            case SCRIPTS:
                return this.booleanSetting7.isEnabled();
            case PIT:
                return this.booleanSetting8.isEnabled();
            default:
                return false;
        }
    }

    private String computeString(Module module) {
        return module.getName().replace(" ", "");
    }

    private static int computeInt(int n, int n2, int n3, int n4) {
        return (n & 0xFF) << 24 | (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | (0xFF | ~n4) - ~n4;
    }

    public void render() {
        if (this.mc.thePlayer != null && this.drawStringWithShadowComponent != null && !this.entries.isEmpty()) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            int scaledWidth = sr.getScaledWidth();
            int height = this.drawStringWithShadowComponent.getHeight();
            int n = height + 0;
            int n2 = 0;

            for (Module module : this.entries) {
                n2 = Math.max(n2, this.drawStringWithShadowComponent.getStringWidth(this.computeString(module)));
            }

            byte b = 8;
            byte b2 = 4;
            int textWidth = this.drawStringWithShadowComponent.getStringWidth("HUD");
            int n3 = Math.max(75, 6 + n2 + 8);
            int n4 = 16 + this.entries.size() * n + 6;
            int n5 = scaledWidth - n3 - 2;
            byte b3 = 4;
            int n6 = (int)(this.numberSetting.computeDouble() / 100.0 * 255.0);
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GlStateManager.disableTexture2D();
            this.handleFloat(n5, b3, n5 + n3, b3 + 10, count7);
            this.handleFloat(n5, b3 + 10, n5 + n3, b3 + n4, computeInt(n6, 30, 30, 30));
            int n7 = n5 + (n3 - b - b2 - textWidth) / 2;
            float f = b3 + 1.5F;

            for (int i = 0; i < 4; i++) {
                float f2 = f + i * 2;
                this.handleFloat(n7, f2, n7 + 1, f2 + 1.0F, count8);
                this.handleFloat(n7 + 2, f2, n7 + b, f2 + 1.0F, count8);
            }

            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawStringWithShadowComponent.drawStringWithShadow("HUD", n7 + b + b2, b3 + (10 - height) / 2.0F, count8);
            float f3 = b3 + 10 + 6;

            for (Module module2 : this.entries) {
                this.drawStringWithShadowComponent.drawStringWithShadow(this.computeString(module2), n5 + 6, f3, this.computeInt2(module2));
                f3 += n;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public void tick() {
        if (Brutality.moduleManager != null) {
            this.process();
            this.entries.clear();

            for (Module module : ModuleManager.getEntries2()) {
                if (module.checkState44()
                    && module.getCategory() != Category.HIDDEN
                    && !ModuleManager.matchesModule(module)
                    && !this.matchesCategory(module.getCategory())) {
                    this.entries.add(module);
                }
            }

            this.entries
                .sort(
                    (module2, module3) -> Integer.compare(
                        this.drawStringWithShadowComponent.getStringWidth(this.computeString(module3)),
                        this.drawStringWithShadowComponent.getStringWidth(this.computeString(module2))
                    )
                );
        }
    }

    private void process() {
        String text = this.modeSetting.getText();
        if (!text.equals(this.text) || this.drawStringWithShadowComponent == null) {
            this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(text, 14.0F);
            this.text = text;
        }
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = (n >> 8 & 0xFF) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(f6, f7, f8, f5);
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
    }
}
