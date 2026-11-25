package org.brutality.utils.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import org.brutality.utils.MessageUtils;
import org.lwjgl.opengl.GL11;

public final class HotbarHelper {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<Integer> itemSet = new HashSet<>();
    private static boolean flag;
    private static int count;
    private static File configFile;
    private static int count2 = -1;
    private static long timestamp = 0L;
    private static final long timestamp2 = 1248777321440083968L;
    private static final long timestamp3 = 120L;

    private static void handleInt(int n, boolean flag) {
        String str = n >= 0 && n < 9 ? "Hotbar " + (n + 1) : "Slot " + (n + 1);
        MessageUtils.x(flag ? MessageUtils.text11 + "Locked " + str + "." : MessageUtils.text15 + "Unlocked " + str + ".");
    }

    public static void init() {
        configFile = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/slotlock.json");
        load();
    }

    public static boolean matchesSlot(Slot slot) {
        return slot != null && matchesInt(slot.getSlotIndex());
    }

    private static void load() {
        itemSet.clear();
        if (configFile != null && configFile.exists()) {
            try {
                try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json != null && json.has("lockedSlots")) {
                        boolean flag = false;

                        for (JsonElement element : json.getAsJsonArray("lockedSlots")) {
                            int asInt = element.getAsInt();
                            if (asInt >= 36 && asInt <= 44) {
                                asInt -= 36;
                                flag = true;
                            }

                            itemSet.add(asInt);
                        }

                        if (flag) {
                            save();
                        }

                        return;
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static boolean matchesC07PacketPlayerDigging(C07PacketPlayerDigging packet) {
        if (packet == null) {
            return false;
        }
        if (packet.getStatus() != Action.DROP_ITEM && packet.getStatus() != Action.DROP_ALL_ITEMS) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            InventoryPlayer inventory = mc.thePlayer.inventory;
            int currentItem = inventory.currentItem;
            if (currentItem >= 0 && currentItem < 9 && matchesInt(currentItem)) {
                process2();
                return true;
            }
            if (mc.currentScreen instanceof GuiContainer gui) {
                Slot theSlot = gui.getTheSlot();
                if (theSlot != null && matchesSlot2(theSlot) && matchesInt(theSlot.getSlotIndex())) {
                    process2();
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    public static void handleGuiContainer(GuiContainer gui, int n) {
        if (gui != null && n != 0) {
            KeyBinding keyBindSlotLock = Minecraft.getMinecraft().gameSettings.keyBindSlotLock;
            if (keyBindSlotLock != null && n == keyBindSlotLock.getKeyCode()) {
                long now = System.currentTimeMillis();
                if (n != count2 || now - timestamp >= timestamp3) {
                    count2 = n;
                    timestamp = now;
                    Slot hoveredSlot = gui.getHoveredSlot();
                    if (matchesSlot2(hoveredSlot)) {
                        handleSlot(hoveredSlot);
                    }
                }
            }
        }
    }

    public static void setFlag(boolean flag2) {
        flag = flag2;
    }

    public static void process3() {
        count++;
    }

    public static boolean checkState() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.thePlayer.inventory != null) {
            int currentItem = mc.thePlayer.inventory.currentItem;
            return currentItem >= 0 && currentItem < 9 && matchesInt(currentItem);
        }
        return false;
    }

    private HotbarHelper() {
    }

    public static void handleFlag(boolean flag) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.playSound("random.click", 1.0F, flag ? 1.25F : 0.85F);
        }
    }

    private static void save() {
        if (configFile == null) {
            configFile = new File(Minecraft.getMinecraft().mcDataDir, "Brutality/slotlock.json");
        }

        try {
            File parentFile = configFile.getParentFile();
            if (parentFile != null && !parentFile.exists()) {
                parentFile.mkdirs();
            }

            JsonObject json = new JsonObject();
            JsonArray jsonArray = new JsonArray();

            for (int n : itemSet) {
                jsonArray.add(new JsonPrimitive(n));
            }

            json.add("lockedSlots", jsonArray);

            try (PrintWriter writer = new PrintWriter(new FileWriter(configFile))) {
                writer.println(gson.toJson((JsonElement)json));
            }
        } catch (Exception ignored) {
        }
    }

    public static void process4() {
        if (count > 0) {
            count--;
        }
    }

    public static boolean matchesSlot2(Slot slot) {
        if (slot == null) {
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer != null && slot.inventory == mc.thePlayer.inventory;
    }

    public static void handleInt(int n, int n2) {
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        Gui.drawRect(n + 2, n2 + 2, n + 14, n2 + 14, (int)timestamp2);
        Gui.drawRect(n + 5, n2 + 9, n + 11, n2 + 14, -10496);
        Gui.drawRect(n + 6, n2 + 6, n + 10, n2 + 9, -10496);
        Gui.drawRect(n + 7, n2 + 4, n + 9, n2 + 7, -10496);
        GlStateManager.enableDepth();
    }

    public static boolean matchesInt(int n) {
        return itemSet.contains(n) || (flag || count > 0) && n >= 0 && n <= 40;
    }

    public static void process() {
        count = 0;
    }

    public static void handleSlot(Slot slot) {
        if (matchesSlot2(slot)) {
            int slotIndex = slot.getSlotIndex();
            boolean flag;
            if (itemSet.contains(slotIndex)) {
                itemSet.remove(slotIndex);
                flag = false;
                handleFlag(false);
            } else {
                itemSet.add(slotIndex);
                flag = true;
                handleFlag(true);
            }

            save();
            handleInt(slotIndex, flag);
        }
    }

    public static void process2() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null) {
            mc.thePlayer.playSound("mob.villager.no", 1.0F, 1.0F);
        }
    }
}
