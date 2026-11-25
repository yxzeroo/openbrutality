package org.brutality.module.impl.visual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.render.GetEntityEvent2;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.StringSetting;

public class NickHider extends Module {
    private static NickHider instance;
    private final StringSetting choppapitSetting = new StringSetting("Name", this, "§choppapit");
    private final BooleanSetting hideInChatSetting = new BooleanSetting("Hide In Chat", this, true);
    private final BooleanSetting hideInTabSetting = new BooleanSetting("Hide In Tab", this, true);
    private final BooleanSetting hideNametagSetting = new BooleanSetting("Hide Nametag", this, true);
    private final BooleanSetting spoofPrestigeSetting = new BooleanSetting("Spoof Prestige", this, false);
    private final ModeSetting xxxvSetting = new ModeSetting(
        "Prestige", this, "0", Arrays.asList("0", "I-IV", "V-IX", "X-XIV", "XV-XIX", "XX-XXIV", "XXV-XXIX", "XXX-XXXIV", "XXXV+")
    );
    private static final Map<String, String> lookupMap = new HashMap<>();
    private static final String[] stringArray = new String[]{"§7[", "§9[", "§e[", "§6[", "§c[", "§5[", "§d[", "§f[", "§b["};
    private final Map<String, String> lookupMap2 = new HashMap<>();

    public String computeString2() {
        return !this.spoofPrestigeSetting.isEnabled() ? null : lookupMap.get(this.xxxvSetting.getText());
    }

    public static boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        NickHider nickHider = instance;
        return nickHider != null && nickHider.checkState44() && Minecraft.getMinecraft().thePlayer != null && player == Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof ChatEvent && this.hideInChatSetting.isEnabled()) {
                    this.onChatEvent((ChatEvent)event);
                }

                if (event instanceof GetPacketEvent && this.hideInChatSetting.isEnabled()) {
                    this.onGetPacketEvent((GetPacketEvent)event);
                }

                if (event instanceof GetEntityEvent2 && this.hideNametagSetting.isEnabled()) {
                    this.onGetEntityEvent2((GetEntityEvent2)event);
                }
            }
        }
    }

    private String computeString(String str) {
        String str2 = lookupMap.get(this.xxxvSetting.getText());
        if (str2 == null) {
            return str;
        }
        String str3 = str;

        for (String str4 : stringArray) {
            if (str3.contains(str4)) {
                str3 = str3.replace(str4, str2 + "[");
                String str5 = str4.substring(0, 2) + "]";
                str3 = str3.replace(str5, str2 + "]");
                break;
            }
        }

        return str3;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.lookupMap2.clear();
    }

    public NickHider() {
        super("NickHider", 0, Category.VISUAL);
        this.handleSettingArray(
            new Setting[]{
                this.choppapitSetting, this.hideInChatSetting, this.hideInTabSetting, this.hideNametagSetting, this.spoofPrestigeSetting, this.xxxvSetting
            }
        );
        instance = this;
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof C01PacketChatMessage packet) {
            String message = packet.getMessage();
            String name = mc.thePlayer.getName();
            if (message.contains(name)) {
                getPacketEvent.cancel();
                String str = message.replace(name, this.computeString());
                mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(str));
            }
        }
    }

    public static String computeString(NetworkPlayerInfo playerInfo) {
        if (playerInfo == null) {
            return null;
        }
        NickHider nickHider = instance;
        if (nickHider != null && nickHider.checkState44()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer == null) {
                return playerInfo.getGameProfile().getName();
            }
            String name = playerInfo.getGameProfile().getName();
            if (name.equals(mc.thePlayer.getName()) && nickHider.hideInTabSetting.isEnabled()) {
                String str = playerInfo.getDisplayName() != null ? playerInfo.getDisplayName().getFormattedText() : name;
                String str2 = str.replace(name, nickHider.computeString());
                if (nickHider.spoofPrestigeSetting.isEnabled()) {
                    str2 = nickHider.computeString(str2);
                }

                return str2;
            }
            return playerInfo.getGameProfile().getName();
        }
        return playerInfo.getGameProfile().getName();
    }

    private String computeString() {
        return this.choppapitSetting.getValue().isEmpty() ? "§choppapit" : this.choppapitSetting.getValue();
    }

    public static String computeString(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NickHider nickHider = instance;
        if (nickHider == null || !nickHider.checkState44()) {
            return player.getName();
        }
        return Minecraft.getMinecraft().thePlayer != null && player == Minecraft.getMinecraft().thePlayer ? nickHider.computeString() : player.getName();
    }

    static {
        lookupMap.put("0", "§7");
        lookupMap.put("I-IV", "§9");
        lookupMap.put("V-IX", "§e");
        lookupMap.put("X-XIV", "§6");
        lookupMap.put("XV-XIX", "§c");
        lookupMap.put("XX-XXIV", "§5");
        lookupMap.put("XXV-XXIX", "§d");
        lookupMap.put("XXX-XXXIV", "§f");
        lookupMap.put("XXXV+", "§b");
    }

    public String computeString2(String str) {
        return !this.spoofPrestigeSetting.isEnabled() ? str : this.computeString(str);
    }

    @Generated
    public static NickHider getInstance() {
        return instance;
    }

    private void onGetEntityEvent2(GetEntityEvent2 getEntityEvent2) {
        if (getEntityEvent2.getEntity() instanceof EntityPlayer) {
            EntityPlayer entity = (EntityPlayer)getEntityEvent2.getEntity();
            if (entity == mc.thePlayer) {
                getEntityEvent2.cancel();
                this.lookupMap2.put(entity.getName(), this.computeString());
            }
        }
    }

    private void onChatEvent(ChatEvent chatEvent) {
        String text = chatEvent.message.getFormattedText();
        String name = mc.thePlayer.getName();
        boolean flag = false;
        String str = text;
        if (text.contains(name)) {
            str = text.replace(name, this.computeString());
            flag = true;
        }

        if (this.spoofPrestigeSetting.isEnabled()) {
            String str2 = this.computeString(str);
            if (!str2.equals(str)) {
                str = str2;
                flag = true;
            }
        }

        if (flag) {
            chatEvent.cancel();
            ChatComponentText chatComponentText = new ChatComponentText(str);
            mc.thePlayer.addChatMessage(chatComponentText);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.lookupMap2.clear();
    }
}
