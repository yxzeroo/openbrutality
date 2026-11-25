package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import lombok.Generated;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.render.GetColorHelper;
import org.brutality.utils.tuples.Pair;

public class CustomModel extends Module {
    public static final ResourceLocation resourceLocation = new ResourceLocation("brutality/models/amogus.png");
    public static final ResourceLocation resourceLocation2 = new ResourceLocation("brutality/models/rabbit.png");
    public static boolean enabled = false;
    public final ModeSetting didwedieSetting = new ModeSetting("Model", this, "DID WE DIE", Arrays.asList("DID WE DIE"));
    private final ModeSetting simpleSetting = new ModeSetting("Duddy", this, "Random", Arrays.asList("simple"));
    private final ColorSetting colorSetting = new ColorSetting("color", this, 0.0F);
    public static CustomModel instance;
    private static final Map<Object, Color> lookupMap = new WeakHashMap<>();

    @Generated
    public static CustomModel getInstance() {
        return instance;
    }

    @Override
    public void process2() {
        lookupMap.clear();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lookupMap.clear();
        enabled = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        enabled = false;
        lookupMap.clear();
    }

    public CustomModel() {
        super("Custom Model", 0, Category.VISUAL);
        this.handleSettingArray(new Setting[]{this.didwedieSetting, this.simpleSetting, this.colorSetting});
        instance = this;
    }

    public double getYOffset() {
        String text = this.didwedieSetting.getText();
        byte b = -1;
        switch (text.hashCode()) {
            case -1445266750:
                if (text.equals("Among Us")) {
                    b = 0;
                }
            default:
                switch (b) {
                    case 0:
                        return 0.25;
                    default:
                        return 0.0;
                }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                lookupMap.clear();
            }
        }
    }

    public Color computeColor(Entity entity) {
        Color WHITE = Color.WHITE;
        String text = this.simpleSetting.getText();
        switch (text) {
            case "Sync":
                Pair pair = GetColorHelper.computePair2();
                WHITE = GetColorHelper.computeColor(15, 1, (Color)pair.getFirst(), (Color)pair.getSecond(), false);
                break;
            case "Random":
                if (lookupMap.containsKey(entity)) {
                    WHITE = lookupMap.get(entity);
                } else {
                    WHITE = GetColorHelper.computeColor();
                    lookupMap.put(entity, WHITE);
                }
                break;
            case "Custom":
                WHITE = this.colorSetting.computeColor();
        }

        return WHITE;
    }
}
