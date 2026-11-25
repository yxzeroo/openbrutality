package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.brutality.command.Command;
import org.brutality.module.impl.pit.hypixel.utility.SwapMacros;
import org.brutality.utils.MessageUtils;

public final class SwapMacrosCommand extends Command {
    @Override
    public List<String> computeList(String[] parts) {
        if (parts.length == 1) {
            return Arrays.asList("add", "remove", "list");
        }
        if (parts.length == 4 && parts[0].equalsIgnoreCase("add")) {
            return Arrays.asList("helmet", "chest", "pants", "boots", "1", "2", "3", "4", "5", "6", "7", "8", "9");
        }
        if (parts.length == 2 && parts[0].equalsIgnoreCase("remove") && SwapMacros.getInstance() != null) {
            ArrayList list = new ArrayList();

            for (SwapMacros.Entry entry : SwapMacros.getInstance().entries()) {
                list.add(entry.name);
            }

            return list;
        }
        return Collections.emptyList();
    }

    public SwapMacrosCommand() {
        super("SwapMacros", "Configure inventory swap macros", ".sm [add <name> <key> <1-9|pants|boots|chest|helmet>|remove <name>|list]", "sm", "swapmacro");
    }

    @Override
    public void onCommand(String[] args, String message) {
        SwapMacros instance = SwapMacros.getInstance();
        if (instance != null) {
            try {
                if (args.length == 0) {
                    SwapMacros.process3();
                    return;
                }

                if (args[0].equalsIgnoreCase("add") && args.length == 4) {
                    instance.handleString(args[1], SwapMacros.computeInt(args[2]), SwapMacros.computeInt2(args[3]));
                    MessageUtils.x("§aMacro saved. Enable SwapMacros to use it.");
                    return;
                }

                if (args[0].equalsIgnoreCase("remove") && args.length == 2) {
                    for (SwapMacros.Entry entry : instance.entries()) {
                        if (entry.name.equalsIgnoreCase(args[1])) {
                            instance.handleEntry2(entry);
                            MessageUtils.x("§aMacro removed.");
                            return;
                        }
                    }

                    throw new IllegalArgumentException("Macro not found.");
                }

                if (args[0].equalsIgnoreCase("list")) {
                    for (SwapMacros.Entry entry2 : instance.entries()) {
                        MessageUtils.x(entry2.name + " - " + SwapMacros.computeString(entry2.key) + " - " + entry2.text2);
                    }

                    return;
                }

                MessageUtils.x(this.getText() + " (hold the item when adding)");
            } catch (Exception e) {
                MessageUtils.x("§c[SwapMacros] " + e.getMessage());
            }
        }
    }
}
