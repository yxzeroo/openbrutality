package org.brutality.module.impl.pit.hypixel.utility;

import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.automation.grinding.lifecycle.LifecycleHandler;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;
import org.brutality.utils.player.FindIdentityHelper;

public class PitUtils extends Module {
    private final BooleanSetting rightClickViewInSpawnSetting = new BooleanSetting("Right Click View in Spawn", this, false);
    private final BooleanSetting showGUIStatsSetting = new BooleanSetting("Show GUI Stats", this, false);
    private final ModeSetting onGUISetting = new ModeSetting("Stats Placement", this, "Automatic", List.of("Automatic", "Left", "Right", "On GUI"))
        .computeModeSetting(this.showGUIStatsSetting::isEnabled);
    private final ModeSetting bothSetting = new ModeSetting("Stats Value", this, "Both", List.of("Vile", "Dollars", "Both"))
        .computeModeSetting(this.showGUIStatsSetting::isEnabled);
    private final ModeSetting bothSetting2 = new ModeSetting("Stats Screens", this, "Both", List.of("Inventory", "Ender Chest", "Both"))
        .computeModeSetting(this.showGUIStatsSetting::isEnabled);
    private final BooleanSetting divineDetectorSetting = new BooleanSetting("Divine Detector", this, false);
    private final ModeSetting bothSetting3 = new ModeSetting("Divine Display", this, "Both", List.of("Chat", "Title", "Both"))
        .computeModeSetting(this.divineDetectorSetting::isEnabled);
    private final ModeSetting noneSetting = new ModeSetting("Divine Sound", this, "Level Up", List.of("Level Up", "Orb", "Anvil", "None"))
        .computeModeSetting(this.divineDetectorSetting::isEnabled);
    private Map<String, Integer> lookupMap = Map.of();
    private long timestamp;
    private int count;
    private static final long timestamp2 = 10000L;

    private void onBasicEvent3(BasicEvent3 basicEvent3) {
        if (!basicEvent3.cancel2()
            && basicEvent3.count6 == 1
            && basicEvent3.flag
            && mc.currentScreen == null
            && mc.thePlayer != null
            && mc.theWorld != null
            && mc.objectMouseOver != null
            && mc.objectMouseOver.entityHit instanceof EntityPlayer player
            && player != mc.thePlayer
            && FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)
            && FreshHelper.matchesWorld3(mc.theWorld, player)) {
            String name = player.getGameProfile().getName();
            if (name != null && name.length() <= 16 && name.matches("[A-Za-z0-9_]+")) {
                basicEvent3.cancel();
                mc.thePlayer.sendChatMessage("/view " + name);
            }
        }
    }

    public boolean matchesFlag(boolean flag) {
        return this.onGUISetting.matchesString2("Right") || this.onGUISetting.matchesString2("Automatic") && flag;
    }

    public boolean checkState9() {
        return this.showGUIStatsSetting.isEnabled();
    }

    private void process() {
        this.lookupMap = Map.of();
        this.timestamp = 0L;
        this.count = 0;
    }

    public boolean checkState6() {
        return !this.bothSetting.matchesString2("Dollars");
    }

    private void process3() {
        String str = "§b§lDIVINE!";
        if (!this.bothSetting3.matchesString2("Title")) {
            MessageUtils.x(str);
        }

        if (!this.bothSetting3.matchesString2("Chat")) {
            mc.ingameGUI.displayTitle(null, null, 5, 35, 10);
            mc.ingameGUI.displayTitle(str, null, -1, -1, -1);
        }

        String text = this.noneSetting.getText();

        String str2 = switch (text) {
            case "Orb" -> "random.orb";
            case "Anvil" -> "random.anvil_land";
            case "Level Up" -> "random.levelup";
            default -> null;
        };
        if (str2 != null) {
            mc.thePlayer.playSound(str2, 1.0F, 1.0F);
        }
    }

    public boolean checkState8() {
        return !this.bothSetting2.matchesString2("Inventory");
    }

    @Override
    public void onDisable() {
        this.process();
        super.onDisable();
    }

    public boolean checkState2() {
        return this.bothSetting2.matchesString2("Both");
    }

    public boolean checkState4() {
        return !this.bothSetting.matchesString2("Vile");
    }

    public boolean checkState5() {
        return !this.bothSetting2.matchesString2("Ender Chest");
    }

    public PitUtils() {
        super("PitUtils", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.showGUIStatsSetting,
                this.onGUISetting,
                this.bothSetting,
                this.bothSetting2,
                this.rightClickViewInSpawnSetting,
                this.divineDetectorSetting,
                this.bothSetting3,
                this.noneSetting
            }
        );
    }

    public boolean checkState() {
        return this.onGUISetting.matchesString2("On GUI");
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (this.rightClickViewInSpawnSetting.isEnabled() && event instanceof BasicEvent3 basicEvent3) {
                    this.onBasicEvent3(basicEvent3);
                } else if (this.divineDetectorSetting.isEnabled() && !(event instanceof RenderEvent4) && mc.thePlayer != null && mc.theWorld != null) {
                    if (event instanceof ChatEvent chatEvent && chatEvent.byteVal != 2 && chatEvent.message != null) {
                        if (!this.lookupMap.isEmpty() && LifecycleHandler.matchesString(chatEvent.message.getUnformattedText())) {
                            this.timestamp = System.currentTimeMillis() + timestamp2;
                            this.count = 0;
                        }
                    } else if (event instanceof EventTick event2 && event2.getState() == EventState.PRE && mc.thePlayer.ticksExisted % 4 == 0) {
                        if (this.timestamp != 0L) {
                            if (System.currentTimeMillis() > this.timestamp) {
                                this.process();
                            } else if (mc.thePlayer.isEntityAlive() && FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                                int n = DivineDetector.computeInt(this.lookupMap, DivineDetector.computeMap2(mc.thePlayer));
                                if (n < 0) {
                                    this.process();
                                } else if (n == 0) {
                                    this.count = 0;
                                } else if (++this.count >= 4) {
                                    this.process3();
                                    this.process();
                                }
                            }
                        } else if (mc.thePlayer.isEntityAlive() && !FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
                            this.lookupMap = FindIdentityHelper.matchesItemStack(mc.thePlayer.getCurrentArmor(1), "divine miracle")
                                ? DivineDetector.computeMap(mc.thePlayer)
                                : Map.of();
                        }
                    }
                } else {
                    this.process();
                }
            }
        }
    }
}
