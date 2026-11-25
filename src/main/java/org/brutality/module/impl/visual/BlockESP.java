package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.render.ResetColorHelper;

public class BlockESP extends Module {
    private final BooleanSetting outlineSetting;
    private final BooleanSetting fillSetting;
    private final BooleanSetting barrierBlocksOnlySetting;
    private final ModeSetting orangeSetting;
    private final NumberSetting fillOpacitySetting;
    private final NumberSetting rangeSetting;
    private final NumberSetting updateRateMsSetting;
    private final Set<BlockPos> itemSet = new HashSet<>();
    private long lastUpdateTime = 0L;
    private int count;
    private static final Color color = new Color(255, 0, 0);
    private static final Color color2 = new Color(0, 0, 255);
    private static final Color color3 = new Color(0, 255, 0);
    private static final Color color4 = new Color(255, 255, 0);
    private static final Color color5 = new Color(128, 0, 128);
    private static final Color color6 = new Color(255, 105, 180);
    private static final Color color7 = new Color(255, 165, 0);
    private static final Color color8 = new Color(255, 255, 255);
    private static final long timestamp = -1118302680926650268L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent && mc.theWorld != null && mc.thePlayer != null) {
                float renderPartialTicks = mc.timer.renderPartialTicks;
                if (System.currentTimeMillis() - this.lastUpdateTime > this.updateRateMsSetting.computeDouble()) {
                    this.count = (int)this.updateRateMsSetting.computeDouble();
                    this.process();
                    this.lastUpdateTime = System.currentTimeMillis();
                }

                this.handleFloat(renderPartialTicks);
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.itemSet.clear();
        this.lastUpdateTime = 0L;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.itemSet.clear();
    }

    private Color computeColor() {
        String text = this.orangeSetting.getText();
        switch (text) {
            case "Rainbow":
                return new Color(this.computeInt(0));
            case "Red":
                return color;
            case "Blue":
                return color2;
            case "Green":
                return color3;
            case "Yellow":
                return color4;
            case "Purple":
                return color5;
            case "Pink":
                return color6;
            case "Orange":
                return color7;
            default:
                return color8;
        }
    }

    public int computeInt(int n) {
        double d = Math.ceil((System.currentTimeMillis() * 0.1 + n * 100) / 100.0);
        d %= 360.0;
        return Color.getHSBColor((float)(d / 360.0), 0.8F, 0.7F).getRGB();
    }

    private void handleFloat(float f) {
        Color color = this.computeColor();
        Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)this.fillOpacitySetting.computeDouble());

        for (BlockPos pos : this.itemSet) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            Color color3 = color2;
            if (!this.barrierBlocksOnlySetting.isEnabled()) {
                color3 = this.computeColor(block, color2);
            }

            ResetColorHelper.handleBlockPos2(pos, color3, this.outlineSetting.isEnabled(), this.fillSetting.isEnabled(), 1.0F);
        }
    }

    public BlockESP() {
        super("BlockESP", 0, Category.VISUAL);
        this.count = (int)timestamp;
        this.outlineSetting = new BooleanSetting("Outline", this, true);
        this.fillSetting = new BooleanSetting("Fill", this, false);
        this.barrierBlocksOnlySetting = new BooleanSetting("Barrier Blocks Only", this, true);
        this.orangeSetting = new ModeSetting("Color", this, "Rainbow", Arrays.asList("Rainbow", "Red", "Blue", "Green", "Yellow", "Purple", "Pink", "Orange"));
        this.fillOpacitySetting = new NumberSetting("Fill Opacity", this, 50.0, 0.0, 255.0, 5.0);
        this.rangeSetting = new NumberSetting("Range", this, 50.0, 10.0, 100.0, 5.0);
        this.updateRateMsSetting = new NumberSetting("Update Rate (ms)", this, 100.0, 50.0, 500.0, 50.0);
        this.handleSettingArray(
            new Setting[]{
                this.outlineSetting,
                this.fillSetting,
                this.barrierBlocksOnlySetting,
                this.orangeSetting,
                this.fillOpacitySetting,
                this.rangeSetting,
                this.updateRateMsSetting
            }
        );
    }

    private Color computeColor(Block block, Color color) {
        if (block == Blocks.barrier) {
            return new Color(255, 0, 0, color.getAlpha());
        }
        if (block == Blocks.bedrock) {
            return new Color(64, 64, 64, color.getAlpha());
        }
        if (block == Blocks.diamond_ore) {
            return new Color(0, 255, 255, color.getAlpha());
        }
        if (block == Blocks.gold_ore) {
            return new Color(255, 215, 0, color.getAlpha());
        }
        if (block == Blocks.iron_ore) {
            return new Color(192, 192, 192, color.getAlpha());
        }
        return block == Blocks.coal_ore ? new Color(64, 64, 64, color.getAlpha()) : color;
    }

    private void process() {
        this.itemSet.clear();
        int x = (int)mc.thePlayer.posX;
        int y = (int)mc.thePlayer.posY;
        int z = (int)mc.thePlayer.posZ;
        int n = (int)this.rangeSetting.computeDouble();
        int n2 = x - n >> 4;
        int n3 = x + n >> 4;
        int n4 = z - n >> 4;
        int n5 = z + n >> 4;
        int n6 = Math.max(0, y - n);
        int n7 = Math.min(255, y + n);
        if (this.barrierBlocksOnlySetting.isEnabled()) {
            for (int i = n2; i <= n3; i++) {
                for (int j = n4; j <= n5; j++) {
                    if (mc.theWorld.getChunkProvider().chunkExists(i, j)) {
                        int n8 = i << 4;
                        int n9 = j << 4;

                        for (int k = n8; k < n8 + 16; k++) {
                            for (int idx = n9; idx < n9 + 16; idx++) {
                                for (int idx2 = n6; idx2 <= n7; idx2++) {
                                    BlockPos pos = new BlockPos(k, idx2, idx);
                                    IBlockState blockState = mc.theWorld.getBlockState(pos);
                                    if (blockState.getBlock() == Blocks.barrier) {
                                        this.itemSet.add(pos);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int idx3 = n2; idx3 <= n3; idx3++) {
                for (int idx4 = n4; idx4 <= n5; idx4++) {
                    if (mc.theWorld.getChunkProvider().chunkExists(idx3, idx4)) {
                        int n10 = idx3 << 4;
                        int n11 = idx4 << 4;

                        for (int idx5 = n10; idx5 < n10 + 16; idx5++) {
                            for (int idx6 = n11; idx6 < n11 + 16; idx6++) {
                                for (int idx7 = n6; idx7 <= n7; idx7++) {
                                    BlockPos pos2 = new BlockPos(idx5, idx7, idx6);
                                    IBlockState blockState2 = mc.theWorld.getBlockState(pos2);
                                    if (blockState2.getBlock() != Blocks.air) {
                                        double dist = mc.thePlayer.getDistance(idx5 + 0.5, idx7 + 0.5, idx6 + 0.5);
                                        if (dist <= this.rangeSetting.computeDouble()) {
                                            this.itemSet.add(pos2);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
