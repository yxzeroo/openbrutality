package org.brutality.optifine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertItemIdsHandler {
    private static final Map<Integer, String> lookupMap = new HashMap<>();
    private static final Pattern pattern = Pattern.compile("^\\s*items\\s*=\\s*([0-9\\s,-]+)\\s*$", 10);
    private static final Pattern pattern2 = Pattern.compile("items\\s*=.*minecraft:", 2);

    public static void process3() {
        String str = "type=item\nitems=283 284 285 286\ntexture=golden_tools\nnbt.display.Name=*Golden*\nmodel=golden_tools\n";
        String str2 = computeString(str);
        String str3 = "type=item\nitems=minecraft:golden_sword minecraft:golden_shovel\ntexture=golden_tools\n";
        String str4 = computeString(str3);
    }

    public static void process2() {
    }

    static {
        lookupMap.put(256, "minecraft:iron_shovel");
        lookupMap.put(257, "minecraft:iron_pickaxe");
        lookupMap.put(258, "minecraft:iron_axe");
        lookupMap.put(259, "minecraft:flint_and_steel");
        lookupMap.put(260, "minecraft:apple");
        lookupMap.put(261, "minecraft:bow");
        lookupMap.put(262, "minecraft:arrow");
        lookupMap.put(263, "minecraft:coal");
        lookupMap.put(264, "minecraft:diamond");
        lookupMap.put(265, "minecraft:iron_ingot");
        lookupMap.put(266, "minecraft:gold_ingot");
        lookupMap.put(267, "minecraft:iron_sword");
        lookupMap.put(268, "minecraft:wooden_sword");
        lookupMap.put(269, "minecraft:wooden_shovel");
        lookupMap.put(270, "minecraft:wooden_pickaxe");
        lookupMap.put(271, "minecraft:wooden_axe");
        lookupMap.put(272, "minecraft:stone_sword");
        lookupMap.put(273, "minecraft:stone_shovel");
        lookupMap.put(274, "minecraft:stone_pickaxe");
        lookupMap.put(275, "minecraft:stone_axe");
        lookupMap.put(276, "minecraft:diamond_sword");
        lookupMap.put(277, "minecraft:diamond_shovel");
        lookupMap.put(278, "minecraft:diamond_pickaxe");
        lookupMap.put(279, "minecraft:diamond_axe");
        lookupMap.put(280, "minecraft:stick");
        lookupMap.put(281, "minecraft:bowl");
        lookupMap.put(282, "minecraft:mushroom_stew");
        lookupMap.put(283, "minecraft:golden_sword");
        lookupMap.put(284, "minecraft:golden_shovel");
        lookupMap.put(285, "minecraft:golden_pickaxe");
        lookupMap.put(286, "minecraft:golden_axe");
        lookupMap.put(287, "minecraft:string");
        lookupMap.put(288, "minecraft:feather");
        lookupMap.put(289, "minecraft:gunpowder");
        lookupMap.put(290, "minecraft:wooden_hoe");
        lookupMap.put(291, "minecraft:stone_hoe");
        lookupMap.put(292, "minecraft:iron_hoe");
        lookupMap.put(293, "minecraft:diamond_hoe");
        lookupMap.put(294, "minecraft:golden_hoe");
        lookupMap.put(295, "minecraft:wheat_seeds");
        lookupMap.put(296, "minecraft:wheat");
        lookupMap.put(297, "minecraft:bread");
        lookupMap.put(298, "minecraft:leather_helmet");
        lookupMap.put(299, "minecraft:leather_chestplate");
        lookupMap.put(300, "minecraft:leather_leggings");
        lookupMap.put(301, "minecraft:leather_boots");
        lookupMap.put(302, "minecraft:chainmail_helmet");
        lookupMap.put(303, "minecraft:chainmail_chestplate");
        lookupMap.put(304, "minecraft:chainmail_leggings");
        lookupMap.put(305, "minecraft:chainmail_boots");
        lookupMap.put(306, "minecraft:iron_helmet");
        lookupMap.put(307, "minecraft:iron_chestplate");
        lookupMap.put(308, "minecraft:iron_leggings");
        lookupMap.put(309, "minecraft:iron_boots");
        lookupMap.put(310, "minecraft:diamond_helmet");
        lookupMap.put(311, "minecraft:diamond_chestplate");
        lookupMap.put(312, "minecraft:diamond_leggings");
        lookupMap.put(313, "minecraft:diamond_boots");
        lookupMap.put(314, "minecraft:golden_helmet");
        lookupMap.put(315, "minecraft:golden_chestplate");
        lookupMap.put(316, "minecraft:golden_leggings");
        lookupMap.put(317, "minecraft:golden_boots");
        lookupMap.put(318, "minecraft:flint");
        lookupMap.put(319, "minecraft:porkchop");
        lookupMap.put(320, "minecraft:cooked_porkchop");
        lookupMap.put(321, "minecraft:painting");
        lookupMap.put(322, "minecraft:golden_apple");
        lookupMap.put(323, "minecraft:sign");
        lookupMap.put(324, "minecraft:wooden_door");
        lookupMap.put(325, "minecraft:bucket");
        lookupMap.put(326, "minecraft:water_bucket");
        lookupMap.put(327, "minecraft:lava_bucket");
        lookupMap.put(328, "minecraft:minecart");
        lookupMap.put(329, "minecraft:saddle");
        lookupMap.put(330, "minecraft:iron_door");
        lookupMap.put(331, "minecraft:redstone");
        lookupMap.put(332, "minecraft:snowball");
        lookupMap.put(333, "minecraft:boat");
        lookupMap.put(334, "minecraft:leather");
        lookupMap.put(335, "minecraft:milk_bucket");
        lookupMap.put(336, "minecraft:brick");
        lookupMap.put(337, "minecraft:clay_ball");
        lookupMap.put(338, "minecraft:reeds");
        lookupMap.put(339, "minecraft:paper");
        lookupMap.put(340, "minecraft:book");
        lookupMap.put(341, "minecraft:slime_ball");
        lookupMap.put(342, "minecraft:chest_minecart");
        lookupMap.put(343, "minecraft:furnace_minecart");
        lookupMap.put(344, "minecraft:egg");
        lookupMap.put(345, "minecraft:compass");
        lookupMap.put(346, "minecraft:fishing_rod");
        lookupMap.put(347, "minecraft:clock");
        lookupMap.put(348, "minecraft:glowstone_dust");
        lookupMap.put(349, "minecraft:fish");
        lookupMap.put(350, "minecraft:cooked_fish");
        lookupMap.put(351, "minecraft:dye");
        lookupMap.put(352, "minecraft:bone");
        lookupMap.put(353, "minecraft:sugar");
        lookupMap.put(354, "minecraft:cake");
        lookupMap.put(355, "minecraft:bed");
        lookupMap.put(356, "minecraft:repeater");
        lookupMap.put(357, "minecraft:cookie");
        lookupMap.put(358, "minecraft:filled_map");
        lookupMap.put(359, "minecraft:shears");
        lookupMap.put(360, "minecraft:melon");
        lookupMap.put(361, "minecraft:pumpkin_seeds");
        lookupMap.put(362, "minecraft:melon_seeds");
        lookupMap.put(363, "minecraft:beef");
        lookupMap.put(364, "minecraft:cooked_beef");
        lookupMap.put(365, "minecraft:chicken");
        lookupMap.put(366, "minecraft:cooked_chicken");
        lookupMap.put(367, "minecraft:rotten_flesh");
        lookupMap.put(368, "minecraft:ender_pearl");
        lookupMap.put(369, "minecraft:blaze_rod");
        lookupMap.put(370, "minecraft:ghast_tear");
        lookupMap.put(371, "minecraft:gold_nugget");
        lookupMap.put(372, "minecraft:nether_wart");
        lookupMap.put(373, "minecraft:potion");
        lookupMap.put(374, "minecraft:glass_bottle");
        lookupMap.put(375, "minecraft:spider_eye");
        lookupMap.put(376, "minecraft:fermented_spider_eye");
        lookupMap.put(377, "minecraft:blaze_powder");
        lookupMap.put(378, "minecraft:magma_cream");
        lookupMap.put(379, "minecraft:brewing_stand");
        lookupMap.put(380, "minecraft:cauldron");
        lookupMap.put(381, "minecraft:ender_eye");
        lookupMap.put(382, "minecraft:speckled_melon");
        lookupMap.put(383, "minecraft:spawn_egg");
        lookupMap.put(384, "minecraft:experience_bottle");
        lookupMap.put(385, "minecraft:fire_charge");
        lookupMap.put(386, "minecraft:writable_book");
        lookupMap.put(387, "minecraft:written_book");
        lookupMap.put(388, "minecraft:emerald");
        lookupMap.put(389, "minecraft:item_frame");
        lookupMap.put(390, "minecraft:flower_pot");
        lookupMap.put(391, "minecraft:carrot");
        lookupMap.put(392, "minecraft:potato");
        lookupMap.put(393, "minecraft:baked_potato");
        lookupMap.put(394, "minecraft:poisonous_potato");
        lookupMap.put(395, "minecraft:map");
        lookupMap.put(396, "minecraft:golden_carrot");
        lookupMap.put(397, "minecraft:skull");
        lookupMap.put(398, "minecraft:carrot_on_a_stick");
        lookupMap.put(399, "minecraft:nether_star");
        lookupMap.put(400, "minecraft:pumpkin_pie");
        lookupMap.put(401, "minecraft:fireworks");
        lookupMap.put(402, "minecraft:firework_charge");
        lookupMap.put(403, "minecraft:enchanted_book");
        lookupMap.put(404, "minecraft:comparator");
        lookupMap.put(405, "minecraft:netherbrick");
        lookupMap.put(406, "minecraft:quartz");
        lookupMap.put(407, "minecraft:tnt_minecart");
        lookupMap.put(408, "minecraft:hopper_minecart");
        lookupMap.put(409, "minecraft:prismarine_shard");
        lookupMap.put(410, "minecraft:prismarine_crystals");
        lookupMap.put(411, "minecraft:rabbit");
        lookupMap.put(412, "minecraft:cooked_rabbit");
        lookupMap.put(413, "minecraft:rabbit_stew");
        lookupMap.put(414, "minecraft:rabbit_foot");
        lookupMap.put(415, "minecraft:rabbit_hide");
        lookupMap.put(416, "minecraft:armor_stand");
        lookupMap.put(417, "minecraft:iron_horse_armor");
        lookupMap.put(418, "minecraft:golden_horse_armor");
        lookupMap.put(419, "minecraft:diamond_horse_armor");
        lookupMap.put(420, "minecraft:lead");
        lookupMap.put(421, "minecraft:name_tag");
        lookupMap.put(422, "minecraft:command_block_minecart");
        lookupMap.put(423, "minecraft:mutton");
        lookupMap.put(424, "minecraft:cooked_mutton");
        lookupMap.put(425, "minecraft:banner");
        lookupMap.put(2256, "minecraft:record_13");
        lookupMap.put(2257, "minecraft:record_cat");
        lookupMap.put(2258, "minecraft:record_blocks");
        lookupMap.put(2259, "minecraft:record_chirp");
        lookupMap.put(2260, "minecraft:record_far");
        lookupMap.put(2261, "minecraft:record_mall");
        lookupMap.put(2262, "minecraft:record_mellohi");
        lookupMap.put(2263, "minecraft:record_stal");
        lookupMap.put(2264, "minecraft:record_strad");
        lookupMap.put(2265, "minecraft:record_ward");
        lookupMap.put(2266, "minecraft:record_11");
        lookupMap.put(2267, "minecraft:record_wait");
    }

    private static String convertItemIds(String str) {
        if (str != null && !str.trim().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            String[] parts = str.split("[,\\s\\n\\r]+");
            boolean flag = true;

            for (String str2 : parts) {
                String str3 = str2.trim();
                if (!str3.isEmpty()) {
                    try {
                        if (str3.contains("-")) {
                            String[] parts2 = str3.split("-");
                            if (parts2.length == 2) {
                                int n = Integer.parseInt(parts2[0].trim());
                                int n2 = Integer.parseInt(parts2[1].trim());

                                for (int i = n; i <= n2; i++) {
                                    String str4 = lookupMap.get(i);
                                    if (str4 != null) {
                                        if (!flag) {
                                            sb.append(" ");
                                        }

                                        sb.append(str4);
                                        flag = false;
                                    }
                                }
                            }
                        } else {
                            int n3 = Integer.parseInt(str3);
                            String str5 = lookupMap.get(n3);
                            if (str5 != null) {
                                if (!flag) {
                                    sb.append(" ");
                                }

                                sb.append(str5);
                                flag = false;
                            }
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            return sb.toString();
        }
        return "";
    }

    public static void handleFile(File file) {
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File file2, String str) {
                    return str.toLowerCase().endsWith(".properties");
                }
            });
            if (files != null) {
                for (File file3 : files) {
                    try {
                        StringBuilder sb = new StringBuilder();
                        BufferedReader reader = null;

                        try {
                            reader = new BufferedReader(new FileReader(file3));

                            String str2;
                            while ((str2 = reader.readLine()) != null) {
                                sb.append(str2).append("\n");
                            }
                        } finally {
                            if (reader != null) {
                                reader.close();
                            }
                        }

                        String str3 = sb.toString();
                        if (!str3.trim().isEmpty() && str3.toLowerCase().contains("items=")) {
                            String str4 = computeString(str3);
                            if (!str3.equals(str4)) {
                                File file4 = new File(file3.getAbsolutePath() + ".backup");
                                if (!file4.exists()) {
                                    FileWriter fileWriter = null;

                                    try {
                                        fileWriter = new FileWriter(file4);
                                        fileWriter.write(str3);
                                    } finally {
                                        if (fileWriter != null) {
                                            fileWriter.close();
                                        }
                                    }
                                }

                                FileWriter fileWriter2 = null;

                                try {
                                    fileWriter2 = new FileWriter(file3);
                                    fileWriter2.write(str4);
                                } finally {
                                    if (fileWriter2 != null) {
                                        fileWriter2.close();
                                    }
                                }
                            }
                        }
                    } catch (IOException ignored) {
                    }
                }
            }
        }
    }

    public static String computeString(String str) {
        if (pattern2.matcher(str).find()) {
            return str;
        }
        Matcher matcher = pattern.matcher(str);
        StringBuffer stringBuffer = new StringBuffer();
        boolean flag = false;

        while (matcher.find()) {
            flag = true;
            String str2 = matcher.group().trim();
            String str3 = matcher.group(1);
            String str4 = convertItemIds(str3);
            if (!str4.isEmpty()) {
                matcher.appendReplacement(stringBuffer, "items=" + str4);
            } else {
                matcher.appendReplacement(stringBuffer, str2);
            }
        }

        matcher.appendTail(stringBuffer);
        if (!flag) {
        }

        return stringBuffer.toString();
    }

    public static void process() {
        try {
            Class clazz = Class.forName("net.optifine.Config");
        } catch (ClassNotFoundException ignored) {
        } catch (Exception ignored2) {
        }
    }
}
