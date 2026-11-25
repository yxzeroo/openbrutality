package org.brutality.module.impl.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.impl.visual.notifications.NotificationFeedHandler;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
import org.brutality.utils.DefaultHelper;
import org.lwjgl.input.Mouse;

public class MiddleClickFriends extends Module {
    private final NumberSetting rangeSetting = new NumberSetting("Range", this, 5.0, 1.0, 10.0, 1.0);
    private boolean flag = false;
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                if (this.mc.theWorld == null || this.mc.thePlayer == null) {
                    return;
                }

                if (Mouse.isButtonDown(2) && !this.flag) {
                    this.flag = true;
                    MovingObjectPosition objectMouseOver = this.mc.objectMouseOver;
                    if (objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectType.ENTITY && objectMouseOver.entityHit instanceof EntityPlayer player) {
                        double dist = this.mc.thePlayer.getDistanceToEntity(player);
                        if (dist <= this.rangeSetting.computeDouble()) {
                            String name = player.getName();
                            if (DefaultHelper.matchesString(name)) {
                                DefaultHelper.handleString(name);
                                NotificationFeedHandler.handleString("§8[§c-§8] §a" + name + " §fremoved from friends.");
                            } else {
                                DefaultHelper.handleString2(name);
                                NotificationFeedHandler.handleString("§8[§a+§8] §a" + name + " §fadded to friends.");
                            }
                        }
                    }
                }

                if (!Mouse.isButtonDown(2)) {
                    this.flag = false;
                }
            }
        }
    }

    public MiddleClickFriends() {
        super("MiddleClickFriends", 0, Category.PLAYER);
        this.handleSettingArray(new Setting[]{this.rangeSetting});
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
