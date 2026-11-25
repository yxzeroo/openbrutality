package org.brutality.command.impl;

import java.util.List;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.pit.hypit.AutoEnchant;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.MessageUtils;

public class AutoEnchantCommand extends Command {
    private String computeString(Module module) {
        try {
            Setting setting = Brutality.settingsManager.computeSetting(module, "itemtype");
            if (setting instanceof ModeSetting) {
                return ((ModeSetting)setting).getText();
            }
        } catch (Exception ignored) {
        }

        return "Unknown";
    }

    private void sendMessage(String str) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, MessageUtils.text8));
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 0) {
            List list = AutoEnchant.computeList();
            if (list.isEmpty()) {
                this.sendMessage("§c[AutoEnchant] §fNo enchants configured!");
                this.sendMessage("§7Usage: §e.autoenchant misery §7or §e.autoenchant misery,somber");
            } else {
                this.sendMessage("§a[AutoEnchant] §fCurrent enchants: §e" + String.join(", ", list));
            }
        } else if (args.length == 1) {
            String str = args[0];
            AutoEnchant.handleString(str);
            List list2 = AutoEnchant.computeList();
            if (list2.isEmpty()) {
                this.sendMessage("§c[AutoEnchant] §fFailed to parse enchants!");
            } else {
                this.sendMessage("§a[AutoEnchant] §fEnchants set to: §e" + String.join(", ", list2));
                this.sendMessage("§7Use §e.toggle autoenchant §7to start");
                Module module = Brutality.moduleManager.computeModule("AutoEnchant");
                if (module != null) {
                    this.sendMessage("§7Current item type: §e" + this.computeString(module));
                    this.sendMessage("§7Change item type in the module settings");
                }
            }
        } else {
            this.sendMessage("§c[AutoEnchant] §fInvalid usage!");
            this.sendMessage("§7Usage: §e.autoenchant <enchant1,enchant2,...>");
            this.sendMessage("§7Example: §e.autoenchant misery §7or §e.autoenchant misery,somber");
            this.sendMessage("§7Change item type: Use module settings or ClickGUI");
        }
    }

    public AutoEnchantCommand() {
        super("AutoEnchant", "Configure enchants for AutoEnchant module", ".autoenchant <enchant1,enchant2,...>", "autoenchant", "ae");
    }
}
