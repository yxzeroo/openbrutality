package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Arrays;
import java.util.Locale;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.HudDragHelper;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.input.Mouse;

public class AssassinESP extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private static AssassinESP instance;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 10.0, 0.0, 1200.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 20.0, 0.0, 1200.0, 1.0);
    private final BooleanSetting showLabelSetting = new BooleanSetting("Show Label", this, true);
    private final BooleanSetting showSecondsSuffixSetting = new BooleanSetting("Show Seconds Suffix", this, true);
    private final ModeSetting modeSetting = new ModeSetting("Time Format", this, "##.#", Arrays.asList("##", "##.#"));
    private int count = 0;
    private long timestamp = 0L;
    private double amount = 0.0;
    private double amount2 = 0.0;
    private double amount3 = 0.0;
    private boolean flag = false;
    private int count2 = 0;
    private static final long timestamp2 = -5093364586317348865L;
    private static final long timestamp3 = 1000L;

    private int computeInt(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);
                        if (stringTagAt.contains("Assassin")) {
                            for (int j = i; j < Math.min(i + 3, tagList.tagCount()); j++) {
                                String stringTagAt2 = tagList.getStringTagAt(j);
                                if (stringTagAt2.contains("Level: 3")) {
                                    return 3;
                                }

                                if (stringTagAt2.contains("Level: 2")) {
                                    return 2;
                                }

                                if (stringTagAt2.contains("Level: 1")) {
                                    return 1;
                                }
                            }

                            return 1;
                        }
                    }
                }
            }
        }

        return 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.timestamp = 0L;
        this.count = 0;
    }

    @Override
    public void onRender() {
        if (this.mc.thePlayer != null) {
            float f = (float)this.xPosSetting.computeDouble();
            float f2 = (float)this.yPosSetting.computeDouble();
            long now = System.currentTimeMillis();
            boolean flag = this.mc.currentScreen instanceof GuiChat;
            boolean flag2 = this.count > 0;
            boolean flag3 = now < this.timestamp;
            if (!flag2 && !flag) {
                HudDragHelper.handleObject(this);
            } else {
                int n = 0;
                if (flag3) {
                    n = (int)Math.max(0L, (this.timestamp - now) / timestamp3);
                }

                String str;
                if (flag2) {
                    if (flag3 && n > 0) {
                        str = EnumChatFormatting.WHITE
                            + (this.showLabelSetting.isEnabled() ? "Assassin: " : "")
                            + EnumChatFormatting.LIGHT_PURPLE
                            + this.computeString(this.timestamp - now);
                    } else {
                        str = EnumChatFormatting.WHITE + (this.showLabelSetting.isEnabled() ? "Assassin: " : "") + EnumChatFormatting.GREEN + "Ready";
                    }
                } else {
                    str = EnumChatFormatting.WHITE + (this.showLabelSetting.isEnabled() ? "Assassin: " : "") + EnumChatFormatting.GRAY + "Not Equipped";
                }

                if (flag) {
                    this.handleFloat(f, f2, str);
                    f = (float)this.xPosSetting.computeDouble();
                    f2 = (float)this.yPosSetting.computeDouble();
                }

                computeFontRenderer(AssassinESP.class).drawStringWithShadow(str, f, f2, (int)timestamp2);
            }
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventTick) {
                if (this.mc.thePlayer == null) {
                    return;
                }

                this.count = this.computeInt(this.mc.thePlayer);
                this.count2++;
                if (this.count2 >= 2) {
                    this.count2 = 0;
                    if (this.mc.thePlayer.isSneaking() && this.flag && this.count > 0) {
                        double d = this.computeDouble();
                        double d2 = this.mc.thePlayer.posX - this.amount;
                        double d3 = this.mc.thePlayer.posY - this.amount2;
                        double d4 = this.mc.thePlayer.posZ - this.amount3;
                        double dist = Math.sqrt(d2 * d2 + d3 * d3 + d4 * d4);
                        double d5 = Math.max(d * 0.8, 3.0);
                        if (dist > d5) {
                            int n = this.computeInt(this.count);
                            this.timestamp = System.currentTimeMillis() + n * 1000;
                        }
                    }

                    this.amount = this.mc.thePlayer.posX;
                    this.amount2 = this.mc.thePlayer.posY;
                    this.amount3 = this.mc.thePlayer.posZ;
                    this.flag = this.mc.thePlayer.isSneaking();
                }
            } else if (event instanceof ChatEvent chatEvent) {
                String text = chatEvent.message.getUnformattedText();
                if (text.contains("Death!")) {
                    this.timestamp = 0L;
                }
            }
        }
    }

    private void handleFloat(float f, float f2, String str) {
        int[] ints = HudDragHelper.computeIntArray();
        int n = ints[0];
        int n2 = ints[1];
        if (Mouse.isButtonDown(0)) {
            if (!HudDragHelper.matchesObject(this)) {
                int textWidth = computeFontRenderer(AssassinESP.class).getStringWidth(str);
                int FONT_HEIGHT = computeFontRenderer(AssassinESP.class).FONT_HEIGHT;
                if (HudDragHelper.matchesInt(n, n2, f, f2, textWidth, FONT_HEIGHT)) {
                    HudDragHelper.matchesObject(this, n, n2, (int)f, (int)f2);
                }
            }
        } else {
            HudDragHelper.handleObject(this);
        }

        if (HudDragHelper.matchesObject(this)) {
            int[] ints2 = HudDragHelper.computeIntArray(this);
            if (ints2 != null) {
                this.xPosSetting.handleDouble2(ints2[0]);
                this.yPosSetting.handleDouble2(ints2[1]);
            }
        }
    }

    private double computeDouble() {
        if (this.mc.theWorld != null && this.mc.thePlayer != null) {
            double MAX_VALUE = Double.MAX_VALUE;

            for (Entity entity : EntityCache.computeList()) {
                if (entity != this.mc.thePlayer && entity instanceof EntityLivingBase) {
                    double dist = this.mc.thePlayer.getDistanceToEntity(entity);
                    if (dist < MAX_VALUE) {
                        MAX_VALUE = dist;
                    }
                }
            }

            return MAX_VALUE == Double.MAX_VALUE ? 10.0 : MAX_VALUE;
        }
        return 10.0;
    }

    private int computeInt(int n) {
        switch (n) {
            case 1:
                return 10;
            case 2:
                return 5;
            case 3:
                return 3;
            default:
                return 10;
        }
    }

    public AssassinESP() {
        super("Assassin ESP", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(new Setting[]{this.xPosSetting, this.yPosSetting, this.showLabelSetting, this.showSecondsSuffixSetting, this.modeSetting});
        instance = this;
    }

    private String computeString(long l) {
        double d = Math.max(0.0, l / 1000.0);
        String str = this.modeSetting.matchesString2("##") ? String.format(Locale.ROOT, "%.0f", d) : String.format(Locale.ROOT, "%.1f", d);
        return str + (this.showSecondsSuffixSetting.isEnabled() ? "s" : "");
    }

    @Generated
    public static AssassinESP getInstance() {
        return instance;
    }
}
