package org.brutality.module.impl.pit.hypixel.automation;

import java.awt.Color;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.brutality.utils.timer.TimerUtil;

public class MysticESP extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final BooleanSetting alertSetting = new BooleanSetting("Alert", this, true);
    private final BooleanSetting soundSetting = new BooleanSetting("Sound", this, true);
    private final NumberSetting showTimeSSetting = new NumberSetting("Show Time (s)", this, 10.0, 1.0, 60.0, 1.0);
    private final TimerUtil timerUtil = new TimerUtil();
    private EntityItem entityItem = null;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (mc.thePlayer != null && mc.theWorld != null) {
                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    if (text.contains("MYSTIC ITEM!") && text.contains("dropped")) {
                        this.entityItem = this.computeEntityItem();
                        if (this.entityItem != null) {
                            this.timerUtil.reset();
                            if (this.alertSetting.isEnabled()) {
                                NotificationFeedHandler.handleString("§6[MYSTIC ITEM] §fa mystic dropped nearby!");
                            }

                            if (this.soundSetting.isEnabled()) {
                                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.orb"), 1.0F));
                            }
                        }
                    }
                }

                if (event instanceof EventRender3D && this.entityItem != null) {
                    if (this.timerUtil.matchesLong4((long)(this.showTimeSSetting.computeDouble() * 1000.0)) || this.entityItem.isDead) {
                        this.entityItem = null;
                        return;
                    }

                    ResetColorHelper.handleBlockPos2(this.entityItem.getPosition(), new Color(255, 200, 0, 200), true, false, 2.0F);
                }
            }
        }
    }

    public MysticESP() {
        super("MysticESP", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.alertSetting, this.soundSetting, this.showTimeSSetting});
    }

    private EntityItem computeEntityItem() {
        EntityItem entity = null;
        double MAX_VALUE = Double.MAX_VALUE;

        for (Object obj : mc.theWorld.loadedEntityList) {
            if (obj instanceof EntityItem entity2 && entity2.getEntityItem() != null && GetMysticEnchantLoreHelper.matchesItemStack5(entity2.getEntityItem())) {
                double dist = mc.thePlayer.getDistanceSqToEntity(entity2);
                if (dist < MAX_VALUE) {
                    MAX_VALUE = dist;
                    entity = entity2;
                }
            }
        }

        return entity;
    }
}
