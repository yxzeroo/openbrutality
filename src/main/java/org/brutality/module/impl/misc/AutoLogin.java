package org.brutality.module.impl.misc;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.StringUtils;
import org.brutality.event.Event;
import org.brutality.event.impl.chat.ChatEvent;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.settings.impl.StringSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.MessageUtils;
import org.brutality.utils.animations.ForwardsType;
import org.brutality.utils.animations.IsDoneHelper;
import org.brutality.utils.animations.impl.ImplAnimation3;

public class AutoLogin extends Module {
    private final NumberSetting delaySetting = new NumberSetting("Delay", this, 5000.0, 0.0, 5000.0, 1.0);
    private final StringSetting muhyavSetting = new StringSetting("Password", this, "muhyav");
    private final IsDoneHelper isDoneHelper = new ImplAnimation3(500, 1.0);
    private final String[] stringArray = new String[]{"password", "pass", "contrasena", "contraseña"};
    private long timestamp;
    private long timestamp2;
    private String text;

    public AutoLogin() {
        super("Auto Login", 0, Category.MISCELLANEOUS);
        this.handleSettingArray(new Setting[]{this.delaySetting, this.muhyavSetting});
    }

    private void handleString(String str) {
        long now = System.currentTimeMillis();
        this.isDoneHelper.computeIsDoneHelper(ForwardsType.FORWARDS);
        this.timestamp2 = now;
        this.timestamp = now + (long)this.delaySetting.computeDouble();
        this.text = str.trim();
    }

    private void reset() {
        this.isDoneHelper.computeIsDoneHelper(ForwardsType.BACKWARDS);
        this.timestamp2 = this.timestamp = 0L;
        this.text = null;
    }

    private int computeInt(String str) {
        int n = 0;
        String str2 = str.toLowerCase();

        for (String str3 : this.stringArray) {
            n += StringUtils.countMatches(str2, str3);
        }

        return n;
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof ChatEvent) {
                    String text = ((ChatEvent)event).message.getUnformattedText();
                    String val = this.muhyavSetting.getValue();
                    int n = this.computeInt(text);
                    if (n > 0) {
                        if (text.contains("/register ")) {
                            this.handleString("/register " + StringUtils.repeat(val + " ", n));
                        } else if (text.contains("/login ")) {
                            this.handleString("/login " + StringUtils.repeat(val + " ", n));
                        }
                    }
                }

                if (event instanceof EventPreMotion && this.timestamp < System.currentTimeMillis() && this.text != null) {
                    this.isDoneHelper.computeIsDoneHelper(ForwardsType.BACKWARDS);
                    MessageUtils.y(this.text);
                    this.reset();
                }

                if (event instanceof EventRender2D && (this.timestamp > System.currentTimeMillis() && this.text != null || !this.isDoneHelper.isDone())) {
                    ScaledResolution sr = new ScaledResolution(mc);
                    float f = 120.0F;
                    float f2 = 5.0F;
                    float f3 = f / 2.0F;
                    float f4 = this.timestamp == 0L
                        ? 1.0F
                        : (float)(System.currentTimeMillis() - this.timestamp2) / (float)(this.timestamp - this.timestamp2);
                    float f5 = (float)this.isDoneHelper.computeDouble();
                    float f6 = sr.getScaledWidth() / 2.0F / f5 - f3;
                    float f7 = sr.getScaledHeight() / 2.0F + 30.0F;
                    float f8 = f7 / f5 + f2;
                    float f9 = sr.getScaledWidth() / 2.0F / f5;
                    f7 /= f5;
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(f5, f5, f5);
                    Color color = new Color(0, 255, 255);
                    Gui.drawRect((int)f6, (int)f7, (int)(f9 + f3), (int)f8, color.darker().darker().getRGB());
                    Gui.drawRect((int)f6, (int)f7, (int)(f9 - f3 + f * f4), (int)f8, color.getRGB());
                    GlStateManager.popMatrix();
                }

                if (event instanceof RenderEvent4) {
                    this.reset();
                }
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.reset();
            super.onEnable();
        }
    }
}
