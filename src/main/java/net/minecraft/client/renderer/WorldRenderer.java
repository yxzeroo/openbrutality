package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.BitSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.src.Config;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.optifine.SmartAnimations;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;
import net.optifine.util.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.brutality.utils.render.HeldItemCustomization;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {
    private ByteBuffer byteBuffer;
    public IntBuffer rawIntBuffer;
    private ShortBuffer field_181676_c;
    public FloatBuffer rawFloatBuffer;
    public int vertexCount;
    private VertexFormatElement field_181677_f;
    private int field_181678_g;
    private boolean needsUpdate;
    public int drawMode;
    private double xOffset;
    private double yOffset;
    private double zOffset;
    private VertexFormat vertexFormat;
    private boolean isDrawing;
    private EnumWorldBlockLayer blockLayer = null;
    private boolean[] drawnIcons = new boolean[256];
    private TextureAtlasSprite[] quadSprites = null;
    private TextureAtlasSprite[] quadSpritesPrev = null;
    private TextureAtlasSprite quadSprite = null;
    public SVertexBuilder sVertexBuilder;
    public RenderEnv renderEnv = null;
    public BitSet animatedSprites = null;
    public BitSet animatedSpritesCached = new BitSet();
    private boolean modeTriangles = false;
    private ByteBuffer byteBufferTriangles;
    private final QuadSort quadSort = new QuadSort();
    private final BitSet sortedQuads = new BitSet();
    private int[] quadSortTemp = new int[0];
    private TextureAtlasSprite[] quadSortSprites = new TextureAtlasSprite[0];
    private IntBuffer quadSortSource;
    private IntBuffer quadSortSourceOwner;

    public WorldRenderer(int bufferSizeIn) {
        this.byteBuffer = GLAllocation.createDirectByteBuffer(bufferSizeIn * 4);
        this.rawIntBuffer = this.byteBuffer.asIntBuffer();
        this.field_181676_c = this.byteBuffer.asShortBuffer();
        this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
        SVertexBuilder.initVertexBuilder(this);
    }

    private void func_181670_b(int p_181670_1_) {
        if (p_181670_1_ > this.rawIntBuffer.remaining()) {
            int n = this.byteBuffer.capacity();
            int n2 = n % 2097152;
            int n3 = n2 + (((this.rawIntBuffer.position() + p_181670_1_) * 4 - n2) / 2097152 + 1) * 2097152;
            LogManager.getLogger().warn("Needed to grow BufferBuilder buffer: Old size " + n + " bytes, new size " + n3 + " bytes.");
            int n4 = this.rawIntBuffer.position();
            ByteBuffer buffer = GLAllocation.createDirectByteBuffer(n3);
            this.byteBuffer.position(0);
            buffer.put(this.byteBuffer);
            buffer.rewind();
            this.byteBuffer = buffer;
            this.rawFloatBuffer = this.byteBuffer.asFloatBuffer();
            this.rawIntBuffer = this.byteBuffer.asIntBuffer();
            this.quadSortSource = null;
            this.quadSortSourceOwner = null;
            this.rawIntBuffer.position(n4);
            this.field_181676_c = this.byteBuffer.asShortBuffer();
            this.field_181676_c.position(n4 << 1);
            if (this.quadSprites != null) {
                TextureAtlasSprite[] quadSprites = this.quadSprites;
                int bufferQuadSize = this.getBufferQuadSize();
                this.quadSprites = new TextureAtlasSprite[bufferQuadSize];
                System.arraycopy(quadSprites, 0, this.quadSprites, 0, Math.min(quadSprites.length, this.quadSprites.length));
                this.quadSpritesPrev = null;
            }
        }
    }

    public void func_181674_a(float p_181674_1_, float p_181674_2_, float p_181674_3_) {
        int n = this.vertexCount / 4;
        float[] floats = this.quadSort.prepare(n);

        for (int i = 0; i < n; i++) {
            floats[i] = func_181665_a(
                this.rawFloatBuffer,
                (float)(p_181674_1_ + this.xOffset),
                (float)(p_181674_2_ + this.yOffset),
                (float)(p_181674_3_ + this.zOffset),
                this.vertexFormat.func_181719_f(),
                i * this.vertexFormat.getNextOffset()
            );
        }

        int[] ints = this.quadSort.sort(n);
        this.sortedQuads.clear();
        int nextOffset = this.vertexFormat.getNextOffset();
        if (this.quadSortTemp.length < nextOffset) {
            this.quadSortTemp = new int[nextOffset];
        }

        if (this.quadSortSourceOwner != this.rawIntBuffer) {
            this.quadSortSourceOwner = this.rawIntBuffer;
            this.quadSortSource = this.rawIntBuffer.duplicate();
        }

        for (int j = 0; (j = this.sortedQuads.nextClearBit(j)) < n; j++) {
            int n2 = ints[j];
            if (n2 != j) {
                this.rawIntBuffer.limit(n2 * nextOffset + nextOffset);
                this.rawIntBuffer.position(n2 * nextOffset);
                this.rawIntBuffer.get(this.quadSortTemp, 0, nextOffset);
                int n3 = n2;

                for (int k = ints[n2]; n3 != j; k = ints[k]) {
                    this.quadSortSource.limit(k * nextOffset + nextOffset);
                    this.quadSortSource.position(k * nextOffset);
                    this.rawIntBuffer.limit(n3 * nextOffset + nextOffset);
                    this.rawIntBuffer.position(n3 * nextOffset);
                    this.rawIntBuffer.put(this.quadSortSource);
                    this.sortedQuads.set(n3);
                    n3 = k;
                }

                this.rawIntBuffer.limit(j * nextOffset + nextOffset);
                this.rawIntBuffer.position(j * nextOffset);
                this.rawIntBuffer.put(this.quadSortTemp, 0, nextOffset);
            }

            this.sortedQuads.set(j);
        }

        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(this.func_181664_j());
        if (this.quadSprites != null) {
            if (this.quadSortSprites.length < n) {
                this.quadSortSprites = new TextureAtlasSprite[n];
            }

            for (int idx = 0; idx < n; idx++) {
                this.quadSortSprites[idx] = this.quadSprites[ints[idx]];
            }

            System.arraycopy(this.quadSortSprites, 0, this.quadSprites, 0, n);
            Arrays.fill(this.quadSortSprites, 0, n, null);
        }
    }

    public WorldRenderer.State func_181672_a() {
        this.rawIntBuffer.rewind();
        int n = this.func_181664_j();
        this.rawIntBuffer.limit(n);
        int[] ints = new int[n];
        this.rawIntBuffer.get(ints);
        this.rawIntBuffer.limit(this.rawIntBuffer.capacity());
        this.rawIntBuffer.position(n);
        TextureAtlasSprite[] textureAtlasSprites = null;
        if (this.quadSprites != null) {
            int n2 = this.vertexCount / 4;
            textureAtlasSprites = new TextureAtlasSprite[n2];
            System.arraycopy(this.quadSprites, 0, textureAtlasSprites, 0, n2);
        }

        return new WorldRenderer.State(ints, new VertexFormat(this.vertexFormat), textureAtlasSprites);
    }

    public int func_181664_j() {
        return this.vertexCount * this.vertexFormat.func_181719_f();
    }

    private static float func_181665_a(FloatBuffer p_181665_0_, float p_181665_1_, float p_181665_2_, float p_181665_3_, int p_181665_4_, int p_181665_5_) {
        float f = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 0);
        float f2 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 1);
        float f3 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 0 + 2);
        float f4 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 0);
        float f5 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 1);
        float f6 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 1 + 2);
        float f7 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 0);
        float f8 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 1);
        float f9 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 2 + 2);
        float f10 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 0);
        float f11 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 1);
        float f12 = p_181665_0_.get(p_181665_5_ + p_181665_4_ * 3 + 2);
        float f13 = (f + f4 + f7 + f10) * 0.25F - p_181665_1_;
        float f14 = (f2 + f5 + f8 + f11) * 0.25F - p_181665_2_;
        float f15 = (f3 + f6 + f9 + f12) * 0.25F - p_181665_3_;
        return f13 * f13 + f14 * f14 + f15 * f15;
    }

    public void setVertexState(WorldRenderer.State state) {
        this.rawIntBuffer.clear();
        this.func_181670_b(state.getRawBuffer().length);
        this.rawIntBuffer.put(state.getRawBuffer());
        this.vertexCount = state.getVertexCount();
        this.vertexFormat = new VertexFormat(state.getVertexFormat());
        if (state.stateQuadSprites != null) {
            if (this.quadSprites == null) {
                this.quadSprites = this.quadSpritesPrev;
            }

            if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
            }

            TextureAtlasSprite[] stateQuadSprites = state.stateQuadSprites;
            System.arraycopy(stateQuadSprites, 0, this.quadSprites, 0, stateQuadSprites.length);
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }

            this.quadSprites = null;
        }
    }

    public void reset() {
        this.vertexCount = 0;
        this.field_181677_f = null;
        this.field_181678_g = 0;
        this.quadSprite = null;
        if (SmartAnimations.isActive()) {
            if (this.animatedSprites == null) {
                this.animatedSprites = this.animatedSpritesCached;
            }

            this.animatedSprites.clear();
        } else if (this.animatedSprites != null) {
            this.animatedSprites = null;
        }

        this.modeTriangles = false;
    }

    public void func_181668_a(int p_181668_1_, VertexFormat p_181668_2_) {
        if (this.isDrawing) {
            throw new IllegalStateException("Already building!");
        }
        this.isDrawing = true;
        this.reset();
        this.drawMode = p_181668_1_;
        this.vertexFormat = p_181668_2_;
        this.field_181677_f = p_181668_2_.getElement(this.field_181678_g);
        this.needsUpdate = false;
        this.byteBuffer.limit(this.byteBuffer.capacity());
        if (Config.isShaders()) {
            SVertexBuilder.endSetVertexFormat(this);
        }

        if (Config.isMultiTexture()) {
            if (this.blockLayer != null) {
                if (this.quadSprites == null) {
                    this.quadSprites = this.quadSpritesPrev;
                }

                if (this.quadSprites == null || this.quadSprites.length < this.getBufferQuadSize()) {
                    this.quadSprites = new TextureAtlasSprite[this.getBufferQuadSize()];
                }
            }
        } else {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }

            this.quadSprites = null;
        }
    }

    public WorldRenderer func_181673_a(double p_181673_1_, double p_181673_3_) {
        if (this.quadSprite != null && this.quadSprites != null) {
            p_181673_1_ = this.quadSprite.toSingleU((float)p_181673_1_);
            p_181673_3_ = this.quadSprite.toSingleV((float)p_181673_3_);
            this.quadSprites[this.vertexCount / 4] = this.quadSprite;
        }

        int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.func_181720_d(this.field_181678_g);
        switch (this.field_181677_f.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(n, (float)p_181673_1_);
                this.byteBuffer.putFloat(n + 4, (float)p_181673_3_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(n, (int)p_181673_1_);
                this.byteBuffer.putInt(n + 4, (int)p_181673_3_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(n, (short)p_181673_3_);
                this.byteBuffer.putShort(n + 2, (short)p_181673_1_);
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(n, (byte)p_181673_3_);
                this.byteBuffer.put(n + 1, (byte)p_181673_1_);
        }

        this.func_181667_k();
        return this;
    }

    public WorldRenderer func_181671_a(int p_181671_1_, int p_181671_2_) {
        int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.func_181720_d(this.field_181678_g);
        switch (this.field_181677_f.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(n, p_181671_1_);
                this.byteBuffer.putFloat(n + 4, p_181671_2_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(n, p_181671_1_);
                this.byteBuffer.putInt(n + 4, p_181671_2_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(n, (short)p_181671_2_);
                this.byteBuffer.putShort(n + 2, (short)p_181671_1_);
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(n, (byte)p_181671_2_);
                this.byteBuffer.put(n + 1, (byte)p_181671_1_);
        }

        this.func_181667_k();
        return this;
    }

    public void putBrightness4(int p_178962_1_, int p_178962_2_, int p_178962_3_, int p_178962_4_) {
        int n = (this.vertexCount - 4) * this.vertexFormat.func_181719_f() + this.vertexFormat.getElementOffsetById(1) / 4;
        int n2 = this.vertexFormat.getNextOffset() >> 2;
        this.rawIntBuffer.put(n, p_178962_1_);
        this.rawIntBuffer.put(n + n2, p_178962_2_);
        this.rawIntBuffer.put(n + n2 * 2, p_178962_3_);
        this.rawIntBuffer.put(n + n2 * 3, p_178962_4_);
    }

    public void putPosition(double x, double y, double z) {
        int n = this.vertexFormat.func_181719_f();
        int n2 = (this.vertexCount - 4) * n;

        for (int i = 0; i < 4; i++) {
            int n3 = n2 + i * n;
            int n4 = n3 + 1;
            int n5 = n4 + 1;
            this.rawIntBuffer.put(n3, Float.floatToRawIntBits((float)(x + this.xOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n3))));
            this.rawIntBuffer.put(n4, Float.floatToRawIntBits((float)(y + this.yOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n4))));
            this.rawIntBuffer.put(n5, Float.floatToRawIntBits((float)(z + this.zOffset) + Float.intBitsToFloat(this.rawIntBuffer.get(n5))));
        }
    }

    public int getColorIndex(int p_78909_1_) {
        return ((this.vertexCount - p_78909_1_) * this.vertexFormat.getNextOffset() + this.vertexFormat.getColorOffset()) / 4;
    }

    public void putColorMultiplier(float red, float green, float blue, int p_178978_4_) {
        int colorIndex = this.getColorIndex(p_178978_4_);
        int n = -1;
        if (!this.needsUpdate) {
            n = this.rawIntBuffer.get(colorIndex);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int n2 = (int)((n & 0xFF) * red);
                int n3 = (int)((n >> 8 & 0xFF) * green);
                int n4 = (int)((n >> 16 & 0xFF) * blue);
                n &= -16777216;
                n = n | n4 << 16 | n3 << 8 | n2;
            } else {
                int n5 = (int)((n >> 24 & 0xFF) * red);
                int n6 = (int)((n >> 16 & 0xFF) * green);
                int n7 = (int)((n >> 8 & 0xFF) * blue);
                n &= 255;
                n = n | n5 << 24 | n6 << 16 | n7 << 8;
            }
        }

        this.rawIntBuffer.put(colorIndex, n);
    }

    private void putColor(int argb, int p_178988_2_) {
        int colorIndex = this.getColorIndex(p_178988_2_);
        int n = argb >> 16 & 0xFF;
        int n2 = argb >> 8 & 0xFF;
        int n3 = argb & 0xFF;
        int n4 = argb >> 24 & 0xFF;
        this.putColorRGBA(colorIndex, n, n2, n3, n4);
    }

    public void putColorRGB_F(float red, float green, float blue, int p_178994_4_) {
        int colorIndex = this.getColorIndex(p_178994_4_);
        int n = MathHelper.clamp_int((int)(red * 255.0F), 0, 255);
        int n2 = MathHelper.clamp_int((int)(green * 255.0F), 0, 255);
        int n3 = MathHelper.clamp_int((int)(blue * 255.0F), 0, 255);
        this.putColorRGBA(colorIndex, n, n2, n3, 255);
    }

    public void putColorRGBA(int index, int red, int p_178972_3_, int p_178972_4_, int p_178972_5_) {
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            this.rawIntBuffer.put(index, p_178972_5_ << 24 | p_178972_4_ << 16 | p_178972_3_ << 8 | red);
        } else {
            this.rawIntBuffer.put(index, red << 24 | p_178972_3_ << 16 | p_178972_4_ << 8 | p_178972_5_);
        }
    }

    public void markDirty() {
        this.needsUpdate = true;
    }

    public WorldRenderer func_181666_a(float p_181666_1_, float p_181666_2_, float p_181666_3_, float p_181666_4_) {
        return this.func_181669_b((int)(p_181666_1_ * 255.0F), (int)(p_181666_2_ * 255.0F), (int)(p_181666_3_ * 255.0F), (int)(p_181666_4_ * 255.0F));
    }

    public WorldRenderer color(int colorHex) {
        return this.func_181666_a(colorHex >> 16 & 0xFF, colorHex >> 8 & 0xFF, colorHex & 0xFF, colorHex >> 24 & 0xFF);
    }

    public WorldRenderer func_181669_b(int p_181669_1_, int p_181669_2_, int p_181669_3_, int p_181669_4_) {
        if (this.needsUpdate) {
            return this;
        }
        int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.func_181720_d(this.field_181678_g);
        switch (this.field_181677_f.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(n, p_181669_1_ / 255.0F);
                this.byteBuffer.putFloat(n + 4, p_181669_2_ / 255.0F);
                this.byteBuffer.putFloat(n + 8, p_181669_3_ / 255.0F);
                this.byteBuffer.putFloat(n + 12, p_181669_4_ / 255.0F);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putFloat(n, p_181669_1_);
                this.byteBuffer.putFloat(n + 4, p_181669_2_);
                this.byteBuffer.putFloat(n + 8, p_181669_3_);
                this.byteBuffer.putFloat(n + 12, p_181669_4_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(n, (short)p_181669_1_);
                this.byteBuffer.putShort(n + 2, (short)p_181669_2_);
                this.byteBuffer.putShort(n + 4, (short)p_181669_3_);
                this.byteBuffer.putShort(n + 6, (short)p_181669_4_);
                break;
            case UBYTE:
            case BYTE:
                if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                    this.byteBuffer.put(n, (byte)p_181669_1_);
                    this.byteBuffer.put(n + 1, (byte)p_181669_2_);
                    this.byteBuffer.put(n + 2, (byte)p_181669_3_);
                    this.byteBuffer.put(n + 3, (byte)p_181669_4_);
                } else {
                    this.byteBuffer.put(n, (byte)p_181669_4_);
                    this.byteBuffer.put(n + 1, (byte)p_181669_3_);
                    this.byteBuffer.put(n + 2, (byte)p_181669_2_);
                    this.byteBuffer.put(n + 3, (byte)p_181669_1_);
                }
        }

        this.func_181667_k();
        return this;
    }

    public void addVertexData(int[] vertexData) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertexData(this, vertexData);
        }

        this.func_181670_b(vertexData.length);
        this.rawIntBuffer.position(this.func_181664_j());
        this.rawIntBuffer.put(vertexData);
        this.vertexCount = this.vertexCount + vertexData.length / this.vertexFormat.func_181719_f();
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertexData(this);
        }
    }

    public void func_181675_d() {
        this.vertexCount++;
        this.func_181670_b(this.vertexFormat.func_181719_f());
        this.field_181678_g = 0;
        this.field_181677_f = this.vertexFormat.getElement(this.field_181678_g);
        if (Config.isShaders()) {
            SVertexBuilder.endAddVertex(this);
        }
    }

    public WorldRenderer func_181662_b(double p_181662_1_, double p_181662_3_, double p_181662_5_) {
        if (Config.isShaders()) {
            SVertexBuilder.beginAddVertex(this);
        }

        int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.func_181720_d(this.field_181678_g);
        switch (this.field_181677_f.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(n, (float)(p_181662_1_ + this.xOffset));
                this.byteBuffer.putFloat(n + 4, (float)(p_181662_3_ + this.yOffset));
                this.byteBuffer.putFloat(n + 8, (float)(p_181662_5_ + this.zOffset));
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(n, Float.floatToRawIntBits((float)(p_181662_1_ + this.xOffset)));
                this.byteBuffer.putInt(n + 4, Float.floatToRawIntBits((float)(p_181662_3_ + this.yOffset)));
                this.byteBuffer.putInt(n + 8, Float.floatToRawIntBits((float)(p_181662_5_ + this.zOffset)));
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(n, (short)(p_181662_1_ + this.xOffset));
                this.byteBuffer.putShort(n + 2, (short)(p_181662_3_ + this.yOffset));
                this.byteBuffer.putShort(n + 4, (short)(p_181662_5_ + this.zOffset));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(n, (byte)(p_181662_1_ + this.xOffset));
                this.byteBuffer.put(n + 1, (byte)(p_181662_3_ + this.yOffset));
                this.byteBuffer.put(n + 2, (byte)(p_181662_5_ + this.zOffset));
        }

        this.func_181667_k();
        return this;
    }

    public void putNormal(float x, float y, float z) {
        int n = (byte)(x * 127.0F) & 255;
        int n2 = (byte)(y * 127.0F) & 255;
        int n3 = (byte)(z * 127.0F) & 255;
        int n4 = n | n2 << 8 | n3 << 16;
        int n5 = this.vertexFormat.getNextOffset() >> 2;
        int n6 = (this.vertexCount - 4) * n5 + this.vertexFormat.getNormalOffset() / 4;
        this.rawIntBuffer.put(n6, n4);
        this.rawIntBuffer.put(n6 + n5, n4);
        this.rawIntBuffer.put(n6 + n5 * 2, n4);
        this.rawIntBuffer.put(n6 + n5 * 3, n4);
    }

    private void func_181667_k() {
        this.field_181678_g++;
        this.field_181678_g = this.field_181678_g % this.vertexFormat.getElementCount();
        this.field_181677_f = this.vertexFormat.getElement(this.field_181678_g);
        if (this.field_181677_f.getUsage() == EnumUsage.PADDING) {
            this.func_181667_k();
        }
    }

    public WorldRenderer func_181663_c(float p_181663_1_, float p_181663_2_, float p_181663_3_) {
        int n = this.vertexCount * this.vertexFormat.getNextOffset() + this.vertexFormat.func_181720_d(this.field_181678_g);
        switch (this.field_181677_f.getType()) {
            case FLOAT:
                this.byteBuffer.putFloat(n, p_181663_1_);
                this.byteBuffer.putFloat(n + 4, p_181663_2_);
                this.byteBuffer.putFloat(n + 8, p_181663_3_);
                break;
            case UINT:
            case INT:
                this.byteBuffer.putInt(n, (int)p_181663_1_);
                this.byteBuffer.putInt(n + 4, (int)p_181663_2_);
                this.byteBuffer.putInt(n + 8, (int)p_181663_3_);
                break;
            case USHORT:
            case SHORT:
                this.byteBuffer.putShort(n, (short)((int)(p_181663_1_ * 32767.0F) & 65535));
                this.byteBuffer.putShort(n + 2, (short)((int)(p_181663_2_ * 32767.0F) & 65535));
                this.byteBuffer.putShort(n + 4, (short)((int)(p_181663_3_ * 32767.0F) & 65535));
                break;
            case UBYTE:
            case BYTE:
                this.byteBuffer.put(n, (byte)((int)(p_181663_1_ * 127.0F) & 0xFF));
                this.byteBuffer.put(n + 1, (byte)((int)(p_181663_2_ * 127.0F) & 0xFF));
                this.byteBuffer.put(n + 2, (byte)((int)(p_181663_3_ * 127.0F) & 0xFF));
        }

        this.func_181667_k();
        return this;
    }

    public void setTranslation(double x, double y, double z) {
        this.xOffset = x;
        this.yOffset = y;
        this.zOffset = z;
    }

    public void finishDrawing() {
        if (!this.isDrawing) {
            throw new IllegalStateException("Not building!");
        }
        this.isDrawing = false;
        this.byteBuffer.position(0);
        this.byteBuffer.limit(this.func_181664_j() * 4);
    }

    public ByteBuffer getByteBuffer() {
        return this.modeTriangles ? this.byteBufferTriangles : this.byteBuffer;
    }

    public VertexFormat getVertexFormat() {
        return this.vertexFormat;
    }

    public int getVertexCount() {
        return this.modeTriangles ? this.vertexCount / 4 * 6 : this.vertexCount;
    }

    public int getDrawMode() {
        return this.modeTriangles ? 4 : this.drawMode;
    }

    public void putColor4(int argb) {
        for (int i = 0; i < 4; i++) {
            this.putColor(argb, i + 1);
        }
    }

    public void putColorRGB_F4(float red, float green, float blue) {
        for (int i = 0; i < 4; i++) {
            this.putColorRGB_F(red, green, blue, i + 1);
        }
    }

    public void putSprite(TextureAtlasSprite p_putSprite_1_) {
        if (this.animatedSprites != null && p_putSprite_1_ != null && p_putSprite_1_.getAnimationIndex() >= 0) {
            this.animatedSprites.set(p_putSprite_1_.getAnimationIndex());
        }

        if (this.quadSprites != null) {
            int n = this.vertexCount / 4;
            this.quadSprites[n - 1] = p_putSprite_1_;
        }
    }

    public void setSprite(TextureAtlasSprite p_setSprite_1_) {
        if (this.animatedSprites != null && p_setSprite_1_ != null && p_setSprite_1_.getAnimationIndex() >= 0) {
            this.animatedSprites.set(p_setSprite_1_.getAnimationIndex());
        }

        if (this.quadSprites != null) {
            this.quadSprite = p_setSprite_1_;
        }
    }

    public boolean isMultiTexture() {
        return this.quadSprites != null;
    }

    public void drawMultiTexture() {
        if (this.quadSprites != null) {
            int countRegisteredSprites = Config.getMinecraft().getTextureMapBlocks().getCountRegisteredSprites();
            if (this.drawnIcons.length <= countRegisteredSprites) {
                this.drawnIcons = new boolean[countRegisteredSprites + 1];
            }

            Arrays.fill(this.drawnIcons, false);
            int n = 0;
            int n2 = -1;
            int n3 = this.vertexCount / 4;

            for (int i = 0; i < n3; i++) {
                TextureAtlasSprite textureAtlasSprite = this.quadSprites[i];
                if (textureAtlasSprite != null) {
                    int indexInMap = textureAtlasSprite.getIndexInMap();
                    if (!this.drawnIcons[indexInMap]) {
                        if (textureAtlasSprite == TextureUtils.iconGrassSideOverlay) {
                            if (n2 < 0) {
                                n2 = i;
                            }
                        } else {
                            i = this.drawForIcon(textureAtlasSprite, i) - 1;
                            n++;
                            if (this.blockLayer != EnumWorldBlockLayer.TRANSLUCENT) {
                                this.drawnIcons[indexInMap] = true;
                            }
                        }
                    }
                }
            }

            if (n2 >= 0) {
                this.drawForIcon(TextureUtils.iconGrassSideOverlay, n2);
                n++;
            }

            if (n > 0) {
            }
        }
    }

    private int drawForIcon(TextureAtlasSprite p_drawForIcon_1_, int p_drawForIcon_2_) {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, p_drawForIcon_1_.glSpriteTextureId);

        int n;
        try (HeldItemCustomization.SpriteFilter spriteFilter = HeldItemCustomization.isSharpDraw() ? new HeldItemCustomization.SpriteFilter() : null) {
            int n2 = -1;
            int n3 = -1;
            int n4 = this.vertexCount / 4;

            for (int i = p_drawForIcon_2_; i < n4; i++) {
                TextureAtlasSprite textureAtlasSprite = this.quadSprites[i];
                if (textureAtlasSprite == p_drawForIcon_1_) {
                    if (n3 < 0) {
                        n3 = i;
                    }
                } else if (n3 >= 0) {
                    this.draw(n3, i);
                    if (this.blockLayer == EnumWorldBlockLayer.TRANSLUCENT) {
                        return i;
                    }

                    n3 = -1;
                    if (n2 < 0) {
                        n2 = i;
                    }
                }
            }

            if (n3 >= 0) {
                this.draw(n3, n4);
            }

            if (n2 < 0) {
                n2 = n4;
            }

            n = n2;
        }

        return n;
    }

    private void draw(int p_draw_1_, int p_draw_2_) {
        int n = p_draw_2_ - p_draw_1_;
        if (n > 0) {
            int n2 = p_draw_1_ * 4;
            int n3 = n * 4;
            GL11.glDrawArrays(this.drawMode, n2, n3);
        }
    }

    public void setBlockLayer(EnumWorldBlockLayer p_setBlockLayer_1_) {
        this.blockLayer = p_setBlockLayer_1_;
        if (p_setBlockLayer_1_ == null) {
            if (this.quadSprites != null) {
                this.quadSpritesPrev = this.quadSprites;
            }

            this.quadSprites = null;
            this.quadSprite = null;
        }
    }

    private int getBufferQuadSize() {
        return this.rawIntBuffer.capacity() * 4 / (this.vertexFormat.func_181719_f() * 4);
    }

    public RenderEnv getRenderEnv(IBlockState p_getRenderEnv_1_, BlockPos p_getRenderEnv_2_) {
        if (this.renderEnv == null) {
            this.renderEnv = new RenderEnv(p_getRenderEnv_1_, p_getRenderEnv_2_);
            return this.renderEnv;
        }
        this.renderEnv.reset(p_getRenderEnv_1_, p_getRenderEnv_2_);
        return this.renderEnv;
    }

    public boolean isDrawing() {
        return this.isDrawing;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getZOffset() {
        return this.zOffset;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return this.blockLayer;
    }

    public void putColorMultiplierRgba(float p_putColorMultiplierRgba_1_, float p_putColorMultiplierRgba_2_, float p_putColorMultiplierRgba_3_, float p_putColorMultiplierRgba_4_, int p_putColorMultiplierRgba_5_) {
        int colorIndex = this.getColorIndex(p_putColorMultiplierRgba_5_);
        int n = -1;
        if (!this.needsUpdate) {
            n = this.rawIntBuffer.get(colorIndex);
            if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
                int n2 = (int)((n & 0xFF) * p_putColorMultiplierRgba_1_);
                int n3 = (int)((n >> 8 & 0xFF) * p_putColorMultiplierRgba_2_);
                int n4 = (int)((n >> 16 & 0xFF) * p_putColorMultiplierRgba_3_);
                int n5 = (int)((n >> 24 & 0xFF) * p_putColorMultiplierRgba_4_);
                n = n5 << 24 | n4 << 16 | n3 << 8 | n2;
            } else {
                int n6 = (int)((n >> 24 & 0xFF) * p_putColorMultiplierRgba_1_);
                int n7 = (int)((n >> 16 & 0xFF) * p_putColorMultiplierRgba_2_);
                int n8 = (int)((n >> 8 & 0xFF) * p_putColorMultiplierRgba_3_);
                int n9 = (int)((n & 0xFF) * p_putColorMultiplierRgba_4_);
                n = n6 << 24 | n7 << 16 | n8 << 8 | n9;
            }
        }

        this.rawIntBuffer.put(colorIndex, n);
    }

    public void quadsToTriangles() {
        if (this.drawMode == 7) {
            if (this.byteBufferTriangles == null) {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }

            if (this.byteBufferTriangles.capacity() < this.byteBuffer.capacity() * 2) {
                this.byteBufferTriangles = GLAllocation.createDirectByteBuffer(this.byteBuffer.capacity() * 2);
            }

            int nextOffset = this.vertexFormat.getNextOffset();
            int n = this.byteBuffer.limit();
            this.byteBuffer.rewind();
            this.byteBufferTriangles.clear();

            for (int i = 0; i < this.vertexCount; i += 4) {
                this.byteBuffer.limit((i + 3) * nextOffset);
                this.byteBuffer.position(i * nextOffset);
                this.byteBufferTriangles.put(this.byteBuffer);
                this.byteBuffer.limit((i + 1) * nextOffset);
                this.byteBuffer.position(i * nextOffset);
                this.byteBufferTriangles.put(this.byteBuffer);
                this.byteBuffer.limit((i + 2 + 2) * nextOffset);
                this.byteBuffer.position((i + 2) * nextOffset);
                this.byteBufferTriangles.put(this.byteBuffer);
            }

            this.byteBuffer.limit(n);
            this.byteBuffer.rewind();
            this.byteBufferTriangles.flip();
            this.modeTriangles = true;
        }
    }

    public boolean isColorDisabled() {
        return this.needsUpdate;
    }

    public class State {
        private final int[] stateRawBuffer;
        private final VertexFormat stateVertexFormat;
        TextureAtlasSprite[] stateQuadSprites;

        public State(int[] stateRawBuffer, VertexFormat stateVertexFormat, TextureAtlasSprite[] stateQuadSprites) {
            this.stateRawBuffer = stateRawBuffer;
            this.stateVertexFormat = stateVertexFormat;
            this.stateQuadSprites = stateQuadSprites;
        }

        public State(int[] stateRawBuffer, VertexFormat stateVertexFormat) {
            this.stateRawBuffer = stateRawBuffer;
            this.stateVertexFormat = stateVertexFormat;
        }

        public int[] getRawBuffer() {
            return this.stateRawBuffer;
        }

        public int getVertexCount() {
            return this.stateRawBuffer.length / this.stateVertexFormat.func_181719_f();
        }

        public VertexFormat getVertexFormat() {
            return this.stateVertexFormat;
        }
    }
}
