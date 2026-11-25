package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.PlayerEvent4;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.timer.TimerUtil;

public class AutoTacticalInsertion extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final NumberSetting healthSetting = new NumberSetting("Health", this, 8.0, 0.0, 20.0, 1.0);
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 500.0, 0.0, 5000.0, 50.0);
    private final BooleanSetting showHUDSetting = new BooleanSetting("Show HUD", this, true);
    private final TimerUtil timerUtil = new TimerUtil();
    private int count = -1;
    private boolean flag;
    private boolean flag2;
    private String text;
    private double amount;
    private double amount2;
    private double amount3;
    private static final long timestamp = -3499405366964256769L;

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        String str = stack.getDisplayName() == null ? "" : stack.getDisplayName().replaceAll("\\u00a7[0-9a-fk-or]", "").toLowerCase();
        return stack.getItem() == Item.getItemById(369) || str.contains("tactical insertion") || str.contains("tactical") && str.contains("insertion");
    }

    @Override
    public void onRender() {
        if (this.checkState44() && this.showHUDSetting.isEnabled() && mc.theWorld != null && mc.thePlayer != null) {
            ScaledResolution sr = new ScaledResolution(mc);
            FontRenderer fontRenderer = computeFontRenderer(AutoTacticalInsertion.class);
            String str;
            if (this.flag2 && this.text != null) {
                EnumChatFormatting WHITE = EnumChatFormatting.WHITE;
                EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                EnumChatFormatting RESET = EnumChatFormatting.RESET;
                String text = this.text;
                String str2 = String.format("%.0f", this.amount);
                String str3 = String.format("%.0f", this.amount2);
                String str4 = String.format("%.0f", this.amount3);
                EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                String str5 = str4;
                String str6 = str3;
                String str7 = str2;
                String str8 = text;
                EnumChatFormatting enumChatFormatting = RESET;
                EnumChatFormatting enumChatFormatting2 = GRAY;
                str = WHITE + "Tactical: " + enumChatFormatting2 + "[" + enumChatFormatting + str8 + " (X: " + str7 + " Y: " + str6 + " Z: " + str5 + ")" + GRAY2 + "]";
            } else {
                EnumChatFormatting GRAY3 = EnumChatFormatting.GRAY;
                EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                EnumChatFormatting GRAY4 = EnumChatFormatting.GRAY;
                str = EnumChatFormatting.WHITE + "Tactical: " + GRAY4 + "[" + GREEN + "Ready" + GRAY3 + "]";
            }

            fontRenderer.drawStringWithShadow(str, 4.0F, sr.getScaledHeight() - 20, (int)timestamp);
        }
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            if (this.matchesItemStack(mc.thePlayer.inventory.getStackInSlot(i))) {
                return i;
            }
        }

        return -1;
    }

    private void handleFlag(boolean flag) {
        if (mc.thePlayer != null && this.count != -1) {
            this.process();
        }

        this.count = -1;
        this.flag = false;
        if (flag) {
            this.flag2 = false;
            this.text = null;
        }
    }

    public AutoTacticalInsertion() {
        super("Auto Tactical Insertion", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.healthSetting, this.delaySetting, this.showHUDSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (!(event instanceof RenderEvent4) && (!(event instanceof GetEntityEvent) || ((GetEntityEvent)event).getEntity() != mc.thePlayer)) {
                if (event instanceof ChatEvent) {
                    String str = ((ChatEvent)event).message == null ? "" : ((ChatEvent)event).message.getUnformattedText();
                    if (str.replaceAll("\\s+", " ").trim().equalsIgnoreCase("GENIUS PLAY! Next respawn set here!")) {
                        this.flag = false;
                        if (this.count != -1) {
                            this.process();
                        }
                    }
                } else if (!(event instanceof BasicEvent2) && !(event instanceof BasicEvent4) && !(event instanceof PlayerEvent4)) {
                    if (event instanceof EventTick && mc.thePlayer != null && mc.theWorld != null) {
                        EventTick event2 = (EventTick)event;
                        if (!EventState.onEvent(event2)) {
                            if (this.count != -1 && !mc.thePlayer.isUsingItem()) {
                                this.process();
                            }
                        } else if (!mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F) && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                            boolean flag = mc.thePlayer.getHealth() + mc.thePlayer.getAbsorptionAmount() <= this.healthSetting.computeDouble();
                            if (!flag) {
                                this.flag = true;
                            }

                            if (this.flag && flag && this.count == -1 && this.timerUtil.matchesLong10((long)this.delaySetting.computeDouble())) {
                                int n = this.computeInt();
                                if (n != -1) {
                                    this.count = mc.thePlayer.inventory.currentItem;
                                    mc.thePlayer.inventory.currentItem = n;
                                    mc.playerController.syncCurrentPlayItem();
                                    NetworkHelper.handlePacket2(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                                    this.text = FreshHelper.computeString(mc.theWorld);
                                    this.amount = mc.thePlayer.posX;
                                    this.amount2 = mc.thePlayer.posY;
                                    this.amount3 = mc.thePlayer.posZ;
                                    this.flag2 = this.text != null;
                                    this.timerUtil.reset();
                                    this.flag = false;
                                }
                            }
                        } else {
                            this.handleFlag(true);
                        }
                    }
                } else {
                    if (this.count != -1) {
                        event.cancel();
                    }
                }
            } else {
                this.handleFlag(true);
            }
        }
    }

    private void process() {
        mc.thePlayer.inventory.currentItem = this.count;
        mc.playerController.syncCurrentPlayItem();
        this.count = -1;
    }
}
