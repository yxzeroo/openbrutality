package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.brutality.gui.MenuButtonRenderer;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlot {
    public final Minecraft mc;
    protected int width;
    protected int height;
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int slotHeight;
    private int scrollUpButtonID;
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;
    protected int initialClickY = -2;
    protected float scrollMultiplier;
    protected float amountScrolled;
    protected int selectedElement = -1;
    protected long lastClicked;
    protected boolean field_178041_q = true;
    protected boolean showSelectionBox = true;
    protected boolean hasListHeader;
    protected int headerPadding;
    private boolean enabled = true;

    public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        this.mc = mcIn;
        this.width = width;
        this.height = height;
        this.top = topIn;
        this.bottom = bottomIn;
        this.slotHeight = slotHeightIn;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn) {
        this.width = widthIn;
        this.height = heightIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = 0;
        this.right = widthIn;
    }

    public void setShowSelectionBox(boolean showSelectionBoxIn) {
        this.showSelectionBox = showSelectionBoxIn;
    }

    protected void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
        this.hasListHeader = hasListHeaderIn;
        this.headerPadding = headerPaddingIn;
        if (!hasListHeaderIn) {
            this.headerPadding = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY);

    protected abstract boolean isSelected(int slotIndex);

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
    }

    protected abstract void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn);

    protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
    }

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
    }

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
    }

    public int getSlotIndexFromScreenCoords(int p_148124_1_, int p_148124_2_) {
        int n = this.left + this.width / 2 - this.getListWidth() / 2;
        int n2 = this.left + this.width / 2 + this.getListWidth() / 2;
        int n3 = p_148124_2_ - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int n4 = n3 / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= n && p_148124_1_ <= n2 && n4 >= 0 && n3 >= 0 && n4 < this.getSize() ? n4 : -1;
    }

    public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
        this.scrollUpButtonID = scrollUpButtonIDIn;
        this.scrollDownButtonID = scrollDownButtonIDIn;
    }

    protected void bindAmountScrolled() {
        this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, this.func_148135_f());
    }

    public int func_148135_f() {
        return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
    }

    public int getAmountScrolled() {
        return (int)this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int p_148141_1_) {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
    }

    public void scrollBy(int amount) {
        this.amountScrolled += amount;
        this.bindAmountScrolled();
        this.initialClickY = -2;
    }

    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == this.scrollUpButtonID) {
                this.amountScrolled = this.amountScrolled - this.slotHeight * 2 / 3;
                this.initialClickY = -2;
                this.bindAmountScrolled();
            } else if (button.id == this.scrollDownButtonID) {
                this.amountScrolled = this.amountScrolled + this.slotHeight * 2 / 3;
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_) {
        if (this.field_178041_q) {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            int scrollBarX = this.getScrollBarX();
            int n = scrollBarX + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            this.drawContainerBackground(tessellator);
            int n2 = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int n3 = this.top + 4 - (int)this.amountScrolled;
            if (this.hasListHeader) {
                this.drawListHeader(n2, n3, tessellator);
            }

            this.drawSelectionBox(n2, n3, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            int n4 = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            GlStateManager.disableTexture2D();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldRenderer.func_181662_b(this.left, this.top + n4, 0.0).func_181673_a(0.0, 1.0).func_181669_b(0, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(this.right, this.top + n4, 0.0).func_181673_a(1.0, 1.0).func_181669_b(0, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(this.right, this.top, 0.0).func_181673_a(1.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            worldRenderer.func_181662_b(this.left, this.top, 0.0).func_181673_a(0.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            tessellator.draw();
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldRenderer.func_181662_b(this.left, this.bottom, 0.0).func_181673_a(0.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            worldRenderer.func_181662_b(this.right, this.bottom, 0.0).func_181673_a(1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
            worldRenderer.func_181662_b(this.right, this.bottom - n4, 0.0).func_181673_a(1.0, 0.0).func_181669_b(0, 0, 0, 0).func_181675_d();
            worldRenderer.func_181662_b(this.left, this.bottom - n4, 0.0).func_181673_a(0.0, 0.0).func_181669_b(0, 0, 0, 0).func_181675_d();
            tessellator.draw();
            int n5 = this.func_148135_f();
            if (n5 > 0) {
                int n6 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                int n7 = MathHelper.clamp_int(n6, 32, this.bottom - this.top - 8);
                int n8 = (int)this.amountScrolled * (this.bottom - this.top - n7) / n5 + this.top;
                if (n8 < this.top) {
                    n8 = this.top;
                }

                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                worldRenderer.func_181662_b(scrollBarX, this.bottom, 0.0).func_181673_a(0.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(n, this.bottom, 0.0).func_181673_a(1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(n, this.top, 0.0).func_181673_a(1.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                worldRenderer.func_181662_b(scrollBarX, this.top, 0.0).func_181673_a(0.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                tessellator.draw();
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                worldRenderer.func_181662_b(scrollBarX, n8 + n7, 0.0).func_181673_a(0.0, 1.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                worldRenderer.func_181662_b(n, n8 + n7, 0.0).func_181673_a(1.0, 1.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                worldRenderer.func_181662_b(n, n8, 0.0).func_181673_a(1.0, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                worldRenderer.func_181662_b(scrollBarX, n8, 0.0).func_181673_a(0.0, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                tessellator.draw();
                worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                worldRenderer.func_181662_b(scrollBarX, n8 + n7 - 1, 0.0).func_181673_a(0.0, 1.0).func_181669_b(192, 192, 192, 255).func_181675_d();
                worldRenderer.func_181662_b(n - 1, n8 + n7 - 1, 0.0).func_181673_a(1.0, 1.0).func_181669_b(192, 192, 192, 255).func_181675_d();
                worldRenderer.func_181662_b(n - 1, n8, 0.0).func_181673_a(1.0, 0.0).func_181669_b(192, 192, 192, 255).func_181675_d();
                worldRenderer.func_181662_b(scrollBarX, n8, 0.0).func_181673_a(0.0, 0.0).func_181669_b(192, 192, 192, 255).func_181675_d();
                tessellator.draw();
            }

            this.func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void handleMouseInput() {
        if (this.isMouseYWithinSlotBounds(this.mouseY)) {
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                int n = (this.width - this.getListWidth()) / 2;
                int n2 = (this.width + this.getListWidth()) / 2;
                int n3 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                int n4 = n3 / this.slotHeight;
                if (n4 < this.getSize() && this.mouseX >= n && this.mouseX <= n2 && n4 >= 0 && n3 >= 0) {
                    this.elementClicked(n4, false, this.mouseX, this.mouseY);
                    this.selectedElement = n4;
                } else if (this.mouseX >= n && this.mouseX <= n2 && n3 < 0) {
                    this.func_148132_a(this.mouseX - n, this.mouseY - this.top + (int)this.amountScrolled - 4);
                }
            }

            if (!Mouse.isButtonDown(0) || !this.getEnabled()) {
                this.initialClickY = -1;
            } else if (this.initialClickY == -1) {
                boolean flag = true;
                if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
                    int n5 = (this.width - this.getListWidth()) / 2;
                    int n6 = (this.width + this.getListWidth()) / 2;
                    int n7 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                    int n8 = n7 / this.slotHeight;
                    if (n8 < this.getSize() && this.mouseX >= n5 && this.mouseX <= n6 && n8 >= 0 && n7 >= 0) {
                        boolean flag2 = n8 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                        this.elementClicked(n8, flag2, this.mouseX, this.mouseY);
                        this.selectedElement = n8;
                        this.lastClicked = Minecraft.getSystemTime();
                    } else if (this.mouseX >= n5 && this.mouseX <= n6 && n7 < 0) {
                        this.func_148132_a(this.mouseX - n5, this.mouseY - this.top + (int)this.amountScrolled - 4);
                        flag = false;
                    }

                    int scrollBarX = this.getScrollBarX();
                    int n9 = scrollBarX + 6;
                    if (this.mouseX >= scrollBarX && this.mouseX <= n9) {
                        this.scrollMultiplier = -1.0F;
                        int n10 = this.func_148135_f();
                        if (n10 < 1) {
                            n10 = 1;
                        }

                        int n11 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / this.getContentHeight());
                        n11 = MathHelper.clamp_int(n11, 32, this.bottom - this.top - 8);
                        this.scrollMultiplier = this.scrollMultiplier / ((float)(this.bottom - this.top - n11) / n10);
                    } else {
                        this.scrollMultiplier = 1.0F;
                    }

                    if (flag) {
                        this.initialClickY = this.mouseY;
                    } else {
                        this.initialClickY = -2;
                    }
                } else {
                    this.initialClickY = -2;
                }
            } else if (this.initialClickY >= 0) {
                this.amountScrolled = this.amountScrolled - (this.mouseY - this.initialClickY) * this.scrollMultiplier;
                this.initialClickY = this.mouseY;
            }

            int eventDWheel = Mouse.getEventDWheel();
            if (eventDWheel != 0) {
                if (eventDWheel > 0) {
                    eventDWheel = -1;
                } else if (eventDWheel < 0) {
                    eventDWheel = 1;
                }

                this.amountScrolled = this.amountScrolled + eventDWheel * this.slotHeight / 2;
            }
        }
    }

    public void setEnabled(boolean enabledIn) {
        this.enabled = enabledIn;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public int getListWidth() {
        return 220;
    }

    protected boolean usesThemedStyle() {
        return this.mc.theWorld == null || this.mc.currentScreen instanceof GuiMultiplayer || this.mc.currentScreen instanceof GuiSelectWorld;
    }

    protected boolean drawsThemedRows() {
        return this.usesThemedStyle();
    }

    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn) {
        boolean flag = this.usesThemedStyle();
        boolean flag2 = this.drawsThemedRows();
        int size = this.getSize();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();

        for (int i = 0; i < size; i++) {
            int n = p_148120_2_ + i * this.slotHeight + this.headerPadding;
            int n2 = this.slotHeight - 4;
            if (n > this.bottom || n + n2 < this.top) {
                this.func_178040_a(i, p_148120_1_, n);
                if (flag) {
                    continue;
                }
            }

            if (!flag2) {
                if (this.showSelectionBox && this.isSelected(i)) {
                    int n3 = this.left + (this.width / 2 - this.getListWidth() / 2);
                    int n4 = this.left + this.width / 2 + this.getListWidth() / 2;
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    GlStateManager.disableTexture2D();
                    worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
                    worldRenderer.func_181662_b(n3, n + n2 + 2, 0.0).func_181673_a(0.0, 1.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                    worldRenderer.func_181662_b(n4, n + n2 + 2, 0.0).func_181673_a(1.0, 1.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                    worldRenderer.func_181662_b(n4, n - 2, 0.0).func_181673_a(1.0, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                    worldRenderer.func_181662_b(n3, n - 2, 0.0).func_181673_a(0.0, 0.0).func_181669_b(128, 128, 128, 255).func_181675_d();
                    worldRenderer.func_181662_b(n3 + 1, n + n2 + 1, 0.0).func_181673_a(0.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                    worldRenderer.func_181662_b(n4 - 1, n + n2 + 1, 0.0).func_181673_a(1.0, 1.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                    worldRenderer.func_181662_b(n4 - 1, n - 1, 0.0).func_181673_a(1.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                    worldRenderer.func_181662_b(n3 + 1, n - 1, 0.0).func_181673_a(0.0, 0.0).func_181669_b(0, 0, 0, 255).func_181675_d();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                }
            } else {
                int n5 = this.left + (this.width - this.getListWidth()) / 2;
                boolean flag3 = mouseXIn >= n5
                    && mouseXIn < n5 + this.getListWidth()
                    && mouseYIn >= Math.max(n, this.top)
                    && mouseYIn < Math.min(n + n2, this.bottom);
                MenuButtonRenderer.handleInt(n5, n - 1, this.getListWidth(), n2 + 2, flag3, this.isSelected(i), false);
            }

            if (!(this instanceof GuiResourcePackList) || n >= this.top - this.slotHeight && n <= this.bottom) {
                this.drawSlot(i, p_148120_1_, n, n2, mouseXIn, mouseYIn);
            }
        }
    }

    public int getScrollBarX() {
        return this.width / 2 + 124;
    }

    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        if (this.usesThemedStyle()) {
            Gui.drawRect(this.left, startY, this.right, endY, -284157141);
        } else {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldRenderer.func_181662_b(this.left, endY, 0.0).func_181673_a(0.0, endY / 32.0F).func_181669_b(64, 64, 64, endAlpha).func_181675_d();
            worldRenderer.func_181662_b(this.left + this.width, endY, 0.0)
                .func_181673_a(this.width / 32.0F, endY / 32.0F)
                .func_181669_b(64, 64, 64, endAlpha)
                .func_181675_d();
            worldRenderer.func_181662_b(this.left + this.width, startY, 0.0)
                .func_181673_a(this.width / 32.0F, startY / 32.0F)
                .func_181669_b(64, 64, 64, startAlpha)
                .func_181675_d();
            worldRenderer.func_181662_b(this.left, startY, 0.0).func_181673_a(0.0, startY / 32.0F).func_181669_b(64, 64, 64, startAlpha).func_181675_d();
            tessellator.draw();
        }
    }

    public void setSlotXBoundsFromLeft(int leftIn) {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight() {
        return this.slotHeight;
    }

    protected void drawContainerBackground(Tessellator p_drawContainerBackground_1_) {
        if (this.usesThemedStyle()) {
            Gui.drawRect(this.left, this.top, this.right, this.bottom, 1880103723);
        } else {
            WorldRenderer worldRenderer = p_drawContainerBackground_1_.getWorldRenderer();
            this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            float f = 32.0F;
            worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181709_i);
            worldRenderer.func_181662_b(this.left, this.bottom, 0.0)
                .func_181673_a(this.left / f, (this.bottom + (int)this.amountScrolled) / f)
                .func_181669_b(32, 32, 32, 255)
                .func_181675_d();
            worldRenderer.func_181662_b(this.right, this.bottom, 0.0)
                .func_181673_a(this.right / f, (this.bottom + (int)this.amountScrolled) / f)
                .func_181669_b(32, 32, 32, 255)
                .func_181675_d();
            worldRenderer.func_181662_b(this.right, this.top, 0.0)
                .func_181673_a(this.right / f, (this.top + (int)this.amountScrolled) / f)
                .func_181669_b(32, 32, 32, 255)
                .func_181675_d();
            worldRenderer.func_181662_b(this.left, this.top, 0.0)
                .func_181673_a(this.left / f, (this.top + (int)this.amountScrolled) / f)
                .func_181669_b(32, 32, 32, 255)
                .func_181675_d();
            p_drawContainerBackground_1_.draw();
        }
    }
}
