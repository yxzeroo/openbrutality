package org.brutality.system;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.CompletableFuture;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;

public class AuthLoginDialog extends JFrame {
    public static final Color color = new Color(8, 8, 10);
    private static final Color color2 = new Color(14, 14, 17);
    public static final Color color3 = new Color(38, 38, 44);
    private static final Color color4 = new Color(120, 32, 36);
    public static final Color color5 = new Color(168, 28, 34);
    public static final Color color6 = new Color(110, 22, 26);
    public static final Color color7 = new Color(225, 225, 228);
    public static final Color color8 = new Color(118, 118, 126);
    public static final Color color9 = new Color(72, 72, 78);
    private static final Color color10 = new Color(96, 168, 128);
    private static final Color color11 = new Color(210, 88, 88);
    private static final int count = 40;
    private static final int count2 = 34;
    private static final int count3 = 14;
    private static final int count4 = 8;
    private static final int count5 = 56;
    private AuthLoginDialog.Entry entry;
    private JLabel jLabel;
    private JLabel jLabel2;
    private JLabel jLabel3;
    public JTextField jTextField;
    private JLabel jLabel4;
    private JTextField jTextField2;
    private JLabel jLabel5;
    public JPasswordField jPasswordField;
    private JButton jButton;
    public JCheckBox jCheckBox;
    private JButton jButton2;
    private JLabel jLabel6;
    private JLabel jLabel7;
    public BackendAuthService backendAuthService;
    private CompletableFuture<Boolean> completableFuture = new CompletableFuture<>();
    public boolean flag;

    public AuthLoginDialog() {
        this.process5();
        this.buildUi();
        this.process7();
        this.handleFlag2(false);

        try {
            this.backendAuthService = BackendAuthService.getInstance();
            this.process6();
        } catch (Exception e) {
            this.handleString("初始化失败", "Initialization failed");
        }
    }

    private void process5() {
        this.setTitle(" ");
        this.setDefaultCloseOperation(3);
        boolean flag = System.getProperty("os.name").toLowerCase().contains("mac");
        this.setUndecorated(!flag);
        this.setResizable(false);
        this.setSize(380, this.flag ? 430 : 480);
        if (!flag) {
            this.setBackground(new Color(0, 0, 0, 0));
        }

        this.setLocationRelativeTo(null);
    }

    private void buildUi() {
        this.entry = new AuthLoginDialog.Entry();
        this.entry.setLayout(null);
        this.setContentPane(this.entry);
        int width = this.getWidth();
        JButton button = this.computeJButton2("×");
        button.setBounds(width - 30, 8, 22, 22);
        button.addActionListener(e -> System.exit(0));
        this.entry.add(button);
        JLabel label = new JLabel("鉴权终端", 0);
        label.setFont(new Font("Microsoft YaHei UI", 0, 17));
        label.setForeground(color7);
        label.setBounds(0, 24, width, 24);
        this.entry.add(label);
        JLabel label2 = new JLabel("ACCESS TERMINAL", 0);
        label2.setFont(new Font("Consolas", 0, 10));
        label2.setForeground(color8);
        label2.setBounds(0, 46, width, 14);
        this.entry.add(label2);
        JLabel label3 = new JLabel("安全访问 · SECURE ACCESS", 0);
        label3.setFont(new Font("Consolas", 0, 9));
        label3.setForeground(color9);
        label3.setBounds(0, 60, width, 14);
        this.entry.add(label3);
        byte b = 88;
        this.jLabel = this.computeJLabel("Login", true);
        this.jLabel2 = this.computeJLabel("Register", false);
        this.jLabel.setBounds(width / 2 - 78, b, 78, 22);
        this.jLabel2.setBounds(width / 2, b, 78, 22);
        this.entry.add(this.jLabel);
        this.entry.add(this.jLabel2);
        this.jLabel3 = this.computeJLabel("KEY");
        this.jTextField = this.computeJTextField();
        this.jLabel4 = this.computeJLabel("USERNAME");
        this.jTextField2 = this.computeJTextField();
        this.jLabel5 = this.computeJLabel("PASSWORD");
        this.jPasswordField = this.computeJPasswordField();
        this.entry.add(this.jLabel3);
        this.entry.add(this.jTextField);
        this.entry.add(this.jLabel4);
        this.entry.add(this.jTextField2);
        this.entry.add(this.jLabel5);
        this.entry.add(this.jPasswordField);
        this.jButton = this.computeJButton2("Forgot password?");
        this.jButton.setForeground(color8);
        this.entry.add(this.jButton);
        this.jCheckBox = new JCheckBox("Remember me");
        this.jCheckBox.setOpaque(false);
        this.jCheckBox.setForeground(color8);
        this.jCheckBox.setFont(new Font("Segoe UI", 0, 10));
        this.jCheckBox.setFocusPainted(false);
        this.jCheckBox.setCursor(Cursor.getPredefinedCursor(12));
        this.entry.add(this.jCheckBox);
        this.jButton2 = this.computeJButton("Login");
        this.entry.add(this.jButton2);
        this.jLabel6 = new JLabel(" ", 0);
        this.jLabel6.setFont(new Font("Microsoft YaHei UI", 0, 12));
        this.jLabel6.setForeground(color8);
        this.entry.add(this.jLabel6);
        this.jLabel7 = new JLabel(" ", 0);
        this.jLabel7.setFont(new Font("Segoe UI", 0, 10));
        this.jLabel7.setForeground(color9);
        this.entry.add(this.jLabel7);
        this.handleInt(width);
        JLabel label4 = new JLabel("加密 · 本地会话", 0);
        label4.setFont(new Font("Microsoft YaHei UI", 0, 9));
        label4.setForeground(color9);
        label4.setBounds(0, this.getHeight() - 28, width, 12);
        this.entry.add(label4);
        JLabel label5 = new JLabel("encrypted · local session", 0);
        label5.setFont(new Font("Consolas", 0, 9));
        label5.setForeground(color9);
        label5.setBounds(0, this.getHeight() - 16, width, 12);
        this.entry.add(label5);
    }

