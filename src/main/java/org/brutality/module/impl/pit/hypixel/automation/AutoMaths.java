package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.network.ServerUtils;

public class AutoMaths extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private String text = null;
    private ScheduledFuture<?> scheduledFuture;
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 1000.0, 0.0, 5000.0, 100.0);
    private final BooleanSetting autoSubmitSetting = new BooleanSetting("Auto Submit", this, true);
    private final BooleanSetting stopMovementInSpawnSetting = new BooleanSetting("Stop Movement In Spawn", this, true);

    private void process3() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindBack.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindLeft.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindRight.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindJump.getKeyCode(), false);
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
    }

    private double computeDouble(char ch, double d, double d2) throws Exception {
        switch (ch) {
            case '*':
                return d2 * d;
            case '+':
                return d2 + d;
            case ',':
            case '.':
            default:
                throw new Exception("Unsupported operator: " + ch);
            case '-':
                return d2 - d;
            case '/':
                if (d == 0.0) {
                    throw new Exception("Cannot divide by zero");
                } else {
                    return d2 / d;
                }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.text = null;
        this.process();
    }

    private void process() {
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
            this.scheduledFuture = null;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                if (this.text != null
                    && this.stopMovementInSpawnSetting.isEnabled()
                    && mc.thePlayer != null
                    && mc.theWorld != null
                    && FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                    this.process3();
                }
            } else {
                if (event instanceof GetPacketEvent getPacketEvent && getPacketEvent.getPacket() instanceof S02PacketChat) {
                    S02PacketChat packet = getPacketEvent.getPacket();
                    String text = packet.getChatComponent().getUnformattedText();
                    String str = "QUICK MATHS! Solve: ";
                    if (text.startsWith(str)) {
                        String str2 = text.substring(str.length());
                        String str3 = str2.replace("x", "*");

                        try {
                            double d = this.computeDouble(str3);
                            long l = Math.round(d);
                            EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
                            EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                            EnumChatFormatting BOLD = EnumChatFormatting.BOLD;
                            mc.thePlayer
                                .addChatMessage(
                                    new ChatComponentText("" + EnumChatFormatting.LIGHT_PURPLE + BOLD + "QUICK MATHS! " + GRAY + "Result: " + YELLOW + l)
                                );
                            this.text = Long.toString(l);
                            if (this.autoSubmitSetting.isEnabled()) {
                                this.process();
                                String text2 = this.text;
                                this.scheduledFuture = scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                                    if (this.checkState44() && mc.thePlayer != null && text2.equals(this.text)) {
                                        String str4 = ServerUtils.checkState() ? "/ac " + text2 : text2;
                                        mc.thePlayer.sendChatMessage(str4);
                                        this.text = null;
                                        this.scheduledFuture = null;
                                    }
                                }), (long)this.delaySetting.computeDouble(), TimeUnit.MILLISECONDS);
                            }
                        } catch (Exception e) {
                            mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Error solving math problem: " + e.getMessage()));
                        }
                    }
                }
            }
        }
    }

    private int computeInt(char ch) {
        switch (ch) {
            case '*':
            case '/':
                return 2;
            case '+':
            case '-':
                return 1;
            case ',':
            case '.':
            default:
                return -1;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.text = null;
        this.process();
    }

    private double computeDouble(String str) throws Exception {
        str = str.replaceAll("\\s+", "");
        return this.computeDouble2(str);
    }

    private boolean matchesChar(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private double computeDouble2(String str) throws Exception {
        Stack<Double> stack = new Stack<>();
        Stack<Character> stack2 = new Stack<>();

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isDigit(ch) || ch == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < str.length() && (Character.isDigit(str.charAt(i)) || str.charAt(i) == '.')) {
                    sb.append(str.charAt(i++));
                }

                stack.push(Double.parseDouble(sb.toString()));
                i--;
            } else if (ch == '(') {
                stack2.push(ch);
            } else if (ch == ')') {
                while (stack2.peek() != '(') {
                    stack.push(this.computeDouble((Character)stack2.pop(), (Double)stack.pop(), (Double)stack.pop()));
                }

                stack2.pop();
            } else if (this.matchesChar(ch)) {
                while (!stack2.isEmpty() && this.computeInt(ch) <= this.computeInt((Character)stack2.peek())) {
                    stack.push(this.computeDouble((Character)stack2.pop(), (Double)stack.pop(), (Double)stack.pop()));
                }

                stack2.push(ch);
            }
        }

        while (!stack2.isEmpty()) {
            stack.push(this.computeDouble((Character)stack2.pop(), (Double)stack.pop(), (Double)stack.pop()));
        }

        return (Double)stack.pop();
    }

    public AutoMaths() {
        super("Auto Maths", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.autoSubmitSetting, this.stopMovementInSpawnSetting});
    }
}
