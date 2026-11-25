package org.brutality.module.impl.movement;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.movement.MovementEvent3;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.network.NetworkHelper;

public class Stasis extends Module {
    public static Stasis instance;
    public static NumberSetting numberSetting;
    public static boolean flag;
    private static double amount;
    private static double amount2;
    private static double amount3;
    private static int count;
    private static int count2;

    @Override
    public void onDisable() {
        handleFlag2(false);
        count2 = 0;
    }

    public Stasis() {
        super("Stasis", 0, Category.MOVEMENT);
        instance = this;
        this.process3();
        this.handleSettingArray(new Setting[]{numberSetting});
    }

    @Override
    public void process2() {
        flag = false;
        count2 = 2;
        amount3 = 0.0;
        amount2 = 0.0;
        amount = 0.0;
    }

    public static void process6() {
        handleFlag2(false);
        count2 = 3;
    }

    public static void handleFlag2(boolean flag2) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && flag2 != flag) {
            if (flag2) {
                amount = mc.thePlayer.motionX;
                amount2 = mc.thePlayer.motionY;
                amount3 = mc.thePlayer.motionZ;
                count = mc.thePlayer.ticksExisted;
            } else {
                mc.thePlayer.motionX = amount;
                mc.thePlayer.motionY = amount2;
                mc.thePlayer.motionZ = amount3;
            }

            flag = flag2;
        }
    }

    @Override
    public void onEvent(Event event) {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
        if (this.checkState44() && true && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
            if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                this.process();
            } else if (event instanceof MovementEvent3) {
                this.process4();
            }
        }
    }

    public static void handleFlag(boolean flag) {
        if (instance != null && instance.checkState44() && computeMinecraft().thePlayer != null) {
            if (flag) {
                process6();
            } else {
                instance.process5();
            }
        }
    }

    public void process4() {
        Minecraft mc = Minecraft.getMinecraft();
        if (flag || count2 == 1) {
            mc.thePlayer.movementInput.moveForward = 0.0F;
            mc.thePlayer.movementInput.moveStrafe = 0.0F;
        }
    }

    @Override
    public void onEnable() {
        count2 = 2;
    }

    public boolean matchesPacket(Packet<?> packet) {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            return true;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (!flag || mc.thePlayer == null || mc.theWorld == null) {
            return true;
        }
        if (!(packet instanceof C03PacketPlayer packet2)) {
            return true;
        }
        if (this.checkState() && !(packet2 instanceof C03PacketPlayer.C05PacketPlayerLook)) {
            NetworkHelper.handlePacket(
                new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround)
            );
            return false;
        }
        return true;
    }

    public void process5() {
        if (flag) {
            handleFlag2(false);
            count2 = 4;
        }
    }

    private boolean checkState() {
        if (computeMinecraft().getCurrentServerData() == null) {
            return false;
        }
        String str = computeMinecraft().getCurrentServerData().serverIP.toLowerCase(Locale.ROOT).split(":")[0];
        return str.equals("hypixel.net") || str.endsWith(".hypixel.net");
    }

    public void process3() {
        numberSetting = new NumberSetting("Pulse", this, 65.0, 2.0, 85.0, 1.0);
    }

    private static Minecraft computeMinecraft() {
        return Minecraft.getMinecraft();
    }

    public void process() {
        Minecraft mc = Minecraft.getMinecraft();
        if (count2 > 0 && --count2 == 0) {
            handleFlag2(true);
        }

        if (flag) {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionY = 0.0;
            mc.thePlayer.motionZ = 0.0;
            if (!(numberSetting.computeDouble() <= 2.0) && mc.thePlayer.ticksExisted - count >= (int)numberSetting.computeDouble()) {
                handleFlag2(false);
                count2 = 1;
            }
        }
    }
}
