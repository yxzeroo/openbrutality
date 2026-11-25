package org.brutality.module.impl.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.clickgui.newclick.NewClickGUI;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.utils.MessageUtils;
import org.lwjgl.opengl.Display;

public class ClientSettings extends Module {
    private static ClientSettings instance;
    private final BooleanSetting customWindowTitleSetting = new BooleanSetting("Custom Window Title", this, false);
    private final BooleanSetting toggleNotificationsSetting = new BooleanSetting("Toggle Notifications", this, true);
    private final BooleanSetting startupMessageSetting = new BooleanSetting("Startup Message", this, true);
    private final BooleanSetting blockMovementWhileSearchingSetting = new BooleanSetting("Block Movement While Searching", this, true);
    private final BooleanSetting movementFixSetting = new BooleanSetting("MovementFix", this, true);
    private final ModeSetting offSetting = new ModeSetting("Auto Load Config", this, "OFF", () -> getConfigOptions());
    private boolean flag;
    private boolean flag2;

    public static boolean checkState4() {
        return true;
    }

    public static boolean checkState8() {
        return MovementFix.instance != null ? MovementFix.instance.checkState44() : instance != null && instance.movementFixSetting.isEnabled();
    }

    public static boolean checkState5() {
        return instance == null || instance.toggleNotificationsSetting.isEnabled();
    }

    private static List<String> getConfigOptions() {
        ArrayList list = new ArrayList();
        list.add("OFF");

        try {
            File file = null;
            if (Brutality.instance != null && Brutality.appdataHandler != null) {
                file = Brutality.appdataHandler.computeFile();
            }

            if (file == null) {
                String str = System.getenv("APPDATA");
                file = str != null ? new File(str, ".minecraft/Brutality/Configs") : new File("Brutality/Configs");
            }

            if (file.exists() && file.isDirectory()) {
                File[] files = file.listFiles((file2, str2) -> str2.endsWith(".json"));
                if (files != null) {
                    for (File file3 : files) {
                        list.add(file3.getName().replace(".json", ""));
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return list;
    }

    @Deprecated
    public static boolean checkState6() {
        return DiscordRPC.checkState2();
    }

    public static boolean checkState2() {
        return instance != null && instance.customWindowTitleSetting.isEnabled();
    }

    public ClientSettings() {
        super("Settings", 0, Category.CLIENT);
        this.setRequiresInGame(false);
        this.handleSettingArray(
            new Setting[]{
                this.customWindowTitleSetting,
                this.toggleNotificationsSetting,
                this.startupMessageSetting,
                this.blockMovementWhileSearchingSetting,
                this.offSetting
            }
        );
        instance = this;
        this.process29();
        this.handleFlag3(true);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        process();
        this.process3();
        if (!this.flag2) {
            this.flag2 = true;
            this.offSetting.process5();
            String text = this.offSetting.getText();
            if (!text.equals("OFF") && Brutality.appdataHandler != null) {
                Brutality.appdataHandler.matchesString(text);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof MovementEvent5) {
            if (NewClickGUI.checkState2()) {
                MovementEvent5 movementEvent5 = (MovementEvent5)event;
                movementEvent5.setFactor(0.0F);
                movementEvent5.setFactor2(0.0F);
                movementEvent5.setJump(false);
            }
        } else if (event instanceof EventUpdate) {
            process();
        }
    }

    public static void process() {
        try {
            if (checkState2()) {
                String str = mc != null && mc.getSession() != null && mc.getSession().getUsername() != null ? mc.getSession().getUsername() : "Player";
                // VENDOR-DISABLED: Brutality.getAuthHandler().getUsername()

                Display.setTitle(Brutality.name + " " + Brutality.version + " | " + str);
            } else {
                Display.setTitle("Minecraft 1.8.9");
            }
        } catch (Exception ignored) {
        }
    }

    public static boolean checkState() {
        return instance == null || instance.blockMovementWhileSearchingSetting.isEnabled();
    }

    private void process3() {
        if (this.startupMessageSetting.isEnabled() && !this.flag) {
            this.flag = true;
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                MessageUtils.x("§7Running §c" + Brutality.name + " §7v§c" + Brutality.version);
            }
        }
    }

    @Override
    public void onDisable() {
        this.handleFlag3(true);
    }

    @Generated
    public static ClientSettings getInstance() {
        return instance;
    }

    @Override
    public boolean checkState3() {
        return true;
    }
}
