package org.brutality.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiTextField;
import org.brutality.Brutality;
import org.brutality.command.impl.APICommand;
import org.brutality.command.impl.AutoEnchantCommand;
import org.brutality.command.impl.BindCommand;
import org.brutality.command.impl.BindsCommand;
import org.brutality.command.impl.BwCommand;
import org.brutality.command.impl.BwautowhoCommand;
import org.brutality.command.impl.BwtagsCommand;
import org.brutality.command.impl.CheckCommand;
import org.brutality.command.impl.CleartabcacheCommand;
import org.brutality.command.impl.ClickGuiCommand;
import org.brutality.command.impl.ConfigCommand;
import org.brutality.command.impl.CopyNameCommand;
import org.brutality.command.impl.DarkLoadoutCommand;
import org.brutality.command.impl.DenickCommand;
import org.brutality.command.impl.DestructCommand;
import org.brutality.command.impl.FriendCommand;
import org.brutality.command.impl.FriendsCommand;
import org.brutality.command.impl.HelpCommand;
import org.brutality.command.impl.HideCommand;
import org.brutality.command.impl.HistoryCommand;
import org.brutality.command.impl.ItemNameCommand;
import org.brutality.command.impl.ItemRenameCommand;
import org.brutality.command.impl.KOSCommand;
import org.brutality.command.impl.KOSSCommand;
import org.brutality.command.impl.LoginCommand;
import org.brutality.command.impl.MellowCommand;
import org.brutality.command.impl.MinfkdrCommand;
import org.brutality.command.impl.ModuleInfoCommand;
import org.brutality.command.impl.ModulePasteCommand;
import org.brutality.command.impl.ModulesCommand;
import org.brutality.command.impl.MuteCommand;
import org.brutality.command.impl.MutesCommand;
import org.brutality.command.impl.NameItemCommand;
import org.brutality.command.impl.ProfilerCommand;
import org.brutality.command.impl.ScriptCommand;
import org.brutality.command.impl.SwapMacrosCommand;
import org.brutality.command.impl.TabstatsCommand;
import org.brutality.command.impl.ToggleCommand;
import org.brutality.command.impl.TruceCommand;
import org.brutality.command.impl.UnhideCommand;
import org.brutality.command.impl.UrchinCommand;
import org.brutality.command.impl.UrchinkeyCommand;
import org.brutality.command.impl.VClipCommand;
import org.brutality.command.impl.ViewCommand;
import org.brutality.event.impl.chat.EventChat;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.chat.ListConfigNamesHelper;

public class CmdManager {
    public static List<Command> entries = new ArrayList<>();
    public String prefix = ".";

