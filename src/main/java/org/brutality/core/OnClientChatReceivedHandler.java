package org.brutality.core;

import java.util.List;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.pit.PitEvent11;
import org.brutality.event.impl.pit.PitEvent4;
import org.brutality.event.impl.pit.PitEvent5;
import org.brutality.event.impl.pit.PitEvent6;
import org.brutality.event.impl.player.EventTick;
import org.brutality.util.DebugHelper;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.GetOrderedTeamsHelper;
import org.brutality.util.contract.Contract;
import org.brutality.util.contract.IsVileContractChoiceHelper;
import org.brutality.util.contract.KillsType;
import org.brutality.util.inventory.GetHoveredSlotHelper;
import org.brutality.util.inventory.GetMysticEnchantLoreHelper;

public class OnClientChatReceivedHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final OnClientChatReceivedHandler instance = new OnClientChatReceivedHandler();
    private static Contract contract = null;
    private static int count = -1;
    private static UUID uuid = null;

    public void onClientChatReceived(ChatEvent chatEvent) {
        if (chatEvent.byteVal == 0) {
            String str = ExecuteCommandHelper.computeString6(chatEvent.message.getUnformattedText());
            if (str.startsWith("CONTRACT STARTED!") && contract != null) {
                new PitEvent6(contract).process();
            }

            if (str.startsWith("CONTRACT COMPLETED!")) {
                new PitEvent4(contract).process();
                handleContract(null);
                if (count != -1) {
                    chatEvent.message = new ChatComponentText(
                        String.valueOf(
                            new StringBuilder()
                                .append(chatEvent.message.getFormattedText())
                                .append(EnumChatFormatting.AQUA)
                                .append(" [")
                                .append(count)
                                .append(" left]")
                        )
                    );
                }
            }

            if (str.startsWith("CONTRACT FAILED!")) {
                new PitEvent5(contract).process();
                handleContract(null);
            }
        }
    }

    public void onClientTick(EventTick event) {
        if (event.getState() == EventState.POST) {
            if (mc.currentScreen != null) {
                if (mc.thePlayer != null) {
                    if (GetHoveredSlotHelper.matchesGuiScreen7(mc.currentScreen)) {
                        Slot slot = GetHoveredSlotHelper.computeSlot("big time contract");
                        if (slot != null) {
                            if (slot.getHasStack()) {
                                var list = GetMysticEnchantLoreHelper.computeList(slot.getStack());
                                if (list != null) {
                                    if (!list.isEmpty()) {
                                        for (String str : list) {
                                            String str2 = ExecuteCommandHelper.computeString6(str).trim();
                                            if (str2.startsWith("Daily contracts:")) {
                                                String[] parts = str2.split(" ");
                                                if (parts.length >= 3) {
                                                    try {
                                                        count = Integer.parseInt(parts[2]);
                                                    } catch (NumberFormatException e) {
                                                        ExecuteCommandHelper.handleString6(
                                                            String.valueOf(
                                                                new StringBuilder("Failed to parse contracts remaining from lore line: ").append(str)
                                                            )
                                                        );
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static boolean checkState2() {
        if (!checkState()) {
            return false;
        }
        KillsType killsType = computeKillsType();
        return killsType == null ? false : killsType.isAutomaticallyTrackable();
    }

    public void onMouseInput(BasicEvent3 basicEvent3) {
        if (mc.currentScreen != null) {
            if (mc.thePlayer == null) {
                return;
            }

            if (!GetHoveredSlotHelper.matchesGuiScreen11(mc.currentScreen)) {
                return;
            }

            int n = basicEvent3.count6;
            if (n != 0 && n != 1) {
                return;
            }

            Slot hoveredSlot = GetHoveredSlotHelper.getHoveredSlot();
            if (hoveredSlot != null) {
                if (!hoveredSlot.getHasStack()) {
                    return;
                }

                if (!IsVileContractChoiceHelper.matchesSlot(hoveredSlot)) {
                    return;
                }

                Contract contract2 = new Contract(hoveredSlot);
                if (contract2.equals(contract)) {
                    return;
                }

                handleContract(contract2);
            }
        }
    }

    public static void handleContract(Contract contract2) {
        ExecuteCommandHelper.handleString6(String.valueOf(new StringBuilder("Old Contract: ").append(contract == null ? "null" : contract.computeString())));
        ExecuteCommandHelper.handleString6(
            String.valueOf(new StringBuilder("Setting last picked contract to: ").append(contract2 == null ? "null" : contract2.computeString()))
        );
        contract = contract2;
    }

    public void onContractCompleted(PitEvent4 pitEvent4) {
        if (count != -1) {
            count--;
        }
    }

    public static Contract computeContract() {
        return !checkState() ? null : contract;
    }

    public static KillsType computeKillsType() {
        if (!checkState()) {
            return null;
        }
        return contract == null ? null : contract.getKillsType();
    }

    public static boolean checkState() {
        return mc.thePlayer == null ? false : mc.theWorld != null && GetOrderedTeamsHelper.computeInt(mc.theWorld.getScoreboard(), "Contract: ") != -1;
    }

    public static void onEvent(Event event) {
        if (event instanceof EventTick) {
            instance.onClientTick((EventTick)event);
        } else if (event instanceof BasicEvent3) {
            instance.onMouseInput((BasicEvent3)event);
        } else if (event instanceof ChatEvent) {
            instance.onClientChatReceived((ChatEvent)event);
        } else if (event instanceof PitEvent11) {
            instance.onPitStateUpdate((PitEvent11)event);
        } else if (event instanceof PitEvent4) {
            instance.onContractCompleted((PitEvent4)event);
        }
    }

    public static int getCount() {
        return count;
    }

    public void onPitStateUpdate(PitEvent11 pitEvent11) {
        if (mc.thePlayer == null) {
            DebugHelper.error("Player is null on lobby join, skipping contract reset");
        } else {
            UUID uniqueID = mc.thePlayer.getUniqueID();
            if (!uniqueID.equals(uuid)) {
                uuid = uniqueID;
                count = -1;
            }
        }
    }
}
