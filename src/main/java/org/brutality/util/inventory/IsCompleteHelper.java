package org.brutality.util.inventory;

import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.util.ExecuteCommandHelper;
import org.brutality.util.PlaySoundHelper;
import org.brutality.util.StopwatchTimer;

public class IsCompleteHelper implements IsCompleteApi {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int count = 500;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private final IsCompleteHelper.FromIndexMode fromIndexMode;
    private final int count2;
    private final int count3;
    private final int count4;
    private final String text;
    private IsCompleteHelper.DefaultMode defaultMode = IsCompleteHelper.DefaultMode.DEFAULT;
    private String description2;
    private boolean started;
    private boolean complete;
    private long timestamp = -1L;

    public void setDescription(String description2) {
        this.description2 = description2;
    }

    public IsCompleteHelper(int n, ItemStack stack, int n2, int n3, boolean flag) {
        this.count2 = n;
        this.count3 = n2;
        this.count4 = n3;
        this.text = HelmetHelper.computeString(stack);
        this.fromIndexMode = IsCompleteHelper.FromIndexMode.FROM_INDEX;
        this.defaultMode = flag ? IsCompleteHelper.DefaultMode.JUMP_SPRINT_ONLY : IsCompleteHelper.DefaultMode.DEFAULT;
    }

    @Override
    public void handleLong(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void handleFunction(Function<Void, Boolean> fn) {
    }

    public IsCompleteHelper(ItemStack stack, int n, int n2) {
        this(HelmetHelper.computeString(stack), n, n2);
    }

    @Override
    public void tick() {
        if (this.text == null) {
            this.process();
        } else {
            int n = this.computeInt();
            if (!this.complete) {
                boolean flag = HelmetHelper.matchesInt2(n);
                boolean flag2 = HelmetHelper.matchesInt2(this.count3);
                if (!flag && !flag2) {
                    int n2 = this.count3;
                    String description2 = this.description2;
                    ExecuteCommandHelper.handleString6(
                        "Invalid swap: neither source nor target slot is in the hotbar " + description2 + "(sourceSlotId" + n + ", targetSlotId" + n2 + ")"
                    );
                    PlaySoundHelper.process12();
                    this.process();
                } else if (n == this.count3) {
                    this.process();
                    ExecuteCommandHelper.handleString6("Source and target slot are the same, no swap needed: " + this.description2);
                } else if (this.utilHelper8.matchesLong(this.count4) && !this.complete) {
                    if (OnClientTickHelper.matchesIsCompleteApi(this) || mc.currentScreen != null) {
                        if (n == -1) {
                            ExecuteCommandHelper.handleString7(EnumChatFormatting.RED + "Item to swap not found in inventory or armor slots.");
                            PlaySoundHelper.process12();
                            this.process();
                        } else {
                            PlaySoundHelper.playSound("random.click", 1.0F, 1.2F);
                            int n3 = HelmetHelper.computeInt2(n);
                            int n4 = HelmetHelper.computeInt2(this.count3);
                            if (flag2) {
                                HelmetHelper.handleInt(n, n4);
                            } else if (flag) {
                                HelmetHelper.handleInt(this.count3, n3);
                            } else {
                                int n5 = this.count3;
                                String description22 = this.description2;
                                ExecuteCommandHelper.handleString6(
                                    "Cannot swap items: neither source nor target slot is in the hotbar "
                                        + description22
                                        + "(sourceSlotId"
                                        + n
                                        + ", targetSlotId"
                                        + n5
                                        + ")"
                                );
                                PlaySoundHelper.process12();
                            }

                            this.process();
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean checkState3() {
        return this.defaultMode != IsCompleteHelper.DefaultMode.DEFAULT && !this.utilHelper8.matchesLong(500L);
    }

    @Override
    public String toString() {
        return this.description2;
    }

    private void process() {
        this.complete = true;
        if (this.timestamp != -1L) {
            long l = System.currentTimeMillis() - this.timestamp;
            String description2 = this.description2;
            ExecuteCommandHelper.handleString6("Finished swap " + description2 + " in " + l + "ms");
        }
    }

    public IsCompleteHelper(String text, int n, int n2) {
        this.text = text;
        this.count3 = n;
        this.count4 = n2;
        this.fromIndexMode = IsCompleteHelper.FromIndexMode.FROM_SIGNATURE;
        this.count2 = -1;
    }

    public void handleFlag(boolean flag) {
        this.defaultMode = flag ? IsCompleteHelper.DefaultMode.JUMP_SPRINT_ONLY : IsCompleteHelper.DefaultMode.DEFAULT;
    }

    public IsCompleteHelper(int n, ItemStack stack, int n2, int n3) {
        this(n, stack, n2, n3, false);
    }

    @Override
    public boolean checkState2() {
        return false;
    }

    @Override
    public void start() {
        if (!this.started) {
            this.utilHelper8.reset();
            this.started = true;
        }
    }

    @Override
    public boolean isComplete() {
        return this.complete;
    }

    @Override
    public boolean checkState() {
        return true;
    }

    private int computeInt() {
        if (this.fromIndexMode == IsCompleteHelper.FromIndexMode.FROM_SIGNATURE) {
            return HelmetHelper.computeInt(this.text);
        }
        Slot slot = mc.thePlayer.inventoryContainer.getSlot(this.count2);
        if (slot == null) {
            ExecuteCommandHelper.handleString7(EnumChatFormatting.RED + "null source slot for swap: " + this.description2);
            PlaySoundHelper.process12();
            this.process();
            return this.count2;
        }
        if (!this.text.equals(HelmetHelper.computeString(slot.getStack()))) {
            ExecuteCommandHelper.handleString7(
                EnumChatFormatting.RED + "Item in source slot does not match intended item for swap: " + this.description2
            );
            PlaySoundHelper.process12();
            this.process();
        }

        return this.count2;
    }

    public static enum DefaultMode {
        DEFAULT,
        JUMP_SPRINT_ONLY;

        private static IsCompleteHelper.DefaultMode[] computeDefaultModeArray() {
            return new IsCompleteHelper.DefaultMode[]{DEFAULT, JUMP_SPRINT_ONLY};
        }
    }

    public static enum FromIndexMode {
        FROM_INDEX,
        FROM_SIGNATURE;

        private static IsCompleteHelper.FromIndexMode[] computeFromIndexModeArray() {
            return new IsCompleteHelper.FromIndexMode[]{FROM_INDEX, FROM_SIGNATURE};
        }
    }
}
