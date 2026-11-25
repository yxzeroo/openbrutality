package org.brutality.launcher;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;
import org.brutality.launcher.DashboardPanel.DarkSpinnerUI;

final class DashboardPanel extends JPanel {
    DashboardPanel(
        String str,
        JLabel label,
        JTextField field,
        JButton button,
        JButton button2,
        JCheckBox jCheckBox,
        JSpinner jSpinner,
        JButton button3,
        JButton button4,
        JLabel label2,
        JSpinner jSpinner2,
        JCheckBox jCheckBox2,
        JCheckBox jCheckBox3
    ) {
        super(new BorderLayout(26, 0));
        this.setBackground(LauncherTheme.BG_DARK);
        this.setBorder(new EmptyBorder(26, 24, 26, 28));
        CardLayout cardLayout = new CardLayout();
        JPanel panel = LauncherTheme.createPanel(cardLayout);
        JPanel panel2 = LauncherTheme.createVerticalPanel();
        panel2.setPreferredSize(new Dimension(168, 0));
        LauncherTheme.addWithSpacing(panel2, LauncherBranding.createLogoLabel(64), 14);
        JLabel label3 = LauncherTheme.createLabel("BRUTALITY", 20, LauncherTheme.TEXT_PRIMARY);
        label3.setFont(LauncherTheme.createFont(20, true));
        LauncherTheme.addWithSpacing(panel2, label3, 6);
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel("DESKTOP LAUNCHER", 10, LauncherTheme.TEXT_MUTED), 42);
        JButton button5 = LauncherTheme.createButton("Play", false);
        button5.putClientProperty("selected", true);
        button5.setEnabled(false);
        LauncherTheme.addWithSpacing(panel2, button5, 12);
        JButton button6 = LauncherTheme.createButton("Settings", false);
        LauncherTheme.addWithSpacing(panel2, button6, 24);
        button5.addActionListener(e -> {
            cardLayout.show(panel, "play");
            button5.putClientProperty("selected", true);
            button6.putClientProperty("selected", false);
            button5.setEnabled(false);
            button6.setEnabled(true);
        });
        button6.addActionListener(e2 -> {
            cardLayout.show(panel, "settings");
            button5.putClientProperty("selected", false);
            button6.putClientProperty("selected", true);
            button5.setEnabled(true);
            button6.setEnabled(false);
        });
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel("MINECRAFT JAVA", 10, LauncherTheme.TEXT_MUTED), 8);
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel("1.8.9  /  Release", 13, LauncherTheme.TEXT_PRIMARY), 20);
        panel2.add(Box.createVerticalGlue());
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel("BRUTALITY ACCOUNT", 10, LauncherTheme.TEXT_MUTED), 8);
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel(str, 14, LauncherTheme.TEXT_PRIMARY), 16);
        LauncherTheme.addWithSpacing(panel2, LauncherTheme.createLabel("Launcher 1.2.10", 10, LauncherTheme.TEXT_MUTED), 0);
        this.add(panel2, "West");
        JPanel panel3 = LauncherTheme.createPanel(new BorderLayout(0, 22));
        JPanel panel4 = LauncherTheme.createVerticalPanel();
        LauncherTheme.addWithSpacing(panel4, LauncherTheme.createLabel("Welcome back, " + str, 24, LauncherTheme.TEXT_PRIMARY), 7);
        LauncherTheme.addWithSpacing(panel4, LauncherTheme.createLabel("Your Minecraft account and client", 13, LauncherTheme.TEXT_MUTED), 0);
        panel3.add(panel4, "North");
        JPanel panel5 = LauncherTheme.createPanel(new BorderLayout(20, 0));
        JPanel panel6 = LauncherTheme.createCardPanel("BRUTALITY CLIENT", "<html>Minecraft<br>1.8.9</html>", "");
        JPanel panel7 = LauncherTheme.createVerticalPanel();
        button3.setPreferredSize(new Dimension(250, 54));
        button3.setMaximumSize(new Dimension(310, 54));
        LauncherTheme.addWithSpacing(panel7, button3, 12);
        LauncherTheme.addWithSpacing(panel7, LauncherTheme.createLabel("Updates are checked when you play.", 12, LauncherTheme.TEXT_MUTED), 0);
        panel6.add(panel7, "South");
        panel5.add(panel6, "Center");
        JPanel panel8 = LauncherTheme.createBorderedPanel(new BorderLayout(0, 18));
        panel8.setPreferredSize(new Dimension(274, 0));
        JPanel panel9 = LauncherTheme.createVerticalPanel();
        LauncherTheme.addWithSpacing(panel9, LauncherTheme.createLabel("MINECRAFT ACCOUNT", 10, LauncherTheme.TEXT_MUTED), 22);
        label.setPreferredSize(new Dimension(72, 72));
        LauncherTheme.addWithSpacing(panel9, label, 18);
        field.setOpaque(false);
        field.setForeground(LauncherTheme.TEXT_PRIMARY);
        field.setFont(LauncherTheme.createFont(16, true));
        field.setBorder(null);
        field.setMaximumSize(new Dimension(225, 26));
        LauncherTheme.addWithSpacing(panel9, field, 9);
        LauncherTheme.addWithSpacing(panel9, LauncherTheme.createLabel("Microsoft sign-in", 12, LauncherTheme.TEXT_MUTED), 0);
        panel8.add(panel9, "North");
        JPanel panel10 = LauncherTheme.createVerticalPanel();
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        LauncherTheme.addWithSpacing(panel10, button, 10);
        LauncherTheme.styleCheckBox(jCheckBox);
        LauncherTheme.addWithSpacing(panel10, jCheckBox, 15);
        LauncherTheme.addWithSpacing(panel10, button2, 0);
        panel8.add(panel10, "South");
        panel5.add(panel8, "East");
        panel.add(panel5, "play");
        JPanel panel11 = LauncherTheme.createBorderedPanel(new BorderLayout());
        JPanel panel12 = LauncherTheme.createVerticalPanel();
        LauncherTheme.addWithSpacing(panel12, LauncherTheme.createLabel("Settings", 24, LauncherTheme.TEXT_PRIMARY), 12);
        LauncherTheme.addWithSpacing(panel12, LauncherTheme.createLabel("Saved automatically for your next launch.", 13, LauncherTheme.TEXT_MUTED), 30);
        LauncherTheme.addWithSpacing(panel12, LauncherTheme.createLabel("MEMORY PER CLIENT (MB)", 10, LauncherTheme.TEXT_MUTED), 8);
        handleJSpinner(jSpinner);
        jSpinner.setMaximumSize(new Dimension(180, 36));
        LauncherTheme.addWithSpacing(panel12, jSpinner, 22);
        LauncherTheme.addWithSpacing(panel12, LauncherTheme.createLabel("CLIENT INSTANCES", 10, LauncherTheme.TEXT_MUTED), 8);
        handleJSpinner(jSpinner2, jCheckBox2.isSelected());
        jCheckBox2.addItemListener(item -> handleJSpinner(jSpinner2, jCheckBox2.isSelected()));
        handleJSpinner(jSpinner2);
        jSpinner2.setMaximumSize(new Dimension(180, 36));
        LauncherTheme.addWithSpacing(panel12, jSpinner2, 22);
        LauncherTheme.styleCheckBox(jCheckBox2);
        LauncherTheme.addWithSpacing(panel12, jCheckBox2, 8);
        LauncherTheme.addWithSpacing(
            panel12, LauncherTheme.createLabel("Allow multiple clients using the signed-in Microsoft account.", 12, LauncherTheme.TEXT_MUTED), 24
        );
        LauncherTheme.styleCheckBox(jCheckBox3);
        LauncherTheme.addWithSpacing(panel12, jCheckBox3, 8);
        LauncherTheme.addWithSpacing(
            panel12, LauncherTheme.createLabel("Show launcher activity and your current Pit server on Discord.", 12, LauncherTheme.TEXT_MUTED), 0
        );
        panel11.add(panel12, "North");
        panel.add(panel11, "settings");
        panel3.add(panel, "Center");
        JPanel panel13 = LauncherTheme.createBorderedPanel(new BorderLayout(20, 16));
        panel13.add(LauncherTheme.createLabel("Brutality  /  Minecraft 1.8.9", 12, LauncherTheme.TEXT_MUTED), "West");
        panel13.add(button4, "East");
        label2.setForeground(LauncherTheme.TEXT_MUTED);
        label2.setFont(LauncherTheme.createFont(11, false));
        panel13.add(label2, "South");
        panel3.add(panel13, "South");
        this.add(panel3, "Center");
    }

    DashboardPanel(
        String str,
        JLabel label,
        JTextField field,
        JButton button,
        JButton button2,
        JCheckBox jCheckBox,
        JSpinner jSpinner,
        JButton button3,
        JButton button4,
        JLabel label2,
        JSpinner jSpinner2,
        JCheckBox jCheckBox2
    ) {
        this(str, label, field, button, button2, jCheckBox, jSpinner, button3, button4, label2, jSpinner2, jCheckBox2, new JCheckBox("Discord IRC", true));
    }

    DashboardPanel(
        String str, JLabel label, JTextField field, JButton button, JButton button2, JCheckBox jCheckBox, JSpinner jSpinner, JButton button3, JButton button4, JLabel label2
    ) {
        this(str, label, field, button, button2, jCheckBox, jSpinner, button3, button4, label2, new JSpinner(new SpinnerNumberModel(1, 1, 20, 1)), new JCheckBox("AutoGrinder mode"));
    }

    private static void handleJSpinner(JSpinner jSpinner) {
        jSpinner.setUI(new DarkSpinnerUI());
        if (jSpinner.getEditor() instanceof DefaultEditor defaultEditor) {
            defaultEditor.getTextField().setBackground(LauncherTheme.BG_PANEL);
            defaultEditor.getTextField().setForeground(LauncherTheme.TEXT_PRIMARY);
            defaultEditor.getTextField().setFont(LauncherTheme.createFont(13, false));
            defaultEditor.getTextField().setBorder(new EmptyBorder(4, 8, 4, 8));
        }

        jSpinner.setBorder(BorderFactory.createLineBorder(LauncherTheme.BORDER_COLOR));
    }

    static void handleJSpinner(JSpinner jSpinner, boolean flag) {
        SpinnerNumberModel model = (SpinnerNumberModel)jSpinner.getModel();
        if (!flag) {
            jSpinner.setValue(1);
        }

        model.setMaximum(flag ? 20 : 1);
        jSpinner.setEnabled(flag);
    }

    static final class DarkSpinnerUI extends javax.swing.plaf.basic.BasicSpinnerUI {
    }
}
