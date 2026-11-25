package org.brutality.module.impl.pit.hypixel.automation;

import java.util.Locale;
import java.util.regex.Pattern;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.AttackEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.HudDragHelper;

public final class PerunCounter extends Module {
    public static final long timestamp = 3500L;
    private static final Pattern pattern = Pattern.compile("(?i)^(?:combo:\\s*)?perun(?:'s)?(?:\\s+wrath)?\\s+(?:I{1,3}|[1-3])\\b.*");
    private final PitCategory pitCategory = PitCategory.HUD;
    private final NumberSetting xPosSetting = new NumberSetting("X Pos", this, 900.0, 0.0, 1920.0, 1.0);
    private final NumberSetting yPosSetting = new NumberSetting("Y Pos", this, 620.0, 0.0, 1080.0, 1.0);
    private final BooleanSetting showTimerSetting = new BooleanSetting("Show Timer", this, true);
    private final BooleanSetting hideWhenIdleSetting = new BooleanSetting("Hide When Idle", this, false);
    private final BooleanSetting onlyHoldingPerunSetting = new BooleanSetting("Only Holding Perun", this, true);
    private int count;
    private long timestamp2 = -1L;
    private Object object;
    private Object object2;

    private void handleAttackEvent(AttackEvent attackEvent, long l) {
        if (attackEvent.getEntityPlayer() == mc.thePlayer
            && attackEvent.getEntityPlayer2() != null
            && attackEvent.getEntityPlayer2() != mc.thePlayer
            && matchesItemStack(attackEvent.computeItemStack())) {
            this.handleLong(l);
            this.count = (this.count + 1) % 4;
            this.timestamp2 = this.count == 0 ? -1L : l;
        }
    }

    public int computeInt(long l) {
        this.handleLong(l);
        return this.count;
    }

    public PerunCounter() {
        super("PerunCounter", 0, Category.PIT);
        this.process18();
        this.handleSettingArray(
            new Setting[]{this.xPosSetting, this.yPosSetting, this.showTimerSetting, this.hideWhenIdleSetting, this.onlyHoldingPerunSetting}
        );
    }

    public long computeLong(long l) {
        this.handleLong(l);
        return this.timestamp2 < 0L ? 0L : Math.max(0L, 3500L - (l - this.timestamp2));
    }

    @Override
    public void onEnable() {
        this.reset();
        super.onEnable();
    }

    private static boolean matchesNBTTagCompound(NBTTagCompound nbt) {
        NBTTagList tagList = nbt.getTagList("CustomEnchants", 10);

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            String str = compoundTagAt.getString("Key").toLowerCase(Locale.ROOT);
            if (("melee_lightning".equals(str) || "perun".equals(str)) && compoundTagAt.getInteger("Level") > 0) {
                return true;
            }
        }

        return false;
    }

    private boolean checkState() {
        if (mc.theWorld != null && mc.thePlayer != null && !mc.thePlayer.isDead && !(mc.thePlayer.getHealth() <= 0.0F)) {
            if (this.object != mc.theWorld || this.object2 != mc.thePlayer) {
                this.reset();
                this.object = mc.theWorld;
                this.object2 = mc.thePlayer;
            }

            return true;
        }
        this.reset();
        return false;
    }

    private void reset() {
        this.count = 0;
        this.timestamp2 = -1L;
        this.object = this.object2 = null;
    }

    public static boolean matchesItemStack(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemSword && stack.hasTagCompound()) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (!matchesNBTTagCompound(tagCompound) && !matchesNBTTagCompound(tagCompound.getCompoundTag("ExtraAttributes"))) {
                NBTTagList tagList = tagCompound.getCompoundTag("display").getTagList("Lore", 8);

                for (int i = 0; i < tagList.tagCount(); i++) {
                    String str = StringUtils.stripControlCodes(tagList.getStringTagAt(i)).trim();
                    if (pattern.matcher(str).matches()) {
                        return true;
                    }
                }

                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDisable() {
        this.reset();
        HudDragHelper.handleObject(this);
        super.onDisable();
    }

    @Override
    public void onRender() {
        if (!this.checkState()) {
            HudDragHelper.handleObject(this);
        } else {
            long now = System.currentTimeMillis();
            int n = this.computeInt(now);
            boolean active = EditableModulesHelper.isActive();
            if (active
                || (!this.hideWhenIdleSetting.isEnabled() || n != 0)
                    && (!this.onlyHoldingPerunSetting.isEnabled() || matchesItemStack(mc.thePlayer.getHeldItem()))) {
                String str = "§fPerun: §b" + (active && n == 0 ? 1 : n) + "/4";
                if (this.showTimerSetting.isEnabled()) {
                    str = str + " §7(" + String.format(Locale.ROOT, "%.1f", active && n == 0 ? 3.5 : this.computeLong(now) / 1000.0) + "s)";
                }

                FontRenderer fontRenderer = this.computeFontRenderer2();
                if (fontRenderer != null) {
                    HudDragHelper.handleObject(this, this.xPosSetting, this.yPosSetting, fontRenderer.getStringWidth(str), fontRenderer.FONT_HEIGHT + 2);
                    fontRenderer.drawStringWithShadow(str, (float)this.xPosSetting.computeDouble(), (float)this.yPosSetting.computeDouble(), -1);
                }
            } else {
                HudDragHelper.handleObject(this);
            }
        }
    }

    private void handleLong(long l) {
        if (this.timestamp2 >= 0L && (l < this.timestamp2 || l - this.timestamp2 >= 3500L)) {
            this.count = 0;
            this.timestamp2 = -1L;
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof RenderEvent4) {
                this.reset();
            } else if (this.checkState()) {
                if (event instanceof AttackEvent) {
                    this.handleAttackEvent((AttackEvent)event, System.currentTimeMillis());
                }
            }
        }
    }
}
