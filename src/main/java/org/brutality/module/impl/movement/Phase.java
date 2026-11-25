package org.brutality.module.impl.movement;

import java.util.Arrays;
import lombok.Generated;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.modules.CountMovementHelper;
import org.brutality.utils.modules.NoneType;

public class Phase extends Module {
    private final ModeSetting vanillaSetting = new ModeSetting("Mode", this, "Vanilla", Arrays.asList("Vanilla"));
    public static Phase instance;
    private boolean flag = false;
    private boolean flag2 = false;
    private boolean flag3 = false;

    private void process5() {
        ScaledResolution sr = new ScaledResolution(mc);
        String str = "Blinking: " + CountMovementHelper.getInstance().computeInt();
        int n = sr.getScaledWidth() / 4 - computeFontRenderer(Phase.class).getStringWidth(str) / 2;
        int n2 = sr.getScaledHeight() / 4;
        computeFontRenderer(Phase.class).drawStringWithShadow(str, n, n2, -1);
    }

    @Generated
    public static Phase getInstance() {
        return instance;
    }

    private void process() {
        if (this.flag) {
            BlockPos pos = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ);
            boolean flag = mc.theWorld.getBlockState(pos).getBlock() == Blocks.air;
            BlockPos pos2 = new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - (flag ? 2 : 1), mc.thePlayer.posZ);
            mc.theWorld.setBlockState(pos2, Blocks.air.getDefaultState(), 11);
            this.flag = false;
            this.flag2 = true;
            if (!CountMovementHelper.getInstance().isFlag()) {
                CountMovementHelper.getInstance().matchesFlag(true, NoneType.NO_SLOW);
            }
        }

        if (this.flag3 && CountMovementHelper.getInstance().isFlag()) {
            this.flag2 = false;
            CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
            this.flag3 = false;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof EventPreMotion) {
                    if (this.vanillaSetting.matchesString2("Vanilla")) {
                        this.process();
                    }
                } else if (event instanceof EventRender2D && this.flag2) {
                    this.process5();
                }
            }
        }
    }

    public void process3() {
        if (this.checkState44() && !this.flag && !this.flag2) {
            this.flag = true;
        }
    }

    public void process4() {
        if (this.checkState44() && this.flag2) {
            this.flag3 = true;
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.flag = false;
            this.flag2 = false;
            this.flag3 = false;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (CountMovementHelper.getInstance().isFlag()) {
            CountMovementHelper.getInstance().matchesFlag(false, NoneType.NO_SLOW);
        }

        this.flag = false;
        this.flag2 = false;
        this.flag3 = false;
    }

    public Phase() {
        super("Phase", 0, Category.MOVEMENT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.vanillaSetting});
        instance = this;
    }
}
