package org.brutality.module.impl.player;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.Brutality;
import org.brutality.automation.hunting.FindHandler;
import org.brutality.automation.hunting.HuntingHandler2;
import org.brutality.core.CoreHandler2;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;
import org.brutality.utils.combat.IsSetHelper;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.player.PlayerApi;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.render.BrutalityHelper;
import org.brutality.utils.render.FontResourceLoader;
import org.brutality.utils.render.IsChoiceHelper;
import org.brutality.utils.render.ModuleSpecificHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.opengl.GL11;

public class BedAura extends Module implements PlayerApi {
    public BlockPos bedPos;
    public BlockPos blockPos;
    public BlockPos blockPos2;
    public BlockPos blockPos3;
    private final ModeSetting oldSetting = new ModeSetting("Mode", this, "New", Arrays.asList("New", "Old"));
    private NumberSetting rangeSetting = new NumberSetting("Range", this, 3.0, 3.0, 8.0, 0.5);
    private NumberSetting breakDelaySetting = new NumberSetting("Break Delay", this, 100.0, 0.0, 500.0, 1.0);
    private NumberSetting oldBreakSpeedSetting = new NumberSetting("Old Break Speed", this, 1.0, 1.0, 2.0, 0.1);
    private BooleanSetting oldSurroundingsSetting = new BooleanSetting("Old Surroundings", this, false);
    private BooleanSetting onlySSRotateSetting = new BooleanSetting("Only S/S Rotate", this, false);
    private ModeSetting blockSetting = new ModeSetting("Render Progress", this, "Bar", Arrays.asList("None", "Bar", "Block"));
    private BooleanSetting whitelistSetting = new BooleanSetting("Whitelist", this, true);
    private NumberSetting barWidthSetting = new NumberSetting("Bar Width", this, 80.0, 40.0, 200.0, 1.0);
    private NumberSetting barHeightSetting = new NumberSetting("Bar Height", this, 2.0, 1.0, 5.0, 1.0);
    private NumberSetting yOffsetSetting = new NumberSetting("Y Offset", this, 15.0, 0.0, 50.0, 1.0);
    private BooleanSetting barOutlineSetting = new BooleanSetting("Bar Outline", this, false);
    private BooleanSetting customFontSetting = new BooleanSetting("Custom Font", this, false);
    private ModeSetting fontSetting;
    private NumberSetting fontSizeSetting = new NumberSetting("Font Size", this, 18.0, 10.0, 30.0, 1.0);
    private final ModeSetting modeSetting = ModuleSpecificHelper.computeModeSetting(this);
    private final ColorSetting customColorSetting = new ColorSetting("Custom Color", this, 0.0F)
        .computeColorSetting(() -> !ModuleSpecificHelper.matchesModeSetting(this.modeSetting));
    private BrutalityHelper brutalityHelper;
    private CustomFontRenderer drawStringWithShadowComponent = null;
    private String text = "";
    private int count = 0;
    public double amount;
    public double amount2;
    private int count2;
    private int count3;
    private int lastSlot = -1;
    private int count4 = -1;
    private int count5 = Integer.MIN_VALUE;
    private int count6 = Integer.MIN_VALUE;
    private final FindHandler findHandler = new FindHandler();
    private FindHandler.Entry entry;
    private FindHandler.Entry2 entry2;
    private Object object;
    private Object object2;
    private long timestamp;
    public boolean rotating;
    private long timestamp2;
    private float[] floatValueArray;
    private boolean flag;
    private long timestamp3;
    private int count7;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;
    private Block block;
    private BlockPos blockPos4;
    private int count8 = -1;
    public static BedAura instance;
    private static final long timestamp4 = 12000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventPreMotion) {
                this.onEventPreMotion((EventPreMotion)event);
            } else if (event instanceof EventRender2D) {
                this.process6();
            } else if (event instanceof EventRender3D) {
                this.process11();
            } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.process5();
                this.process3();
                if (this.oldSetting.matchesString2("New")) {
                    this.process7();
                }
            } else if (event instanceof ChatEvent) {
                this.onChatReceived((ChatEvent)event);
            } else if (event instanceof RenderEvent4 renderEvent4 && renderEvent4.world != renderEvent4.lastWorld) {
                IsSetHelper.reset();
            }
        }
    }

    @Override
    public void process4() {
        this.process();
        if (this.lastSlot != -1 && mc.thePlayer == this.object && mc.theWorld == this.object2 && mc.thePlayer.inventory.currentItem == this.count4) {
            mc.thePlayer.inventory.currentItem = this.lastSlot;
        }

        this.lastSlot = this.count4 = -1;
        if (this.floatValueArray != null && OnJumpHelper.floatValueArray2 == this.floatValueArray) {
            OnJumpHelper.disable();
        }

        this.floatValueArray = null;
        this.rotating = false;
    }

    private BlockPos computeBlockPos(BlockPos pos) {
        if (pos == null) {
            return null;
        }
        BlockPos[] posArray = new BlockPos[]{pos.north(), pos.west(), pos.up(), pos.east(), pos.south()};

        for (BlockPos pos2 : posArray) {
            Block block = mc.theWorld.getBlockState(pos2).getBlock();
            if (block.getMaterial().isReplaceable() && !(block instanceof BlockBed)) {
                return pos;
            }
        }

        BlockPos pos3 = pos.north();
        float f = -1.0F;

        for (BlockPos pos4 : posArray) {
            Block block2 = mc.theWorld.getBlockState(pos4).getBlock();
            if (!(block2 instanceof BlockBed)) {
                float playerRelativeBlockHardness = block2.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos4);
                if (playerRelativeBlockHardness > f) {
                    pos3 = pos4;
                    f = playerRelativeBlockHardness;
                }
            }
        }

        return pos3;
    }

    private void process7() {
        if (this.entry != null
            && this.rotating
            && mc.thePlayer == this.object
            && mc.theWorld == this.object2
            && mc.currentScreen == null
            && !mc.thePlayer.isDead
            && !mc.thePlayer.isUsingItem()
            && mc.playerController != null
            && System.currentTimeMillis() >= this.timestamp
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState()) {
            int ticksExisted = mc.thePlayer.ticksExisted;
            if (ticksExisted != this.count6) {
                this.count6 = ticksExisted;
                FindHandler.Entry2 entry2 = this.entry.computeEntry2();
                if (WorldQueryCache.matchesModule(this, entry2.blockPos)) {
                    Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
                    float[] lastReportedRotations = mc.thePlayer.getLastReportedRotations();
                    Vec3 vec = OnJumpHelper.computeVec3(lastReportedRotations[1], lastReportedRotations[0]);
                    MovingObjectPosition hit = mc.theWorld
                        .rayTraceBlocks(
                            positionEyes,
                            positionEyes.addVector(vec.xCoord * this.computeDouble(), vec.yCoord * this.computeDouble(), vec.zCoord * this.computeDouble()),
                            false,
                            false,
                            false
                        );
                    if (!mc.theWorld.getBlockState(entry2.blockPos).equals(entry2.iBlockState)) {
                        this.process();
                        this.entry = null;
                        this.count5 = Integer.MIN_VALUE;
                    } else if (hit != null && hit.typeOfHit == MovingObjectType.BLOCK && entry2.blockPos.equals(hit.getBlockPos())) {
                        HuntingHandler2.Entry entry = HuntingHandler2.computeEntry(mc.thePlayer, mc.theWorld, entry2.blockPos);
                        if (entry.factor <= 0.0F) {
                            this.process();
                            this.entry = null;
                        } else {
                            if (mc.thePlayer.inventory.currentItem != entry.count) {
                                if (this.lastSlot == -1) {
                                    this.lastSlot = mc.thePlayer.inventory.currentItem;
                                }

                                mc.thePlayer.inventory.currentItem = entry.count;
                                this.count4 = entry.count;
                            }

                            this.entry2 = entry2;
                            mc.thePlayer.swingItem();
                            mc.playerController.onPlayerDamageBlock(entry2.blockPos, hit.sideHit);
                            this.amount = mc.playerController.getBlockDamageProgress();
                            if (mc.theWorld.isAirBlock(entry2.blockPos)) {
                                this.amount = 1.0;
                                this.entry2 = null;
                                this.entry = null;
                                this.count5 = Integer.MIN_VALUE;
                                this.timestamp = System.currentTimeMillis() + (long)this.breakDelaySetting.computeDouble();
                            }
                        }
                    }
                }
            }
        }
    }

    public BedAura() {
        super("BedAura", 0, Category.PLAYER);
        this.fontSetting = IsChoiceHelper.computeModeSetting("Font", this);
        this.oldBreakSpeedSetting.setConditionalVisibility(() -> this.oldSetting.matchesString2("Old"));
        this.oldSurroundingsSetting.setConditionalVisibility(() -> this.oldSetting.matchesString2("Old"));
        this.barWidthSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar"));
        this.barHeightSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar"));
        this.yOffsetSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar"));
        this.barOutlineSetting.setConditionalVisibility(this::z);
        this.customFontSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar"));
        this.fontSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar") && this.customFontSetting.isEnabled());
        this.fontSizeSetting.setConditionalVisibility(() -> this.blockSetting.getText().equals("Bar") && this.customFontSetting.isEnabled());
        this.handleSettingArray(
            new Setting[]{
                this.oldSetting,
                this.rangeSetting,
                this.breakDelaySetting,
                this.onlySSRotateSetting,
                this.blockSetting,
                this.whitelistSetting,
                this.oldBreakSpeedSetting,
                this.oldSurroundingsSetting,
                this.barWidthSetting,
                this.barHeightSetting,
                this.yOffsetSetting,
                this.barOutlineSetting,
                this.customFontSetting,
                this.fontSetting,
                this.fontSizeSetting,
                this.modeSetting,
                this.customColorSetting
            }
        );
        this.process8();
        instance = this;
    }

    @Generated
    public static BedAura getInstance() {
        return instance;
    }

    public static boolean checkState() {
        BedAura bedAura = instance;
        if (bedAura != null
            && bedAura.checkState44()
            && bedAura.rotating
            && bedAura.entry != null
            && bedAura.entry2 != null
            && mc.thePlayer != null
            && mc.playerController != null
            && mc.currentScreen == null
            && mc.thePlayer == bedAura.object
            && mc.theWorld == bedAura.object2
            && !mc.thePlayer.isDead
            && !mc.thePlayer.isUsingItem()
            && !StartHotbarMovesHelper.flag
            && !CoreHandler2.checkState()) {
            double d = bedAura.computeDouble();
            return mc.thePlayer.getPositionEyes(1.0F).squareDistanceTo(bedAura.entry2.vec3) <= d * d
                && mc.theWorld.getBlockState(bedAura.entry2.blockPos).equals(bedAura.entry2.iBlockState);
        }
        return false;
    }

    private FontRenderer getFontRenderer() {
        if (!this.customFontSetting.isEnabled()) {
            return mc.fontRendererObj;
        }
        String text = this.fontSetting.getText();
        int n = (int)this.fontSizeSetting.computeDouble();
        if (this.drawStringWithShadowComponent == null || !text.equals(this.text) || n != this.count) {
            this.text = text;
            this.count = n;
            this.drawStringWithShadowComponent = this.computeDrawStringWithShadowComponent(text, n);
        }

        return (FontRenderer)(this.drawStringWithShadowComponent != null ? this.drawStringWithShadowComponent : mc.fontRendererObj);
    }

    private BlockPos computeBlockPos() {
        int n = (int)Math.ceil(this.rangeSetting.computeDouble());
        BlockPos pos = new BlockPos(mc.thePlayer);

        for (int i = -n; i <= n; i++) {
            for (int j = -n; j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = pos.add(i, j, k);
                    if (mc.theWorld.getBlockState(pos2).getBlock() instanceof BlockBed
                        && (!this.whitelistSetting.isEnabled() || !IsSetHelper.matchesBlockPos(pos2))) {
                        return pos2;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public void onDisable() {
        this.reset();
        this.process10();
        this.flag = false;
    }

    private float[] computeFloatArray(Vec3 vec) {
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        double d = vec.xCoord - positionEyes.xCoord;
        double d2 = vec.yCoord - positionEyes.yCoord;
        double d3 = vec.zCoord - positionEyes.zCoord;
        return OnJumpHelper.computeFloatArray(
            new float[]{(float)Math.toDegrees(Math.atan2(d3, d)) - 90.0F, (float)(-Math.toDegrees(Math.atan2(d2, Math.hypot(d, d3))))},
            mc.thePlayer.getLastReportedRotations()
        );
    }

    private void reset() {
        this.process();
        if (this.lastSlot != -1 && mc.thePlayer == this.object && mc.theWorld == this.object2 && mc.thePlayer.inventory.currentItem == this.count4) {
            mc.thePlayer.inventory.currentItem = this.lastSlot;
        }

        this.lastSlot = this.count4 = -1;
        this.object = this.object2 = null;
        this.entry = null;
        this.entry2 = null;
        this.bedPos = this.blockPos2 = this.blockPos = this.blockPos3 = null;
        this.amount = this.amount2 = 0.0;
        this.rotating = false;
        this.count5 = this.count6 = Integer.MIN_VALUE;
        this.timestamp = 0L;
        if (this.floatValueArray != null && OnJumpHelper.floatValueArray2 == this.floatValueArray) {
            OnJumpHelper.disable();
        }

        this.floatValueArray = null;
    }

    private void process5() {
        boolean flag = this.oldSetting.matchesString2("Old");
        if (flag != this.flag4) {
            this.reset();
            this.process10();
            this.flag4 = flag;
        }
    }

    private boolean matchesEntry2(FindHandler.Entry2 entry2) {
        float[] floats = this.computeFloatArray(entry2.vec3);
        Vec3 positionEyes = mc.thePlayer.getPositionEyes(1.0F);
        Vec3 vec = OnJumpHelper.computeVec3(floats[1], floats[0]);
        MovingObjectPosition hit = mc.theWorld
            .rayTraceBlocks(
                positionEyes,
                positionEyes.addVector(vec.xCoord * this.computeDouble(), vec.yCoord * this.computeDouble(), vec.zCoord * this.computeDouble()),
                false,
                false,
                false
            );
        return hit != null && hit.typeOfHit == MovingObjectType.BLOCK && entry2.blockPos.equals(hit.getBlockPos());
    }

    private void process() {
        if (this.entry2 != null && mc.thePlayer == this.object && mc.theWorld == this.object2 && mc.playerController != null) {
            mc.playerController.resetBlockRemoving();
        }

        this.entry2 = null;
        this.amount = 0.0;
    }

    private void onEventPreMotion(EventPreMotion event) {
        if (this.oldSetting.matchesString2("Old")) {
            if (this.rotating && this.floatValueArray != null && mc.thePlayer != null) {
                mc.thePlayer.rotationYawHead = this.floatValueArray[0];
                mc.thePlayer.rotationPitchHead = this.floatValueArray[1];
                if (this.onlySSRotateSetting.isEnabled()) {
                    mc.thePlayer.renderYawOffset = this.floatValueArray[0];
                }

                event.handleFloat(this.floatValueArray[0], this.floatValueArray[1]);
            }
        } else if (this.rotating && this.floatValueArray != null && mc.thePlayer != null && this.entry != null && WorldQueryCache.matchesModule2(this)) {
            if (!this.matchesEntry2(this.entry.computeEntry2())) {
                this.reset();
            } else {
                this.floatValueArray = this.computeFloatArray(this.entry.computeEntry2().vec3);
                OnJumpHelper.handleFloat(this.floatValueArray[0], this.floatValueArray[1], 1);
                OnJumpHelper.floatValueArray2 = this.floatValueArray;
                mc.thePlayer.rotationYawHead = this.floatValueArray[0];
                mc.thePlayer.rotationPitchHead = this.floatValueArray[1];
                if (this.onlySSRotateSetting.isEnabled()) {
                    mc.thePlayer.renderYawOffset = this.floatValueArray[0];
                }

                event.handleFloat(this.floatValueArray[0], this.floatValueArray[1]);
            }
        }
    }

    private double computeDouble() {
        return Math.min(this.rangeSetting.computeDouble(), (double)mc.playerController.getBlockReachDistance());
    }

    private void handleBlockPos2(BlockPos pos) {
        if (pos == null) {
            this.rotating = false;
            this.floatValueArray = null;
        } else {
            double d = pos.getX() + 0.5 - mc.thePlayer.posX;
            double d2 = pos.getY() + 0.5 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
            double d3 = pos.getZ() + 0.5 - mc.thePlayer.posZ;
            float f = (float)(Math.atan2(d3, d) * 180.0 / Math.PI) - 90.0F;
            float f2 = (float)(-(Math.atan2(d2, Math.sqrt(d * d + d3 * d3)) * 180.0 / Math.PI));
            this.floatValueArray = new float[]{f, f2};
            OnJumpHelper.setReach((float)this.rangeSetting.computeDouble());
            OnJumpHelper.handleFloat(f, f2, 1);
            OnJumpHelper.floatValueArray2 = this.floatValueArray;
            this.rotating = true;
        }
    }

    private void onChatReceived(ChatEvent chatEvent) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            String str = this.stripColor(chatEvent.message.getUnformattedText());
            if (str.startsWith(" ") && str.contains("Protect your bed and destroy the enemy beds.")) {
                IsSetHelper.process();
                this.flag = false;
            } else if (str.equals("You will respawn because you still have a bed!")) {
                this.flag = true;
                this.timestamp3 = System.currentTimeMillis();
            } else if (str.equals("You have respawned!") && this.flag && System.currentTimeMillis() - this.timestamp3 <= timestamp4) {
                IsSetHelper.process();
                this.flag = false;
            }
        }
    }

    public void process6() {
        if (this.blockSetting.matchesString2("Bar")) {
            long now = System.currentTimeMillis();
            float f = (float)(now - this.timestamp2) / 1000.0F;
            this.timestamp2 = now;
            float f2 = 0.025F;
            float f3 = 1.0F - (float)Math.exp(-f / f2);
            this.amount2 = GetCurrentScreenHelper.computeFloat((float)this.amount2, (float)this.amount, f3);
            if (this.amount2 > 1.0) {
                this.amount2 = 1.0;
            }

            ScaledResolution sr = new ScaledResolution(mc);
            int scaledWidth = sr.getScaledWidth();
            int scaledHeight = sr.getScaledHeight();
            float f4 = (float)this.barWidthSetting.computeDouble();
            float f5 = (float)this.barHeightSetting.computeDouble();
            float f6 = (float)this.yOffsetSetting.computeDouble();
            float f7 = scaledWidth / 2.0F - f4 / 2.0F;
            float f8 = scaledHeight / 2.0F + f6;
            if (this.bedPos != null) {
                this.count2 = (int)GetCurrentScreenHelper.computeFloat(this.count2, 255.0F, f3);
                this.count3 = (int)GetCurrentScreenHelper.computeFloat(this.count3, 100.0F, f3);
            } else {
                this.count2 = (int)GetCurrentScreenHelper.computeFloat(this.count2, 0.0F, f3);
                this.count3 = (int)GetCurrentScreenHelper.computeFloat(this.count3, 0.0F, f3);
            }

            if (this.count2 >= 5 || this.bedPos != null) {
                Color color = this.computeColor();
                Color color2 = new Color(0, 0, 0, this.count3);
                Color color3 = new Color(color.getRed(), color.getGreen(), color.getBlue(), this.count2);
                if (this.barOutlineSetting.isEnabled()) {
                    Color color4 = new Color(255, 255, 255, Math.min(this.count2 + 50, 255));
                    Gui.drawRect((int)(f7 - 1.0F), (int)(f8 - 1.0F), (int)(f7 + f4 + 1.0F), (int)f8, color4.getRGB());
                    Gui.drawRect((int)(f7 - 1.0F), (int)(f8 + f5), (int)(f7 + f4 + 1.0F), (int)(f8 + f5 + 1.0F), color4.getRGB());
                    Gui.drawRect((int)(f7 - 1.0F), (int)f8, (int)f7, (int)(f8 + f5), color4.getRGB());
                    Gui.drawRect((int)(f7 + f4), (int)f8, (int)(f7 + f4 + 1.0F), (int)(f8 + f5), color4.getRGB());
                }

                Gui.drawRect((int)f7, (int)f8, (int)(f7 + f4), (int)(f8 + f5), color2.getRGB());
                float f9 = f4 * (float)this.amount2;
                Gui.drawRect((int)f7, (int)f8, (int)(f7 + f9), (int)(f8 + f5), color3.getRGB());
                String str = String.format("Breaker (%.0f%%)", this.amount2 * 100.0);
                int rgb = new Color(255, 255, 255, this.count2).getRGB();
                FontRenderer fontRenderer = this.getFontRenderer();
                float f10 = scaledWidth / 2.0F - fontRenderer.getStringWidth(str) / 2.0F;
                float f11 = f8 + f5 + 3.0F;
                fontRenderer.drawStringWithShadow(str, f10, f11, rgb);
            }
        }
    }

    public void process3() {
        if (this.oldSetting.matchesString2("Old")) {
            this.process12();
        } else if (GetCurrentScreenHelper.checkState2() && mc.playerController != null) {
            if (this.object != null && (mc.thePlayer != this.object || mc.theWorld != this.object2)) {
                this.reset();
            }

            if (mc.currentScreen == null
                && !mc.thePlayer.isDead
                && !mc.thePlayer.isRiding()
                && !mc.thePlayer.isUsingItem()
                && !StartHotbarMovesHelper.flag
                && !CoreHandler2.checkState()
                && (Scaffold.getInstance() == null || !Scaffold.getInstance().checkState44())) {
                IsSetHelper.tick();
                if (this.whitelistSetting.isEnabled() && IsSetHelper.isFlag()) {
                    this.reset();
                } else {
                    if (this.lastSlot != -1 && mc.thePlayer.inventory.currentItem != this.count4) {
                        this.lastSlot = this.count4 = -1;
                        this.process();
                        this.entry = null;
                    }

                    if (this.entry != null
                        && (
                            !mc.theWorld.isBlockLoaded(this.entry.blockPos)
                                || !(mc.theWorld.getBlockState(this.entry.blockPos).getBlock() instanceof BlockBed)
                                || this.whitelistSetting.isEnabled() && IsSetHelper.matchesBlockPos(this.entry.blockPos)
                                || !this.matchesEntry2(this.entry.computeEntry2())
                        )) {
                        this.process();
                        this.entry = null;
                    }

                    if (this.entry2 != null && !mc.theWorld.getBlockState(this.entry2.blockPos).equals(this.entry2.iBlockState)) {
                        boolean airBlock = mc.theWorld.isAirBlock(this.entry2.blockPos);
                        this.process();
                        this.entry = null;
                        this.count5 = Integer.MIN_VALUE;
                        this.timestamp = System.currentTimeMillis() + (airBlock ? (long)this.breakDelaySetting.computeDouble() : 0L);
                    }

                    int ticksExisted = mc.thePlayer.ticksExisted;
                    if (this.entry == null && (this.count5 == Integer.MIN_VALUE || ticksExisted < this.count5 || ticksExisted - this.count5 >= 5)) {
                        this.count5 = ticksExisted;
                        double d = this.computeDouble();
                        this.entry = this.findHandler
                            .computeEntry(
                                mc.theWorld,
                                this.computeList(d),
                                mc.thePlayer.getPositionEyes(1.0F),
                                d,
                                pos -> {
                                    HuntingHandler2.Entry entry = HuntingHandler2.computeEntry(mc.thePlayer, mc.theWorld, pos);
                                    return entry.count2 == Integer.MAX_VALUE
                                        ? Double.POSITIVE_INFINITY
                                        : entry.count2 + (entry.factor >= 1.0F ? 0 : 5) + this.breakDelaySetting.computeDouble() / 50.0;
                                },
                                this::matchesEntry2
                            );
                    }

                    if (this.entry == null) {
                        int n = this.count5;
                        this.reset();
                        this.count5 = n;
                    } else {
                        this.bedPos = this.entry.blockPos;
                        this.blockPos2 = this.entry.computeEntry2().blockPos.equals(this.bedPos) ? null : this.entry.computeEntry2().blockPos;
                        if (!WorldQueryCache.matchesModule(this)) {
                            this.process4();
                        } else {
                            KillAura.process33();
                            this.object = mc.thePlayer;
                            this.object2 = mc.theWorld;
                            this.floatValueArray = this.computeFloatArray(this.entry.computeEntry2().vec3);
                            OnJumpHelper.handleFloat(this.floatValueArray[0], this.floatValueArray[1], 1);
                            OnJumpHelper.floatValueArray2 = this.floatValueArray;
                            this.rotating = true;
                        }
                    }
                }
            } else {
                this.reset();
            }
        } else {
            this.reset();
        }
    }

    private List<BlockPos> computeList(double d) {
        ArrayList list = new ArrayList();
        int n = (int)Math.ceil(d) + 1;
        BlockPos pos = new BlockPos(mc.thePlayer);

        for (int i = -n; i <= n; i++) {
            for (int j = ~(n - 1); j <= n; j++) {
                for (int k = -n; k <= n; k++) {
                    BlockPos pos2 = pos.add(i, j, k);
                    if (mc.theWorld.isBlockLoaded(pos2)) {
                        if (mc.theWorld.getBlockState(pos2).getBlock() instanceof BlockBed
                            && (!this.whitelistSetting.isEnabled() || !IsSetHelper.matchesBlockPos(pos2))) {
                            list.add(pos2);
                        }

                        if (list.size() >= 32) {
                            return list;
                        }
                    }
                }
            }
        }

        return list;
    }

    private Color computeColor() {
        this.process8();
        return ModuleSpecificHelper.matchesModeSetting(this.modeSetting) && this.brutalityHelper != null
            ? this.brutalityHelper.getColor()
            : this.customColorSetting.computeColor();
    }

    private BlockPos computeBlockPos2(BlockPos pos) {
        BlockPos pos2 = pos.up();
        return mc.theWorld.getBlockState(pos2).getBlock() == Blocks.air ? null : pos2;
    }

    private void process14() {
        this.bedPos = this.blockPos = this.blockPos4 = null;
        this.amount = this.amount2 = 0.0;
        this.rotating = false;
        if (OnJumpHelper.floatValueArray2 == this.floatValueArray) {
            OnJumpHelper.disable();
        }

        this.floatValueArray = null;
    }

    private CustomFontRenderer computeDrawStringWithShadowComponent(String str, int n) {
        return FontResourceLoader.computeDrawStringWithShadowComponent(str, n);
    }

    private void handleBlockPos(BlockPos pos) {
        if (pos != null && mc.theWorld != null && mc.thePlayer != null) {
            Block block = mc.theWorld.getBlockState(pos).getBlock();
            int n = GetCurrentScreenHelper.computeInt(block);
            if (n != -1 && mc.thePlayer.inventory.currentItem != n) {
                if (this.count8 == -1) {
                    this.count8 = mc.thePlayer.inventory.currentItem;
                }

                mc.thePlayer.inventory.currentItem = n;
            }

            double d = block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, pos) * this.oldBreakSpeedSetting.computeDouble();
            if (KillAura.instance == null || !KillAura.instance.blocking) {
                if (this.onlySSRotateSetting.isEnabled() && this.amount == 0.0 || !this.onlySSRotateSetting.isEnabled()) {
                    OnJumpHelper.disable();
                    this.handleBlockPos2(pos);
                }

                if (!this.onlySSRotateSetting.isEnabled()) {
                    mc.thePlayer.swingItem();
                }

                if (this.amount == 0.0) {
                    this.flag2 = false;
                    mc.thePlayer.swingItem();
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                }

                if (this.amount + d >= 1.0) {
                    this.handleBlockPos2(pos);
                }

                if (this.amount >= 1.0) {
                    if (this.onlySSRotateSetting.isEnabled()) {
                        mc.thePlayer.swingItem();
                    }

                    this.flag2 = true;
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                    mc.playerController.onPlayerDestroyBlock(pos, EnumFacing.UP);
                    mc.theWorld.setBlockState(pos, Blocks.air.getDefaultState(), 11);
                    this.amount = 0.0;
                    this.count7 = 0;
                    this.flag3 = pos.equals(this.blockPos4);
                }

                if (this.amount + d >= 1.0) {
                    this.rotating = true;
                }

                if (!this.flag2) {
                    this.amount += d;
                }
            }
        }
    }

    private String stripColor(String str) {
        return str == null ? null : str.replaceAll("(?i)§[0-9A-FK-OR]", "");
    }

    @Override
    public void process2() {
        this.reset();
        IsSetHelper.reset();
    }

    public static float[] computeFloatArray() {
        return instance != null && instance.checkState44() && instance.rotating ? instance.floatValueArray : null;
    }

    public void process11() {
        if (this.blockSetting.matchesString2("Block") && this.bedPos != null) {
            double d = (this.blockPos2 == null ? this.bedPos.getX() : this.blockPos2.getX()) - mc.getRenderManager().viewerPosX;
            double d2 = (this.blockPos2 == null ? this.bedPos.getY() : this.blockPos2.getY()) - mc.getRenderManager().viewerPosY;
            double d3 = (this.blockPos2 == null ? this.bedPos.getZ() : this.blockPos2.getZ()) - mc.getRenderManager().viewerPosZ;
            AxisAlignedBB box = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + this.amount2, d3 + 1.0);
            AxisAlignedBB box2 = new AxisAlignedBB(d, d2, d3, d + 1.0, d2 + 1.0, d3 + 1.0);
            Color color = this.computeColor();
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.disableTexture2D();
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 0.2F);
            ResetColorHelper.handleAxisAlignedBB(box);
            GL11.glLineWidth(3.0F);
            GL11.glColor4f(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
            ResetColorHelper.handleAxisAlignedBB2(box2);
            GlStateManager.enableTexture2D();
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onEnable() {
        this.reset();
        this.process10();
        this.flag4 = this.oldSetting.matchesString2("Old");
        this.flag = false;
    }

    private boolean z() {
        return this.blockSetting.getText().equals("Bar");
    }

    private void process8() {
        this.brutalityHelper = ModuleSpecificHelper.computeBrutalityHelper(this.modeSetting);
    }

    private void process13() {
        if (this.oldSurroundingsSetting.isEnabled()
            && this.bedPos != null
            && this.computeBlockPos2(this.bedPos) != null
            && this.count7 > this.breakDelaySetting.computeDouble() / 30.0
            && !this.flag3) {
            this.blockPos4 = this.computeBlockPos2(this.bedPos);
            this.handleBlockPos(this.blockPos4);
        } else {
            this.blockPos4 = null;
            if (this.count8 != -1) {
                mc.thePlayer.inventory.currentItem = this.count8;
                this.count8 = -1;
            }

            if (this.blockPos != null && mc.theWorld.getBlockState(this.blockPos).getBlock() != this.block) {
                this.amount = 0.0;
                this.flag3 = false;
            }

            this.blockPos = this.bedPos;
            if (this.count7 > this.breakDelaySetting.computeDouble() / 30.0) {
                this.handleBlockPos(this.computeBlockPos(this.bedPos));
            } else {
                this.amount = 0.0;
            }
        }
    }

    private void process12() {
        this.rotating = false;
        if (!GetCurrentScreenHelper.checkState2()) {
            this.process10();
        } else {
            IsSetHelper.tick();
            this.count7++;
            if (Brutality.moduleManager.computeModule("Scaffold") != null && Brutality.moduleManager.computeModule("Scaffold").checkState44()) {
                this.process10();
            } else if (KillAura.entityLivingBase2 != null) {
                if (this.amount > 0.0) {
                    BlockPos pos = this.blockPos4 != null ? this.blockPos4 : this.bedPos;
                    if (pos != null && mc.thePlayer.sendQueue != null) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, pos, EnumFacing.UP));
                    }

                    this.amount = this.amount2 = 0.0;
                }
            } else {
                this.bedPos = this.computeBlockPos();
                if (this.bedPos == null) {
                    this.process14();
                } else {
                    this.block = mc.theWorld.getBlockState(this.bedPos).getBlock();
                    if (mc.thePlayer.getDistance(this.bedPos.getX() + 0.5, this.bedPos.getY() + 0.5, this.bedPos.getZ() + 0.5)
                        > this.rangeSetting.computeDouble()) {
                        this.process10();
                    } else {
                        if (!this.onlySSRotateSetting.isEnabled()) {
                            this.handleBlockPos2(this.blockPos4 != null ? this.blockPos4 : this.bedPos);
                        }

                        this.process13();
                    }
                }
            }
        }
    }

    private void process10() {
        if (this.count8 != -1 && mc.thePlayer != null) {
            mc.thePlayer.inventory.currentItem = this.count8;
        }

        this.count8 = -1;
        this.process14();
        this.block = null;
        this.count7 = 0;
        this.flag2 = this.flag3 = false;
        if (OnJumpHelper.floatValueArray2 == this.floatValueArray) {
            OnJumpHelper.disable();
        }
    }
}
