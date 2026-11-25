package org.brutality.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.PlaySoundHelper;
import org.brutality.util.module.AutoUseType;
import org.brutality.util.module.ClientOnlyType;
import org.brutality.util.setting.ActivateHelper;
import org.brutality.util.setting.GetAllowedValuesHelper;
import org.brutality.util.setting.SettingHelper;
import org.brutality.util.setting.SettingHelper3;
import org.brutality.util.setting.SettingHelper4;
import org.brutality.util.setting.SettingHelper5;
import org.brutality.util.setting.ValueMustBeBetweenHelper;
import org.brutality.utils.MessageUtils;

public abstract class SortSettingsHandler {
    public int keyCode;
    private final AutoUseType autoUseType;
    public boolean flag;
    private final String description;
    private final List<SettingHelper3> entries;
    private boolean enabled;
    private final ClientOnlyType clientOnlyType;
    private final boolean flag2;
    public static Minecraft mc = Minecraft.getMinecraft();
    private final String displayName;
    private final String text;
    private final List<GetDisplayNameHandler<?>> entries2 = new ArrayList<>();

    public void r() {
        if (this.isEnabled()) {
            this.disable();
        } else {
            this.enable();
        }
    }

    public String getText() {
        return this.text;
    }

    public List<GetDisplayNameHandler<?>> computeList() {
        List list = new ArrayList<>(this.entries2);

        for (SettingHelper3 settingHelper3 : this.entries) {
            list.addAll(settingHelper3.getY());
        }

        return list;
    }

    public void handleGetDisplayNameHandler(GetDisplayNameHandler<?> getDisplayNameHandler) {
        this.entries2.remove(getDisplayNameHandler);
        this.sortSettings();
    }

    public SortSettingsHandler() {
        this.entries = new ArrayList<>();
        this.enabled = false;
        this.flag = false;
        this.keyCode = -1;
        this.displayName = "";
        this.text = "";
        this.description = "";
        this.autoUseType = AutoUseType.AUTO_USE;
        this.flag2 = false;
        this.clientOnlyType = ClientOnlyType.CLIENT_ONLY;
    }

    public void disable() {
        this.handleFlag(false);
    }

    public void handleFlag(boolean flag) {
        if (!this.checkState3()) {
            MinecraftForge.EVENT_BUS.unregister(this);
        }

        this.enabled = false;
        this.onDisable();
        if (!flag) {
            this.process3();
            this.process4();
        }
    }

    public void process5() {
        ClientCommandHandler.instance.registerCommand(new SortSettingsHandler.Entry(this.getText()));
    }

    public String getDescription() {
        return this.description;
    }

    public boolean checkState3() {
        return false;
    }

