package org.brutality.module.impl.player;

import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.player.PlayerEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.lwjgl.input.Mouse;

public class AutoTool extends Module {
    public static AutoTool instance;
    private final NumberSetting activationTimeSetting = new NumberSetting("Activation Time", this, 0.0, 0.0, 1000.0, 25.0);
    private final NumberSetting hoverDelaySetting = new NumberSetting("Hover Delay", this, 0.0, 0.0, 1000.0, 25.0);
    private final BooleanSetting onlyWhileCrouchingSetting = new BooleanSetting("Only While Crouching", this, false);
    private final BooleanSetting requireLeftMouseSetting = new BooleanSetting("Require Left Mouse", this, true);
    private final BooleanSetting switchBackWhenDoneSetting = new BooleanSetting("Switch Back When Done", this, true);
    private final BooleanSetting overrideSwapBackSetting = new BooleanSetting("Override Swap Back", this, true);
    private final BooleanSetting ignoreWhileUsingItemSetting = new BooleanSetting("Ignore While Using Item", this, true);
    private int count = -1;
    private boolean flag;
    private int tickCounter;
    private int count2 = -1;
    private int count3 = -1;
    private BlockPos blockPos;

    private void handleInt(int n) {
        if (mc.thePlayer != null && n != -1 && n != mc.thePlayer.inventory.currentItem) {
            mc.thePlayer.inventory.currentItem = n;
            this.flag = true;
            mc.playerController.updateController();
        }
    }

    @Generated
    public static AutoTool getInstance() {
        return instance;
    }

    private void process4() {
        if (mc.thePlayer == null || mc.theWorld == null) {
            this.handleFlag(true);
        } else if (mc.currentScreen == null && !mc.thePlayer.isDead && mc.thePlayer.capabilities.allowEdit) {
            int n = ++this.tickCounter;
            boolean buttonDown = Mouse.isButtonDown(0);
            this.handleFlag(buttonDown, n);
            BlockPos pos = null;
            if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                pos = mc.objectMouseOver.getBlockPos();
            }

            this.handleBlockPos(pos, n);
            if (pos == null) {
                this.process();
            } else if (this.onlyWhileCrouchingSetting.isEnabled() && !mc.thePlayer.isSneaking()) {
                this.process();
            } else if (!this.requireLeftMouseSetting.isEnabled() || buttonDown && this.matchesInt(this.count2, this.activationTimeSetting.computeDouble(), n)) {
                if (!this.matchesInt(this.count3, this.hoverDelaySetting.computeDouble(), n)) {
                    this.process();
                } else if (!this.ignoreWhileUsingItemSetting.isEnabled() || !mc.thePlayer.isUsingItem() && !Mouse.isButtonDown(1)) {
                    Block block = mc.theWorld.getBlockState(pos).getBlock();
                    int n2 = GetCurrentScreenHelper.computeInt(block);
                    if (n2 == -1) {
                        this.process();
                    } else {
                        if (this.count == -1 && n2 != mc.thePlayer.inventory.currentItem) {
                            this.count = mc.thePlayer.inventory.currentItem;
                        }

                        if (n2 != mc.thePlayer.inventory.currentItem) {
                            this.handleInt(n2);
                        }
                    }
                } else {
                    this.process();
                }
            } else {
                this.process();
            }
        } else {
            this.handleFlag(true);
        }
    }

    public void process3() {
        this.count = -1;
        this.flag = false;
    }

    private void handleFlag(boolean flag, int n) {
        if (flag) {
            if (this.count2 == -1) {
                this.count2 = n;
            }
        } else {
            this.count2 = -1;
        }
    }

    private void handleFlag(boolean flag) {
        if (flag) {
            this.tickCounter = 0;
            this.count2 = -1;
            this.count3 = -1;
            this.blockPos = null;
        }

        this.process();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (!Autoblockin.active) {
                if (event instanceof PlayerEvent4) {
                    this.onPlayerEvent4((PlayerEvent4)event);
                } else if (event instanceof EventUpdate event2) {
                    if (event2.getState() == EventState.PRE) {
                        this.process4();
                    }
                }
            }
        }
    }

    public AutoTool() {
        super("AutoTool", 0, Category.PLAYER);
        this.handleSettingArray(
            new Setting[]{
                this.activationTimeSetting,
                this.hoverDelaySetting,
                this.onlyWhileCrouchingSetting,
                this.requireLeftMouseSetting,
                this.switchBackWhenDoneSetting,
                this.overrideSwapBackSetting,
                this.ignoreWhileUsingItemSetting
            }
        );
        instance = this;
    }

    @Override
    public void onEnable() {
        this.handleFlag(true);
    }

    private void handleBlockPos(BlockPos blockPos, int n) {
        if (blockPos == null) {
            this.count3 = -1;
            this.blockPos = null;
        } else {
            if (this.blockPos == null || !this.blockPos.equals(blockPos)) {
                this.count3 = n;
                this.blockPos = blockPos;
            }
        }
    }

    @Override
    public void onDisable() {
        this.handleFlag(true);
    }

    private boolean matchesInt(int n, double d, int n2) {
        int n3 = this.computeInt(d);
        return n3 <= 0 ? true : n != -1 && n2 - n >= n3;
    }

    private void process() {
        if (this.count != -1 && this.switchBackWhenDoneSetting.isEnabled() && mc.thePlayer != null) {
            this.handleInt(this.count);
        }

        this.count = -1;
        this.flag = false;
    }

    private int computeInt(double d) {
        return d <= 0.0 ? 0 : (int)Math.ceil(d / 50.0);
    }

    public static int computeInt() {
        return instance == null ? -1 : instance.count;
    }

    private void onPlayerEvent4(PlayerEvent4 playerEvent4) {
        if (this.flag && mc.thePlayer != null) {
            if (this.overrideSwapBackSetting.isEnabled()) {
                this.count = playerEvent4.computeInt(mc.thePlayer.inventory.currentItem);
            }

            playerEvent4.cancel();
        }
    }
}
