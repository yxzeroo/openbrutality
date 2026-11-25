package org.brutality.module.impl.pit.hypixel.swap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Vec3;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent2;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.PruneHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class AntiVenomSwap extends Module {
    private static AntiVenomSwap instance;
    private final PitCategory pitCategory = PitCategory.SWAP;
    private final NumberSetting distanceSetting = new NumberSetting("Distance", this, 7.0, 1.0, 20.0, 0.5);
    private final NumberSetting hitRangeSetting = new NumberSetting("Hit Range", this, 4.5, 1.0, 6.0, 0.5);
    private static final long timestamp = 3500L;
    private final NumberSetting swapDelaySetting = new NumberSetting("Swap Delay", this, 75.0, 0.0, 500.0, 25.0);
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final PruneHelper pruneHelper = new PruneHelper();
    private final Set<UUID> itemSet = new HashSet<>();
    private EntityPlayerSP entityPlayerSP;
    private Object object;
    private ItemStack itemStack;
    private ItemStack itemStack2;
    private boolean flag;
    private boolean flag2;
    private boolean flag3;
    private boolean flag4;

    @Override
    public void onDisable() {
        this.pruneHelper.clear();
        this.itemSet.clear();
        this.flag3 = this.flag || this.flag2;
        this.process4();
    }

    private static boolean matchesItemStack(ItemStack stack) {
        return stack != null
            && stack.getItem() instanceof ItemArmor
            && ((ItemArmor)stack.getItem()).armorType == 2
            && stack.hasTagCompound()
            && stack.getTagCompound().toString().toLowerCase(Locale.ROOT).contains("somber");
    }

    private boolean checkState() {
        return mc.thePlayer != null
            && mc.theWorld != null
            && mc.thePlayer.isEntityAlive()
            && !StartHotbarMovesHelper.flag
            && mc.thePlayer.inventory.getItemStack() == null
            && (mc.currentScreen == null || mc.currentScreen instanceof GuiInventory);
    }

    protected long computeLong() {
        return System.currentTimeMillis();
    }

    protected void handleRunnable(Runnable task) {
        StartHotbarMovesHelper.startArmorRemoval(2, true, task);
    }

    private void process4() {
        if (this.entityPlayerSP != null && mc.thePlayer != this.entityPlayerSP) {
            this.process6();
        } else if (this.flag && !this.flag2) {
            if (!this.checkState44()) {
                this.flag3 = true;
            }

            if (this.flag3 && this.checkState()) {
                if (!matchesItemStack(this.entityPlayerSP.inventory.armorInventory[1], this.itemStack2)) {
                    this.process6();
                } else {
                    int n = this.itemStack == null ? -1 : StartHotbarMovesHelper.computeInt3(this.itemStack, 2);
                    if (this.itemStack != null && n < 0) {
                        if (!this.flag4) {
                            MessageUtils.x("AntiVenomSwap: waiting for original pants to restore.");
                            this.flag4 = true;
                        }
                    } else if (this.itemStack != null || this.entityPlayerSP.inventory.getFirstEmptyStack() >= 0) {
                        this.flag2 = true;
                        Runnable task = () -> mc.addScheduledTask(() -> {
                            this.flag2 = false;
                            if (mc.thePlayer != this.entityPlayerSP || matchesItemStack(this.entityPlayerSP.inventory.armorInventory[1], this.itemStack)) {
                                this.process6();
                            }
                        });
                        if (this.itemStack == null) {
                            this.handleRunnable(task);
                        } else {
                            this.handleInt(n, task);
                        }
                    }
                }
            }
        }
    }

    public AntiVenomSwap() {
        super("AntiVenomSwap", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.distanceSetting, this.hitRangeSetting, this.swapDelaySetting, this.silentSetting});
        instance = this;
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        return player != mc.thePlayer && player.isEntityAlive() && mc.thePlayer.getDistanceToEntity(player) <= this.distanceSetting.computeDouble();
    }

    private int computeInt() {
        ArrayList list = new ArrayList();

        for (int i = 0; i < 36; i++) {
            if (matchesItemStack(mc.thePlayer.inventory.mainInventory[i])) {
                list.add(i < 9 ? 36 + i : i);
            }
        }

        return list.isEmpty() ? -1 : (Integer)list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    public static void process() {
        AntiVenomSwap antiVenomSwap = instance;
        if (antiVenomSwap != null && (antiVenomSwap.flag3 || antiVenomSwap.flag || antiVenomSwap.flag2)) {
            antiVenomSwap.process4();
        }
    }

    private void process3() {
        if (!this.flag && !this.flag2 && !this.flag3 && this.checkState()) {
            ItemStack stack = mc.thePlayer.inventory.armorInventory[1];
            if (!matchesItemStack(stack)) {
                int n = this.computeInt();
                if (n < 0) {
                    if (!this.flag4) {
                        MessageUtils.x("AntiVenomSwap: no Somber pants found.");
                        this.flag4 = true;
                    }
                } else {
                    this.flag4 = false;
                    this.entityPlayerSP = mc.thePlayer;
                    this.itemStack = stack == null ? null : stack.copy();
                    this.itemStack2 = mc.thePlayer.inventory.mainInventory[n < 36 ? n : n - 36].copy();
                    this.flag = true;
                    this.flag2 = true;
                    this.handleInt(n, () -> mc.addScheduledTask(() -> {
                        this.flag2 = false;
                        if (mc.thePlayer != this.entityPlayerSP) {
                            this.process6();
                        } else if (!matchesItemStack(this.entityPlayerSP.inventory.armorInventory[1], this.itemStack2)) {
                            this.process6();
                        } else {
                            if (!this.checkState44()) {
                                this.flag3 = true;
                            }

                            this.process4();
                        }
                    }));
                }
            }
        }
    }

    private static boolean matchesItemStack(ItemStack stack, ItemStack stack2) {
        return stack != null && stack2 != null ? stack.getItem() == stack2.getItem() && ItemStack.areItemStackTagsEqual(stack, stack2) : stack == stack2;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
            if (event instanceof RenderEvent4) {
                this.pruneHelper.clear();
                this.itemSet.clear();
                this.flag3 = this.flag3 | (this.flag || this.flag2);
            } else if (mc.thePlayer != null && mc.theWorld != null) {
                if (this.object != mc.theWorld) {
                    this.pruneHelper.clear();
                    this.itemSet.clear();
                    this.object = mc.theWorld;
                }

                if (event instanceof AttackEvent2 && ((AttackEvent2)event).getEntityLivingBase() == mc.thePlayer) {
                    this.process5();
                } else if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                    HashSet set = new HashSet();

                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (this.matchesEntityPlayer(playerEntity)) {
                            set.add(playerEntity.getUniqueID());
                        }
                    }

                    this.pruneHelper.prune(set, this.computeLong(), 3500L);
                    this.itemSet.retainAll(set);
                    if (this.flag && this.itemSet.isEmpty()) {
                        this.flag3 = true;
                    }

                    this.process4();
                    if (!this.itemSet.isEmpty() && !this.flag && !this.flag3) {
                        this.process3();
                    }
                }
            }
        }
    }

    private static boolean matchesItemStack(ItemStack stack, String str) {
        if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2) {
            String str2 = StringUtils.stripControlCodes(stack.getDisplayName()).toLowerCase(Locale.ROOT);
            if (str2.matches(".*\\b" + str + "\\b.*")) {
                return true;
            }
            if (!stack.hasTagCompound()) {
                return false;
            }
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes", 10) ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
            NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                if (str.equalsIgnoreCase(compoundTagAt.getString("Key")) && compoundTagAt.getInteger("Level") > 0) {
                    return true;
                }
            }

            NBTTagList tagList2 = tagCompound.getCompoundTag("display").getTagList("Lore", 8);

            for (int j = 0; j < tagList2.tagCount(); j++) {
                String str3 = StringUtils.stripControlCodes(tagList2.getStringTagAt(j)).toLowerCase(Locale.ROOT);
                if (str3.matches(".*\\b" + str + "\\b.*")) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    private void process6() {
        this.entityPlayerSP = null;
        this.itemStack = this.itemStack2 = null;
        this.flag = this.flag2 = this.flag3 = this.flag4 = false;
        this.itemSet.clear();
        this.pruneHelper.clear();
    }

    @Override
    public void onEnable() {
        this.pruneHelper.clear();
        this.itemSet.clear();
    }

    private void process5() {
        if (!mc.thePlayer.isEntityAlive()) {
            this.flag3 = this.flag3 | this.flag;
            this.pruneHelper.clear();
        } else {
            EntityPlayer player = null;

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (this.matchesEntityPlayer(playerEntity)
                    && !(mc.thePlayer.getDistanceToEntity(playerEntity) > this.hitRangeSetting.computeDouble())
                    && playerEntity.isSwingInProgress
                    && playerEntity.swingProgressInt <= 5
                    && this.matchesEntityPlayer2(playerEntity)) {
                    if (player != null) {
                        this.pruneHelper.clear();
                        return;
                    }

                    player = playerEntity;
                }
            }

            if (player != null && matchesItemStack(player.inventory.armorInventory[1], "venom")) {
                if (this.pruneHelper.hit(player.getUniqueID(), this.computeLong(), 3500L)) {
                    this.itemSet.add(player.getUniqueID());
                    if (!this.flag) {
                        this.process3();
                    }
                }
            } else {
                this.pruneHelper.clear();
            }
        }
    }

    private boolean matchesEntityPlayer2(EntityPlayer player) {
        Vec3 positionEyes = player.getPositionEyes(1.0F);
        Vec3 lookVec = player.getLookVec();
        double d = this.hitRangeSetting.computeDouble();
        Vec3 vec = positionEyes.addVector(lookVec.xCoord * d, lookVec.yCoord * d, lookVec.zCoord * d);
        MovingObjectPosition hit = mc.thePlayer.getEntityBoundingBox().expand(0.1, 0.1, 0.1).calculateIntercept(positionEyes, vec);
        if (hit == null) {
            return false;
        }
        double d2 = positionEyes.distanceTo(hit.hitVec);
        MovingObjectPosition hit2 = mc.theWorld.rayTraceBlocks(positionEyes, hit.hitVec, false, true, false);
        if (hit2 != null && positionEyes.distanceTo(hit2.hitVec) < d2) {
            return false;
        }
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity != player && entity != mc.thePlayer && entity.canBeCollidedWith()) {
                MovingObjectPosition hit3 = entity.getEntityBoundingBox().expand(0.1, 0.1, 0.1).calculateIntercept(positionEyes, vec);
                if (hit3 != null && positionEyes.distanceTo(hit3.hitVec) < d2) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void handleInt(int n, Runnable task) {
        if (this.silentSetting.matchesString2("Silent")) {
            StartHotbarMovesHelper.handleInt2(2, n, true, (long)this.swapDelaySetting.computeDouble(), false, false, task);
        } else {
            StartHotbarMovesHelper.handleInt(2, n, true, (long)this.swapDelaySetting.computeDouble(), false, false, task);
        }
    }
}
