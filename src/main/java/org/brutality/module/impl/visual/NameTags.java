package org.brutality.module.impl.visual;

import java.awt.geom.Rectangle2D.Float;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.regex.Pattern;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.command.impl.DenickCommand;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.GetEntityEvent2;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.AntiBot;
import org.brutality.module.impl.pit.hypixel.automation.PinTimer;
import org.brutality.module.impl.pit.hypixel.hud.TelebowTimer;
import org.brutality.module.impl.pit.hypixel.hud.ToxicInfo;
import org.brutality.module.impl.pit.hypixel.hud.VenomedTimer;
import org.brutality.module.impl.pit.hypixel.utility.JumpBoostDetector;
import org.brutality.module.impl.pit.hypixel.utility.PodDetection;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.MysticItemInfo;
import org.brutality.util.inventory.VenomType;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.TruceManager;
import org.brutality.utils.WHelper;
import org.brutality.utils.render.CreateHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class NameTags extends Module {
    private static NameTags instance;
    public final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this);
    private FontRenderer fontRenderer;
    private String text = "";
    public final NumberSetting scaleSetting = new NumberSetting("Scale", this, 1.0, 0.1, 10.0, 0.1);
    public final BooleanSetting autoScaleSetting = new BooleanSetting("Auto Scale", this, false);
    public final BooleanSetting showDistanceSetting = new BooleanSetting("Show Distance", this, true);
    public final BooleanSetting showVenomedTimerSetting = new BooleanSetting("Show Venomed Timer", this, true);
    public final BooleanSetting showHeldItemSetting = new BooleanSetting("Show Held Item", this, false);
    public final BooleanSetting friendKOSSymbolSetting = new BooleanSetting("Friend / KOS Symbol", this, true);
    public final BooleanSetting pitEnchantsSetting = new BooleanSetting("Pit Enchants", this, true);
    public final BooleanSetting backgroundSetting = new BooleanSetting("Background", this, true);
    public final NumberSetting opacitySetting = new NumberSetting("Opacity", this, 150.0, 0.0, 255.0, 5.0);
    public final ModeSetting noneSetting = new ModeSetting(
        "Relationship Style", this, "Fill", Arrays.asList("Fill", "Outline", "Side Bar", "Underline", "None")
    );
    private static final float factor = 0.5F;
    public final BooleanSetting textShadowSetting = new BooleanSetting("Text Shadow", this, true);
    private final KeybindListSetting selfSetting = new KeybindListSetting(
        "Targets",
        this,
        new BooleanSetting("Players", this, true),
        new BooleanSetting("Mobs", this, false),
        new BooleanSetting("Animals", this, false),
        new BooleanSetting("Bots", this, false),
        new BooleanSetting("Invisible", this, false),
        new BooleanSetting("Self", this, false)
    );
    private final BooleanSetting playersSetting;
    private final BooleanSetting botsSetting;
    private final BooleanSetting mobsSetting;
    private final BooleanSetting animalsSetting;
    private final BooleanSetting invisibleSetting;
    private final BooleanSetting selfSetting2;
    private static final long timestamp = 250000000L;
    private final Map<EntityPlayer, NameTags.Entry> lookupMap = new WeakHashMap<>();
    private boolean flag;
    private static final Pattern pattern = Pattern.compile("(?i)§([0-9a-f])");
    private static final Pattern pattern2 = Pattern.compile("(?i)§r");

    private static void handleFontRenderer(FontRenderer fontRenderer, List<String> list, float f) {
        float f2 = -f - 3.0F;

        for (String str : list) {
            handleFontRenderer(fontRenderer, str, -computeFloat(fontRenderer, str) / 2.0F, f2);
            f2 -= f + 3.0F;
        }
    }

    private void handleFloatType(Float val, boolean flag, boolean flag2, boolean flag3) {
        float f = val.x;
        float f2 = val.y;
        float f3 = f + val.width;
        float f4 = f2 + val.height;
        int n = computeInt(flag, flag2, flag3);
        boolean flag4 = flag || flag2 || flag3;
        if (flag4 && this.noneSetting.matchesString2("Fill")) {
            int n2 = flag ? 100 : (flag2 ? 150 : (int)this.opacitySetting.computeDouble());
            this.handleFloat(f, f2, f3, f4, n2 << 24 | n);
        } else if (this.backgroundSetting.isEnabled()) {
            this.handleFloat(f, f2, f3, f4, (int)this.opacitySetting.computeDouble() << 24);
        }

        if (flag4 && !this.noneSetting.matchesString2("Fill") && !this.noneSetting.matchesString2("None")) {
            int n3 = -16777216 + (n & ~-16777216);
            float f5 = Math.min(0.5F, val.height / 2.0F);
            if (this.noneSetting.matchesString2("Outline")) {
                if (CreateHelper.matchesFloat(f, f2, f3, f4, n3)) {
                    return;
                }

                this.handleFloat(f, f2, f3, f2 + f5, n3);
                this.handleFloat(f, f4 - f5, f3, f4, n3);
                this.handleFloat(f, f2 + f5, f + f5, f4 - f5, n3);
                this.handleFloat(f3 - f5, f2 + f5, f3, f4 - f5, n3);
            } else if (this.noneSetting.matchesString2("Side Bar")) {
                this.handleFloat(f - 1.0F - f5, f2, f - 1.0F, f4, n3);
            } else if (this.noneSetting.matchesString2("Underline")) {
                this.handleFloat(f, f4 - f5, f3, f4, n3);
            }
        }
    }

    private static int computeInt(boolean flag, boolean flag2, boolean flag3) {
        return flag ? 5174862 : (flag2 ? 11141120 : (flag3 ? 5147894 : 0));
    }

    private static void handleList(List<String> list, String str) {
        if (str != null && !str.trim().isEmpty()) {
            list.add(str.trim());
        }
    }

    private static String computeString2(float f) {
        BigDecimal bigDecimal = BigDecimal.valueOf(Math.round(f * 100.0) / 100.0).stripTrailingZeros();
        return (bigDecimal.scale() < 1 ? bigDecimal.setScale(1) : bigDecimal).toPlainString();
    }

    private static String computeString2(String str) {
        return IsChoiceHelper.computeString(str);
    }

    private FontRenderer computeFontRenderer() {
        if (this.fontSetting != null && !IsChoiceHelper.matchesString(this.fontSetting.getText())) {
            String text = this.fontSetting.getText();
            if (this.fontRenderer == null || !text.equals(this.text)) {
                this.fontRenderer = FontResourceLoader.computeDrawStringWithShadowComponent4(computeString2(this.fontSetting.getText()), 18.0F);
                this.text = text;
            }

            return this.fontRenderer;
        }
        return mc.fontRendererObj;
    }

    private static void handleFontRenderer(FontRenderer fontRenderer, String str, float f, float f2) {
        boolean flag = fontRenderer instanceof CustomFontRenderer;
        String str2 = flag ? computeString(fontRenderer, str) : str;
        float f3 = flag ? f2 - 2.0F : f2;
        if (instance != null && instance.textShadowSetting != null && !instance.textShadowSetting.isEnabled()) {
            fontRenderer.drawString(str2, f, f3, 16777215, false);
        } else {
            fontRenderer.drawStringWithShadow(str2, f, f3, 16777215);
        }
    }

    public static String computeString(EntityPlayer player) {
        LinkedHashMap<VenomType, Integer> map = new LinkedHashMap<>();
        java.util.ArrayList list = new java.util.ArrayList();
        Collections.addAll(list, player.inventory.armorInventory);
        list.add(player.getHeldItem());

        for (ItemStack stack : ((Iterable<ItemStack>)(Object)(list))) {
            if (stack != null && stack.getTagCompound() != null) {
                MysticItemInfo mysticItemInfo = new MysticItemInfo(stack);

                for (Map.Entry entry : mysticItemInfo.lookupMap2.entrySet()) {
                    map.merge((VenomType)entry.getKey(), (Integer)entry.getValue(), Math::max);
                }
            }
        }

        java.util.ArrayList list2 = new java.util.ArrayList();

        for (Map.Entry entry2 : ((Iterable<Map.Entry>)(Object)(map.entrySet()))) {
            list2.add(((VenomType)entry2.getKey()).getColorCode() + ((VenomType)entry2.getKey()).getDisplayName() + " " + entry2.getValue());
        }

        return String.join(" §7/ ", list2);
    }

    private String getJumpBoostPrefix(EntityPlayer player) {
        if (player == mc.thePlayer) {
            return "";
        }
        JumpBoostDetector instance = JumpBoostDetector.getInstance();
        if (instance != null && instance.checkState44()) {
            int n = instance.computeInt(player.getName());
            if (n <= 0) {
                return "";
            }
            EnumChatFormatting DARK_GREEN = EnumChatFormatting.DARK_GREEN;
            EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
            return EnumChatFormatting.DARK_GREEN + "[Jump IV - " + GREEN + n + "s" + DARK_GREEN + "] ";
        }
        return "";
    }

    public String computeString(String str) {
        if (!this.friendKOSSymbolSetting.isEnabled()) {
            return "";
        }
        return DefaultHelper.matchesString(str) ? EnumChatFormatting.GREEN + "F" : (WHelper.matchesString(str) ? EnumChatFormatting.DARK_RED + "K" : "");
    }

    private String computeString(float f) {
        if (f > 40.0F) {
            return EnumChatFormatting.GREEN.toString();
        }
        return f >= 20.0F ? EnumChatFormatting.YELLOW.toString() : EnumChatFormatting.RED.toString();
    }

    private String computeString2(EntityPlayer player) {
        if (player == mc.thePlayer) {
            return "";
        }
        PinTimer instance = PinTimer.getInstance();
        if (instance != null && instance.checkState44()) {
            int n = instance.computeInt(player.getName());
            if (n <= 0) {
                return "";
            }
            EnumChatFormatting enumChatFormatting = instance.matchesString2(player.getName()) ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
            EnumChatFormatting enumChatFormatting2 = instance.matchesString2(player.getName()) ? EnumChatFormatting.DARK_GREEN : EnumChatFormatting.DARK_RED;
            return enumChatFormatting2 + "[Pin - " + enumChatFormatting + n + "s" + enumChatFormatting2 + "] ";
        }
        return "";
    }

    private static String computeString(FontRenderer fontRenderer, String str) {
        String str2 = pattern.matcher(str).replaceAll("§r§$1");
        return fontRenderer instanceof CustomFontRenderer ? pattern2.matcher(str2).replaceAll("§r§f") : str2;
    }

    private void handleEntityLivingBase(EntityLivingBase living, double d, double d2, double d3) {
        Minecraft mc = Minecraft.getMinecraft();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        FontRenderer fontRenderer = this.computeFontRenderer();
        double dist = mc.thePlayer.getDistanceToEntity(living);
        float f = (float)this.scaleSetting.computeDouble();
        if (this.autoScaleSetting.isEnabled()) {
            f += (float)(dist / 1.0) * 0.05F;
        }

        double d4 = d2 + living.height + 0.5;
        String text = living.getDisplayName().getFormattedText();
        String str;
        if (living instanceof EntityPlayer player) {
            String name = player.getName();
            String str2 = DenickCommand.computeString(name);
            if (str2 != null && !str2.equalsIgnoreCase(name)) {
                str = text + " " + EnumChatFormatting.GRAY + "(" + EnumChatFormatting.YELLOW + str2 + EnumChatFormatting.GRAY + ")";
            } else {
                String str3 = DenickCommand.computeString2(name);
                if (str3 != null && !str3.equalsIgnoreCase(name)) {
                    EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                    EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                    EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                    str = text + " " + GRAY2 + "[Nick: " + YELLOW + str3 + GRAY + "]";
                } else {
                    str = text;
                }
            }
        } else {
            str = text;
        }

        StringBuilder sb = new StringBuilder();
        java.util.ArrayList list = new java.util.ArrayList();
        StringBuilder sb2 = new StringBuilder();
        String str4 = "";
        if (this.showDistanceSetting.isEnabled() && living != mc.thePlayer) {
            String str5 = this.computeString((float)dist);
            str4 = str5 + (int)dist + "m";
        }

        String str6;
        String str7;
        if (living instanceof EntityPlayer player2) {
            var list2 = this.computeList(player2);
            if (!list2.isEmpty()) {
                sb.append((String)list2.get(0)).append(EnumChatFormatting.RESET).append(" ");
                list.addAll(list2.subList(1, list2.size()));
            }

            str6 = str + VisualHandler.computeString2(player2.getName());
            if (this.showHeldItemSetting.isEnabled() && player2.getHeldItem() != null) {
                str6 = str6 + " " + EnumChatFormatting.GRAY + "[" + player2.getHeldItem().getDisplayName() + EnumChatFormatting.GRAY + "]";
            }

            float f2 = player2.getHealth() / 2.0F;
            float f3 = player2.getAbsorptionAmount() / 2.0F;
            EnumChatFormatting enumChatFormatting = f2 >= 8.0 ? EnumChatFormatting.GREEN : (f2 >= 4.0 ? EnumChatFormatting.GOLD : EnumChatFormatting.RED);
            sb2.append(" ").append(enumChatFormatting).append(computeString2(f2));
            if (f3 > 0.0F) {
                sb2.append(" ").append(EnumChatFormatting.GOLD).append(computeString2(f3));
            }

            str7 = str6 + sb2;
            if (!str4.isEmpty()) {
                sb2.append(" ").append(str4);
            }
        } else {
            str6 = str;
            str7 = str;
            if (!str4.isEmpty()) {
                sb2.append(" ").append(str4);
            }
        }

        String str8 = sb.toString() + str6 + sb2.toString();
        GlStateManager.pushMatrix();
        GlStateManager.translate(d, d4, d3);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX * (mc.gameSettings.thirdPersonView == 2 ? -1 : 1), 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F * f, -0.025F * f, 0.025F * f);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.disableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        float f4 = computeFloat(fontRenderer, str8) / 2.0F;
        boolean flag = false;
        boolean flag2 = false;
        boolean flag3 = false;
        if (living instanceof EntityPlayer && living != mc.thePlayer) {
            String name2 = living.getName();
            flag = DefaultHelper.matchesString(name2);
            flag2 = WHelper.matchesString(name2);
            flag3 = TruceManager.matchesString(name2);
        }

        Float val = computeFloatType(fontRenderer, "", sb.toString() + str7, f4, this.textShadowSetting.isEnabled());
        this.handleFloatType(val, flag, flag2, flag3);
        GlStateManager.enableTexture2D();
        handleFontRenderer(fontRenderer, str8, -f4, 0.0F);
        float f5 = Math.max((float)fontRenderer.FONT_HEIGHT, val.height);
        if (this.pitEnchantsSetting.isEnabled() && living instanceof EntityPlayer) {
            String str9 = this.computeString3((EntityPlayer)living);
            if (!str9.isEmpty()) {
                list.add(str9);
            }
        }

        handleFontRenderer(fontRenderer, list, f5);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private String computeString4(EntityPlayer player) {
        if (player == mc.thePlayer) {
            return "";
        }
        ToxicInfo instance = ToxicInfo.getInstance();
        if (instance == null || !instance.checkState44()) {
            return "";
        }
        if (!instance.showForOthersSetting.isEnabled()) {
            return "";
        }
        int n = instance.computeInt(player.getName());
        if (n <= 0) {
            return "";
        }
        EnumChatFormatting DARK_RED = EnumChatFormatting.DARK_RED;
        EnumChatFormatting RED = EnumChatFormatting.RED;
        return EnumChatFormatting.DARK_RED + "[" + RED + "-" + n + "%" + DARK_RED + "] ";
    }

    private String getVenomedPrefix(EntityPlayer player) {
        if (this.showVenomedTimerSetting.isEnabled() && player != Minecraft.getMinecraft().thePlayer) {
            VenomedTimer instance = VenomedTimer.getInstance();
            if (instance == null || !instance.checkState44()) {
                return "";
            }
            if (!instance.getDetectForOthersSetting().isEnabled()) {
                return "";
            }
            int n = instance.computeInt3(player.getName());
            if (n <= 0) {
                return "";
            }
            String str = instance.computeString(player.getName());
            EnumChatFormatting DARK_PURPLE = EnumChatFormatting.DARK_PURPLE;
            EnumChatFormatting LIGHT_PURPLE = EnumChatFormatting.LIGHT_PURPLE;
            return EnumChatFormatting.DARK_PURPLE + "[Venomed - " + LIGHT_PURPLE + str + "s" + DARK_PURPLE + "] ";
        }
        return "";
    }

    private static void process() {
        if (OpenGlHelper.shadersSupported) {
            OpenGlHelper.glUseProgram(0);
        }

        GL11.glDisable(GL11.GL_STENCIL_TEST);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        OpenGlHelper.setActiveTexture(33986);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.invalidateFontStateCache(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
    }

    private void handleFloat(float f, float f2, float f3, float f4, int n) {
        float f5 = (n >> 24 & 0xFF) / 255.0F;
        float f6 = (n >> 16 & 0xFF) / 255.0F;
        float f7 = ((0xFF | ~(n >> 8)) - ~(n >> 8)) / 255.0F;
        float f8 = (n & 0xFF) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.color(f6, f7, f8, f5);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldRenderer.func_181662_b(f, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f4, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f3, f2, 0.0).func_181675_d();
        worldRenderer.func_181662_b(f, f2, 0.0).func_181675_d();
        tessellator.draw();
    }

    private static Float computeFloatType(FontRenderer fontRenderer, String str, String str2, float f, boolean flag) {
        float f2 = -f + computeFloat(fontRenderer, str);
        if (!(fontRenderer instanceof CustomFontRenderer drawStringWithShadowComponent)) {
            return new Float(f2, 0.0F, computeInt(fontRenderer, str2), fontRenderer.FONT_HEIGHT - 1);
        }
        Float floatValue = drawStringWithShadowComponent.computeEntry(computeString(fontRenderer, str2)).floatValue;
        Float floatValue2 = drawStringWithShadowComponent.computeEntry("AgjpQy0123456789").floatValue;
        float f3 = Math.min(floatValue.y, floatValue2.y) - 2.0F - 0.5F;
        float f4 = Math.max(floatValue.y + floatValue.height, floatValue2.y + floatValue2.height) - 2.0F + 0.5F + (flag ? 0.5F : 0.0F);
        return new Float(f2 + floatValue.x - 0.75F, f3, floatValue.width + 1.5F + (flag ? 0.5F : 0.0F), f4 - f3);
    }

    private static float computeFloat(FontRenderer fontRenderer, String str) {
        return fontRenderer instanceof CustomFontRenderer
            ? ((CustomFontRenderer)fontRenderer).computeEntry(computeString(fontRenderer, str)).factor
            : computeInt(fontRenderer, str);
    }

    private String computeString3(EntityPlayer player) {
        long now = System.nanoTime();
        NameTags.Entry entry = this.lookupMap.get(player);
        if (entry != null && now - entry.timestamp < 250000000L) {
            return entry.text;
        }
        String str = computeString(player);
        this.lookupMap.put(player, new NameTags.Entry(str, now));
        return str;
    }

    @Generated
    public static NameTags getInstance() {
        return instance;
    }

    public NameTags() {
        super("NameTags", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.fontSetting,
                this.scaleSetting,
                this.autoScaleSetting,
                this.showDistanceSetting,
                this.showVenomedTimerSetting,
                this.showHeldItemSetting,
                this.pitEnchantsSetting,
                this.friendKOSSymbolSetting,
                this.backgroundSetting,
                this.opacitySetting,
                this.noneSetting,
                this.textShadowSetting,
                this.selfSetting
            }
        );
        instance = this;
        this.playersSetting = (BooleanSetting)this.selfSetting.computeSetting("Players");
        this.botsSetting = (BooleanSetting)this.selfSetting.computeSetting("Bots");
        this.mobsSetting = (BooleanSetting)this.selfSetting.computeSetting("Mobs");
        this.animalsSetting = (BooleanSetting)this.selfSetting.computeSetting("Animals");
        this.invisibleSetting = (BooleanSetting)this.selfSetting.computeSetting("Invisible");
        this.selfSetting2 = (BooleanSetting)this.selfSetting.computeSetting("Self");
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D && mc.theWorld != null) {
                float renderPartialTicks = mc.timer.renderPartialTicks;
                this.flag = true;

                try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                    process();

                    for (Entity entity : mc.theWorld.loadedEntityList) {
                        if (entity instanceof EntityLivingBase && !entity.isDead) {
                            EntityLivingBase living = (EntityLivingBase)entity;
                            if (ResetColorHelper.matchesAxisAlignedBB(living.getEntityBoundingBox().expand(0.5, 4.0, 0.5))) {
                                this.onEvent(
                                    new GetEntityEvent2(
                                        living,
                                        null,
                                        renderPartialTicks,
                                        entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * renderPartialTicks - mc.getRenderManager().renderPosX,
                                        entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * renderPartialTicks - mc.getRenderManager().renderPosY,
                                        entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * renderPartialTicks - mc.getRenderManager().renderPosZ
                                    )
                                );
                            }
                        }
                    }
                } finally {
                    this.flag = false;
                }
            }

            if (event instanceof GetEntityEvent2 getEntityEvent2 && getEntityEvent2.getEntity() != null) {
                EntityLivingBase entity2 = getEntityEvent2.getEntity();
                Minecraft mc2 = Minecraft.getMinecraft();
                if (entity2 == mc2.thePlayer && (mc2.gameSettings == null || mc2.gameSettings.thirdPersonView == 0)) {
                    getEntityEvent2.cancel();
                    return;
                }

                if (FPSBooster.matchesEntity(entity2)) {
                    getEntityEvent2.cancel();
                    return;
                }

                Focus instance = Focus.getInstance();
                if (instance != null && instance.checkState44()) {
                    boolean flag = entity2 == mc2.thePlayer ? instance.checkState() : !(entity2 instanceof EntityPlayer) || !instance.matchesString(entity2.getName());
                    if (flag) {
                        getEntityEvent2.cancel();
                        return;
                    }
                }

                boolean flag2 = false;
                if (entity2 == mc2.thePlayer) {
                    if (this.selfSetting2 != null && this.selfSetting2.isEnabled()) {
                        flag2 = true;
                    }
                } else if (entity2 instanceof EntityPlayer) {
                    boolean flag3 = AntiBot.matchesEntity(entity2);
                    if (flag3 && this.botsSetting != null && this.botsSetting.isEnabled()) {
                        flag2 = true;
                    } else if (!flag3 && this.playersSetting != null && this.playersSetting.isEnabled()) {
                        flag2 = true;
                    }
                } else if (entity2 instanceof EntityMob && this.mobsSetting != null && this.mobsSetting.isEnabled()) {
                    flag2 = true;
                } else if (entity2 instanceof EntityAnimal && this.animalsSetting != null && this.animalsSetting.isEnabled()) {
                    flag2 = true;
                }

                if (entity2.isInvisible() && (this.invisibleSetting == null || !this.invisibleSetting.isEnabled())) {
                    flag2 = false;
                }

                if (flag2) {
                    getEntityEvent2.cancel();
                    if (!this.flag) {
                        return;
                    }

                    this.handleEntityLivingBase(entity2, getEntityEvent2.getX(), getEntityEvent2.getY(), getEntityEvent2.getZ());
                }
            }
        }
    }

    private static int computeInt(FontRenderer fontRenderer, String str) {
        return fontRenderer.getStringWidth(computeString(fontRenderer, str));
    }

    private List<String> computeList(EntityPlayer player) {
        java.util.ArrayList list = new java.util.ArrayList();
        handleList(list, this.getVenomedPrefix(player));
        handleList(list, this.computeString2(player));
        TelebowTimer instance = TelebowTimer.getInstance();
        if (player != mc.thePlayer && instance != null) {
            handleList(list, instance.computeString(player.getName()));
        }

        handleList(list, this.getJumpBoostPrefix(player));
        PodDetection instance2 = PodDetection.getInstance();
        if (instance2 != null) {
            handleList(list, instance2.computeString(player));
        }

        handleList(list, this.computeString4(player));
        if (player != mc.thePlayer) {
            handleList(list, this.computeString(player.getName()));
        }

        if (DenickCommand.matchesString(player.getName())) {
            handleList(list, EnumChatFormatting.GREEN + "[D]");
        }

        return list;
    }

    public static final class Entry {
        public final String text;
        public final long timestamp;

        public Entry(String text, long timestamp) {
            this.text = text;
            this.timestamp = timestamp;
        }
    }
}
