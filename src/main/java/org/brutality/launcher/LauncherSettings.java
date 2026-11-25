package org.brutality.launcher;

import java.io.IOException;
import java.nio.file.Path;

record LauncherSettings(int memory, int instances, boolean grinder, boolean discord) {
    static int launchCount(boolean grinder, int instances) {
        throw new UnsupportedOperationException("missing-from-dump: org.brutality.launcher.j");
    }

    static LauncherSettings load(Path path) {
        throw new UnsupportedOperationException("missing-from-dump: org.brutality.launcher.j");
    }

    void save(Path path) throws IOException {
        throw new UnsupportedOperationException("missing-from-dump: org.brutality.launcher.j");
    }
}
