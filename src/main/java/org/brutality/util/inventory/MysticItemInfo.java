package org.brutality.util.inventory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class MysticItemInfo {
    public boolean flag;
    public int count;
    public long timestamp;
    public boolean flag2;
    public int count2;
    public int count3;
    public Map<String, Integer> lookupMap = new LinkedHashMap<>();
    public Map<VenomType, Integer> lookupMap2 = new LinkedHashMap<>();
    public int count4;

    public MysticItemInfo() {
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lookupMap, this.count2, this.count3, this.flag2, this.flag);
    }

    @Override
    public String toString() {
        boolean flag = this.flag;
        boolean flag2 = this.flag2;
        int n = this.count3;
        int n2 = this.count2;
        Map lookupMap = this.lookupMap;
        return "MysticItemInfo{enchantLevelsByKey="
            + lookupMap
            + ", maximumLives="
            + n2
            + ", tier="
            + n
            + ", gemmed="
            + flag2
            + ", hasCustomEnchantments="
            + flag
            + "}";
    }

    public MysticItemInfo(ItemStack stack) {
        this();
        if (stack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }
        this.timestamp = GetMysticEnchantLoreHelper.computeLong(stack);
        NBTTagCompound compoundTag = stack.writeToNBT(new NBTTagCompound()).getCompoundTag("tag").getCompoundTag("ExtraAttributes");
        NBTTagList tagList = compoundTag.getTagList("CustomEnchants", 10);
        this.flag = !tagList.hasNoTags();
        if (this.flag) {
            this.count = compoundTag.getInteger("Lives");
            this.count2 = compoundTag.getInteger("MaxLives");
            this.count3 = compoundTag.getInteger("UpgradeTier");
            this.flag2 = compoundTag.getInteger("UpgradeGemsUses") == 1;

            for (int i = 0; i < 3; i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                String str = compoundTagAt.getString("Key");
                int integer = compoundTagAt.getInteger("Level");
                this.lookupMap.put(str, integer);
                this.count4 += integer;
            }

            for (int j = 0; j < 3; j++) {
                NBTTagCompound compoundTagAt2 = tagList.getCompoundTagAt(j);
                String str2 = compoundTagAt2.getString("Key");
                int integer2 = compoundTagAt2.getInteger("Level");
                VenomType venomType = VenomType.computeVenomType(str2);
                if (venomType != null) {
                    this.lookupMap2.put(venomType, integer2);
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && this.getClass() == obj.getClass()) {
            MysticItemInfo mysticItemInfo = (MysticItemInfo)obj;
            return this.count2 == mysticItemInfo.count2
                && this.count3 == mysticItemInfo.count3
                && this.flag2 == mysticItemInfo.flag2
                && this.flag == mysticItemInfo.flag
                && matchesMap(this.lookupMap, mysticItemInfo.lookupMap);
        }
        return false;
    }

    private static <K, V> boolean matchesMap(Map<K, V> map, Map<K, V> map2) {
        if (map.size() != map2.size()) {
            return false;
        }
        Iterator it = map.entrySet().iterator();
        Iterator it2 = map2.entrySet().iterator();

        while (it.hasNext()) {
            Entry entry = (Entry)it.next();
            Entry entry2 = (Entry)it2.next();
            if (!Objects.equals(entry.getKey(), entry2.getKey()) || !Objects.equals(entry.getValue(), entry2.getValue())) {
                return false;
            }
        }

        return true;
    }
}
