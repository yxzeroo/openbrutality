package org.brutality.utils.player;

import org.brutality.utils.mcp.McpApi3;

public class GetPitchHelper {
    public static float cameraYaw;
    public static float cameraPitch;
    public static boolean flag;

    public static float getPitch() {
        return flag ? cameraPitch : McpApi3.mc.thePlayer.rotationPitch;
    }

    public float getPrevYaw() {
        return flag ? cameraYaw : McpApi3.mc.thePlayer.prevRotationYaw;
    }

    public float computeFloat() {
        return flag ? cameraPitch : McpApi3.mc.thePlayer.prevRotationPitch;
    }

    public static void disable() {
        setFlag(false);
        cameraYaw = McpApi3.mc.thePlayer.rotationYaw;
        cameraPitch = McpApi3.mc.thePlayer.rotationPitch;
    }

    public static void setFlag(boolean flag2) {
        flag = flag2;
    }

    public static float getYaw() {
        return flag ? cameraYaw : McpApi3.mc.thePlayer.rotationYaw;
    }

    public static void handleFloat(float f, float f2) {
        cameraYaw += f * 0.15F;
        cameraPitch -= f2 * 0.15F;
        cameraPitch = Math.max(-90.0F, Math.min(90.0F, cameraPitch));
    }

    public static void enable() {
        setFlag(true);
        cameraYaw = McpApi3.mc.thePlayer.rotationYaw;
        cameraPitch = McpApi3.mc.thePlayer.rotationPitch;
    }
}
