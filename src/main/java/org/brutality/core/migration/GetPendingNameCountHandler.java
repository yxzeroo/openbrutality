package org.brutality.core.migration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Tuple;
import org.brutality.core.BrutalityHandler;
import org.brutality.util.DebugHelper;
import org.brutality.utils.MessageUtils;

public class GetPendingNameCountHandler {
    private static final List<Tuple<LogHandler, BrutalityHandler>> entries = new ArrayList<>();

    public static List<String> getPendingListNames() {
        return entries.stream().map(tuple -> tuple.getFirst().text).collect(Collectors.toList());
    }

    public static void handleLogHandler(LogHandler logHandler, BrutalityHandler brutalityHandler) {
        entries.add(new Tuple<>(logHandler, brutalityHandler));
    }

    public static void handleLogHandler(LogHandler logHandler, BrutalityHandler brutalityHandler, int n) {
        DebugHelper.debug(String.valueOf(new StringBuilder("Migrating ").append(logHandler.text).append(" to new set")));

        for (String str : logHandler.getItemSet()) {
            try {
                DebugHelper.debug(String.valueOf(new StringBuilder("Migrating ").append(str)));
                brutalityHandler.matchesString(str, "");
            } catch (Exception e) {
                MessageUtils.x(
                    String.valueOf(
                        new StringBuilder("Failed to migrate name: ")
                            .append(EnumChatFormatting.YELLOW)
                            .append(str)
                            .append(EnumChatFormatting.RED)
                            .append(" (Skipped). Reason: ")
                            .append(e.getMessage())
                    )
                );
                DebugHelper.error(String.valueOf(new StringBuilder("Failed to migrate name: ").append(str).append(" - ").append(e.getMessage())));
            }
        }
    }

    public static void handleInt(int n) {
        Iterator it = entries.iterator();
        if (it.hasNext()) {
            Tuple tuple = (Tuple)it.next();
            LogHandler first = (LogHandler)tuple.getFirst();
            BrutalityHandler second = (BrutalityHandler)tuple.getSecond();
            DebugHelper.debug(String.valueOf(new StringBuilder("Migrating ").append(first.text).append(" to new set")));
            Iterator it2 = first.getItemSet().iterator();

            while (true) {
                while (!it2.hasNext()) {
                }

                String str = (String)it2.next();

                try {
                    DebugHelper.debug(String.valueOf(new StringBuilder("Migrating ").append(str)));
                    second.matchesString(str, "");
                } catch (Exception e) {
                    MessageUtils.x(
                        String.valueOf(
                            new StringBuilder("Failed to migrate name: ")
                                .append(EnumChatFormatting.YELLOW)
                                .append(str)
                                .append(EnumChatFormatting.RED)
                                .append(" (Skipped). Reason: ")
                                .append(e.getMessage())
                        )
                    );
                    DebugHelper.error(String.valueOf(new StringBuilder("Failed to migrate name: ").append(str).append(" - ").append(e.getMessage())));
                }
            }
        }

        MessageUtils.x(String.valueOf(new StringBuilder().append(EnumChatFormatting.GREEN).append("All migrations completed successfully!")));
        MessageUtils.x(
            String.valueOf(
                new StringBuilder()
                    .append(EnumChatFormatting.GREEN)
                    .append("Check your lists using the command")
                    .append(EnumChatFormatting.YELLOW)
                    .append("/<list_name> list")
                    .append(EnumChatFormatting.GREEN)
                    .append(" to see the names.")
            )
        );
        entries.clear();
    }

    public static int getPendingNameCount() {
        return entries.stream().map(tuple -> tuple.getFirst().getItemSet().size()).reduce(0, (integer, integer2) -> Integer.sum(integer, integer2));
    }
}
