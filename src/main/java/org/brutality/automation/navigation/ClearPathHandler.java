package org.brutality.automation.navigation;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.brutality.automation.hunting.HuntingHandler3;
import org.brutality.automation.mining.FenceHandler;
import org.brutality.automation.mining.MiningHandler;
import org.brutality.automation.mining.MiningHandler2;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.util.BEASTHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.util.StopwatchTimer;
import org.brutality.util.math.BuildTargetComparatorHelper;
import org.brutality.utils.render.GlStateGuard;

public class ClearPathHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final long timestamp = 150L;
    private static final long timestamp2 = 3000L;
    private static final double amount = 4.0;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final StopwatchTimer utilHelper82 = new StopwatchTimer();
    private final StopwatchTimer utilHelper83 = new StopwatchTimer();
    private final StopwatchTimer utilHelper84 = new StopwatchTimer();
    private final MiningHandler miningHandler = new MiningHandler();
    private final Random random = new Random();
    private final int count;
    private final int count2;
    private final int count3;
    private final boolean flag;
    private final double amount2;
    private HuntingHandler3.Entry2 entry2;
    private BlockPos blockPos;
    private WorldClient worldClient;
    private int count4 = Integer.MIN_VALUE;
    private List<BlockPos> entries;
    private BlockPos blockPos2;
    private BlockPos blockPos3;
    private int count5;
    private int count6;
    private int count7;
    private int count8;
    private int count9;
    private int count10;
    private boolean flag2 = true;
    private Vec3 vec3;
    private Entity trackedEntity;
    private boolean flag3;
    private boolean flag4;
    private final StopwatchTimer utilHelper85 = new StopwatchTimer();

    private double computeDouble(BlockPos pos, BlockPos pos2, double d, boolean flag, int n, int n2, double d2, double d3) {
        double d4 = pos.getX() + 0.5;
        double d5 = pos.getZ() + 0.5;
        double d6 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.55F;
        BlockPos pos3 = new BlockPos(d4, d6, d5);
        this.miningHandler.handleBlockPos(pos.down());
        if (!FenceHandler.matchesBlockPos7(pos)) {
            this.count7 = 0;
            this.count9 = 5;
            this.process4();
            return Double.POSITIVE_INFINITY;
        }
        this.vec3 = new Vec3(d4, d6, d5);
        if (BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, pos)) {
            this.count9 = 0;
        } else {
            this.count9++;
        }

        if (this.flag2) {
            BuildTargetComparatorHelper.handleBlockPos(pos3, n, n2);
        }

        double d7 = Math.hypot(d4 - mc.thePlayer.posX, d5 - mc.thePlayer.posZ);
        if (flag) {
            boolean flag2 = this.matchesBlockPos(pos2, pos, d7);
            boolean flag3 = pos.getY() < pos2.getY();
            this.handleDouble(d, d2, d3, flag2, flag3);
        } else {
            this.process4();
        }

        return d7;
    }

    public boolean checkState2() {
        return this.entries != null && !this.entries.isEmpty();
    }

    private void process4() {
        this.handleKeyBinding(mc.gameSettings.keyBindForward, false);
        this.handleKeyBinding(mc.gameSettings.keyBindBack, false);
        this.handleKeyBinding(mc.gameSettings.keyBindSprint, false);
        this.handleKeyBinding(mc.gameSettings.keyBindJump, false);
        this.handleKeyBinding(mc.gameSettings.keyBindLeft, false);
        this.handleKeyBinding(mc.gameSettings.keyBindRight, false);
        this.flag3 = false;
    }

    private boolean matchesBlockPos(BlockPos pos) {
        return BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, pos) && BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, pos.up());
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }

    public void setCount6(int n) {
        this.count6 = n;
    }

    public void handleBlockPos(BlockPos pos, boolean flag, int n, int n2, double d, double d2) {
        this.handleBlockPos(pos, flag, n, n2, d, d2, false);
    }

    private void process3() {
        this.count8 = this.count2 + this.random.nextInt(this.count3 - this.count2 + 1);
    }

    public void handleBlockPos(BlockPos pos, boolean flag, int n, int n2, double d) {
        this.handleBlockPos(pos, flag, n, n2, d, 0.0);
    }

    public BlockPos computeBlockPos() {
        return BEASTHelper.computeBlockPos(mc.thePlayer);
    }

    public void handleEntity(Entity entity, int n, int n2, double d) {
        this.handleEntity(entity, n, n2, d, 0.0);
    }

    private void handleDouble(double d) {
        if (d <= 0.0) {
            if (this.flag3) {
                this.flag3 = false;
                this.handleKeyBinding(mc.gameSettings.keyBindLeft, false);
                this.handleKeyBinding(mc.gameSettings.keyBindRight, false);
            }
        } else {
            if (this.utilHelper85.matchesLong(700L)) {
                this.utilHelper85.reset();
                if (Math.random() < d) {
                    this.flag3 = true;
                    this.flag4 = this.random.nextBoolean();
                } else {
                    this.flag3 = false;
                }
            }

            this.handleKeyBinding(mc.gameSettings.keyBindLeft, this.flag3 && !this.flag4);
            this.handleKeyBinding(mc.gameSettings.keyBindRight, this.flag3 && this.flag4);
        }
    }

    private void handleBlockPos(BlockPos pos, double d) {
        int count = this.count;
        if (!BuildTargetComparatorHelper.matchesEntityPlayer(mc.thePlayer, pos)) {
            count += 12;
        }

        if (d > 20.0) {
            count += 15;
        }

        if (d > 40.0) {
            count += 15;
        }

        if (d > 65.0) {
            count += 15;
        }

        if (d > 85.0) {
            count += 20;
        }

        this.count7 = count;
    }

    private boolean matchesBlockPos2(BlockPos pos) {
        boolean flag = FenceHandler.matchesBlockPos3(pos.east()) || FenceHandler.matchesBlockPos3(pos.east().up());
        boolean flag2 = FenceHandler.matchesBlockPos3(pos.west()) || FenceHandler.matchesBlockPos3(pos.west().up());
        boolean flag3 = FenceHandler.matchesBlockPos3(pos.south()) || FenceHandler.matchesBlockPos3(pos.south().up());
        boolean flag4 = FenceHandler.matchesBlockPos3(pos.north()) || FenceHandler.matchesBlockPos3(pos.north().up());
        boolean flag5 = flag && flag2 && !flag3 && !flag4;
        boolean flag6 = flag4 && flag3 && !flag && !flag2;
        return flag5 || flag6;
    }

    private void handleDouble(double d, double d2, double d3, boolean flag, boolean flag2) {
        this.handleKeyBinding(mc.gameSettings.keyBindForward, true);
        this.handleKeyBinding(mc.gameSettings.keyBindSprint, d > 3.5);
        this.handleDouble(d3);
        boolean flag3 = flag || !flag2 && Math.random() < d2;
        this.handleKeyBinding(mc.gameSettings.keyBindJump, flag3);
    }

    public void handleBlockPos(BlockPos pos, int n, int n2, double d) {
        this.handleBlockPos(pos, true, n, n2, d, 0.0);
    }

    public boolean checkState3() {
        return this.entries != null
            && !this.entries.isEmpty()
            && this.blockPos2 != null
            && Math.sqrt(this.entries.get(this.entries.size() - 1).distanceSq(this.blockPos2)) < 1.25;
    }

    private void handleKeyBinding(KeyBinding keyBinding, boolean flag) {
        if (keyBinding != null && keyBinding.isKeyDown() != flag) {
            keyBinding.pressed = flag;
        }
    }

    public ClearPathHandler(double d) {
        this(8, 0, 0, 1000, d, true);
    }

    public void handleEntity(Entity entity, int n, int n2, double d, double d2) {
        BlockPos pos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ));
        BlockPos pos2 = MiningHandler2.computeBlockPos(pos);
        this.handleBlockPos(pos2 != null ? pos2 : pos, true, n, n2, d, d2);
        if (this.flag2 && this.vec3 == null && mc.thePlayer.canEntityBeSeen(entity)) {
            BuildTargetComparatorHelper.handleEntity(entity, n, n2);
        }
    }

    public ClearPathHandler() {
        this(100, 10, 30, 350, 2.5, false);
    }

    private int computeInt() {
        return NavigationHandler.computeInt() >= 2 && this.count6 >= 500 ? this.count6 / 2 : this.count6;
    }

    public boolean isEntry2() {
        return this.entry2 != null;
    }

    public void process2() {
        this.entry2 = null;
        this.blockPos = null;
        this.worldClient = null;
        this.trackedEntity = null;
        this.count4 = Integer.MIN_VALUE;
        KeybindStateUtil.process();
        this.vec3 = null;
        this.entries = null;
        this.miningHandler.handleList(null);
        this.miningHandler.handleBlockPos(null);
        this.blockPos2 = null;
        this.blockPos3 = null;
        this.count5 = 0;
        this.count7 = 0;
        this.count8 = 0;
        this.count9 = 0;
        this.process();
    }

    private void handleBlockPos(BlockPos pos, boolean flag, int n, int n2, double d, double d2, boolean flag2) {
        this.vec3 = null;
        BlockPos pos2 = this.computeBlockPos();
        double dist = mc.thePlayer.getDistance(pos.getX(), pos.getY(), pos.getZ());
        if (this.worldClient != null && this.worldClient != mc.theWorld) {
            this.clearPath();
        }

        BlockPos pos3 = this.blockPos != null ? this.blockPos : this.blockPos2;
        boolean flag3 = flag2 && pos3 != null && this.utilHelper82.matchesLong(300L) && pos3.distanceSq(pos) >= 36.0;
        if (flag3 || this.entry2 == null && this.matchesBlockPos3(pos)) {
            if (this.utilHelper83.matchesLong(5000L)) {
                this.utilHelper83.reset();
                this.process3();
            }

            this.handleBlockPos(pos);
        }

        this.handleFlag(flag2);
        if (this.entries != null && !this.entries.isEmpty() && this.count5 < this.entries.size()) {
            this.count5 = this.computeInt(this.count5);
            this.count5 = Math.min(this.count5, this.entries.size() - 1);
            BlockPos pos4 = this.entries.get(this.count5);
            double d3 = this.computeDouble(pos4, pos2, dist, flag, n, n2, d, d2);
            if (d3 <= this.amount2 && pos4.getY() <= pos2.getY()) {
                this.count5++;
                this.blockPos3 = pos4;
                if (this.count5 >= this.entries.size()) {
                    this.count7 = 0;
                }
            }
        } else {
            if (flag2 && this.entry2 != null) {
                var list = this.entry2.computeList2();
                int n3 = this.computeInt(list, pos2);
                if (n3 >= 0) {
                    this.computeDouble((BlockPos)list.get(n3), pos2, dist, true, n, n2, d, 0.0);
                    return;
                }
            }

            if (!flag || this.entry2 == null && !flag2 || !this.matchesBlockPos(pos2, pos, dist, n, n2)) {
                this.process4();
            }
        }
    }

    public void process() {
        this.utilHelper8.reset();
        this.count10 = 0;
    }

    public void clearPath() {
        this.entry2 = null;
        this.blockPos = null;
        this.worldClient = null;
        this.trackedEntity = null;
        this.count4 = Integer.MIN_VALUE;
        this.vec3 = null;
        this.entries = null;
        this.miningHandler.handleList(null);
        this.miningHandler.handleBlockPos(null);
        this.blockPos2 = null;
        this.blockPos3 = null;
        this.count5 = 0;
        this.count7 = 0;
        this.count9 = 0;
        this.utilHelper82.clear();
        this.process();
        this.utilHelper84.reset();
    }

    public void onEventRender3D(EventRender3D event) {
        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
            this.miningHandler.onEventRender3D(event);
        }
    }

    public ClearPathHandler(int count, int n, int n2, int n3, double amount2, boolean flag) {
        this.count = count;
        this.count2 = n;
        this.count3 = n2;
        this.count6 = n3;
        this.amount2 = amount2;
        this.flag = flag;
    }

    public Vec3 getVec3() {
        return this.vec3;
    }

    public void handleFlag2(boolean flag) {
        this.miningHandler.setEnabled(flag);
    }

    private boolean matchesBlockPos3(BlockPos pos) {
        if (!this.utilHelper82.matchesLong(150L)) {
            return false;
        }
        if (this.count7 > 0) {
            this.count7--;
        }

        boolean flag = this.entries == null || this.entries.isEmpty() || this.count5 >= this.entries.size();
        boolean flag2 = this.entries != null && this.utilHelper84.matchesLong(3000L);
        boolean flag3 = this.count9 >= 5;
        boolean flag4 = this.blockPos2 == null || this.blockPos2.distanceSq(pos) >= 4.0;
        if (flag) {
            return true;
        }
        return !flag2 && !flag3 ? flag4 && this.count7 <= 0 : this.count7 <= 0;
    }

    public void handleEntity2(Entity trackedEntity, int n, int n2, double d) {
        if (this.trackedEntity != trackedEntity) {
            this.clearPath();
            this.trackedEntity = trackedEntity;
        }

        BlockPos pos = trackedEntity.getPosition();
        this.handleBlockPos(pos, true, n, n2, d, 0.0, true);
        if (this.flag2 && this.vec3 == null && mc.thePlayer.canEntityBeSeen(trackedEntity)) {
            BuildTargetComparatorHelper.handleEntity(trackedEntity, n, n2);
        }
    }

    private void handleBlockPos(BlockPos blockPos) {
        BlockPos pos = this.computeBlockPos();
        double dist = mc.thePlayer.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        this.utilHelper82.reset();
        this.entry2 = HuntingHandler3.computeEntry2(pos, blockPos, this.computeInt(), this.count8);
        this.blockPos = blockPos;
        this.worldClient = mc.theWorld;
        this.handleBlockPos(blockPos, dist);
    }

    private void handleBlockPos(BlockPos pos, BlockPos pos2) {
        if (this.checkState3()) {
            this.process();
        } else {
            if (this.entries != null && this.entries.size() > 1) {
                BlockPos pos3 = this.entries.get(this.entries.size() - 1);
                double dist = Math.sqrt(pos.distanceSq(pos2));
                double dist2 = Math.sqrt(pos3.distanceSq(pos2));
                double d = dist - dist2;
                if (!pos3.equals(pos) && (d > 0.5 || this.entries.size() > 2)) {
                    this.process();
                    return;
                }
            }

            this.count10++;
        }
    }

    private int computeInt(int n) {
        if (!this.flag) {
            return n;
        }
        int n2 = NavigationHandler.computeInt();

        int n3;
        for (n3 = n; n3 < this.entries.size(); n3++) {
            BlockPos pos = this.entries.get(n3);
            if (this.blockPos3 != null) {
                boolean flag = FenceHandler.matchesBlockPos8(pos) || FenceHandler.matchesBlockPos8(pos.down());
                boolean flag2 = pos.getY() + 4.0 < this.blockPos3.getY();
                boolean flag3 = pos.getY() > this.blockPos3.getY() + (n2 - 1);
                if (!flag && (flag3 || flag2)) {
                    break;
                }
            }

            if (this.matchesBlockPos2(pos)) {
                break;
            }

            if (n3 < this.entries.size() - 1) {
                BlockPos pos2 = this.entries.get(n3 + 1);
                boolean flag4 = this.matchesBlockPos(pos2);
                boolean flag5 = n3 >= this.entries.size() - 2 || this.matchesBlockPos(this.entries.get(n3 + 2));
                if ((!flag4 || !flag5) && !this.matchesBlockPos2(pos2)) {
                    break;
                }
            }

            this.blockPos3 = pos;
        }

        return Math.min(n3, this.entries.size() - 1);
    }

    private boolean matchesBlockPos(BlockPos pos, BlockPos pos2, double d) {
        if (pos2.getY() - pos.getY() == 1 && d < 1.5) {
            return true;
        }
        double d2 = pos2.getX() + 0.5 - mc.thePlayer.posX;
        double d3 = pos2.getZ() + 0.5 - mc.thePlayer.posZ;
        double d4 = Math.hypot(d2, d3);
        if (d4 <= 0.1) {
            return false;
        }
        d2 /= d4;
        d3 /= d4;
        BlockPos pos3 = new BlockPos(Math.floor(mc.thePlayer.posX + d2), (double)pos.getY(), Math.floor(mc.thePlayer.posZ + d3));
        return FenceHandler.matchesBlockPos3(pos3) && !FenceHandler.matchesBlockPos3(pos3.up());
    }

    private int computeInt(List<BlockPos> list, BlockPos pos) {
        int n = list.indexOf(pos);
        if (n >= 0 && n + 1 < list.size() && FenceHandler.matchesBlockPos(pos, (BlockPos)list.get(n + 1))) {
            return n + 1;
        }
        int n2 = -1;
        double d = 2.26;

        for (int i = 1; i < list.size(); i++) {
            BlockPos pos2 = (BlockPos)list.get(i);
            double d2 = pos.distanceSq(pos2);
            if (i > n && d2 > 0.0 && d2 <= d && FenceHandler.matchesBlockPos(pos, pos2)) {
                d = d2;
                n2 = i;
            }
        }

        return n2;
    }

    private void handleFlag(boolean flag) {
        if (this.entry2 != null && this.count4 != mc.thePlayer.ticksExisted) {
            this.count4 = mc.thePlayer.ticksExisted;
            if (this.entry2.matchesInt(flag ? 24 : 8, 2000000L)) {
                var list = this.entry2.computeList();
                BlockPos blockPos = this.blockPos;
                this.entry2 = null;
                this.blockPos = null;
                BlockPos pos = this.computeBlockPos();
                boolean flag2 = this.entries != null && !this.entries.isEmpty() && this.count5 < this.entries.size();
                if (list != null && list.size() > 1 && !((BlockPos)list.get(list.size() - 1)).equals(pos)) {
                    int n = this.computeInt(list, pos);
                    if (n < 0) {
                        this.count7 = 0;
                        this.utilHelper82.clear();
                    } else {
                        this.entries = list;
                        this.miningHandler.handleList(list.stream().map(BlockPos::down).collect(Collectors.toList()));
                        this.blockPos2 = blockPos;
                        this.blockPos3 = null;
                        this.count5 = n;
                        this.count9 = 0;
                        this.handleBlockPos(pos, blockPos);
                        this.utilHelper84.reset();
                    }
                } else {
                    this.count10++;
                    if (!flag2) {
                        this.entries = null;
                        this.blockPos2 = blockPos;
                        this.blockPos3 = null;
                        this.count5 = 0;
                        this.count9 = 0;
                    }

                    this.utilHelper84.reset();
                }
            }
        }
    }

    private boolean matchesBlockPos(BlockPos pos, BlockPos pos2, double d, int n, int n2) {
        int n3 = Integer.compare(pos2.getX(), pos.getX());
        int n4 = Integer.compare(pos2.getZ(), pos.getZ());
        if ((n3 != 0 || n4 != 0) && mc.thePlayer.onGround && this.flag2) {
            BlockPos pos3 = pos.add(n3, 0, n4);
            if (!FenceHandler.matchesBlockPos(pos, pos3)) {
                return false;
            }
            this.vec3 = new Vec3(pos3.getX() + 0.5, mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.55F, pos3.getZ() + 0.5);
            BuildTargetComparatorHelper.handleDouble(this.vec3.xCoord, this.vec3.yCoord, this.vec3.zCoord, n, n2);
            this.handleDouble(d, 0.0, 0.0, false, false);
            return true;
        }
        return false;
    }

    public boolean checkState() {
        return this.count10 >= 2 && this.utilHelper8.matchesLong(1000L);
    }
}
