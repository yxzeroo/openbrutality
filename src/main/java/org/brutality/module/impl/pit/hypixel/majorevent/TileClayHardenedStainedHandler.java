package org.brutality.module.impl.pit.hypixel.majorevent;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Module;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.StopwatchTimer;
import org.brutality.utils.render.ResetColorHelper;

public final class TileClayHardenedStainedHandler {
    private final BooleanSetting espCakesSetting;
    private final BooleanSetting espCherriesSetting;
    private final BooleanSetting espChocChipsSetting;
    private final NumberSetting outlineOpacitySetting;
    private final NumberSetting fillOpacitySetting;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Color color = new Color(255, 235, 190, 200);
    private static final Color color2 = new Color(220, 50, 50, 200);
    private static final Color color3 = new Color(40, 20, 10, 200);
    private static final int count = 7;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final ArrayList<Vec3> entries = new ArrayList<>();
    private final ArrayList<Vec3> entries2 = new ArrayList<>();
    private final ArrayList<Vec3> entries3 = new ArrayList<>();
    private static final long timestamp = 1000L;

    private boolean matchesBlockPos(BlockPos pos) {
        BlockPos[] posArray = new BlockPos[]{pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down()};

        for (BlockPos pos2 : posArray) {
            if (mc.theWorld.getBlockState(pos2).getBlock() == Blocks.cake) {
                return true;
            }
        }

        return false;
    }

    private void handleBlockPos(BlockPos pos, Color color) {
        if (this.fillOpacitySetting.computeDouble() > 0.0) {
            ResetColorHelper.handleBlockPos2(
                pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(this.fillOpacitySetting.computeDouble() * 2.55)), false, true, 1.5F
            );
        }

        if (this.outlineOpacitySetting.computeDouble() > 0.0) {
            ResetColorHelper.handleBlockPos2(
                pos, new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(this.outlineOpacitySetting.computeDouble() * 2.55)), true, false, 1.5F
            );
        }
    }

    public void onDisable() {
        this.entries.clear();
        this.entries2.clear();
        this.entries3.clear();
        this.utilHelper8.reset();
    }

    public void onEvent(Event event) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            if (event instanceof EventRender3D) {
                if (this.utilHelper8.matchesLong(timestamp)) {
                    this.process();
                    this.utilHelper8.reset();
                }

                if (this.espCakesSetting.isEnabled()) {
                    for (Vec3 vec : this.entries) {
                        this.handleBlockPos(new BlockPos(vec.xCoord - 0.5, vec.yCoord, vec.zCoord - 0.5), color);
                    }
                }

                if (this.espCherriesSetting.isEnabled()) {
                    for (Vec3 vec2 : this.entries2) {
                        this.handleBlockPos(new BlockPos(vec2.xCoord - 0.5, vec2.yCoord, vec2.zCoord - 0.5), color2);
                    }
                }

                if (this.espChocChipsSetting.isEnabled()) {
                    for (Vec3 vec3 : this.entries3) {
                        this.handleBlockPos(new BlockPos(vec3.xCoord - 0.5, vec3.yCoord, vec3.zCoord - 0.5), color3);
                    }
                }
            }
        }
    }

    private void process() {
        this.entries.clear();
        this.entries2.clear();
        this.entries3.clear();
        int x = (int)mc.thePlayer.posX;
        int y = (int)mc.thePlayer.posY;
        int z = (int)mc.thePlayer.posZ;

        for (int i = x - 7; i < x + 7; i++) {
            for (int j = y - 7; j < y + 7; j++) {
                for (int k = z - 7; k < z + 7; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    if (mc.theWorld.isBlockLoaded(pos)) {
                        if (mc.theWorld.getBlockState(pos).getBlock() == Blocks.cake) {
                            this.entries.add(new Vec3(i + 0.5, j, k + 0.5));
                        } else {
                            String unlocalizedName = mc.theWorld.getBlockState(pos).getBlock().getUnlocalizedName();
                            if (unlocalizedName.equals("tile.clayHardenedStained")) {
                                int metaFromState = mc.theWorld.getBlockState(pos).getBlock().getMetaFromState(mc.theWorld.getBlockState(pos));
                                if (metaFromState == 14 && this.matchesBlockPos(pos)) {
                                    this.entries2.add(new Vec3(i + 0.5, j, k + 0.5));
                                } else if (metaFromState == 15 && this.matchesBlockPos(pos)) {
                                    this.entries3.add(new Vec3(i + 0.5, j, k + 0.5));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public TileClayHardenedStainedHandler(Module module) {
        this.espCakesSetting = new BooleanSetting("ESP Cakes", module, true);
        this.espCherriesSetting = new BooleanSetting("ESP Cherries", module, true);
        this.espChocChipsSetting = new BooleanSetting("ESP Choc Chips", module, true);
        this.outlineOpacitySetting = new NumberSetting("Outline Opacity", module, 100.0, 0.0, 100.0, 1.0);
        this.fillOpacitySetting = new NumberSetting("Fill Opacity", module, 35.0, 0.0, 100.0, 1.0);
        module.handleSettingArray(this.espCakesSetting, this.espCherriesSetting, this.espChocChipsSetting, this.outlineOpacitySetting, this.fillOpacitySetting);
    }

    public void onEnable() {
        this.onDisable();
    }
}
