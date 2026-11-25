package org.brutality.module.impl.visual;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.render.GlStateGuard;

public class Chams extends Module {
    private final BooleanSetting ignoreBotsSetting = new BooleanSetting("Ignore bots", this, false);
    private static Chams instance;
    private static List<String> entries = new java.util.ArrayList<>();
    private static long timestamp = 0L;
    private static final long timestamp2 = 500L;

    public Chams() {
        super("Chams", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.ignoreBotsSetting});
        instance = this;
    }

    public static boolean checkState() {
        return instance != null && instance.checkState44();
    }

    public static boolean matchesEntity(Entity entity) {
        return !computeList().contains(entity.getName());
    }

    private static List<String> computeList() {
        long now = System.currentTimeMillis();
        if (now - timestamp > 500L) {
            entries = getTablist();
            timestamp = now;
        }

        return entries;
    }

    public static GlStateGuard computeRenderHelper7(Entity entity) {
        if (instance != null
            && instance.checkState44()
            && entity instanceof EntityPlayer
            && entity != mc.thePlayer
            && (!instance.ignoreBotsSetting.isEnabled() || !matchesEntity(entity))) {
            GlStateGuard renderHelper7 = new GlStateGuard();
            GlStateManager.enablePolygonOffset();
            GlStateManager.doPolygonOffset(1.0F, -1000000.0F);
            return renderHelper7;
        }
        return null;
    }

    private static List<String> getTablist() {
        return mc.thePlayer != null && mc.getNetHandler() != null
            ? mc.getNetHandler()
                .getPlayerInfoMap()
                .stream()
                .map(NetworkPlayerInfo::getGameProfile)
                .filter(gameProfile -> !gameProfile.getId().equals(mc.thePlayer.getUniqueID()))
                .map(GameProfile::getName)
                .collect(Collectors.toList())
            : Collections.emptyList();
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onDisable() {
        super.onDisable();
        entries.clear();
        timestamp = 0L;
    }
}
