package org.brutality.extensions;

import com.sun.jna.Function;
import com.sun.jna.NativeLibrary;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.DiscordEventHandlers.Builder;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {
    public volatile boolean running;
    public Thread thread;
    public DiscordRP.Entry entry;
    private DiscordEventHandlers discordEventHandlers;
    private long timestamp = 0L;
    public String r = "Idle";
    public String state = "In the brutality launcher";
    private static final long timestamp2 = 1000L;

    public synchronized void shutdown() {
        if (this.running) {
            this.running = false;
            this.thread.interrupt();

            try {
                this.entry.function3.invokeVoid(new Object[0]);
            } finally {
                this.entry.close();
                this.entry = null;
                this.discordEventHandlers = null;
            }
        }
    }

    public synchronized void start() {
        if (!this.running) {
            this.timestamp = System.currentTimeMillis() / timestamp2;
            DiscordEventHandlers discordEventHandlers = new Builder().setReadyEventHandler(new ReadyCallback() {
                @Override
                public void apply(DiscordUser discordUser) {
                    DiscordRP.this.handleString(DiscordRP.this.r, DiscordRP.this.state);
                }
            }).build();

            try {
                this.entry = DiscordRP.Entry.computeEntry(Path.of(System.getProperty("java.io.tmpdir")));
                this.discordEventHandlers = discordEventHandlers;
                this.entry.function.invokeVoid(new Object[]{"1355632568803922063", this.discordEventHandlers, 1, null});
            } catch (LinkageError | RuntimeException e) {
                if (this.entry != null) {
                    this.entry.close();
                }

                this.entry = null;
                this.discordEventHandlers = null;
                throw e;
            }

            this.running = true;
            this.thread = new Thread("Discord RPC Callback") {
                private static final long timestamp = 250L;

                @Override
                public void run() {
                    while (DiscordRP.this.running) {
                        synchronized (DiscordRP.this) {
                            if (!DiscordRP.this.running || Thread.currentThread() != DiscordRP.this.thread) {
                                return;
                            }

                            DiscordRP.this.entry.function2.invokeVoid(new Object[0]);
                        }

                        try {
                            Thread.sleep(timestamp);
                        } catch (InterruptedException e2) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }
            };
            this.thread.setDaemon(true);
            this.thread.start();
        }
    }

    public synchronized void handleString(String str, String state) {
        this.r = str;
        this.state = state;
        if (this.running) {
            net.arikia.dev.drpc.DiscordRichPresence.Builder builder = new net.arikia.dev.drpc.DiscordRichPresence.Builder(state);
            builder.setBigImage("large", "");
            builder.setDetails(str);
            builder.setStartTimestamps(this.timestamp);
            this.entry.function4.invokeVoid(new Object[]{builder.build()});
        }
    }

    public static final class Entry implements AutoCloseable {
        public final Path path;
        public final Path path2;
        public final NativeLibrary nativeLibrary;
        public final Function function;
        public final Function function2;
        public final Function function3;
        public final Function function4;

        private Entry(Path path, Path path2, NativeLibrary nativeLibrary) {
            this.path = path;
            this.path2 = path2;
            this.nativeLibrary = nativeLibrary;
            this.function = nativeLibrary.getFunction("Discord_Initialize");
            this.function2 = nativeLibrary.getFunction("Discord_RunCallbacks");
            this.function3 = nativeLibrary.getFunction("Discord_Shutdown");
            this.function4 = nativeLibrary.getFunction("Discord_UpdatePresence");
        }

        public static DiscordRP.Entry computeEntry(Path path) {
            Path path2 = null;
            Path path3 = null;
            NativeLibrary nativeLibrary = null;

            try {
                String str = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
                String str2 = !str.contains("mac") && !str.contains("darwin")
                    ? (str.contains("win") ? ("64".equals(System.getProperty("sun.arch.data.model")) ? "win-x64" : "win-x86") : "linux")
                    : "darwin";
                String str3 = str2.startsWith("win-") ? "discord-rpc.dll" : (str2.equals("darwin") ? "libdiscord-rpc.dylib" : "libdiscord-rpc.so");
                path2 = Files.createTempDirectory(path, "brutality-discord-rpc-" + ProcessHandle.current().pid() + "-");
                path3 = path2.resolve(str3);

                try (InputStream resourceAsStream = DiscordRP.class.getResourceAsStream("/" + str2 + "/" + str3)) {
                    if (resourceAsStream == null) {
                        throw new IOException("Discord native resource unavailable");
                    }

                    Files.copy(resourceAsStream, path3);
                }

                nativeLibrary = NativeLibrary.getInstance(path3.toAbsolutePath().toString());
                return new DiscordRP.Entry(path2, path3, nativeLibrary);
            } catch (RuntimeException | LinkageError | IOException e) {
                if (nativeLibrary != null) {
                    nativeLibrary.dispose();
                }

                handlePath(path3, path2);
                throw new IllegalStateException("Discord presence is unavailable on this system", e);
            }
        }

        private static void handlePath(Path path, Path path2) {
            try {
                if (path != null) {
                    Files.deleteIfExists(path);
                }

                if (path2 != null) {
                    Files.deleteIfExists(path2);
                }
            } catch (IOException e) {
                if (path2 != null) {
                    path2.toFile().deleteOnExit();
                }

                if (path != null) {
                    path.toFile().deleteOnExit();
                }
            }
        }

        @Override
        public void close() {
            try {
                this.nativeLibrary.dispose();
            } finally {
                handlePath(this.path2, this.path);
            }
        }
    }
}
