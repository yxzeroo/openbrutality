package org.brutality.utils.hypixel.pit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.brutality.Brutality;

public class DisplayHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final File file = new File("Brutality/Data/KingsQuest");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static DisplayHelper.Entry entry = new DisplayHelper.Entry();
    private static String currentUsername = "";

    private static void handleItemStack(ItemStack stack) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("display")) {
            if (stack.getTagCompound().getCompoundTag("display").hasKey("Lore")) {
                NBTTagList tagList = stack.getTagCompound().getCompoundTag("display").getTagList("Lore", 8);
                DisplayHelper.Entry entry2 = new DisplayHelper.Entry();
                boolean flag = false;
                boolean flag2 = false;

                for (int i = 0; i < tagList.tagCount(); i++) {
                    String stringTagAt = tagList.getStringTagAt(i);
                    String str = stringTagAt.replaceAll("§.", "");
                    if (str.contains("The Task:")) {
                        flag = true;
                        flag2 = false;
                    } else if (str.contains("The Rewards:")) {
                        flag = false;
                        flag2 = true;
                    } else {
                        if (flag) {
                            if (str.contains("Bring a") && str.contains("Cake")) {
                                String[] parts = str.split("Bring a ");
                                if (parts.length > 1) {
                                    entry2.text = parts[1].trim();
                                }
                            } else if (str.contains("Kill") || str.contains("Kills:")) {
                                handleString(str, entry2);
                            } else if (str.contains("Renown:")) {
                                handleString2(str, entry2);
                            }
                        }

                        if (flag2) {
                            if (str.contains("XP") || str.contains("xp")) {
                                entry2.count4 = computeInt(str);
                            } else if (str.contains("g") && !str.contains("Helmet")) {
                                entry2.count5 = computeInt(str);
                            } else if (!str.isEmpty() && !str.contains("+") && !str.contains("XP") && !str.contains("g")) {
                                entry2.text2 = str.trim();
                            }
                        }

                        if (str.equals("The task is incomplete!")) {
                            entry2.unacceptedMode = DisplayHelper.UnacceptedMode.INCOMPLETE;
                        } else if (str.equals("Click to accept!")) {
                            entry2.unacceptedMode = DisplayHelper.UnacceptedMode.UNACCEPTED;
                        } else if (str.equals("Click to complete!")) {
                            entry2.unacceptedMode = DisplayHelper.UnacceptedMode.COMPLETABLE;
                        } else if (str.contains("Grats")) {
                            entry2.unacceptedMode = DisplayHelper.UnacceptedMode.ACCEPTED;
                        } else if (str.contains("You already completed the quest recently!")) {
                            entry2.unacceptedMode = DisplayHelper.UnacceptedMode.ACCEPTED;
                        }
                    }
                }

                entry2.lastUpdated = System.currentTimeMillis();
                entry = entry2;
                process6();
            }
        }
    }

    public static void process3() {
        if (currentUsername == null || currentUsername.isEmpty()) {
            process5();
        }

        try {
            File file2 = new File(file, currentUsername + "_quest.json");
            JsonObject json = new JsonObject();
            json.addProperty("status", entry.unacceptedMode.name());
            json.addProperty("currentKills", entry.count);
            json.addProperty("requiredKills", entry.count2);
            json.addProperty("currentRenown", entry.currentRenown);
            json.addProperty("requiredRenown", entry.count3);
            json.addProperty("hasCake", entry.flag);
            json.addProperty("cakeType", entry.text);
            json.addProperty("rewardItem", entry.text2);
            json.addProperty("rewardXP", entry.count4);
            json.addProperty("rewardGold", entry.count5);
            json.addProperty("lastUpdated", entry.lastUpdated);

            try (PrintWriter writer = new PrintWriter(new FileWriter(file2))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception ignored) {
        }
    }

    static {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void handleString(String str) {
        if (str.contains("+") && str.contains("Renown")) {
            try {
                Pattern pattern = Pattern.compile("\\+(\\d+)\\s*Renown");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    int n = Integer.parseInt(matcher.group(1));
                    entry.currentRenown += n;
                    if (entry.currentRenown > entry.count3) {
                        entry.currentRenown = entry.count3;
                    }

                    process3();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void process4() {
        if (mc.currentScreen instanceof GuiChest) {
            ContainerChest openContainer = (ContainerChest)mc.thePlayer.openContainer;
            IInventory lowerChestInventory = openContainer.getLowerChestInventory();
            if (lowerChestInventory != null) {
                String text = lowerChestInventory.getDisplayName().getUnformattedText();
                if (text.contains("The King")) {
                    process5();

                    for (int i = 0; i < lowerChestInventory.getSizeInventory(); i++) {
                        ItemStack stack = lowerChestInventory.getStackInSlot(i);
                        if (stack != null) {
                            String displayName = stack.getDisplayName();
                            if (displayName.contains("The King's Quest") || displayName.contains("Special Mission")) {
                                handleItemStack(stack);
                                break;
                            }
                        }
                    }

                    process3();
                }
            }
        }
    }

    private static void process5() {
        try {
            if (mc.thePlayer != null && mc.thePlayer.getName() != null) {
                currentUsername = mc.thePlayer.getName();
            } else if (mc.getSession() != null && mc.getSession().getUsername() != null) {
                currentUsername = mc.getSession().getUsername();
            // VENDOR-DISABLED: Brutality.getAuthHandler().getUsername()
            }
        } catch (Exception e) {
            currentUsername = "Unknown";
        }
    }

    private static int computeInt(String str) {
        try {
            String str2 = str.replaceAll("[^0-9,]", "");
            String str3 = str2.replace(",", "");
            if (!str3.isEmpty()) {
                return Integer.parseInt(str3);
            }
        } catch (Exception ignored) {
        }

        return 0;
    }

    private static void handleString2(String str, DisplayHelper.Entry entry) {
        try {
            if (str.contains("/")) {
                String[] parts = str.split("/");
                if (parts.length == 2) {
                    String str2 = parts[0].replaceAll("[^0-9]", "");
                    String str3 = parts[1].replaceAll("[^0-9]", "");
                    if (!str2.isEmpty()) {
                        entry.currentRenown = Integer.parseInt(str2);
                    }

                    if (!str3.isEmpty()) {
                        entry.count3 = Integer.parseInt(str3);
                    }
                }
            } else if (str.contains("Collect") && str.contains("renown")) {
                String str4 = str.replaceAll("[^0-9]", "");
                if (!str4.isEmpty()) {
                    entry.count3 = Integer.parseInt(str4);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private static void handleString(String str, DisplayHelper.Entry entry) {
        try {
            if (str.contains("/")) {
                String[] parts = str.split("/");
                if (parts.length == 2) {
                    String str2 = parts[0].replaceAll("[^0-9]", "");
                    String str3 = parts[1].replaceAll("[^0-9]", "");
                    if (!str2.isEmpty()) {
                        entry.count = Integer.parseInt(str2);
                    }

                    if (!str3.isEmpty()) {
                        entry.count2 = Integer.parseInt(str3);
                    }
                }
            } else if (str.contains("Kill") && str.contains("players")) {
                String str4 = str.replaceAll("[^0-9]", "");
                if (!str4.isEmpty()) {
                    entry.count2 = Integer.parseInt(str4);
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void process6() {
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            entry.flag = false;

            for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
                if (stack != null) {
                    String str = stack.getDisplayName().replaceAll("§.", "");
                    if (str.contains("Mini Cake") || entry.text != null && str.contains(entry.text)) {
                        entry.flag = true;
                        break;
                    }
                }
            }
        }
    }

    public static DisplayHelper.Entry getEntry() {
        return entry;
    }

    public static boolean checkState() {
        return entry.unacceptedMode == DisplayHelper.UnacceptedMode.COMPLETABLE
            || entry.count >= entry.count2 && entry.currentRenown >= entry.count3 && entry.flag;
    }

    public static String computeString() {
        switch (entry.unacceptedMode) {
            case UNACCEPTED:
                return "King's Quest - Unaccepted (Visit The King)";
            case INCOMPLETE:
                return "King's Quest - In Progress";
            case COMPLETABLE:
                return "King's Quest - Ready to Complete!";
            case ACCEPTED:
                return "King's Quest - Already Completed";
            default:
                return "King's Quest - Unknown Status";
        }
    }

    public static void process() {
        process5();

        try {
            File file2 = new File(file, currentUsername + "_quest.json");
            if (!file2.exists()) {
                entry = new DisplayHelper.Entry();
            } else {
                try (BufferedReader reader = new BufferedReader(new FileReader(file2))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null) {
                        entry = new DisplayHelper.Entry();
                        if (json.has("status")) {
                            try {
                                entry.unacceptedMode = DisplayHelper.UnacceptedMode.valueOf(json.get("status").getAsString());
                            } catch (Exception e) {
                                entry.unacceptedMode = DisplayHelper.UnacceptedMode.UNACCEPTED;
                            }
                        }

                        if (json.has("currentKills")) {
                            entry.count = json.get("currentKills").getAsInt();
                        }

                        if (json.has("requiredKills")) {
                            entry.count2 = json.get("requiredKills").getAsInt();
                        }

                        if (json.has("currentRenown")) {
                            entry.currentRenown = json.get("currentRenown").getAsInt();
                        }

                        if (json.has("requiredRenown")) {
                            entry.count3 = json.get("requiredRenown").getAsInt();
                        }

                        if (json.has("hasCake")) {
                            entry.flag = json.get("hasCake").getAsBoolean();
                        }

                        if (json.has("cakeType")) {
                            entry.text = json.get("cakeType").getAsString();
                        }

                        if (json.has("rewardItem")) {
                            entry.text2 = json.get("rewardItem").getAsString();
                        }

                        if (json.has("rewardXP")) {
                            entry.count4 = json.get("rewardXP").getAsInt();
                        }

                        if (json.has("rewardGold")) {
                            entry.count5 = json.get("rewardGold").getAsInt();
                        }

                        if (json.has("lastUpdated")) {
                            entry.lastUpdated = json.get("lastUpdated").getAsLong();
                        }

                        process6();
                        return;
                    }

                    entry = new DisplayHelper.Entry();
                }
            }
        } catch (Exception e2) {
            entry = new DisplayHelper.Entry();
        }
    }

    public static void process2() {
        entry = new DisplayHelper.Entry();
        process3();
    }

    public static class Entry {
        public DisplayHelper.UnacceptedMode unacceptedMode = DisplayHelper.UnacceptedMode.UNACCEPTED;
        public int count = 0;
        public int count2 = 100;
        public int currentRenown = 0;
        public int count3 = 10;
        public boolean flag = false;
        public String text = "";
        public long lastUpdated = 0L;
        public String text2 = "";
        public int count4 = 0;
        public int count5 = 0;
    }

    public static enum UnacceptedMode {
        UNACCEPTED("Click to accept!"),
        INCOMPLETE("The task is incomplete!"),
        COMPLETABLE("Click to complete!"),
        ACCEPTED("Grats! :D");

        private final String message2;

        public String getMessage() {
            return this.message2;
        }

        private static DisplayHelper.UnacceptedMode[] computeUnacceptedModeArray() {
            return new DisplayHelper.UnacceptedMode[]{UNACCEPTED, INCOMPLETE, COMPLETABLE, ACCEPTED};
        }

        private UnacceptedMode(String message2) {
            this.message2 = message2;
        }
    }
}
