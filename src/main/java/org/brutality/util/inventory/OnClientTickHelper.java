package org.brutality.util.inventory;

import java.util.Iterator;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import org.brutality.event.EventState;
import org.brutality.event.impl.player.EventTick;
import org.brutality.util.ActivateSwapInputGuardHelper;
import org.brutality.util.BEASTHelper;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.LockForMillisHelper;
import org.brutality.util.UtilHelper3;
import org.brutality.util.StopwatchTimer;

public class OnClientTickHelper {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final Queue<IsCompleteApi> queue = new ConcurrentLinkedQueue<>();
    private static final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private static final long timestamp = 40L;

    public static boolean matchesIsCompleteApi(IsCompleteApi isCompleteApi) {
        if (!isCompleteApi.checkState3()) {
            return true;
        }
        boolean flag = BEASTHelper.computeInt3(mc.thePlayer) >= 2;
        return BEASTHelper.checkState() || flag;
    }

    public static void handleIterable(Iterable<IsCompleteHelper> iterable) {
        Iterator it = iterable.iterator();

        while (it.hasNext()) {
            handleIsCompleteApi((IsCompleteApi)it.next());
        }
    }

    public void onClientTick(EventTick event) {
        if (event.getState() == EventState.PRE) {
            if (mc != null) {
                if (mc.thePlayer == null) {
                    return;
                }

                if (GetMysticEnchantLoreHelper.checkState2()) {
                    return;
                }

                if (queue.isEmpty()) {
                    return;
                }

                IsCompleteApi isCompleteApi = queue.peek();
                IsCompleteApi isCompleteApi2 = isCompleteApi;
                isCompleteApi.start();
                if (isCompleteApi.isComplete()) {
                    IsCompleteApi isCompleteApi3 = queue.poll();
                    boolean flag;
                    if (!queue.isEmpty()) {
                        if (queue.peek().checkState()) {
                            if (matchesIsCompleteApi(Objects.requireNonNull(queue.peek()))) {
                                flag = true;
                            } else {
                                flag = false;
                            }
                        } else {
                            flag = false;
                        }
                    } else {
                        flag = false;
                    }

                    if (!flag) {
                        if (!GetMysticEnchantLoreHelper.checkState6()) {
                            GetMysticEnchantLoreHelper.process2();
                            ExecuteCommandHelper.handleString6(String.valueOf(new StringBuilder("Closed inventory after finishing swap: ").append(isCompleteApi3)));
                            int[] ints = new int[]{
                                mc.gameSettings.keyBindForward.getKeyCode(),
                                mc.gameSettings.keyBindBack.getKeyCode(),
                                mc.gameSettings.keyBindLeft.getKeyCode(),
                                mc.gameSettings.keyBindRight.getKeyCode(),
                                mc.gameSettings.keyBindJump.getKeyCode(),
                                mc.gameSettings.keyBindSneak.getKeyCode(),
                                mc.gameSettings.keyBindUseItem.getKeyCode()
                            };
                            int len = ints.length;

                            for (int i = 0; i < 7; i++) {
                                UtilHelper3.handleInt3(ints[i]);
                            }
                        } else {
                            ExecuteCommandHelper.handleString("Finished armor swapping but did not close inventory because you're holding an item.");
                        }
                    }

                    if (queue.isEmpty()) {
                        return;
                    }

                    isCompleteApi2 = queue.peek();
                    queue.peek().start();
                }

                if (mc.currentScreen == null && isCompleteApi2.checkState() && matchesIsCompleteApi(isCompleteApi2)) {
                    ExecuteCommandHelper.handleString6(String.valueOf(new StringBuilder("Opening inv for swap: ").append(isCompleteApi2)));
                    mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                    utilHelper8.reset();
                }

                if (!utilHelper8.matchesLong(timestamp)) {
                    return;
                }

                if (isCompleteApi2.checkState() && matchesIsCompleteApi(isCompleteApi2)) {
                    LockForMillisHelper.lockForMillis(100);
                    ActivateSwapInputGuardHelper.lockKeyboardInput(100);
                    ActivateSwapInputGuardHelper.activateSwapInputGuard(150);
                }

                isCompleteApi2.tick();
                if (isCompleteApi2.isComplete() && queue.size() >= 2) {
                    queue.stream().skip(1L).findFirst().ifPresent(isCompleteApi4 -> isCompleteApi4.start());
                }
            }
        }
    }

    public static synchronized boolean checkState() {
        return !queue.isEmpty();
    }

    public static void handleIsCompleteApi(IsCompleteApi isCompleteApi) {
        isCompleteApi.handleLong(System.currentTimeMillis());
        queue.add(isCompleteApi);
    }
}
