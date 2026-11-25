package org.brutality.module.impl.pit.orion.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.util.ChatComponentText;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class AutoCompleteChecker extends Module {
    public static AutoCompleteChecker instance;
    private static final List<String> entries = new ArrayList<>();
    private static boolean flag;
    private final PitCategory pitCategory = PitCategory.AUTOMATION;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean flag2 = false;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (this.mc.thePlayer != null && this.flag2 && event instanceof GetPacketEvent getPacketEvent) {
                if (getPacketEvent.getPacket() instanceof S3APacketTabComplete packet) {
                    String[] parts = packet.func_149630_c();
                    if (!flag) {
                        this.mc.thePlayer.addChatMessage(new ChatComponentText("§e[AutoCompleteChecker] Received " + parts.length + " suggestions:"));
                    }

                    for (String str : parts) {
                        if (!flag) {
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§7[Debug] Suggestion: " + str));
                        }
                    }

                    ArrayList<String> list = new ArrayList<>();

                    for (String str2 : parts) {
                        if (str2.toLowerCase().startsWith("/minecraft:tell ")) {
                            String str3 = str2.substring("/minecraft:tell ".length()).trim();
                            if (!str3.isEmpty()) {
                                list.add(str3);
                            }
                        } else if (!str2.contains(" ") && !str2.startsWith("/")) {
                            list.add(str2);
                        }
                    }

                    if (!flag) {
                        this.mc.thePlayer.addChatMessage(new ChatComponentText("§e[AutoCompleteChecker] Extracted " + list.size() + " autocomplete players:"));
                    }

                    for (String str4 : list) {
                        if (!flag) {
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§7[Debug] Autocomplete Player: " + str4));
                        }
                    }

                    var list2 = this.computeList();
                    if (!flag) {
                        this.mc.thePlayer.addChatMessage(new ChatComponentText("§e[AutoCompleteChecker] Tab list contains " + list2.size() + " players:"));
                    }

                    for (String str5 : list2) {
                        if (!flag) {
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§7[Debug] Tab Player: " + str5));
                        }
                    }

                    List<String> list3 = (List<String>)(Object)(list.stream().filter(str6 -> !list2.contains(str6)).collect(Collectors.toList()));
                    entries.clear();
                    entries.addAll(list3);
                    if (!flag && list3.isEmpty()) {
                        this.mc
                            .thePlayer
                            .addChatMessage(new ChatComponentText("§a[AutoCompleteChecker] No hidden players found (all autocomplete players are in tab list)"));
                    } else if (!flag) {
                        this.mc
                            .thePlayer
                            .addChatMessage(
                                new ChatComponentText(
                                    "§c[AutoCompleteChecker] Found " + list3.size() + " hidden players (in autocomplete but not in tab list):"
                                )
                            );

                        for (String str7 : list3) {
                            this.mc.thePlayer.addChatMessage(new ChatComponentText("§e- " + str7));
                        }
                    }

                    flag = false;
                    this.flag2 = false;
                    this.handleFlag3(false);
                }
            }
        }
    }

    private List<String> computeList() {
        List list = new ArrayList();
        if (this.mc.thePlayer != null && this.mc.getNetHandler() != null) {
            var playerInfoMap = this.mc.getNetHandler().getPlayerInfoMap();
            if (playerInfoMap != null) {
                for (NetworkPlayerInfo playerInfo : playerInfoMap) {
                    String name = playerInfo.getGameProfile().getName();
                    if (name != null && !name.isEmpty()) {
                        list.add(name);
                    }
                }
            }

            return list;
        }
        return list;
    }

    public static List<String> computeList2() {
        return Collections.unmodifiableList(new ArrayList<>(entries));
    }

    public static void process() {
        handleFlag(false);
    }

    public static void handleFlag(boolean flag2) {
        if (instance != null && instance.mc.thePlayer != null && instance.mc.getNetHandler() != null) {
            flag = flag2;
            if (!instance.checkState44()) {
                instance.handleFlag3(true);
            } else {
                instance.onEnable();
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            super.onEnable();
            if (this.mc.thePlayer != null && this.mc.getNetHandler() != null) {
                this.mc.getNetHandler().addToSendQueue(new C14PacketTabComplete("/minecraft:tell "));
                if (!flag) {
                    this.mc.thePlayer.addChatMessage(new ChatComponentText("§e[AutoCompleteChecker] Sent tab completion request for /minecraft:tell "));
                }

                this.flag2 = true;
            } else {
                if (!flag) {
                    this.mc.thePlayer.addChatMessage(new ChatComponentText("§c[AutoCompleteChecker] Cannot send packet: Player or network handler is null"));
                }

                this.handleFlag3(false);
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.flag2 = false;
    }

    public AutoCompleteChecker() {
        super("AutoCompleteChecker", 0, Category.PIT);
        instance = this;
    }
}
