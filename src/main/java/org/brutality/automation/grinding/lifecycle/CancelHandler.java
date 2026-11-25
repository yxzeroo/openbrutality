package org.brutality.automation.grinding.lifecycle;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import org.brutality.Brutality;
import org.brutality.gui.ChangelogScreen;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.util.GetDisplayedTitleHelper;
import org.brutality.util.KeybindStateUtil;
import org.brutality.utils.hypixel.pit.AcceptHelper;

public final class CancelHandler {
    private long timestamp;
    private int generation;
    private boolean flag;
    private ScheduledFuture<?> scheduledFuture;

    public void cancel() {
        this.generation++;
        this.flag = false;
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }

        this.scheduledFuture = null;
    }

    public void handleAutoGrinder(AutoGrinder autoGrinder, long l) {
        Minecraft mc = Minecraft.getMinecraft();
        if (!this.flag && this.scheduledFuture == null && System.currentTimeMillis() >= this.timestamp && mc.getCurrentServerData() != null) {
            this.timestamp = System.currentTimeMillis() + 60000L;
            ServerData serverData = mc.getCurrentServerData();
            int generation = this.generation;
            this.flag = true;
            CompletableFuture.supplyAsync(AcceptHelper::computeString, Brutality.scheduledExecutorService)
                .whenComplete((str, t) -> mc.addScheduledTask(() -> {
                    if (this.generation == generation) {
                        this.flag = false;
                        if (t == null && autoGrinder.checkState39() && mc.theWorld != null && GetDisplayedTitleHelper.checkState4()) {
                            long now = System.currentTimeMillis();
                            long l2 = AcceptHelper.computeLong(str, now, autoGrinder::matchesString);
                            long l3 = l2 - l - now;
                            if (l2 >= 0L && l3 >= 10000L) {
                                autoGrinder.process22();
                                KeybindStateUtil.process();
                                mc.theWorld.sendQuittingDisconnectingPacket();
                                mc.loadWorld(null);
                                mc.displayGuiScreen(new ChangelogScreen());
                                this.scheduledFuture = Brutality.scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                                    if (this.generation == generation) {
                                        this.scheduledFuture = null;
                                        if (autoGrinder.checkState39() && mc.theWorld == null && !(mc.currentScreen instanceof GuiConnecting)) {
                                            mc.displayGuiScreen(new GuiConnecting(new ChangelogScreen(), mc, serverData));
                                        }
                                    }
                                }), l3, TimeUnit.MILLISECONDS);
                            }
                        }
                    }
                }));
        }
    }
}
