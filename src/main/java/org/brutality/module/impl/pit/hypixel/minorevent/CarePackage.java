package org.brutality.module.impl.pit.hypixel.minorevent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
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
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.EntityHelper;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.render.GlStateGuard;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.brutality.utils.timer.TimerUtil;

public class CarePackage
extends Module
implements PlayerApi {
    public static CarePackage instance;
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 4.0, 1.0, 5.0, 0.5);
    private final NumberSetting rightCPSSetting = new NumberSetting("Right CPS", this, 10.0, 1.0, 20.0, 1.0);
    private final NumberSetting leftCPSSetting = new NumberSetting("Left CPS", this, 10.0, 1.0, 20.0, 1.0);
    private final NumberSetting lootDelaySetting = new NumberSetting("Loot Delay", this, 100.0, 0.0, 500.0, 1.0);
    private final NumberSetting startDelayTicksSetting = new NumberSetting("Start Delay Ticks", this, 0.0, 0.0, 20.0, 1.0);
    private final TimerUtil timerUtil = new TimerUtil();
    private int count;
    private int count2;
    private int count3 = -1;
    private long timestamp;
    private final BooleanSetting renderSetting = new BooleanSetting("Render", this, true);
    private final BooleanSetting swingSetting = new BooleanSetting("Swing", this, true);
    private final BooleanSetting trapChestsSetting = new BooleanSetting("Trap Chests", this, false);
    private final BooleanSetting enderChestsSetting = new BooleanSetting("Ender Chests", this, false);
    private final BooleanSetting ignoreSwordsSetting = new BooleanSetting("Ignore Swords", this, true);
    private final BooleanSetting carePackageESPSetting = new BooleanSetting("Care Package ESP", this, true);
    private final BooleanSetting carePackageTracerSetting = new BooleanSetting("Care Package Tracer", this, false);
    private final NumberSetting espRangeSetting = new NumberSetting("ESP Range", this, 128.0, 16.0, 256.0, 8.0);
    private final ColorSetting espColorSetting = new ColorSetting("ESP Color", this, 0.1f, 0.8f, 1.0f, 0.45f);
    private final Set<BlockPos> itemSet = new HashSet<BlockPos>();
    private long timestamp2;
    private BlockPos blockPos = null;
    private boolean flag;
    private float[] floatValueArray = null;
    private final Set<BlockPos> itemSet2 = new HashSet<BlockPos>();
    private BlockPos blockPos2 = null;
    private boolean flag2 = false;
    private final TimerUtil timerUtil2 = new TimerUtil();
    private final TimerUtil timerUtil3 = new TimerUtil();
    private long timestamp3 = 0L;
    private BlockPos blockPos3 = null;
    private static final long timestamp4;

    public float[] getFloatValueArray() {
        return this.floatValueArray;
    }

    private void handleBlockPos(BlockPos blockPos) {
        WorldQueryCache.matchesModule(this, blockPos, this.swingSetting.isEnabled(), true);
    }

    public boolean checkState4() {
        return this.checkState44() && FreshHelper.flag && CarePackage.mc.currentScreen instanceof GuiChest && this.count3 != -1 && (double)this.count >= this.startDelayTicksSetting.computeDouble();
    }

    @Override
    public void onEnable() {
        this.itemSet.clear();
        this.timestamp2 = 0L;
        this.flag = false;
        this.blockPos = null;
        this.floatValueArray = null;
        this.blockPos2 = null;
        this.flag2 = false;
        this.itemSet2.clear();
        this.blockPos3 = null;
        this.timestamp3 = 0L;
        this.count2 = 0;
        this.count = 0;
        this.count3 = -1;
        this.timestamp = 0L;
        this.timerUtil.reset();
        this.timerUtil2.reset();
        this.timerUtil3.reset();
    }

    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    private void process() {
        if (CarePackage.mc.theWorld == null || CarePackage.mc.thePlayer == null || !this.carePackageESPSetting.isEnabled() && !this.carePackageTracerSetting.isEnabled()) {
            this.itemSet.clear();
            return;
        }
        long l = System.currentTimeMillis();
        if (l - this.timestamp2 < 250L) {
            return;
        }
        this.timestamp2 = l;
        this.itemSet.clear();
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (Entity entity : CarePackage.mc.theWorld.loadedEntityList) {
            String string;
            if (!(entity instanceof EntityArmorStand) || !entity.hasCustomName() || CarePackage.mc.thePlayer.getDistanceSqToEntity(entity) > this.espRangeSetting.computeDouble() * this.espRangeSetting.computeDouble() || !(string = StringUtils.stripControlCodes(entity.getCustomNameTag()).trim().toUpperCase(Locale.ROOT)).contains("RIGHT CLICK") && !string.contains("LEFT CLICK")) continue;
            arrayList.add(entity);
        }
        for (Entity entity : arrayList) {
            boolean bl = false;
            for (Entity entity2 : CarePackage.mc.theWorld.loadedEntityList) {
                String string;
                if (!(entity2 instanceof EntityArmorStand) || !entity2.hasCustomName() || entity2.posY <= entity.posY || entity2.posY - entity.posY > 2.0 || Math.hypot(entity2.posX - entity.posX, entity2.posZ - entity.posZ) > 0.8 || !(string = StringUtils.stripControlCodes(entity2.getCustomNameTag()).trim()).matches(".*[0-9].*") || !string.replace("[", "").replace("]", "").matches("[0-9,./ (): %-]+")) continue;
                bl = true;
                break;
            }
            if (!bl) continue;
            Object object = null;
            double d = Double.MAX_VALUE;
            BlockPos blockPos = new BlockPos(entity.posX, entity.posY, entity.posZ);
            for (int i = -2; i <= 2; ++i) {
                for (int j = -2; j <= 2; ++j) {
                    for (int k = -5; k <= 0; ++k) {
                        double d2;
                        Block block;
                        BlockPos blockPos2 = blockPos.add(i, k, j);
                        if (!CarePackage.mc.theWorld.isBlockLoaded(blockPos2) || (block = CarePackage.mc.theWorld.getBlockState(blockPos2).getBlock()) != Blocks.chest && block != Blocks.trapped_chest || !((d2 = entity.getDistanceSq((double)blockPos2.getX() + 0.5, blockPos2.getY() + 1, (double)blockPos2.getZ() + 0.5)) < d)) continue;
                        d = d2;
                        object = blockPos2;
                    }
                }
            }
            if (object == null) continue;
            this.itemSet.add((BlockPos)object);
        }
    }

    public CarePackage() {
        super("Care Package", 0, Category.PIT);
        this.handleSettingArray(this.rangeSetting, this.rightCPSSetting, this.leftCPSSetting, this.lootDelaySetting, this.startDelayTicksSetting, this.renderSetting, this.swingSetting, this.trapChestsSetting, this.enderChestsSetting, this.ignoreSwordsSetting, this.carePackageESPSetting, this.carePackageTracerSetting, this.espRangeSetting, this.espColorSetting);
        instance = this;
    }

    @Override
    public void onDisable() {
        this.itemSet.clear();
        this.process6();
        this.blockPos3 = null;
        this.blockPos = null;
        this.itemSet2.clear();
        this.blockPos2 = null;
        this.flag2 = false;
    }

    private boolean matchesBlockPos(BlockPos blockPos) {
        Block block = CarePackage.mc.theWorld.getBlockState(blockPos).getBlock();
        return (block == Blocks.chest || this.trapChestsSetting.isEnabled() && block == Blocks.trapped_chest || this.enderChestsSetting.isEnabled() && block == Blocks.ender_chest) && CarePackage.mc.thePlayer.getDistanceSq((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5) <= this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();
    }

    public boolean checkState2() {
        return this.blockPos != null && this.floatValueArray != null;
    }

    static {
        timestamp4 = 500L;
    }

    private void process8() {
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

    @Override
    public void process4() {
        this.process6();
        this.flag = false;
    }

    private float[] computeFloatArray(BlockPos blockPos) {
        double d = (double)blockPos.getX() + 0.5 - CarePackage.mc.thePlayer.posX;
        double d2 = (double)blockPos.getY() + 0.5 - (CarePackage.mc.thePlayer.posY + (double)CarePackage.mc.thePlayer.getEyeHeight());
        double d3 = (double)blockPos.getZ() + 0.5 - CarePackage.mc.thePlayer.posZ;
        double d4 = Math.sqrt(d * d + d3 * d3);
        float f = MathHelper.wrapAngleTo180_float((float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0f);
        float f2 = MathHelper.clamp_float((float)(-(Math.atan2(d2, d4) * 180.0 / Math.PI)), -90.0f, 90.0f);
        return new float[]{f, f2};
    }

    private BlockPos findNearestChest() {
        long l = System.currentTimeMillis();
        if (l - this.timestamp3 < 500L && (this.blockPos3 == null || !this.itemSet2.contains(this.blockPos3) && this.matchesBlockPos(this.blockPos3))) {
            return this.blockPos3;
        }
        this.timestamp3 = l;
        ArrayList<BlockPos> arrayList = new ArrayList<BlockPos>();
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos blockPos2 = CarePackage.mc.thePlayer.getPosition();
        for (int i = (n ^ 0xFFFFFFFF) + 1; i <= n; ++i) {
            for (int j = -n; j <= n; ++j) {
                for (int k = (n ^ 0xFFFFFFFF) + 1; k <= n; ++k) {
                    double d;
                    boolean bl;
                    BlockPos blockPos3 = blockPos2.add(i, j, k);
                    Block block = CarePackage.mc.theWorld.getBlockState(blockPos3).getBlock();
                    boolean bl2 = bl = block == Blocks.chest || this.trapChestsSetting.isEnabled() && block == Blocks.trapped_chest || this.enderChestsSetting.isEnabled() && block == Blocks.ender_chest;
                    if (!bl || this.itemSet2.contains(blockPos3) || !((d = CarePackage.mc.thePlayer.getDistance((double)blockPos3.getX() + 0.5, (double)blockPos3.getY() + 0.5, (double)blockPos3.getZ() + 0.5)) <= this.rangeSetting.computeDouble())) continue;
                    arrayList.add(blockPos3);
                }
            }
        }
        if (arrayList.isEmpty()) {
            this.blockPos3 = null;
            return null;
        }
        arrayList.sort(Comparator.comparingDouble(blockPos -> CarePackage.mc.thePlayer.getDistance((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5)));
        this.blockPos3 = (BlockPos)arrayList.get(0);
        return this.blockPos3;
    }

    private void process3() {
        try (GlStateGuard renderHelper7 = new GlStateGuard();){
            for (BlockPos object2 : this.itemSet) {
                if (CarePackage.mc.theWorld.getBlockState(object2).getBlock() != Blocks.chest && CarePackage.mc.theWorld.getBlockState(object2).getBlock() != Blocks.trapped_chest) continue;
                if (this.carePackageESPSetting.isEnabled()) {
                    ResetColorHelper.handleBlockPos2(object2, this.espColorSetting.computeColor(), true, true, 1.0f);
                }
                if (!this.carePackageTracerSetting.isEnabled()) continue;
                ResetColorHelper.handleBlockPos(object2, this.espColorSetting.computeColor().getRGB());
            }
        }
        if (!this.renderSetting.isEnabled() || this.blockPos == null) {
            return;
        }
        double d = CarePackage.mc.thePlayer.getDistance((double)this.blockPos.getX() + 0.5, (double)this.blockPos.getY() + 0.5, (double)this.blockPos.getZ() + 0.5);
        Color color = d <= this.rangeSetting.computeDouble() * 0.6 ? new Color(0, 200, 255, 110) : new Color(255, 165, 0, 110);
        ResetColorHelper.handleBlockPos2(this.blockPos, color, true, true, 1.0f);
    }

    private void handleBlockPos2(BlockPos blockPos) {
        WorldQueryCache.matchesModule(this, blockPos, this.swingSetting.isEnabled());
    }

    @Override
    public void onUpdate() {
        if (!GetCurrentScreenHelper.checkState2() || !FreshHelper.flag || CarePackage.mc.thePlayer.isDead || FreshHelper.matchesWorld3(CarePackage.mc.theWorld, CarePackage.mc.thePlayer)) {
            this.process6();
            this.blockPos = null;
            return;
        }
        boolean bl = CarePackage.mc.currentScreen instanceof GuiChest;
        if (this.flag2 && !bl && this.blockPos2 != null) {
            this.itemSet2.add(this.blockPos2);
            TileEntity tileEntity = CarePackage.mc.theWorld.getTileEntity(this.blockPos2);
            if (tileEntity instanceof TileEntityChest) {
                ((TileEntityChest)tileEntity).closeInventory(CarePackage.mc.thePlayer);
            }
            this.blockPos2 = null;
        }
        this.flag2 = bl;
        if (bl) {
            this.process7();
        } else {
            this.count = 0;
            this.count2 = 0;
            this.count3 = -1;
        }
        if (CarePackage.mc.currentScreen != null) {
            this.process6();
            return;
        }
        if (this.ignoreSwordsSetting.isEnabled() && CarePackage.mc.thePlayer.getHeldItem() != null && CarePackage.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            this.process6();
            return;
        }
        if (Brutality.moduleManager.computeModule("Scaffold").checkState44()) {
            this.process6();
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
        this.process8();
        this.process5();
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
                this.process();
                this.onUpdate();
            }
        } else if (event instanceof EventPreMotion) {
            if (((EventPreMotion)event).getState() == EventState.PRE) {
                this.onEventPreMotion((EventPreMotion)event);
            }
        } else if (event instanceof EventRender3D && (this.renderSetting.isEnabled() || this.carePackageESPSetting.isEnabled() || this.carePackageTracerSetting.isEnabled())) {
            this.process3();
        }
    }

    private void onEventPreMotion(EventPreMotion eventPreMotion) {
        if (!WorldQueryCache.matchesModule2(this)) {
            return;
        }
        if (this.floatValueArray == null || !OnJumpHelper.checkState()) {
            return;
        }
        CarePackage.mc.thePlayer.rotationYawHead = OnJumpHelper.floatValueArray2[0];
        CarePackage.mc.thePlayer.rotationPitchHead = OnJumpHelper.floatValueArray2[1];
        CarePackage.mc.thePlayer.renderYawOffset = OnJumpHelper.floatValueArray2[0];
        eventPreMotion.setYaw(OnJumpHelper.floatValueArray2[0]);
        eventPreMotion.setPitch(OnJumpHelper.floatValueArray2[1]);
    }

    private void process5() {
        long l = (long)(1000.0 / this.rightCPSSetting.computeDouble());
        long l2 = (long)(1000.0 / this.leftCPSSetting.computeDouble());
        if (this.timerUtil2.matchesLong(l, true)) {
            this.blockPos2 = this.blockPos;
            this.timestamp = System.currentTimeMillis();
            this.handleBlockPos(this.blockPos);
        }
        if (!(CarePackage.mc.currentScreen instanceof GuiChest) && this.timerUtil3.matchesLong(l2, true)) {
            this.handleBlockPos2(this.blockPos);
        }
    }

    @Override
    public void process2() {
        this.onDisable();
        this.onEnable();
    }

    public static int computeInt(Item item) {
        if (item == Items.golden_sword) {
            return 0;
        }
        if (item == Items.bow) {
            return 1;
        }
        if (item == Items.leather_leggings) {
            return 2;
        }
        if (item == Items.diamond_helmet) {
            return 3;
        }
        if (item == Items.diamond_chestplate || item == Items.diamond_leggings || item == Items.diamond_boots) {
            return 4;
        }
        if (item == Items.diamond_sword) {
            return 5;
        }
        if (item == Items.gold_ingot) {
            return 6;
        }
        if (item == Items.experience_bottle) {
            return 7;
        }
        return Integer.MAX_VALUE;
    }

    private void process7() {
        if (!(CarePackage.mc.thePlayer.openContainer instanceof ContainerChest)) {
            return;
        }
        ContainerChest containerChest = (ContainerChest)CarePackage.mc.thePlayer.openContainer;
        IInventory iInventory = containerChest.getLowerChestInventory();
        String string = iInventory.getDisplayName().getUnformattedText();
        if (!(string.equals("Chest") || string.equals("Double Chest") || string.toLowerCase(Locale.ROOT).contains("care") || System.currentTimeMillis() - this.timestamp < 1500L)) {
            return;
        }
        if (this.count3 != containerChest.windowId) {
            this.count3 = containerChest.windowId;
            this.count2 = 0;
            this.count = 0;
        }
        if ((double)(++this.count) < this.startDelayTicksSetting.computeDouble() || EntityHelper.isFlag() || !this.timerUtil.matchesLong8((int)this.lootDelaySetting.computeDouble())) {
            return;
        }
        int n = -1;
        int n2 = Integer.MAX_VALUE;
        for (int i = 0; i < iInventory.getSizeInventory(); ++i) {
            int n3;
            ItemStack itemStack = iInventory.getStackInSlot(i);
            if (itemStack == null || (n3 = CarePackage.computeInt(itemStack.getItem())) >= n2) continue;
            n2 = n3;
            n = i;
        }
        if (n >= 0) {
            CarePackage.mc.playerController.windowClick(containerChest.windowId, n, 0, n2 < 6 ? 1 : 0, CarePackage.mc.thePlayer);
            this.timerUtil.reset();
            this.count2 = 0;
        } else if (++this.count2 > 3) {
            CarePackage.mc.thePlayer.closeScreen();
        }
    }

    public boolean checkState() {
        return this.flag && this.blockPos != null || this.blockPos2 != null && CarePackage.mc.currentScreen instanceof GuiChest;
    }
}
