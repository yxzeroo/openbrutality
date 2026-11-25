package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;
import org.brutality.gui.chat.ParseIntComponent;
import org.brutality.gui.chat.SendAnnouncementScreen;

public class ViewCommand extends Command {
    private static final long timestamp = 60L;

    public ViewCommand() {
        super("view", "Open a player's PitPanda profile and actions", "view <username>", "v");
    }

    @Override
    public List<String> computeList(String[] parts) {
        return (List<String>)(parts.length > 1 ? new ArrayList<>() : CommandHandler.computeList(parts, true));
    }

    @Override
    public void onCommand(String[] args, String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (args.length == 1 && args[0].matches("[A-Za-z0-9_]{1,16}")) {
            String str = args[0];
            Thread thread = new Thread(() -> {
                try {
                    Thread.sleep(timestamp);
                } catch (InterruptedException e) {
                    return;
                }

                mc.addScheduledTask(() -> mc.displayGuiScreen(new SendAnnouncementScreen(ParseIntComponent.computeEntry2(str))));
            }, "Brutality-ViewOpen");
            thread.setDaemon(true);
            thread.start();
        } else {
            if (mc.thePlayer != null) {
                mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: ." + this.getName() + " <username>"));
            }
        }
    }
}
