package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AntiLauncher extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean[] flagArray;
    private int count = -1;

    @Override
    public void onDisable() {
        super.onDisable();
        this.process3();
        this.process();
    }

    private void process() {
        if (mc.gameSettings != null && !this.checkState()) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPacketReceive) {
                if (mc.thePlayer != null && mc.gameSettings != null) {
                    if (((EventPacketReceive)event).getPacket() instanceof S1BPacketEntityAttach packet) {
                        if (packet.getEntityId() == mc.thePlayer.getEntityId() && packet.getVehicleEntityId() != -1) {
                            event.cancel();
                            this.flagArray = new boolean[]{true, false};
                            this.count = 0;
                        }
                    }
                }
            } else {
                if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                    if (this.flagArray == null || this.count < 0 || this.count >= this.flagArray.length) {
                        return;
                    }

                    if (mc.gameSettings == null) {
                        return;
                    }

                    boolean flag = this.flagArray[this.count++];
                    int keyCode = mc.gameSettings.keyBindSneak.getKeyCode();
                    KeyBinding.setKeyBindState(keyCode, flag);
                    if (flag) {
                        KeyBinding.onTick(keyCode);
                    }

                    if (this.count >= this.flagArray.length) {
                        this.process3();
                        if (!this.checkState()) {
                            KeyBinding.setKeyBindState(keyCode, false);
                        }
                    }
                }
            }
        }
    }

    private boolean checkState() {
        if (mc.gameSettings == null) {
            return false;
        }
        try {
            int keyCode = mc.gameSettings.keyBindSneak.getKeyCode();
            return keyCode < 0 ? Mouse.isButtonDown(keyCode + 100) : Keyboard.isKeyDown(keyCode);
        } catch (Exception e) {
            return false;
        }
    }

    public AntiLauncher() {
        super("AntiLauncher", 0, Category.PIT);
    }

    private void process3() {
        this.flagArray = null;
        this.count = -1;
    }
}
