package org.brutality.module.impl.pit.hypixel.automation;

import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.utils.MessageUtils;

public class AutoPit extends Module {
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private static boolean flag = false;
    private int count = -1;

    public AutoPit() {
        super("AutoPit", 0, Category.PIT);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (mc.thePlayer != null && mc.theWorld != null) {
                    if (!flag) {
                        String str = this.computeString();
                        boolean flag2 = str != null && str.toUpperCase().contains("HYPIXEL");
                        if (!flag2) {
                            this.count = -1;
                        } else if (this.count < 0) {
                            this.count = 1;
                        } else {
                            this.count--;
                            if (this.count <= 0) {
                                mc.thePlayer.sendChatMessage("/play pit");
                                flag = true;
                                this.count = -1;
                                MessageUtils.x(MessageUtils.computeString("[AutoPit]", MessageUtils.text7) + " Joining Pit...");
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.process();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.process();
    }

    private void process() {
        this.count = -1;
    }

    private String computeString() {
        Scoreboard scoreboard = mc.theWorld.getScoreboard();
        if (scoreboard == null) {
            return null;
        }
        ScoreObjective objectiveInDisplaySlot = scoreboard.getObjectiveInDisplaySlot(1);
        if (objectiveInDisplaySlot == null) {
            return null;
        }
        String displayName = objectiveInDisplaySlot.getDisplayName();
        return displayName == null ? null : EnumChatFormatting.getTextWithoutFormattingCodes(displayName);
    }
}
