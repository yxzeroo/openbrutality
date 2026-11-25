package org.brutality.notification.modern;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class ModernHandler2 {
    private static LinkedBlockingQueue<RenderAllHandler> linkedBlockingQueue = new LinkedBlockingQueue<>();
    private static List<RenderAllHandler> entries = new ArrayList<>();
    private static final int count = 5;
    private static boolean flag = false;

    public static void handleRenderAllHandler(RenderAllHandler renderAllHandler) {
        linkedBlockingQueue.add(renderAllHandler);
    }

    private static void process() {
        try {
            for (int i = 0; i < entries.size(); i++) {
                try {
                    RenderAllHandler renderAllHandler = entries.get(i);
                    if (renderAllHandler != null) {
                        int n = entries.size() - 1 - i;
                        int n2 = (renderAllHandler.getHeight() + 5) * n;
                        renderAllHandler.setYOffset(n2);
                    }
                } catch (IndexOutOfBoundsException e) {
                    break;
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void update() {
        try {
            Iterator it = entries.iterator();

            while (it.hasNext()) {
                RenderAllHandler renderAllHandler = (RenderAllHandler)it.next();
                if (!renderAllHandler.checkState()) {
                    it.remove();
                }
            }

            while (!linkedBlockingQueue.isEmpty()) {
                RenderAllHandler renderAllHandler2 = linkedBlockingQueue.poll();
                if (renderAllHandler2 != null) {
                    renderAllHandler2.process5();
                    entries.add(renderAllHandler2);
                }
            }

            process();
        } catch (Exception ignored) {
        }
    }

    public static void render() {
        try {
            update();
            if (!flag) {
                if (!entries.isEmpty()) {
                    try {
                        entries.get(0).render();
                    } catch (Exception ignored) {
                    }
                }

                flag = true;
            } else {
                RenderAllHandler.renderAll();
            }
        } catch (Exception ignored2) {
        }
    }
}
