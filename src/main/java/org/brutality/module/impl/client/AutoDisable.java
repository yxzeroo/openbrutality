package org.brutality.module.impl.client;

import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.combat.DamageBoost;
import org.brutality.module.impl.combat.Displace;
import org.brutality.module.impl.combat.Velocity;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;

public final class AutoDisable extends Module {
    private static AutoDisable instance;
    private final BooleanSetting damageBoostVelocitySetting = new BooleanSetting("DamageBoost + Velocity", this, true);
    private final BooleanSetting keepSprintDisplaceSetting = new BooleanSetting("KeepSprint + Displace", this, true);
    private final BooleanSetting keepSprintVelocitySetting = new BooleanSetting("KeepSprint + Velocity", this, true);

    public AutoDisable() {
        super("Auto Disable", 0, Category.CLIENT);
        this.setRequiresInGame(false);
        this.handleSettingArray(new Setting[]{this.damageBoostVelocitySetting, this.keepSprintDisplaceSetting, this.keepSprintVelocitySetting});
        instance = this;
    }

    public static void handleModule(Module module) {
        AutoDisable autoDisable = instance;
        if (autoDisable != null && autoDisable.checkState44() && module != autoDisable) {
            for (Module module2 : ModuleManager.entries3.toArray(new Module[0])) {
                if (module2 != module && module2.checkState44() && !module2.checkState3() && !module2.checkState7() && autoDisable.matchesModule2(module, module2)) {
                    module2.process39();
                }
            }
        }
    }

    @Override
    public void onEvent(Event event) {
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Module[] modules = ModuleManager.entries3.toArray(new Module[0]);

        for (int i = modules.length - 1; i >= 0; i--) {
            if (modules[i].checkState44()) {
                handleModule(modules[i]);
            }
        }
    }

    private boolean matchesModule2(Module module, Module module2) {
        return this.damageBoostVelocitySetting.isEnabled() && matchesModule(module, module2, DamageBoost.class, Velocity.class)
            || this.keepSprintDisplaceSetting.isEnabled() && matchesModule(module, module2, KeepSprint.class, Displace.class)
            || this.keepSprintVelocitySetting.isEnabled() && matchesModule(module, module2, KeepSprint.class, Velocity.class);
    }

    public static boolean matchesModule(Module module, Module module2) {
        return instance != null && instance.checkState44() && instance.matchesModule2(module, module2);
    }

    private static boolean matchesModule(Module module, Module module2, Class<?> clazz, Class<?> clazz2) {
        return clazz.isInstance(module) && clazz2.isInstance(module2) || clazz2.isInstance(module) && clazz.isInstance(module2);
    }
}
