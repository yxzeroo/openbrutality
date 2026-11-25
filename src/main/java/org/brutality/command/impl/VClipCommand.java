package org.brutality.command.impl;

import net.minecraft.client.Minecraft;
import org.brutality.command.Command;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class VClipCommand extends Command {
    public VClipCommand() {
        super("VClip", "Teleports the player vertically by a specified distance", ".vclip <distance>", "vclip");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length != 1) {
            NotificationFeedHandler.handleString("§c[VClip] Usage: .vclip <distance>");
        } else {
            try {
                double d = Double.parseDouble(args[0]);
                Minecraft mc = Minecraft.getMinecraft();
                if (mc.thePlayer != null) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + d, mc.thePlayer.posZ);
                    String str = d > 0.0 ? "up" : "down";
                    double d2 = Math.abs(d);
                    NotificationFeedHandler.handleString("§a[VClip] Clipped " + d2 + " blocks " + str + "!");
                } else {
                    NotificationFeedHandler.handleString("§c[VClip] Player not found!");
                }
            } catch (NumberFormatException e) {
                NotificationFeedHandler.handleString("§c[VClip] Invalid number format! Please enter a valid distance.");
            }
        }
    }
}
