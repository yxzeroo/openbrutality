package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.regex.Pattern;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.chat.GetPersistentLineCountHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.UpdateAnimationsHelper;

public class Chat extends Module {
    private static Chat instance;
    private final BooleanSetting animationsSetting = new BooleanSetting("Animations", this, true);
    private final NumberSetting chatScaleSetting = new NumberSetting("Chat Scale", this, 1.0, 0.5, 2.0, 0.1);
    private final NumberSetting chatOpacitySetting = new NumberSetting("Chat Opacity", this, 1.0, 0.1, 1.0, 0.1);
    private final NumberSetting backgroundOpacitySetting = new NumberSetting("Background Opacity", this, 0.5, 0.0, 1.0, 0.1);
    private final NumberSetting chatWidthSetting = new NumberSetting("Chat Width", this, 1.0, 0.5, 2.0, 0.1);
    private final NumberSetting chatHeightSetting = new NumberSetting("Chat Height", this, 1.0, 0.5, 2.0, 0.1);
    private final NumberSetting focusedHeightSetting = new NumberSetting("Focused Height", this, 1.0, 0.5, 2.0, 0.1);
    private final BooleanSetting smoothChatSetting = new BooleanSetting("Smooth Chat", this, false);
    private final BooleanSetting chatShadowSetting = new BooleanSetting("Chat Shadow", this, true);
    private final BooleanSetting infiniteChatSetting = new BooleanSetting("Infinite Chat", this, false);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false);
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this)
        .computeModeSetting(() -> this.customFontSetting.isEnabled() && !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting chatXSetting = new NumberSetting("Chat X", this, 2.0, 0.0, 100.0, 0.01);
    private final NumberSetting chatYSetting = new NumberSetting("Chat Y", this, 20.0, 0.0, 100.0, 0.01);
    private final BooleanSetting defaultXYSetting = new BooleanSetting("Default X & Y", this, false);
    private final BooleanSetting highlightMentionsSetting = new BooleanSetting("Highlight Mentions", this, true);
    private final ModeSetting bothSetting = new ModeSetting("Mention Style", this, "Background", Arrays.asList("Background", "Text Color", "Both"));
    private final NumberSetting mentionRedSetting = new NumberSetting("Mention Red", this, 255.0, 0.0, 255.0, 1.0)
        .computeNumberSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting mentionGreenSetting = new NumberSetting("Mention Green", this, 255.0, 0.0, 255.0, 1.0)
        .computeNumberSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting mentionBlueSetting = new NumberSetting("Mention Blue", this, 0.0, 0.0, 255.0, 1.0)
        .computeNumberSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private final NumberSetting mentionAlphaSetting = new NumberSetting("Mention Alpha", this, 100.0, 0.0, 255.0, 1.0);
    private final BooleanSetting mentionSoundSetting = new BooleanSetting("Mention Sound", this, true);
    private final BooleanSetting caseSensitiveSetting = new BooleanSetting("Case Sensitive", this, false);
    private Pattern pattern;
    private String text = "";
    private CustomFontRenderer drawStringWithShadowComponent;
    private String text2 = "";

    public IChatComponent computeIChatComponent2(IChatComponent component) {
        if (this.highlightMentionsSetting.isEnabled() && component != null) {
            String text = component.getUnformattedText();
            if (this.matchesString(text)) {
                if (this.mentionSoundSetting.isEnabled()) {
                    mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
                }

                return this.computeIChatComponent(component);
            }
            return component;
        }
        return component;
    }

    public float computeFloat2() {
        return this.checkState44() && !this.defaultXYSetting.isEnabled() ? (float)this.chatYSetting.computeDouble() : 20.0F;
    }

    private void process4() {
        if (!this.customFontSetting.isEnabled()) {
            this.drawStringWithShadowComponent = null;
            this.text2 = "";
        } else {
            String str;
            if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
                BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
                str = brutalityHelper != null ? brutalityHelper.getText() : "Brutality";
            } else {
                str = this.fontSetting.getText();
            }

            if (this.drawStringWithShadowComponent == null || !str.equalsIgnoreCase(this.text2)) {
                this.text2 = str;

                try {
                    this.drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(str, 18.0F);
                } catch (Exception e) {
                    this.drawStringWithShadowComponent = null;
                }
            }
        }
    }

    private void process5() {
        if (mc.gameSettings != null) {
            UpdateAnimationsHelper.setEnabled(this.animationsSetting.isEnabled());
            mc.gameSettings.chatScale = (float)this.chatScaleSetting.computeDouble();
            mc.gameSettings.chatOpacity = (float)this.chatOpacitySetting.computeDouble();
            mc.gameSettings.chatWidth = (float)this.chatWidthSetting.computeDouble();
            mc.gameSettings.chatHeightUnfocused = (float)this.chatHeightSetting.computeDouble();
            mc.gameSettings.chatHeightFocused = (float)this.focusedHeightSetting.computeDouble();
            GetPersistentLineCountHelper.setEnabled(this.infiniteChatSetting.isEnabled());
        }
    }

    public boolean checkState5() {
        return this.checkState44() && this.infiniteChatSetting.isEnabled();
    }

    @Override
    public void process28() {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process5();
        this.process();
    }

    private EnumChatFormatting computeEnumChatFormatting() {
        int n = (int)this.mentionRedSetting.computeDouble();
        int n2 = (int)this.mentionGreenSetting.computeDouble();
        int n3 = (int)this.mentionBlueSetting.computeDouble();
        if (n > 200 && n2 < 100 && n3 < 100) {
            return EnumChatFormatting.RED;
        }
        if (n < 100 && n2 > 200 && n3 < 100) {
            return EnumChatFormatting.GREEN;
        }
        if (n < 100 && n2 < 100 && n3 > 200) {
            return EnumChatFormatting.BLUE;
        }
        if (n > 200 && n2 > 200 && n3 < 100) {
            return EnumChatFormatting.YELLOW;
        }
        if (n > 200 && n2 < 100 && n3 > 200) {
            return EnumChatFormatting.LIGHT_PURPLE;
        }
        if (n < 100 && n2 > 200 && n3 > 200) {
            return EnumChatFormatting.AQUA;
        }
        if (n > 150 && n2 > 150 && n3 > 150) {
            return EnumChatFormatting.WHITE;
        }
        return n < 100 && n2 < 100 && n3 < 100 ? EnumChatFormatting.DARK_GRAY : EnumChatFormatting.GOLD;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process3();
    }

    public CustomFontRenderer computeDrawStringWithShadowComponent() {
        this.process4();
        return this.drawStringWithShadowComponent;
    }

    public boolean checkState() {
        return ModuleSpecificHelper.matchesModeSetting(this.modeSetting);
    }

    public float computeFloat() {
        return this.checkState44() && !this.defaultXYSetting.isEnabled() ? (float)this.chatXSetting.computeDouble() : 2.0F;
    }

    public String computeString() {
        return this.bothSetting.getText();
    }

    public boolean checkState4() {
        return this.checkState44() && this.mentionSoundSetting.isEnabled();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.process5();
                this.process();
            }
        }
    }

    public boolean checkState2() {
        return this.checkState44() && this.smoothChatSetting.isEnabled();
    }

    private void process() {
        if (mc.thePlayer != null) {
            String name = mc.thePlayer.getName();
            if (!name.equals(this.text)) {
                this.text = name;
                String str = "\\b" + Pattern.quote(name) + "\\b";
                int n = this.caseSensitiveSetting.isEnabled() ? 0 : 2;
                this.pattern = Pattern.compile(str, n);
            }
        }
    }

    public boolean checkState9() {
        return this.checkState44() && this.highlightMentionsSetting.isEnabled();
    }

    public static Chat getInstance() {
        return instance;
    }

    public boolean checkState11() {
        return this.checkState44();
    }

    public float computeFloat3() {
        return (float)this.backgroundOpacitySetting.computeDouble();
    }

    public boolean matchesString(String str) {
        return this.highlightMentionsSetting.isEnabled() && this.pattern != null && str != null ? this.pattern.matcher(str).find() : false;
    }

    public boolean checkState8() {
        return this.checkState44() && this.customFontSetting.isEnabled() && this.computeDrawStringWithShadowComponent() != null;
    }

    private IChatComponent computeIChatComponent(IChatComponent component) {
        String text = this.bothSetting.getText();
        ChatComponentText chatComponentText = new ChatComponentText(component.getFormattedText());
        switch (text) {
            case "Text Color":
                EnumChatFormatting enumChatFormatting = this.computeEnumChatFormatting();
                chatComponentText.getChatStyle().setColor(enumChatFormatting);
                break;
            case "Background":
                chatComponentText.getChatStyle().setBold(true);
                break;
            case "Both":
                EnumChatFormatting enumChatFormatting2 = this.computeEnumChatFormatting();
                chatComponentText.getChatStyle().setColor(enumChatFormatting2);
                chatComponentText.getChatStyle().setBold(true);
        }

        return chatComponentText;
    }

    public Color computeColor() {
        if (ModuleSpecificHelper.matchesModeSetting(this.modeSetting)) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            if (brutalityHelper != null) {
                Color color = brutalityHelper.getColor();
                return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)this.mentionAlphaSetting.computeDouble());
            }
        }

        return new Color(
            (int)this.mentionRedSetting.computeDouble(),
            (int)this.mentionGreenSetting.computeDouble(),
            (int)this.mentionBlueSetting.computeDouble(),
            (int)this.mentionAlphaSetting.computeDouble()
        );
    }

    public Chat() {
        super("Chat", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.animationsSetting,
                this.chatScaleSetting,
                this.chatOpacitySetting,
                this.backgroundOpacitySetting,
                this.chatWidthSetting,
                this.chatHeightSetting,
                this.focusedHeightSetting,
                this.smoothChatSetting,
                this.chatShadowSetting,
                this.infiniteChatSetting,
                this.chatXSetting,
                this.chatYSetting,
                this.defaultXYSetting,
                this.modeSetting,
                this.customFontSetting,
                this.fontSetting,
                this.highlightMentionsSetting,
                this.bothSetting,
                this.mentionRedSetting,
                this.mentionGreenSetting,
                this.mentionBlueSetting,
                this.mentionAlphaSetting,
                this.mentionSoundSetting,
                this.caseSensitiveSetting
            }
        );
        instance = this;
        this.handleFlag3(true);
    }

    public boolean checkState6() {
        return !this.checkState44() || this.chatShadowSetting.isEnabled();
    }

    private void process3() {
        if (mc.gameSettings != null) {
            UpdateAnimationsHelper.setEnabled(true);
            mc.gameSettings.chatScale = 1.0F;
            mc.gameSettings.chatOpacity = 1.0F;
            mc.gameSettings.chatWidth = 1.0F;
            mc.gameSettings.chatHeightUnfocused = 0.44444445F;
            mc.gameSettings.chatHeightFocused = 1.0F;
            GetPersistentLineCountHelper.setEnabled(false);
        }
    }
}
