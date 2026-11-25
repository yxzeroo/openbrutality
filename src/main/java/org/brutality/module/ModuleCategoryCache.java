package org.brutality.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ModuleCategoryCache {
    public static boolean flag;
    private HashMap<Object, Module> lookupMap = new HashMap<>();
    private final Map<Category, List<Module>> lookupMap2 = new ConcurrentHashMap<>();
    private List<Module> entries = new ArrayList<>();
    private long timestamp = 0L;
    private static final long timestamp2 = 50L;
    private boolean flag2 = false;

    public List<Module> computeList() {
        return new ArrayList<>(this.lookupMap.values());
    }

    public HashMap<Object, Module> getLookupMap() {
        return this.lookupMap;
    }

    public final List<Module> computeList2() {
        long now = System.currentTimeMillis();
        if (now - this.timestamp < 50L && !this.entries.isEmpty()) {
            return new ArrayList<>(this.entries);
        }
        this.entries.clear();

        for (Module module : this.lookupMap.values()) {
            if (module.checkState44()) {
                this.entries.add(module);
            }
        }

        this.timestamp = now;
        return new ArrayList<>(this.entries);
    }

    public <T extends Module> T computeModule(Class<T> clazz) {
        return (T)this.lookupMap.get(clazz);
    }

    public void handleHashMap(HashMap<Object, Module> lookupMap) {
        this.lookupMap = lookupMap;
        this.process();
    }

    private void rebuildCategoryCache() {
        this.lookupMap2.clear();

        for (Module module : this.lookupMap.values()) {
            Category category = module.getCategory();
            this.lookupMap2.computeIfAbsent(category, category2 -> new ArrayList<>()).add(module);
        }

        this.flag2 = true;
    }

    @Deprecated
    public List<Module> computeList2(String str) {
        return this.computeList(str);
    }

    public Module computeModule(String str) {
        if (str == null) {
            return null;
        }
        for (Module module : this.lookupMap.values()) {
            if (module.getName().equalsIgnoreCase(str)) {
                return module;
            }
        }

        return null;
    }

    public void process() {
        this.flag2 = false;
        this.lookupMap2.clear();
        this.entries.clear();
        this.timestamp = 0L;
    }

    public void process2() {
        this.timestamp = 0L;
    }

    public List<Module> computeList(String str) {
        if (str != null && !str.isEmpty()) {
            String str2 = str.toLowerCase();
            ArrayList list = new ArrayList();

            for (Module module : this.lookupMap.values()) {
                if (module.getName().toLowerCase().contains(str2)) {
                    list.add(module);
                }
            }

            return list;
        }
        return new ArrayList<>();
    }

    public Module computeModule2(Class<? extends Module> clazz) {
        return this.lookupMap.get(clazz);
    }

    public List<Module> computeList(Category category) {
        if (!this.flag2) {
            this.rebuildCategoryCache();
        }

        return this.lookupMap2.getOrDefault(category, Collections.emptyList());
    }
}
