import java.io.File;
import java.util.Arrays;
import net.minecraft.client.main.Main;
import org.brutality.management.NativesExtractor;

public class Start {
    public static void main(String[] args) {
        Main.main(
            computeObjectArray(
                new String[]{"--version", "GradleMCP", "--accessToken", "0", "--assetsDir", "assets", "--assetIndex", "1.8", "--userProperties", "{}"}, args
            )
        );
    }

    private static File computeFile() {
        try {
            String path = Start.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            File file = new File(path);

            for (File i = file; i != null; i = i.getParentFile()) {
                if (new File(i, "settings.gradle").exists() || new File(i, "build.gradle").exists()) {
                    return i;
                }
            }
        } catch (Exception e) {
            System.out.println("Error finding project root: " + e.getMessage());
        }

        return new File(System.getProperty("user.dir"));
    }

    public static <T> T[] computeObjectArray(T[] first, T[] second) {
        Object[] objs = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, objs, first.length, second.length);
        return (T[])objs;
    }

    static {
        NativesExtractor.extractNatives();
        File file = computeFile();
        String str = System.getProperty("os.name").toLowerCase();
        String str2;
        if (str.contains("win")) {
            str2 = "windows";
        } else if (str.contains("mac")) {
            str2 = "macos";
        } else {
            str2 = "linux";
        }

        File file2 = new File(file, "test_natives/" + str2);
        System.out.println("Current working directory: " + System.getProperty("user.dir"));
        System.out.println("Project root: " + file.getAbsolutePath());
        System.out.println("Natives directory: " + file2.getAbsolutePath());
        System.out.println("Natives directory exists: " + file2.exists());
        System.setProperty("org.lwjgl.librarypath", file2.getAbsolutePath());
        System.out.println("org.lwjgl.librarypath set to: " + System.getProperty("org.lwjgl.librarypath"));
        if (file2.exists()) {
            System.out.println("Files in natives directory:");
            File[] files = file2.listFiles();
            if (files != null) {
                for (File file3 : files) {
                    System.out.println("  - " + file3.getName());
                }
            }
        }
    }
}
