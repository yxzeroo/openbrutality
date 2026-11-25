package org.brutality.utils.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.IChatComponent;

public class UpdateAnimationsHelper {
    private static final List<UpdateAnimationsHelper.Entry> entries = new ArrayList<>();
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final int count = 550;
    private static boolean enabled = true;
    private static long timestamp = 0L;
    private static final long timestamp2 = 50L;
    private static final int count2 = 50;

    public static int computeInt(FontRenderer fontRenderer, String str, int n, int n2, int n3, IChatComponent component) {
        if (!enabled) {
            return fontRenderer.drawStringWithShadow(str, n, n2, n3);
        }
        UpdateAnimationsHelper.Entry2 entry2 = computeEntry2(component);
        if (entry2 != null && entry2.flag) {
            float f = n + entry2.offsetX;
            int n4 = computeInt(n3, entry2);
            return n4 >>> 24 < 4 ? n : fontRenderer.drawStringWithShadow(str, f, n2, n4);
        }
        return fontRenderer.drawStringWithShadow(str, n, n2, n3);
    }

    public static boolean isEnabled() {
        return enabled;
    }

    private static boolean matchesIChatComponent(IChatComponent component, IChatComponent component2) {
        if (component == component2) {
            return true;
        }
        if (component != null && component2 != null) {
            String text = component.getFormattedText();
            String text2 = component2.getFormattedText();
            if (text.equals(text2)) {
                return true;
            }
            String text3 = component.getUnformattedText();
            String text4 = component2.getUnformattedText();
            return text3.equals(text4);
        }
        return false;
    }

    public static int computeInt(int n, UpdateAnimationsHelper.Entry2 entry2) {
        if (entry2 != null && entry2.flag) {
            int n2 = n >> 24 & 0xFF;
            if (n2 == 0) {
                n2 = 255;
            }

            int n3 = n2 * entry2.count / 255;
            return n3 << 24 | (16777215 | ~n) - ~n;
        }
        return n;
    }

    public static UpdateAnimationsHelper.Entry2 computeEntry2(IChatComponent component) {
        if (enabled && mc.thePlayer != null && mc.theWorld != null) {
            long now = System.currentTimeMillis();
            synchronized (UpdateAnimationsHelper.class) {
                for (int i = entries.size() - 1; i >= 0; i--) {
                    UpdateAnimationsHelper.Entry entry = entries.get(i);
                    if (entry.iChatComponent == component) {
                        long l = now - entry.timestamp;
                        if (l <= 550L) {
                            return computeEntry2(l);
                        }
                    }
                }

                return null;
            }
        } else {
            return null;
        }
    }

    public static synchronized void updateAnimations() {
        if (enabled) {
            long now = System.currentTimeMillis();
            long l = 650L;
            Iterator it = entries.iterator();

            while (it.hasNext()) {
                UpdateAnimationsHelper.Entry entry = (UpdateAnimationsHelper.Entry)it.next();
                long l2 = now - entry.timestamp;
                if (l2 > l) {
                    it.remove();
                }
            }
        }
    }

    private static UpdateAnimationsHelper.Entry2 computeEntry2(long l) {
        UpdateAnimationsHelper.Entry2 entry2 = new UpdateAnimationsHelper.Entry2();
        entry2.offsetX = RenderHelper9.computeFloat2(l);
        entry2.count = (int)(255.0F * RenderHelper9.computeFloat(l));
        entry2.flag = l < 550L;
        return entry2;
    }

    public static synchronized void handleIChatComponent(IChatComponent component) {
        if (enabled && mc.thePlayer != null && mc.theWorld != null) {
            entries.add(new UpdateAnimationsHelper.Entry(component, timestamp, false));

            while (entries.size() > 50) {
                entries.remove(0);
            }
        }
    }

    public static synchronized void handleIChatComponent2(IChatComponent component) {
        if (enabled && mc.thePlayer != null && mc.theWorld != null) {
            long now = System.currentTimeMillis();
            String text = component.getFormattedText();

            for (UpdateAnimationsHelper.Entry entry : entries) {
                if (entry.flag && entry.iChatComponent == component) {
                    long l = now - entry.timestamp;
                    if (l < 100L) {
                        return;
                    }
                }
            }

            long l2 = now;
            if (now - timestamp < 50L) {
                l2 = timestamp;
            } else {
                timestamp = now;
            }

            entries.add(new UpdateAnimationsHelper.Entry(component, l2, true));

            while (entries.size() > 50) {
                entries.remove(0);
            }
        }
    }

    public static synchronized void handleIChatComponent3(IChatComponent component) {
        if (enabled && mc.thePlayer != null && mc.theWorld != null) {
            long now = System.currentTimeMillis();
            timestamp = now;
            entries.add(new UpdateAnimationsHelper.Entry(component, now, false));

            while (entries.size() > 50) {
                entries.remove(0);
            }
        }
    }

    public static synchronized void setEnabled(boolean enabled2) {
        enabled = enabled2;
        if (!enabled2) {
            entries.clear();
        }
    }

    public static class Entry {
        public final IChatComponent iChatComponent;
        public final long timestamp;
        public final boolean flag;

        public Entry(IChatComponent iChatComponent, long timestamp, boolean flag) {
            this.iChatComponent = iChatComponent;
            this.timestamp = timestamp;
            this.flag = flag;
        }
    }

    public static class Entry2 {
        public float offsetX = 0.0F;
        public int count;
        public boolean flag;
        private static final long timestamp = 106016367144075519L;

        public Entry2() {
            this.count = (int)timestamp;
            this.flag = false;
        }
    }
}
