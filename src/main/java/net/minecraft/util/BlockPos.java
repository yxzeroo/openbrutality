package net.minecraft.util;

import com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import net.minecraft.entity.Entity;

public class BlockPos extends Vec3i {
    public static final BlockPos ORIGIN = new BlockPos(0, 0, 0);
    private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
    private static final int Y_SHIFT = 0 + NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    public BlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockPos(double x, double y, double z) {
        super(x, y, z);
    }

    public BlockPos(Entity source) {
        this(source.posX, source.posY, source.posZ);
    }

    public BlockPos(Vec3 source) {
        this(source.xCoord, source.yCoord, source.zCoord);
    }

    public BlockPos(Vec3i source) {
        this(source.getX(), source.getY(), source.getZ());
    }

    public BlockPos add(double x, double y, double z) {
        return x == 0.0 && y == 0.0 && z == 0.0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(int x, int y, int z) {
        return x == 0 && y == 0 && z == 0 ? this : new BlockPos(this.getX() + x, this.getY() + y, this.getZ() + z);
    }

    public BlockPos add(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0
            ? this
            : new BlockPos(this.getX() + vec.getX(), this.getY() + vec.getY(), this.getZ() + vec.getZ());
    }

    public BlockPos subtract(Vec3i vec) {
        return vec.getX() == 0 && vec.getY() == 0 && vec.getZ() == 0
            ? this
            : new BlockPos(this.getX() - vec.getX(), this.getY() - vec.getY(), this.getZ() - vec.getZ());
    }

    public BlockPos up() {
        return this.up(1);
    }

    public BlockPos up(int n) {
        return this.offset(EnumFacing.UP, n);
    }

    public BlockPos down() {
        return this.down(1);
    }

    public BlockPos down(int n) {
        return this.offset(EnumFacing.DOWN, n);
    }

    public BlockPos north() {
        return this.north(1);
    }

    public BlockPos north(int n) {
        return this.offset(EnumFacing.NORTH, n);
    }

    public BlockPos south() {
        return this.south(1);
    }

    public BlockPos south(int n) {
        return this.offset(EnumFacing.SOUTH, n);
    }

    public BlockPos west() {
        return this.west(1);
    }

    public BlockPos west(int n) {
        return this.offset(EnumFacing.WEST, n);
    }

    public BlockPos east() {
        return this.east(1);
    }

    public BlockPos east(int n) {
        return this.offset(EnumFacing.EAST, n);
    }

    public BlockPos offset(EnumFacing facing) {
        return this.offset(facing, 1);
    }

    public BlockPos offset(EnumFacing facing, int n) {
        return n == 0
            ? this
            : new BlockPos(
                this.getX() + facing.getFrontOffsetX() * n, this.getY() + facing.getFrontOffsetY() * n, this.getZ() + facing.getFrontOffsetZ() * n
            );
    }

    public BlockPos crossProduct(Vec3i vec) {
        return new BlockPos(
            this.getY() * vec.getZ() - this.getZ() * vec.getY(),
            this.getZ() * vec.getX() - this.getX() * vec.getZ(),
            this.getX() * vec.getY() - this.getY() * vec.getX()
        );
    }

    public long toLong() {
        return (this.getX() & X_MASK) << X_SHIFT | (this.getY() & Y_MASK) << Y_SHIFT | (this.getZ() & Z_MASK) << 0;
    }

    public static BlockPos fromLong(long serialized) {
        int n = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
        int n2 = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
        int n3 = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
        return new BlockPos(n, n2, n3);
    }

    public static Iterable<BlockPos> getAllInBox(BlockPos from, BlockPos to) {
        final BlockPos pos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos pos2 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos>() {
            @Override
            public Iterator<BlockPos> iterator() {
                return new AbstractIterator<BlockPos>() {
                    private BlockPos lastReturned = null;

                    protected BlockPos computeNext() {
                        if (this.lastReturned == null) {
                            this.lastReturned = pos;
                            return this.lastReturned;
                        }
                        if (this.lastReturned.equals(pos2)) {
                            return this.endOfData();
                        }
                        int x = this.lastReturned.getX();
                        int y = this.lastReturned.getY();
                        int z = this.lastReturned.getZ();
                        if (x < pos2.getX()) {
                            x++;
                        } else if (y < pos2.getY()) {
                            x = pos.getX();
                            y++;
                        } else if (z < pos2.getZ()) {
                            x = pos.getX();
                            y = pos.getY();
                            z++;
                        }

                        this.lastReturned = new BlockPos(x, y, z);
                        return this.lastReturned;
                    }
                };
            }
        };
    }

    public static Iterable<BlockPos.MutableBlockPos> getAllInBoxMutable(BlockPos from, BlockPos to) {
        final BlockPos pos = new BlockPos(Math.min(from.getX(), to.getX()), Math.min(from.getY(), to.getY()), Math.min(from.getZ(), to.getZ()));
        final BlockPos pos2 = new BlockPos(Math.max(from.getX(), to.getX()), Math.max(from.getY(), to.getY()), Math.max(from.getZ(), to.getZ()));
        return new Iterable<BlockPos.MutableBlockPos>() {
            @Override
            public Iterator<BlockPos.MutableBlockPos> iterator() {
                return new AbstractIterator<BlockPos.MutableBlockPos>() {
                    private BlockPos.MutableBlockPos theBlockPos = null;

                    protected BlockPos.MutableBlockPos computeNext() {
                        if (this.theBlockPos == null) {
                            this.theBlockPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
                            return this.theBlockPos;
                        }
                        if (this.theBlockPos.equals(pos2)) {
                            return this.endOfData();
                        }
                        int x = this.theBlockPos.getX();
                        int y = this.theBlockPos.getY();
                        int z = this.theBlockPos.getZ();
                        if (x < pos2.getX()) {
                            x++;
                        } else if (y < pos2.getY()) {
                            x = pos.getX();
                            y++;
                        } else if (z < pos2.getZ()) {
                            x = pos.getX();
                            y = pos.getY();
                            z++;
                        }

                        this.theBlockPos.x = x;
                        this.theBlockPos.y = y;
                        this.theBlockPos.z = z;
                        return this.theBlockPos;
                    }
                };
            }
        };
    }

    public Vec3 toVec3() {
        return new Vec3(this.getX(), this.getY(), this.getZ());
    }

    public static final class MutableBlockPos extends BlockPos {
        int x;
        int y;
        int z;

        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(int x_, int y_, int z_) {
            super(0, 0, 0);
            this.x = x_;
            this.y = y_;
            this.z = z_;
        }

        @Override
        public int getX() {
            return this.x;
        }

        @Override
        public int getY() {
            return this.y;
        }

        @Override
        public int getZ() {
            return this.z;
        }

        public BlockPos.MutableBlockPos func_181079_c(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
            this.x = p_181079_1_;
            this.y = p_181079_2_;
            this.z = p_181079_3_;
            return this;
        }
    }
}
