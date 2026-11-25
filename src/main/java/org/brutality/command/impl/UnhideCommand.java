package org.brutality.command.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.command.Command;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class UnhideCommand extends Command {
    public UnhideCommand() {
        super("Unhide", "Removes a module from the hidden list", "unhide <module>", "unhide");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length == 0) {
            NotificationFeedHandler.handleString("§c[Unhide] Please specify a module to unhide.");
        } else {
            String str = args[0];
            Module module = Brutality.moduleManager.computeModule(str);
            if (module == null) {
                NotificationFeedHandler.handleString("§c[Unhide] Module not found.");
            } else {
                String name = module.getName();
                File file = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/hidden.txt");
                if (!file.exists()) {
                    NotificationFeedHandler.handleString("§c[Unhide] No hidden modules found.");
                } else {
                    ArrayList<String> list = new ArrayList<>();
                    boolean flag = false;
                    int n = -1;

                    String str2;
                    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                        for (int i = 0; (str2 = reader.readLine()) != null; i++) {
                            list.add(str2);
                            if (str2.equalsIgnoreCase(name)) {
                                flag = true;
                                n = i;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        NotificationFeedHandler.handleString("§c[Unhide] Error reading hidden.txt.");
                        return;
                    }

                    if (!flag) {
                        NotificationFeedHandler.handleString("§c[Unhide] Module is not hidden.");
                    } else {
                        list.remove(n);

                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                            for (String str3 : list) {
                                writer.write(str3);
                                writer.newLine();
                            }
                        } catch (IOException e2) {
                            e2.printStackTrace();
                            NotificationFeedHandler.handleString("§c[Unhide] Error writing to hidden.txt.");
                            return;
                        }

                        NotificationFeedHandler.handleString("§a[Unhide] Successfully unhidden: " + name);
                    }
                }
            }
        }
    }
}
