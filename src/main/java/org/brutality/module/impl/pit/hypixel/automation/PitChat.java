package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;

public class PitChat extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final BooleanSetting hideBountyMessageSetting = new BooleanSetting("Hide Bounty Message", this, false);
    private final BooleanSetting hideStreakMessageSetting = new BooleanSetting("Hide Streak Message", this, false);
    private final BooleanSetting hideBountyClaimedSetting = new BooleanSetting("Hide Bounty Claimed", this, false);
    private final BooleanSetting hidePlayerEnchantesSetting = new BooleanSetting("Hide Player Enchantes", this, false);
    private final BooleanSetting hideKillMessagesSetting = new BooleanSetting("Hide Kill Messages", this, false);
    private final BooleanSetting hidePromotionMessagesSetting = new BooleanSetting("Hide Promotion Messages", this, false);
    private final BooleanSetting hideAssistMessagesSetting = new BooleanSetting("Hide Assist Messages", this, false);
    private final BooleanSetting debugSetting = new BooleanSetting("Debug", this, false);

    private void handleString(String str, String str2) {
        try {
            ModuleManager.handleString(MessageUtils.computeString(str, str2));
        } catch (Exception e) {
            System.out.println("[MysticsCounterHUD] Failed to send chat message: " + str + " | Error: " + e.getMessage());
        }

        System.out.println("[MysticsCounterHUD] " + str);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (!(event instanceof EventUpdate) || this.mc.thePlayer != null && this.mc.theWorld != null) {
                    if (event instanceof ChatEvent chatEvent) {
                        String text = chatEvent.message.getFormattedText();
                        String str = text.replaceAll("§[0-9a-fk-or]", "");
                        if (this.hideKillMessagesSetting.isEnabled()) {
                            boolean flag = str.contains("KILL!")
                                || str.contains("DOUBLE KILL!")
                                || str.contains("TRIPLE KILL!")
                                || str.contains("QUADRA KILL!")
                                || str.contains("PENTA KILL!")
                                || str.contains("MULTI KILL!");
                            if (flag) {
                                chatEvent.cancel();
                                if (this.debugSetting.isEnabled()) {
                                    this.handleString("Debug: Hidden kill message (modules already detected it)", MessageUtils.text15);
                                }

                                return;
                            }
                        }

                        if (this.hideAssistMessagesSetting.isEnabled() && str.contains("ASSIST!")) {
                            chatEvent.cancel();
                            if (this.debugSetting.isEnabled()) {
                                this.handleString("Debug: Hidden assist message (modules already detected it)", MessageUtils.text15);
                            }

                            return;
                        }

                        if (this.hideBountyMessageSetting.isEnabled() && text.contains("BOUNTY!") && text.contains("on") && text.contains("bump")) {
                            chatEvent.cancel();
                            if (this.debugSetting.isEnabled()) {
                                this.handleString("Debug: Cancelled Bounty message: " + text, MessageUtils.text15);
                            }

                            return;
                        }

                        if (this.hideStreakMessageSetting.isEnabled() && text.contains("STREAK!") && text.contains("of") && text.contains("by")) {
                            chatEvent.cancel();
                            if (this.debugSetting.isEnabled()) {
                                this.handleString("Debug: Cancelled Streak message: " + text, MessageUtils.text15);
                            }

                            return;
                        }

                        if (this.hideBountyClaimedSetting.isEnabled() && text.contains("BOUNTY CLAIMED!") && text.contains("killed") && text.contains("for")) {
                            chatEvent.cancel();
                            if (this.debugSetting.isEnabled()) {
                                this.handleString("Debug: Cancelled Bounty Claimed message: " + text, MessageUtils.text15);
                            }
                        }

                        if (this.hidePlayerEnchantesSetting.isEnabled() && text.contains("created") && text.contains("gg")) {
                            chatEvent.cancel();
                        }

                        if (this.hidePromotionMessagesSetting.isEnabled() && text.contains("PROMOTION") && text.contains("boosted")) {
                            chatEvent.cancel();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            this.handleString("Pit Chat enabled. Debug: " + this.debugSetting.isEnabled(), MessageUtils.text8);
        }
    }

    public PitChat() {
        super("Pit Chat", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.hideBountyMessageSetting,
                this.hideStreakMessageSetting,
                this.hideBountyClaimedSetting,
                this.hidePlayerEnchantesSetting,
                this.hideKillMessagesSetting,
                this.hideAssistMessagesSetting,
                this.hidePromotionMessagesSetting,
                this.debugSetting
            }
        );
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.handleString("Pit Chat disabled.", MessageUtils.text8);
    }
}
