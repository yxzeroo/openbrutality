package org.brutality.command.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import org.brutality.command.Command;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.MessageUtils;

public class ModulePasteCommand extends Command {
    public ModulePasteCommand() {
        super("ModulePaste", "Copies all module names to clipboard", ".copymodule", "copymodule");
    }

    @Override
    public void onCommand(String[] args, String message) {
        StringBuilder sb = new StringBuilder();
        var modules = ModuleManager.getEntries2();
        int n = 0;

        for (Module module : modules) {
            if (module.getCategory() != Category.HIDDEN) {
                sb.append(module.getName()).append("\n");
                n++;
            }
        }

        try {
            StringSelection stringSelection = new StringSelection(sb.toString());
            Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            systemClipboard.setContents(stringSelection, stringSelection);
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Copied " + n + " module names to clipboard!", MessageUtils.text11)
            );
        } catch (Exception e) {
            ModuleManager.handleString(
                MessageUtils.computeString("[", MessageUtils.text9)
                    + MessageUtils.computeString("B", MessageUtils.text5)
                    + MessageUtils.computeString("] - ", MessageUtils.text9)
                    + MessageUtils.computeString("Failed to copy to clipboard: " + e.getMessage(), MessageUtils.text13)
            );
        }
    }
}
