package org.brutality.utils.render;

import com.sun.jna.Memory;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Locale;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public final class BorderlessWindow {
    private final BorderlessWindow.Listener listener;
    private Rectangle active;
    private String text;
    private boolean flag;

    public BorderlessWindow(BorderlessWindow.Listener listener) {
        this.listener = listener;
    }

    public void handleInt(int n, int n2) throws LWJGLException {
        if (!this.isActive()) {
            Rectangle rect = this.listener.computeRectangle(n, n2);
            Rectangle rect2 = this.listener.computeRectangle(rect);
            this.text = System.getProperty("org.lwjgl.opengl.Window.undecorated");
            this.flag = this.listener.checkState();
            this.active = rect;

            try {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                this.listener.handleFlag(false);
                this.listener.handleRectangle(rect2);
            } catch (RuntimeException | LWJGLException e) {
                try {
                    this.process();
                } catch (Exception e2) {
                    e.addSuppressed(e2);
                }

                throw e;
            }
        }
    }

    public BorderlessWindow() {
        this(new BorderlessWindow.Entry());
    }

    public boolean isActive() {
        return this.active != null;
    }

    public void process() throws LWJGLException {
        if (this.isActive()) {
            if (this.text == null) {
                System.clearProperty("org.lwjgl.opengl.Window.undecorated");
            } else {
                System.setProperty("org.lwjgl.opengl.Window.undecorated", this.text);
            }

            this.listener.handleFlag(this.flag);
            this.listener.handleRectangle(this.active);
            this.active = null;
        }
    }

    public static Rectangle computeRectangle(Rectangle rect) {
        if (System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows")) {
            try {
                NativeLibrary instance = NativeLibrary.getInstance("user32");
                Memory memory = new Memory(16L);
                memory.setInt(0L, rect.x);
                memory.setInt(4L, rect.y);
                memory.setInt(8L, rect.x + rect.width);
                memory.setInt(12L, rect.y + rect.height);
                Pointer pointer = instance.getFunction("MonitorFromRect", 63).invokePointer(new Object[]{memory, 2});
                Memory memory2 = new Memory(40L);
                memory2.clear();
                memory2.setInt(0L, 40);
                int n = instance.getFunction("GetMonitorInfoW", 63).invokeInt(new Object[]{pointer, memory2});
                if (n != 0) {
                    return new Rectangle(memory2.getInt(4L), memory2.getInt(8L), memory2.getInt(12L) - memory2.getInt(4L), memory2.getInt(16L) - memory2.getInt(8L));
                }
            } catch (LinkageError | RuntimeException e) {
                LogManager.getLogger(BorderlessWindow.class).warn("Native monitor lookup failed; using desktop bounds", (Throwable)e);
            }
        }

        Rectangle rect2 = null;
        long l = -1L;

        for (GraphicsDevice screenDevice : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
            Rectangle bounds = screenDevice.getDefaultConfiguration().getBounds();
            Rectangle rect3 = rect.intersection(bounds);
            long l2 = rect3.isEmpty() ? 0L : (long)rect3.width * rect3.height;
            if (l2 > l) {
                rect2 = bounds;
                l = l2;
            }
        }

        return rect2 == null ? new Rectangle(0, 0, Display.getDesktopDisplayMode().getWidth(), Display.getDesktopDisplayMode().getHeight()) : rect2;
    }

    public static final class Entry implements BorderlessWindow.Listener {
        @Override
        public void handleFlag(boolean flag) {
            Display.setResizable(flag);
        }

        @Override
        public Rectangle computeRectangle(Rectangle rect) {
            return BorderlessWindow.computeRectangle(rect);
        }

        @Override
        public void handleRectangle(Rectangle rect) throws LWJGLException {
            Display.setFullscreen(false);
            Display.setDisplayMode(new DisplayMode(rect.width, rect.height));
            Display.setLocation(rect.x, rect.y);
        }

        @Override
        public boolean checkState() {
            return !Display.isCreated() || Display.isResizable();
        }

        @Override
        public Rectangle computeRectangle(int n, int n2) {
            return Display.isCreated()
                ? new Rectangle(Display.getX(), Display.getY(), Math.max(1, Display.getWidth()), Math.max(1, Display.getHeight()))
                : new Rectangle(0, 0, Math.max(1, n), Math.max(1, n2));
        }
    }

    public interface Listener {
        Rectangle computeRectangle(Rectangle bounds);

        void handleRectangle(Rectangle bounds) throws LWJGLException;

        boolean checkState();

        void handleFlag(boolean value);

        Rectangle computeRectangle(int width, int height);
    }
}