    private void handleNumberSetting(NumberSetting numberSetting, String str) {
        try {
            double d = Double.parseDouble(str);
            if (d >= numberSetting.getMinValue() && d <= numberSetting.getMaxValue()) {
                numberSetting.handleDouble2(d);
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("B", MessageUtils.text5)
                        + MessageUtils.computeString("] - ", MessageUtils.text9)
                        + MessageUtils.computeString(numberSetting.getName() + " set to " + d, MessageUtils.text11)
                );
            } else {
                String str2 = MessageUtils.computeString("[", MessageUtils.text9);
                String str3 = MessageUtils.computeString("B", MessageUtils.text5);
                String str4 = MessageUtils.computeString("] - ", MessageUtils.text9);
                double maxValue = numberSetting.getMaxValue();
                double minValue = numberSetting.getMinValue();
                ModuleManager.handleString(
                    str2 + str3 + str4 + MessageUtils.computeString("Invalid value! Must be between " + minValue + " and " + maxValue, MessageUtils.text13)
                );
            }
        } catch (NumberFormatException e) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid number format!", MessageUtils.text13)
            );
        }
    }

    private void handleModule(Module module, String[] parts) {
        if (parts.length == 1) {
            new ModuleInfoCommand().onCommand(new String[0], parts[0]);
        } else if (parts.length != 2) {
            if (parts.length == 3) {
                String str = this.computeString(parts[1]);
                String str2 = parts[2].toLowerCase();
                Setting setting = Brutality.settingsManager.computeSetting(module, str);
                if (setting != null) {
                    if (setting instanceof NumberSetting) {
                        this.handleNumberSetting((NumberSetting)setting, str2);
                    } else if (setting instanceof ModeSetting) {
                        this.handleModeSetting((ModeSetting)setting, str2);
                    } else {
                        this.process();
                    }
                } else {
                    this.process();
                }
            }
        } else {
            String str3 = this.computeString(parts[1]);
            Setting setting2 = Brutality.settingsManager.computeSetting(module, str3);
            if (setting2 != null && setting2 instanceof BooleanSetting boolSetting) {
                boolSetting.setEnabled(!boolSetting.isEnabled());
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("B", MessageUtils.text5)
                        + MessageUtils.computeString("] - ", MessageUtils.text9)
                        + MessageUtils.computeString(setting2.getName() + " toggled to " + boolSetting.isEnabled(), MessageUtils.text11)
                );
            } else {
                this.process();
            }
        }
    }

    public boolean matchesInt(int n) {
        Minecraft mc = Minecraft.getMinecraft();
        if (n != 15) {
            return false;
        }
        if (!(mc.currentScreen instanceof GuiChat gui)) {
            return false;
        }
        String str = "";
        GuiTextField inputField = gui.getInputField();
        if (inputField != null) {
            str = inputField.getText();
        }

        if (!str.startsWith(this.prefix)) {
            return false;
        }
        if (!ListConfigNamesHelper.computeList2(str).isEmpty()) {
            return false;
        }
        String[] parts = str.substring(this.prefix.length()).split(" ");
        String str2 = parts[0].toLowerCase();
        String[] parts2 = Arrays.copyOfRange(parts, 1, parts.length);
        if (str.endsWith(" ")) {
            parts2 = Arrays.copyOf(parts2, parts2.length + 1);
            parts2[parts2.length - 1] = "";
        }

        Command command = null;

        for (Command command2 : entries) {
            if (command2.entries.contains(str2) || command2.name2.equalsIgnoreCase(str2)) {
                command = command2;
                break;
            }
        }

        if (command == null) {
            return false;
        }
        List list = command.computeList(parts2);
        if (list != null && !list.isEmpty()) {
            if (list.size() == 1) {
                if (parts2.length > 0) {
                    String str3 = parts2[parts2.length - 1];
                } else {
                }

                String str5 = (String)list.get(0);
                if (str.endsWith(" ")) {
                    if (inputField != null) {
                        inputField.setText(str + str5 + " ");
                        inputField.setCursorPositionEnd();
                    }
                } else {
                    int n2 = str.lastIndexOf(32);
                    String str6 = n2 >= 0 ? str.substring(0, n2 + 1) : this.prefix + str2 + " ";
                    if (inputField != null) {
                        inputField.setText(str6 + str5 + " ");
                        inputField.setCursorPositionEnd();
                    }
                }
            } else {
                StringBuilder sb = new StringBuilder("§7Tab: ");

                for (int i = 0; i < list.size(); i++) {
                    if (i > 0) {
                        sb.append("§7, ");
                    }

                    sb.append("§f").append((String)list.get(i));
                }

                ModuleManager.handleString(sb.toString());
            }

            return true;
        }
        return false;
    }

    public void process2() {
        entries.add(new BindCommand());
        entries.add(new BindsCommand());
        entries.add(new HelpCommand());
        entries.add(new ToggleCommand());
        entries.add(new SwapMacrosCommand());
        entries.add(new DarkLoadoutCommand());
        entries.add(new ClickGuiCommand());
        entries.add(new ModuleInfoCommand());
        entries.add(new ModulesCommand());
        entries.add(new ConfigCommand());
        entries.add(new FriendCommand());
        entries.add(new FriendsCommand());
        entries.add(new TruceCommand());
        entries.add(new KOSCommand());
        entries.add(new KOSSCommand());
        entries.add(new ItemRenameCommand());
        entries.add(new NameItemCommand());
        entries.add(new ItemNameCommand());
        entries.add(new LoginCommand());
        entries.add(new DenickCommand());
        entries.add(new HideCommand());
        entries.add(new UnhideCommand());
        entries.add(new MuteCommand());
        entries.add(new MutesCommand());
        entries.add(new ModulePasteCommand());
        entries.add(new VClipCommand());
        entries.add(new DestructCommand());
        entries.add(new CopyNameCommand());
        entries.add(new CheckCommand());
        entries.add(new HistoryCommand());
        entries.add(new ViewCommand());
        entries.add(new APICommand());
        entries.add(new AutoEnchantCommand());
        entries.add(new ScriptCommand());
        entries.add(new BwCommand());
        entries.add(new MinfkdrCommand());
        entries.add(new BwtagsCommand());
        entries.add(new TabstatsCommand());
        entries.add(new CleartabcacheCommand());
        entries.add(new UrchinCommand());
        entries.add(new UrchinkeyCommand());
        entries.add(new BwautowhoCommand());
        entries.add(new MellowCommand());
        entries.add(new ProfilerCommand());
    }

    public CmdManager() {
        this.process2();
    }

    private String computeString(String str) {
        return str.toLowerCase().replace(" ", "").replace("-", "");
    }

    @Generated
    public static List<Command> getCommands() {
        return entries;
    }

    private void process() {
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("B", MessageUtils.text5)
                + MessageUtils.computeString("] - ", MessageUtils.text9)
                + MessageUtils.computeString("Setting not found!", MessageUtils.text13)
        );
    }

    public void onEventChat(EventChat event) {
        String message = event.getMessage();
        if (message.toLowerCase().startsWith("/packetlog")) {
            event.cancel();
            MessageUtils.x(MessageUtils.text13 + "[Brutality] " + MessageUtils.text16 + "That command has been blocked by Brutality Client.");
        } else if (message.toLowerCase().startsWith("/packetdebugger")) {
            event.cancel();
            MessageUtils.x(MessageUtils.text13 + "[Brutality] " + MessageUtils.text16 + "That command has been blocked by Brutality Client.");
        } else if (message.startsWith(this.prefix)) {
            event.cancel();
            boolean flag = false;
            String[] parts = message.split(" ");
            String str = parts[0].substring(this.prefix.length()).toLowerCase();

            for (Command command : entries) {
                if (command.entries.contains(str) || command.name2.equalsIgnoreCase(str)) {
                    command.onCommand(Arrays.copyOfRange(parts, 1, parts.length), message);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                Module module = Brutality.moduleManager.computeModule(str);
                if (module != null) {
                    this.handleModule(module, parts);
                    flag = true;
                }
            }

            if (!flag) {
                ModuleManager.handleString(
                    MessageUtils.computeString("[", MessageUtils.text9)
                        + MessageUtils.computeString("B", MessageUtils.text5)
                        + MessageUtils.computeString("] - ", MessageUtils.text9)
                        + MessageUtils.computeString("Invalid Command!", MessageUtils.text13)
                );
            }
        }
    }

    private void handleModeSetting(ModeSetting modeSetting, String str) {
        String str2 = str.toLowerCase();
        if (modeSetting.computeList2().stream().anyMatch(str3 -> str3.equalsIgnoreCase(str2))) {
            modeSetting.handleString(str2);
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString(modeSetting.getName() + " set to " + str2, MessageUtils.text11)
            );
        } else {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Invalid mode! Available modes: " + modeSetting.computeList2(), MessageUtils.text13)
            );
        }
    }
}
