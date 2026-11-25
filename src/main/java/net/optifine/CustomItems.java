package net.optifine;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.src.Config;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.NbtTagValue;
import net.optifine.render.Blender;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.StrUtils;
import org.brutality.module.impl.visual.FPSBooster;
import org.lwjgl.opengl.GL11;

public class CustomItems {
    private static final IdentityHashMap<ItemStack, CustomItems.FrameMatch> frameMatches = new IdentityHashMap<>();
    private static final List<CustomItems.FrameMatch> frameMatchPool = new ArrayList<>();
    private static int usedFrameMatches;
    private static boolean simplifiedRender;
    private static CustomItemProperties[][] itemProperties = (CustomItemProperties[][])null;
    private static CustomItemProperties[][] enchantmentProperties = (CustomItemProperties[][])null;
    private static Map mapPotionIds = null;
    private static ItemModelGenerator itemModelGenerator = new ItemModelGenerator();
    private static boolean useGlint = true;
    private static boolean renderOffHand = false;
    public static final int MASK_POTION_SPLASH = 16384;
    public static final int MASK_POTION_NAME = 63;
    public static final int MASK_POTION_EXTENDED = 64;
    public static final String KEY_TEXTURE_OVERLAY = "texture.potion_overlay";
    public static final String KEY_TEXTURE_SPLASH = "texture.potion_bottle_splash";
    public static final String KEY_TEXTURE_DRINKABLE = "texture.potion_bottle_drinkable";
    public static final String DEFAULT_TEXTURE_OVERLAY = "items/potion_overlay";
    public static final String DEFAULT_TEXTURE_SPLASH = "items/potion_bottle_splash";
    public static final String DEFAULT_TEXTURE_DRINKABLE = "items/potion_bottle_drinkable";
    private static final int[][] EMPTY_INT2_ARRAY = new int[0][];
    private static final String TYPE_POTION_NORMAL = "normal";
    private static final String TYPE_POTION_SPLASH = "splash";
    private static final String TYPE_POTION_LINGER = "linger";

    public static void beginRenderFrame() {
        frameMatches.clear();

        for (int i = 0; i < usedFrameMatches; i++) {
            frameMatchPool.get(i).clear();
        }

        usedFrameMatches = 0;
    }

    public static boolean beginEntityRender(EntityLivingBase entity) {
        boolean flag = simplifiedRender;
        simplifiedRender = FPSBooster.matchesEntity2(entity);
        return flag;
    }

    public static void endEntityRender(boolean previous) {
        simplifiedRender = previous;
    }

    public static void update() {
        beginRenderFrame();
        itemProperties = (CustomItemProperties[][])null;
        enchantmentProperties = (CustomItemProperties[][])null;
        useGlint = true;
        if (Config.isCustomItems()) {
            readCitProperties("mcpatcher/cit.properties");
            IResourcePack[] resourcePacks = Config.getResourcePacks();

            for (int i = resourcePacks.length - 1; i >= 0; i--) {
                IResourcePack iResourcePack = resourcePacks[i];
                update(iResourcePack);
            }

            update(Config.getDefaultResourcePack());
            if (itemProperties.length <= 0) {
                itemProperties = (CustomItemProperties[][])null;
            }

            if (enchantmentProperties.length <= 0) {
                enchantmentProperties = (CustomItemProperties[][])null;
            }
        }
    }

