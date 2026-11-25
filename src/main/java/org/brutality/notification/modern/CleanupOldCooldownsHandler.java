package org.brutality.notification.modern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;

public class CleanupOldCooldownsHandler {
    private static CleanupOldCooldownsHandler instance;
    private final List<GetMessageHandler> entries = new CopyOnWriteArrayList<>();
    private final Queue<GetMessageHandler> queue = new LinkedList<>();
    private final Map<String, Long> lookupMap = new HashMap<>();
    private final List<GetMessageHandler> entries2 = new ArrayList<>();
    private long timestamp;
    private ModernHandler.ModernMode modernMode = ModernHandler.ModernMode.MODERN;
    private boolean flag = true;
    private int count = 5;
    private long timestamp2 = 100L;
    private boolean enabled2 = true;
    private int count2 = 2;
    private int count3 = 11;
    private int count4 = 11;

    private void handleInt(int n, int n2, int n3, int n4, int n5) {
        int n6 = Math.min(n5 / 2, 128) << 24 | 0;
    }

    public void clearForPlayer(String str) {
        if (str != null) {
            this.entries.removeIf(getMessageHandler -> str.equals(getMessageHandler.getPlayerName()));
            this.queue.removeIf(getMessageHandler2 -> str.equals(getMessageHandler2.getPlayerName()));
            this.lookupMap.entrySet().removeIf(entry -> entry.getKey().startsWith(str + ":"));
        }
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    private CleanupOldCooldownsHandler() {
    }

    public void setEnabled(boolean enabled2) {
        this.enabled2 = enabled2;
        if (!enabled2) {
            this.clearAll();
        }
    }

    public void handleString(String str, ModernHandler.PlayerJoinMode playerJoinMode) {
        this.handleString(str, playerJoinMode, ModernHandler.LowMode.NORMAL);
    }

    public void handleInt(int n) {
        this.count4 = Math.max(8, n);
    }

    private void handleGetMessageHandler(GetMessageHandler getMessageHandler, FontRenderer fontRenderer, int n) {
        if (!this.modernMode.hasAnimation()) {
            fontRenderer.drawStringWithShadow(getMessageHandler.getMessage(), this.count2, this.count3 + n, 16777215);
        } else {
            int n2 = this.count2 + getMessageHandler.getPosX();
            int n3 = this.count3 + n + getMessageHandler.getOffsetY();
            int n4 = Math.max(0, Math.min(255, (int)(getMessageHandler.getFactor() * 255.0F)));
            int n5 = n4 << 24 | 16777215;
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (this.modernMode.hasBackground()) {
                this.handleInt(n2 - 2, n3 - 2, fontRenderer.getStringWidth(getMessageHandler.getMessage()) + 4, fontRenderer.FONT_HEIGHT + 4, n4);
            }

            fontRenderer.drawStringWithShadow(getMessageHandler.getMessage(), n2, n3, n5);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    private void handleNoneMode(ModernHandler.NoneMode noneMode) {
        if (noneMode != ModernHandler.NoneMode.NONE) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.thePlayer != null) {
                mc.thePlayer.playSound(noneMode.getSoundName(), noneMode.getVolume(), 1.0F);
            }
        }
    }

    public boolean isFlag() {
        return this.flag;
    }

    public int computeInt2() {
        return this.queue.size();
    }

    private void handleString(String str, String str2) {
        if (str != null) {
            String str3 = str + ":" + str2;
            this.lookupMap.put(str3, System.currentTimeMillis());
        }
    }

    public void handleInt(int n, int n2) {
        this.count2 = n;
        this.count3 = n2;
    }

    public int computeInt() {
        return this.entries.size();
    }

    public ModernHandler.ModernMode getModernMode() {
        return this.modernMode;
    }

    public void handleString(String str, ModernHandler.PlayerJoinMode playerJoinMode, ModernHandler.LowMode lowMode, String str2, boolean flag) {
        if (this.enabled2) {
            if (this.matchesString(str2, playerJoinMode.name())) {
                GetMessageHandler getMessageHandler = new GetMessageHandler(str, playerJoinMode, lowMode, str2, flag);
                if (this.flag && getMessageHandler.checkState()) {
                    this.handleNoneMode(getMessageHandler.getNoneMode());
                }

                if (this.entries.size() >= this.count) {
                    this.queue.offer(getMessageHandler);
                } else {
                    this.entries.add(getMessageHandler);
                }

                this.handleString(str2, playerJoinMode.name());
            }
        }
    }

    public boolean isEnabled() {
        return this.enabled2;
    }

    public static CleanupOldCooldownsHandler getInstance() {
        if (instance == null) {
            instance = new CleanupOldCooldownsHandler();
        }

        return instance;
    }

    public void handleMinecraft(Minecraft mc, FontRenderer fontRenderer) {
        if (this.enabled2 && !this.entries.isEmpty()) {
            long now = System.currentTimeMillis();
            int n = 0;
            this.entries2.clear();

            for (GetMessageHandler getMessageHandler : this.entries) {
                getMessageHandler.handleLong(now);
                if (getMessageHandler.matchesLong(now)) {
                    this.entries2.add(getMessageHandler);
                } else {
                    this.handleGetMessageHandler(getMessageHandler, fontRenderer, n);
                    n += this.count4;
                }
            }

            this.entries.removeAll(this.entries2);
            this.process();
            if (now - this.timestamp >= 1000L) {
                this.cleanupOldCooldowns(now);
                this.timestamp = now;
            }
        } else {
            this.process();
        }
    }

    public void handleString(String str, ModernHandler.PlayerJoinMode playerJoinMode, ModernHandler.LowMode lowMode) {
        this.handleString(str, playerJoinMode, lowMode, null, true);
    }

    public void handleInt2(int n) {
        this.count = Math.max(1, Math.min(10, n));
    }

    private boolean matchesString(String str, String str2) {
        if (str == null) {
            return true;
        }
        String str3 = str + ":" + str2;
        Long val = this.lookupMap.get(str3);
        long now = System.currentTimeMillis();
        return val == null || now - val > this.timestamp2;
    }

    private void process() {
        while (!this.queue.isEmpty() && this.entries.size() < this.count) {
            GetMessageHandler getMessageHandler = this.queue.poll();
            if (getMessageHandler != null && !getMessageHandler.matchesLong(System.currentTimeMillis())) {
                this.entries.add(getMessageHandler);
            }
        }
    }

    private void cleanupOldCooldowns(long l) {
        this.lookupMap.entrySet().removeIf(entry -> l - entry.getValue() > 300000L);
    }

    public void handleString(String str) {
        this.handleString(str, ModernHandler.PlayerJoinMode.SYSTEM, ModernHandler.LowMode.NORMAL);
    }

    public void handleLong(long l) {
        this.timestamp2 = Math.max(0L, l);
    }

    public void setModernMode(ModernHandler.ModernMode modernMode) {
        this.modernMode = modernMode;
    }

    public void clearAll() {
        this.entries.clear();
        this.queue.clear();
    }
}
