package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.StopwatchTimer;
import org.brutality.utils.WHelper;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class AutoParty extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static final int count = 200;
    private static final long timestamp = 500L;
    private final ModeSetting guildSetting = new ModeSetting("Mode", this, "Guild", Arrays.asList("Guild"));
    private final BooleanSetting sendGPartySetting = new BooleanSetting("Send /g party", this, true);
    private final NumberSetting intervalTicksSetting = new NumberSetting("Interval (ticks)", this, 10.0, 1.0, 120.0, 1.0);
    private final BooleanSetting autoWarpSetting = new BooleanSetting("Auto warp", this, false);
    private final NumberSetting autoWarpIntervalTicksSetting = new NumberSetting("Auto Warp Interval (ticks)", this, 10.0, 1.0, 120.0, 1.0);
    private final BooleanSetting sendLocationSetting = new BooleanSetting("Send Location", this, true);
    private final BooleanSetting randomizeSetting = new BooleanSetting("Randomize", this, true);
    private final BooleanSetting showInSpawnSetting = new BooleanSetting("Show In Spawn", this, false);
    private final BooleanSetting showHPSetting = new BooleanSetting("Show HP", this, false);
    private final NumberSetting showHPBelowSetting = new NumberSetting("Show HP Below", this, 10.0, 1.0, 20.0, 0.5);
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private int count2 = 0;
    private int count3 = 0;
    private final Deque<String> deque = new ArrayDeque<>();

    public AutoParty() {
        super("AutoParty", 0, Category.PIT);
        this.handleSettingArray(
            new Setting[]{
                this.guildSetting,
                this.sendGPartySetting,
                this.intervalTicksSetting,
                this.autoWarpSetting,
                this.autoWarpIntervalTicksSetting,
                this.sendLocationSetting,
                this.randomizeSetting,
                this.showInSpawnSetting,
                this.showHPSetting,
                this.showHPBelowSetting
            }
        );
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.count2 = 0;
        this.count3 = 0;
        this.utilHelper8.reset();
        this.deque.clear();
        if (this.sendGPartySetting.isEnabled() && mc.thePlayer != null) {
            mc.thePlayer.sendChatMessage("/g party");
        }
    }

    private void of() {
        if (this.sendLocationSetting.isEnabled()) {
            ArrayList list = new ArrayList();

            for (EntityPlayer playerEntity : mc.theWorld.playerEntities) {
                if (playerEntity != null
                    && playerEntity != mc.thePlayer
                    && !playerEntity.isDead
                    && WHelper.matchesString(playerEntity.getName())
                    && (this.showInSpawnSetting.isEnabled() || !FreshHelper.matchesWorld3(mc.theWorld, playerEntity))) {
                    String str = FreshHelper.computeString(playerEntity, mc.theWorld).replaceAll("§[0-9a-fk-or]", "").trim();
                    String str2 = playerEntity.getName() + " - " + str;
                    if (this.showHPSetting.isEnabled() && playerEntity.getHealth() <= this.showHPBelowSetting.computeDouble()) {
                        str2 = str2 + " (" + Math.round(playerEntity.getHealth()) + "hp)";
                    }

                    list.add(str2);
                }
            }

            if (!list.isEmpty()) {
                if (this.randomizeSetting.isEnabled()) {
                    Collections.shuffle(list);
                }

                this.deque.addAll(computeList(list));
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (((EventTick)event).getState() == EventState.PRE) {
                    if (mc.theWorld != null && mc.thePlayer != null) {
                        if (this.autoWarpSetting.isEnabled()) {
                            if (++this.count3 >= (int)this.autoWarpIntervalTicksSetting.computeDouble()) {
                                this.count3 = 0;
                                mc.thePlayer.sendChatMessage("/p warp");
                            }
                        } else {
                            this.count3 = 0;
                        }

                        if (!this.deque.isEmpty() && this.utilHelper8.matchesLong(500L)) {
                            this.utilHelper8.reset();
                            mc.thePlayer.sendChatMessage(this.deque.poll());
                        }

                        this.count2++;
                        if (this.count2 >= (int)this.intervalTicksSetting.computeDouble()) {
                            this.count2 = 0;
                            if (this.guildSetting.matchesString2("Guild")) {
                                this.of();
                            }
                        }
                    }
                }
            }
        }
    }

    private static List<String> computeList(List<String> list) {
        ArrayList list2 = new ArrayList();
        String str = "/gc ";
        StringBuilder sb = new StringBuilder(str);

        for (String str2 : list) {
            String str3 = sb.length() > str.length() ? ", " + str2 : str2;
            if (sb.length() + str3.length() > 200) {
                list2.add(sb.toString());
                sb = new StringBuilder(str).append(str2);
            } else {
                sb.append(str3);
            }
        }

        if (sb.length() > str.length()) {
            list2.add(sb.toString());
        }

        return list2;
    }
}
