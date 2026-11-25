package org.brutality.module.impl.pit.hypixel.utility;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.hypixel.pit.FreshHelper;

public class LobbySwapper extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static final long timestamp = 7500L;
    private static final long timestamp2 = 7500L;
    public static boolean flag;
    private volatile boolean flag2 = false;
    private static final Random random = new Random();
    private static final String[] stringArray = new String[]{
        "/play sb",
        "/play teams_insane",
        "/play tnt_pvprun",
        "/play bedwars_four_four",
        "/play arcade_hole_in_the_wall",
        "/play arcade_simon_says",
        "/play uhc_solo",
        "/play duels_potion_duel",
        "/play super_smash_2v2_normal",
        "/play murder_classic",
        "/play wool_wool_wars_two_four"
    };

    private void process() {
        if (!this.flag2) {
            this.flag2 = true;
            mc.thePlayer.sendChatMessage("/p leave");
            if (flag) {
                mc.thePlayer.sendChatMessage("/housing random");
                scheduledExecutorService.schedule(this::process3, 7500L, TimeUnit.MILLISECONDS);
            } else {
                this.process3();
            }
        } else {
            MessageUtils.x("Lobby swapping is already running.");
        }
    }

    private void process3() {
        scheduledExecutorService.schedule(() -> {
            if (this.flag2 && this.checkState44()) {
                mc.thePlayer.sendChatMessage("/play pit");
                scheduledExecutorService.schedule(() -> {
                    if (this.flag2 && this.checkState44()) {
                        mc.thePlayer.sendChatMessage(this.computeString());
                        this.process3();
                    }
                }, 7500L, TimeUnit.MILLISECONDS);
            }
        }, 7500L, TimeUnit.MILLISECONDS);
    }

    public LobbySwapper() {
        super("LobbySwapper", 0, Category.PIT);
    }

    @Override
    public void onEnable() {
        if (FreshHelper.flag) {
            flag = true;
        }

        this.process();
    }

    private void process4() {
        this.flag2 = false;
        MessageUtils.x("Lobby swapper stopped.");
    }

    private String computeString() {
        return stringArray[random.nextInt(stringArray.length)];
    }

    @Override
    public void onDisable() {
        this.process4();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            ;
        }
    }
}
