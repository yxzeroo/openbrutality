package org.brutality.utils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.player.IsKeyDownHelper;
import org.brutality.utils.rotation.OnJumpHelper;
import org.lwjgl.input.Mouse;

public class GetCurrentScreenHelper {
    private static final Random random = new Random();
    private static final long[] longValueArray = new long[]{System.currentTimeMillis(), 0L, 0L, 0L};
    private static final int[] intValueArray = new int[]{-1640531527, 1013904242, 436410924, 1504918853};
    private static int count = new Color(189, 0, 1).getRGB();
    private static int count2 = new Color(253, 63, 63).getRGB();
    private static int count3 = new Color(215, 162, 50).getRGB();
    private static int count4 = new Color(254, 254, 62).getRGB();
    private static int count5 = new Color(0, 191, 4).getRGB();
    private static int count6 = new Color(64, 253, 62).getRGB();
    private static int count7 = new Color(65, 255, 254).getRGB();
    private static int count8 = new Color(0, 190, 189).getRGB();
    private static int count9 = new Color(1, 1, 187).getRGB();
    private static int count10 = new Color(61, 64, 255).getRGB();
    private static int count11 = new Color(254, 63, 255).getRGB();
    private static int count12 = new Color(190, 0, 190).getRGB();
    private static int count13 = new Color(190, 190, 190).getRGB();
    private static int count14 = new Color(63, 63, 63).getRGB();
    private static int count15 = new Color(17, 17, 17).getRGB();
    public static Minecraft mc = Minecraft.getMinecraft();
    public static FontRenderer fontRenderer = GetCurrentScreenHelper.mc.fontRendererObj;

