package org.brutality.utils.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class BrutalityHelper {
    private String name2;
    private Color color;
    private Color color2;
    private Color color3;
    private Color color4;
    private Color color5;
    private Color color6;
    private Color color7;
    private final String text;
    private static final List<BrutalityHelper> entries = new ArrayList<>();
    private boolean flag;

    public String getName() {
        return this.name2;
    }

    public static List<String> computeList() {
        ArrayList list = new ArrayList();

        for (BrutalityHelper brutalityHelper : entries) {
            list.add(brutalityHelper.getName());
        }

        return list;
    }

    public Color computeColor2(int n) {
        return new Color(this.color3.getRed(), this.color3.getGreen(), this.color3.getBlue(), n);
    }

    public Color getColor7() {
        return this.color7;
    }

    private BrutalityHelper(String str, Color color, Color color2, Color color3, Color color4, Color color5, Color color6, Color color7) {
        this(str, color, color2, color3, color4, color5, color6, color7, "Brutality");
    }

    public Color getColor2() {
        return this.color2;
    }

    public Color getColor3() {
        return this.color3;
    }

    public Color getColor6() {
        return this.color6;
    }

    private static BrutalityHelper computeBrutalityHelper(String str, Color color, Color color2) {
        Color color3 = computeColor(color, color2, 0.5F);
        BrutalityHelper brutalityHelper = new BrutalityHelper(
            str,
            color,
            color2,
            color3,
            computeColor(color, Color.WHITE, 0.88F),
            computeColor(color, Color.BLACK, 0.82F),
            Color.WHITE,
            computeColor(color3, Color.WHITE, 0.62F)
        );
        brutalityHelper.flag = true;
        return brutalityHelper;
    }

    public Color computeColor(int n) {
        return new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), n);
    }

    public boolean isFlag() {
        return this.flag;
    }

    public Color computeColor3(int n) {
        return new Color(this.color2.getRed(), this.color2.getGreen(), this.color2.getBlue(), n);
    }

    public Color getColor5() {
        return this.color5;
    }

    public static List<BrutalityHelper> computeList2() {
        return new ArrayList<>(entries);
    }

    public Color computeColor(float f) {
        f = Math.max(0.0F, Math.min(1.0F, f));
        int n = (int)(this.color.getRed() + (this.color2.getRed() - this.color.getRed()) * f);
        int n2 = (int)(this.color.getGreen() + (this.color2.getGreen() - this.color.getGreen()) * f);
        int n3 = (int)(this.color.getBlue() + (this.color2.getBlue() - this.color.getBlue()) * f);
        return new Color(n, n2, n3);
    }

    @Override
    public String toString() {
        return this.name2;
    }

    public String getText() {
        return this.text;
    }

    private BrutalityHelper(String name2, Color color, Color color2, Color color3, Color color4, Color color5, Color color6, Color color7, String str) {
        this.name2 = name2;
        this.color = color;
        this.color2 = color2;
        this.color3 = color3;
        this.color4 = color4;
        this.color5 = color5;
        this.color6 = color6;
        this.color7 = color7;
        this.text = str != null && !str.isEmpty() ? IsChoiceHelper.normalize(str) : "Brutality";
    }

    public static BrutalityHelper getInstance() {
        return entries.get(0);
    }

    public Color getColor4() {
        return this.color4;
    }

    public static BrutalityHelper computeBrutalityHelper(String str) {
        for (BrutalityHelper brutalityHelper : entries) {
            if (brutalityHelper.getName().equalsIgnoreCase(str)) {
                return brutalityHelper;
            }
        }

        return entries.get(0);
    }

    public Color getColor() {
        return this.color;
    }

    static {
        entries.add(
            new BrutalityHelper(
                "Ocean Pink",
                new Color(255, 105, 180),
                new Color(0, 191, 255),
                new Color(255, 20, 147),
                new Color(255, 240, 245),
                new Color(139, 69, 94),
                new Color(255, 255, 255),
                new Color(200, 200, 200)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Cyber Sunset",
                new Color(255, 20, 147),
                new Color(0, 255, 255),
                new Color(30, 144, 255),
                new Color(240, 248, 255),
                new Color(25, 25, 112),
                new Color(255, 255, 255),
                new Color(176, 196, 222)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Forest Fire",
                new Color(255, 69, 0),
                new Color(34, 139, 34),
                new Color(0, 128, 0),
                new Color(240, 255, 240),
                new Color(0, 100, 0),
                new Color(255, 255, 255),
                new Color(152, 251, 152)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Purple Ocean",
                new Color(138, 43, 226),
                new Color(0, 191, 255),
                new Color(255, 69, 0),
                new Color(255, 248, 220),
                new Color(139, 69, 19),
                new Color(255, 255, 255),
                new Color(255, 218, 185)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Neon Dreams",
                new Color(57, 255, 20),
                new Color(255, 0, 255),
                new Color(148, 0, 211),
                new Color(250, 240, 230),
                new Color(75, 0, 130),
                new Color(255, 255, 255),
                new Color(221, 160, 221)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Fire Ice",
                new Color(220, 20, 60),
                new Color(0, 255, 255),
                new Color(178, 34, 34),
                new Color(255, 240, 245),
                new Color(139, 0, 0),
                new Color(255, 255, 255),
                new Color(255, 182, 193)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Golden Mint",
                new Color(255, 215, 0),
                new Color(152, 251, 152),
                new Color(255, 140, 0),
                new Color(255, 255, 240),
                new Color(184, 134, 11),
                new Color(255, 255, 255),
                new Color(255, 228, 181)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Arctic Aurora",
                new Color(0, 255, 255),
                new Color(255, 105, 180),
                new Color(176, 196, 222),
                new Color(255, 255, 255),
                new Color(112, 128, 144),
                new Color(0, 0, 0),
                new Color(105, 105, 105)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Midnight Glow",
                new Color(57, 255, 20),
                new Color(255, 20, 147),
                new Color(105, 105, 105),
                new Color(47, 47, 47),
                new Color(0, 0, 0),
                new Color(255, 255, 255),
                new Color(192, 192, 192)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Electric Coral",
                new Color(0, 191, 255),
                new Color(255, 127, 80),
                new Color(0, 206, 209),
                new Color(240, 255, 255),
                new Color(0, 139, 139),
                new Color(255, 255, 255),
                new Color(175, 238, 238)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Toxic Candy",
                new Color(57, 255, 20),
                new Color(255, 20, 147),
                new Color(50, 205, 50),
                new Color(240, 255, 240),
                new Color(0, 100, 0),
                new Color(255, 255, 255),
                new Color(144, 238, 144)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Sapphire Flame",
                new Color(0, 191, 255),
                new Color(255, 69, 0),
                new Color(65, 105, 225),
                new Color(240, 248, 255),
                new Color(0, 0, 139),
                new Color(255, 255, 255),
                new Color(173, 216, 230)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Rose Gold",
                new Color(255, 20, 147),
                new Color(255, 215, 0),
                new Color(199, 21, 133),
                new Color(255, 240, 245),
                new Color(139, 69, 94),
                new Color(255, 255, 255),
                new Color(255, 182, 193)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Venom Strike",
                new Color(50, 205, 50),
                new Color(138, 43, 226),
                new Color(34, 139, 34),
                new Color(240, 255, 240),
                new Color(0, 100, 0),
                new Color(255, 255, 255),
                new Color(152, 251, 152)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Galaxy Storm",
                new Color(138, 43, 226),
                new Color(0, 255, 255),
                new Color(128, 0, 128),
                new Color(248, 248, 255),
                new Color(75, 0, 130),
                new Color(255, 255, 255),
                new Color(221, 160, 221)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Blood Moon",
                new Color(255, 0, 0),
                new Color(25, 25, 25),
                new Color(220, 20, 60),
                new Color(255, 245, 238),
                new Color(139, 0, 0),
                new Color(255, 255, 255),
                new Color(255, 160, 122)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Deep Magenta",
                new Color(0, 104, 139),
                new Color(255, 0, 255),
                new Color(25, 25, 112),
                new Color(240, 248, 255),
                new Color(0, 0, 139),
                new Color(255, 255, 255),
                new Color(176, 196, 222)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Toxic Ocean",
                new Color(57, 255, 20),
                new Color(0, 191, 255),
                new Color(0, 128, 0),
                new Color(240, 255, 240),
                new Color(0, 100, 0),
                new Color(255, 255, 255),
                new Color(152, 251, 152)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Pink Lightning",
                new Color(255, 0, 255),
                new Color(255, 255, 0),
                new Color(199, 21, 133),
                new Color(255, 240, 255),
                new Color(139, 0, 139),
                new Color(255, 255, 255),
                new Color(221, 160, 221)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Copper Fire",
                new Color(205, 127, 50),
                new Color(255, 69, 0),
                new Color(160, 82, 45),
                new Color(255, 248, 220),
                new Color(139, 69, 19),
                new Color(255, 255, 255),
                new Color(244, 164, 96)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Silver Lightning",
                new Color(192, 192, 192),
                new Color(0, 191, 255),
                new Color(169, 169, 169),
                new Color(248, 248, 255),
                new Color(105, 105, 105),
                new Color(255, 255, 255),
                new Color(220, 220, 220)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Ruby Mint",
                new Color(224, 17, 95),
                new Color(152, 251, 152),
                new Color(178, 34, 34),
                new Color(255, 240, 245),
                new Color(139, 0, 0),
                new Color(255, 255, 255),
                new Color(255, 182, 193)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Ice Fire",
                new Color(0, 191, 255),
                new Color(255, 69, 0),
                new Color(0, 0, 139),
                new Color(240, 248, 255),
                new Color(25, 25, 112),
                new Color(255, 255, 255),
                new Color(176, 196, 222)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Void Purple",
                new Color(153, 102, 204),
                new Color(25, 25, 25),
                new Color(128, 0, 128),
                new Color(248, 248, 255),
                new Color(75, 0, 130),
                new Color(255, 255, 255),
                new Color(221, 160, 221)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Aqua Pink",
                new Color(64, 224, 208),
                new Color(255, 105, 180),
                new Color(0, 206, 209),
                new Color(240, 255, 255),
                new Color(0, 139, 139),
                new Color(255, 255, 255),
                new Color(175, 238, 238)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Lava Flow",
                new Color(255, 127, 80),
                new Color(57, 255, 20),
                new Color(255, 99, 71),
                new Color(255, 248, 220),
                new Color(205, 92, 92),
                new Color(255, 255, 255),
                new Color(255, 218, 185)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Mystic Purple",
                new Color(230, 230, 250),
                new Color(0, 255, 255),
                new Color(186, 85, 211),
                new Color(248, 248, 255),
                new Color(147, 112, 219),
                new Color(255, 255, 255),
                new Color(238, 221, 238)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Electric Mint",
                new Color(0, 191, 255),
                new Color(152, 251, 152),
                new Color(0, 250, 154),
                new Color(240, 255, 240),
                new Color(46, 139, 87),
                new Color(255, 255, 255),
                new Color(193, 255, 193)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Solar Flare",
                new Color(255, 218, 185),
                new Color(255, 0, 0),
                new Color(255, 140, 0),
                new Color(255, 248, 220),
                new Color(205, 133, 63),
                new Color(255, 255, 255),
                new Color(255, 228, 196)
            )
        );
        entries.add(
            new BrutalityHelper(
                "Nebula Dream",
                new Color(102, 51, 153),
                new Color(255, 105, 180),
                new Color(75, 0, 130),
                new Color(248, 248, 255),
                new Color(72, 61, 139),
                new Color(255, 255, 255),
                new Color(186, 85, 211)
            )
        );
        entries.add(computeBrutalityHelper("Twilight", new Color(124, 92, 255), new Color(255, 110, 196)));
        entries.add(computeBrutalityHelper("Peach Fizz", new Color(255, 154, 139), new Color(255, 106, 136)));
        entries.add(computeBrutalityHelper("Lagoon", new Color(0, 201, 167), new Color(0, 128, 255)));
        entries.add(computeBrutalityHelper("Ember", new Color(255, 94, 58), new Color(255, 170, 50)));
        entries.add(computeBrutalityHelper("Mojito", new Color(86, 224, 120), new Color(0, 184, 169)));
        entries.add(computeBrutalityHelper("Lavender Haze", new Color(180, 150, 255), new Color(120, 170, 255)));
        entries.add(computeBrutalityHelper("Sakura Bloom", new Color(255, 175, 210), new Color(255, 110, 165)));
        entries.add(computeBrutalityHelper("Deep Sea", new Color(40, 110, 255), new Color(60, 220, 255)));
        entries.add(computeBrutalityHelper("Grape Soda", new Color(150, 70, 255), new Color(230, 80, 220)));
        entries.add(computeBrutalityHelper("Citrus", new Color(255, 214, 70), new Color(255, 140, 40)));
        entries.add(computeBrutalityHelper("Mint Frost", new Color(130, 255, 214), new Color(100, 190, 255)));
        entries.add(computeBrutalityHelper("Crimson Dusk", new Color(235, 45, 85), new Color(150, 45, 190)));
        entries.add(computeBrutalityHelper("Northern Lights", new Color(40, 230, 160), new Color(120, 100, 255)));
        entries.add(computeBrutalityHelper("Honey", new Color(255, 196, 80), new Color(230, 120, 50)));
        entries.add(computeBrutalityHelper("Steel", new Color(160, 180, 210), new Color(90, 120, 175)));
        entries.add(computeBrutalityHelper("Bubblegum", new Color(255, 125, 220), new Color(170, 125, 255)));
        entries.add(computeBrutalityHelper("Moss", new Color(150, 210, 95), new Color(60, 150, 95)));
        entries.add(computeBrutalityHelper("Sunrise", new Color(255, 115, 90), new Color(255, 205, 110)));
        entries.add(computeBrutalityHelper("Glacier", new Color(175, 232, 255), new Color(90, 160, 255)));
        entries.add(computeBrutalityHelper("Plum", new Color(200, 85, 170), new Color(120, 65, 180)));
        entries.add(computeBrutalityHelper("Tropic", new Color(255, 205, 65), new Color(55, 220, 170)));
        entries.add(computeBrutalityHelper("Blush", new Color(255, 150, 160), new Color(255, 196, 170)));
        entries.add(computeBrutalityHelper("Cobalt", new Color(45, 90, 255), new Color(115, 65, 240)));
        entries.add(computeBrutalityHelper("Jade", new Color(0, 195, 125), new Color(0, 150, 165)));
        entries.add(computeBrutalityHelper("Flamingo", new Color(255, 100, 135), new Color(255, 155, 100)));
        entries.add(computeBrutalityHelper("Electric Violet", new Color(145, 65, 255), new Color(60, 125, 255)));
        entries.add(computeBrutalityHelper("Cherry Cola", new Color(220, 40, 70), new Color(140, 30, 60)));
        entries.add(computeBrutalityHelper("Seafoam", new Color(125, 240, 205), new Color(60, 200, 235)));
        entries.add(computeBrutalityHelper("Amethyst", new Color(165, 115, 235), new Color(215, 120, 210)));
        entries.add(computeBrutalityHelper("Monochrome", new Color(235, 236, 242), new Color(140, 142, 155)));
        entries.add(computeBrutalityHelper("Obsidian", new Color(125, 100, 180), new Color(70, 58, 125)));
        entries.add(computeBrutalityHelper("Aurora Borealis", new Color(60, 255, 170), new Color(190, 95, 255)));
    }

    private static Color computeColor(Color color, Color color2, float f) {
        return new Color(
            (int)(color.getRed() + (color2.getRed() - color.getRed()) * f),
            (int)(color.getGreen() + (color2.getGreen() - color.getGreen()) * f),
            (int)(color.getBlue() + (color2.getBlue() - color.getBlue()) * f)
        );
    }
}
