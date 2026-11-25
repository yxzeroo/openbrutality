package org.brutality.command.impl;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.command.Command;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;

public class CopyNameCommand extends Command {
    public CopyNameCommand() {
        super("CopyName", "Copies the name of the player you're looking at to clipboard", ".copyname", "copyname", "cn");
    }

    @Override
    public void onCommand(String[] args, String message) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.theWorld != null) {
            MovingObjectPosition objectMouseOver = mc.objectMouseOver;
            if (objectMouseOver == null || objectMouseOver.typeOfHit != MovingObjectType.ENTITY) {
                NotificationFeedHandler.handleString("§c[CopyName] You're not looking at any entity!");
            } else if (objectMouseOver.entityHit instanceof EntityPlayer player) {
                String name = player.getName();

                try {
                    StringSelection stringSelection = new StringSelection(name);
                    Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    systemClipboard.setContents(stringSelection, null);
                    NotificationFeedHandler.handleString("§a[CopyName] Copied '" + name + "' to clipboard!");
                } catch (Exception e) {
                    NotificationFeedHandler.handleString("§c[CopyName] Failed to copy to clipboard!");
                }
            } else {
                NotificationFeedHandler.handleString("§c[CopyName] You're not looking at a player!");
            }
        } else {
            NotificationFeedHandler.handleString("§c[CopyName] You must be in-game to use this command!");
        }
    }
}
