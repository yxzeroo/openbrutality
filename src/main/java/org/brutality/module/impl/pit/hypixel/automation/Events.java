package org.brutality.module.impl.pit.hypixel.automation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.hypixel.pit.AcceptHelper;
import org.brutality.utils.hypixel.pit.DisplayHelper3;
import org.brutality.utils.hypixel.pit.PitHelper;
import org.lwjgl.input.Mouse;

public class Events extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPositionSetting = new NumberSetting("X Position", this, 10.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPositionSetting = new NumberSetting("Y Position", this, 20.0, 0.0, 1080.0, 1.0);
    private final NumberSetting eventCountSetting = new NumberSetting("Event Count", this, 12.0, 1.0, 25.0, 1.0);
    private final BooleanSetting nightQuestSetting = new BooleanSetting("Night Quest", this, true);
    private volatile List<DisplayHelper3.Entry> entries = Collections.emptyList();
    private volatile long timestamp;
    private long timestamp2;
    private volatile int generation;
    private boolean flag;
    private ExecutorService executor;
    private long timestamp3 = Long.MIN_VALUE;
    private List<DisplayHelper3.Entry> entries2;
    private Object object;
    private FontRenderer fontRenderer;
    private int count;
    private int count2;
    private boolean flag2;
    private List<String> entries3 = Collections.emptyList();

    public Events() {
        super("Events", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPositionSetting, this.yPositionSetting, this.eventCountSetting, this.nightQuestSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && ((EventUpdate)event).getState() == EventState.PRE) {
                this.refreshEventsAsync(System.currentTimeMillis());
            }
        }
    }

    private synchronized void refreshEventsAsync(long l) {
        if (!this.flag) {
            int n = 0;

            for (DisplayHelper3.Entry entry : this.entries) {
                if (entry.timestamp > l) {
                    if (++n >= 10) {
                        break;
                    }
                }
            }

            if (l >= this.timestamp || n < 10 && l - this.timestamp2 >= 15000L) {
                if (this.executor == null || this.executor.isShutdown()) {
                    this.executor = Executors.newSingleThreadExecutor(task -> {
                        Thread thread = new Thread(task, "Brutality-Events");
                        thread.setDaemon(true);
                        return thread;
                    });
                }

                this.flag = true;
                this.timestamp2 = l;
                this.timestamp = l + 15000L;
                int generation = this.generation;
                this.executor.execute(() -> {
                    try {
                        var list = DisplayHelper3.parse(AcceptHelper.computeString(), System.currentTimeMillis());
                        synchronized (this) {
                            if (generation == this.generation) {
                                this.entries = list;
                                this.timestamp = System.currentTimeMillis() + (list.size() < 10 ? 15000 : 300000);
                                return;
                            }
                        }
                    } catch (RuntimeException e) {
                        return;
                    } finally {
                        synchronized (this) {
                            if (generation == this.generation) {
                                this.flag = false;
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onRender() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null && computeFontRenderer(Events.class) != null) {
            long now = System.currentTimeMillis();
            FontRenderer fontRenderer = computeFontRenderer(Events.class);
            if (this.timestamp3 != now / 1000L
                || this.entries2 != this.entries
                || this.object != this.mc.theWorld
                || this.fontRenderer != fontRenderer
                || this.count != (int)this.eventCountSetting.computeDouble()
                || this.flag2 != this.nightQuestSetting.isEnabled()) {
                this.timestamp3 = now / 1000L;
                this.entries2 = this.entries;
                this.object = this.mc.theWorld;
                this.fontRenderer = fontRenderer;
                this.count = (int)this.eventCountSetting.computeDouble();
                this.flag2 = this.nightQuestSetting.isEnabled();
                ArrayList list = new ArrayList();
                this.count2 = 0;

                for (DisplayHelper3.Entry entry : this.computeList(now)) {
                    boolean flag = entry.text.equals("Night Quest");
                    String str = flag ? (PitHelper.matchesLong(now) ? "Ends in " : "In ") : "";
                    long l = entry.computeLong(now) + (flag ? 999 : 0);
                    EnumChatFormatting enumChatFormatting = DisplayHelper3.computeEnumChatFormatting(entry.text);
                    String text = entry.text;
                    EnumChatFormatting GRAY = EnumChatFormatting.GRAY;
                    EnumChatFormatting GREEN = EnumChatFormatting.GREEN;
                    String str2 = DisplayHelper3.computeString(l);
                    EnumChatFormatting GRAY2 = EnumChatFormatting.GRAY;
                    String str3 = str2;
                    EnumChatFormatting enumChatFormatting2 = GREEN;
                    EnumChatFormatting enumChatFormatting3 = GRAY;
                    var str4 = text;
                    String str5 = enumChatFormatting + str4 + enumChatFormatting3 + " [" + enumChatFormatting2 + str + str3 + GRAY2 + "]";
                    list.add(str5);
                    this.count2 = Math.max(this.count2, fontRenderer.getStringWidth(str5));
                }

                this.entries3 = list;
            }

            if (this.entries3.isEmpty()) {
                HudDragHelper.handleObject(this);
            } else {
                int n = this.count2;
                float f = (float)this.xPositionSetting.computeDouble();
                float f2 = (float)this.yPositionSetting.computeDouble();
                int n2 = fontRenderer.FONT_HEIGHT + 1;
                if (this.mc.currentScreen instanceof GuiChat) {
                    int[] ints = HudDragHelper.computeIntArray();
                    if (Mouse.isButtonDown(0)) {
                        if (!HudDragHelper.matchesObject(this) && HudDragHelper.matchesInt(ints[0], ints[1], f, f2, n, this.entries3.size() * n2)
                            )
                         {
                            HudDragHelper.matchesObject(this, ints[0], ints[1], (int)f, (int)f2);
                        }
                    } else {
                        HudDragHelper.handleObject(this);
                    }

                    if (HudDragHelper.matchesObject(this)) {
                        int[] ints2 = HudDragHelper.computeIntArray(this);
                        if (ints2 != null) {
                            f = ints2[0];
                            f2 = ints2[1];
                            this.xPositionSetting.handleDouble2(f);
                            this.yPositionSetting.handleDouble2(f2);
                        }
                    }
                } else {
                    HudDragHelper.handleObject(this);
                }

                for (String str6 : this.entries3) {
                    fontRenderer.drawStringWithShadow(str6, f + n - fontRenderer.getStringWidth(str6), f2, 16777215);
                    f2 += n2;
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }
    }

    private List<DisplayHelper3.Entry> computeList(long l) {
        Long val = this.nightQuestSetting.isEnabled() && this.mc.theWorld != null ? PitHelper.computeLong2(l) : null;
        return DisplayHelper3.display(this.entries, l, (int)this.eventCountSetting.computeDouble(), val);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.timestamp = 0L;
        this.refreshEventsAsync(System.currentTimeMillis());
    }

    @Override
    public synchronized void onDisable() {
        super.onDisable();
        this.generation++;
        if (this.executor != null) {
            this.executor.shutdownNow();
        }

        this.executor = null;
        this.flag = false;
        HudDragHelper.handleObject(this);
    }
}
