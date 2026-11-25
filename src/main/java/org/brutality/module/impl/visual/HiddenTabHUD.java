package org.brutality.module.impl.visual;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.performance.InvalidateHelper;

public class HiddenTabHUD extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private static final Pattern pattern = Pattern.compile("(?i)(?:§[0-9a-fk-or])*Players?:\\s*(?:§[0-9a-fk-or])*(\\d+)");
    private static Field field = null;
    private static boolean flag = false;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null && this.mc.theWorld != null) {
            ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
            int n = this.computeInt();
            int currentPlayerCount = this.getCurrentPlayerCount();
            int n2 = n - currentPlayerCount;
            String str = Math.abs(n2) == 1 ? "Player " : "Players ";
            String str2 = n2 == 0 ? "§f" : (n2 > 0 ? "§a+" : "§c");
            String str3 = str;
            String str4 = "§fVanished " + str3 + str2 + n2;
            int n3 = sr.getScaledWidth() / 2;
            byte b = 0;
            computeFontRenderer(HiddenTabHUD.class)
                .drawStringWithShadow(
                    str4,
                    n3 - computeFontRenderer(HiddenTabHUD.class).getStringWidth(str4) / 2.0F,
                    b + 2,
                    n2 > 0 ? 5635925 : (n2 < 0 ? 16733525 : 16777215)
                );
        }
    }

    private String computeString() {
        try {
            GuiPlayerTabOverlay tabList = this.mc.ingameGUI.getTabList();
            if (tabList == null) {
                return "";
            }
            if (flag) {
                if (field != null) {
                    IChatComponent component = (IChatComponent)field.get(tabList);
                    if (component != null) {
                        return component.getFormattedText();
                    }
                }

                return "";
            }
            String[] parts = new String[]{"footer", "field_175252_a", "field_175255_h"};

            for (String str : parts) {
                try {
                    Field declaredField = GuiPlayerTabOverlay.class.getDeclaredField(str);
                    declaredField.setAccessible(true);
                    IChatComponent component2 = (IChatComponent)declaredField.get(tabList);
                    if (component2 != null) {
                        field = declaredField;
                        flag = true;
                        return component2.getFormattedText();
                    }
                } catch (NoSuchFieldException ignored) {
                } catch (IllegalAccessException e) {
                    return "";
                }
            }

            flag = true;
            return "";
        } catch (Exception e2) {
            return "";
        }
    }

    public HiddenTabHUD() {
        super("HiddenTabHUD", 0, Category.VISUAL);
        this.process18();
    }

    private int getCurrentPlayerCount() {
        if (this.mc.thePlayer != null && this.mc.getNetHandler() != null) {
            Collection playerInfoMap = this.mc.getNetHandler().getPlayerInfoMap();
            return playerInfoMap != null ? playerInfoMap.size() : 0;
        }
        return 0;
    }

    private int computeInt() {
        if (this.mc.thePlayer != null && this.mc.getNetHandler() != null && this.mc.getNetHandler().getPlayerInfoMap() != null) {
            String str = this.computeString();
            if (str != null && !str.isEmpty()) {
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    try {
                        return Integer.parseInt(matcher.group(1));
                    } catch (NumberFormatException ignored) {
                    }
                }

                return 0;
            }
            return 0;
        }
        return 0;
    }
}
