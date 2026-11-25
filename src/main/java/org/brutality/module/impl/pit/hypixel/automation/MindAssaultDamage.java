package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.render.GlStateGuard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class MindAssaultDamage extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 50.0, 0.0, 1080.0, 1.0);
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 5.0, 0.0, 1920.0, 1.0);
    private final BooleanSetting showReductionSetting = new BooleanSetting("Show Reduction", this, true);
    private final BooleanSetting showInChatSetting = new BooleanSetting("Show In Chat", this, false);
    private final BooleanSetting detectForOthersSetting = new BooleanSetting("Detect for Others", this, true);
    private final BooleanSetting showDamageOnPlayerSetting = new BooleanSetting("Show Damage on Player", this, false);
    private final BooleanSetting bestDamageSpotSetting = new BooleanSetting("Best Damage Spot", this, false);
    private Vec3 vec3;
    private double amount;
    private long timestamp;
    private World world;
    private String text = "";
    private double amount2 = 0.0;
    private boolean flag = false;
    private final Map<String, Double> lookupMap = new HashMap<>();
    private static final long timestamp2 = 500L;

    private void process6() {
        this.flag = false;
        this.amount2 = 0.0;
        this.text = "";
        this.lookupMap.clear();
    }

    private void process4() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld != null && mc.thePlayer != null) {
            if (this.flag && this.amount2 > 0.0) {
                EntityPlayerSP player = mc.thePlayer;
                double d = player.lastTickPosX + (player.posX - player.lastTickPosX) * mc.timer.renderPartialTicks;
                double d2 = player.lastTickPosY + (player.posY - player.lastTickPosY) * mc.timer.renderPartialTicks + player.height + 0.5;
                double d3 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * mc.timer.renderPartialTicks;
                String str = String.format("%.2f", this.amount2);
                this.handleDouble(d, d2, d3, str, 65535);
            }

            if (this.detectForOthersSetting.isEnabled()) {
                for (EntityPlayer player2 : EntityCache.getEntries()) {
                    if (player2 != mc.thePlayer) {
                        Double val = this.lookupMap.get(player2.getName());
                        if (val != null && val > 0.0) {
                            double d4 = player2.lastTickPosX + (player2.posX - player2.lastTickPosX) * mc.timer.renderPartialTicks;
                            double d5 = player2.lastTickPosY + (player2.posY - player2.lastTickPosY) * mc.timer.renderPartialTicks + player2.height + 0.5;
                            double d6 = player2.lastTickPosZ + (player2.posZ - player2.lastTickPosZ) * mc.timer.renderPartialTicks;
                            String str2 = String.format("%.2f", val);
                            this.handleDouble(d4, d5, d6, str2, 16737894);
                        }
                    }
                }
            }
        }
    }

    private void handleMinecraft(Minecraft mc) {
        if (this.bestDamageSpotSetting.isEnabled()
            && this.flag
            && "PIT".equals(StringUtils.stripControlCodes(FreshHelper.computeString(mc.thePlayer, mc.theWorld)))) {
            long now = System.currentTimeMillis();
            if (this.world != mc.theWorld) {
                this.process3();
            }

            this.world = mc.theWorld;
            if (now >= this.timestamp) {
                this.timestamp = now + timestamp2;
                this.vec3 = null;
                this.amount = 0.0;
                List list = new ArrayList();

                for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                    if (playerEntity != mc.thePlayer && !playerEntity.isDead) {
                        ItemStack equipmentInSlot = playerEntity.getEquipmentInSlot(2);
                        if (equipmentInSlot != null && equipmentInSlot.getItem() instanceof ItemArmor && ((ItemArmor)equipmentInSlot.getItem()).getArmorMaterial() == ArmorMaterial.LEATHER) {
                            list.add(playerEntity);
                        }
                    }
                }

                double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
                int n = MathHelper.floor_double(mc.thePlayer.posY);

                for (int i = -18; i <= 18; i++) {
                    for (int j = -18; j <= 18; j++) {
                        double d = i + 0.5;
                        double d2 = j + 0.5;
                        if (!(Math.hypot(d - 0.5, d2 - 0.5) >= 19.0)) {
                            for (int k = n - 2; k <= n + 2; k++) {
                                BlockPos pos = new BlockPos(i, k - 1, j);
                                if (mc.theWorld.isBlockLoaded(pos)) {
                                    IBlockState blockState = mc.theWorld.getBlockState(pos);
                                    AxisAlignedBB collisionBoundingBox = blockState.getBlock().getCollisionBoundingBox(mc.theWorld, pos, blockState);
                                    if (collisionBoundingBox != null) {
                                        double maxY = collisionBoundingBox.maxY;
                                        double d3 = computeDouble(list, d, maxY, d2);
                                        double d4 = d - mc.thePlayer.posX;
                                        double d5 = maxY - mc.thePlayer.posY;
                                        double d6 = d2 - mc.thePlayer.posZ;
                                        double d7 = d4 * d4 + d5 * d5 + d6 * d6;
                                        if (!(d3 <= 0.0)
                                            && !(d3 < this.amount)
                                            && (d3 != this.amount || !(d7 >= POSITIVE_INFINITY))
                                            && "PIT".equals(StringUtils.stripControlCodes(FreshHelper.computeString(d, maxY, d2, mc.theWorld)))) {
                                            AxisAlignedBB box = new AxisAlignedBB(
                                                d - 0.3, maxY + 0.01, d2 - 0.3, d + 0.3, maxY + 1.8, d2 + 0.3
                                            );
                                            if (matchesMinecraft(mc, box)) {
                                                this.vec3 = new Vec3(d, maxY, d2);
                                                this.amount = d3;
                                                POSITIVE_INFINITY = d7;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            this.process3();
        }
    }

    private void process3() {
        this.vec3 = null;
        this.amount = 0.0;
        this.timestamp = 0L;
        this.world = null;
    }

    private double computeDouble(EntityPlayer player) {
        double d = 0.0;

        for (EntityPlayer playerEntity : player.worldObj.playerEntities) {
            if (playerEntity != player && !playerEntity.isDead && !(playerEntity.getDistanceToEntity(player) > 11.0F)) {
                ItemStack equipmentInSlot = playerEntity.getEquipmentInSlot(2);
                if (equipmentInSlot != null && equipmentInSlot.getItem() instanceof ItemArmor && ((ItemArmor)equipmentInSlot.getItem()).getArmorMaterial() == ArmorMaterial.LEATHER) {
                    d += 0.75;
                    if (d >= 8.0) {
                        return 8.0;
                    }
                }
            }
        }

        return d;
    }

    private void process5() {
        String str = this.showReductionSetting.isEnabled() ? "§5-60% " : "";
        this.text = str + String.format(Locale.ROOT, "§4+%.2f ❤", this.amount2);
    }

    private void handleDouble(double d, double d2, double d3, String str, int n) {
        Minecraft mc = Minecraft.getMinecraft();
        double d4 = d - mc.getRenderManager().viewerPosX;
        double d5 = d2 - mc.getRenderManager().viewerPosY;
        double d6 = d3 - mc.getRenderManager().viewerPosZ;
        double dist = Math.sqrt(d4 * d4 + d5 * d5 + d6 * d6);
        float f = Math.max(0.1F, (float)(dist * 0.01F));
        GL11.glPushMatrix();
        GL11.glTranslated(d4, d5, d6);
        GL11.glRotatef(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f, -f, f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        int textWidth = computeFontRenderer(MindAssaultDamage.class).getStringWidth(str);
        computeFontRenderer(MindAssaultDamage.class).drawStringWithShadow(str, -textWidth / 2, 0.0F, n);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process3();
        HudDragHelper.handleObject(this);
        this.process6();
    }

    private static boolean matchesMinecraft(Minecraft mc, AxisAlignedBB box) {
        List list = new ArrayList();

        for (int i = MathHelper.floor_double(box.minX); i <= MathHelper.floor_double(box.maxX); i++) {
            for (int j = MathHelper.floor_double(box.minY); j <= MathHelper.floor_double(box.maxY); j++) {
                for (int k = MathHelper.floor_double(box.minZ); k <= MathHelper.floor_double(box.maxZ); k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    IBlockState blockState = mc.theWorld.getBlockState(pos);
                    blockState.getBlock().addCollisionBoxesToList(mc.theWorld, pos, blockState, box, list, mc.thePlayer);
                    if (!list.isEmpty()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);
                        if (stringTagAt.contains("Mind Assault")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean matchesString(String str) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.theWorld == null) {
            return false;
        }
        for (EntityPlayer player : EntityCache.getEntries()) {
            if (player.getName().equals(str)) {
                return this.matchesEntityPlayer(player);
            }
        }

        return false;
    }

    public double computeDouble(String str) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player != null && Minecraft.getMinecraft().theWorld != null) {
            if (player.getName().equals(str)) {
                return this.amount2;
            }
            Double val = this.lookupMap.get(str);
            return val != null ? val : 0.0;
        }
        return 0.0;
    }

    private static double computeDouble(List<EntityPlayer> players, double d, double d2, double d3) {
        int n = 0;

        for (EntityPlayer player : players) {
            double d4 = d - player.posX;
            double d5 = d2 - player.posY;
            double d6 = d3 - player.posZ;
            if (d4 * d4 + d5 * d5 + d6 * d6 <= 121.0) {
                if (++n >= 11) {
                    return 8.0;
                }
            }
        }

        return n * 0.75;
    }

    @Override
    public void onRender() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer != null && mc.theWorld != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            boolean flag = mc.currentScreen instanceof GuiChat;
            boolean flag2 = !this.lookupMap.isEmpty();
            boolean flag3 = this.flag || flag && this.showInChatSetting.isEnabled() || flag2;
            if (!flag3) {
                HudDragHelper.handleObject(this);
            } else {
                int FONT_HEIGHT = computeFontRenderer(MindAssaultDamage.class).FONT_HEIGHT;
                if (this.detectForOthersSetting.isEnabled() && !this.lookupMap.isEmpty()) {
                    FONT_HEIGHT += this.lookupMap.size() * (computeFontRenderer(MindAssaultDamage.class).FONT_HEIGHT + 2) + 5;
                }

                if (flag) {
                    int[] ints = HudDragHelper.computeIntArray();
                    int n = ints[0];
                    int n2 = ints[1];
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this)) {
                            String str;
                            if (this.flag) {
                                String str2 = this.amount2 >= 8.0 ? " MAXED!" : "";
                                String text = this.text;
                                str = "Minds: " + text + str2;
                            } else {
                                str = "Minds: 0.00 ♥";
                            }

                            String str3 = str;
                            int textWidth = computeFontRenderer(MindAssaultDamage.class).getStringWidth(str3);
                            if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                                HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                            }
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }

                    if (HudDragHelper.matchesObject(this)) {
                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            this.xPosSetting.handleDouble2(ints2[0]);
                            this.yPosSetting.handleDouble2(ints2[1]);
                            f = ints2[0];
                            f2 = ints2[1];
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                if (this.flag) {
                    int n3 = 16777215;
                    int n4 = 16751001;
                    char ch = '\uffff';
                    computeFontRenderer(MindAssaultDamage.class).drawStringWithShadow("Minds: ", f, f2, n3);
                    computeFontRenderer(MindAssaultDamage.class)
                        .drawStringWithShadow(this.text, f + computeFontRenderer(MindAssaultDamage.class).getStringWidth("Minds: "), f2, n4);
                    if (this.amount2 >= 8.0) {
                        String str4 = " MAXED!";
                        computeFontRenderer(MindAssaultDamage.class)
                            .drawStringWithShadow(str4, f + computeFontRenderer(MindAssaultDamage.class).getStringWidth("Minds: " + this.text), f2, ch);
                    }
                } else if (flag && this.showInChatSetting.isEnabled()) {
                    computeFontRenderer(MindAssaultDamage.class).drawStringWithShadow("Minds: ", f, f2, 16777215);
                    computeFontRenderer(MindAssaultDamage.class)
                        .drawStringWithShadow("0.00 ♥", f + computeFontRenderer(MindAssaultDamage.class).getStringWidth("Minds: "), f2, 16751001);
                }

                if (this.detectForOthersSetting.isEnabled() && (!this.lookupMap.isEmpty() || flag)) {
                    float f3 = f2 + computeFontRenderer(MindAssaultDamage.class).FONT_HEIGHT + 5.0F;
                    if (this.lookupMap.isEmpty() && flag) {
                        computeFontRenderer(MindAssaultDamage.class)
                            .drawStringWithShadow(EnumChatFormatting.GOLD + "Others: " + EnumChatFormatting.GRAY + "None", f, f3, 16777215);
                    } else {
                        for (Entry entry : this.lookupMap.entrySet()) {
                            var key = (String)entry.getKey();
                            double val = (Double)entry.getValue();
                            var str5 = this.showReductionSetting.isEnabled() ? EnumChatFormatting.DARK_PURPLE + "-60% " : "";
                            var str6 = str5 + EnumChatFormatting.DARK_RED + String.format("+%.2f ", val) + EnumChatFormatting.RED + "♥";
                            if (val >= 8.0) {
                                str6 = str6 + EnumChatFormatting.AQUA + " MAXED!";
                            }

                            String str7 = EnumChatFormatting.GOLD + key + ": " + str6;
                            computeFontRenderer(MindAssaultDamage.class).drawStringWithShadow(str7, f, f3, 16777215);
                            f3 += computeFontRenderer(MindAssaultDamage.class).FONT_HEIGHT + 2;
                        }
                    }
                }
            }
        } else {
            this.process6();
            HudDragHelper.handleObject(this);
        }
    }

    private void process() {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.bestDamageSpotSetting.isEnabled() && this.vec3 != null && mc.theWorld == this.world && mc.thePlayer != null) {
            double d = this.vec3.xCoord - mc.getRenderManager().viewerPosX;
            double d2 = this.vec3.yCoord - mc.getRenderManager().viewerPosY;
            double d3 = this.vec3.zCoord - mc.getRenderManager().viewerPosZ;

            try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                GlStateManager.depthMask(false);
                GlStateManager.color(0.0F, 1.0F, 1.0F, 1.0F);
                GL11.glLineWidth(2.0F);
                RenderGlobal.func_181561_a(new AxisAlignedBB(d - 0.4, d2 + 0.02, d3 - 0.4, d + 0.4, d2 + 0.08, d3 + 0.4));
            }

            try (GlStateGuard renderHelper72 = new GlStateGuard()) {
                this.handleDouble(this.vec3.xCoord, this.vec3.yCoord + 0.5, this.vec3.zCoord, String.format(Locale.ROOT, "Best: +%.2f", this.amount), 65535);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() != EventState.PRE) {
                    return;
                }

                Minecraft mc = Minecraft.getMinecraft();
                this.process6();
                if (mc.thePlayer == null || mc.theWorld == null) {
                    this.process3();
                    return;
                }

                this.flag = this.matchesEntityPlayer(mc.thePlayer);
                if (this.flag) {
                    this.amount2 = this.computeDouble(mc.thePlayer);
                    this.process5();
                }

                this.handleMinecraft(mc);
                if (this.detectForOthersSetting.isEnabled()) {
                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (playerEntity != mc.thePlayer && !playerEntity.isDead && this.matchesEntityPlayer(playerEntity)) {
                            this.lookupMap.put(playerEntity.getName(), this.computeDouble(playerEntity));
                        }
                    }
                }
            } else if (event instanceof EventRender3D) {
                this.process();
                if (this.showDamageOnPlayerSetting.isEnabled()) {
                    this.process4();
                }
            }
        }
    }

    public MindAssaultDamage() {
        super("MindAssaultDamage", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.xPosSetting,
                this.yPosSetting,
                this.showReductionSetting,
                this.showInChatSetting,
                this.detectForOthersSetting,
                this.showDamageOnPlayerSetting,
                this.bestDamageSpotSetting
            }
        );
    }
}