    public void handleSettingHelper3(SettingHelper3 settingHelper3) {
        this.entries.add(settingHelper3);
        this.sortSettings();

        for (GetDisplayNameHandler getDisplayNameHandler : settingHelper3.getY()) {
            getDisplayNameHandler.setSortSettingsHandler(this);
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public ClientOnlyType getClientOnlyType() {
        return this.clientOnlyType;
    }

    public void enable() {
        this.handleFlag2(false);
    }

    public void onDisable() {
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public List<GetDisplayNameHandler<?>> getEntries2() {
        return this.entries2;
    }

    public void process6() {
        var getDisplayNameHandlers = this.computeList();

        for (GetDisplayNameHandler getDisplayNameHandler : getDisplayNameHandlers) {
            getDisplayNameHandler.activate();
        }

        int size = getDisplayNameHandlers.size();
        EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
        String displayName = this.getDisplayName();
        EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
        String str = displayName;
        EnumChatFormatting enumChatFormatting = YELLOW;
        int n = size;
        MessageUtils.x("All " + n + " settings for " + enumChatFormatting + str + WHITE + " have been reset to default.");
    }

    public AutoUseType getAutoUseType() {
        return this.autoUseType;
    }

    public void handleFlag2(boolean flag) {
        if (!this.checkState3()) {
            MinecraftForge.EVENT_BUS.register(this);
        }

        this.enabled = true;
        this.onEnable();
        if (!flag) {
            this.process3();
            this.process4();
        }
    }

    public List<SettingHelper3> getEntries() {
        return this.entries;
    }

    public void process() {
        Minecraft.getMinecraft()
            .thePlayer
            .addChatMessage(
                ExecuteCommandHelper.computeIChatComponent(
                    String.valueOf(
                        new StringBuilder()
                            .append(EnumChatFormatting.GRAY)
                            .append("Settings for ")
                            .append(EnumChatFormatting.WHITE)
                            .append(this.getDisplayName())
                            .append(": ")
                            .append(
                                this.isEnabled()
                                    ? String.valueOf(new StringBuilder().append(EnumChatFormatting.GREEN).append("Enabled"))
                                    : String.valueOf(new StringBuilder().append(EnumChatFormatting.RED).append("Disabled"))
                            )
                    ),
                    String.valueOf(
                        new StringBuilder()
                            .append(EnumChatFormatting.YELLOW)
                            .append("Click to toggle ")
                            .append(EnumChatFormatting.LIGHT_PURPLE)
                            .append(this.getDisplayName())
                    ),
                    String.valueOf(new StringBuilder("/toggle ").append(this.getText()))
                )
            );
        ExecuteCommandHelper.handleString2(
            String.valueOf(new StringBuilder().append(EnumChatFormatting.BOLD).append("-> ").append(EnumChatFormatting.GRAY).append(this.getDescription()))
        );
        this.computeList()
            .forEach(
                getDisplayNameHandler -> {
                    String str = getDisplayNameHandler.computeString();
                    String displayName = getDisplayNameHandler.getDisplayName();
                    String str2 = EnumChatFormatting.YELLOW.toString();
                    String str3;
                    if (getDisplayNameHandler instanceof ActivateHelper) {
                        str3 = str2 + "Click to toggle";
                    } else {
                        str3 = getDisplayNameHandler instanceof GetAllowedValuesHelper
                            ? str2 + "Click to cycle value"
                            : str2 + "Click to change using /" + this.getText() + " " + getDisplayNameHandler.getKey() + " [value]";
                    }

                    ChatComponentText chatComponentText = new ChatComponentText(str);
                    ChatStyle chatStyle = new ChatStyle();
                    if (!(getDisplayNameHandler instanceof ActivateHelper)) {
                        if (getDisplayNameHandler instanceof GetAllowedValuesHelper) {
                            chatStyle.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + getDisplayNameHandler.getSortSettingsHandler().getText() + " " + getDisplayNameHandler.getKey()));
                        } else {
                            chatStyle.setChatClickEvent(
                                new ClickEvent(
                                    Action.SUGGEST_COMMAND, "/" + getDisplayNameHandler.getSortSettingsHandler().getText() + " " + getDisplayNameHandler.getKey() + " " + getDisplayNameHandler.getValue()
                                )
                            );
                        }
                    } else {
                        chatStyle.setChatClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + getDisplayNameHandler.getSortSettingsHandler().getText() + " " + getDisplayNameHandler.getKey()));
                    }

                    chatStyle.setChatHoverEvent(new HoverEvent(net.minecraft.event.HoverEvent.Action.SHOW_TEXT, new ChatComponentText(displayName + "\n" + str3)));
                    chatComponentText.setChatStyle(chatStyle);
                    Minecraft.getMinecraft().thePlayer.addChatMessage(chatComponentText);
                }
            );
        if (this.computeList().isEmpty()) {
            ExecuteCommandHelper.handleString2(
                String.valueOf(new StringBuilder().append(EnumChatFormatting.RED).append("* No settings available for this module."))
            );
        }
    }

    public boolean checkState() {
        return this.keyCode != 0 ? this.keyCode != -1 : false;
    }

    public void onEnable() {
    }

    public SortSettingsHandler(String displayName, String text, String description, AutoUseType autoUseType, boolean flag) {
        this.entries = new ArrayList<>();
        this.enabled = false;
        this.flag = false;
        this.keyCode = -1;
        this.displayName = displayName;
        this.text = text;
        this.description = description;
        this.autoUseType = autoUseType;
        this.flag2 = flag;
        this.clientOnlyType = ClientOnlyType.CLIENT_ONLY;
    }

    public SortSettingsHandler(String displayName, String text, String description, AutoUseType autoUseType, boolean flag, ClientOnlyType clientOnlyType) {
        this.entries = new ArrayList<>();
        this.enabled = false;
        this.flag = false;
        this.keyCode = -1;
        this.displayName = displayName;
        this.text = text;
        this.description = description;
        this.autoUseType = autoUseType;
        this.flag2 = flag;
        this.clientOnlyType = clientOnlyType;
    }

    public boolean matchesInt(int n) {
        return this.keyCode == n;
    }

    public boolean checkState2() {
        return false;
    }

    public void process3() {
        if (this.isEnabled()) {
            MessageUtils.x(
                String.valueOf(
                    new StringBuilder()
                        .append(EnumChatFormatting.GREEN)
                        .append("» ")
                        .append(EnumChatFormatting.WHITE)
                        .append(this.getDisplayName())
                        .append(" ")
                        .append(EnumChatFormatting.GREEN)
                        .append(EnumChatFormatting.BOLD)
                        .append("Enabled")
                )
            );
        } else {
            MessageUtils.x(
                String.valueOf(
                    new StringBuilder()
                        .append(EnumChatFormatting.RED)
                        .append("» ")
                        .append(EnumChatFormatting.WHITE)
                        .append(this.getDisplayName())
                        .append(" ")
                        .append(EnumChatFormatting.RED)
                        .append(EnumChatFormatting.BOLD)
                        .append("Disabled")
                )
            );
        }
    }

    public GetDisplayNameHandler<?> computeGetDisplayNameHandler(String str) {
        String str2 = str.replace("-", "");

        for (GetDisplayNameHandler getDisplayNameHandler : this.computeList()) {
            if (getDisplayNameHandler.computeString2().equalsIgnoreCase(str2)) {
                return getDisplayNameHandler;
            }
        }

        return null;
    }

    public void process2() {
    }

    public void handleGetDisplayNameHandler2(GetDisplayNameHandler<?> getDisplayNameHandler) {
        if (this.entries2.contains(getDisplayNameHandler)) {
            throw new IllegalArgumentException("Setting " + getDisplayNameHandler.getKey() + " already registered in module " + this.getText());
        }
        this.entries2.add(getDisplayNameHandler);
        this.sortSettings();
        getDisplayNameHandler.setSortSettingsHandler(this);
    }

    public void process4() {
        PlaySoundHelper.process7();
    }

    private void sortSettings() {
        Comparator<GetDisplayNameHandler<?>> comparator = Comparator.<GetDisplayNameHandler<?>, Integer>comparing(getDisplayNameHandler -> {
            if (getDisplayNameHandler.getKey().startsWith("debug")) {
                return 100;
            }
            if (getDisplayNameHandler instanceof GetAllowedValuesHelper) {
                return 1;
            }
            if (getDisplayNameHandler instanceof ActivateHelper) {
                return 2;
            }
            if (getDisplayNameHandler instanceof SettingHelper5) {
                return 3;
            }
            if (getDisplayNameHandler instanceof SettingHelper) {
                return 4;
            }
            if (getDisplayNameHandler instanceof SettingHelper4) {
                return 5;
            }
            return getDisplayNameHandler instanceof ValueMustBeBetweenHelper ? 6 : 99;
        }).thenComparing(getDisplayNameHandler2 -> getDisplayNameHandler2.getKey().toLowerCase());
        this.entries2.sort(comparator);

        for (SettingHelper3 settingHelper3 : this.entries) {
            settingHelper3.handleComparator(comparator);
        }
    }

    public static class Entry extends CoreHandler3 {
        private String text;
        private String commandName;
        private List<String> commandAliases;

        public Entry(String text, List<String> commandAliases) {
            this.text = text;
            this.commandName = text;
            this.commandAliases = commandAliases;
        }

        @Override
        public String getText() {
            return this.text;
        }

        @Override
        public String getCommandName() {
            return this.commandName;
        }

        @Override
        public List<String> getCommandAliases() {
            return this.commandAliases;
        }

        public Entry(String text) {
            this.text = text;
            this.commandName = text;
            this.commandAliases = new ArrayList<>();
        }
    }
}
