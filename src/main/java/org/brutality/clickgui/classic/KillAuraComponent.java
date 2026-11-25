package org.brutality.clickgui.classic;

import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.brutality.gui.GuiComponent2;
import org.brutality.gui.chat.ChatComponent;

public class KillAuraComponent {
    private static final String[][] stringArray = new String[][]{
        {"Kill Aura", "Kill Aura"}, {"Chest Stealer", "Chest Stealer"}, {"Inventory Manager", "Inventory Manager"}
    };

    public static void handleGuiButton(GuiButton button) {
        if (button instanceof GuiComponent2 && button.id >= 9001 && button.id <= 9003) {
            ((GuiComponent2)button).process();
        }
    }

    public static void handleList(List<GuiButton> buttons, int n) {
        int n2 = 0;

        for (String[] parts : stringArray) {
            n2 = Math.max(n2, ChatComponent.computeDrawStringWithShadowComponent().getStringWidth(parts[1]) + 26);
        }

        for (GuiButton button : buttons) {
            if (button instanceof GuiComponent2 && button.id >= 9001 && button.id <= 9003) {
                button.width = n2;
                button.height = 16;
                button.xPosition = 5;
                button.yPosition = 5 + (button.id - 9001) * 20;
            }
        }
    }

    public static void handleGuiScreen(GuiScreen screen) {
        for (int i = 0; i < stringArray.length; i++) {
            screen.buttonList.add(new GuiComponent2(9001 + i, stringArray[i][0], stringArray[i][1]));
        }

        handleList(screen.buttonList, screen.width);
    }
}
