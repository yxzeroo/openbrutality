package org.brutality.module.impl.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import org.brutality.event.Event;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.utils.MessageUtils;

public final class PotionEffectDebug extends Module {
    private static PotionEffectDebug instance;
    private final BooleanSetting chatSetting = new BooleanSetting("Chat", this, true);
    private final BooleanSetting includeSelfSetting = new BooleanSetting("Include Self", this, false);
    private Logger logger;
    private FileHandler fileHandler;
    private long timestamp;
    private int count;
    private static final long timestamp2 = 3228106280192180224L;
    private static final long timestamp3 = 1000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44() && event instanceof RenderEvent4 && this.logger != null) {
            this.logger.info("WORLD CHANGE");
        }
    }

    @Override
    public void onDisable() {
        if (this.logger != null) {
            this.logger.info("Disabled");
        }

        this.process();
    }

    @Override
    public void onEnable() {
        this.process();
        this.timestamp = 0L;
        this.count = 0;

        try {
            Path path = mc.mcDataDir.toPath().resolve("Brutality/debug").toAbsolutePath();
            Files.createDirectories(path);
            this.fileHandler = new FileHandler(path.toString().replace("%", "%%") + "/potion-effects-%g.log", (int)timestamp2, 3, true);
            this.fileHandler.setEncoding("UTF-8");
            this.fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    Instant instant = Instant.ofEpochMilli(logRecord.getMillis());
                    String message = logRecord.getMessage();
                    String str = System.lineSeparator();
                    String str2 = message;
                    Instant instant2 = instant;
                    return "[" + instant2 + "] " + str2 + str;
                }
            });
            this.logger = Logger.getAnonymousLogger();
            this.logger.setUseParentHandlers(false);
            this.logger.addHandler(this.fileHandler);
            this.logger.info("Enabled; received S1D/S1E packets only. Long durations may be capped at 32767 ticks.");
            MessageUtils.x("PotionEffectDebug: logging to Brutality/debug/potion-effects-0.log. Chat limited to 10 entries/sec; file records all entries.");
        } catch (SecurityException | IOException e) {
            this.process();
            MessageUtils.x("PotionEffectDebug: could not open log: " + e.getMessage());
        }
    }

    public PotionEffectDebug() {
        super("PotionEffectDebug", 0, Category.CLIENT);
        this.handleSettingArray(new Setting[]{this.chatSetting, this.includeSelfSetting});
        instance = this;
    }

    private void process() {
        if (this.fileHandler != null) {
            if (this.logger != null) {
                this.logger.removeHandler(this.fileHandler);
            }

            this.fileHandler.close();
        }

        this.logger = null;
        this.fileHandler = null;
    }

    public static void handleEntity(Entity entity, int n, int n2, int n3, int n4, boolean flag) {
        PotionEffectDebug potionEffectDebug = instance;
        if (potionEffectDebug != null && potionEffectDebug.checkState44()) {
            if (entity != mc.thePlayer || potionEffectDebug.includeSelfSetting.isEnabled()) {
                if (entity == null || entity instanceof EntityPlayer) {
                    Potion potion = n2 >= 0 && n2 < Potion.potionTypes.length ? Potion.potionTypes[n2] : null;
                    String str = potion == null ? "Unknown" : I18n.format(potion.getName());
                    String str2 = entity == null ? "unresolved" : entity.getName();
                    String str3 = (flag ? "S1E REMOVE " : "S1D APPLY ") + str2 + " entity=" + n + " effect=" + str + " id=" + n2;
                    if (!flag) {
                        int n5 = n3 + 1;
                        String str4 = String.format(Locale.ROOT, "%.2f", n4 / 20.0);
                        int n6 = n5;
                        str3 = str3 + " level=" + n6 + " duration=" + n4 + " ticks (" + str4 + " seconds)";
                    }

                    if (potionEffectDebug.logger != null) {
                        potionEffectDebug.logger.info(str3);
                    }

                    long now = System.currentTimeMillis();
                    if (now - potionEffectDebug.timestamp >= timestamp3) {
                        potionEffectDebug.timestamp = now;
                        potionEffectDebug.count = 0;
                    }

                    if (potionEffectDebug.chatSetting.isEnabled() && potionEffectDebug.count++ < 10) {
                        MessageUtils.x("[PotionEffectDebug] " + str3);
                    }
                }
            }
        }
    }
}
