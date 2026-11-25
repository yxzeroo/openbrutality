package org.brutality.module.impl.visual.slinky;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import org.brutality.Brutality;
import org.brutality.gui.font.CustomFontRenderer;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.settings.impl.BooleanSetting;
import org.brutality.settings.impl.ModeSetting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.performance.InvalidateHelper;
import org.brutality.utils.render.ArialHelper;
import org.brutality.utils.render.InterfaceHelper;
import org.brutality.utils.render.ResetColorHelper;
import org.lwjgl.input.Mouse;

public final class UpdateRowsHandler {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final List<UpdateRowsHandler.Entry2> entries = new ArrayList<>();
    private final List<UpdateRowsHandler.Entry2> entries2 = new ArrayList<>();
    private final List<UpdateRowsHandler.Entry> entries3 = new ArrayList<>();
    private final Map<String, String> lookupMap = new HashMap<>();
    private final NumberSetting numberSetting;
    private final NumberSetting numberSetting2;
    private final NumberSetting numberSetting3;
    private final ModeSetting modeSetting;
    private final BooleanSetting booleanSetting;
    private final BooleanSetting booleanSetting2;
    private final BooleanSetting booleanSetting3;
    private final BooleanSetting booleanSetting4;
    private final BooleanSetting booleanSetting5;
    private final CustomFontRenderer drawStringWithShadowComponent = ArialHelper.computeDrawStringWithShadowComponent();
    private int count = 0;
    private int count2 = 1;
    private boolean dragging;
    private int count3;
    private int count4;
    private boolean flag;
    private int count5;
    private int count6 = -1;
    private String text = "white";
    private static final long timestamp = -339879677167398896L;

    private float computeFloat(UpdateRowsHandler.Entry2 entry2, long l) {
        if (this.booleanSetting.isEnabled() && entry2.timestamp != 0L) {
            float f = entry2.count < 0 ? 95.0F : 135.0F;
            float f2 = (float)(l - entry2.timestamp) / f;
            if (f2 >= 1.0F) {
                entry2.timestamp = 0L;
                return entry2.count < 0 ? 0.0F : 1.0F;
            }
            float f3 = Math.max(0.0F, f2);
            if (entry2.count < 0) {
                float f4 = 1.0F - f3;
                return f4 * f4;
            }
            float f5 = 1.0F - f3;
            f5 *= f5 * f5;
            return (1.0F - f5) * (1.0F - f5);
        }
        return 1.0F;
    }

    private void process2() {
        this.entries.clear();
        if (Brutality.moduleManager != null) {
            for (Module module : ModuleManager.getEntries2()) {
                UpdateRowsHandler.Entry2 entry2 = new UpdateRowsHandler.Entry2();
                entry2.name = module.getName();
                entry2.visible = false;
                entry2.count2 = this.drawStringWithShadowComponent.getStringWidth(module.getName());
                this.entries.add(entry2);
            }
        }
    }

    public void tick() {
        this.count5++;
        this.process();
        this.updateRows();
    }

    private void updateRows() {
        if (Brutality.moduleManager != null) {
            long now = System.currentTimeMillis();

            for (UpdateRowsHandler.Entry2 entry2 : this.entries) {
                Module module = Brutality.moduleManager.computeModule(entry2.name);
                boolean flag = module != null && module.checkState44() && !ModuleManager.matchesModule(module);
                if (!flag) {
                    if (entry2.visible) {
                        if (this.booleanSetting.isEnabled()) {
                            if (!entry2.flag) {
                                entry2.flag = true;
                                entry2.count = -1;
                                entry2.timestamp = now;
                            }
                        } else {
                            entry2.visible = false;
                        }
                    }
                } else {
                    if (!entry2.visible || entry2.flag) {
                        entry2.visible = true;
                        entry2.flag = false;
                        entry2.count = 1;
                        entry2.timestamp = this.booleanSetting.isEnabled() ? now : 0L;
                    }

                    String str = this.computeString(entry2.name);
                    entry2.count2 = this.drawStringWithShadowComponent.getStringWidth(str);
                }
            }

            this.entries.sort(Comparator.<UpdateRowsHandler.Entry2>comparingInt(entry22 -> entry22.count2).reversed());
        }
    }

