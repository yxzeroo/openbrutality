package org.brutality.module.impl.misc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.ResourceLocation;
import org.brutality.event.Event;
import org.brutality.event.impl.ImplEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
import org.brutality.utils.MessageUtils;

public class SoundDebugger extends Module {
    private final BooleanSetting chatSetting = new BooleanSetting("Chat", this, true);
    private final BooleanSetting logToFileSetting = new BooleanSetting("Log To File", this, true);
    private final BooleanSetting showSkippedSetting = new BooleanSetting("Show Skipped", this, true);
    private final BooleanSetting ignoreHarpSetting = new BooleanSetting("Ignore Harp", this, false);
    private final BooleanSetting ignoreStoneFootstepsSetting = new BooleanSetting("Ignore Stone Footsteps", this, false);
    private final BooleanSetting ignorePlayerHitSoundsSetting = new BooleanSetting("Ignore Player Hit Sounds", this, false);
    private final BooleanSetting ignorePlayerFootstepsSetting = new BooleanSetting("Ignore Player Footsteps", this, false);
    private final BooleanSetting ignorePlayerActionsSetting = new BooleanSetting("Ignore Player Actions", this, false);
    private final StringSetting filterSetting = new StringSetting("Filter", this, "");
    private final NumberSetting messagesPerSecondSetting = new NumberSetting("Messages Per Second", this, 20.0, 1.0, 100.0, 1.0);
    private final List<Pattern> entries = new ArrayList<>();
    private Logger logger;
    private FileHandler fileHandler;
    private long timestamp;
    private long timestamp2;
    private int count;
    private int count2;
    private static final long timestamp3 = 1815558185840279552L;

    @Override
    public synchronized void onDisable() {
        if (this.logger != null && this.logToFileSetting.isEnabled()) {
            this.logger.info("Sound Debugger disabled; suppressed in current window=" + this.count2);
        }

        this.process();
        this.entries.clear();
    }

    private boolean matchesString(String str) {
        if (!this.ignoreHarpSetting.isEnabled() || !str.contains("harp") && !str.contains("note_block")) {
            if (!this.ignoreStoneFootstepsSetting.isEnabled() || !str.contains("step.stone") && !str.contains("step_stone") && !str.contains("stone_step")) {
                if (!this.ignorePlayerHitSoundsSetting.isEnabled()
                    || !str.contains(".hurt") && !str.contains(".damage") && !str.contains(".hit") && !str.contains(".attack")) {
                    return !this.ignorePlayerFootstepsSetting.isEnabled() || !str.contains("step") && !str.contains("footstep")
                        ? this.ignorePlayerActionsSetting.isEnabled()
                            && (
                                str.contains(".jump")
                                    || str.contains(".land")
                                    || str.contains(".fall")
                                    || str.contains(".death")
                                    || str.contains(".eat")
                                    || str.contains(".drink")
                                    || str.contains(".splash")
                                    || str.contains(".swim")
                            )
                        : true;
                }
                return true;
            }
            return true;
        }
        return true;
    }

    private void process() {
        if (this.fileHandler != null) {
            if (this.logger != null) {
                this.logger.removeHandler(this.fileHandler);
            }

            this.fileHandler.close();
        }

        this.fileHandler = null;
        this.logger = null;
    }

