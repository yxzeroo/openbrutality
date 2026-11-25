package org.brutality.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.brutality.module.impl.pit.hypixel.automation.AutoGrinder;
import org.brutality.module.impl.pit.hypixel.automation.AutoHunt;
import org.brutality.utils.render.WrapDegreesHelper;

public final class PlayerHelper13 {
    private static final WrapDegreesHelper wrapDegreesHelper = new WrapDegreesHelper();
    private static Entity entity;
    private static World world;
    private static boolean active;

    public static float computeFloat2(Entity entity, float f) {
        return matchesEntity(entity) ? wrapDegreesHelper.getYaw() : f;
    }

    public static void reset() {
        active = false;
        entity = null;
        world = null;
        wrapDegreesHelper.reset();
    }

    public static void handleMinecraft(Minecraft mc) {
        Entity renderViewEntity = mc.getRenderViewEntity();
        if ((AutoHunt.checkState4() || AutoGrinder.checkState37())
            && mc.thePlayer != null
            && mc.theWorld != null
            && renderViewEntity == mc.thePlayer
            && !mc.thePlayer.isDead
            && !mc.thePlayer.isPlayerSleeping()
            && !mc.gameSettings.debugCamEnable
            && !GetPitchHelper.flag) {
            if (entity != renderViewEntity || world != mc.theWorld) {
                wrapDegreesHelper.reset();
            }

            entity = renderViewEntity;
            world = mc.theWorld;
            active = true;
            wrapDegreesHelper.handleFloat(renderViewEntity.rotationYaw, renderViewEntity.rotationPitch, System.nanoTime());
        } else {
            reset();
        }
    }

    public static boolean matchesEntity(Entity entity2) {
        return active && entity2 == entity;
    }

    private PlayerHelper13() {
    }

    public static float computeFloat(Entity entity, float f) {
        return matchesEntity(entity) ? wrapDegreesHelper.getPitch() : f;
    }
}
