package org.brutality.command.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class HideCommand extends Command {
    public HideCommand() {
        super("Hide", "Adds a module to the hidden list", "hide <module>", "hide");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 0) {
            NotificationFeedHandler.handleString("§c[Hide] Please specify a module to hide.");
        } else {
            String str = args[0];
            Module module = Brutality.moduleManager.computeModule(str);
            if (module == null) {
                NotificationFeedHandler.handleString("§c[Hide] Module not found.");
            } else {
                String name = module.getName();
                File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/hidden.txt");
                if (!file.exists()) {
                    file.getParentFile().mkdirs();

                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                        NotificationFeedHandler.handleString("§c[Hide] Error creating hidden.txt file.");
                        return;
                    }
                }

                try {
                    label80: {
                        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                            String str2;
                            do {
                                if ((str2 = reader.readLine()) == null) {
                                    break label80;
                                }
                            } while (!str2.equalsIgnoreCase(name));

                            NotificationFeedHandler.handleString("§c[Hide] Module is already hidden.");
                        }

                        return;
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                    NotificationFeedHandler.handleString("§c[Hide] Error reading hidden.txt.");
                    return;
                }

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                    writer.write(name);
                    writer.newLine();
                } catch (IOException e3) {
                    e3.printStackTrace();
                    NotificationFeedHandler.handleString("§c[Hide] Error writing to hidden.txt.");
                    return;
                }

                NotificationFeedHandler.handleString("§a[Hide] Successfully hidden: " + name);
            }
        }
    }
}
