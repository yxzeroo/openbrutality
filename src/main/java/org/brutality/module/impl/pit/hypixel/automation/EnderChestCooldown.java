package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Generated;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;

public class EnderChestCooldown extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    public static final double amount = 3000.0;
    private static EnderChestCooldown instance;
    private boolean flag = false;
    private BlockPos blockPos = null;
    private final Map<BlockPos, Long> lookupMap = new HashMap<>();
    private final BooleanSetting showSecondsSuffixSetting = new BooleanSetting("Show Seconds Suffix", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Time Format", this, "##.#", Arrays.asList("##", "##.#"));
    private static final long timestamp = 1162690034042667008L;

    private void process3() {
        for (Entry entry : this.lookupMap.entrySet()) {
            BlockPos key = (BlockPos)entry.getKey();
            double d = this.computeDouble(key);
            if (!(d <= 0.0)) {
                this.handleBlockPos(key, d);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                this.process3();
            } else if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() == EventState.PRE) {
                    if (mc.thePlayer != null && mc.theWorld != null) {
                        this.process();
                        boolean flag = this.checkState();
                        if (flag && !this.flag) {
                            this.blockPos = this.computeBlockPos();
                        } else if (!flag && this.flag && this.blockPos != null) {
                            this.lookupMap.put(this.blockPos, System.currentTimeMillis());
                            this.blockPos = null;
                        }

                        this.flag = flag;
                    }
                }
            }
        }
    }

    private Color computeColor(double d) {
        float f = (float)Math.min(d / 3.0, 1.0);
        if (f < 0.5F) {
            float f2 = f * 2.0F;
            return new Color(1.0F, f2, 0.0F);
        }
        float f3 = (f - 0.5F) * 2.0F;
        return new Color(1.0F - f3, 1.0F, 0.0F);
    }

    @Override
    public void onDisable() {
        this.lookupMap.clear();
        this.blockPos = null;
        this.flag = false;
        super.onDisable();
    }

    private BlockPos computeBlockPos() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            if (mc.theWorld.getTileEntity(blockPos) instanceof TileEntityEnderChest) {
                return blockPos;
            }
        }

        Vec3 vec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        Vec3 look = mc.thePlayer.getLook(1.0F);
        Vec3 vec2 = vec.addVector(look.xCoord * 5.0, look.yCoord * 5.0, look.zCoord * 5.0);
        MovingObjectPosition hit = mc.theWorld.rayTraceBlocks(vec, vec2);
        if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK) {
            BlockPos blockPos2 = hit.getBlockPos();
            if (mc.theWorld.getTileEntity(blockPos2) instanceof TileEntityEnderChest) {
                return blockPos2;
            }
        }

        return null;
    }

    @Generated
    public static EnderChestCooldown getInstance() {
        return instance;
    }

    private boolean checkState() {
        if (!(mc.currentScreen instanceof GuiChest)) {
            return false;
        }
        try {
            ContainerChest inventorySlots = (ContainerChest)((GuiChest)mc.currentScreen).inventorySlots;
            return inventorySlots.getLowerChestInventory() instanceof TileEntityEnderChest;
        } catch (Exception e) {
            return false;
        }
    }

    private String computeString(double d) {
        String str = this.modeSetting.matchesString2("##")
            ? String.format(Locale.ROOT, "%.0f", Math.max(0.0, d))
            : String.format(Locale.ROOT, "%.1f", Math.max(0.0, d));
        return str + (this.showSecondsSuffixSetting.isEnabled() ? "s" : "");
    }

    public boolean matchesBlockPos(BlockPos pos) {
        return this.computeDouble(pos) > 0.0;
    }

    public double computeDouble(BlockPos pos) {
        Long val = this.lookupMap.get(pos);
        if (val == null) {
            return -1.0;
        }
        long l = System.currentTimeMillis() - val;
        double d = (3000.0 - l) / 1000.0;
        return d > 0.0 ? d : -1.0;
    }

    private void process() {
        long now = System.currentTimeMillis();
        Iterator it = this.lookupMap.entrySet().iterator();

        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            if (now - (Long)entry.getValue() >= 3000.0) {
                it.remove();
            }
        }
    }

    public EnderChestCooldown() {
        super("EnderChestCooldown", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.showSecondsSuffixSetting, this.modeSetting});
        instance = this;
        this.handleFlag3(true);
    }

    private void handleBlockPos(BlockPos pos, double d) {
        double d2 = pos.getX() - mc.getRenderManager().renderPosX + 0.5;
        double d3 = pos.getY() - mc.getRenderManager().renderPosY + 1.35;
        double d4 = pos.getZ() - mc.getRenderManager().renderPosZ + 0.5;
        String str = this.computeString(d);
        Color color = this.computeColor(d);
        int n = (int)timestamp | color.getRed() << 16 | color.getGreen() << 8;
        int blue = color.getBlue();
        int n2 = blue + (n & ~blue);
        GlStateManager.pushMatrix();
        GlStateManager.translate(d2, d3, d4);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-0.025F, -0.025F, 0.025F);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        int textWidth = computeFontRenderer(EnderChestCooldown.class).getStringWidth(str);
        computeFontRenderer(EnderChestCooldown.class).drawStringWithShadow(str, -textWidth / 2.0F, 0.0F, n2);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }
}
