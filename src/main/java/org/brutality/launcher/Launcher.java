package org.brutality.launcher;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.stream.Stream;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import org.brutality.launcher.Launcher.InstallUpdateWorker;
import org.brutality.launcher.Launcher.LaunchClientWorker;
import org.brutality.launcher.Launcher.LoadSessionWorker;
import org.brutality.launcher.Launcher.SaveSessionWorker;
import org.brutality.launcher.Launcher.SignOutWorker;
import org.brutality.launcher.Launcher.StartupUpdateWorker;
import org.brutality.launcher.Launcher.WindowCloseListener;
import org.brutality.launcher.MicrosoftOAuthClient.MinecraftAccount;
import org.brutality.launcher.internal.BootstrapConfig;
import org.brutality.launcher.internal.ClientBootstrapMain;

public final class Launcher {
    static final Path launcherDir = Path.of(System.getProperty("user.home"), "Brutality", "launcher");
    static final Path minecraftDir = MinecraftDirResolver.computePath(
        System.getProperty("os.name", ""), System.getProperty("user.home"), System.getenv("APPDATA")
    );
    static final String text = "https://bru3y2ty22n2n2nsy6y2hb3b3g2.up.railway.app";
    static final HttpClient httpClient = HttpClient.newBuilder()
        .sslContext(ClientBootstrapMain.getSslContext())
        .connectTimeout(Duration.ofSeconds(15L))
        .build();
    private final JFrame jFrame = new JFrame("Brutality Launcher");
    private final JLabel jLabel = new JLabel("Ready. Updates are installed automatically.");
    private final JButton g = LauncherTheme.createButton("Play Brutality  →", true);
    private final JTextField jTextField = new JTextField("Player");
    private final JSpinner jSpinner = new JSpinner(new SpinnerNumberModel(2048, 1024, 8192, 512));
    private final JSpinner jSpinner2 = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));
    private final JCheckBox jCheckBox = new JCheckBox("AutoGrinder mode", false);
    private final JCheckBox jCheckBox2 = new JCheckBox("Discord IRC", true);
    private InstanceCoordinator instanceCoordinator;
    private final JButton jButton = LauncherTheme.createButton("Sign in with Microsoft", true);
    private final JButton jButton2 = LauncherTheme.createButton("Sign out", false);
    private final JLabel jLabel2 = new JLabel(AvatarIconLoader.loadDefaultAvatar());
    private BrutalityAccount brutalityAccount;
    private final JCheckBox r = new JCheckBox("Remember Microsoft account on this computer", true);
    private volatile MinecraftAccount minecraftAccount;
    private SwingWorker<MinecraftAccount, Void> swingWorker;
    private SavedMcSession savedMcSession;
    private boolean flag;
    private boolean w;
    private Timer x;

    public static void main(String[] parts) throws Exception {
        if (parts.length > 0 && parts[0].equals("--client")) {
            String[] parts2 = computeStringArray(Arrays.copyOfRange(parts, 1, parts.length), System.getenv("BRUTALITY_MC_SESSION"));
            BootstrapConfig.handleStringArray(parts2, Launcher.class.getName(), parts);
        } else {
            SwingUtilities.invokeLater(() -> new Launcher().process3());
        }
    }

    private void process() {
        JButton defaultButton = this.jFrame.getRootPane().getDefaultButton();
        if (this.savedMcSession != null
            && !this.flag
            && !this.w
            && !this.instanceCoordinator.acquireLock()
            && (this.swingWorker == null || this.swingWorker.isDone())
            && (defaultButton == null || defaultButton.isEnabled())) {
            this.flag = true;
            boolean enabled = this.g.isEnabled();
            boolean enabled2 = this.jButton.isEnabled();
            boolean enabled3 = this.jButton2.isEnabled();
            this.g.setEnabled(false);
            this.jButton.setEnabled(false);
            this.jButton2.setEnabled(false);
            if (defaultButton != null) {
                defaultButton.setEnabled(false);
            }

            this.jLabel.setText("Installing launcher update…");
            SavedMcSession savedMcSession = this.savedMcSession;
            new InstallUpdateWorker(this, savedMcSession, enabled, enabled2, enabled3, defaultButton).execute();
        }
    }

    private static void handlePath(Path path, Path path2) throws IOException {
        if (Files.isRegularFile(path)) {
            Files.createDirectories(path2.getParent());
            Files.copy(path, path2, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void process2() {
        if (!this.flag) {
            int val = (Integer)this.jSpinner.getValue();
            boolean selected = this.jCheckBox.isSelected();
            int n = LauncherSettings.launchCount(selected, (Integer)this.jSpinner2.getValue());
            boolean selected2 = this.r.isSelected();
            this.w = true;
            this.g.setEnabled(false);
            this.jButton.setEnabled(false);
            this.jButton2.setEnabled(false);
            new LaunchClientWorker(this, selected2, n, val).execute();
        }
    }

    static String[] computeStringArray(String[] parts, String str) throws Exception {
        String[] parts2 = (String[])parts.clone();
        if (str != null && !str.isBlank()) {
            JsonObject asJsonObject = JsonParser.parseString(str).getAsJsonObject();
            String str2 = MicrosoftOAuthClient.computeString(asJsonObject, "name");
            String str3 = MicrosoftOAuthClient.computeString(asJsonObject, "uuid");
            if (str2.matches("[A-Za-z0-9_]{1,16}") && str3.matches("[a-fA-F0-9]{32}")) {
                for (int i = 0; i + 1 < parts2.length; i++) {
                    if (parts2[i].equals("--username")) {
                        parts2[i + 1] = str2;
                    }

                    if (parts2[i].equals("--uuid")) {
                        parts2[i + 1] = str3;
                    }

                    if (parts2[i].equals("--accessToken")) {
                        parts2[i + 1] = MicrosoftOAuthClient.computeString(asJsonObject, "accessToken");
                    }

                    if (parts2[i].equals("--userType")) {
                        parts2[i + 1] = "mojang";
                    }
                }

                return parts2;
            }
            throw new IOException("Invalid Minecraft session profile.");
        }
        return parts2;
    }

    private void process3() {
        this.process6();
        LauncherSettings launcherSettings = LauncherSettings.load(launcherDir.resolve("settings.properties"));
        this.jSpinner.setValue(launcherSettings.memory());
        this.jSpinner2.setValue(launcherSettings.instances());
        this.jCheckBox.setSelected(launcherSettings.grinder());
        this.jCheckBox2.setSelected(launcherSettings.discord());
        this.instanceCoordinator = new InstanceCoordinator(launcherDir);
        this.instanceCoordinator.updateLock(this.jCheckBox2.isSelected());
        this.jSpinner.addChangeListener(changeEvent -> this.process4());
        this.jSpinner2.addChangeListener(changeEvent2 -> this.process4());
        this.jCheckBox.addActionListener(e -> this.process4());
        this.jCheckBox2.addActionListener(e2 -> {
            this.process4();
            this.instanceCoordinator.updateLock(this.jCheckBox2.isSelected());
        });
        this.jFrame.addWindowListener(new WindowCloseListener(this));
        this.jFrame.setDefaultCloseOperation(3);
        this.jFrame.setIconImages(LauncherBranding.loadWindowIcons());
        AuthPanel authPanel = new AuthPanel(brutalityAccount -> {
            this.brutalityAccount = brutalityAccount;
            this.process8();
        });
        this.jFrame.setContentPane(authPanel);
        this.jFrame.getRootPane().setDefaultButton(authPanel.getJButton());
        this.jFrame.setMinimumSize(new Dimension(380, 480));
        this.jFrame.setSize(380, 480);
        this.jFrame.setResizable(false);
        this.jFrame.setLocationRelativeTo(null);
        this.jFrame.setVisible(true);
        this.b();
    }

    static Path computePath(Path path, Set<Path> paths) {
        HashSet set = new HashSet();

        for (Path path2 : paths) {
            set.add(path2.toAbsolutePath().normalize());
        }

        for (int i = 1; i < 100000; i++) {
            Path path3 = computePath(path, i).toAbsolutePath().normalize();
            if (!set.contains(path3)) {
                return path3;
            }
        }

        throw new IllegalStateException("No free instance profile directory is available.");
    }

    static List<String> computeList(Path path, Path path2, Path path3, String str, int n) {
        String str2 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        boolean flag = str2.contains("mac") || str2.contains("darwin");
        boolean flag2 = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win") && !flag;
        ArrayList list = new ArrayList();
        list.add(path.resolve("bin").resolve(flag2 ? "java.exe" : "java").toString());
        if (flag) {
            list.add("-XstartOnFirstThread");
        }

        list.add("-Xmx" + n + "m");
        if (!flag2) {
            Path path4 = path2.getParent().resolve(!str2.contains("mac") && !str2.contains("darwin") ? "natives-linux" : "natives-macos");
            list.add("-Dorg.lwjgl.librarypath=" + path4);
            list.add("-Dnet.java.games.input.librarypath=" + path4);
        }

        for (String str3 : new String[]{
            "java.base/java.lang",
            "java.base/java.io",
            "java.base/java.net",
            "java.base/java.nio",
            "java.base/java.util",
            "java.base/java.util.concurrent",
            "java.base/java.lang.reflect",
            "java.base/java.lang.invoke",
            "java.base/java.text",
            "java.base/sun.nio.ch",
            "java.desktop/java.awt",
            "java.desktop/sun.awt"
        }) {
            list.add("--add-opens=" + str3 + "=ALL-UNNAMED");
        }

        list.addAll(
            List.of(
                "-cp",
                path2 + File.pathSeparator + path2.getParent().resolve("lib") + "/*",
                Launcher.class.getName(),
                "--client",
                "--gameDir",
                path3.toString(),
                "--assetsDir",
                path3.resolve("assets").toString(),
                "--assetIndex",
                "1.8",
                "--username",
                str,
                "--uuid",
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + str).getBytes(StandardCharsets.UTF_8)).toString(),
                "--accessToken",
                "0",
                "--userType",
                "legacy",
                "--version",
                "Brutality"
            )
        );
        return list;
    }

    static void handleString(String str) throws Exception {
        JsonObject asJsonObject = JsonParser.parseString(str).getAsJsonObject();
        if (asJsonObject.has("license") && asJsonObject.has("signature") && asJsonObject.getAsJsonObject("license").has("key")) {
            Files.createDirectories(minecraftDir);
            Path path = Files.createTempFile(minecraftDir, "license-", ".download");

            try {
                Files.writeString(path, str);
                Files.move(path, minecraftDir.resolve("license.dat"), StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(path);
            }
        } else {
            throw new IOException("Invalid license document");
        }
    }

    static void handlePath2(Path path, Path path2) throws IOException {
        Path path3 = path.resolve("Brutality/Configs");
        if (Files.isDirectory(path3)) {
            try (var stream = Files.walk(path3)) {
                for (Path path4 : (Iterable<Path>)stream::iterator) {
                    if (Files.isRegularFile(path4) && path4.getFileName().toString().endsWith(".json")) {
                        Path path5 = path2.resolve("Brutality/Configs").resolve(path3.relativize(path4));
                        Files.createDirectories(path5.getParent());
                        Files.copy(path4, path5, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
    }

    static Path computePath(Path path, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Instance numbers start at 1");
        }
        return path.resolve("brutality-instances").resolve("instance-" + n);
    }

    private void handleMinecraftAccount(MinecraftAccount minecraftAccount) {
        new SaveSessionWorker(this, minecraftAccount).execute();
    }

    private void b() {
        this.x = new Timer(2000, e -> this.process());
        this.x.start();
        new StartupUpdateWorker(this).execute();
    }

    static void b(Path path, Path path2) throws IOException {
        Files.createDirectories(path2);
        handlePath3(path.resolve("license.dat"), path2.resolve("license.dat"));
        Path path3 = path2.resolve("Brutality/.launcher-instance-initialized");
        if (!Files.exists(path3)) {
            handlePath2(path, path2);
            handlePath(path.resolve("Brutality/configbinds.json"), path2.resolve("Brutality/configbinds.json"));

            for (String str : new String[]{"options.txt", "optionsof.txt"}) {
                handlePath(path.resolve(str), path2.resolve(str));
            }

            Files.createDirectories(path3.getParent());
            Files.writeString(path3, "initialized");
        }
    }

    private void process4() {
        try {
            new LauncherSettings(
                    (Integer)this.jSpinner.getValue(), (Integer)this.jSpinner2.getValue(), this.jCheckBox.isSelected(), this.jCheckBox2.isSelected()
                )
                .save(launcherDir.resolve("settings.properties"));
        } catch (IOException e) {
            this.jLabel.setText("Could not save launcher settings.");
        }
    }

    private void g() {
        this.jButton.setEnabled(false);
        this.g.setEnabled(false);
        this.jButton2.setText("Cancel login");
        final boolean selected = this.r.isSelected();
        this.jLabel.setText("Complete Microsoft login in your browser. Sign out cancels login.");
        this.swingWorker = new SwingWorker<MinecraftAccount, Void>() {
            boolean flag = false;

            protected MinecraftAccount doInBackground() throws Exception {
                MinecraftAccount minecraftAccount = new MicrosoftOAuthClient().computeMinecraftAccount(uri -> {
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (Exception e) {
                        throw new IllegalStateException("Could not open browser");
                    }
                });
                if (this.isCancelled()) {
                    throw new CancellationException();
                }
                try {
                    if (selected) {
                        DiscordPresenceHelper.updatePresence(minecraftAccount.refreshToken());
                        this.flag = true;
                    } else {
                        DiscordPresenceHelper.shutdownPresence();
                    }
                } catch (Exception ignored) {
                }

                return minecraftAccount;
            }

            @Override
            protected void done() {
                Launcher.this.jButton.setEnabled(true);
                Launcher.this.g.setEnabled(true);
                Launcher.this.jButton2.setText("Sign out");

                try {
                    Launcher.this.minecraftAccount = this.get();
                    Launcher.this.jTextField.setText(Launcher.this.minecraftAccount.name());
                    Launcher.this.jButton.setText("Switch account");
                    Launcher.this.handleMinecraftAccount(Launcher.this.minecraftAccount);
                    Launcher.this.jLabel
                        .setText(
                            selected && !this.flag
                                ? "Signed in for this launch; account could not be remembered."
                                : "Signed in as " + Launcher.this.minecraftAccount.name() + ". Ready to play."
                        );
                } catch (Exception e2) {
                    Launcher.this.jLabel.setText("Microsoft login cancelled or failed. Try again.");
                }
            }
        };
        this.swingWorker.execute();
    }

    private void process6() {
        try {
            Path parent = Path.of(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

            for (int i = 0; i < 4 && parent != null; parent = parent.getParent()) {
                Path path = parent.resolve("license.dat");
                if (Files.isRegularFile(path)) {
                    handleString(Files.readString(path));
                    return;
                }

                i++;
            }

            Path path2 = launcherDir.resolve("game/license.dat");
            if (!Files.isRegularFile(minecraftDir.resolve("license.dat")) && Files.isRegularFile(path2)) {
                handleString(Files.readString(path2));
            }
        } catch (Exception ignored) {
        }
    }

    private void process7() {
        this.jButton.setEnabled(false);
        this.g.setEnabled(false);
        this.jButton2.setEnabled(false);
        new LoadSessionWorker(this).execute();
    }

    private static void handlePath3(Path path, Path path2) throws IOException {
        if (Files.isRegularFile(path) && !Files.exists(path2)) {
            Files.createDirectories(path2.getParent());
            Files.copy(path, path2);
        }
    }

    private void process8() {
        this.jTextField.setEditable(false);
        this.jTextField.setText("Not connected");
        this.jButton.addActionListener(e -> this.g());
        this.jButton2.addActionListener(e2 -> {
            if (this.swingWorker != null && !this.swingWorker.isDone()) {
                this.swingWorker.cancel(true);
            } else {
                this.minecraftAccount = null;
                this.jTextField.setText("Not connected");
                this.jButton.setText("Sign in with Microsoft");
                this.jLabel2.setIcon(AvatarIconLoader.loadDefaultAvatar());
                new SignOutWorker(this).execute();
            }
        });
        JButton button = LauncherTheme.createButton("Open game logs", false);
        button.addActionListener(e3 -> {
            try {
                Desktop.getDesktop().open(launcherDir.toFile());
            } catch (Exception e4) {
                this.jLabel.setText("No game log yet.");
            }
        });
        this.g.addActionListener(e5 -> this.process2());
        this.jFrame
            .setContentPane(
                new DashboardPanel(
                    this.brutalityAccount.username(),
                    this.jLabel2,
                    this.jTextField,
                    this.jButton,
                    this.jButton2,
                    this.r,
                    this.jSpinner,
                    this.g,
                    button,
                    this.jLabel,
                    this.jSpinner2,
                    this.jCheckBox,
                    this.jCheckBox2
                )
            );
        this.jFrame.setResizable(true);
        this.jFrame.setMinimumSize(new Dimension(1000, 680));
        this.jFrame.setSize(1080, 710);
        this.jFrame.setLocationRelativeTo(null);
        this.jFrame.getRootPane().setDefaultButton(this.g);
        this.jFrame.revalidate();
        this.jFrame.repaint();
        this.process7();
    }

    static final class WindowCloseListener extends java.awt.event.WindowAdapter {
        WindowCloseListener(Launcher launcher) {}
    }

    static final class StartupUpdateWorker extends javax.swing.SwingWorker<Void, Void> {
        StartupUpdateWorker(Launcher launcher) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class InstallUpdateWorker extends javax.swing.SwingWorker<Void, Void> {
        InstallUpdateWorker(Launcher launcher, SavedMcSession session, boolean a, boolean b, boolean c, javax.swing.JButton btn) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class SignOutWorker extends javax.swing.SwingWorker<Void, Void> {
        SignOutWorker(Launcher launcher) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class SaveSessionWorker extends javax.swing.SwingWorker<Void, Void> {
        SaveSessionWorker(Launcher launcher, MinecraftAccount account) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class LoadSessionWorker extends javax.swing.SwingWorker<Void, Void> {
        LoadSessionWorker(Launcher launcher) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class LaunchClientWorker extends javax.swing.SwingWorker<Void, Void> {
        LaunchClientWorker(Launcher launcher, boolean remember, int count, int memory) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }
}
