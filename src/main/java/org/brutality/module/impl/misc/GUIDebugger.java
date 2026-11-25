package org.brutality.module.impl.misc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.utils.MessageUtils;

public class GUIDebugger extends Module {
    private GuiScreen guiScreen;
    private boolean flag;

    private void handleStringBuilder(StringBuilder sb, String str) {
        String str2 = "[GUI Debugger] " + str;
        sb.append(str2).append(System.lineSeparator());
        MessageUtils.x(str2);
    }

    private void handleStringBuilder(StringBuilder sb) {
        try {
            Path path = mc.mcDataDir.toPath().resolve("Brutality/logs/gui-debugger.log");
            Files.createDirectories(path.getParent());
            Files.writeString(path, sb, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            this.flag = false;
        } catch (SecurityException | IOException e) {
            if (!this.flag) {
                this.flag = true;
                MessageUtils.x("[GUI Debugger] Could not write Brutality/logs/gui-debugger.log: " + e.getMessage());
            }
        }
    }

    public GUIDebugger() {
        super("GUI Debugger", 0, Category.MISCELLANEOUS);
        this.setRequiresInGame(false);
    }

    @Override
    public void onEnable() {
        this.guiScreen = null;
        this.flag = false;
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
            GuiScreen screen = mc.currentScreen;
            if (screen != this.guiScreen) {
                this.guiScreen = screen;
                if (screen instanceof GuiContainer gui) {
                    String str = gui.inventorySlots.inventorySlots.isEmpty()
                        ? gui.getClass().getSimpleName()
                        : gui.inventorySlots.inventorySlots.get(0).inventory.getDisplayName().getUnformattedText();
                    int n = gui.inventorySlots.inventorySlots.isEmpty() ? 0 : gui.inventorySlots.inventorySlots.get(0).inventory.getSizeInventory();
                    StringBuilder sb = new StringBuilder().append('[').append(Instant.now()).append(']').append(System.lineSeparator());
                    this.handleStringBuilder(sb, str + " | rows=" + (n + 8) / 9 + " | slots=" + gui.inventorySlots.inventorySlots.size());

                    for (int i = 0; i < gui.inventorySlots.inventorySlots.size(); i++) {
                        Slot slot = gui.inventorySlots.inventorySlots.get(i);
                        ItemStack stack = slot.getStack();
                        if (stack != null) {
                            ResourceLocation nameForObject = Item.itemRegistry.getNameForObject(stack.getItem());
                            String str2 = nameForObject == null ? "unknown" : nameForObject.toString();
                            String str3 = stack.getDisplayName().replace('§', '&');
                            String str4 = stack.getTooltip(mc.thePlayer, false).toString().replace('§', '&');
                            String str5 = stack.hasTagCompound() ? stack.getTagCompound().toString().replace('§', '&') : "{}";
                            int idFromItem = Item.getIdFromItem(stack.getItem());
                            int stackSize = stack.stackSize;
                            int metadata = stack.getMetadata();
                            int n2 = stackSize;
                            int n3 = idFromItem;
                            this.handleStringBuilder(
                                sb,
                                "slot="
                                    + i
                                    + " item="
                                    + str2
                                    + " numericId="
                                    + n3
                                    + " count="
                                    + n2
                                    + " meta="
                                    + metadata
                                    + " name="
                                    + str3
                                    + " lore="
                                    + str4
                                    + " nbt="
                                    + str5
                            );
                        }
                    }

                    this.handleStringBuilder(sb);
                }
            }
        }
    }
}
