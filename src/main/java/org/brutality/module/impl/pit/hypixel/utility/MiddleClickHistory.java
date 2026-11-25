package org.brutality.module.impl.pit.hypixel.utility;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.gui.chat.FetchComponent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.utils.hypixel.pit.ParseHelper;
import org.lwjgl.input.Mouse;

public class MiddleClickHistory extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final String text = "https://pitpanda.rocks/api/itemsearch/nonce";
    private static final String text2 = "617c9c76-e5a5-4213-a6b9-aded74ae64c8";
    private volatile boolean flag = false;
    private Map<ItemStack, MiddleClickHistory.Entry> lookupMap = new HashMap<>();
    private final Map<Integer, List<MiddleClickHistory.Entry2>> lookupMap2 = new HashMap<>();
    private static MiddleClickHistory instance;
    private int count = -1;
    private long timestamp;
    private final long timestamp2 = 60000L;
    private long timestamp3 = 0L;
    private final long timestamp4 = 2000L;
    private static final int count2 = 50;
    private boolean flag2 = false;

    public static void handleItemStack2(ItemStack stack, List<String> list) {
        MiddleClickHistory middleClickHistory = instance;
        if (middleClickHistory != null && middleClickHistory.checkState44() && stack != null && list != null && System.currentTimeMillis() <= middleClickHistory.timestamp) {
            Integer integer = middleClickHistory.computeInteger(stack);
            if (integer != null && integer == middleClickHistory.count) {
                List list2 = middleClickHistory.lookupMap2.get(integer);
                list.add("");
                if (list2 != null && !list2.isEmpty()) {
                    list.add("§7Most recent owners:");

                    for (int i = 0; i < Math.min(8, list2.size()); i++) {
                        MiddleClickHistory.Entry2 entry2 = (MiddleClickHistory.Entry2)list2.get(i);
                        list.add("§7 - §f" + entry2.getUsername() + " §7(" + entry2.getText() + ")");
                    }
                } else {
                    list.add("§7Getting most recent owners...");
                }
            }
        }
    }

    private void sendMessage(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        Runnable task = () -> {
            EntityPlayerSP player = mc.thePlayer;
            if (player != null) {
                player.addChatMessage(new ChatComponentText(str));
            }
        };
        if (mc.isCallingFromMinecraftThread()) {
            task.run();
        } else {
            mc.addScheduledTask(task);
        }
    }

    private void handleItemStack(ItemStack stack, List<MiddleClickHistory.Entry2> entry2s) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound nbt;
        if (stack.getTagCompound().hasKey("display")) {
            nbt = stack.getTagCompound().getCompoundTag("display");
        } else {
            nbt = new NBTTagCompound();
            stack.getTagCompound().setTag("display", nbt);
        }

        NBTTagList tagList = null;
        NBTTagList tagList2;
        if (nbt.hasKey("Lore", 9)) {
            tagList2 = nbt.getTagList("Lore", 8);
            tagList = new NBTTagList();

            for (int i = 0; i < tagList2.tagCount(); i++) {
                tagList.appendTag(new NBTTagString(tagList2.getStringTagAt(i)));
            }

            NBTTagList tagList3 = new NBTTagList();
            boolean flag = false;

            for (int j = 0; j < tagList2.tagCount(); j++) {
                String stringTagAt = tagList2.getStringTagAt(j);
                if (stringTagAt.contains("Most recent owners:")) {
                    flag = true;
                } else if (!flag) {
                    tagList3.appendTag(new NBTTagString(stringTagAt));
                } else if (stringTagAt.contains("Earliest owner:")) {
                    flag = false;
                }
            }

            tagList2 = tagList3;
        } else {
            tagList2 = new NBTTagList();
        }

        if (tagList2.tagCount() > 0) {
            tagList2.appendTag(new NBTTagString(""));
        }

        tagList2.appendTag(new NBTTagString("§fMost recent owners:"));
        int n = 0;

        for (MiddleClickHistory.Entry2 entry2 : entry2s) {
            if (entry2.getUsername().equals("---")) {
                if (n != 0) {
                    tagList2.appendTag(new NBTTagString("§8§m------------------------------"));
                }
            } else if (entry2.getUsername().contains("Earliest owner:")) {
                String[] parts = entry2.getUsername().split(":");
                tagList2.appendTag(new NBTTagString("§fEarliest owner: §b" + parts[1].trim() + " §7" + entry2.getText()));
            } else if (n < 50) {
                tagList2.appendTag(new NBTTagString("§8- §b" + entry2.getUsername() + " §7" + entry2.getText()));
                n++;
            }
        }

        nbt.setTag("Lore", tagList2);
        this.lookupMap.put(stack, new MiddleClickHistory.Entry(tagList, System.currentTimeMillis()));
    }

    private List<MiddleClickHistory.Entry2> computeList(int n) {
        List list = new ArrayList();

        try {
            URL url = new URL("https://pitpanda.rocks/api/itemsearch/nonce" + n + "?key=617c9c76-e5a5-4213-a6b9-aded74ae64c8");
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            int responseCode = conn.getResponseCode();
            if (responseCode == 429) {
                this.sendMessage("§8[§cB§8] - §cRate limited by PitPanda API. Please wait before trying again.");
                return list;
            }

            if (responseCode != 200) {
                this.sendMessage("§8[§cB§8] - §cPitPanda API returned status: " + responseCode);
                return list;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }

            reader.close();
            JsonElement element = new JsonParser().parse(sb.toString());
            JsonObject asJsonObject = element.getAsJsonObject();
            ParseHelper.Entry entry = ParseHelper.parse(asJsonObject);
            HashMap<String, String> map = new HashMap<>();

            for (ParseHelper.Entry2 entry2 : entry.entries) {
                if (list.size() >= 50) {
                    break;
                }

                String str2 = map.computeIfAbsent(entry2.text, str3 -> {
                    String str4 = FetchComponent.computeString(str3);
                    return str4 != null ? str4 : str3.substring(0, Math.min(8, str3.length()));
                });
                list.add(new MiddleClickHistory.Entry2(str2, this.computeString(entry2.timestamp)));
            }

            if (entry.entry2 != null) {
                list.add(new MiddleClickHistory.Entry2("---", "---"));
                String str5 = map.computeIfAbsent(entry.entry2.text, str6 -> {
                    String str7 = FetchComponent.computeString(str6);
                    return str7 != null ? str7 : str6.substring(0, Math.min(8, str6.length()));
                });
                list.add(new MiddleClickHistory.Entry2("Earliest owner: " + str5, this.computeString(entry.entry2.timestamp)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendMessage("§8[§cB§8] - §cError fetching item history: " + e.getMessage());
        }

        return list;
    }

    private Integer computeInteger(ItemStack stack) {
        return stack.hasTagCompound() ? this.computeInteger(stack.getTagCompound()) : null;
    }

    private String computeString(long l) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return l > 0L ? "(" + simpleDateFormat.format(new Date(l)) + ")" : "(Unknown date)";
    }

    private void handleItemStack(ItemStack stack, MiddleClickHistory.Entry entry) {
        if (stack != null && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound.hasKey("display")) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (entry.nbtTagList != null) {
                    compoundTag.setTag("Lore", entry.nbtTagList);
                } else {
                    compoundTag.removeTag("Lore");
                }
            }
        }
    }

    public MiddleClickHistory() {
        super("MiddleClickHistory", 0, Category.PIT);
        instance = this;
    }

    private Integer computeInteger(NBTTagCompound nbt) {
        if (nbt.hasKey("Nonce")) {
            return nbt.getInteger("Nonce");
        }
        if (nbt.hasKey("ExtraAttributes")) {
            NBTTagCompound compoundTag = nbt.getCompoundTag("ExtraAttributes");
            if (compoundTag.hasKey("Nonce")) {
                return compoundTag.getInteger("Nonce");
            }
        }

        if (nbt.hasKey("tag")) {
            NBTTagCompound compoundTag2 = nbt.getCompoundTag("tag");
            if (compoundTag2.hasKey("ExtraAttributes")) {
                NBTTagCompound compoundTag3 = compoundTag2.getCompoundTag("ExtraAttributes");
                if (compoundTag3.hasKey("Nonce")) {
                    return compoundTag3.getInteger("Nonce");
                }
            }
        }

        for (String str : nbt.getKeySet()) {
            NBTBase tag = nbt.getTag(str);
            if (tag instanceof NBTTagCompound) {
                Integer integer = this.computeInteger((NBTTagCompound)tag);
                if (integer != null) {
                    return integer;
                }
            } else if (tag instanceof NBTTagList tagList) {
                for (int i = 0; i < tagList.tagCount(); i++) {
                    if (tagList.getCompoundTagAt(i).hasKey("Nonce")) {
                        return tagList.getCompoundTagAt(i).getInteger("Nonce");
                    }
                }
            }
        }

        return null;
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && mc.thePlayer != null && mc.thePlayer.inventory != null) {
            for (int i = 0; i < mc.thePlayer.inventory.getSizeInventory(); i++) {
                if (mc.thePlayer.inventory.getStackInSlot(i) == stack) {
                    return true;
                }
            }

            if (mc.thePlayer.openContainer != null) {
                for (Object inventorySlot : mc.thePlayer.openContainer.inventorySlots) {
                    if (inventorySlot instanceof Slot && ((Slot)inventorySlot).getStack() == stack) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }

    private ItemStack computeItemStack() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen instanceof GuiContainer gui) {
            Slot slot = this.computeSlot(gui);
            return slot != null && slot.getHasStack() ? slot.getStack() : null;
        }
        return mc.thePlayer.getHeldItem();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                this.process();
                if (!this.checkState44() || this.flag || Minecraft.getMinecraft().thePlayer == null) {
                    this.flag2 = Mouse.isButtonDown(2);
                    return;
                }

                boolean buttonDown = Mouse.isButtonDown(2);
                if (buttonDown && !this.flag2 && mc.currentScreen instanceof GuiContainer) {
                    long now = System.currentTimeMillis();
                    if (now - this.timestamp3 < 2000L) {
                        long l = (2000L - (now - this.timestamp3)) / 1000L + 1L;
                        this.sendMessage("§8[§cB§8] - §ePlease wait " + l + "s before checking another item.");
                        this.flag2 = buttonDown;
                        return;
                    }

                    this.timestamp3 = now;
                    ItemStack stack = this.computeItemStack();
                    if (stack != null) {
                        this.flag = true;
                        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.click"), 1.0F));
                        this.sendMessage("§8[§cB§8] - §bGetting item ownership information...");
                        ItemStack stack2 = stack.copy();
                        Integer integer = this.computeInteger(stack2);
                        if (integer == null || integer <= 0) {
                            this.sendMessage("§8[§cB§8] - §cThis item does not have a valid nonce.");
                            this.flag = false;
                            this.flag2 = buttonDown;
                            return;
                        }

                        this.count = integer;
                        this.timestamp = now + 60000L;
                        this.sendMessage("§8[§cB§8] - §7Found nonce: " + integer + ", querying PitPanda...");
                        CompletableFuture.<List<MiddleClickHistory.Entry2>>supplyAsync(() -> this.computeList(integer))
                            .thenAccept(entry2s -> Minecraft.getMinecraft().addScheduledTask(() -> {
                                if (entry2s != null && !entry2s.isEmpty()) {
                                    this.lookupMap2.put(integer, new ArrayList<>(entry2s));
                                    this.timestamp = System.currentTimeMillis() + 60000L;
                                    this.sendMessage("§8[§cB§8] - §aOwner history loaded into the tooltip.");
                                } else if (entry2s != null && entry2s.isEmpty()) {
                                    this.sendMessage("§8[§cB§8] - §cNo history found for this item.");
                                }

                                this.flag = false;
                            }))
                            .exceptionally(t -> {
                                this.sendMessage("§8[§cB§8] - §cError: " + t.getMessage());
                                this.flag = false;
                                return null;
                            });
                    }
                }

                this.flag2 = buttonDown;
            }
        }
    }

    private Slot computeSlot(GuiContainer gui) {
        try {
            return gui.getTheSlot();
        } catch (Exception e) {
            return null;
        }
    }

    private void process() {
        long now = System.currentTimeMillis();
        var it = this.lookupMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            ItemStack key = (ItemStack)entry.getKey();
            MiddleClickHistory.Entry val = (MiddleClickHistory.Entry)entry.getValue();
            if (now - val.timestamp > 60000L) {
                this.handleItemStack(key, val);
                it.remove();
            }
        }
    }

    public static class Entry {
        public final NBTTagList nbtTagList;
        public final long timestamp;

        public Entry(NBTTagList nbtTagList, long timestamp) {
            this.nbtTagList = nbtTagList;
            this.timestamp = timestamp;
        }
    }

    public static class Entry2 {
        private final String username;
        private final String text;

        public String getUsername() {
            return this.username;
        }

        public Entry2(String username, String text) {
            this.username = username;
            this.text = text;
        }

        public String getText() {
            return this.text;
        }
    }
}
