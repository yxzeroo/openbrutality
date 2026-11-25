package org.brutality.module.impl.pit.hypixel.hud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.lwjgl.input.Mouse;

public class RGMDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 70.0, 0.0, 1200.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting detectOthersRGMSetting = new BooleanSetting("Detect Others RGM", this, true);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final BooleanSetting showSecondsSuffixSetting = new BooleanSetting("Show Seconds Suffix", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Time Format", this, "##.#", Arrays.asList("##", "##.#"));
    private final BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, true);
    private final ModeSetting fontSetting = IsChoiceHelper.computeModeSetting("Font", this).computeModeSetting(() -> this.customFontSetting.isEnabled());
    private final Pattern pattern = Pattern.compile("RGM! Procced against (.+)!");
    private final Map<String, List<RGMDamage.Entry>> lookupMap = new HashMap<>();
    private String text = "";
    private final Map<String, List<Long>> lookupMap2 = new HashMap<>();
    private final Map<String, Integer> lookupMap3 = new HashMap<>();

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        String str = this.modeSetting.matchesString2("##") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
        return str + (this.showSecondsSuffixSetting.isEnabled() ? "s" : "");
    }

    private void process() {
        var it = this.lookupMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            List<RGMDamage.Entry> val = (List<RGMDamage.Entry>)entry.getValue();
            val.removeIf(RGMDamage.Entry::isExpired);
            if (val.isEmpty()) {
                it.remove();
            }
        }
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        float f = (float)this.xPosSetting.computeDouble();
        float f2 = (float)this.yPosSetting.computeDouble();
        boolean flag = this.matchesEntityPlayerSP(mc.thePlayer);
        boolean flag2 = mc.currentScreen instanceof GuiChat;
        boolean flag3 = flag || flag2 && this.showInChatSetting.isEnabled();
        if (!flag3) {
            HudDragHelper.handleObject(this);
        } else {
            this.process();
            FontRenderer fontRenderer = this.computeFontRenderer(mc);
            int FONT_HEIGHT = fontRenderer.FONT_HEIGHT;
            if (!this.lookupMap.isEmpty()) {
                int n = 0;

                for (List list : this.lookupMap.values()) {
                    n += list.size();
                }

                FONT_HEIGHT = (n + 1) * (fontRenderer.FONT_HEIGHT + 2);
            }

            if (flag2) {
                int[] ints = HudDragHelper.computeIntArray();
                int n2 = ints[0];
                int n3 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this)) {
                        String str = "Retro: ";
                        int n4 = fontRenderer.getStringWidth(str) + 100;
                        if (HudDragHelper.matchesInt(n2, n3, f, f2, n4, FONT_HEIGHT)) {
                            HudDragHelper.matchesObject(this, n2, n3, (int)f, (int)f2);
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xPosSetting.handleDouble2(ints2[0]);
                        this.yPosSetting.handleDouble2(ints2[1]);
                        f = ints2[0];
                        f2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            if (flag) {
                this.renderRGMText(mc, fontRenderer, f, f2);
            } else if (flag2 && this.showInChatSetting.isEnabled()) {
                String str2 = this.showLabelSetting.isEnabled() ? "Retro: " : "";
                fontRenderer.drawStringWithShadow(str2, f, f2, 16777215);
                fontRenderer.drawStringWithShadow("No Active", f + fontRenderer.getStringWidth(str2), f2, 16733525);
            }
        }
    }

    private int computeInt(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.hasTagCompound()) {
            String str = stack.getTagCompound().toString();
            if (str.contains("Retro-Gravity Microcosm III")) {
                return 3;
            }

            if (str.contains("Retro-Gravity Microcosm II")) {
                return 2;
            }

            if (str.contains("Retro-Gravity Microcosm")) {
                return 1;
            }
        }

        return 0;
    }

    public RGMDamage() {
        super("RGMDamage", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.showInChatSetting,
                this.detectOthersRGMSetting,
                this.showLabelSetting,
                this.showSecondsSuffixSetting,
                this.modeSetting,
                this.customFontSetting,
                this.fontSetting
            }
        );
    }

    private boolean matchesEntityPlayerSP(EntityPlayerSP player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;
        if (armorInventory[1] != null) {
            ItemStack stack = armorInventory[1];
            if (stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                return tagCompound.toString().contains("Retro");
            }
        }

        return false;
    }

    private void renderRGMText(Minecraft mc, FontRenderer fontRenderer, float f, float f2) {
        String str = this.showLabelSetting.isEnabled() ? "Retro: " : "";
        fontRenderer.drawStringWithShadow(str, f, f2, 16777215);
        if (this.lookupMap.isEmpty()) {
            fontRenderer.drawStringWithShadow("No Active", f + fontRenderer.getStringWidth(str), f2, 16733525);
        } else {
            List<RGMDamage.Entry> list = new ArrayList<>();

            for (List list2 : this.lookupMap.values()) {
                for (RGMDamage.Entry entry : ((Iterable<RGMDamage.Entry>)(Object)(list2))) {
                    if (!entry.isExpired()) {
                        list.add(entry);
                    }
                }
            }

            if (list.isEmpty()) {
                fontRenderer.drawStringWithShadow("No Active", f + fontRenderer.getStringWidth("Retro: "), f2, 16733525);
            } else {
                list.sort(Comparator.comparingLong(entry2 -> entry2.timestamp));
                String str2 = ((RGMDamage.Entry)list.get(0)).computeString(this.computeString(((RGMDamage.Entry)list.get(0)).computeLong()));
                fontRenderer.drawStringWithShadow(str2, f + fontRenderer.getStringWidth(str), f2, 16733525);
                float f3 = f2 + fontRenderer.FONT_HEIGHT + 2.0F;

                for (int i = 1; i < list.size(); i++) {
                    String str3 = ((RGMDamage.Entry)list.get(i)).computeString(this.computeString(((RGMDamage.Entry)list.get(i)).computeLong()));
                    fontRenderer.drawStringWithShadow(str3, f + fontRenderer.getStringWidth(str), f3, 16733525);
                    f3 += fontRenderer.FONT_HEIGHT + 2;
                }
            }
        }
    }

    private int computeInt(EntityPlayerSP player) {
        ItemStack[] armorInventory = player.inventory.armorInventory;
        if (armorInventory[1] != null) {
            ItemStack stack = armorInventory[1];
            if (stack.hasTagCompound()) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                String str = tagCompound.toString();
                if (str.contains("Retro-Gravity Microcosm III")) {
                    return 3;
                }

                if (str.contains("Retro-Gravity Microcosm II")) {
                    return 2;
                }

                if (str.contains("Retro-Gravity Microcosm")) {
                    return 1;
                }
            }
        }

        return 0;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof ChatEvent) {
                String text = ((ChatEvent)event).message.getUnformattedText();
                Matcher matcher = this.pattern.matcher(text);
                if (matcher.find()) {
                    String str = matcher.group(1);
                    String str2 = str.replaceAll("§[0-9a-fk-or]", "").trim();
                    int n = this.computeInt(Minecraft.getMinecraft().thePlayer);
                    if (n > 1) {
                        RGMDamage.Entry entry = new RGMDamage.Entry(str2, n, System.currentTimeMillis());
                        var list = this.lookupMap.computeIfAbsent(str2, str3 -> new ArrayList<>());
                        list.add(entry);
                    }
                }
            }

            if (event instanceof GetDamageEvent) {
                Minecraft mc = Minecraft.getMinecraft();
                GetDamageEvent getDamageEvent = (GetDamageEvent)event;
                if (getDamageEvent.getEntity() != mc.thePlayer && getDamageEvent.getEntity2() == mc.thePlayer) {
                    this.text = getDamageEvent.getEntity().getName();
                    if (this.detectOthersRGMSetting.isEnabled() && getDamageEvent.getEntity() instanceof EntityPlayer) {
                        EntityPlayer entity = (EntityPlayer)getDamageEvent.getEntity();
                        String name = entity.getName();
                        if (!entity.onGround) {
                            long now = System.currentTimeMillis();
                            var list2 = this.lookupMap2.computeIfAbsent(name, str4 -> new ArrayList<>());
                            if (!list2.isEmpty()) {
                                long l = (Long)list2.get(list2.size() - 1);
                                if (now - l < 1000L) {
                                    return;
                                }
                            }

                            list2.add(now);
                            list2.removeIf(val -> now - val > 10000L);
                            if (list2.size() >= 3) {
                                int n2 = this.computeInt(entity);
                                if (n2 > 0) {
                                    this.lookupMap3.put(name, n2);
                                    String str5 = this.computeString(n2);
                                    String str6 = MessageUtils.text16;
                                    MessageUtils.x(MessageUtils.text13 + "[RGM] " + str6 + name + " procced RGM " + n2 + " on you! " + str5);
                                }

                                list2.clear();
                            }
                        } else {
                            this.lookupMap2.remove(name);
                        }
                    }
                }
            }
        }
    }

    private FontRenderer computeFontRenderer(Minecraft mc) {
        if (this.customFontSetting.isEnabled()) {
            String text = this.fontSetting.getText();
            CustomFontRenderer drawStringWithShadowComponent = FontResourceLoader.computeDrawStringWithShadowComponent2(text, 18.0F);
            return (FontRenderer)(drawStringWithShadowComponent != null ? drawStringWithShadowComponent : mc.fontRendererObj);
        }
        return mc.fontRendererObj;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
        this.lookupMap.clear();
    }

    private String computeString(int n) {
        switch (n) {
            case 1:
                return "Heals 1.25hp";
            case 2:
                return "Heals 1.25hp + 1.5hp extra dmg for 30s";
            case 3:
                return "Heals 1.25hp + 1.5hp extra dmg for 30s + 0.5hp true dmg";
            default:
                return "";
        }
    }

    public static class Entry {
        private final String text;
        private final int count;
        public final long timestamp;
        private static final long timestamp2 = 30000L;

        public String computeString(String str) {
            double d = 1.5;
            String str2 = "";
            if (this.count >= 3) {
                str2 = " +10% True";
            }

            return this.text + " +" + d + "♥" + str2 + " (" + str + ")";
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - this.timestamp > 30000L;
        }

        public Entry(String text, int count, long timestamp) {
            this.text = text;
            this.count = count;
            this.timestamp = timestamp;
        }

        public long computeLong() {
            long l = System.currentTimeMillis() - this.timestamp;
            return Math.max(0L, 30000L - l);
        }
    }
}
