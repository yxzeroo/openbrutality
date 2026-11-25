package org.brutality.module.impl.visual;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.KeybindListSetting;
import org.brutality.utils.DefaultHelper;
import org.brutality.utils.WHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;

public class Tracers extends Module {
    private static final List<String> entries = Arrays.asList(
        "Venom",
        "Misery",
        "Spite",
        "Needless Suffering",
        "Grim Reaper",
        "Nostalgia",
        "Hedge Fund",
        "Heartripper",
        "Sanguisuge",
        "Lycanthropy",
        "Mind Assault",
        "Golden Handcuffs"
    );
    private static List<String> entries2 = new java.util.ArrayList<>();
    private static long timestamp = 0L;
    private static final long timestamp2 = 500L;
    private static final Map<EntityPlayer, String> w = new HashMap<>();
    private static final Map<EntityPlayer, Long> x = new HashMap<>();
    private static final long timestamp3 = 500L;
    private final BooleanSetting showInvisibleSetting = new BooleanSetting("Show Invisible", this, false);
    private final BooleanSetting kosOnlySetting = new BooleanSetting("KOS Only", this, false);
    private final BooleanSetting ignoreFriendsSetting = new BooleanSetting("Ignore Friends", this, false);
    private final BooleanSetting showKOSSetting = new BooleanSetting("Show KOS", this, true);
    private final BooleanSetting showFriendsSetting = new BooleanSetting("Show Friends", this, true);
    private final BooleanSetting showDarkPantsSetting = new BooleanSetting("Show Dark Pants", this, true);
    private final BooleanSetting showNeutralSetting = new BooleanSetting("Show Neutral", this, true);
    private final BooleanSetting venomSetting = new BooleanSetting("Venom", this, true);
    private final BooleanSetting miserySetting = new BooleanSetting("Misery", this, true);
    private final BooleanSetting spiteSetting = new BooleanSetting("Spite", this, true);
    private final BooleanSetting needlessSufferingSetting = new BooleanSetting("Needless Suffering", this, true);
    private final BooleanSetting grimReaperSetting = new BooleanSetting("Grim Reaper", this, true);
    private final BooleanSetting nostalgiaSetting = new BooleanSetting("Nostalgia", this, true);
    private final BooleanSetting hedgeFundSetting = new BooleanSetting("Hedge Fund", this, true);
    private final BooleanSetting heartripperSetting = new BooleanSetting("Heartripper", this, true);
    private final BooleanSetting sanguisugeSetting = new BooleanSetting("Sanguisuge", this, true);
    private final BooleanSetting lycanthropySetting = new BooleanSetting("Lycanthropy", this, true);
    private final BooleanSetting mindAssaultSetting = new BooleanSetting("Mind Assault", this, true);
    private final BooleanSetting goldenHandcuffsSetting = new BooleanSetting("Golden Handcuffs", this, true);
    private final BooleanSetting syncThemeSetting = new BooleanSetting("Sync Theme", this, false);
    private final ColorSetting colorSetting = new ColorSetting("Color", this, 0.0F).computeColorSetting(() -> !this.syncThemeSetting.isEnabled());
    private final KeybindListSetting playerTypesSetting = new KeybindListSetting(
        "Player Types", this, this.showKOSSetting, this.showFriendsSetting, this.showDarkPantsSetting, this.showNeutralSetting
    );
    private final KeybindListSetting darkDetectionSetting = new KeybindListSetting(
        "Dark Detection",
        this,
        this.venomSetting,
        this.miserySetting,
        this.spiteSetting,
        this.needlessSufferingSetting,
        this.grimReaperSetting,
        this.nostalgiaSetting,
        this.hedgeFundSetting,
        this.heartripperSetting,
        this.sanguisugeSetting,
        this.lycanthropySetting,
        this.mindAssaultSetting,
        this.goldenHandcuffsSetting
    );
    private final KeybindListSetting colorSetting2 = new KeybindListSetting("Color", this, this.syncThemeSetting, this.colorSetting);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventRender3D event2) {
                float factor = event2.getFactor();

                for (EntityPlayer player : EntityCache.getEntries()) {
                    if (player != mc.thePlayer
                        && player.isEntityAlive()
                        && (!player.isInvisible() || this.showInvisibleSetting.isEnabled())
                        && !(player.getDistanceToEntity(mc.thePlayer) > 100.0F)) {
                        boolean flag = DefaultHelper.matchesString(player.getName());
                        if (!this.ignoreFriendsSetting.isEnabled() || !flag) {
                            boolean flag2 = WHelper.matchesString(player.getName());
                            boolean flag3 = this.computeString(player) != null;
                            if ((!this.kosOnlySetting.isEnabled() || flag2) && this.matchesEntityPlayer(player, flag2, flag, flag3)) {
                                double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * factor - mc.getRenderManager().renderPosX;
                                double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * factor - mc.getRenderManager().renderPosY;
                                double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * factor - mc.getRenderManager().renderPosZ;
                                boolean viewBobbing = mc.gameSettings.viewBobbing;
                                ResetColorHelper.process12();
                                mc.gameSettings.viewBobbing = false;
                                mc.entityRenderer.setupCameraTransform(mc.timer.renderPartialTicks, 2);
                                mc.gameSettings.viewBobbing = viewBobbing;
                                double[] doubles = this.computeDoubleArray(player, flag2, flag, flag3);
                                ResetColorHelper.handleEntity(player, doubles, d, d2 + player.getEyeHeight(), d3);
                                ResetColorHelper.process2();
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean matchesString(String str) {
        switch (str) {
            case "Venom":
                return this.venomSetting.isEnabled();
            case "Misery":
                return this.miserySetting.isEnabled();
            case "Spite":
                return this.spiteSetting.isEnabled();
            case "Needless Suffering":
                return this.needlessSufferingSetting.isEnabled();
            case "Grim Reaper":
                return this.grimReaperSetting.isEnabled();
            case "Nostalgia":
                return this.nostalgiaSetting.isEnabled();
            case "Hedge Fund":
                return this.hedgeFundSetting.isEnabled();
            case "Heartripper":
                return this.heartripperSetting.isEnabled();
            case "Sanguisuge":
                return this.sanguisugeSetting.isEnabled();
            case "Lycanthropy":
                return this.lycanthropySetting.isEnabled();
            case "Mind Assault":
                return this.mindAssaultSetting.isEnabled();
            case "Golden Handcuffs":
                return this.goldenHandcuffsSetting.isEnabled();
            default:
                return true;
        }
    }

    private String computeString(EntityPlayer player) {
        long now = System.currentTimeMillis();
        Long val = x.get(player);
        if (val != null && now - val < 500L) {
            return w.get(player);
        }
        x.put(player, now);

        for (int i = 0; i < 4; i++) {
            ItemStack stack = player.inventory.armorInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int j = 0; j < tagList.tagCount(); j++) {
                            String stringTagAt = tagList.getStringTagAt(j);

                            for (String str : entries) {
                                if (stringTagAt.contains(str) && this.matchesString(str)) {
                                    w.put(player, str);
                                    return str;
                                }
                            }
                        }
                    }
                }
            }
        }

        w.put(player, null);
        return null;
    }

    private double[] computeDoubleArray(EntityPlayer player, boolean flag, boolean flag2, boolean flag3) {
        Color color;
        if (this.syncThemeSetting.isEnabled()) {
            BrutalityHelper brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper();
            color = brutalityHelper != null ? brutalityHelper.getColor() : Color.WHITE;
        } else {
            color = this.colorSetting.computeColor();
        }

        return new double[]{color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0};
    }

    public Tracers() {
        super("Tracers", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.showInvisibleSetting,
                this.kosOnlySetting,
                this.ignoreFriendsSetting,
                this.playerTypesSetting,
                this.darkDetectionSetting,
                this.colorSetting2
            }
        );
    }

    private boolean matchesEntityPlayer(EntityPlayer player, boolean flag, boolean flag2, boolean flag3) {
        if (flag && this.showKOSSetting.isEnabled()) {
            return true;
        }
        if (flag3 && this.showDarkPantsSetting.isEnabled()) {
            return !flag2;
        }
        return flag2 && this.showFriendsSetting.isEnabled() ? true : !flag && !flag2 && !flag3 && this.showNeutralSetting.isEnabled();
    }

    private static List<String> computeList() {
        return mc.getNetHandler().getPlayerInfoMap().stream().map(NetworkPlayerInfo::getGameProfile).map(GameProfile::getName).collect(Collectors.toList());
    }

    private static List<String> computeList2() {
        long now = System.currentTimeMillis();
        if (now - timestamp > 500L) {
            entries2 = computeList();
            timestamp = now;
        }

        return entries2;
    }
}
