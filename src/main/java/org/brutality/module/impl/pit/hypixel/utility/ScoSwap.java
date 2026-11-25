package org.brutality.module.impl.pit.hypixel.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.scoreboard.Scoreboard;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.util.GetOrderedTeamsHelper;
import org.brutality.util.StopwatchTimer;
import org.brutality.utils.player.StartHotbarMovesHelper;

public class ScoSwap extends Module {
    private final PitCategory pitCategory = PitCategory.SWAP;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int count = 5000;
    private static final long timestamp = 3000L;
    private static final Pattern pattern = Pattern.compile("(\\d[\\d,]*)g");
    private final ModeSetting silentSetting = new ModeSetting("Mode", this, "Normal", Arrays.asList("Normal", "Silent"));
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 50.0, 1.0, 500.0, 50.0);
    private final NumberSetting swapBackDelaySetting = new NumberSetting("Swap Back Delay", this, 200.0, 0.0, 2000.0, 50.0);
    private final BooleanSetting safeModeSetting = new BooleanSetting("Safe Mode", this, true);
    private boolean flag = false;
    private boolean flag2 = false;
    private ItemStack itemStack = null;
    private final StopwatchTimer utilHelper8 = new StopwatchTimer();
    private ScheduledFuture<?> scheduledFuture;

    private int computeInt2() {
        if (mc.thePlayer == null) {
            return -1;
        }
        int n = -1;

        try {
            String text = mc.thePlayer.getDisplayName().getFormattedText();
            if (text != null) {
                String str = text.replaceAll("§.", "");
                Matcher matcher = pattern.matcher(str);
                if (matcher.find()) {
                    n = Integer.parseInt(matcher.group(1).replace(",", ""));
                }
            }
        } catch (Exception ignored) {
        }

        try {
            Scoreboard scoreboard = mc.theWorld.getScoreboard();
            var list = GetOrderedTeamsHelper.computeList(scoreboard);
            if (list != null) {
                for (String str2 : list) {
                    if (str2 != null) {
                        String str3 = str2.replaceAll("§.", "");
                        if (str3.contains("Bounty")) {
                            Matcher matcher2 = pattern.matcher(str3);
                            if (matcher2.find()) {
                                int n2 = Integer.parseInt(matcher2.group(1).replace(",", ""));
                                if (n2 > n) {
                                    n = n2;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored2) {
        }

        return n;
    }

    private int computeInt() {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof ItemArmor && ((ItemArmor)stack.getItem()).armorType == 2 && this.matchesItemStack(stack)) {
                return 36 + i;
            }
        }

        for (int j = 9; j < 36; j++) {
            ItemStack stack2 = mc.thePlayer.inventory.mainInventory[j];
            if (stack2 != null && stack2.getItem() instanceof ItemArmor && ((ItemArmor)stack2.getItem()).armorType == 2 && this.matchesItemStack(stack2)) {
                return j;
            }
        }

        return -1;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.scheduledFuture != null) {
            this.scheduledFuture.cancel(false);
        }

        this.scheduledFuture = null;
        this.flag = false;
        this.flag2 = false;
        this.itemStack = null;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            StartHotbarMovesHelper.onEvent3(event);
            if (event instanceof ChatEvent chatEvent) {
                if (this.flag && !this.flag2 && chatEvent.message != null && chatEvent.message.getUnformattedText().contains("SELF-CHECKOUT!")) {
                    this.flag2 = true;
                    this.scheduledFuture = StartHotbarMovesHelper.scheduledExecutorService.schedule(() -> mc.addScheduledTask(() -> {
                        if (this.checkState44()) {
                            this.scheduledFuture = null;
                            this.process();
                        }
                    }), Math.max(1L, (long)this.swapBackDelaySetting.computeDouble()), TimeUnit.MILLISECONDS);
                }
            } else if (event instanceof EventUpdate) {
                if (((EventUpdate)event).getState() == EventState.PRE) {
                    if (mc.thePlayer != null && mc.theWorld != null) {
                        if (!StartHotbarMovesHelper.flag) {
                            if (this.flag) {
                                if (!this.flag2 && this.utilHelper8.matchesLong(3000L)) {
                                    this.flag2 = true;
                                    this.process();
                                }
                            } else {
                                int n = this.computeInt2();
                                if (n >= 5000) {
                                    if (!this.checkState()) {
                                        int n2 = this.computeInt();
                                        if (n2 != -1) {
                                            this.itemStack = mc.thePlayer.inventory.armorInventory[1];
                                            this.flag = true;
                                            this.flag2 = false;
                                            this.utilHelper8.reset();
                                            List list = new ArrayList();
                                            list.add(new StartHotbarMovesHelper.Entry3(2, n2));
                                            if (this.silentSetting.matchesString2("Silent")) {
                                                StartHotbarMovesHelper.startSwap(
                                                    list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, null
                                                );
                                            } else {
                                                StartHotbarMovesHelper.handleList(
                                                    list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, null
                                                );
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

    public ScoSwap() {
        super("ScoSwap", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.silentSetting, this.delaySetting, this.swapBackDelaySetting, this.safeModeSetting});
    }

    private void process() {
        this.flag2 = false;
        if (this.flag) {
            this.flag = false;
            if (mc.thePlayer == null) {
                this.itemStack = null;
            } else {
                int n = StartHotbarMovesHelper.computeInt3(this.itemStack, 2);
                if (n == -1) {
                    this.itemStack = null;
                } else {
                    List list = new ArrayList();
                    list.add(new StartHotbarMovesHelper.Entry3(2, n));
                    Runnable task = () -> this.itemStack = null;
                    if (this.silentSetting.matchesString2("Silent")) {
                        StartHotbarMovesHelper.startSwap(list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task);
                    } else {
                        StartHotbarMovesHelper.handleList(list, this.safeModeSetting.isEnabled(), (long)this.delaySetting.computeDouble(), true, true, task);
                    }
                }
            }
        }
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.hasTagCompound()) {
            String str = stack.getTagCompound().toString().toLowerCase();
            if (!str.contains("self-checkout") && !str.contains("max_bounty_self_claim")) {
                NBTTagCompound tagCompound = stack.getTagCompound();
                if (tagCompound.hasKey("display", 10)) {
                    NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                    if (compoundTag.hasKey("Lore", 9)) {
                        NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                        for (int i = 0; i < tagList.tagCount(); i++) {
                            if (tagList.getStringTagAt(i).contains("Self-checkout")) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
            return true;
        }
        return false;
    }

    private boolean checkState() {
        return this.matchesItemStack(mc.thePlayer.inventory.armorInventory[1]);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.flag = false;
        this.flag2 = false;
        this.itemStack = null;
    }
}
