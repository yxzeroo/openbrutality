package org.brutality.module.impl.visual;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.GetCurrentScreenHelper;

public class FallView extends Module {
    private final NumberSetting damageThresholdSetting = new NumberSetting("Damage threshold", this, 0.0, 0.0, 100.0, 5.0);
    private final BooleanSetting disableWhileFlyingSetting = new BooleanSetting("Disable while flying", this, true);
    private final BooleanSetting onlyWhileSneakingSetting = new BooleanSetting("Only while sneaking", this, false);
    private final BooleanSetting overrideHealthFormatSetting = new BooleanSetting("Override health format", this, true);
    private final BooleanSetting showDamageSetting = new BooleanSetting("Show damage", this, true);
    private final BooleanSetting showDistanceSetting = new BooleanSetting("Show distance", this, false);
    private double amount = -1.0;
    private double amount2 = -1.0;
    private float factor = 0.0F;
    private int count = -1;
    private final ItemStack[] itemStackArray = new ItemStack[4];
    private boolean flag = false;
    private final Map<FallView.Entry, Integer> lookupMap = new HashMap<>();
    private static final int count2 = 1000;
    private String dy;
    private boolean flag2;
    private String dz;
    private int count3 = Color.WHITE.getRGB();
    private boolean flag3;

    @Override
    public void onDisable() {
        super.onDisable();
        this.amount = -1.0;
        this.amount2 = -1.0;
        this.factor = 0.0F;
        this.count = -1;
        this.flag = false;

        for (int i = 0; i < this.itemStackArray.length; i++) {
            this.itemStackArray[i] = null;
        }

        this.lookupMap.clear();
        this.process();
    }