    public void render() {
        if (this.mc.thePlayer != null) {
            this.entries2.clear();

            for (UpdateRowsHandler.Entry2 entry2 : this.entries) {
                if (entry2.visible || entry2.flag) {
                    this.entries2.add(entry2);
                }
            }

            boolean flag = this.mc.currentScreen instanceof GuiChat;
            if (!this.entries2.isEmpty() || flag) {
                long now = System.currentTimeMillis();
                float f = (float)this.numberSetting3.computeDouble();
                ScaledResolution sr = InvalidateHelper.computeScaledResolution(this.mc);
                int scaledWidth = sr.getScaledWidth();
                this.entries3.clear();
                float f2 = 0.0F;
                float f3 = 0.0F;
                long l = 0L;

                for (UpdateRowsHandler.Entry2 entry22 : this.entries2) {
                    String str = this.computeString(entry22.name);
                    float f4 = this.computeFloat(entry22, now);
                    if (entry22.flag && f4 <= 0.01F) {
                        entry22.visible = false;
                        entry22.flag = false;
                    } else {
                        float f5 = entry22.count2 * f;
                        f2 = Math.max(f2, f5);
                        float f6 = this.drawStringWithShadowComponent.getHeight() * f + f;
                        float f7 = this.booleanSetting.isEnabled() ? f4 : 1.0F;
                        f3 += f6 * f7;
                        UpdateRowsHandler.Entry entry = new UpdateRowsHandler.Entry();
                        entry.name = str;
                        entry.factor = f4;
                        entry.factor3 = f5;
                        entry.color = InterfaceHelper.computeInt(this.text, this.booleanSetting3.isEnabled() ? 0L : l, now);
                        entry.factor2 = f6;
                        this.entries3.add(entry);
                        l -= (long)(100.0F * f);
                    }
                }

                if (this.entries3.isEmpty() && flag) {
                    UpdateRowsHandler.Entry entry3 = new UpdateRowsHandler.Entry();
                    entry3.name = this.booleanSetting4.isEnabled() ? "ravenarray" : "RavenArray";
                    entry3.factor = 1.0F;
                    entry3.color = InterfaceHelper.computeInt(this.text, 0L, now);
                    entry3.factor2 = this.drawStringWithShadowComponent.getHeight() * f + f;
                    entry3.factor3 = this.drawStringWithShadowComponent.getStringWidth(entry3.name) * f;
                    f2 = entry3.factor3;
                    this.entries3.add(entry3);
                    f3 = entry3.factor2;
                }

                if (!this.entries3.isEmpty()) {
                    float f8 = Math.max(0.55F, f * 0.55F);
                    float f9 = this.booleanSetting2.isEnabled() ? 1.0F : 0.0F;
                    float f10 = f2 + f8 * 2.0F + f9;
                    int n = scaledWidth - (int)f10 - 2;
                    byte b = 2;
                    int n2 = n + this.count;
                    int n3 = b + this.count2;
                    if (flag) {
                        int n4 = Mouse.getX() / sr.getScaleFactor();
                        int n5 = sr.getScaledHeight() - Mouse.getY() / sr.getScaleFactor();
                        boolean buttonDown = Mouse.isButtonDown(0);
                        if (buttonDown && !this.flag && n4 >= n2 && n4 <= n2 + f10 && n5 >= n3 && n5 <= n3 + f3) {
                            this.dragging = true;
                            this.count3 = n4 - n2;
                            this.count4 = n5 - n3;
                        }

                        if (!buttonDown) {
                            this.dragging = false;
                        }

                        if (this.dragging) {
                            this.count = n4 - this.count3 - n;
                            this.count2 = n5 - this.count4 - b;
                            n2 = n + this.count;
                            n3 = b + this.count2;
                        }

                        this.flag = buttonDown;
                    } else {
                        this.dragging = false;
                        this.flag = false;
                    }

                    int n6 = (int)this.numberSetting.computeDouble() << 24;
                    int n7 = n6 | (int)timestamp;
                    float f11 = n3;
                    boolean flag2 = n2 + f10 / 2.0F >= scaledWidth / 2.0F;
                    float f12 = (float)this.numberSetting2.computeDouble() * f;
                    boolean flag3 = f12 > 1.5F;

                    for (UpdateRowsHandler.Entry entry4 : this.entries3) {
                        float f13 = this.booleanSetting.isEnabled() ? 1.0F - entry4.factor : 0.0F;
                        float f14 = flag2 ? f13 * 18.0F : -f13 * 18.0F;
                        float f15 = entry4.factor3;
                        float f16;
                        float f17;
                        float f18;
                        float f19;
                        if (flag2) {
                            f17 = n2 + f2 + f8 * 2.0F + f9;
                            f16 = f17 - f15 - f8 * 2.0F;
                            f18 = f17;
                            f19 = f16 + f8;
                        } else {
                            f18 = n2;
                            f16 = n2 + f9;
                            f17 = f16 + f15 + f8 * 2.0F;
                            f19 = f16 + f8;
                        }

                        float f20 = f16 + f14;
                        float f21 = f17 + f14;
                        float f22 = f18 + f14;
                        float f23 = f19 + f14;
                        if (n6 != 0) {
                            float f24 = f21 - f20;
                            float f25 = entry4.factor2;
                            if (flag3) {
                                ResetColorHelper.handleFloat2(f20, f11, f21, f11 + f25, Math.max(0.0F, f12), n7);
                            } else {
                                ResetColorHelper.handleFloat(f20, f11, f24, f25, n7);
                            }
                        }

                        if (this.booleanSetting2.isEnabled()) {
                            ResetColorHelper.handleFloat(f22, f11, f9, entry4.factor2, entry4.color);
                        }

                        int n8 = InterfaceHelper.computeInt(entry4.color, entry4.factor);
                        if (this.booleanSetting5.isEnabled()) {
                            this.drawStringWithShadowComponent.drawStringWithShadow(entry4.name, f23, f11, n8);
                        } else {
                            this.drawStringWithShadowComponent.computeInt3(entry4.name, f23, f11, n8);
                        }

                        f11 += entry4.factor2 * (this.booleanSetting.isEnabled() ? entry4.factor : 1.0F);
                    }
                }
            }
        }
    }