    @Override
    public synchronized void onEvent(Event event) {
        if (this.checkState44()) {
            ISound iSound;
            String str;
            if (event instanceof ImplEvent.Entry2 entry2) {
                iSound = entry2.iSound;
                ResourceLocation resourceLocation = entry2.resourceLocation;
                boolean flag = entry2.flag;
                String text = entry2.text;
                str = "source=" + text + " streaming=" + flag + " file=" + resourceLocation;
            } else {
                if (!(event instanceof ImplEvent.Entry) || !this.showSkippedSetting.isEnabled()) {
                    return;
                }

                ImplEvent.Entry entry = (ImplEvent.Entry)event;
                iSound = entry.iSound;
                str = "SKIPPED: " + entry.text;
            }

            String str2 = iSound.getSoundLocation().toString();
            String str3 = str2.toLowerCase(Locale.ROOT);
            if (!this.matchesString(str3)) {
                String str4 = this.filterSetting.getValue().trim().toLowerCase(Locale.ROOT);
                if (str4.isEmpty() || str2.toLowerCase(Locale.ROOT).contains(str4)) {
                    for (Pattern pattern : this.entries) {
                        if (pattern.matcher(iSound.getSoundLocation().getResourcePath()).matches()) {
                            return;
                        }
                    }

                    long l = System.nanoTime() / 1000000L;
                    if (this.timestamp == 0L || l - this.timestamp >= 1000L) {
                        if (this.count2 > 0 && this.logger != null && this.logToFileSetting.isEnabled()) {
                            this.logger.info("Sound Debugger chat rate limit: omitted " + this.count2 + " chat messages; file log is complete");
                        }

                        this.timestamp = l;
                        this.count = this.count2 = 0;
                    }

                    long l2 = this.timestamp2 == 0L ? -1L : l - this.timestamp2;
                    this.timestamp2 = l;
                    String str5 = l2 < 0L ? "delay=first" : "delay=" + l2 + "ms";
                    float pitch = iSound.getPitch();
                    float volume = iSound.getVolume();
                    float xPosF = iSound.getXPosF();
                    float zPosF = iSound.getZPosF();
                    float yPosF = iSound.getYPosF();
                    float f = xPosF;
                    float f2 = volume;
                    float f3 = pitch;
                    String str6 = "[SoundDebugger] "
                        + str5
                        + " "
                        + str2
                        + " pitch="
                        + f3
                        + " volume="
                        + f2
                        + " position="
                        + f
                        + ","
                        + yPosF
                        + ","
                        + zPosF
                        + " "
                        + str;
                    if (this.logToFileSetting.isEnabled() && this.logger != null) {
                        this.logger.info(str6);
                    }

                    if (this.chatSetting.isEnabled()) {
                        if (this.count++ >= this.messagesPerSecondSetting.computeDouble()) {
                            this.count2++;
                        } else {
                            MessageUtils.x(str6);
                        }
                    }
                }
            }
        }
    }

    public SoundDebugger() {
        super("Sound Debugger", 0, Category.MISCELLANEOUS);
        this.setRequiresInGame(false);
        this.handleSettingArray(
            new Setting[]{
                this.chatSetting,
                this.logToFileSetting,
                this.showSkippedSetting,
                this.ignoreHarpSetting,
                this.ignoreStoneFootstepsSetting,
                this.ignorePlayerHitSoundsSetting,
                this.ignorePlayerFootstepsSetting,
                this.ignorePlayerActionsSetting,
                this.filterSetting,
                this.messagesPerSecondSetting
            }
        );
    }

    @Override
    public synchronized void onEnable() {
        this.process();
        this.entries.clear();
        this.count = this.count2 = 0;
        this.timestamp = 0L;
        this.timestamp2 = 0L;
        Path path = mc.mcDataDir.toPath().resolve("Brutality/logs").toAbsolutePath();
        Path path2 = mc.mcDataDir.toPath().resolve("Brutality/SoundDebugger/blacklist.txt");

        try {
            Files.createDirectories(path2.getParent());
            if (!Files.exists(path2)) {
                Files.writeString(path2, "# One full-match sound-name regex per line. Reload by toggling Sound Debugger.\n", StandardCharsets.UTF_8);
            }

            try (var stream = Files.lines(path2, StandardCharsets.UTF_8)) {
                stream.limit(256L).map(String::trim).filter(str -> !str.isEmpty() && !str.startsWith("#")).forEach(str2 -> {
                    try {
                        if (str2.length() <= 256) {
                            this.entries.add(Pattern.compile(str2));
                        }
                    } catch (PatternSyntaxException e) {
                        MessageUtils.x("Sound Debugger: ignored invalid blacklist pattern: " + str2);
                    }
                });
            }

            Files.createDirectories(path);
            this.fileHandler = new FileHandler(path.toString().replace("%", "%%") + "/sound-debugger-%g.log", (int)timestamp3, 3, true);
            this.fileHandler.setEncoding("UTF-8");
            this.fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    Instant instant = Instant.ofEpochMilli(logRecord.getMillis());
                    String message = logRecord.getMessage();
                    String str3 = System.lineSeparator();
                    String str4 = message;
                    Instant instant2 = instant;
                    return "[" + instant2 + "] " + str4 + str3;
                }
            });
            this.logger = Logger.getAnonymousLogger();
            this.logger.setLevel(Level.INFO);
            this.logger.setUseParentHandlers(false);
            this.logger.addHandler(this.fileHandler);
            if (this.logToFileSetting.isEnabled()) {
                this.logger
                    .info("Sound Debugger enabled; source creation hook, normal + streaming; rate limit=" + this.messagesPerSecondSetting.computeDouble());
            }
        } catch (SecurityException | IOException e2) {
            this.process();
            MessageUtils.x("Sound Debugger: could not open Brutality/logs/sound-debugger-0.log: " + e2.getMessage());
        }
    }
}