    private static void readCitProperties(String fileName) {
        try {
            ResourceLocation location = new ResourceLocation(fileName);
            InputStream resourceStream = Config.getResourceStream(location);
            if (resourceStream == null) {
                return;
            }

            Config.dbg("CustomItems: Loading " + fileName);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.load(resourceStream);
            resourceStream.close();
            useGlint = Config.parseBoolean(propertiesOrdered.getProperty("useGlint"), true);
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    private static void update(IResourcePack rp) {
        String[] parts = ResUtils.collectFiles(rp, "mcpatcher/cit/", ".properties", (String[])null);
        var map = makeAutoImageProperties(rp);
        if (map.size() > 0) {
            var set = map.keySet();
            String[] parts2 = (String[])(set.toArray(new String[set.size()]));
            parts = (String[])Config.addObjectsToArray(parts, parts2);
        }

        Arrays.sort(parts);
        var list = makePropertyList(itemProperties);
        var list2 = makePropertyList(enchantmentProperties);

        for (int i = 0; i < parts.length; i++) {
            String str = parts[i];
            Config.dbg("CustomItems: " + str);

            try {
                CustomItemProperties customItemProperties = null;
                if (map.containsKey(str)) {
                    customItemProperties = (CustomItemProperties)map.get(str);
                }

                if (customItemProperties == null) {
                    ResourceLocation location = new ResourceLocation(str);
                    InputStream inputStream = rp.getInputStream(location);
                    if (inputStream == null) {
                        Config.warn("CustomItems file not found: " + str);
                        continue;
                    }

                    PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
                    propertiesOrdered.load(inputStream);
                    customItemProperties = new CustomItemProperties(propertiesOrdered, str);
                }

                if (customItemProperties.isValid(str)) {
                    addToItemList(customItemProperties, list);
                    addToEnchantmentList(customItemProperties, list2);
                }
            } catch (FileNotFoundException e) {
                Config.warn("CustomItems file not found: " + str);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        itemProperties = propertyListToArray(list);
        enchantmentProperties = propertyListToArray(list2);
        var propertiesComparator = getPropertiesComparator();

        for (int j = 0; j < itemProperties.length; j++) {
            CustomItemProperties[] customItemPropertiesArray = itemProperties[j];
            if (customItemPropertiesArray != null) {
                Arrays.sort(customItemPropertiesArray, propertiesComparator);
            }
        }

        for (int k = 0; k < enchantmentProperties.length; k++) {
            CustomItemProperties[] customItemPropertiesArray2 = enchantmentProperties[k];
            if (customItemPropertiesArray2 != null) {
                Arrays.sort(customItemPropertiesArray2, propertiesComparator);
            }
        }
    }

    private static Comparator getPropertiesComparator() {
        return new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                CustomItemProperties customItemProperties = (CustomItemProperties)o1;
                CustomItemProperties customItemProperties2 = (CustomItemProperties)o2;
                return customItemProperties.layer != customItemProperties2.layer
                    ? customItemProperties.layer - customItemProperties2.layer
                    : (
                        customItemProperties.weight != customItemProperties2.weight
                            ? customItemProperties2.weight - customItemProperties.weight
                            : (!customItemProperties.basePath.equals(customItemProperties2.basePath) ? customItemProperties.basePath.compareTo(customItemProperties2.basePath) : customItemProperties.name.compareTo(customItemProperties2.name))
                    );
            }
        };
    }

    public static void updateIcons(TextureMap textureMap) {
        for (CustomItemProperties allProperty : getAllProperties()) {
            allProperty.updateIcons(textureMap);
        }
    }

    public static void loadModels(ModelBakery modelBakery) {
        for (CustomItemProperties allProperty : getAllProperties()) {
            allProperty.loadModels(modelBakery);
        }
    }

    public static void updateModels() {
        for (CustomItemProperties allProperty : getAllProperties()) {
            if (allProperty.type == 1) {
                TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
                allProperty.updateModelTexture(textureMapBlocks, itemModelGenerator);
                allProperty.updateModelsFull();
            }
        }
    }

    private static List<CustomItemProperties> getAllProperties() {
        List list = new ArrayList();
        addAll(itemProperties, list);
        addAll(enchantmentProperties, list);
        return list;
    }

    private static void addAll(CustomItemProperties[][] cipsArr, List<CustomItemProperties> list) {
        if (cipsArr != null) {
            for (int i = 0; i < cipsArr.length; i++) {
                CustomItemProperties[] customItemPropertiesArray = cipsArr[i];
                if (customItemPropertiesArray != null) {
                    for (int j = 0; j < customItemPropertiesArray.length; j++) {
                        CustomItemProperties customItemProperties = customItemPropertiesArray[j];
                        if (customItemProperties != null) {
                            list.add(customItemProperties);
                        }
                    }
                }
            }
        }
    }

    private static Map makeAutoImageProperties(IResourcePack rp) {
        HashMap map = new HashMap();
        map.putAll(makePotionImageProperties(rp, "normal", Item.getIdFromItem(Items.potionitem)));
        map.putAll(makePotionImageProperties(rp, "splash", Item.getIdFromItem(Items.potionitem)));
        map.putAll(makePotionImageProperties(rp, "linger", Item.getIdFromItem(Items.potionitem)));
        return map;
    }

    private static Map makePotionImageProperties(IResourcePack rp, String type, int itemId) {
        HashMap map = new HashMap();
        String str = type + "/";
        String[] parts = new String[]{"mcpatcher/cit/potion/" + str, "mcpatcher/cit/Potion/" + str};
        String[] parts2 = new String[]{".png"};
        String[] parts3 = ResUtils.collectFiles(rp, parts, parts2);

        for (int i = 0; i < parts3.length; i++) {
            String str2 = parts3[i];
            String str3 = StrUtils.removePrefixSuffix(str2, parts, parts2);
            Properties properties = makePotionProperties(str3, type, itemId, str2);
            if (properties != null) {
                String str4 = StrUtils.removeSuffix(str2, parts2) + ".properties";
                CustomItemProperties customItemProperties = new CustomItemProperties(properties, str4);
                map.put(str4, customItemProperties);
            }
        }

        return map;
    }

    private static Properties makePotionProperties(String name, String type, int itemId, String path) {
        if (StrUtils.endsWith(name, new String[]{"_n", "_s"})) {
            return null;
        }
        if (name.equals("empty") && type.equals("normal")) {
            itemId = Item.getIdFromItem(Items.glass_bottle);
            PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
            propertiesOrdered.put("type", "item");
            propertiesOrdered.put("items", String.valueOf(itemId));
            return propertiesOrdered;
        }
        int[] ints = (int[])getMapPotionIds().get(name);
        if (ints == null) {
            Config.warn("Potion not found for image: " + path);
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = 0; i < ints.length; i++) {
            int n = ints[i];
            if (type.equals("splash")) {
                n |= 16384;
            }

            if (i > 0) {
                stringBuffer.append(" ");
            }

            stringBuffer.append(n);
        }

        int n2 = 16447;
        if (name.equals("water") || name.equals("mundane")) {
            n2 |= 64;
        }

        PropertiesOrdered propertiesOrdered2 = new PropertiesOrdered();
        propertiesOrdered2.put("type", "item");
        propertiesOrdered2.put("items", String.valueOf(itemId));
        propertiesOrdered2.put("damage", String.valueOf(stringBuffer.toString()));
        propertiesOrdered2.put("damageMask", String.valueOf((int)n2));
        if (type.equals("splash")) {
            propertiesOrdered2.put("texture.potion_bottle_splash", name);
        } else {
            propertiesOrdered2.put("texture.potion_bottle_drinkable", name);
        }

        return propertiesOrdered2;
    }

    private static Map getMapPotionIds() {
        if (mapPotionIds == null) {
            mapPotionIds = new LinkedHashMap();
            mapPotionIds.put("water", getPotionId(0, 0));
            mapPotionIds.put("awkward", getPotionId(0, 1));
            mapPotionIds.put("thick", getPotionId(0, 2));
            mapPotionIds.put("potent", getPotionId(0, 3));
            mapPotionIds.put("regeneration", getPotionIds(1));
            mapPotionIds.put("movespeed", getPotionIds(2));
            mapPotionIds.put("fireresistance", getPotionIds(3));
            mapPotionIds.put("poison", getPotionIds(4));
            mapPotionIds.put("heal", getPotionIds(5));
            mapPotionIds.put("nightvision", getPotionIds(6));
            mapPotionIds.put("clear", getPotionId(7, 0));
            mapPotionIds.put("bungling", getPotionId(7, 1));
            mapPotionIds.put("charming", getPotionId(7, 2));
            mapPotionIds.put("rank", getPotionId(7, 3));
            mapPotionIds.put("weakness", getPotionIds(8));
            mapPotionIds.put("damageboost", getPotionIds(9));
            mapPotionIds.put("moveslowdown", getPotionIds(10));
            mapPotionIds.put("leaping", getPotionIds(11));
            mapPotionIds.put("harm", getPotionIds(12));
            mapPotionIds.put("waterbreathing", getPotionIds(13));
            mapPotionIds.put("invisibility", getPotionIds(14));
            mapPotionIds.put("thin", getPotionId(15, 0));
            mapPotionIds.put("debonair", getPotionId(15, 1));
            mapPotionIds.put("sparkling", getPotionId(15, 2));
            mapPotionIds.put("stinky", getPotionId(15, 3));
            mapPotionIds.put("mundane", getPotionId(0, 4));
            mapPotionIds.put("speed", mapPotionIds.get("movespeed"));
            mapPotionIds.put("fire_resistance", mapPotionIds.get("fireresistance"));
            mapPotionIds.put("instant_health", mapPotionIds.get("heal"));
            mapPotionIds.put("night_vision", mapPotionIds.get("nightvision"));
            mapPotionIds.put("strength", mapPotionIds.get("damageboost"));
            mapPotionIds.put("slowness", mapPotionIds.get("moveslowdown"));
            mapPotionIds.put("instant_damage", mapPotionIds.get("harm"));
            mapPotionIds.put("water_breathing", mapPotionIds.get("waterbreathing"));
        }

        return mapPotionIds;
    }

    private static int[] getPotionIds(int baseId) {
        return new int[]{baseId, baseId + 16, baseId + 32, baseId + 48};
    }

    private static int[] getPotionId(int baseId, int subId) {
        return new int[]{baseId + subId * 16};
    }

    private static int getPotionNameDamage(String name) {
        String str = "potion." + name;
        Potion[] potionTypes = Potion.potionTypes;

        for (int i = 0; i < potionTypes.length; i++) {
            Potion potion = potionTypes[i];
            if (potion != null) {
                String name2 = potion.getName();
                if (str.equals(name2)) {
                    return potion.getId();
                }
            }
        }

        return -1;
    }

    private static List makePropertyList(CustomItemProperties[][] propsArr) {
        List list = new ArrayList();
        if (propsArr != null) {
            for (int i = 0; i < propsArr.length; i++) {
                CustomItemProperties[] customItemPropertiesArray = propsArr[i];
                List list2 = null;
                if (customItemPropertiesArray != null) {
                    list2 = new ArrayList<>(Arrays.asList(customItemPropertiesArray));
                }

                list.add(list2);
            }
        }

        return list;
    }

    private static CustomItemProperties[][] propertyListToArray(List lists) {
        CustomItemProperties[][] customItemPropertiesArraies = new CustomItemProperties[lists.size()][];

        for (int i = 0; i < lists.size(); i++) {
            var list = (List)lists.get(i);
            if (list != null) {
                CustomItemProperties[] customItemPropertiesArray = (CustomItemProperties[])(list.toArray(new CustomItemProperties[list.size()]));
                Arrays.sort(customItemPropertiesArray, new CustomItemsComparator());
                customItemPropertiesArraies[i] = customItemPropertiesArray;
            }
        }

        return customItemPropertiesArraies;
    }

    private static void addToItemList(CustomItemProperties cp, List itemList) {
        if (cp.items != null) {
            for (int i = 0; i < cp.items.length; i++) {
                int n = cp.items[i];
                if (n <= 0) {
                    Config.warn("Invalid item ID: " + n);
                } else {
                    addToList(cp, itemList, n);
                }
            }
        }
    }

    private static void addToEnchantmentList(CustomItemProperties cp, List enchantmentList) {
        if (cp.type == 2 && cp.enchantmentIds != null) {
            for (int i = 0; i < 256; i++) {
                if (cp.enchantmentIds.isInRange(i)) {
                    addToList(cp, enchantmentList, i);
                }
            }
        }
    }

    private static void addToList(CustomItemProperties cp, List lists, int id) {
        while (id >= lists.size()) {
            lists.add(null);
        }

        var list = (List)lists.get(id);
        if (list == null) {
            list = new ArrayList();
            lists.set(id, list);
        }

        list.add(cp);
    }

    public static IBakedModel getCustomItemModel(ItemStack itemStack, IBakedModel model, ResourceLocation modelLocation, boolean fullModel) {
        if (simplifiedRender) {
            return model;
        }
        if (!fullModel && model.isGui3d()) {
            return model;
        }
        if (itemProperties == null) {
            return model;
        }
        CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 1);
        if (customItemProperties == null) {
            return model;
        }
        IBakedModel bakedModel = customItemProperties.getBakedModel(modelLocation, fullModel);
        return bakedModel != null ? bakedModel : model;
    }

    public static boolean bindCustomArmorTexture(ItemStack itemStack, int layer, String overlay) {
        if (itemProperties == null) {
            return false;
        }
        ResourceLocation customArmorLocation = getCustomArmorLocation(itemStack, layer, overlay);
        if (customArmorLocation == null) {
            return false;
        }
        Config.getTextureManager().bindTexture(customArmorLocation);
        return true;
    }

    private static ResourceLocation getCustomArmorLocation(ItemStack itemStack, int layer, String overlay) {
        CustomItemProperties customItemProperties = getCustomItemProperties(itemStack, 3);
        if (customItemProperties == null) {
            return null;
        }
        if (customItemProperties.mapTextureLocations == null) {
            return customItemProperties.textureLocation;
        }
        if (!(itemStack.getItem() instanceof ItemArmor item)) {
            return null;
        }
        String name = item.getArmorMaterial().getName();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("texture.");
        stringBuffer.append(name);
        stringBuffer.append("_layer_");
        stringBuffer.append(layer);
        if (overlay != null) {
            stringBuffer.append("_");
            stringBuffer.append(overlay);
        }

        String str = stringBuffer.toString();
        ResourceLocation location = (ResourceLocation)customItemProperties.mapTextureLocations.get(str);
        return location == null ? customItemProperties.textureLocation : location;
    }

    private static CustomItemProperties getCustomItemProperties(ItemStack itemStack, int type) {
        if (simplifiedRender) {
            return null;
        }
        if (itemStack != null && (type == 1 || type == 3) && FPSBooster.checkState()) {
            int n = type == 1 ? 0 : 1;
            CustomItems.FrameMatch frameMatch = frameMatches.get(itemStack);
            int n2 = 1 << n;
            if (frameMatch != null && (frameMatch.known & n2) != 0) {
                return n == 0 ? frameMatch.item : frameMatch.armor;
            }
            CustomItemProperties customItemProperties = findCustomItemProperties(itemStack, type);
            if (frameMatch == null) {
                if (frameMatches.size() >= 4096) {
                    beginRenderFrame();
                }

                if (usedFrameMatches == frameMatchPool.size()) {
                    frameMatchPool.add(new CustomItems.FrameMatch());
                }

                frameMatch = frameMatchPool.get(usedFrameMatches++);
                frameMatches.put(itemStack, frameMatch);
            }

            if (n == 0) {
                frameMatch.item = customItemProperties;
            } else {
                frameMatch.armor = customItemProperties;
            }

            frameMatch.known |= n2;
            return customItemProperties;
        }
        return findCustomItemProperties(itemStack, type);
    }

    private static CustomItemProperties findCustomItemProperties(ItemStack itemStack, int type) {
        if (itemProperties == null) {
            return null;
        }
        if (itemStack == null) {
            return null;
        }
        Item item = itemStack.getItem();
        int idFromItem = Item.getIdFromItem(item);
        if (idFromItem >= 0 && idFromItem < itemProperties.length) {
            CustomItemProperties[] customItemPropertiesArray = itemProperties[idFromItem];
            if (customItemPropertiesArray != null) {
                for (int i = 0; i < customItemPropertiesArray.length; i++) {
                    CustomItemProperties customItemProperties = customItemPropertiesArray[i];
                    if (customItemProperties.type == type && matchesProperties(customItemProperties, itemStack, (int[][])null)) {
                        return customItemProperties;
                    }
                }
            }
        }

        return null;
    }

    private static boolean matchesProperties(CustomItemProperties cip, ItemStack itemStack, int[][] enchantmentIdLevels) {
        Item item = itemStack.getItem();
        if (cip.damage != null) {
            int itemDamage = itemStack.getItemDamage();
            if (cip.damageMask != 0) {
                itemDamage &= cip.damageMask;
            }

            if (cip.damagePercent) {
                int maxDamage = item.getMaxDamage();
                itemDamage = (int)((double)(itemDamage * 100) / maxDamage);
            }

            if (!cip.damage.isInRange(itemDamage)) {
                return false;
            }
        }

        if (cip.stackSize != null && !cip.stackSize.isInRange(itemStack.stackSize)) {
            return false;
        }
        int[][] intsArray = enchantmentIdLevels;
        if (cip.enchantmentIds != null) {
            if (enchantmentIdLevels == null) {
                intsArray = getEnchantmentIdLevels(itemStack);
            }

            boolean flag = false;

            for (int i = 0; i < intsArray.length; i++) {
                int n = intsArray[i][0];
                if (cip.enchantmentIds.isInRange(n)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        if (cip.enchantmentLevels != null) {
            if (intsArray == null) {
                intsArray = getEnchantmentIdLevels(itemStack);
            }

            boolean flag2 = false;

            for (int j = 0; j < intsArray.length; j++) {
                int n2 = intsArray[j][1];
                if (cip.enchantmentLevels.isInRange(n2)) {
                    flag2 = true;
                    break;
                }
            }

            if (!flag2) {
                return false;
            }
        }

        if (cip.nbtTagValues != null) {
            NBTTagCompound tagCompound = itemStack.getTagCompound();

            for (int k = 0; k < cip.nbtTagValues.length; k++) {
                NbtTagValue nbtTagValue = cip.nbtTagValues[k];
                if (!nbtTagValue.matches(tagCompound)) {
                    return false;
                }
            }
        }

        if (cip.hand != 0) {
            if (cip.hand == 1 && renderOffHand) {
                return false;
            }

            if (cip.hand == 2 && !renderOffHand) {
                return false;
            }
        }

        return true;
    }

    private static int[][] getEnchantmentIdLevels(ItemStack itemStack) {
        Item item = itemStack.getItem();
        NBTTagList tagList = item == Items.enchanted_book ? Items.enchanted_book.getEnchantments(itemStack) : itemStack.getEnchantmentTagList();
        if (tagList != null && tagList.tagCount() > 0) {
            int[][] intsArray = new int[tagList.tagCount()][2];

            for (int i = 0; i < tagList.tagCount(); i++) {
                NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
                short s = compoundTagAt.getShort("id");
                short s2 = compoundTagAt.getShort("lvl");
                intsArray[i][0] = s;
                intsArray[i][1] = s2;
            }

            return intsArray;
        }
        return EMPTY_INT2_ARRAY;
    }

    public static boolean renderCustomEffect(RenderItem renderItem, ItemStack itemStack, IBakedModel model) {
        if (simplifiedRender) {
            return true;
        }
        if (enchantmentProperties == null) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] enchantmentIdLevels = getEnchantmentIdLevels(itemStack);
        if (enchantmentIdLevels.length <= 0) {
            return false;
        }
        HashSet set = null;
        boolean flag = false;
        TextureManager textureManager = Config.getTextureManager();

        for (int i = 0; i < enchantmentIdLevels.length; i++) {
            int n = enchantmentIdLevels[i][0];
            if (n >= 0 && n < enchantmentProperties.length) {
                CustomItemProperties[] customItemPropertiesArray = enchantmentProperties[n];
                if (customItemPropertiesArray != null) {
                    for (int j = 0; j < customItemPropertiesArray.length; j++) {
                        CustomItemProperties customItemProperties = customItemPropertiesArray[j];
                        if (set == null) {
                            set = new HashSet();
                        }

                        if (set.add(n) && matchesProperties(customItemProperties, itemStack, enchantmentIdLevels) && customItemProperties.textureLocation != null) {
                            textureManager.bindTexture(customItemProperties.textureLocation);
                            float textureWidth = customItemProperties.getTextureWidth(textureManager);
                            if (!flag) {
                                flag = true;
                                GlStateManager.depthMask(false);
                                GlStateManager.depthFunc(GL11.GL_EQUAL);
                                GlStateManager.disableLighting();
                                GlStateManager.matrixMode(GL11.GL_TEXTURE);
                            }

                            Blender.setupBlend(customItemProperties.blend, 1.0F);
                            GlStateManager.pushMatrix();
                            GlStateManager.scale(textureWidth / 2.0F, textureWidth / 2.0F, textureWidth / 2.0F);
                            float f = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                            GlStateManager.translate(f, 0.0F, 0.0F);
                            GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                            renderItem.renderModel(model, -1);
                            GlStateManager.popMatrix();
                        }
                    }
                }
            }
        }

        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.depthMask(true);
            textureManager.bindTexture(TextureMap.locationBlocksTexture);
        }

        return flag;
    }

    public static boolean renderCustomArmorEffect(
        EntityLivingBase entity, ItemStack itemStack, ModelBase model, float limbSwing, float prevLimbSwing, float partialTicks, float timeLimbSwing, float yaw, float pitch, float scale
    ) {
        if (simplifiedRender) {
            return true;
        }
        if (enchantmentProperties == null) {
            return false;
        }
        if (Config.isShaders() && Shaders.isShadowPass) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int[][] enchantmentIdLevels = getEnchantmentIdLevels(itemStack);
        if (enchantmentIdLevels.length <= 0) {
            return false;
        }
        HashSet set = null;
        boolean flag = false;
        TextureManager textureManager = Config.getTextureManager();

        for (int i = 0; i < enchantmentIdLevels.length; i++) {
            int n = enchantmentIdLevels[i][0];
            if (n >= 0 && n < enchantmentProperties.length) {
                CustomItemProperties[] customItemPropertiesArray = enchantmentProperties[n];
                if (customItemPropertiesArray != null) {
                    for (int j = 0; j < customItemPropertiesArray.length; j++) {
                        CustomItemProperties customItemProperties = customItemPropertiesArray[j];
                        if (set == null) {
                            set = new HashSet();
                        }

                        if (set.add(n) && matchesProperties(customItemProperties, itemStack, enchantmentIdLevels) && customItemProperties.textureLocation != null) {
                            textureManager.bindTexture(customItemProperties.textureLocation);
                            float textureWidth = customItemProperties.getTextureWidth(textureManager);
                            if (!flag) {
                                flag = true;
                                if (Config.isShaders()) {
                                    ShadersRender.renderEnchantedGlintBegin();
                                }

                                GlStateManager.enableBlend();
                                GlStateManager.depthFunc(GL11.GL_EQUAL);
                                GlStateManager.depthMask(false);
                            }

                            Blender.setupBlend(customItemProperties.blend, 1.0F);
                            GlStateManager.disableLighting();
                            GlStateManager.matrixMode(GL11.GL_TEXTURE);
                            GlStateManager.loadIdentity();
                            GlStateManager.rotate(customItemProperties.rotation, 0.0F, 0.0F, 1.0F);
                            float f = textureWidth / 8.0F;
                            GlStateManager.scale(f, f / 2.0F, f);
                            float f2 = customItemProperties.speed * (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
                            GlStateManager.translate(0.0F, f2, 0.0F);
                            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                            model.render(entity, limbSwing, prevLimbSwing, timeLimbSwing, yaw, pitch, scale);
                        }
                    }
                }
            }
        }

        if (flag) {
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.matrixMode(GL11.GL_TEXTURE);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(GL11.GL_MODELVIEW);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
                ShadersRender.renderEnchantedGlintEnd();
            }
        }

        return flag;
    }

    public static boolean isUseGlint() {
        return useGlint;
    }

    static final class FrameMatch {
        CustomItemProperties item;
        CustomItemProperties armor;
        int known;

        void clear() {
            this.item = this.armor = null;
            this.known = 0;
        }
    }
}
