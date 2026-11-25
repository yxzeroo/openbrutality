package org.brutality.module.impl.pit.hypixel.automation;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class AutoReport extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final BooleanSetting spawnOnlySetting = new BooleanSetting("Spawn Only", this, true);
    private final NumberSetting reportCooldownSetting = new NumberSetting("Report Cooldown", this, 6000.0, 1000.0, 30000.0, 500.0);
    private final Set<String> itemSet = new HashSet<>();
    private String text = "";
    private long timestamp = 0L;
    private long timestamp2 = 0L;
    private long timestamp3 = 0L;

    private void process() {
        long now = System.currentTimeMillis();
        if (!this.spawnOnlySetting.isEnabled() || FreshHelper.matchesWorld3(mc.theWorld, mc.thePlayer)) {
            String str = this.computeString();
            if (str != null && str.contains("Report")) {
                if (now - this.timestamp3 >= 750L && !this.text.isEmpty()) {
                    if (str.contains("Formatted")) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 21, 0, 0, mc.thePlayer);
                    } else if (str.contains("Cheating")) {
                        mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, 11, 0, 0, mc.thePlayer);
                        this.itemSet.add(this.text);
                        this.text = "";
                    }

                    this.timestamp3 = now;
                }
            } else if (now - this.timestamp >= 5000L) {
                this.timestamp = now;
                if (now - this.timestamp2 >= (long)this.reportCooldownSetting.computeDouble()) {
                    for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                        if (playerEntity != mc.thePlayer && !this.itemSet.contains(playerEntity.getName()) && WHelper.matchesString(playerEntity.getName())) {
                            String name = playerEntity.getName();
                            mc.thePlayer.sendChatMessage("/report " + name);
                            this.text = name;
                            this.timestamp2 = now;
                            this.timestamp3 = now;
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.itemSet.clear();
        this.text = "";
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof EventTick) {
                    this.process();
                }
            }
        }
    }

    private String computeString() {
        if (mc.currentScreen == null) {
            return null;
        }
        try {
            return mc.currentScreen.getClass().getName();
        } catch (Exception e) {
            return null;
        }
    }

    public AutoReport() {
        super("AutoReport", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.spawnOnlySetting, this.reportCooldownSetting});
    }
}
