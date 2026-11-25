package net.minecraft.client.gui;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.MenuButtonRenderer;
import org.brutality.gui.OceanComponent;
import org.brutality.gui.chat.ChatComponent;
import org.brutality.gui.font.CustomFontRenderer;
import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public int width = 200;
    public int height = 20;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;
    private float brutalityHover;
    private static final Map<Class<?>, Boolean> FLAT_STYLE = new ConcurrentHashMap<>();

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    protected int getHoverState(boolean mouseOver) {
        byte b = 1;
        if (!this.enabled) {
            b = 0;
        } else if (mouseOver) {
            b = 2;
        }

        return b;
    }

    private boolean usesFlatStyle() {
        return FLAT_STYLE.computeIfAbsent(
            this.getClass(),
            clazz -> {
                try {
                    return clazz.getMethod("mouseDragged", Minecraft.class, int.class, int.class).getDeclaringClass() == GuiButton.class
                        && clazz.getMethod("drawButton", Minecraft.class, int.class, int.class).getDeclaringClass() == GuiButton.class;
                } catch (SecurityException | NoSuchMethodException e) {
                    return false;
                }
            }
        );
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible && this.usesFlatStyle()) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.brutalityHover = this.brutalityHover + ((this.hovered && this.enabled ? 1.0F : 0.0F) - this.brutalityHover) * 0.25F;
            MenuButtonRenderer.handleInt(
                this.xPosition,
                this.yPosition,
                this.width,
                this.height,
                this.displayString,
                ChatComponent.computeDrawStringWithShadowComponent(),
                OceanComponent.computeColor().getRGB(),
                this.brutalityHover,
                this.enabled
            );
            this.mouseDragged(mc, mouseX, mouseY);
        } else {
            if (this.visible) {
                FontRenderer fontRenderer = mc.fontRendererObj;
                mc.getTextureManager().bindTexture(buttonTextures);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int hoverState = this.getHoverState(this.hovered);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 46 + hoverState * 20, this.width / 2, this.height);
                this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
                this.mouseDragged(mc, mouseX, mouseY);
                int n = 14737632;
                if (!this.enabled) {
                    n = 10526880;
                } else if (this.hovered) {
                    n = 16777120;
                }

                if (ChatComponent.matchesGuiScreen(mc.currentScreen)) {
                    CustomFontRenderer drawStringWithShadowComponent = ChatComponent.computeDrawStringWithShadowComponent();
                    int textWidth = drawStringWithShadowComponent.getStringWidth(this.displayString);
                    int n2 = this.xPosition + (this.width - textWidth) / 2;
                    int n3 = this.yPosition + (this.height - (int)drawStringWithShadowComponent.getFONT_HEIGHT()) / 2;
                    drawStringWithShadowComponent.drawStringWithShadow(this.displayString, n2, n3, n);
                } else {
                    this.drawCenteredString(fontRenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, n);
                }
            }
        }
    }

    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled
            && this.visible
            && mouseX >= this.xPosition
            && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
