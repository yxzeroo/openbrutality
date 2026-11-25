package org.brutality.module.impl.misc;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.misc.skindenicker.SkindenickerHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.MessageUtils;

public class SkinDenicker extends Module {
    private final BooleanSetting showFailedDenicksSetting = new BooleanSetting("Show Failed Denicks", this, true);
    private final Set<String> itemSet = new HashSet<>();
    private final Map<String, Integer> lookupMap = new HashMap<>();

    public SkinDenicker() {
        super("SkinDenicker", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.showFailedDenicksSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && mc.getNetHandler() != null) {
            if (event instanceof RenderEvent4) {
                this.itemSet.clear();
                this.lookupMap.clear();
            } else if (event instanceof EventUpdate && EventState.onEvent(event)) {
                for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    if (playerInfo != null && playerInfo.getGameProfile() != null) {
                        String str = playerInfo.getGameProfile().getId().toString();
                        if (str.length() >= 15 && str.charAt(14) == '1') {
                            String name = playerInfo.getGameProfile().getName();
                            if (!this.itemSet.contains(name)) {
                                String str2 = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName().getFormattedText() : name;
                                boolean flag = !str2.contains("§");
                                if (flag) {
                                    int n = this.lookupMap.getOrDefault(name, 0) + 1;
                                    this.lookupMap.put(name, n);
                                    if (n < 200) {
                                        continue;
                                    }
                                }

                                this.handleNetworkPlayerInfo(playerInfo, str2);
                                this.itemSet.add(name);
                                this.lookupMap.remove(name);
                            }
                        }
                    }
                }
            }
        }
    }

    private void handleNetworkPlayerInfo(NetworkPlayerInfo playerInfo, String str) {
        Property first = Iterables.getFirst(playerInfo.getGameProfile().getProperties().get("textures"), null);
        if (first != null) {
            String str2;
            try {
                str2 = new String(Base64.getDecoder().decode(first.getValue()));
            } catch (Exception e) {
                return;
            }

            JsonObject json;
            try {
                json = new JsonParser().parse(str2).getAsJsonObject();
            } catch (Exception e2) {
                return;
            }

            if (json.has("textures")) {
                JsonObject asJsonObject = json.getAsJsonObject("textures");
                if (asJsonObject.has("SKIN")) {
                    String str3 = asJsonObject.getAsJsonObject("SKIN").has("url") ? asJsonObject.getAsJsonObject("SKIN").get("url").getAsString() : "";
                    if (!str3.isEmpty()) {
                        String[] parts = str3.split("/");
                        if (parts.length >= 5) {
                            String str4 = parts[4];
                            String str5 = this.computeString(str);
                            if (SkindenickerHandler.itemSet.contains(str4)) {
                                if (this.showFailedDenicksSetting.isEnabled()) {
                                    EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                                    EnumChatFormatting RESET = EnumChatFormatting.RESET;
                                    EnumChatFormatting DARK_GRAY = EnumChatFormatting.DARK_GRAY;
                                    EnumChatFormatting RED = EnumChatFormatting.RED;
                                    MessageUtils.x(
                                        EnumChatFormatting.DARK_GRAY + "[" + RED + "Denicker" + DARK_GRAY + "] " + RESET + str5 + GRAY + " is nicked."
                                    );
                                }
                            } else if (json.has("profileName")) {
                                String asString = json.get("profileName").getAsString();
                                if (!asString.isEmpty()) {
                                    EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                                    EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                                    EnumChatFormatting GRAY3 = EnumChatFormatting.GRAY;
                                    EnumChatFormatting RED2 = EnumChatFormatting.RED;
                                    EnumChatFormatting DARK_GRAY2 = EnumChatFormatting.DARK_GRAY;
                                    EnumChatFormatting RED3 = EnumChatFormatting.RED;
                                    MessageUtils.x(
                                        EnumChatFormatting.DARK_GRAY
                                            + "["
                                            + RED3
                                            + "Denicker"
                                            + DARK_GRAY2
                                            + "] "
                                            + RED2
                                            + str5
                                            + GRAY3
                                            + " is actually "
                                            + GREEN
                                            + asString
                                            + GRAY2
                                            + "."
                                    );
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.itemSet.clear();
        this.lookupMap.clear();
        SkindenickerHandler.process();
    }

    private String computeString(String str) {
        while (str.matches(".*§[0-9a-frk-o]$")) {
            str = str.substring(0, str.length() - 2);
        }

        return str.trim();
    }
}
