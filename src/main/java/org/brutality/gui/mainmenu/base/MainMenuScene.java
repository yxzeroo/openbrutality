package org.brutality.gui.mainmenu.base;

public abstract class MainMenuScene {
    protected int width2;
    protected int height2;

    public abstract void handleInt(int x, int y, int w, int h, float deltaTime);

    public abstract void handleInt(int width, int height, float deltaTime, int mouseX, int mouseY);

    public abstract void handleInt(int x, int y, int w, int h, String text, float hoverProgress, int index);

    public abstract String getDescription();

    public abstract String getName();

    public int getWidth() {
        return this.width2;
    }

    public int getHeight() {
        return this.height2;
    }

    public void dispose() {
    }

    public abstract void handleInt(int width, int height);
}
