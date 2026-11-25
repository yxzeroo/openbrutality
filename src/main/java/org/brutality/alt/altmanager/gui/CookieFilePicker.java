package org.brutality.alt.altmanager.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D.Float;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class CookieFilePicker {
    public static final Color color = new Color(24, 18, 6);
    public static final Color color2 = new Color(48, 34, 8);
    public static final Color color3 = new Color(255, 140, 0);
    public static final Color color4 = new Color(255, 210, 60);
    public static final Color color5 = new Color(196, 146, 44);
    public static JFrame jFrame;

    public static void open(Consumer<File> callback) {
        SwingUtilities.invokeLater(() -> {
            if (jFrame != null) {
                jFrame.setVisible(true);
                jFrame.toFront();
                jFrame.requestFocus();
            } else {
                jFrame = new JFrame("Cookie Login");
                jFrame.setDefaultCloseOperation(2);
                jFrame.setSize(420, 340);
                jFrame.setMinimumSize(new Dimension(320, 260));
                jFrame.setLocationRelativeTo(null);
                jFrame.setAlwaysOnTop(true);
                jFrame.setBackground(color);
                CookieFilePicker.Entry2 entry2 = new CookieFilePicker.Entry2();
                jFrame.setContentPane(entry2);
                jFrame.setDropTarget(new CookieFilePicker.Entry(entry2, callback));
                jFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        CookieFilePicker.jFrame = null;
                    }
                });
                jFrame.setVisible(true);
                jFrame.toFront();
            }
        });
    }

    public static void close() {
        SwingUtilities.invokeLater(() -> {
            if (jFrame != null) {
                jFrame.dispose();
            }
        });
    }

    private CookieFilePicker() {
    }

    public static final class Entry extends DropTarget {
        private final CookieFilePicker.Entry2 entry2;
        private final Consumer<File> consumer;

        @Override
        public void dragExit(DropTargetEvent dropTargetEvent) {
            super.dragExit(dropTargetEvent);
            this.entry2.handleFlag(false);
        }

        @Override
        public void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
            super.dragEnter(dropTargetDragEvent);
            this.entry2.handleFlag(true);
        }

        public Entry(CookieFilePicker.Entry2 entry2, Consumer<File> consumer) {
            this.entry2 = entry2;
            this.consumer = consumer;
        }

        @Override
        public void drop(DropTargetDropEvent dropTargetDropEvent) {
            this.entry2.handleFlag(false);

            try {
                dropTargetDropEvent.acceptDrop(1);

                for (File file : ((Iterable<File>)(Object)((List)dropTargetDropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)))) {
                    new Thread(() -> this.consumer.accept(file), "Cookie-Drop-Login").start();
                }

                dropTargetDropEvent.dropComplete(true);
                CookieFilePicker.close();
            } catch (Exception e) {
                dropTargetDropEvent.dropComplete(false);
            }
        }
    }

    public static final class Entry2 extends JPanel {
        private boolean hovered;

        public Entry2() {
            this.setBackground(CookieFilePicker.color);
        }

        public void handleFlag(boolean hovered) {
            if (this.hovered != hovered) {
                this.hovered = hovered;
                this.repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            int width = this.getWidth();
            int height = this.getHeight();
            g2d.setColor(CookieFilePicker.color);
            g2d.fillRect(0, 0, width, height);
            byte b = 18;
            Float val = new Float(b, b, width - b * 2.0F, height - b * 2.0F, 22.0F, 22.0F);
            g2d.setColor(this.hovered ? CookieFilePicker.color2.brighter() : CookieFilePicker.color2);
            g2d.fill(val);
            g2d.setColor(
                this.hovered
                    ? CookieFilePicker.color3
                    : new Color(CookieFilePicker.color3.getRed(), CookieFilePicker.color3.getGreen(), CookieFilePicker.color3.getBlue(), 90)
            );
            g2d.setStroke(new BasicStroke(this.hovered ? 2.0F : 1.4F, 1, 1, 1.0F, new float[]{7.0F, 6.0F}, 0.0F));
            g2d.draw(val);
            int n = height / 2 - 23;
            String str = "Cookie Login";
            g2d.setFont(new Font("SansSerif", 1, 24));
            g2d.setColor(CookieFilePicker.color4);
            g2d.drawString(str, (width - g2d.getFontMetrics().stringWidth(str)) / 2.0F, n + 30.0F);
            String str2 = "(Drag and drop your cookies file here)";
            g2d.setFont(new Font("SansSerif", 0, 14));
            g2d.setColor(this.hovered ? CookieFilePicker.color3 : CookieFilePicker.color5);
            g2d.drawString(str2, (width - g2d.getFontMetrics().stringWidth(str2)) / 2.0F, n + 52.0F);
            g2d.dispose();
        }
    }
}
