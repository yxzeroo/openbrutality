package org.brutality.gui.font.impl;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.font.api.GlyphFonts;
import org.brutality.gui.font.api.NameApi;
import org.brutality.utils.render.IsChoiceHelper;

public final class SFProComponent implements GlyphFonts {
    public static final String text = "SF-Pro";
    public static final String text2 = "stylesicons";
    public static final String text3 = "SF";
    public static final String text4 = "SFREGULAR";
    public static final String text5 = "SFBOLD";
    public static final String text6 = "oxide";
    public static final String text7 = "JetBrainsMono";
    public static final String text8 = "JetBrainsMono-Bold";
    private static final String[] stringArray = new String[]{"brutality/fonts/", "brutality/font/"};
    private static final String[] stringArray2 = new String[]{".ttf", ".otf"};
    private final Map<String, NameApi> lookupMap = new HashMap<>();

    private SFProComponent() {
    }

    private static String computeString(String str) {
        for (String str2 : stringArray2) {
            if (str.toLowerCase(Locale.ROOT).endsWith(str2)) {
                return str.substring(0, str.length() - str2.length());
            }
        }

        return str;
    }

    private NameApi computeNameApi() {
        NameApi nameApi = this.lookupMap.get("SF-Pro");
        if (nameApi == null) {
            nameApi = NameComponent.computeNameApi("SF-Pro", new Font("SansSerif", 0, 18));
            this.lookupMap.put("SF-Pro", nameApi);
        }

        return nameApi;
    }

    @Override
    public List<String> computeList() {
        return new ArrayList<>(IsChoiceHelper.entries);
    }

    private static Font computeFont2(String str) {
        for (String str2 : stringArray2) {
            Path path = computePath().resolve(str + str2);
            if (Files.isRegularFile(path)) {
                try {
                    Font font;
                    try (InputStream in = Files.newInputStream(path)) {
                        font = computeFont(in);
                    }

                    return font;
                } catch (RuntimeException | IOException e) {
                    return computeFont(str);
                }
            }
        }

        return computeFont(str);
    }

    public static GlyphFonts computeGlyphFonts() {
        return new SFProComponent();
    }

    @Override
    public NameApi computeNameApi(String str) {
        if (IsChoiceHelper.isChoice(str)) {
            str = IsChoiceHelper.computeString(str);
        }

        String str2 = computeString(str == null ? "SF-Pro" : str);
        NameApi nameApi = this.lookupMap.get(str2);
        if (nameApi == null) {
            Font font = computeFont2(str2);
            if (font == null) {
                return str2.equals("SF-Pro") ? this.computeNameApi() : this.computeNameApi("SF-Pro");
            }

            nameApi = NameComponent.computeNameApi(str2, font);
            this.lookupMap.put(str2, nameApi);
        }

        return nameApi;
    }

    private static Font computeFont(String str) {
        for (String str2 : stringArray) {
            for (String str3 : stringArray2) {
                try {
                    IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(str2 + str + str3));

                    try (InputStream inputStream = resource.getInputStream()) {
                        Font font = computeFont(inputStream);
                        if (font != null) {
                            return font;
                        }
                    }
                } catch (RuntimeException | IOException ignored) {
                }
            }
        }

        return null;
    }

    private static Path computePath() {
        Path path = Minecraft.getMinecraft().mcDataDir.toPath().resolve("Brutality").resolve("fonts");

        try {
            Files.createDirectories(path);
        } catch (IOException ignored) {
        }

        return path;
    }

    private static Font computeFont(InputStream in) {
        try {
            return Font.createFont(0, in);
        } catch (FontFormatException e) {
            return null;
        } catch (IOException e2) {
            throw new RuntimeException("Couldn't completely read font resource", e2);
        }
    }
}
