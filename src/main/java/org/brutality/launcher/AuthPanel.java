package org.brutality.launcher;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import org.brutality.launcher.AuthPanel.ClearSavedAuthWorker;
import org.brutality.launcher.AuthPanel.CredentialsDocListener;
import org.brutality.launcher.AuthPanel.LoadSavedAuthWorker;
import org.brutality.launcher.AuthPanel.SubmitAuthWorker;

final class AuthPanel extends JPanel {
    private final JTextField jTextField = new JTextField();
    private final JPasswordField b = new JPasswordField();
    private final JPasswordField jPasswordField = new JPasswordField();
    private final JTextArea jTextArea = new JTextArea("Sign in to your Brutality account to continue.");
    private final JButton jButton = LauncherTheme.createButton("Sign in", true);
    private final JPanel jPanel = LauncherTheme.createVerticalPanel();
    private final JCheckBox g = new JCheckBox("Remember me (fill in next time)");
    private int count;
    private String text = "login";
    private final Map<String, JButton> lookupMap = new HashMap<>();

    AuthPanel(Consumer<BrutalityAccount> callback) {
        super(new BorderLayout(0, 8));
        this.setBackground(LauncherTheme.BG_DARK);
        this.setBorder(new EmptyBorder(14, 24, 14, 24));
        this.jTextArea.setEditable(false);
        this.jTextArea.setOpaque(false);
        this.jTextArea.setLineWrap(true);
        this.jTextArea.setWrapStyleWord(true);
        this.jTextArea.setForeground(LauncherTheme.TEXT_MUTED);
        this.jTextArea.setFont(LauncherTheme.createFont(11, false));
        this.jTextArea.setPreferredSize(new Dimension(316, 32));
        this.jTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        JPanel panel = LauncherTheme.createVerticalPanel();
        JPanel panel2 = LauncherTheme.createPanel(new FlowLayout(1, 10, 0));
        panel2.add(LauncherBranding.createLogoLabel(36));
        panel2.add(LauncherTheme.createLabel("BRUTALITY", 18, LauncherTheme.TEXT_PRIMARY));
        LauncherTheme.addWithSpacing(panel, panel2, 10);
        JPanel panel3 = LauncherTheme.createPanel(new GridLayout(1, 3, 6, 0));

        for (String[] parts : new String[][]{{"Login", "login"}, {"Register", "register"}, {"Reset password", "reset-password"}}) {
            JButton button = LauncherTheme.createButton(parts[0], false);
            this.lookupMap.put(parts[1], button);
            button.addActionListener(e -> {
                if (this.jButton.isEnabled()) {
                    this.count++;
                    this.text = parts[1];
                    this.b.setText("");
                    this.jPasswordField.setText("");
                    this.b();
                }
            });
            panel3.add(button);
            button.setFont(LauncherTheme.createFont(11, false));
            button.setBorder(new EmptyBorder(8, 4, 8, 4));
        }

        LauncherTheme.addWithSpacing(panel, panel3, 0);
        this.add(panel, "North");

        for (JTextField field : new JTextField[]{this.jTextField, this.b, this.jPasswordField}) {
            LauncherTheme.styleTextField(field);
            field.setFont(LauncherTheme.createFont(12, false));
            field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(LauncherTheme.BORDER_COLOR), new EmptyBorder(6, 9, 6, 9)));
            field.setPreferredSize(new Dimension(316, 32));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        }

        this.g.setOpaque(false);
        this.g.setForeground(LauncherTheme.TEXT_MUTED);
        this.g.setFont(LauncherTheme.createFont(12, false));
        CredentialsDocListener credentialsDocListener = new CredentialsDocListener(this);
        this.jTextField.getDocument().addDocumentListener(credentialsDocListener);
        this.b.getDocument().addDocumentListener(credentialsDocListener);
        this.b();
        this.add(this.jPanel, "Center");
        JPanel panel4 = LauncherTheme.createVerticalPanel();
        this.jButton.setBorder(new EmptyBorder(9, 18, 9, 18));
        this.jButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        LauncherTheme.addWithSpacing(panel4, this.jButton, 8);
        LauncherTheme.addWithSpacing(panel4, this.jTextArea, 0);
        this.add(panel4, "South");
        this.jButton.setEnabled(false);
        this.g.setEnabled(false);
        new LoadSavedAuthWorker(this).execute();
        this.g.addActionListener(e2 -> {
            if (!this.g.isSelected()) {
                this.g.setEnabled(false);
                this.jButton.setEnabled(false);
                new ClearSavedAuthWorker(this).execute();
            }
        });
        this.jButton.addActionListener(e3 -> {
            String str = this.jTextField.getText().trim();
            char[] password = this.b.getPassword();
            char[] password2 = this.jPasswordField.getPassword();
            if (this.text.equals("register") && !Arrays.equals(password, password2)) {
                this.jTextArea.setText("Passwords do not match.");
                Arrays.fill(password, '\u0000');
                Arrays.fill(password2, '\u0000');
            } else {
                String str2 = new String(password);
                Arrays.fill(password, '\u0000');
                Arrays.fill(password2, '\u0000');
                boolean selected = this.g.isSelected();
                this.b.setText("");
                this.jPasswordField.setText("");
                this.jButton.setEnabled(false);
                this.g.setEnabled(false);
                this.jTextArea.setText("Checking your account…");
                String text = this.text;
                new SubmitAuthWorker(this, text, str, str2, selected, callback).execute();
            }
        });
    }

    JButton getJButton() {
        return this.jButton;
    }

    private void handleString(String str, JTextField field) {
        LauncherTheme.addWithSpacing(this.jPanel, LauncherTheme.createLabel(str, 11, LauncherTheme.TEXT_MUTED), 3);
        LauncherTheme.addWithSpacing(this.jPanel, field, 5);
    }

    private void b() {
        this.lookupMap.forEach((str, button) -> {
            button.putClientProperty("selected", str.equals(this.text));
            button.repaint();
        });
        this.jPanel.removeAll();
        this.handleString("Username", this.jTextField);
        this.handleString(this.text.equals("reset-password") ? "New password" : "Password", this.b);
        if (this.text.equals("register")) {
            this.handleString("Confirm password", this.jPasswordField);
        }

        if (!this.text.equals("reset-password")) {
            LauncherTheme.addWithSpacing(this.jPanel, this.g, 0);
        }

        this.jButton.setText(this.text.equals("register") ? "Create account" : (this.text.equals("reset-password") ? "Reset password" : "Sign in"));
        this.jPanel.revalidate();
        this.jPanel.repaint();
    }

    static final class CredentialsDocListener implements javax.swing.event.DocumentListener {
        CredentialsDocListener(AuthPanel panel) {
        }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) {}
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) {}
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
    }

    static final class LoadSavedAuthWorker extends javax.swing.SwingWorker<Void, Void> {
        LoadSavedAuthWorker(AuthPanel panel) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class ClearSavedAuthWorker extends javax.swing.SwingWorker<Void, Void> {
        ClearSavedAuthWorker(AuthPanel panel) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }

    static final class SubmitAuthWorker extends javax.swing.SwingWorker<Void, Void> {
        SubmitAuthWorker(AuthPanel panel, String mode, String user, String pass, boolean remember, Consumer<BrutalityAccount> cb) {}
        @Override protected Void doInBackground() { throw new UnsupportedOperationException("missing-from-dump"); }
    }
}