    private void process3() {
        this.process();
        if (mc.currentScreen == null) {
            if (GetCurrentScreenHelper.checkState2() && !mc.thePlayer.capabilities.isCreativeMode) {
                if (!this.disableWhileFlyingSetting.isEnabled() || !mc.thePlayer.capabilities.allowFlying) {
                    if (!this.onlyWhileSneakingSetting.isEnabled() || mc.thePlayer.isSneaking()) {
                        if (mc.thePlayer.onGround) {
                            this.amount = -1.0;
                            this.amount2 = -1.0;
                            this.factor = 0.0F;
                        } else if (this.amount == -1.0) {
                            this.amount = mc.thePlayer.posY;
                            this.amount2 = this.computeDouble(mc.thePlayer.posX, mc.thePlayer.posZ);
                        } else {
                            double d = this.computeDouble(mc.thePlayer.posX, mc.thePlayer.posZ);
                            if (d != this.amount2) {
                                this.amount2 = d;
                                this.factor = 0.0F;
                            }
                        }

                        float f = this.computeFloat();
                        if (!(f <= 2.5F)) {
                            PotionEffect activePotionEffect = mc.thePlayer.getActivePotionEffect(Potion.jump);
                            float f2 = activePotionEffect != null ? activePotionEffect.getAmplifier() + 1 : 0.0F;
                            int n = activePotionEffect != null ? activePotionEffect.getAmplifier() + 1 : 0;
                            PotionEffect activePotionEffect2 = mc.thePlayer.getActivePotionEffect(Potion.resistance);
                            boolean flag = activePotionEffect2 != null;
                            int n2 = flag ? activePotionEffect2.getAmplifier() + 1 : 0;
                            ItemStack[] stacks = new ItemStack[4];
                            boolean flag2 = false;
                            int n3 = 0;

                            for (int i = 0; i < 4; i++) {
                                ItemStack stack = mc.thePlayer.inventory.armorItemInSlot(i);
                                stacks[i] = stack;
                                if (this.itemStackArray[i] != stack) {
                                    flag2 = true;
                                }

                                if (stack != null) {
                                    n3 = n3 * 31 + (stack.getItem() != null ? stack.getItem().hashCode() : 0);
                                    n3 = n3 * 31 + stack.getItemDamage();
                                    n3 = n3 * 31 + EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack);
                                }
                            }

                            int count = this.count;
                            if (flag2 || !this.flag) {
                                long l = 0L;

                                for (int j = 0; j < 100; j++) {
                                    int enchantmentModifierDamage = EnchantmentHelper.getEnchantmentModifierDamage(stacks, DamageSource.fall);
                                    if (enchantmentModifierDamage > 20) {
                                        enchantmentModifierDamage = 20;
                                    }

                                    l += enchantmentModifierDamage;
                                }

                                count = (int)Math.round(l / 100.0);
                                this.count = count;
                                System.arraycopy(stacks, 0, this.itemStackArray, 0, 4);
                                this.flag = true;
                                if (this.lookupMap.size() > 500) {
                                    this.lookupMap.clear();
                                }
                            }

                            FallView.Entry entry = new FallView.Entry(n3, f, n, n2);
                            Integer integer = this.lookupMap.get(entry);
                            int n4;
                            if (integer != null) {
                                n4 = integer;
                            } else {
                                float f3 = f - 3.0F - f2;
                                double d2 = Math.max(0, MathHelper.ceiling_double_int(f3));
                                if (flag && d2 > 0.0) {
                                    int n5 = n2 * 5;
                                    int n6 = 25 - n5;
                                    d2 = n6 * d2 / 25.0;
                                }

                                if (d2 > 0.0 && count > 0) {
                                    d2 = (25 - count) * d2 / 25.0;
                                }

                                n4 = MathHelper.ceiling_double_int(d2);
                                if (this.lookupMap.size() >= 1000) {
                                    this.lookupMap.clear();
                                }

                                this.lookupMap.put(entry, n4);
                            }

                            double d3 = Math.max(1.0, (double)mc.thePlayer.getHealth());
                            double d4 = n4 / d3 * 100.0;
                            if (this.showDamageSetting.isEnabled() && n4 > 0 && d4 > this.damageThresholdSetting.computeDouble()) {
                                float f4 = this.overrideHealthFormatSetting.isEnabled() ? computeFloat(n4 / 2.0F) : n4;
                                double d5 = n4 / d3;
                                String str = n4 >= d3 ? "§4" : (d5 >= 0.7 ? "§c" : (d5 >= 0.5 ? "§6" : (d5 >= 0.3 ? "§e" : "§a")));
                                this.dy = str + computeString(f4) + (this.overrideHealthFormatSetting.isEnabled() ? "§c❤§r" : "");
                                this.flag2 = true;
                            }

                            if (this.showDistanceSetting.isEnabled()) {
                                this.dz = computeString(computeFloat(f)) + "m";
                                this.count3 = this.computeColor(f).getRGB();
                                this.flag3 = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private double computeDouble(double d, double d2) {
        int n = (int)Math.floor(mc.thePlayer.posY);

        for (int i = n; i > -1; i--) {
            BlockPos pos = new BlockPos(Math.floor(d), (double)i, Math.floor(d2));
            Material material = mc.theWorld.getBlockState(pos).getBlock().getMaterial();
            if (material == Material.water) {
                return -1.0;
            }

            if (!material.isReplaceable()) {
                return i + 1;
            }
        }

        return -1.0;
    }

    private static String computeString(float f) {
        return f == (int)f ? String.valueOf((int)f) : String.valueOf(f);
    }

    private static float computeFloat(float f) {
        return Math.round(f * 10.0F) / 10.0F;
    }

    private void onEventRender2D(EventRender2D event) {
        if (GetCurrentScreenHelper.checkState2()) {
            if (mc.currentScreen == null) {
                if (this.flag2 || this.flag3) {
                    ScaledResolution scaledResolution = event.getScaledResolution();
                    if (this.flag2 && this.dy != null) {
                        computeFontRenderer(FallView.class)
                            .drawStringWithShadow(
                                this.dy,
                                scaledResolution.getScaledWidth() / 2.0F - computeFontRenderer(FallView.class).getStringWidth(this.dy) / 2.0F,
                                scaledResolution.getScaledHeight() / 2.0F - 15.0F,
                                Color.WHITE.getRGB()
                            );
                    }

                    if (this.flag3 && this.dz != null) {
                        computeFontRenderer(FallView.class)
                            .drawStringWithShadow(
                                this.dz,
                                scaledResolution.getScaledWidth() / 2.0F - computeFontRenderer(FallView.class).getStringWidth(this.dz) / 2.0F,
                                scaledResolution.getScaledHeight() / 2.0F + 6.0F,
                                this.count3
                            );
                    }
                }
            }
        }
    }

    public FallView() {
        super("Fall View", 0, Category.VISUAL);
        this.process18();
        this.handleSettingArray(
            new Setting[]{
                this.damageThresholdSetting,
                this.disableWhileFlyingSetting,
                this.onlyWhileSneakingSetting,
                this.overrideHealthFormatSetting,
                this.showDamageSetting,
                this.showDistanceSetting
            }
        );
    }

    private Color computeColor(float f) {
        float f2 = 2.5F;
        float f3 = 20.0F;
        float f4 = MathHelper.clamp_float((f - f2) / (f3 - f2), 0.0F, 1.0F);
        short s = 255;
        int n = (int)(255.0F * (1.0F - f4));
        return new Color(s, n, 0);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate && EventState.onEvent(event)) {
                this.process3();
            } else if (event instanceof EventRender2D) {
                this.onEventRender2D((EventRender2D)event);
            }
        }
    }

    private float computeFloat() {
        if (this.amount != -1.0 && this.amount2 != -1.0) {
            if (this.factor == 0.0F) {
                this.factor = (float)Math.max(0.0, this.amount - this.amount2);
            }

            return this.factor;
        }
        double d = this.computeDouble(mc.thePlayer.posX, mc.thePlayer.posZ);
        return d == -1.0 ? 0.0F : (float)Math.max(0.0, mc.thePlayer.posY - d);
    }

    private void process() {
        this.dy = null;
        this.flag2 = false;
        this.dz = null;
        this.count3 = Color.WHITE.getRGB();
        this.flag3 = false;
    }

    public static class Entry {
        public final int count;
        public final int count2;
        public final int count3;
        public final int count4;

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            return !(obj instanceof FallView.Entry entry)
                ? false
                : this.count == entry.count && this.count2 == entry.count2 && this.count3 == entry.count3 && this.count4 == entry.count4;
        }

        @Override
        public int hashCode() {
            int count = this.count;
            count = 31 * count + this.count2;
            count = 31 * count + this.count3;
            return 31 * count + this.count4;
        }

        public Entry(int count, float f, int n, int n2) {
            this.count = count;
            this.count2 = Math.round(f * 100.0F);
            this.count3 = n;
            this.count4 = n2;
        }
    }
}