    private void handleInt(int n) {
        int n2 = n - 80;
        int n3 = this.getHeight() - 58;
        this.jLabel6.setBounds(40, n3, n2, 18);
        this.jLabel7.setBounds(40, n3 + 18, n2, 14);
    }

    private void handleFlag2(boolean flag) {
        this.flag = flag;
        int width = this.getWidth();
        int n = width - 80;
        int n2 = 124;
        if (flag) {
            this.setSize(width, 430);
            this.jLabel3.setVisible(true);
            this.jTextField.setVisible(true);
            this.jLabel3.setBounds(40, n2, n, 14);
            this.jTextField.setBounds(40, n2 + 14 + 8, n, 34);
            n2 += 66;
        } else {
            this.setSize(width, 480);
            this.jLabel3.setVisible(false);
            this.jTextField.setVisible(false);
        }

        this.jLabel4.setBounds(40, n2, n, 14);
        this.jTextField2.setBounds(40, n2 + 14 + 8, n, 34);
        n2 += 62;
        this.jLabel5.setBounds(40, n2, n, 14);
        this.jPasswordField.setBounds(40, n2 + 14 + 8, n, 34);
        int n3 = n2 + 66;
        if (!flag) {
            this.jButton.setVisible(true);
            this.jButton.setBounds(40, n3 - 7, n, 20);
            n2 = n3 + 17;
            this.jCheckBox.setVisible(true);
            this.jCheckBox.setBounds(40, n2, n, 18);
            n3 = n2 + 28;
        } else {
            this.jButton.setVisible(false);
            this.jCheckBox.setVisible(false);
        }

        this.jButton2.setBounds(40, n3, n, 36);
        this.jButton2.setText(flag ? "Register" : "Login");
        this.handleInt(width);

        for (Component component : this.entry.getComponents()) {
            if (component instanceof JLabel) {
                String text = ((JLabel)component).getText();
                if (text != null && text.startsWith("encrypted")) {
                    component.setBounds(0, this.getHeight() - 16, width, 12);
                } else if (text != null && text.startsWith("加密")) {
                    component.setBounds(0, this.getHeight() - 28, width, 12);
                }
            }
        }

        this.handleJLabel(this.jLabel, !flag);
        this.handleJLabel(this.jLabel2, flag);
        this.setLocationRelativeTo(null);
        this.revalidate();
        this.repaint();
    }

    private void process7() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            private Point point;

