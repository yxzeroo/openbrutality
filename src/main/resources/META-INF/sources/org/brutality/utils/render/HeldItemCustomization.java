/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Held-item placement behavior adapted from DulkirMod-Fabric's
 * ItemInHandRendererMixin and AnimationPreset (inglettronald and contributors).
 * https://github.com/inglettronald/DulkirMod-Fabric
 * Ported to Minecraft 1.8.9; swing timing is isolated from combat simulation.
 */
package org.brutality.utils.render;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public final class HeldItemCustomization {
    private int swingStart = Integer.MIN_VALUE;
    private int duration;
    private static int sharpDrawDepth;

    public static SharpDraw beginSharpDraw() { return new SharpDraw(); }
    public static boolean isSharpDraw() { return sharpDrawDepth > 0; }

    public static final class SharpDraw implements AutoCloseable {
        private SharpDraw() { sharpDrawDepth++; }
        @Override public void close() { sharpDrawDepth--; }
    }

    /** Atlas filtering does not cover OptiFine's separately bound item sprites. */
    public static final class SpriteFilter implements AutoCloseable {
        private final int min = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        private final int mag = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
        public SpriteFilter() {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        }
        @Override public void close() {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, min);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, mag);
        }
    }

    public static void apply(float x, float y, float z, float rotationX, float rotationY,
                             float rotationZ, float scaleX, float scaleY, float scaleZ) {
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(rotationX, 1, 0, 0);
        GlStateManager.rotate(rotationY, 0, 1, 0);
        GlStateManager.rotate(rotationZ, 0, 0, 1);
        GlStateManager.scale(scaleX, scaleY, scaleZ);
    }

    /** Repeated attacks do not restart a slow visual swing halfway through. */
    public void swing(int tick, int ticks) {
        if (swingStart != Integer.MIN_VALUE && tick >= swingStart && tick - swingStart < duration) return;
        swingStart = tick;
        duration = Math.max(1, ticks);
    }

    public float progress(int tick, float partialTicks) {
        if (swingStart == Integer.MIN_VALUE || tick < swingStart) return 0;
        float elapsed = tick - swingStart + Math.max(0, Math.min(1, partialTicks));
        return elapsed >= duration ? 0 : elapsed / duration;
    }

    public void reset() {
        swingStart = Integer.MIN_VALUE;
        duration = 0;
    }
}