    public UpdateRowsHandler(
        NumberSetting numberSetting,
        NumberSetting numberSetting2,
        NumberSetting numberSetting3,
        ModeSetting modeSetting,
        BooleanSetting booleanSetting,
        BooleanSetting booleanSetting2,
        BooleanSetting booleanSetting3,
        BooleanSetting booleanSetting4,
        BooleanSetting booleanSetting5
    ) {
        this.numberSetting = numberSetting;
        this.numberSetting2 = numberSetting2;
        this.numberSetting3 = numberSetting3;
        this.modeSetting = modeSetting;
        this.booleanSetting = booleanSetting;
        this.booleanSetting2 = booleanSetting2;
        this.booleanSetting3 = booleanSetting3;
        this.booleanSetting4 = booleanSetting4;
        this.booleanSetting5 = booleanSetting5;
    }

    public void onEnable() {
        this.entries.clear();
        this.count5 = 0;
        this.count6 = -1;
        this.process2();
    }

    private String computeString(String str) {
        String str2 = this.lookupMap.get(str);
        Module module = Brutality.moduleManager.computeModule(str);
        if (module != null && module.isSuffix2() && module.getSuffix() != null) {
            str2 = module.getSuffix();
        }

        String str3 = str;
        if (str2 != null && !str2.isEmpty()) {
            str3 = str + " " + str2;
        }

        if (this.booleanSetting4.isEnabled()) {
            str3 = str3.toLowerCase();
        }

        return str3;
    }

    private void process() {
        int n = this.modeSetting.computeList2().indexOf(this.modeSetting.getText());
        if (n < 0) {
            n = 0;
        }

        if (n != this.count6) {
            this.count6 = n;
            this.text = InterfaceHelper.computeString2(n);
        }
    }

    public static class Entry {
        public String name;
        public float factor;
        public int color;
        public float factor2;
        public float factor3;
    }

    public static class Entry2 {
        public String name;
        public boolean visible;
        public boolean flag;
        public long timestamp;
        public int count = 1;
        public int count2;
    }
}
