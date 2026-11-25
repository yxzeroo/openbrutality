package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.MessageUtils;

public final class ChatLog extends Module {
    private static final String text = "Brutality/logs/pit-chat.jsonl";
    private static final Pattern pattern = Pattern.compile("^\\[(?:([IVXLCDM]+)-)?(\\d{1,3})]\\s+(?:\\[[^\\]\\r\\n]+]\\s+)*([A-Za-z0-9_]{1,16}):\\s+(.+)$");
    private static final Pattern pattern2 = Pattern.compile("[a-z][a-z0-9']{2,31}");
    private static final Set<String> itemSet = Set.of(
        "the",
        "and",
        "for",
        "you",
        "are",
        "with",
        "that",
        "this",
        "have",
        "what",
        "your",
        "was",
        "but",
        "not",
        "can",
        "they",
        "just",
        "its",
        "there",
        "from",
        "about",
        "how",
        "who",
        "then",
        "when",
        "will",
        "has",
        "had",
        "any",
        "get",
        "it's"
    );
    private final Map<String, Integer> lookupMap = new HashMap<>();
    private int count;
    private boolean flag;
    private static final long timestamp = 20L;

    private static JsonObject computeJsonObject(String str) {
        JsonObject json = new JsonObject();
        json.addProperty("type", str);
        json.addProperty("time", Instant.now().toString());
        return json;
    }

    public ChatLog() {
        super("ChatLog", 0, Category.PIT);
    }

    @Override
    public void onEnable() {
        this.count = 0;
        this.lookupMap.clear();
        this.flag = false;
    }

    @Override
    public void onDisable() {
        if (this.count != 0) {
            JsonArray jsonArray = new JsonArray();
            this.lookupMap
                .entrySet()
                .stream()
                .sorted(Entry.<String, Integer>comparingByValue().reversed().thenComparing(Entry.comparingByKey()))
                .limit(timestamp)
                .forEach(entry -> {
                    JsonObject json = new JsonObject();
                    json.addProperty("term", entry.getKey());
                    json.addProperty("count", entry.getValue());
                    jsonArray.add(json);
                });
            JsonObject json2 = computeJsonObject("summary");
            json2.addProperty("messages", this.count);
            json2.add("topTerms", jsonArray);
            this.matchesJsonObject(json2);
            this.count = 0;
            this.lookupMap.clear();
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof ChatEvent chatEvent && chatEvent.byteVal == 0 && chatEvent.message != null && ConnectToServerHandler.checkState2()) {
            String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(chatEvent.message.getUnformattedText());
            Matcher matcher = pattern.matcher(textWithoutFormattingCodes.strip());
            if (matcher.matches()) {
                String str = matcher.group(4);
                JsonObject json = computeJsonObject("chat");
                json.addProperty("prestige", computeInt(matcher.group(1)));
                json.addProperty("level", Integer.parseInt(matcher.group(2)));
                json.addProperty("message", str);
                if (this.matchesJsonObject(json)) {
                    this.count++;
                    Matcher matcher2 = pattern2.matcher(str.toLowerCase(Locale.ROOT));

                    while (matcher2.find()) {
                        String str2 = matcher2.group();
                        if (!itemSet.contains(str2)) {
                            this.lookupMap.merge(str2, 1, Integer::sum);
                        }
                    }
                }
            }
        }
    }

    private boolean matchesJsonObject(JsonObject json) {
        try {
            Path path = mc.mcDataDir.toPath().resolve("Brutality/logs/pit-chat.jsonl");
            Files.createDirectories(path.getParent());
            Files.writeString(path, json + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            this.flag = false;
            return true;
        } catch (SecurityException | IOException e) {
            if (!this.flag) {
                this.flag = true;
                MessageUtils.x("[ChatLog] Could not write Brutality/logs/pit-chat.jsonl: " + e.getMessage());
            }

            return false;
        }
    }

    private static int computeInt(String str) {
        if (str == null) {
            return 0;
        }
        int n = 0;
        short s = 0;

        for (int i = str.length() - 1; i >= 0; i--) {
            short s2 = switch (str.charAt(i)) {
                case 'C' -> 100;
                case 'D' -> 500;
                default -> throw new IllegalArgumentException("Invalid Roman numeral");
                case 'I' -> 1;
                case 'L' -> 50;
                case 'M' -> 1000;
                case 'V' -> 5;
                case 'X' -> 10;
            };
            n += s2 < s ? -s2 : s2;
            s = s2;
        }

        return n;
    }
}