    public static boolean matchesEntityPlayer(EntityPlayer entityPlayer, Item item) {
        for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); ++i) {
            ItemStack itemStack = entityPlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() != item) continue;
            return true;
        }
        return false;
    }

    public static boolean matchesFloat(float f, BlockPos blockPos) {
        return GetCurrentScreenHelper.matchesFloat(f, blockPos.getX(), blockPos.getZ());
    }

    public static String computeString(String string) {
        char[] cArray = StringUtils.stripControlCodes(string).toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : cArray) {
            if (c >= '\u007f' || c <= '\u0014') continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static int computeInt2() {
        if (!GetCurrentScreenHelper.checkState2()) {
            return -1;
        }
        Scoreboard scoreboard = GetCurrentScreenHelper.mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return -1;
        }
        ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreObjective == null || !GetCurrentScreenHelper.computeString(scoreObjective.getDisplayName()).contains("BED WARS")) {
            return -1;
        }
        for (String string : GetCurrentScreenHelper.computeList2()) {
            String[] stringArray = (string = GetCurrentScreenHelper.computeString(string)).split("  ");
            if (stringArray.length > 1) {
                if (!stringArray[1].startsWith("L")) continue;
                return 0;
            }
            if (string.equals("Waiting...") || string.startsWith("Starting in")) {
                return 1;
            }
            if (!string.startsWith("R Red:") && !string.startsWith("B Blue:")) continue;
            return 2;
        }
        return -1;
    }

    public static float computeFloat(float f, float f2, float f3, float f4) {
        float f5 = 1.0f - (float)Math.pow(1.0f - f4, f3);
        return f + f5 * (f2 - f);
    }

    public static boolean matchesDouble(double d) {
        return d == Math.floor(d);
    }

    public static GuiScreen getCurrentScreen() {
        return GetCurrentScreenHelper.getMinecraft().currentScreen;
    }

    public static WorldClient getWorld() {
        return GetCurrentScreenHelper.getMinecraft().theWorld;
    }

    public static int computeInt(float f) {
        return (double)f >= 6.0 ? 65280 : ((double)f >= 3.0 ? 0xFFFF00 : 0xFF0000);
    }

    public static List<String> computeList2() {
        ArrayList<String> arrayList = new ArrayList<String>();
        if (GetCurrentScreenHelper.mc.theWorld == null) {
            return arrayList;
        }
        Scoreboard scoreboard = GetCurrentScreenHelper.mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return arrayList;
        }
        ScoreObjective scoreObjective = scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreObjective == null) {
            return arrayList;
        }
        Collection<Score> collection = scoreboard.getSortedScores(scoreObjective);
        ArrayList<Score> arrayList2 = new ArrayList<Score>();
        for (Score object : collection) {
            if (object == null || object.getPlayerName() == null || object.getPlayerName().startsWith("#")) continue;
            arrayList2.add(object);
        }
        collection = arrayList2.size() > 15 ? new ArrayList<Score>(Lists.newArrayList(Iterables.skip(arrayList2, arrayList2.size() - 15))) : arrayList2;
        int n = 0;
        for (Score score : collection) {
            ScorePlayerTeam scorePlayerTeam = scoreboard.getPlayersTeam(score.getPlayerName());
            arrayList.add(ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.getPlayerName()));
            if (++n != collection.size()) continue;
            arrayList.add(scoreObjective.getDisplayName());
        }
        Collections.reverse(arrayList);
        return arrayList;
    }

    public static String computeString2(String string) {
        return string.replace("\u00a7k", "").replace("\u00a7l", "").replace("\u00a7m", "").replace("\u00a7n", "").replace("\u00a7o", "").replace("\u00a7r", "");
    }

    public static float computeFloat(ItemStack itemStack, Block block) {
        int n;
        float f = itemStack.getStrVsBlock(block);
        if (f > 1.0f && (n = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, itemStack)) > 0) {
            f += (float)(n * n + 1);
        }
        return f;
    }

    public static ItemStack getStackInSlot(int n) {
        return GetCurrentScreenHelper.getPlayer().inventoryContainer.getSlot(n).getStack();
    }

    public static int computeInt2(int n, int n2) {
        return n & 0xFFFFFF | n2 << 24;
    }

    public static boolean matchesFloat(float f, double d, double d2) {
        f = (float)((double)f * 0.5);
        double d3 = MathHelper.wrapAngleTo180_double((GetCurrentScreenHelper.mc.thePlayer.rotationYaw - OnJumpHelper.computeFloat(d, d2)) % 360.0f);
        return d3 > 0.0 ? d3 < (double)f : d3 > (double)(-f);
    }

    public static GameSettings getGameSettings() {
        return GetCurrentScreenHelper.getMinecraft().gameSettings;
    }

    public static float computeFloat(EntityLivingBase entityLivingBase) {
        return entityLivingBase.getHealth() + entityLivingBase.getAbsorptionAmount();
    }

    public static int computeInt(EntityPlayer entityPlayer) {
        for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); ++i) {
            ItemStack itemStack = entityPlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !(itemStack.getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    public static Minecraft getMinecraft() {
        return Minecraft.getMinecraft();
    }

    public static int computeInt(Entity entity) {
        String string;
        Object object;
        if (entity instanceof EntityPlayer && (object = (ScorePlayerTeam)((EntityLivingBase)entity).getTeam()) != null && (string = FontRenderer.getFormatFromString(((ScorePlayerTeam)object).getColorPrefix())).length() >= 2) {
            return mc.getRenderManager().getFontRenderer().getColorCode(string.charAt(1));
        }
        object = entity.getDisplayName().getFormattedText();
        if (((String)(object = GetCurrentScreenHelper.computeString2((String)object))).isEmpty() || !((String)object).startsWith("\u00a7") || ((String)object).charAt(1) == 'f') {
            return -1;
        }
        switch (((String)object).charAt(1)) {
            case '0': {
                return count15;
            }
            case '1': {
                return count9;
            }
            case '2': {
                return count5;
            }
            case '3': {
                return count8;
            }
            case '4': {
                return count;
            }
            case '5': {
                return count12;
            }
            case '6': {
                return count3;
            }
            case '7': {
                return count13;
            }
            case '8': {
                return count14;
            }
            case '9': {
                return count10;
            }
            case 'a': {
                return count6;
            }
            case 'b': {
                return count7;
            }
            case 'c': {
                return count2;
            }
            case 'd': {
                return count11;
            }
            case 'e': {
                return count4;
            }
        }
        return -1;
    }

    public static int computeInt(EntityPlayer entityPlayer, Item item) {
        for (int i = 0; i < entityPlayer.inventory.getSizeInventory(); ++i) {
            ItemStack itemStack = entityPlayer.inventory.getStackInSlot(i);
            if (itemStack == null || itemStack.getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static int computeInt() {
        int n = 0;
        for (Object e : GetCurrentScreenHelper.mc.theWorld.loadedEntityList) {
            ++n;
        }
        return n;
    }

    public static boolean matchesEntityLivingBase2(EntityLivingBase entityLivingBase) {
        return entityLivingBase.getHeldItem() != null && entityLivingBase.getHeldItem().getItem() instanceof ItemFood;
    }

    public static boolean matchesEntity2(Entity entity) {
        try {
            Entity entity2 = entity;
            if (GetCurrentScreenHelper.mc.thePlayer.isOnSameTeam((EntityLivingBase)entity) || GetCurrentScreenHelper.mc.thePlayer.getDisplayName().getUnformattedText().startsWith(entity2.getDisplayName().getUnformattedText().substring(0, 2)) || GetCurrentScreenHelper.computeString().startsWith(entity2.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        catch (Exception exception) {
        }
        return false;
    }

    private static int computeInt(String string) {
        if (string == null) {
            return 0;
        }
        int n = 0;
        for (char c : string.toCharArray()) {
            n += c;
            n = n << 1 | n >>> 31;
        }
        return n;
    }

    public static String computeString(Object object) {
        if (object instanceof NetworkPlayerInfo) {
            return ((NetworkPlayerInfo)object).getGameProfile().getName();
        }
        return null;
    }

    public static List<EntityPlayer> computeList() {
        return GetCurrentScreenHelper.getWorld().playerEntities;
    }

    public static void handleString2(String string) {
        GetCurrentScreenHelper.getPlayer().addChatMessage(new ChatComponentText(string));
    }

    public static boolean matchesItemStack(ItemStack itemStack) {
        return itemStack != null && itemStack.getItem() instanceof ItemPotion && !ItemPotion.isSplash(itemStack.getItemDamage());
    }

    public static boolean checkState() {
        return GetCurrentScreenHelper.matchesEntityLivingBase(Minecraft.getMinecraft().thePlayer);
    }

    private static boolean matchesInt(int n, int n2) {
        return (n ^ n2) == 0;
    }

    public static boolean checkState5() {
        for (int i = (int)GetCurrentScreenHelper.mc.thePlayer.posY; i > -1; --i) {
            if (GetCurrentScreenHelper.mc.theWorld.getBlockState(new BlockPos(GetCurrentScreenHelper.mc.thePlayer.posX, (double)i, GetCurrentScreenHelper.mc.thePlayer.posZ)).getBlock() instanceof BlockAir) continue;
            return false;
        }
        return true;
    }

    private static void process() {
        try {
            GetCurrentScreenHelper.longValueArray[0] = System.currentTimeMillis();
            int n = 0;
            for (int i = 0; i < 4; ++i) {
                n += intValueArray[i];
                n = n << 1 | n >>> 31;
            }
            if ((n & 0xFF) != 90) return;
        }
        catch (Exception exception) {
        }
    }

    public static float computeFloat(float f) {
        return Math.abs(f) * -1.0f;
    }

    public static boolean matchesEntity(Entity entity) {
        if (!(entity instanceof EntityPlayer)) {
            return false;
        }
        return ((EntityPlayer)entity).isUsingItem() && GetCurrentScreenHelper.matchesEntityLivingBase2((EntityLivingBase)entity);
    }

    public static int computeInt(int n, int n2) {
        return random.nextInt(n2 - n + 1) + n;
    }

    public static boolean matchesEntityLivingBase(EntityLivingBase entityLivingBase) {
        if (entityLivingBase.getHeldItem() == null) {
            return false;
        }
        Item item = entityLivingBase.getHeldItem().getItem();
        return item instanceof ItemSword;
    }

    public static boolean isMining() {
        return GetCurrentScreenHelper.checkState8() && Mouse.isButtonDown(0) && GetCurrentScreenHelper.mc.objectMouseOver != null && GetCurrentScreenHelper.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && GetCurrentScreenHelper.mc.objectMouseOver.getBlockPos() != null;
    }

    public static boolean checkState6() {
        if (Minecraft.getMinecraft().thePlayer.getHeldItem() == null) {
            return false;
        }
        return Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    public static EntityPlayerSP getPlayer() {
        return GetCurrentScreenHelper.getMinecraft().thePlayer;
    }

    public static FontRenderer computeFontRenderer() {
        return GetCurrentScreenHelper.getMinecraft().fontRendererObj;
    }

    public static long computeLong(long l, long l2) {
        return Math.abs(l2 - l);
    }

    public static double computeDouble(double d, int n) {
        if (n == 0) {
            return Math.round(d);
        }
        double d2 = Math.pow(10.0, n);
        return (double)Math.round(d * d2) / d2;
    }

    public static NetHandlerPlayClient getNetHandler() {
        return GetCurrentScreenHelper.getMinecraft().getNetHandler();
    }

    public static void handleString(String string) {
        Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(string));
    }

    public static void handleString3(String string) {
        GetCurrentScreenHelper.getPlayer().addChatMessage(new ChatComponentText("\u00a78[\u00a7cS\u00a78]\u00a77 " + string));
    }

    public static int computeInt(int n, double d) {
        int n2 = n >> 24;
        int n3 = (0xFF | ~n2) - ~n2;
        int n4 = n >> 16 & 0xFF;
        int n5 = n >> 8;
        int n6 = (0xFF | ~n5) - ~n5;
        int n7 = n;
        int n8 = (0xFF | ~n7) - ~n7;
        d = (100.0 - d) / 100.0;
        n4 = (int)((double)n4 * d);
        n6 = (int)((double)n6 * d);
        n8 = (int)((double)n8 * d);
        n4 = GetCurrentScreenHelper.computeInt(n4);
        n6 = GetCurrentScreenHelper.computeInt(n6);
        n8 = GetCurrentScreenHelper.computeInt(n8);
        int n9 = n3 << 24 | n4 << 16 | n6 << 8 | n8;
        return n9;
    }

    public static boolean checkState4() {
        return IsKeyDownHelper.matchesInt(GetCurrentScreenHelper.mc.gameSettings.keyBindJump.getKeyCode());
    }

    public static boolean checkState8() {
        return GetCurrentScreenHelper.mc.currentScreen == null && GetCurrentScreenHelper.mc.inGameHasFocus;
    }

    public static float computeFloat(float f, float f2, float f3) {
        return f + f3 * (f2 - f);
    }

    public static void handleDouble(double d) {
        GetCurrentScreenHelper.mc.thePlayer.motionX *= d;
        GetCurrentScreenHelper.mc.thePlayer.motionZ *= d;
    }

    public static Block getBlock(BlockPos blockPos) {
        return GetCurrentScreenHelper.getWorld().getBlockState(blockPos).getBlock();
    }

    public static int computeInt(Block block) {
        float f = 1.0f;
        int n = -1;
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            float f2;
            ItemStack itemStack = GetCurrentScreenHelper.mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null || !((f2 = GetCurrentScreenHelper.computeFloat(itemStack, block)) > f)) continue;
            f = f2;
            n = i;
        }
        return n;
    }

    public static boolean checkState2() {
        return GetCurrentScreenHelper.mc.thePlayer != null && GetCurrentScreenHelper.mc.theWorld != null;
    }

    public static double computeDouble(NumberSetting numberSetting, NumberSetting numberSetting2, Random random) {
        return numberSetting.computeDouble() == numberSetting2.computeDouble() ? numberSetting.computeDouble() : numberSetting.computeDouble() + random.nextDouble() * (numberSetting2.computeDouble() - numberSetting.computeDouble());
    }

    public static int computeInt(int n) {
        if (n > 255) {
            return 255;
        }
        if (n < 4) {
            return 4;
        }
        return n;
    }

    public static boolean matchesFloat(float f, float f2, float f3) {
        f2 *= 0.5f;
        double d = MathHelper.wrapAngleTo180_double((f - f3) % 360.0f);
        if (d > 0.0) {
            return d < (double)f2;
        }
        return d > (double)(-f2);
    }

    public static boolean checkState3() {
        return GetCurrentScreenHelper.getGameSettings().thirdPersonView == 0;
    }

    public static String computeString() {
        try {
            NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(GetCurrentScreenHelper.mc.thePlayer.getUniqueID());
            return ScorePlayerTeam.formatPlayerName(networkPlayerInfo.getPlayerTeam(), networkPlayerInfo.getGameProfile().getName());
        }
        catch (Exception exception) {
            return "";
        }
    }

    public static double computeDouble(double d, double d2) {
        return d + random.nextDouble() * (d2 - d);
    }

    public static EntityLivingBase computeEntityLivingBase(int n) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity entity = null;
        MovingObjectPosition movingObjectPosition = minecraft.thePlayer.rayTrace(n, 1.0f);
        Vec3 vec3 = minecraft.thePlayer.getPositionEyes(1.0f);
        float f = minecraft.thePlayer.rotationYaw;
        float f2 = minecraft.thePlayer.rotationPitch;
        float f3 = MathHelper.cos(-f * ((float)Math.PI / 180) - (float)Math.PI);
        float f4 = MathHelper.sin(-f * ((float)Math.PI / 180) - (float)Math.PI);
        float f5 = -MathHelper.cos(-f2 * ((float)Math.PI / 180));
        float f6 = MathHelper.sin(-f2 * ((float)Math.PI / 180));
        Vec3 vec32 = new Vec3(f4 * f5, f6, f3 * f5);
        Vec3 vec33 = vec3.addVector(vec32.xCoord * (double)n, vec32.yCoord * (double)n, vec32.zCoord * (double)n);
        Vec3 vec34 = null;
        List<Entity> list = minecraft.theWorld.getEntitiesWithinAABBExcludingEntity(minecraft.thePlayer, minecraft.thePlayer.getEntityBoundingBox().addCoord(vec32.xCoord * (double)n, vec32.yCoord * (double)n, vec32.zCoord * (double)n).expand(1.0, 1.0, 1.0));
        double d = n;
        for (Entity entity2 : list) {
            double d2;
            if (!entity2.canBeCollidedWith()) continue;
            float f7 = entity2.getCollisionBorderSize();
            AxisAlignedBB axisAlignedBB = entity2.getEntityBoundingBox().expand(f7, f7, f7);
            MovingObjectPosition movingObjectPosition2 = axisAlignedBB.calculateIntercept(vec3, vec33);
            if (axisAlignedBB.isVecInside(vec3)) {
                if (!(d >= 0.0) && d != 0.0) continue;
                entity = entity2;
                vec34 = movingObjectPosition2 == null ? vec3 : movingObjectPosition2.hitVec;
                d = 0.0;
                continue;
            }
            if (movingObjectPosition2 == null || !((d2 = vec3.distanceTo(movingObjectPosition2.hitVec)) < d) && d != 0.0) continue;
            entity = entity2;
            vec34 = movingObjectPosition2.hitVec;
            d = d2;
        }
        if (entity != null && (d < (double)n || movingObjectPosition == null)) {
            movingObjectPosition = new MovingObjectPosition(entity, vec34);
        }
        if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && movingObjectPosition.entityHit instanceof EntityLivingBase) {
            return (EntityLivingBase)movingObjectPosition.entityHit;
        }
        return null;
    }

    public static PlayerControllerMP computePlayerControllerMP() {
        return GetCurrentScreenHelper.getMinecraft().playerController;
    }

    public static boolean matchesFloat(float f, Entity entity) {
        return GetCurrentScreenHelper.matchesFloat(f, entity.posX, entity.posZ);
    }

    public static EntityRenderer getEntityRenderer() {
        return GetCurrentScreenHelper.getMinecraft().entityRenderer;
    }
}
