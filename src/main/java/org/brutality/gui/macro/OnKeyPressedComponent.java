package org.brutality.gui.macro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.input.Keyboard;

public final class OnKeyPressedComponent {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(task -> {
        Thread thread = new Thread(task, "Brutality-ChatMacros");
        thread.setDaemon(true);
        return thread;
    });
    private static List<OnKeyPressedComponent.Entry> entries;
    private static final long timestamp = -4467937422990054816L;

    public static void onKeyPressed(int n) {
        if (n != 0) {
            ArrayList list;
            synchronized (OnKeyPressedComponent.class) {
                list = new ArrayList<>(computeList());
            }

            for (OnKeyPressedComponent.Entry entry : ((Iterable<OnKeyPressedComponent.Entry>)(Object)(list))) {
                if (entry.key == n && !entry.flag && entry.message != null && !entry.message.trim().isEmpty()) {
                    entry.flag = true;
                    String str = entry.message.trim();
                    Runnable task = () -> Minecraft.getMinecraft().addScheduledTask(() -> {
                        entry.flag = false;
                        Minecraft mc = Minecraft.getMinecraft();
                        if (mc.thePlayer != null) {
                            mc.thePlayer.sendChatMessage(str);
                        }
                    });
                    if (entry.count <= 0) {
                        task.run();
                    } else {
                        scheduledExecutorService.schedule(task, (long)entry.count, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
    }

    public static synchronized List<OnKeyPressedComponent.Entry> computeList() {
        if (entries == null) {
            load();
        }

        return entries;
    }

    public static synchronized void save() {
        Path path = computePath();
        if (path != null && entries != null) {
            try {
                Files.createDirectories(path.getParent());
                Path path2 = path.resolveSibling(path.getFileName() + ".tmp");
                Files.write(path2, gson.toJson(entries).getBytes(StandardCharsets.UTF_8));
                Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                LogManager.getLogger(OnKeyPressedComponent.class).warn("Could not save chat macros", (Throwable)e);
            }
        }
    }

    public static String computeString(int n) {
        if (n == 0) {
            return "None";
        }
        if (n < 0) {
            return "Mouse " + (n + 101);
        }
        String keyName = Keyboard.getKeyName(n);
        return keyName == null ? "Key " + n : keyName;
    }

    private static Path computePath() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? mc.mcDataDir.toPath().resolve("Brutality/chat-macros.json") : null;
    }

    private static void load() {
        entries = new ArrayList<>();
        Path path = computePath();
        if (path != null && Files.isRegularFile(path)) {
            try {
                var obj = gson.fromJson(
                    new String(Files.readAllBytes(path), StandardCharsets.UTF_8), (new TypeToken<List<OnKeyPressedComponent.Entry>>() {}).getType()
                );
                if (obj != null) {
                    for (OnKeyPressedComponent.Entry entry : ((Iterable<OnKeyPressedComponent.Entry>)(Object)(obj))) {
                        if (entry != null) {
                            if (entry.name == null) {
                                entry.name = "Macro";
                            }

                            if (entry.message == null) {
                                entry.message = "";
                            }

                            entry.count = Math.max(0, Math.min((int)timestamp, entry.count));
                            entries.add(entry);
                        }
                    }
                }
            } catch (RuntimeException | IOException e) {
                LogManager.getLogger(OnKeyPressedComponent.class).warn("Could not load chat macros", (Throwable)e);
            }
        }
    }

    private OnKeyPressedComponent() {
    }

    public static final class Entry {
        public String name = "New macro";
        public String message = "/play pit";
        public int key = 0;
        public int count = 0;
        public transient volatile boolean flag;
    }
}
