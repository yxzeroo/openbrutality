package org.brutality.gui.mainmenu.effects;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.brutality.gui.mainmenu.base.MainMenuScene;
import org.brutality.gui.mainmenu.theme.AuroraScene;
import org.brutality.gui.mainmenu.theme.BlackHoleScene;
import org.brutality.gui.mainmenu.theme.CyberpunkScene;
import org.brutality.gui.mainmenu.theme.DisposeComponent;
import org.brutality.gui.mainmenu.theme.EarthScene;
import org.brutality.gui.mainmenu.theme.FireScene;
import org.brutality.gui.mainmenu.theme.LondonScene;
import org.brutality.gui.mainmenu.theme.MarsScene;
import org.brutality.gui.mainmenu.theme.NewYorkCityScene;
import org.brutality.gui.mainmenu.theme.NormalScene;
import org.brutality.gui.mainmenu.theme.OceanScene;
import org.brutality.gui.mainmenu.theme.SolarSystemScene;

public class NebulaComponent {
    private static MainMenuScene mainMenuScene;
    private static List<MainMenuScene> entries = new ArrayList<>();
    private static boolean initialized = false;
    private static final long timestamp = 256L;

    public static MainMenuScene computeMainMenuScene(String str) {
        for (MainMenuScene mainMenuScene : entries) {
            if (mainMenuScene.getName().equals(str)) {
                return mainMenuScene;
            }
        }

        return null;
    }

    public static MainMenuScene computeMainMenuScene2() {
        init();
        return mainMenuScene;
    }

    public static void init() {
        if (!initialized) {
            entries.clear();
            handleMainMenuScene(new DisposeComponent(new FireScene().getName(), new FireScene().getDescription(), 0));
            handleMainMenuScene(new DisposeComponent(new NormalScene().getName(), new NormalScene().getDescription(), 1));
            handleMainMenuScene(new OceanScene());
            handleMainMenuScene(new DisposeComponent(new SolarSystemScene().getName(), new SolarSystemScene().getDescription(), 2));
            handleMainMenuScene(new DisposeComponent(new AuroraScene().getName(), new AuroraScene().getDescription(), 3));
            handleMainMenuScene(new DisposeComponent(new CyberpunkScene().getName(), new CyberpunkScene().getDescription(), 4));
            handleMainMenuScene(new DisposeComponent(new EarthScene().getName(), new EarthScene().getDescription(), 5));
            handleMainMenuScene(new DisposeComponent(new MarsScene().getName(), new MarsScene().getDescription(), 6));
            handleMainMenuScene(new DisposeComponent(new LondonScene().getName(), new LondonScene().getDescription(), 7));
            handleMainMenuScene(new DisposeComponent(new NewYorkCityScene().getName(), new NewYorkCityScene().getDescription(), 8));
            handleMainMenuScene(new DisposeComponent(new BlackHoleScene().getName(), new BlackHoleScene().getDescription(), 9));
            handleMainMenuScene(new DisposeComponent("Nebula", "Drifting violet clouds and layered starlight", 10));
            handleMainMenuScene(new DisposeComponent("Alpine", "Snowy mountain ridges beneath a blue dusk sky", 11));
            handleMainMenuScene(new DisposeComponent("Sunset", "Golden light across a rippling horizon", 12));
            handleMainMenuScene(new DisposeComponent("Rain", "Rain falling over a quiet city at night", 13));
            handleMainMenuScene(new DisposeComponent("Prism", "Soft iridescent ribbons of light", 14));
            handleMainMenuScene(new DisposeComponent("Sakura", "Falling cherry petals and a moonlit torii", 15));
            handleMainMenuScene(new DisposeComponent("Tokyo", "Neon districts and an illuminated Tokyo Tower", 16));
            handleMainMenuScene(new DisposeComponent("Halloween", "Moonlit pumpkins, violet fog and candlelit autumn nights", 17));
            if (mainMenuScene == null && !entries.isEmpty()) {
                mainMenuScene = computeMainMenuScene();
                if (mainMenuScene == null) {
                    mainMenuScene = computeMainMenuScene("Halloween");
                }
            }

            initialized = true;
        }
    }

    public static void process() {
        for (MainMenuScene mainMenuScene2 : entries) {
            mainMenuScene2.dispose();
        }

        initialized = false;
        String str = mainMenuScene != null ? mainMenuScene.getName() : null;
        mainMenuScene = null;
        init();
        MainMenuScene mainMenuScene3 = computeMainMenuScene(str);
        if (mainMenuScene3 != null) {
            mainMenuScene = mainMenuScene3;
        }
    }

    public static void handleMainMenuScene2(MainMenuScene mainMenuScene2) {
        if (mainMenuScene2 != null) {
            mainMenuScene = mainMenuScene2;
            Path path = computePath();
            if (path != null) {
                Path path2 = path.resolveSibling(path.getFileName() + ".tmp");

                try {
                    Files.createDirectories(path.getParent());
                    Files.writeString(path2, mainMenuScene2.getName());

                    try {
                        Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                    } catch (AtomicMoveNotSupportedException e) {
                        Files.move(path2, path, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e2) {
                    LogManager.getLogger(NebulaComponent.class).warn("Could not save menu theme", (Throwable)e2);
                }
            }
        }
    }

    public static int computeInt(MainMenuScene mainMenuScene) {
        return entries.indexOf(mainMenuScene);
    }

    public static void handleMainMenuScene(MainMenuScene mainMenuScene) {
        entries.add(mainMenuScene);
    }

    public static List<MainMenuScene> getEntries() {
        return entries;
    }

    private static Path computePath() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc != null && mc.mcDataDir != null ? mc.mcDataDir.toPath().resolve("Brutality/menu-theme.txt") : null;
    }

    private static MainMenuScene computeMainMenuScene() {
        Path path = computePath();
        if (path == null) {
            return null;
        }
        Path path2 = path.resolveSibling("menu-season-halloween-2026.txt");
        if (!Files.exists(path2)) {
            MainMenuScene mainMenuScene = computeMainMenuScene("Halloween");
            handleMainMenuScene2(mainMenuScene);

            try {
                Files.createDirectories(path.getParent());
                if (Files.isRegularFile(path) && Files.readString(path).trim().equals("Halloween")) {
                    Files.writeString(path2, "2.3.2");
                }
            } catch (IOException e) {
                LogManager.getLogger(NebulaComponent.class).warn("Could not save seasonal menu selection", (Throwable)e);
            }

            return mainMenuScene;
        }
        if (!Files.isRegularFile(path)) {
            return null;
        }
        try {
            return Files.size(path) > timestamp ? null : computeMainMenuScene(Files.readString(path).trim());
        } catch (IOException e2) {
            LogManager.getLogger(NebulaComponent.class).warn("Could not load menu theme", (Throwable)e2);
            return null;
        }
    }
}
