package org.brutality.automation.hunting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public final class FindHandler {
    private static final double[] doubleValueArray = new double[]{0.1, 0.3, 0.5, 0.7, 0.9};

    public FindHandler.Entry find(World world, Collection<BlockPos> collection, Vec3 vec, double d, ToDoubleFunction<BlockPos> toDoubleFunction) {
        return this.computeEntry(world, collection, vec, d, toDoubleFunction, entry2 -> true);
    }

    public FindHandler.Entry computeEntry(
        World world, Collection<BlockPos> collection, Vec3 vec, double d, ToDoubleFunction<BlockPos> toDoubleFunction, Predicate<FindHandler.Entry2> predicate
    ) {
        if (world != null && !collection.isEmpty() && !(d <= 0.0)) {
            HashMap map = new HashMap();
            HashMap map2 = new HashMap();
            HashSet set = new HashSet(collection);
            FindHandler.Entry entry = null;

            for (BlockPos pos : collection) {
                if (world.isBlockLoaded(pos)) {
                    IBlockState blockState = world.getBlockState(pos);
                    if (blockState.getBlock() instanceof BlockBed) {
                        Block block = blockState.getBlock();
                        block.setBlockBoundsBasedOnState(world, pos);
                        AxisAlignedBB selectedBoundingBox = block.getSelectedBoundingBox(world, pos);

                        for (EnumFacing facing : EnumFacing.values()) {
                            for (double d2 : doubleValueArray) {
                                for (double d3 : doubleValueArray) {
                                    Vec3 vec2 = this.computeVec3(selectedBoundingBox, facing, d2, d3);
                                    if (!(vec.squareDistanceTo(vec2) > d * d)) {
                                        FindHandler.Entry entry2 = this.computeEntry(world, set, vec, vec2, toDoubleFunction, map, map2);
                                        if (entry2 != null
                                            && predicate.test(entry2.computeEntry2())
                                            && (
                                                entry == null
                                                    || entry2.amount < entry.amount - 1.0E-6
                                                    || Math.abs(entry2.amount - entry.amount) <= 1.0E-6
                                                        && (
                                                            entry2.entries.size() < entry.entries.size()
                                                                || entry2.entries.size() == entry.entries.size()
                                                                    && vec.squareDistanceTo(entry2.computeEntry2().vec3)
                                                                        < vec.squareDistanceTo(entry.computeEntry2().vec3)
                                                        )
                                            )) {
                                            entry = entry2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return entry;
        }
        return null;
    }

    private Vec3 computeVec3(AxisAlignedBB box, EnumFacing facing, double d, double d2) {
        double d3 = box.minX + (box.maxX - box.minX) * d;
        double d4 = box.minY + (box.maxY - box.minY) * d;
        double d5 = box.minZ + (box.maxZ - box.minZ) * d2;
        switch (facing) {
            case WEST:
                d3 = box.minX + 0.001;
                break;
            case EAST:
                d3 = box.maxX - 0.001;
                break;
            case DOWN:
                d3 = box.minX + (box.maxX - box.minX) * d;
                d4 = box.minY + 0.001;
                break;
            case UP:
                d3 = box.minX + (box.maxX - box.minX) * d;
                d4 = box.maxY - 0.001;
                break;
            case NORTH:
                d5 = box.minZ + 0.001;
                d4 = box.minY + (box.maxY - box.minY) * d2;
                break;
            case SOUTH:
                d5 = box.maxZ - 0.001;
                d4 = box.minY + (box.maxY - box.minY) * d2;
        }

        return new Vec3(d3, d4, d5);
    }

    private FindHandler.Entry computeEntry(
        World world, Set<BlockPos> set, Vec3 vec, Vec3 vec2, ToDoubleFunction<BlockPos> toDoubleFunction, Map<BlockPos, IBlockState> map, Map<BlockPos, Double> map2
    ) {
        double d = vec2.xCoord - vec.xCoord;
        double d2 = vec2.yCoord - vec.yCoord;
        double d3 = vec2.zCoord - vec.zCoord;
        int n = MathHelper.floor_double(vec.xCoord);
        int n2 = MathHelper.floor_double(vec.yCoord);
        int n3 = MathHelper.floor_double(vec.zCoord);
        int n4 = Double.compare(d, 0.0);
        int n5 = Double.compare(d2, 0.0);
        int n6 = Double.compare(d3, 0.0);
        double d4 = this.computeDouble(vec.xCoord, n, d, n4);
        double d5 = this.computeDouble(vec.yCoord, n2, d2, n5);
        double d6 = this.computeDouble(vec.zCoord, n3, d3, n6);
        double d7 = d == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / d);
        double d8 = d2 == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / d2);
        double d9 = d3 == 0.0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / d3);
        ArrayList list = new ArrayList();
        double d10 = 0.0;
        double d11 = 0.0;

        for (int i = 0; i < 64 && d11 <= 1.0; i++) {
            BlockPos pos = new BlockPos(n, n2, n3);
            if (!world.isBlockLoaded(pos)) {
                return null;
            }

            IBlockState blockState = (IBlockState)map.get(pos);
            if (blockState == null) {
                blockState = world.getBlockState(pos);
                map.put(pos, blockState);
            }

            Block block = blockState.getBlock();
            if (block.canCollideCheck(blockState, false)) {
                MovingObjectPosition hit = block.collisionRayTrace(world, pos, vec, vec2);
                if (hit != null) {
                    if (block instanceof BlockBed && !set.contains(pos)) {
                        return null;
                    }

                    Double val = (Double)map2.get(pos);
                    if (val == null) {
                        val = toDoubleFunction.applyAsDouble(pos);
                        map2.put(pos, val);
                    }

                    if (!Double.isFinite(val) || val <= 0.0) {
                        return null;
                    }

                    d10 += val;
                    list.add(new FindHandler.Entry2(pos, hit, blockState));
                    if (block instanceof BlockBed) {
                        return new FindHandler.Entry(pos, list, d10);
                    }

                    if (list.size() > 16) {
                        return null;
                    }
                }
            }

            if (d4 < d5 && d4 < d6) {
                d11 = d4;
                d4 += d7;
                n += n4;
            } else if (d5 < d6) {
                d11 = d5;
                d5 += d8;
                n2 += n5;
            } else {
                d11 = d6;
                d6 += d9;
                n3 += n6;
            }
        }

        return null;
    }

    private double computeDouble(double d, int n, double d2, int n2) {
        return d2 == 0.0 ? Double.POSITIVE_INFINITY : ((n2 > 0 ? n + 1 : n) - d) / d2;
    }

    public static final class Entry {
        public final BlockPos blockPos;
        public final List<FindHandler.Entry2> entries;
        public final double amount;

        public FindHandler.Entry2 computeEntry2() {
            return this.entries.get(0);
        }

        public Entry(BlockPos blockPos, List<FindHandler.Entry2> entry2s, double amount) {
            this.blockPos = blockPos;
            this.entries = Collections.unmodifiableList(entry2s);
            this.amount = amount;
        }
    }

    public static final class Entry2 {
        public final BlockPos blockPos;
        public final EnumFacing enumFacing;
        public final Vec3 vec3;
        public final IBlockState iBlockState;

        public Entry2(BlockPos blockPos, MovingObjectPosition hit, IBlockState iBlockState) {
            this.blockPos = blockPos;
            this.enumFacing = hit.sideHit;
            this.vec3 = hit.hitVec;
            this.iBlockState = iBlockState;
        }
    }
}
