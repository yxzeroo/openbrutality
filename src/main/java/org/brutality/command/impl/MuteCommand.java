package org.brutality.command.impl;

import java.util.ArrayList;
import java.util.List;
import org.brutality.command.Command;
import org.brutality.command.CommandHandler;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.player.PlayerHelper7;

public class MuteCommand extends Command {
    private void process() {
        MessageUtils.x("§7[Mute] §f.mute <name> §7— toggle mute");
        MessageUtils.x("§7[Mute] §f.mute remove <name> §7— unmute");
        MessageUtils.x("§7[Mute] §f.mute list §7— muted players");
        MessageUtils.x("§7[Mute] Settings: §fChatMute §7module in clickgui");
    }

    private void handleString(String str) {
        if (PlayerHelper7.matchesString2(str)) {
            this.handleString2(str);
        } else {
            PlayerHelper7.handleString2(str);
            NotificationFeedHandler.handleString("§c[Mute] §f" + str + " §7muted. §8(Hover chat to read)");
            MessageUtils.x("§c[Mute] §f" + str + " §7muted — messages hidden until hover.");
        }
    }

    private void handleString2(String str) {
        if (!PlayerHelper7.matchesString2(str)) {
            MessageUtils.x("§7[Mute] §f" + str + " §7is not muted.");
        } else {
            PlayerHelper7.handleString(str);
            NotificationFeedHandler.handleString("§a[Mute] §f" + str + " §7unmuted.");
            MessageUtils.x("§a[Mute] §f" + str + " §7unmuted.");
        }
    }

    public MuteCommand() {
        super("Mute", "Mute players in chat", ".mute <username|list|remove>", "mute", "silence", "block");
    }

    @Override
    public void onCommand(String[] args, String message) {
        if (args.length < 1) {
            this.process();
        } else {
            String str = args[0].toLowerCase();
            if (str.equals("list")) {
                new MutesCommand().onCommand(new String[0], message);
            } else if (!str.equals("remove") && !str.equals("unmute")) {
                this.handleString(args[0]);
            } else if (args.length < 2) {
                MessageUtils.x("§c[Mute] Usage: .mute remove <username>");
            } else {
                this.handleString2(args[1]);
            }
        }
    }

    @Override
    public List<String> computeList(String[] parts) {
        return (List<String>)(parts.length > 1 ? new ArrayList<>() : CommandHandler.computeList(parts, false));
    }
}
