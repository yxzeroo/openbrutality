package org.brutality.core;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.brutality.core.migration.GetPendingNameCountHandler;
import org.brutality.core.migration.LogHandler;
import org.brutality.pitutils.PitutilsHandler;
import org.brutality.service.ServiceHandler2;
import org.brutality.service.itemsearch.ItemsearchHandler;
import org.brutality.service.itemsearch.ItemsearchHandler2;
import org.brutality.service.itemsearch.PlanetPitType;
import org.brutality.util.BEASTHelper;
import org.brutality.util.ComputeScheduledFutureHelper;
import org.brutality.utils.MessageUtils;

public class BrutalityHandler {
    private static final Gson gson = new Gson();
    private final String text;
    private final File file;
    private final Map<UUID, String> lookupMap = new ConcurrentHashMap<UUID, String>();
    private final Set<String> itemSet = ConcurrentHashMap.newKeySet();

    public BrutalityHandler(String string, String string2, String string3) {
        this.text = string.toUpperCase();
        this.file = new File(new File(Minecraft.getMinecraft().mcDataDir, "Brutality"), string2);
        File file = new File(new File(Minecraft.getMinecraft().mcDataDir, "Brutality"), string3);
        try {
            if (file.exists()) {
                if (this.file.exists()) {
                    if (this.computeMap() != null) {
                        if (this.computeMap().isEmpty()) {
                            GetPendingNameCountHandler.handleLogHandler(new LogHandler(String.valueOf(new StringBuilder("[DEPRECATED] ").append(string)), file), this);
                        }
                    } else {
                        GetPendingNameCountHandler.handleLogHandler(new LogHandler(String.valueOf(new StringBuilder("[DEPRECATED] ").append(string)), file), this);
                    }
                } else {
                    GetPendingNameCountHandler.handleLogHandler(new LogHandler(String.valueOf(new StringBuilder("[DEPRECATED] ").append(string)), file), this);
                }
            }
        }
        catch (Exception exception) {
        }
        this.process();
    }

    public void process3() {
        try {
            Files.write(Paths.get(this.file.getAbsolutePath(), new String[0]), gson.toJson(this.lookupMap).getBytes(), new OpenOption[0]);
        }
        catch (IOException iOException) {
            throw new RuntimeException(iOException);
        }
    }

    public static EntityPlayer computeEntityPlayer(String string) {
        boolean bl;
        EntityPlayer entityPlayer;
        if (Minecraft.getMinecraft().theWorld == null) {
            return null;
        }
        Iterator iterator = new ArrayList(Minecraft.getMinecraft().theWorld.playerEntities).iterator();
        do {
            if (!iterator.hasNext()) {
                return null;
            }
            entityPlayer = (EntityPlayer)iterator.next();
            boolean bl2 = PitutilsHandler.matchesString(entityPlayer.getDisplayName().getUnformattedText()) ? PitutilsHandler.computeString(entityPlayer.getDisplayName().getUnformattedText()).equalsIgnoreCase(string) : false;
            bl = bl2;
        } while (!entityPlayer.getName().equalsIgnoreCase(string) && !bl);
        return entityPlayer;
    }

    public void process() {
        if (!this.file.exists()) {
            this.lookupMap.clear();
            return;
        }
        try {
            Map map = (Map)gson.fromJson(new String(Files.readAllBytes(this.file.toPath())), new Entry().getType());
            this.lookupMap.clear();
            if (map != null) {
                this.lookupMap.putAll(map);
            }
        }
        catch (IOException iOException) {
            this.lookupMap.clear();
            throw new RuntimeException(iOException);
        }
    }

    public boolean matchesEntityPlayer2(EntityPlayer entityPlayer) throws IOException {
        return this.matchesString(entityPlayer.getName());
    }

    public boolean matchesString(String string) throws IOException {
        if (ServiceHandler2.matchesString(string)) {
            return this.matchesString2(string);
        }
        if (!this.itemSet.add(string)) {
            return false;
        }
        ComputeScheduledFutureHelper.handleRunnable2(() -> {
            try {
                try {
                    this.matchesString2(string);
                    this.itemSet.remove(string);
                }
                catch (IOException iOException) {
                    throw new RuntimeException(iOException);
                }
            }
            catch (Throwable throwable) {
                this.itemSet.remove(string);
                throw throwable;
            }
        });
        return false;
    }

    public BrutalityHandler(String string, String string2) {
        this.text = string.toUpperCase();
        this.file = new File(new File(Minecraft.getMinecraft().mcDataDir, "Brutality"), string2);
        this.process();
    }

