package org.brutality.module.impl.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.pit.hypixel.minorevent.CarePackage;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public class ChestAura
extends Module
implements PlayerApi {
    public static ChestAura instance;
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 4.0, 1.0, 5.0, 0.5);
    private final BooleanSetting renderSetting = new BooleanSetting("Render", this, true);
    private final BooleanSetting swingSetting = new BooleanSetting("Swing", this, true);
    private final BooleanSetting trapChestsSetting = new BooleanSetting("Trap Chests", this, false);
    private final BooleanSetting enderChestsSetting = new BooleanSetting("Ender Chests", this, false);
    private final BooleanSetting ignoreSwordsSetting = new BooleanSetting("Ignore Swords", this, true);
    private BlockPos blockPos = null;
    private boolean flag;
    private float[] floatValueArray = null;
    private final Set<BlockPos> itemSet = new HashSet<BlockPos>();
    private BlockPos blockPos2 = null;
    private boolean flag2 = false;
    private long timestamp = 0L;
    private BlockPos blockPos3 = null;
    private static final long timestamp2;

    private float[] computeFloatArray(BlockPos blockPos) {
        double d = (double)blockPos.getX() + 0.5 - ChestAura.mc.thePlayer.posX;
        double d2 = (double)blockPos.getY() + 0.5 - (ChestAura.mc.thePlayer.posY + (double)ChestAura.mc.thePlayer.getEyeHeight());
        double d3 = (double)blockPos.getZ() + 0.5 - ChestAura.mc.thePlayer.posZ;
        double d4 = Math.sqrt(d * d + d3 * d3);
        float f = MathHelper.wrapAngleTo180_float((float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0f);
        float f2 = MathHelper.clamp_float((float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI)), -90.0f, 90.0f);
        return new float[]{f, f2};
    }

    @Override
    public void onEvent(Event event) {
        if (!this.checkState44()) {
            return;
        }
        if (event instanceof RenderEvent4) {
            this.onEnable();
            return;
        }
        if (event instanceof EventUpdate) {
            if (((EventUpdate)event).getState() == EventState.PRE) {
                this.onUpdate();
            }
        } else if (event instanceof EventPreMotion) {
            if (((EventPreMotion)event).getState() == EventState.PRE) {
                this.onEventPreMotion((EventPreMotion)event);
            }
        } else if (event instanceof EventRender3D && this.renderSetting.isEnabled()) {
            this.process3();
        }
    }

    public boolean checkState2() {
        return this.blockPos != null && this.floatValueArray != null;
    }

    public ChestAura() {
        super("ChestAura", 0, Category.PLAYER);
        this.handleSettingArray(this.rangeSetting, this.renderSetting, this.swingSetting, this.trapChestsSetting, this.enderChestsSetting, this.ignoreSwordsSetting);
        instance = this;
    }

    private void onEventPreMotion(EventPreMotion eventPreMotion) {
        if (!WorldQueryCache.matchesModule2(this)) {
            return;
        }
        if (this.floatValueArray == null || !OnJumpHelper.checkState()) {
            return;
        }
        ChestAura.mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
        ChestAura.mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
        ChestAura.mc.thePlayer.renderYawOffset = OnJumpHelper.floatValueArray2[0];
        eventPreMotion.setYaw(OnJumpHelper.floatValueArray2[0]);
        eventPreMotion.setPitch(OnJumpHelper.floatValueArray2[1]);
    }

    private void process6() {
        this.flag = false;
        if (this.floatValueArray == null) {
            return;
        }
        float[] fArray = this.floatValueArray;
        this.floatValueArray = null;
        if (fArray == null || OnJumpHelper.floatValueArray2 != fArray) {
            return;
        }
        OnJumpHelper.disable();
        if (MovementFix.isEnabled()) {
            MovementFix.process3();
        }
    }

    private void process5() {
        if (this.itemSet.contains(this.blockPos)) {
            return;
        }
        this.handleBlockPos(this.blockPos);
        this.blockPos2 = this.blockPos;
    }

    public float[] getFloatValueArray() {
        return this.floatValueArray;
    }

    private void process() {
        OnJumpHelper.setReach((float)this.rangeSetting.computeDouble());
        float[] fArray = this.computeFloatArray(this.blockPos);
        if (fArray != null) {
            this.floatValueArray = fArray;
            OnJumpHelper.handleFloat(fArray[0], fArray[1], 2);
            OnJumpHelper.floatValueArray2 = fArray;
            if (MovementFix.isEnabled()) {
                MovementFix.handleFloat(fArray[0], fArray[1]);
            }
        }
    }

    @Override
    public void process2() {
        this.onDisable();
        this.onEnable();
    }

    @Override
    public void onEnable() {
        this.flag = false;
        this.blockPos = null;
        this.floatValueArray = null;
        this.blockPos2 = null;
        this.flag2 = false;
        this.itemSet.clear();
        this.blockPos3 = null;
        this.timestamp = 0L;
    }

    @Override
    public void process4() {
        this.process6();
        this.flag = false;
    }

    @Override
    public void onDisable() {
        this.process6();
        this.blockPos3 = null;
        this.blockPos = null;
        this.itemSet.clear();
        this.blockPos2 = null;
        this.flag2 = false;
    }

    @Override
    public void onUpdate() {
        if (!GetCurrentScreenHelper.checkState2()) {
            this.process28();
            return;
        }
        boolean bl = ChestAura.mc.currentScreen instanceof GuiChest;
        if (this.flag2 && !bl && this.blockPos2 != null) {
            this.itemSet.add(this.blockPos2);
            TileEntity tileEntity = ChestAura.mc.theWorld.getTileEntity(this.blockPos2);
            if (tileEntity instanceof TileEntityChest) {
                ((TileEntityChest)tileEntity).closeInventory(ChestAura.mc.thePlayer);
            }
            this.blockPos2 = null;
        }
        this.flag2 = bl;
        if (ChestAura.mc.currentScreen != null) {
            this.process6();
            return;
        }
        if (this.ignoreSwordsSetting.isEnabled() && ChestAura.mc.thePlayer.getHeldItem() != null && ChestAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            this.process6();
            return;
        }
        if (Brutality.moduleManager.computeModule("Scaffold").checkState44()) {
            this.process6();
            return;
        }
        if (CarePackage.instance != null && CarePackage.instance.checkState44() && FreshHelper.flag) {
            this.process6();
            this.blockPos = null;
            return;
        }
        this.blockPos = this.findNearestChest();
        if (this.blockPos == null) {
            this.process6();
            return;
        }
        this.flag = true;
        if (!WorldQueryCache.matchesModule(this)) {
            this.process4();
            return;
        }
        KillAura.process33();
        this.process();
        this.process5();
    }

    private boolean matchesBlockPos(BlockPos blockPos) {
        Block block = ChestAura.mc.theWorld.getBlockState(blockPos).getBlock();
        return (block == Blocks.chest || this.trapChestsSetting.isEnabled() && block == Blocks.trapped_chest || this.enderChestsSetting.isEnabled() && block == Blocks.ender_chest) && ChestAura.mc.thePlayer.getDistanceSq((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();
    }

    private void handleBlockPos(BlockPos blockPos) {
        WorldQueryCache.matchesModule(this, blockPos, this.swingSetting.isEnabled(), true);
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    static {
        timestamp2 = 500L;
    }

    private void process3() {
        if (this.blockPos == null) {
            return;
        }
        double d = ChestAura.mc.thePlayer.getDistance((double)this.blockPos.getX() + 0.5, (double)this.blockPos.getY() + 0.5, (double)this.blockPos.getZ() + 0.5);
        Color color = d <= this.rangeSetting.computeDouble() * 0.6 ? new Color(0, 200, 255, 110) : new Color(255, 165, 0, 110);
        ResetColorHelper.handleBlockPos2(this.blockPos, color, true, true, 1.0f);
    }

    private BlockPos findNearestChest() {
        long l = System.currentTimeMillis();
        if (l - this.timestamp < 500L && (this.blockPos3 == null || !this.itemSet.contains(this.blockPos3) && this.matchesBlockPos(this.blockPos3))) {
            return this.blockPos3;
        }
        this.timestamp = l;
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos2 = ChestAura.mc.thePlayer.getPosition();
        for (int i = -n; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = (n ^ 0xFFFFFFFF) + 1; k <= n; ++k) {
                    double d;
                    boolean bl;
                    BlockPos blockPos3 = blockPos2.add(i, j, k);
                    Block block = ChestAura.mc.theWorld.getBlockState(blockPos3).getBlock();
                    boolean bl2 = bl = block == Blocks.chest || this.trapChestsSetting.isEnabled() && block == Blocks.trapped_chest || this.enderChestsSetting.isEnabled() && block == Blocks.ender_chest;
                    if (!bl || this.itemSet.contains(blockPos3) || !((d = ChestAura.mc.thePlayer.getDistance((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.5, (double)blockPos3.getZ() + 0.5)) <= this.rangeSetting.computeDouble())) continue;
                    arrayList.add(blockPos3);
                }
            }
        }
        if (arrayList.isEmpty()) {
            this.blockPos3 = null;
            return null;
        }
        arrayList.sort(Comparator.comparingDouble(blockPos -> ChestAura.mc.thePlayer.getDistance((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5)));
        this.blockPos3 = (BlockPos)arrayList.get(0);
        return this.blockPos3;
    }

    public boolean checkState() {
        return this.flag && this.blockPos != null || this.blockPos2 != null && ChestAura.mc.currentScreen instanceof GuiChest;
    }
}
