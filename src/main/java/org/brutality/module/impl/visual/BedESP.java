package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.combat.IsSetHelper;
import org.brutality.utils.render.BedESPHelper;
import org.brutality.utils.render.OnBlockChangeHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.opengl.GL11;

public class BedESP extends Module {
    private static final List<String> entries = Arrays.asList("Static", "Gradient", "Rainbow");
    private static final int count = 5;
    private static final float factor = 0.2F;
    private static final float factor2 = 0.2F;
    private final ModeSetting staticValueSetting = new ModeSetting("Color Mode", this, "Static", entries);
    private final ColorSetting colorSetting = new ColorSetting("Color", this, 0.0F, 0.67F, 1.0F, 0.3F);
    private final ColorSetting color2Setting = new ColorSetting("Color 2", this, 0.6F, 0.67F, 1.0F, 0.3F);
    private final NumberSetting gradientSpeedSetting = new NumberSetting("Gradient Speed", this, 1.0, 0.1, 8.0, 0.1);
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 40.0, 2.0, 200.0, 2.0);
    private final NumberSetting scanSpeedSetting = new NumberSetting("Scan Speed", this, 8.0, 1.0, 32.0, 1.0);
    private final BooleanSetting onlyRenderFirstBedSetting = new BooleanSetting("Only Render First Bed", this, false);
    private final BooleanSetting renderFullBlockSetting = new BooleanSetting("Render Full Block", this, false);
    private final BooleanSetting exposedOutlineSetting = new BooleanSetting("Exposed Outline", this, true);
    private final ColorSetting exposedOutlineColorSetting = new ColorSetting("Exposed Outline Color", this, 0.12F, 0.75F, 1.0F, 1.0F);
    private final BooleanSetting showDefenseLayersSetting = new BooleanSetting("Show Defense Layers", this, true);
    private final BooleanSetting showBreakToolsSetting = new BooleanSetting("Show Break Tools", this, false);
    private final BooleanSetting showDefenseCountsSetting = new BooleanSetting("Show Defense Counts", this, true);
    private final NumberSetting defenseHeightSetting = new NumberSetting("Defense Height", this, 0.6, 0.1, 3.0, 0.1);
    private final NumberSetting defenseScaleSetting = new NumberSetting("Defense Scale", this, 1.0, 0.1, 2.0, 0.05);
    private final BooleanSetting defenseAutoScaleSetting = new BooleanSetting("Defense Auto Scale", this, false);
    private final BooleanSetting showOwnBedSetting = new BooleanSetting("Show Own Bed", this, false);
    private final List<BlockPos[]> entries2 = new java.util.ArrayList<>();
    private final Map<Long, BedESP.Entry> lookupMap = new HashMap<>();
    private static final long timestamp = 3000L;

    private void handleBlockPosArray2(BlockPos[] posArray, AxisAlignedBB box) {
        BedESP.Entry entry = this.lookupMap.get(posArray[0].toLong());
        if (entry != null && !entry.entries.isEmpty()) {
            double d = (box.minX + box.maxX) * 0.5;
            double d2 = box.maxY + this.defenseHeightSetting.computeDouble();
            double d3 = (box.minZ + box.maxZ) * 0.5;
            double d4 = d - mc.getRenderManager().viewerPosX;
            double d5 = d2 - mc.getRenderManager().viewerPosY;
            double d6 = d3 - mc.getRenderManager().viewerPosZ;
            float f = (float)this.defenseScaleSetting.computeDouble() * 0.02F;
            if (this.defenseAutoScaleSetting.isEnabled()) {
                float dist = (float)Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
                f = Math.max(f, f * (dist / 8.0F));
            }

            FontRenderer fontRenderer = computeFontRenderer(BedESP.class);
            GlStateManager.pushMatrix();
            GlStateManager.translate(d4, d5, d6);
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f, -f, f);
            GlStateManager.disableDepth();
            int n = 0;

            for (String str : entry.entries) {
                int textWidth = fontRenderer.getStringWidth(str);
                fontRenderer.drawStringWithShadow(str, -textWidth / 2.0F, n, -1);
                n += fontRenderer.FONT_HEIGHT + 1;
            }

            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }
    }

    private Block computeBlock(String str) {
        if (str.contains("obsidian")) {
            return Blocks.obsidian;
        }
        if (str.contains("wool")) {
            return Blocks.wool;
        }
        if (str.contains("clay")) {
            return Blocks.stained_hardened_clay;
        }
        if (str.contains("wood")) {
            return Blocks.planks;
        }
        if (str.contains("end stone")) {
            return Blocks.end_stone;
        }
        if (str.contains("glass")) {
            return Blocks.glass;
        }
        if (str.contains("water")) {
            return Blocks.water;
        }
        return str.contains("lava") ? Blocks.lava : null;
    }

    @Override
    public void onEnable() {
        OnBlockChangeHelper instance = OnBlockChangeHelper.getInstance();
        instance.process2();
        instance.clear();
        instance.process3();
        this.entries2.clear();
        this.lookupMap.clear();
    }

    private String computeString(String str) {
        String str2 = str.toLowerCase();
        if (str2.contains("obsidian")) {
            return "Diamond Pickaxe";
        }
        if (str2.contains("wool")) {
            return "Shears";
        }
        if (str2.contains("wood")) {
            return "Iron Axe";
        }
        if (str2.contains("clay")) {
            return "Iron Pickaxe";
        }
        if (str2.contains("end stone")) {
            return "Iron Pickaxe";
        }
        if (str2.contains("glass")) {
            return "Iron Pickaxe";
        }
        ItemStack[] stacks = new ItemStack[]{
            new ItemStack(Items.iron_pickaxe),
            new ItemStack(Items.iron_axe),
            new ItemStack(Items.iron_shovel),
            new ItemStack(Items.shears),
            new ItemStack(Items.iron_sword)
        };
        Block block = this.computeBlock(str2);
        float f = 1.0F;
        String str3 = "Iron Pickaxe";

        for (ItemStack stack : stacks) {
            if (block == null) {
                break;
            }

            float f2 = GetCurrentScreenHelper.computeFloat(stack, block);
            if (f2 > f) {
                f = f2;
                Item item = stack.getItem();
                str3 = item.getItemStackDisplayName(stack);
            }
        }

        return str3;
    }

    private List<BlockPos[]> computeList() {
        java.util.ArrayList list = new java.util.ArrayList();
        OnBlockChangeHelper instance = OnBlockChangeHelper.getInstance();
        double d = this.rangeSetting.computeDouble() * this.rangeSetting.computeDouble();
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        for (Map.Entry entry : instance.computeIterable()) {
            for (BlockPos pos : ((Iterable<BlockPos>)(Object)((Set)entry.getValue()))) {
                if (this.showOwnBedSetting.isEnabled() || !IsSetHelper.matchesBlockPos(pos)) {
                    double d2 = pos.getX() + 0.5 - x;
                    double d3 = pos.getY() + 0.5 - y;
                    double d4 = pos.getZ() + 0.5 - z;
                    if (!(d2 * d2 + d3 * d3 + d4 * d4 > d)) {
                        BlockPos[] posArray = this.computeBlockPosArray(pos);
                        if (posArray != null
                            && this.matchesBlockPosArray(posArray)
                            && (this.showOwnBedSetting.isEnabled() || !IsSetHelper.matchesBlockPos(posArray[0]) && !IsSetHelper.matchesBlockPos(posArray[1]))) {
                            list.add(posArray);
                        }
                    }
                }
            }
        }

        if (this.onlyRenderFirstBedSetting.isEnabled() && list.size() > 1) {
            BlockPos[] posArray2 = null;
            double MAX_VALUE = Double.MAX_VALUE;

            for (BlockPos[] posArray3 : ((Iterable<BlockPos[]>)(Object)(list))) {
                double d5 = posArray3[0].getX() + 0.5 - x;
                double d6 = posArray3[0].getY() + 0.5 - y;
                double d7 = posArray3[0].getZ() + 0.5 - z;
                double d8 = d5 * d5 + d6 * d6 + d7 * d7;
                if (d8 < MAX_VALUE) {
                    MAX_VALUE = d8;
                    posArray2 = posArray3;
                }
            }

            return posArray2 == null ? Collections.emptyList() : Collections.singletonList(posArray2);
        }
        return list;
    }

    private void handleBlockPosArray(BlockPos[] posArray, AxisAlignedBB box) {
        int n = this.computeInt();
        float f = (n >> 24 & 0xFF) / 255.0F;
        float f2 = (n >> 16 & 0xFF) / 255.0F;
        float f3 = (n >> 8 & 0xFF) / 255.0F;
        float f4 = (n & 0xFF) / 255.0F;
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glLineWidth(2.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(f2, f3, f4, f);
        ResetColorHelper.handleAxisAlignedBB3(box);
        if (this.exposedOutlineSetting.isEnabled() && this.matchesBlockPosArray2(posArray)) {
            Color color = this.exposedOutlineColorSetting.computeColor();
            GL11.glLineWidth(3.0F);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, color.getAlpha() / 255.0F);
            ResetColorHelper.handleAxisAlignedBB2(box);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            var list = this.computeList();
            this.entries2.clear();
            this.entries2.addAll(list);
            if (!this.showDefenseLayersSetting.isEnabled()) {
                this.lookupMap.clear();
            } else {
                HashSet set = new HashSet();

                for (BlockPos[] posArray : this.entries2) {
                    long l = posArray[0].toLong();
                    set.add(l);
                    this.lookupMap.put(l, this.computeDefenseSnapshot(posArray[0], posArray[1]));
                }

                this.lookupMap.keySet().retainAll(set);
            }
        } else {
            this.entries2.clear();
            this.lookupMap.clear();
        }
    }

    private void process3() {
        if (mc.theWorld != null && mc.thePlayer != null && !this.entries2.isEmpty()) {
            float f = this.renderFullBlockSetting.isEnabled() ? 1.0F : 0.5625F;

            for (BlockPos[] posArray : this.entries2) {
                AxisAlignedBB box = this.computeAxisAlignedBB(posArray[0], posArray[1], f);
                if (ResetColorHelper.matchesAxisAlignedBB(box)) {
                    this.handleBlockPosArray(posArray, box);
                    if (this.showDefenseLayersSetting.isEnabled()) {
                        this.handleBlockPosArray2(posArray, box);
                    }
                }
            }
        }
    }

    @Override
    public String getSuffix() {
        return String.valueOf(OnBlockChangeHelper.getInstance().computeInt());
    }

    public int computeInt2() {
        return this.checkState44() ? (int)this.scanSpeedSetting.computeDouble() : 0;
    }

    private BlockPos[] computeBlockPosArray(BlockPos pos) {
        IBlockState blockState = mc.theWorld.getBlockState(pos);
        if (!(blockState.getBlock() instanceof BlockBed)) {
            return null;
        }
        EnumFacing val = blockState.getValue(BlockBed.FACING);
        return new BlockPos[]{new BlockPos(pos), pos.offset(val)};
    }

    private static EnumFacing computeEnumFacing(BlockPos pos, BlockPos pos2) {
        int n = pos2.getX() - pos.getX();
        int n2 = pos2.getZ() - pos.getZ();

        for (EnumFacing facing : Plane.HORIZONTAL) {
            if (facing.getFrontOffsetX() == n && facing.getFrontOffsetZ() == n2) {
                return facing;
            }
        }

        return null;
    }

    private AxisAlignedBB computeAxisAlignedBB(BlockPos pos, BlockPos pos2, float f) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        double d = y + f;
        if (pos.getX() != pos2.getX()) {
            return pos.getX() > pos2.getX()
                ? new AxisAlignedBB(x - 1.0, y, z, x + 1.0, d, z + 1.0)
                : new AxisAlignedBB(x, y, z, x + 2.0, d, z + 1.0);
        }
        return pos.getZ() > pos2.getZ()
            ? new AxisAlignedBB(x, y, z - 1.0, x + 1.0, d, z + 1.0)
            : new AxisAlignedBB(x, y, z, x + 1.0, d, z + 2.0);
    }

    private boolean matchesBlockPosArray(BlockPos[] posArray) {
        IBlockState blockState = mc.theWorld.getBlockState(posArray[0]);
        IBlockState blockState2 = mc.theWorld.getBlockState(posArray[1]);
        return blockState != null && blockState.getBlock() instanceof BlockBed || blockState2 != null && blockState2.getBlock() instanceof BlockBed;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPacketReceive) {
                Packet packet = ((EventPacketReceive)event).getPacket();
                BedESPHelper.handlePacket(packet);
            } else if (event instanceof ChatEvent) {
                this.onChatReceived((ChatEvent)event);
            } else if (event instanceof RenderEvent4 renderEvent4) {
                if (renderEvent4.world != renderEvent4.lastWorld) {
                    IsSetHelper.reset();
                }
            } else if (event instanceof EventUpdate event2) {
                BedESPHelper.onEventUpdate(event2);
                if (event2.getState() == EventState.PRE) {
                    IsSetHelper.tick();
                    this.process();
                }
            } else {
                if (event instanceof GetPartialTicksEvent) {
                    this.process3();
                }
            }
        }
    }

    private String computeString(IBlockState blockState) {
        Block block = blockState.getBlock();
        if (block == Blocks.obsidian) {
            return "Obsidian";
        }
        if (block == Blocks.end_stone) {
            return "End Stone";
        }
        if (block == Blocks.wool) {
            return "Wool";
        }
        if (block == Blocks.stained_hardened_clay) {
            return "Clay";
        }
        if (block == Blocks.planks || block == Blocks.log || block == Blocks.log2) {
            return "Wood";
        }
        if (block == Blocks.glass) {
            return "Glass";
        }
        if (block == Blocks.lava || block == Blocks.flowing_lava) {
            return "Lava";
        }
        if (block != Blocks.water && block != Blocks.flowing_water) {
            String localizedName = block.getLocalizedName();
            if (localizedName != null && !localizedName.isEmpty()) {
                return localizedName;
            }
            var nameForObject = Block.blockRegistry.getNameForObject(block);
            return nameForObject == null ? "Block" : nameForObject.toString();
        }
        return "Water";
    }

    public int computeInt() {
        String text = this.staticValueSetting.getText();
        if ("Gradient".equalsIgnoreCase(text)) {
            Color color = this.colorSetting.computeColor();
            Color color2 = this.color2Setting.computeColor();
            double d = Math.sin(System.currentTimeMillis() * this.gradientSpeedSetting.computeDouble() / 1000.0) * 0.5 + 0.5;
            int n = (int)(color.getRed() * d + color2.getRed() * (1.0 - d));
            int n2 = (int)(color.getGreen() * d + color2.getGreen() * (1.0 - d));
            int n3 = (int)(color.getBlue() * d + color2.getBlue() * (1.0 - d));
            int n4 = (int)(color.getAlpha() * d + color2.getAlpha() * (1.0 - d));
            return GetCurrentScreenHelper.computeInt2(n3 + ((n2 << 8) + (n << 16 & ~(n2 << 8)) & ~n3), n4);
        }
        if ("Rainbow".equalsIgnoreCase(text)) {
            float f = (float)(System.currentTimeMillis() % timestamp) / 3000.0F;
            Color hsbColor = Color.getHSBColor(f, 0.9F, 1.0F);
            return GetCurrentScreenHelper.computeInt2(hsbColor.getRGB(), this.colorSetting.computeColor().getAlpha());
        }
        return this.colorSetting.computeColor().getRGB();
    }

    private BedESP.Entry computeDefenseSnapshot(BlockPos pos, BlockPos pos2) {
        EnumFacing facing = computeEnumFacing(pos, pos2);
        if (facing == null) {
            return BedESP.Entry.instance;
        }
        HashMap<String, Integer> map = new HashMap<>();
        int n = 0;

        for (int i = 1; i <= 5; i++) {
            BedESP.Entry2 entry2 = this.computeEntry2(pos, facing, i);
            if (entry2.count2 != 0) {
                float f = (float)entry2.count / entry2.count2;
                if (f > 0.2F) {
                    if (++n >= 2) {
                        break;
                    }
                } else {
                    n = 0;

                    for (Map.Entry entry : entry2.lookupMap.entrySet()) {
                        if ((float)((Integer)entry.getValue()).intValue() / entry2.count2 >= 0.2F) {
                            map.put((String)entry.getKey(), map.getOrDefault(entry.getKey(), 0) + (Integer)entry.getValue());
                        }
                    }
                }
            }
        }

        if (map.isEmpty()) {
            return BedESP.Entry.instance;
        }
        java.util.ArrayList list = new java.util.ArrayList();
        if (this.showBreakToolsSetting.isEnabled()) {
            HashSet set = new HashSet();

            for (String str : ((Iterable<String>)(Object)(map.keySet()))) {
                set.add(this.computeString(str));
            }

            java.util.ArrayList list2 = new java.util.ArrayList(set);
            Collections.sort(list2);
            list.addAll(list2);
        } else {
            java.util.ArrayList<Map.Entry<String, Integer>> list3 = new java.util.ArrayList<>(map.entrySet());
            list3.sort((entry3, entry4) -> Integer.compare((Integer)entry4.getValue(), (Integer)entry3.getValue()));

            for (Map.Entry entry5 : ((Iterable<Map.Entry>)(Object)(list3))) {
                if (this.showDefenseCountsSetting.isEnabled()) {
                    list.add((String)entry5.getKey() + " x" + entry5.getValue());
                } else {
                    list.add((String)entry5.getKey());
                }
            }
        }

        return new BedESP.Entry(list);
    }

    private boolean matchesBlockPosArray2(BlockPos[] posArray) {
        for (BlockPos pos : posArray) {
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos pos2 = pos.offset(facing);
                if (mc.theWorld.getBlockState(pos2).getBlock() == Blocks.air) {
                    return true;
                }
            }
        }

        return false;
    }

    private void onChatReceived(ChatEvent chatEvent) {
        if (mc.thePlayer != null && mc.theWorld != null && chatEvent.message != null) {
            String str = this.stripColor(chatEvent.message.getUnformattedText());
            if (str != null) {
                if (str.startsWith(" ") && str.contains("Protect your bed and destroy the enemy beds.")) {
                    IsSetHelper.process();
                }
            }
        }
    }

    private String stripColor(String str) {
        return str == null ? null : str.replaceAll("(?i)§[0-9A-FK-OR]", "");
    }

    public BedESP() {
        super("BedESP", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.staticValueSetting,
                this.colorSetting,
                this.color2Setting,
                this.gradientSpeedSetting,
                this.rangeSetting,
                this.scanSpeedSetting,
                this.onlyRenderFirstBedSetting,
                this.renderFullBlockSetting,
                this.exposedOutlineSetting,
                this.exposedOutlineColorSetting,
                this.showDefenseLayersSetting,
                this.showBreakToolsSetting,
                this.showDefenseCountsSetting,
                this.defenseHeightSetting,
                this.defenseScaleSetting,
                this.defenseAutoScaleSetting,
                this.showOwnBedSetting
            }
        );
    }

    @Override
    public void onDisable() {
        OnBlockChangeHelper instance = OnBlockChangeHelper.getInstance();
        instance.process();
        this.entries2.clear();
        this.lookupMap.clear();
    }

    private BedESP.Entry2 computeEntry2(BlockPos pos, EnumFacing facing, int n) {
        HashMap<String, Integer> map = new HashMap<>();
        int n2 = 0;
        int n3 = 0;
        int n4 = n + 1;

        for (int i = 0; i <= n; i++) {
            for (int j = ~n4 + 1; j <= n4; j++) {
                for (int k = -n4; k <= n4; k++) {
                    int n5 = pos.getX() + facing.getFrontOffsetX() * k + (facing.getAxis() == Axis.Z ? j : 0);
                    int n6 = pos.getZ() + facing.getFrontOffsetZ() * k + (facing.getAxis() == Axis.X ? j : 0);
                    int n7 = pos.getY() + i;
                    BlockPos pos2 = new BlockPos(n5, n7, n6);
                    IBlockState blockState = mc.theWorld.getBlockState(pos2);
                    if (blockState != null && blockState.getBlock() != Blocks.air && !(blockState.getBlock() instanceof BlockBed)) {
                        String str = this.computeString(blockState);
                        map.put(str, map.getOrDefault(str, 0) + 1);
                        n3++;
                    } else {
                        n2++;
                        n3++;
                    }
                }
            }
        }

        return new BedESP.Entry2(map, n2, n3);
    }

    public static final class Entry {
        public static final BedESP.Entry instance = new BedESP.Entry(Collections.emptyList());
        public final List<String> entries;

        public Entry(List<String> entries) {
            this.entries = entries;
        }
    }

    public static final class Entry2 {
        public final Map<String, Integer> lookupMap;
        public final int count;
        public final int count2;

        public Entry2(Map<String, Integer> lookupMap, int count, int n) {
            this.lookupMap = lookupMap;
            this.count = count;
            this.count2 = n;
        }
    }
}
