package org.brutality.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.utils.render.RenderHelper;
import org.brutality.utils.render.GlStateGuard;

public final class EditableModulesHelper {
    private static final Set<String> itemSet = new LinkedHashSet<>(
        Arrays.asList(
            "Armor HUD",
            "BPS Graph",
            "Scoreboard",
            "Health",
            "HUD",
            "InventoryGUI",
            "PotionHUD",
            "Session",
            "TabGUI",
            "Watermark",
            "MellowHUD",
            "Assassin ESP",
            "Chicken Timer",
            "Egg Timer",
            "Aura Timer",
            "Explo Timer",
            "Events",
            "FeatherCountHUD",
            "Feast Timer",
            "Jump Potion HUD",
            "ResistanceHUD",
            "StrengthHUD",
            "Mirror Damage",
            "MindAssaultDamage",
            "MidCounter",
            "Low Life Warning",
            "PainDamage",
            "PheonixHUD",
            "Player List",
            "Pin Timer",
            "PodHUD",
            "Prestige List",
            "Mystic Drop Counter",
            "SoulripperDamage",
            "SpawnTimer",
            "TelebowTimer",
            "PerunCounter",
            "RGMDamage",
            "SharkDamage",
            "SolitudeReduction",
            "Toxic Info",
            "Venomed Timer",
            "Robbery",
            "Pizza",
            "Notifications",
            "Interface",
            "BlockCounterHUD",
            "Blockhead",
            "Squads"
        )
    );
    private static boolean active;
    private static final Set<String> itemSet2 = new LinkedHashSet<>();

    private EditableModulesHelper() {
    }

    public static boolean matchesModule3(Module module) {
        return module != null && itemSet.contains(module.getName());
    }

    public static void handleFloat(float f) {
        if (active && Brutality.moduleManager != null) {
            try (RenderHelper renderHelper = new RenderHelper()) {
                EventRender2D event = new EventRender2D(new ScaledResolution(Minecraft.getMinecraft()), f);

                for (Module editableModule : editableModules()) {
                    if (itemSet2.contains(editableModule.getName())) {
                        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                            editableModule.onEvent(event);
                        }
                    }
                }
            }
        }
    }

    public static boolean matchesModule2(Module module) {
        return !active || itemSet2.contains(module.getName());
    }

    public static void end() {
        active = false;
        itemSet2.clear();
        HudDragHelper.process();
    }

    public static boolean isActive() {
        return active;
    }

    public static void process() {
        active = true;
        itemSet2.clear();

        for (Module editableModule : editableModules()) {
            if (editableModule.checkState44()) {
                itemSet2.add(editableModule.getName());
            }
        }
    }

    public static boolean matchesString(String str) {
        return itemSet2.contains(str);
    }

    public static void handleString(String str) {
        if (!itemSet2.remove(str)) {
            itemSet2.add(str);
        }
    }

    public static List<Module> editableModules() {
        return Brutality.moduleManager == null
            ? Collections.emptyList()
            : ModuleManager.getEntries2().stream().filter(module -> module.checkState44() && matchesModule3(module)).collect(Collectors.toList());
    }

    public static boolean matchesModule(Module module) {
        Minecraft mc = Minecraft.getMinecraft();
        return !active
            && mc.currentScreen == null
            && mc.gameSettings != null
            && mc.gameSettings.keyBindPlayerList != null
            && mc.gameSettings.keyBindPlayerList.isKeyDown()
            && matchesModule3(module);
    }

    public static void process2() {
        if (active) {
            try (RenderHelper renderHelper = new RenderHelper()) {
                for (Module editableModule : editableModules()) {
                    if (itemSet2.contains(editableModule.getName())) {
                        try (GlStateGuard renderHelper7 = new GlStateGuard()) {
                            editableModule.onRender();
                        }
                    }
                }
            }
        }
    }
}
