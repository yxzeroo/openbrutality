package org.brutality.mellow.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RemoveHelper {
    private final Set<String> itemSet = ConcurrentHashMap.newKeySet();
    private Path path;

    public boolean matchesString(String str) {
        return this.itemSet.contains(str.toLowerCase());
    }

    public void remove(String str) {
        this.itemSet.remove(str.toLowerCase());
        this.save();
    }

    public Set<String> computeSet() {
        return new HashSet<>(this.itemSet);
    }

    public void add(String str) {
        this.itemSet.add(str.toLowerCase());
        this.save();
    }

    public void save() {
        try {
            if (this.path == null) {
                this.path = Paths.get("brutality/mellow_blacklist.txt");
            }

            Files.write(this.path, String.join("\n", this.itemSet).getBytes());
        } catch (Exception ignored) {
        }
    }

    public int size() {
        return this.itemSet.size();
    }

    public void load() {
        try {
            this.path = Paths.get("brutality/mellow_blacklist.txt");
            if (Files.exists(this.path)) {
                Files.readAllLines(this.path).forEach(this.itemSet::add);
            }
        } catch (Exception ignored) {
        }
    }
}
