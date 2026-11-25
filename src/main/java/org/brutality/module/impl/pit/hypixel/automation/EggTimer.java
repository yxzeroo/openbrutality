package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.player.PlayerHelper9;
import org.lwjgl.input.Mouse;

public class EggTimer extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 0.0, 0.0, 1200.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 0.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private long timestamp;
    private static final long timestamp2 = -476352469140504577L;
    private static final long timestamp3 = 1000L;

    @Override
    public void onDisable() {
        super.onDisable();
        HudDragHelper.handleObject(this);
        this.timestamp = 0L;
    }

    private boolean checkState4() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && this.matchesItemStack(stack)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkState() {
        return System.currentTimeMillis() < this.timestamp || this.checkState4() || this.checkState2();
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (Item.getIdFromItem(item) != 383) {
            return false;
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityTag")) {
            String str = stack.getTagCompound().getCompoundTag("EntityTag").getString("id");
            return "MushroomCow".equals(str) || "minecraft:mooshroom".equals(str);
        }
        return stack.getMetadata() == 96;
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        float f = (float)this.xPosSetting.computeDouble();
        float f2 = (float)this.yPosSetting.computeDouble();
        boolean flag = mc.currentScreen instanceof GuiChat;
        boolean flag2 = this.checkState();
        boolean flag3 = flag2 || flag && this.showInChatSetting.isEnabled();
        if (!flag3) {
            HudDragHelper.handleObject(this);
        } else {
            if (flag) {
                int[] ints = HudDragHelper.computeIntArray();
                int n = ints[0];
                int n2 = ints[1];
                if (Mouse.isButtonDown(0)) {
                    if (!HudDragHelper.matchesObject(this)) {
                        String str = this.computeString();
                        int textWidth = computeFontRenderer(EggTimer.class).getStringWidth(str);
                        int FONT_HEIGHT = computeFontRenderer(EggTimer.class).FONT_HEIGHT;
                        if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                            HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (HudDragHelper.matchesObject(this)) {
                    int[] ints2 = HudDragHelper.computeIntArray(this);
                    if (ints2 != null) {
                        this.xPosSetting.handleDouble2(ints2[0]);
                        this.yPosSetting.handleDouble2(ints2[1]);
                        f = ints2[0];
                        f2 = ints2[1];
                    }
                }
            } else {
                HudDragHelper.handleObject(this);
            }

            FontRenderer fontRenderer = computeFontRenderer(EggTimer.class);
            String str2 = this.computeString();
            fontRenderer.drawStringWithShadow(str2, f, f2, (int)timestamp2);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof BasicEvent4 || event instanceof EventPacketSend) {
                ItemStack stack = PlayerHelper9.onEvent(event);
                if (this.matchesItemStack(stack)) {
                    this.timestamp = System.currentTimeMillis() + timestamp3;
                }
            }

            if (event instanceof GetPacketEvent) {
                this.onGetPacketEvent((GetPacketEvent)event);
            }
        }
    }

    private String computeString() {
        var str = this.showLabelSetting.isEnabled() ? "Egg: " : "";
        if (System.currentTimeMillis() < this.timestamp) {
            return EnumChatFormatting.WHITE + str + EnumChatFormatting.RED + "Cooldown";
        }
        if (this.checkState4()) {
            return EnumChatFormatting.WHITE + str + EnumChatFormatting.GREEN + "Ready";
        }
        return this.checkState2()
            ? EnumChatFormatting.WHITE + str + EnumChatFormatting.RED + "Cooldown"
            : EnumChatFormatting.WHITE + str + EnumChatFormatting.GRAY + "None";
    }

    private boolean checkState2() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (player == null) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && this.matchesItemStack2(stack)) {
                return true;
            }
        }

        return false;
    }

    private void onGetPacketEvent(GetPacketEvent getPacketEvent) {
        if (getPacketEvent.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = getPacketEvent.getPacket();
            String text = packet.getChatComponent().getUnformattedText();
            if (text.contains("DEATH!")) {
                this.timestamp = 0L;
            }
        }
    }

    private boolean matchesItemStack2(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (Item.getIdFromItem(item) != 383) {
            return false;
        }
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("EntityTag")) {
            String str = stack.getTagCompound().getCompoundTag("EntityTag").getString("id");
            return "Skeleton".equals(str) || "minecraft:skeleton".equals(str);
        }
        return stack.getMetadata() == 51;
    }

    public EggTimer() {
        super("Egg Timer", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.yPosSetting, this.xPosSetting, this.showInChatSetting, this.showLabelSetting});
    }
}
