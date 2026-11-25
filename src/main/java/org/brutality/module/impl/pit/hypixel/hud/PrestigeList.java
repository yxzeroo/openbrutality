package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;

public class PrestigeList extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 10.0, 0.0, 1000.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1000.0, 1.0);
    private final BooleanSetting showPrestige0Setting = new BooleanSetting("Show Prestige 0", this, true);
    private final BooleanSetting showPrestige14Setting = new BooleanSetting("Show Prestige 1-4", this, true);
    private final BooleanSetting showPrestige59Setting = new BooleanSetting("Show Prestige 5-9", this, true);
    private final BooleanSetting showPrestige1014Setting = new BooleanSetting("Show Prestige 10-14", this, true);
    private final BooleanSetting showPrestige1519Setting = new BooleanSetting("Show Prestige 15-19", this, true);
    private final BooleanSetting showPrestige2024Setting = new BooleanSetting("Show Prestige 20-24", this, true);
    private final BooleanSetting showPrestige2529Setting = new BooleanSetting("Show Prestige 25-29", this, true);
    private final BooleanSetting showPrestige3034Setting = new BooleanSetting("Show Prestige 30-34", this, true);
    private final BooleanSetting showPrestige3539Setting = new BooleanSetting("Show Prestige 35-39", this, true);
    private final BooleanSetting showLocationSetting = new BooleanSetting("Show Location", this, true);
    private final BooleanSetting onlyShowOnPitSetting = new BooleanSetting("Only Show On Pit", this, true);
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                FreshHelper.process();
            }

            if (event instanceof EventRender2D) {
                if (this.onlyShowOnPitSetting.isEnabled() && !FreshHelper.flag) {
                    return;
                }

                float f = (float)this.xPosSetting.computeDouble();
                float f2 = (float)this.yPosSetting.computeDouble();
                var players = this.computeList();
                boolean flag = this.mc.currentScreen instanceof GuiChat;
                boolean flag2 = !players.isEmpty();
                if (!flag2 && !flag) {
                    HudDragHelper.handleObject(this);
                    return;
                }

                if (flag) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n = ints[0];
                    int n2 = ints[1];
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this)) {
                            int n3 = 0;
                            int n4 = Math.max(1, players.size()) * computeFontRenderer(PrestigeList.class).FONT_HEIGHT;

                            for (EntityPlayer player : players) {
                                String text = player.getDisplayName().getFormattedText();
                                String str = this.computeString(player);
                                String str2 = this.showLocationSetting.isEnabled() ? " - " + this.computeString2(player) : "";
                                String str3 = String.format("%s - %s%s", text, str, str2);
                                int textWidth = computeFontRenderer(PrestigeList.class).getStringWidth(str3);
                                if (textWidth > n3) {
                                    n3 = textWidth;
                                }
                            }

                            if (players.isEmpty()) {
                                n3 = computeFontRenderer(PrestigeList.class).getStringWidth("Prestige List");
                            }

                            if (HudDragHelper.matchesInt(n, n2, f, f2, n3, n4)) {
                                HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
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
                }

                if (flag2) {
                    int n5 = (int)f2;

                    for (EntityPlayer player2 : players) {
                        String text2 = player2.getDisplayName().getFormattedText();
                        String str4 = this.computeString(player2);
                        String str5 = "";
                        if (this.showLocationSetting.isEnabled()) {
                            str5 = " - " + this.computeString2(player2);
                        }

                        int n6 = this.computeInt(player2, str4);
                        String str6 = String.format("%s - %s%s", text2, str4, str5);
                        computeFontRenderer(PrestigeList.class).drawStringWithShadow(str6, f, n5, n6);
                        n5 += computeFontRenderer(PrestigeList.class).FONT_HEIGHT;
                    }
                } else if (flag) {
                    computeFontRenderer(PrestigeList.class).drawStringWithShadow("Prestige List", f, f2, Color.GRAY.getRGB());
                }
            }
        }
    }

    private int computeInt(EntityPlayer player, String str) {
        if (FreshHelper.matchesWorld2(this.mc.theWorld, player)) {
            return Color.GREEN.getRGB();
        }
        if (FreshHelper.matchesWorld4(this.mc.theWorld, player)) {
            return Color.RED.getRGB();
        }
        switch (str) {
            case "Diamond":
                return Color.CYAN.getRGB();
            case "Chain":
                return Color.GRAY.getRGB();
            default:
                return Color.WHITE.getRGB();
        }
    }

    public PrestigeList() {
        super("Prestige List", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.showPrestige0Setting,
                this.showPrestige14Setting,
                this.showPrestige59Setting,
                this.showPrestige1014Setting,
                this.showPrestige1519Setting,
                this.showPrestige2024Setting,
                this.showPrestige2529Setting,
                this.showPrestige3034Setting,
                this.showPrestige3539Setting,
                this.showLocationSetting,
                this.onlyShowOnPitSetting
            }
        );
    }

    private List<EntityPlayer> computeList() {
        List list = new ArrayList();
        HashSet set = new HashSet();

        for (EntityPlayer player : EntityCache.getEntries()) {
            if (set.add(player.getName()) && this.matchesEntityPlayer(player) && !player.equals(this.mc.thePlayer)) {
                list.add(player);
            }
        }

        return list;
    }

    private String computeString2(EntityPlayer player) {
        if (this.mc.theWorld == null) {
            return "Unknown";
        }
        String str = FreshHelper.computeString(player, this.mc.theWorld);
        if (FreshHelper.matchesWorld2(this.mc.theWorld, player)) {
            return "§a§lSpawn";
        }
        if (FreshHelper.matchesWorld5(this.mc.theWorld, player)) {
            return "§e§lAbove Spawn";
        }
        if (FreshHelper.matchesWorld4(this.mc.theWorld, player)) {
            return "§c§lPIT";
        }
        return FreshHelper.matchesWorld(this.mc.theWorld, player, 33.0) ? "§6§lNear PIT" : str.replaceAll("§.", "");
    }

    private String computeString(EntityPlayer player) {
        boolean flag = false;
        boolean flag2 = false;

        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                ItemArmor item = (ItemArmor)stack.getItem();
                if (item.getArmorMaterial() == ArmorMaterial.DIAMOND) {
                    flag = true;
                } else if (item.getArmorMaterial() == ArmorMaterial.CHAIN) {
                    flag2 = true;
                }
            }
        }

        if (flag) {
            return "Diamond";
        }
        return flag2 ? "Chain" : "None";
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        String text = player.getDisplayName().getFormattedText();
        return this.showPrestige0Setting.isEnabled() && text.contains("§7[")
            || this.showPrestige14Setting.isEnabled() && text.contains("§9[")
            || this.showPrestige59Setting.isEnabled() && text.contains("§e[")
            || this.showPrestige1014Setting.isEnabled() && text.contains("§6[")
            || this.showPrestige1519Setting.isEnabled() && text.contains("§c[")
            || this.showPrestige2024Setting.isEnabled() && text.contains("§5[")
            || this.showPrestige2529Setting.isEnabled() && text.contains("§d[")
            || this.showPrestige3034Setting.isEnabled() && text.contains("§f[")
            || this.showPrestige3539Setting.isEnabled() && text.contains("§b[");
    }
}
