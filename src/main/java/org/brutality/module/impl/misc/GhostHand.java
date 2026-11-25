package org.brutality.module.impl.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.misc.ColorMathUtil;
import org.lwjgl.input.Mouse;

public class GhostHand extends Module {
    public static GhostHand instance;
    public static BooleanSetting booleanSetting;
    public static BooleanSetting booleanSetting2;
    public static BooleanSetting booleanSetting3;
    public static BooleanSetting booleanSetting4;
    public static BooleanSetting booleanSetting5;
    public static BooleanSetting booleanSetting6;

    @Override
    public void onEvent(Event event) {
    }

    public static GhostHand getInstance() {
        return instance;
    }

    private static Minecraft computeMinecraft() {
        return Minecraft.getMinecraft();
    }

    public void process() {
        booleanSetting = new BooleanSetting("Allow mining", this, true);
        booleanSetting2 = new BooleanSetting("Allow placing", this, true);
        booleanSetting3 = new BooleanSetting("Disable with weapon", this, false);
        booleanSetting4 = new BooleanSetting("Disable on bots", this, true);
        booleanSetting5 = new BooleanSetting("Only while holding tools", this, true);
        booleanSetting6 = new BooleanSetting("Only while sneaking", this, false);
    }

    public boolean checkState() {
        // VENDOR-DISABLED: if (this.checkState44() && RuntimeAuthGuard.checkState2() && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
        if (this.checkState44() && true && computeMinecraft().thePlayer != null && computeMinecraft().theWorld != null) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.objectMouseOver == null || mc.objectMouseOver.typeOfHit != MovingObjectType.ENTITY) {
                return false;
            }
            if (!booleanSetting.isEnabled() && Mouse.isButtonDown(0)) {
                return false;
            }
            if (!booleanSetting2.isEnabled() && Mouse.isButtonDown(1)) {
                return false;
            }
            if (booleanSetting3.isEnabled() && ColorMathUtil.Entry9.checkState()) {
                return false;
            }
            if (booleanSetting5.isEnabled() && !ColorMathUtil.Entry9.checkState3()) {
                return false;
            }
            if (booleanSetting6.isEnabled() && !mc.thePlayer.isSneaking()) {
                return false;
            }
            if (booleanSetting4.isEnabled() && ColorMathUtil.Entry9.matchesEntity(mc.objectMouseOver.entityHit)) {
                return false;
            }
            MovingObjectPosition hit = ColorMathUtil.Entry9.computeMovingObjectPosition(mc.playerController.getBlockReachDistance());
            if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
                mc.objectMouseOver = hit;
                return true;
            }
            return false;
        }
        return false;
    }

    public GhostHand() {
        super("Ghost Hand", 0, Category.MISCELLANEOUS);
        instance = this;
        this.process();
        this.handleSettingArray(new Setting[]{booleanSetting, booleanSetting2, booleanSetting3, booleanSetting4, booleanSetting5, booleanSetting6});
    }
}