            @Override
            public void mousePressed(MouseEvent e) {
                this.point = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e2) {
                if (this.point != null) {
                    Point locationOnScreen = e2.getLocationOnScreen();
                    locationOnScreen.translate(-this.point.x, -this.point.y);
                    AuthLoginDialog.this.setLocation(locationOnScreen);
                }
            }
        };
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        this.jButton2.addActionListener(e3 -> {
            if (this.flag) {
                this.doRegister();
            } else {
                this.process3();
            }
        });
        this.jButton.addActionListener(e4 -> this.process4());
        this.jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e5) {
                AuthLoginDialog.this.handleFlag3(false);
            }
        });
        this.jLabel2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e6) {
                AuthLoginDialog.this.handleFlag3(true);
            }
        });
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e7) {
                if (e7.getKeyCode() == 10) {
                    if (AuthLoginDialog.this.flag) {
                        AuthLoginDialog.this.doRegister();
                    } else {
                        AuthLoginDialog.this.process3();
                    }
                }
            }
        };
        KeyAdapter keyAdapter2 = new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e8) {
                AuthLoginDialog.this.process();
            }
        };

        for (JTextField field : new JTextField[]{this.jTextField, this.jTextField2, this.jPasswordField}) {
            field.addKeyListener(keyAdapter);
            field.addKeyListener(keyAdapter2);
        }
    }

    private void process4() {
        final JTextField field = this.computeJTextField();
        field.setText(this.jTextField2.getText().trim());
        final JTextField field2 = this.computeJTextField();
        JPasswordField passwordField = this.computeJPasswordField();
        JPasswordField passwordField2 = this.computeJPasswordField();
        JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
        panel.add(new JLabel("Username"));
        panel.add(field);
        panel.add(new JLabel("Key"));
        panel.add(field2);
        panel.add(new JLabel("New password"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm new password"));
        panel.add(passwordField2);
        if (JOptionPane.showConfirmDialog(this, panel, "Reset password", 2, -1) == 0) {
            final String str = new String(passwordField.getPassword());
            if (!str.equals(new String(passwordField2.getPassword()))) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Reset password", 0);
            } else {
                (new SwingWorker<BackendAuthService.Entry3, Void>() {
                    protected BackendAuthService.Entry3 doInBackground() {
                        return AuthLoginDialog.this.backendAuthService.computeEntry3(field.getText().trim(), field2.getText().trim(), str);
                    }

                    @Override
                    protected void done() {
                        try {
                            BackendAuthService.Entry3 entry3 = this.get();
                            JOptionPane.showMessageDialog(AuthLoginDialog.this, entry3.message, "Reset password", entry3.success ? 1 : 0);
                            if (entry3.success) {
                                AuthLoginDialog.this.jPasswordField.setText("");
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(AuthLoginDialog.this, "Password reset failed.", "Reset password", 0);
                        }
                    }
                }).execute();
            }
        }
    }

    public void handleFlag3(boolean flag) {
        if (this.flag != flag) {
            this.process();
            this.handleFlag2(flag);
        }
    }

    private JLabel computeJLabel(String str, boolean flag) {
        JLabel label = new JLabel(str, 0);
        label.setFont(new Font("Segoe UI", 0, 11));
        label.setForeground(flag ? color7 : color8);
        label.setCursor(Cursor.getPredefinedCursor(12));
        return label;
    }

    private void handleJLabel(JLabel label, boolean flag) {
        label.setForeground(flag ? color5 : color8);
        label.setFont(new Font("Segoe UI", flag ? 1 : 0, 11));
    }

    private JLabel computeJLabel(String str) {
        JLabel label = new JLabel(str);
        label.setFont(new Font("Consolas", 0, 10));
        label.setForeground(color8);
        return label;
    }

    private JTextField computeJTextField() {
        return new AuthLoginDialog.Entry2();
    }

    private JPasswordField computeJPasswordField() {
        return new AuthLoginDialog.Entry3();
    }

    private JButton computeJButton2(String str) {
        final JButton button = new JButton(str);
        button.setFont(new Font("Segoe UI", 0, 14));
        button.setForeground(color9);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(12));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(AuthLoginDialog.color7);
            }

            @Override
            public void mouseExited(MouseEvent e2) {
                button.setForeground(AuthLoginDialog.color9);
            }
        });
        return button;
    }

    private JButton computeJButton(String str) {
        JButton button = new JButton(str) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean rollover = this.getModel().isRollover();
                boolean pressed = this.getModel().isPressed();
                boolean enabled = this.isEnabled();
                Color color = !enabled ? new Color(36, 36, 40) : (pressed ? AuthLoginDialog.color6 : (rollover ? new Color(188, 38, 44) : AuthLoginDialog.color5));
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 4, 4);
                g2d.setColor(new Color(255, 255, 255, 18));
                g2d.drawLine(1, 1, this.getWidth() - 2, 1);
                g2d.setFont(this.getFont());
                g2d.setColor(enabled ? AuthLoginDialog.color7 : AuthLoginDialog.color8);
                FontMetrics fontMetrics = g2d.getFontMetrics();
                int n = (this.getWidth() - fontMetrics.stringWidth(this.getText())) / 2;
                int n2 = (this.getHeight() + fontMetrics.getAscent()) / 2 - 2;
                g2d.drawString(this.getText(), n, n2);
                g2d.dispose();
            }
        };
        button.setFont(new Font("Segoe UI", 0, 12));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(12));
        return button;
    }

    private void process6() {
        if (this.backendAuthService != null) {
            try {
                BackendAuthService.Entry2 entry2 = this.backendAuthService.computeEntry2();
                if (entry2 != null && entry2.username != null && entry2.password != null) {
                    SwingUtilities.invokeLater(() -> {
                        this.jTextField2.setText(entry2.username);
                        this.jPasswordField.setText(entry2.password);
                        this.jCheckBox.setSelected(true);
                    });
                }
            } catch (Exception ignored) {
            }
        }
    }

    public void process3() {
        if (this.backendAuthService == null) {
            this.handleString("系统不可用", "System unavailable");
        } else {
            final String str = this.jTextField2.getText().trim();
            final String str2 = new String(this.jPasswordField.getPassword()).trim();
            if (!str.isEmpty() && !str2.isEmpty()) {
                this.handleString2("验证中…", "Authenticating…");
                this.handleFlag(false);
                (new SwingWorker<BackendAuthService.Entry3, Void>() {
                    protected BackendAuthService.Entry3 doInBackground() {
                        return AuthLoginDialog.this.backendAuthService.computeEntry3(str, str2, AuthLoginDialog.this.jCheckBox.isSelected());
                    }

                    @Override
                    protected void done() {
                        try {
                            BackendAuthService.Entry3 entry3 = this.get();
                            if (entry3.success) {
                                AuthLoginDialog.this.backendAuthService.handleString3(str, entry3.sessionToken);
                                AuthLoginDialog.this.process2();
                            } else {
                                AuthLoginDialog.this.handleFlag(true);
                                AuthLoginDialog.this.handleString("验证失败", entry3.message != null ? entry3.message : "Authentication failed");
                                AuthLoginDialog.this.jPasswordField.setText("");
                            }
                        } catch (Exception e) {
                            AuthLoginDialog.this.handleFlag(true);
                            e.printStackTrace();
                            AuthLoginDialog.this.handleString("连接异常", "Connection error: " + e.toString());
                        }
                    }
                }).execute();
            } else {
                this.handleString("请输入用户名和密码", "Enter username and password");
            }
        }
    }

    public void doRegister() {
        if (this.backendAuthService == null) {
            this.handleString("系统不可用", "System unavailable");
        } else {
            final String str = this.jTextField.getText().trim();
            final String str2 = this.jTextField2.getText().trim();
            final String str3 = new String(this.jPasswordField.getPassword()).trim();
            if (!str.isEmpty() && !str2.isEmpty() && !str3.isEmpty()) {
                this.handleString2("注册中…", "Registering…");
                this.handleFlag(false);
                (new SwingWorker<BackendAuthService.Entry3, Void>() {
                    protected BackendAuthService.Entry3 doInBackground() {
                        return AuthLoginDialog.this.backendAuthService.computeEntry32(str, str2, str3);
                    }

                    @Override
                    protected void done() {
                        try {
                            BackendAuthService.Entry3 entry3 = this.get();
                            AuthLoginDialog.this.handleFlag(true);
                            if (entry3.success) {
                                AuthLoginDialog.this.handleString3("注册成功", "Registration successful");
                                AuthLoginDialog.this.jTextField.setText("");
                                AuthLoginDialog.this.jPasswordField.setText("");
                                Timer timer = new Timer(1200, e -> AuthLoginDialog.this.handleFlag3(false));
                                timer.setRepeats(false);
                                timer.start();
                            } else {
                                AuthLoginDialog.this.handleString("注册失败", entry3.message != null ? entry3.message : "Registration failed");
                                AuthLoginDialog.this.jTextField.setText("");
                                AuthLoginDialog.this.jPasswordField.setText("");
                            }
                        } catch (Exception e2) {
                            AuthLoginDialog.this.handleFlag(true);
                            e2.printStackTrace();
                            AuthLoginDialog.this.handleString("连接异常", "Connection error: " + e2.toString());
                        }
                    }
                }).execute();
            } else {
                this.handleString("请填写密钥、用户名和密码", "Enter key, username, and password");
            }
        }
    }

    public void process2() {
        this.jButton2.setEnabled(false);
        this.handleString3("登录成功", "Login successful");
        Timer timer = new Timer(700, e -> {
            this.completableFuture.complete(true);
            this.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void handleFlag(boolean flag) {
        this.jTextField.setEnabled(flag);
        this.jTextField2.setEnabled(flag);
        this.jPasswordField.setEnabled(flag);
        this.jButton2.setEnabled(flag);
        this.jCheckBox.setEnabled(flag);
        this.jLabel.setEnabled(flag);
        this.jLabel2.setEnabled(flag);
    }

    private void handleString2(String str, String str2) {
        SwingUtilities.invokeLater(() -> {
            this.jLabel6.setText(str);
            this.jLabel7.setText(str2);
            this.jLabel6.setForeground(color8);
            this.jLabel7.setForeground(color9);
        });
    }

    public void handleString3(String str, String str2) {
        SwingUtilities.invokeLater(() -> {
            this.jLabel6.setText(str);
            this.jLabel7.setText(str2);
            this.jLabel6.setForeground(color10);
            this.jLabel7.setForeground(new Color(color10.getRed(), color10.getGreen(), color10.getBlue(), 180));
        });
    }

    public void handleString(String str, String str2) {
        SwingUtilities.invokeLater(() -> {
            this.jLabel6.setText(str);
            this.jLabel7.setText(str2);
            this.jLabel6.setForeground(color11);
            this.jLabel7.setForeground(new Color(color11.getRed(), color11.getGreen(), color11.getBlue(), 200));
        });
    }

    public void process() {
        this.handleString2(" ", " ");
    }

    public CompletableFuture<Boolean> computeCompletableFuture() {
        this.setVisible(true);
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            this.toFront();
            this.requestFocus();
            this.requestFocusInWindow();
        }

        return this.completableFuture;
    }

    public static CompletableFuture<Boolean> authenticate() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                if (window instanceof AuthLoginDialog && window.isDisplayable() && window.isVisible()) {
                    window.dispose();
                }
            }
        });
        AuthLoginDialog[] authLoginDialogs = new AuthLoginDialog[1];

        try {
            SwingUtilities.invokeAndWait(() -> {
                authLoginDialogs[0] = new AuthLoginDialog();
                authLoginDialogs[0].computeCompletableFuture();
            });
        } catch (Exception e) {
            CompletableFuture future = new CompletableFuture();
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }

            future.completeExceptionally(e);
            return future;
        }

        return authLoginDialogs[0].completableFuture;
    }

    public void handleGraphics(Graphics g, int n, int n2, boolean flag) {
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(color2);
        g2d.fillRoundRect(0, 0, n, n2, 4, 4);
        g2d.setColor(flag ? color4 : color3);
        g2d.drawRoundRect(0, 0, n - 1, n2 - 1, 4, 4);
        g2d.dispose();
    }

    public class Entry extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D)g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int width = this.getWidth();
            int height = this.getHeight();
            g2d.setColor(AuthLoginDialog.color);
            g2d.fillRoundRect(0, 0, width, height, 6, 6);
            g2d.setColor(AuthLoginDialog.color3);
            g2d.drawRoundRect(0, 0, width - 1, height - 1, 6, 6);
            g2d.setColor(new Color(AuthLoginDialog.color5.getRed(), AuthLoginDialog.color5.getGreen(), AuthLoginDialog.color5.getBlue(), 40));
            g2d.drawLine(40, 114, width - 40, 114);
            g2d.dispose();
        }
    }

    public class Entry2 extends JTextField {
        public Entry2() {
            this.setFont(new Font("Segoe UI", 0, 13));
            this.setForeground(AuthLoginDialog.color7);
            this.setCaretColor(AuthLoginDialog.color7);
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            AuthLoginDialog.this.handleGraphics(g, this.getWidth(), this.getHeight(), this.isFocusOwner());
            super.paintComponent(g);
        }
    }

    public class Entry3 extends JPasswordField {
        public Entry3() {
            this.setFont(new Font("Segoe UI", 0, 13));
            this.setForeground(AuthLoginDialog.color7);
            this.setCaretColor(AuthLoginDialog.color7);
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
            this.setEchoChar('•');
        }

        @Override
        protected void paintComponent(Graphics g) {
            AuthLoginDialog.this.handleGraphics(g, this.getWidth(), this.getHeight(), this.isFocusOwner());
            super.paintComponent(g);
        }
    }
}
