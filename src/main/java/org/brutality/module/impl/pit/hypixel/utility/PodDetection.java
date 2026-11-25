package org.brutality.module.impl.pit.hypixel.utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent2;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetHealthEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.hypixel.pit.EquipmentHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public final class PodDetection extends Module {
    private static PodDetection instance;
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final BooleanSetting estimateFromLaunchSetting = new BooleanSetting("Estimate From Launch", this, true);
    private final EquipmentHelper equipmentHelper = new EquipmentHelper();
    private final Map<UUID, EntityPlayer> lookupMap = new HashMap<>();
    private Object object;
    private Object object2;
    private static final Pattern pattern = Pattern.compile("(?i)^Escape Pod(?:\\s+(III|II|I|[123]))?$");

    @Override
    public void onDisable() {
        this.clear();
        this.object = this.object2 = null;
    }

    private void clear() {
        this.equipmentHelper.clear();
        this.lookupMap.clear();
    }

    public static PodDetection getInstance() {
        return instance;
    }

    public String computeString(EntityPlayer player) {
        if (this.checkState44() && player != mc.thePlayer) {
            this.process();
            EquipmentHelper.Entry2 entry2 = this.equipmentHelper.computeEntry2(player.getUniqueID(), System.currentTimeMillis());
            if (entry2 != null && player.isEntityAlive() && this.lookupMap.get(player.getUniqueID()) == player) {
                int n = (int)Math.max(0L, (entry2.timestamp - System.currentTimeMillis() + 999L) / 1000L);
                String str = new String[]{"", "I", "II", "III", "IV"}[entry2.getCount()];
                String str2 = entry2.flag ? "~" : "";
                return EnumChatFormatting.LIGHT_PURPLE + "[Regen " + str + " - " + str2 + n + "] ";
            }
            return "";
        }
        return "";
    }

    public static void handleEntityPlayer(EntityPlayer player, int n, int n2, boolean flag) {
        PodDetection podDetection = instance;
        if (podDetection != null && podDetection.checkState44() && mc.theWorld != null && mc.thePlayer != null) {
            podDetection.process();
            if (podDetection.matchesEntityPlayer(player)) {
                if (flag) {
                    podDetection.equipmentHelper.handleUUID(player.getUniqueID());
                } else {
                    long now = System.currentTimeMillis();
                    podDetection.equipmentHelper.handleUUID(player.getUniqueID(), computeInt(player.inventory.armorInventory[1]), player.getHealth(), player.posY, now, false);
                    podDetection.equipmentHelper.handleUUID(player.getUniqueID(), n, n2, now);
                }
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        if (player == mc.thePlayer) {
            return false;
        }
        UUID uniqueID = player.getUniqueID();
        EntityPlayer player2 = this.lookupMap.put(uniqueID, player);
        if (player2 != null && player2 != player) {
            this.equipmentHelper.remove(uniqueID);
        }

        if (player.isEntityAlive() && (!player.onGround || !FreshHelper.matchesWorld2(mc.theWorld, player))) {
            return true;
        }
        this.equipmentHelper.remove(uniqueID);
        return false;
    }

    public PodDetection() {
        super("PodDetection", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.estimateFromLaunchSetting});
        instance = this;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.clear();
            } else {
                this.process();
                if (mc.theWorld != null && mc.thePlayer != null) {
                    if (event instanceof GetEntityEvent && ((GetEntityEvent)event).getEntity() instanceof EntityPlayer) {
                        this.equipmentHelper.remove(((EntityPlayer)((GetEntityEvent)event).getEntity()).getUniqueID());
                    } else {
                        EntityPlayer player = null;
                        if (event instanceof AttackEvent2 && ((AttackEvent2)event).getEntityLivingBase() instanceof EntityPlayer) {
                            player = (EntityPlayer)((AttackEvent2)event).getEntityLivingBase();
                        } else if (event instanceof GetHealthEvent getHealthEvent
                            && getHealthEvent.getEntity() instanceof EntityPlayer
                            && getHealthEvent.getHealth() < getHealthEvent.getPreviousHealth()) {
                            player = (EntityPlayer)getHealthEvent.getEntity();
                        }

                        if (player != null && this.matchesEntityPlayer(player) && !player.isPotionActive(Potion.poison)) {
                            this.equipmentHelper
                                .handleUUID(
                                    player.getUniqueID(), computeInt(player.inventory.armorInventory[1]), player.getHealth(), player.posY, System.currentTimeMillis()
                                );
                        }

                        if (event instanceof EventTick && ((EventTick)event).getState() == EventState.PRE) {
                            long now = System.currentTimeMillis();
                            HashSet set = new HashSet();

                            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                                if (playerEntity != mc.thePlayer) {
                                    set.add(playerEntity.getUniqueID());
                                    boolean flag = !this.lookupMap.containsKey(playerEntity.getUniqueID());
                                    if (this.matchesEntityPlayer(playerEntity)) {
                                        int n = computeInt(playerEntity.inventory.armorInventory[1]);
                                        this.equipmentHelper
                                            .handleUUID(
                                                playerEntity.getUniqueID(),
                                                n,
                                                playerEntity.getHealth(),
                                                playerEntity.posY,
                                                now,
                                                this.estimateFromLaunchSetting.isEnabled() && !playerEntity.isPotionActive(Potion.poison)
                                            );
                                        PotionEffect activePotionEffect = playerEntity.getActivePotionEffect(Potion.regeneration);
                                        if (flag && activePotionEffect != null) {
                                            this.equipmentHelper.handleUUID(playerEntity.getUniqueID(), activePotionEffect.getAmplifier(), activePotionEffect.getDuration(), now);
                                        }
                                    }
                                }
                            }

                            this.equipmentHelper.handleSet(set);
                            this.lookupMap.keySet().retainAll(set);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.clear();
        this.object = mc.theWorld;
        this.object2 = mc.thePlayer;
    }

    private void process() {
        if (this.object != mc.theWorld || this.object2 != mc.thePlayer) {
            this.clear();
            this.object = mc.theWorld;
            this.object2 = mc.thePlayer;
        }
    }

    public static int computeInt(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2 && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            NBTTagCompound nbt = tagCompound.hasKey("ExtraAttributes", 10) ? tagCompound.getCompoundTag("ExtraAttributes") : tagCompound;
            NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                if ("escape_pod".equalsIgnoreCase(compoundTagAt.getString("Key"))) {
                    int integer = compoundTagAt.getInteger("Level");
                    return integer >= 1 && integer <= 3 ? integer : 0;
                }
            }

            NBTTagList tagList2 = tagCompound.getCompoundTag("display").getTagList("Lore", 8);

            for (int j = 0; j < tagList2.tagCount(); j++) {
                Matcher matcher = pattern.matcher(StringUtils.stripControlCodes(tagList2.getStringTagAt(j)).trim());
                if (matcher.matches()) {
                    String str = matcher.group(1);
                    if (str != null && !"I".equalsIgnoreCase(str) && !"1".equals(str)) {
                        if (!"II".equalsIgnoreCase(str) && !"2".equals(str)) {
                            return 3;
                        }

                        return 2;
                    }

                    return 1;
                }
            }

            return 0;
        }
        return 0;
    }
}