    public boolean matchesString3(String string) throws Exception {
        EntityPlayer entityPlayer = BrutalityHandler.computeEntityPlayer(string);
        if (entityPlayer == null) {
            UUID uUID = ServiceHandler2.computeUUID(string);
            if (uUID == null) {
                throw new Exception(String.valueOf(new StringBuilder("Player ").append(string).append(" not found.")));
            }
            return this.matchesUUID(uUID);
        }
        if (entityPlayer == null) {
            return false;
        }
        if (entityPlayer.getGameProfile() == null) {
            return false;
        }
        if (entityPlayer.getGameProfile().getId() == null) {
            return false;
        }
        return this.matchesUUID(entityPlayer.getGameProfile().getId());
    }

    public boolean matchesString2(String string) throws IOException {
        EntityPlayer entityPlayer = BrutalityHandler.computeEntityPlayer(string);
        if (entityPlayer != null && entityPlayer.getGameProfile() != null && entityPlayer.getGameProfile().getId() != null) {
            return this.matchesEntityPlayer(entityPlayer);
        }
        UUID uUID = ServiceHandler2.computeUUID(string);
        if (uUID == null) {
            return false;
        }
        return this.lookupMap.containsKey(uUID);
    }

    public String computeString(EntityPlayer entityPlayer) {
        String string = entityPlayer.getDisplayName().getUnformattedText();
        if (BEASTHelper.matchesEntityPlayer3(entityPlayer) && PitutilsHandler.matchesString(string)) {
            UUID uUID = ServiceHandler2.computeUUID(PitutilsHandler.computeString(string));
            if (uUID == null) {
                return null;
            }
            return this.lookupMap.get(uUID);
        }
        UUID uUID = entityPlayer.getGameProfile().getId();
        if (uUID == null) {
            return null;
        }
        return this.lookupMap.get(uUID);
    }

    private boolean matchesUUID(UUID uUID) {
        if (uUID == null) {
            return false;
        }
        String string = this.lookupMap.remove(uUID);
        this.process3();
        return string != null;
    }

    public Map<String, String> computeMap() {
        HashMap<UUID, String> hashMap = new HashMap<UUID, String>(this.lookupMap);
        HashMap<String, String> hashMap2 = new HashMap<String, String>(hashMap.size());
        Iterator<Map.Entry<UUID, String>> iterator = hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            String string;
            Map.Entry<UUID, String> entry = iterator.next();
            String string2 = entry.getValue().replace("\u00a7", "&");
            try {
                string = ServiceHandler2.computeString(entry.getKey());
            }
            catch (Exception exception) {
                string = entry.getKey().toString();
            }
            hashMap2.put(string, string2);
        }
        return hashMap2;
    }

    private boolean matchesUUID(UUID uUID, String string) {
        if (uUID == null) {
            return false;
        }
        if (string == null) {
            return false;
        }
        this.lookupMap.put(uUID, string);
        this.process3();
        return true;
    }

    public boolean matchesEntityPlayer(EntityPlayer entityPlayer) {
        return this.computeString(entityPlayer) != null;
    }

    public boolean matchesString(String string, String string2) throws Exception {
        String string3 = string2.replace("&", "\u00a7");
        EntityPlayer entityPlayer = BrutalityHandler.computeEntityPlayer(string);
        if (entityPlayer == null) {
            UUID uUID = ServiceHandler2.computeUUID(string);
            if (uUID == null) {
                throw new Exception(String.valueOf(new StringBuilder("Player ").append(string).append(" not found.")));
            }
            return this.matchesUUID(uUID, string3);
        }
        if (entityPlayer == null) {
            return false;
        }
        if (entityPlayer.getGameProfile() == null) {
            return false;
        }
        if (entityPlayer.getGameProfile().getId() == null) {
            return false;
        }
        UUID uUID = entityPlayer.getGameProfile().getId();
        if (!BEASTHelper.matchesEntityPlayer3(entityPlayer)) {
            return this.matchesUUID(uUID, string3);
        }
        UUID uUID2 = ItemsearchHandler2.computeUUID(ItemsearchHandler2.computeList(entityPlayer, new ItemsearchHandler(PlanetPitType.PITPANDA, false, 0, false, false)), 2);
        if (uUID2 != null) {
            PitutilsHandler.handleString(entityPlayer.getName(), ServiceHandler2.computeString(uUID2), false);
            this.matchesUUID(uUID2, string3);
            return true;
        }
        this.matchesUUID(entityPlayer.getGameProfile().getId(), string3);
        MessageUtils.x(String.valueOf(new StringBuilder("Failed to denick ").append(entityPlayer.getDisplayName().getUnformattedText()).append(", added fake UUID to ").append(this.text).append(" list.")));
        return true;
    }

    public void process2() {
        this.lookupMap.clear();
        this.process3();
    }

    public class Entry
    extends TypeToken<HashMap<UUID, String>> {
    }
}
