package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.brutality.module.impl.pit.hypixel.swap.InventoryShortcuts;
import org.brutality.module.impl.pit.hypixel.utility.ChunkOfVileHelper;
import org.brutality.module.impl.pit.hypixel.utility.SwapMacros;
import org.brutality.utils.player.HotbarHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.render.LowLifeWarningHelper;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public abstract class GuiContainer extends GuiScreen {
    protected static final ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    protected int xSize = 176;
    protected int ySize = 166;
    public Container inventorySlots;
    protected int guiLeft;
    protected int guiTop;
    private Slot theSlot;
    private Slot clickedSlot;
    private boolean isRightMouseClick;
    private ItemStack draggedStack;
    private int touchUpX;
    private int touchUpY;
    private Slot returningStackDestSlot;
    private long returningStackTime;
    private ItemStack returningStack;
    private Slot currentDragTargetSlot;
    private long dragItemDropDelay;
    protected final Set<Slot> dragSplittingSlots = Sets.newHashSet();
    protected boolean dragSplitting;
    private int dragSplittingLimit;
    private int dragSplittingButton;
    private boolean ignoreMouseUp;
    private int dragSplittingRemnant;
    private long lastClickTime;
    private Slot lastClickSlot;
    private int lastClickButton;
    private boolean doubleClick;
    private ItemStack shiftClickedSlot;

    public GuiContainer(Container inventorySlotsIn) {
        this.inventorySlots = inventorySlotsIn;
        this.ignoreMouseUp = true;
    }

    @Override
    public void initGui() {
        super.initGui();
        mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)guiLeft, (float)guiTop, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        this.theSlot = null;
        int n = 240;
        short s = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, s / 1.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            this.drawSlot(slot);
            if (this.isMouseOverSlot(slot, mouseX, mouseY) && slot.canBeHovered()) {
                this.theSlot = slot;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                int xDisplayPosition = slot.xDisplayPosition;
                int yDisplayPosition = slot.yDisplayPosition;
                GlStateManager.colorMask(true, true, true, false);
                this.drawGradientRect(xDisplayPosition, yDisplayPosition, xDisplayPosition + 16, yDisplayPosition + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
        }

        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer inventory = mc.thePlayer.inventory;
        ItemStack stack = this.draggedStack == null ? inventory.getItemStack() : this.draggedStack;
        if (stack != null) {
            byte b = 8;
            int n2 = this.draggedStack == null ? 8 : 16;
            String str = null;
            if (this.draggedStack != null && this.isRightMouseClick) {
                stack = stack.copy();
                stack.stackSize = MathHelper.ceiling_float_int(stack.stackSize / 2.0F);
            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
                stack = stack.copy();
                stack.stackSize = this.dragSplittingRemnant;
                if (stack.stackSize == 0) {
                    str = EnumChatFormatting.YELLOW + "0";
                }
            }

            this.drawItemStack(stack, mouseX - guiLeft - b, mouseY - guiTop - n2, str);
        }

        if (this.returningStack != null) {
            float f = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0F;
            if (f >= 1.0F) {
                f = 1.0F;
                this.returningStack = null;
            }

            int n3 = this.returningStackDestSlot.xDisplayPosition - this.touchUpX;
            int n4 = this.returningStackDestSlot.yDisplayPosition - this.touchUpY;
            int n5 = this.touchUpX + (int)(n3 * f);
            int n6 = this.touchUpY + (int)(n4 * f);
            this.drawItemStack(this.returningStack, n5, n6, (String)null);
        }

        GlStateManager.popMatrix();
        ChunkOfVileHelper.handleGuiContainer(this, guiLeft, guiTop, this.xSize, this.width, this.height);
        if (inventory.getItemStack() == null && this.theSlot != null && this.theSlot.getHasStack()) {
            ItemStack stack2 = this.theSlot.getStack();
            this.renderToolTip(stack2, mouseX, mouseY);
        }

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    private void drawItemStack(ItemStack stack, int x, int y, String altText) {
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        this.itemRender.zLevel = 200.0F;
        this.itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, x, y - (this.draggedStack == null ? 0 : 8), altText);
        this.zLevel = 0.0F;
        this.itemRender.zLevel = 0.0F;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    protected abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

    private void drawSlot(Slot slotIn) {
        int xDisplayPosition = slotIn.xDisplayPosition;
        int yDisplayPosition = slotIn.yDisplayPosition;
        ItemStack stack = slotIn.getStack();
        boolean flag = false;
        boolean flag2 = slotIn == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack itemStack = mc.thePlayer.inventory.getItemStack();
        String str = null;
        if (slotIn == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && stack != null) {
            stack = stack.copy();
            stack.stackSize /= 2;
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slotIn) && itemStack != null) {
            if (this.dragSplittingSlots.size() == 1) {
                return;
            }

            if (Container.canAddItemToSlot(slotIn, itemStack, true) && this.inventorySlots.canDragIntoSlot(slotIn)) {
                stack = itemStack.copy();
                flag = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, stack, slotIn.getStack() == null ? 0 : slotIn.getStack().stackSize);
                if (stack.stackSize > stack.getMaxStackSize()) {
                    str = "" + EnumChatFormatting.YELLOW + stack.getMaxStackSize();
                    stack.stackSize = stack.getMaxStackSize();
                }

                if (stack.stackSize > slotIn.getItemStackLimit(stack)) {
                    str = "" + EnumChatFormatting.YELLOW + slotIn.getItemStackLimit(stack);
                    stack.stackSize = slotIn.getItemStackLimit(stack);
                }
            } else {
                this.dragSplittingSlots.remove(slotIn);
                this.updateDragSplitting();
            }
        }

        this.zLevel = 100.0F;
        this.itemRender.zLevel = 100.0F;
        if (stack == null) {
            String slotTexture = slotIn.getSlotTexture();
            if (slotTexture != null) {
                TextureAtlasSprite atlasSprite = mc.getTextureMapBlocks().getAtlasSprite(slotTexture);
                GlStateManager.disableLighting();
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                this.drawTexturedModalRect(xDisplayPosition, yDisplayPosition, atlasSprite, 16, 16);
                GlStateManager.enableLighting();
                flag2 = true;
            }
        }

        if (!flag2) {
            if (flag) {
                drawRect(xDisplayPosition, yDisplayPosition, xDisplayPosition + 16, yDisplayPosition + 16, -2130706433);
            }

            try {
                LowLifeWarningHelper.handleSlot(slotIn, xDisplayPosition, yDisplayPosition);
            } catch (Exception ignored) {
            }

            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(stack, xDisplayPosition, yDisplayPosition);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, stack, xDisplayPosition, yDisplayPosition, str);

            try {
                if (HotbarHelper.matchesSlot2(slotIn) && HotbarHelper.matchesSlot(slotIn)) {
                    HotbarHelper.handleInt(xDisplayPosition, yDisplayPosition);
                }
            } catch (Exception ignored2) {
            }
        }

        this.itemRender.zLevel = 0.0F;
        this.zLevel = 0.0F;
    }

    private void updateDragSplitting() {
        ItemStack itemStack = mc.thePlayer.inventory.getItemStack();
        if (itemStack != null && this.dragSplitting) {
            this.dragSplittingRemnant = itemStack.stackSize;

            for (Slot dragSplittingSlot : this.dragSplittingSlots) {
                ItemStack stack = itemStack.copy();
                int n = dragSplittingSlot.getStack() == null ? 0 : dragSplittingSlot.getStack().stackSize;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, stack, n);
                if (stack.stackSize > stack.getMaxStackSize()) {
                    stack.stackSize = stack.getMaxStackSize();
                }

                if (stack.stackSize > dragSplittingSlot.getItemStackLimit(stack)) {
                    stack.stackSize = dragSplittingSlot.getItemStackLimit(stack);
                }

                this.dragSplittingRemnant = this.dragSplittingRemnant - (stack.stackSize - n);
            }
        }
    }

    private Slot getSlotAtPosition(int x, int y) {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (this.isMouseOverSlot(slot, x, y)) {
                return slot;
            }
        }

        return null;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!StartHotbarMovesHelper.flag) {
            if (this.clickContainerControls(mouseX, mouseY, mouseButton)) {
                this.doubleClick = false;
                this.ignoreMouseUp = true;
            } else if (SwapMacros.matchesInt2(mouseButton - 100)) {
                this.ignoreMouseUp = true;
            } else if (InventoryShortcuts.matchesSlot(this.getSlotAtPosition(mouseX, mouseY), mouseButton, isCtrlKeyDown())) {
                this.ignoreMouseUp = true;
            } else if (mouseButton < 2 || mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100 || !this.checkHotbarMouseButtons(mouseButton)) {
                super.mouseClicked(mouseX, mouseY, mouseButton);
                boolean flag = mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
                Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
                long systemTime = Minecraft.getSystemTime();
                this.doubleClick = this.lastClickSlot == slotAtPosition && systemTime - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
                this.ignoreMouseUp = false;
                if (mouseButton == 0 || mouseButton == 1 || flag) {
                    int guiLeft = this.guiLeft;
                    int guiTop = this.guiTop;
                    boolean flag2 = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
                    int n = -1;
                    if (slotAtPosition != null) {
                        n = slotAtPosition.slotNumber;
                    }

                    if (flag2) {
                        n = -999;
                    }

                    if (mc.gameSettings.touchscreen && flag2 && mc.thePlayer.inventory.getItemStack() == null) {
                        mc.displayGuiScreen((GuiScreen)null);
                        return;
                    }

                    if (n != -1) {
                        if (mc.gameSettings.touchscreen) {
                            if (slotAtPosition != null && slotAtPosition.getHasStack()) {
                                this.clickedSlot = slotAtPosition;
                                this.draggedStack = null;
                                this.isRightMouseClick = mouseButton == 1;
                            } else {
                                this.clickedSlot = null;
                            }
                        } else if (!this.dragSplitting) {
                            if (mc.thePlayer.inventory.getItemStack() == null) {
                                if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                                    this.handleMouseClick(slotAtPosition, n, mouseButton, 3);
                                } else {
                                    boolean flag3 = n != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                                    byte b = 0;
                                    if (flag3) {
                                        this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
                                        b = 1;
                                    } else if (n == -999) {
                                        b = 4;
                                    }

                                    this.handleMouseClick(slotAtPosition, n, mouseButton, b);
                                }

                                this.ignoreMouseUp = true;
                            } else {
                                this.dragSplitting = true;
                                this.dragSplittingButton = mouseButton;
                                this.dragSplittingSlots.clear();
                                if (mouseButton == 0) {
                                    this.dragSplittingLimit = 0;
                                } else if (mouseButton == 1) {
                                    this.dragSplittingLimit = 1;
                                } else if (mouseButton == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                                    this.dragSplittingLimit = 2;
                                }
                            }
                        }
                    }
                }

                this.lastClickSlot = slotAtPosition;
                this.lastClickTime = systemTime;
                this.lastClickButton = mouseButton;
            }
        }
    }

    protected boolean clickContainerControls(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (!StartHotbarMovesHelper.flag) {
            Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
            ItemStack itemStack = mc.thePlayer.inventory.getItemStack();
            if (this.clickedSlot != null && mc.gameSettings.touchscreen) {
                if (clickedMouseButton == 0 || clickedMouseButton == 1) {
                    if (this.draggedStack == null) {
                        if (slotAtPosition != this.clickedSlot && this.clickedSlot.getStack() != null) {
                            this.draggedStack = this.clickedSlot.getStack().copy();
                        }
                    } else if (this.draggedStack.stackSize > 1 && slotAtPosition != null && Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false)) {
                        long systemTime = Minecraft.getSystemTime();
                        if (this.currentDragTargetSlot == slotAtPosition) {
                            if (systemTime - this.dragItemDropDelay > 500L) {
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.handleMouseClick(slotAtPosition, slotAtPosition.slotNumber, 1, 0);
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                                this.dragItemDropDelay = systemTime + 750L;
                                this.draggedStack.stackSize--;
                            }
                        } else {
                            this.currentDragTargetSlot = slotAtPosition;
                            this.dragItemDropDelay = systemTime;
                        }
                    }
                }
            } else if (this.dragSplitting
                && slotAtPosition != null
                && itemStack != null
                && itemStack.stackSize > this.dragSplittingSlots.size()
                && Container.canAddItemToSlot(slotAtPosition, itemStack, true)
                && slotAtPosition.isItemValid(itemStack)
                && this.inventorySlots.canDragIntoSlot(slotAtPosition)) {
                this.dragSplittingSlots.add(slotAtPosition);
                this.updateDragSplitting();
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (!StartHotbarMovesHelper.flag) {
            Slot slotAtPosition = this.getSlotAtPosition(mouseX, mouseY);
            int guiLeft = this.guiLeft;
            int guiTop = this.guiTop;
            boolean flag = mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
            int n = -1;
            if (slotAtPosition != null) {
                n = slotAtPosition.slotNumber;
            }

            if (flag) {
                n = -999;
            }

            if (this.doubleClick && slotAtPosition != null && state == 0 && this.inventorySlots.canMergeSlot((ItemStack)null, slotAtPosition)) {
                if (isShiftKeyDown()) {
                    if (slotAtPosition != null && slotAtPosition.inventory != null && this.shiftClickedSlot != null) {
                        for (Slot inventorySlot : this.inventorySlots.inventorySlots) {
                            if (inventorySlot != null
                                && inventorySlot.canTakeStack(mc.thePlayer)
                                && inventorySlot.getHasStack()
                                && inventorySlot.inventory == slotAtPosition.inventory
                                && Container.canAddItemToSlot(inventorySlot, this.shiftClickedSlot, true)) {
                                this.handleMouseClick(inventorySlot, inventorySlot.slotNumber, state, 1);
                            }
                        }
                    }
                } else {
                    this.handleMouseClick(slotAtPosition, n, state, 6);
                }

                this.doubleClick = false;
                this.lastClickTime = 0L;
            } else {
                if (this.dragSplitting && this.dragSplittingButton != state) {
                    this.dragSplitting = false;
                    this.dragSplittingSlots.clear();
                    this.ignoreMouseUp = true;
                    return;
                }

                if (this.ignoreMouseUp) {
                    this.ignoreMouseUp = false;
                    return;
                }

                if (this.clickedSlot != null && mc.gameSettings.touchscreen) {
                    if (state == 0 || state == 1) {
                        if (this.draggedStack == null && slotAtPosition != this.clickedSlot) {
                            this.draggedStack = this.clickedSlot.getStack();
                        }

                        boolean flag2 = Container.canAddItemToSlot(slotAtPosition, this.draggedStack, false);
                        if (n != -1 && this.draggedStack != null && flag2) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                            this.handleMouseClick(slotAtPosition, n, 0, 0);
                            if (mc.thePlayer.inventory.getItemStack() != null) {
                                this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, state, 0);
                                this.touchUpX = mouseX - guiLeft;
                                this.touchUpY = mouseY - guiTop;
                                this.returningStackDestSlot = this.clickedSlot;
                                this.returningStack = this.draggedStack;
                                this.returningStackTime = Minecraft.getSystemTime();
                            } else {
                                this.returningStack = null;
                            }
                        } else if (this.draggedStack != null) {
                            this.touchUpX = mouseX - guiLeft;
                            this.touchUpY = mouseY - guiTop;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        }

                        this.draggedStack = null;
                        this.clickedSlot = null;
                    }
                } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
                    this.handleMouseClick((Slot)null, -999, Container.func_94534_d(0, this.dragSplittingLimit), 5);

                    for (Slot dragSplittingSlot : this.dragSplittingSlots) {
                        this.handleMouseClick(dragSplittingSlot, dragSplittingSlot.slotNumber, Container.func_94534_d(1, this.dragSplittingLimit), 5);
                    }

                    this.handleMouseClick((Slot)null, -999, Container.func_94534_d(2, this.dragSplittingLimit), 5);
                } else if (mc.thePlayer.inventory.getItemStack() != null) {
                    if (state == mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                        this.handleMouseClick(slotAtPosition, n, state, 3);
                    } else {
                        boolean flag3 = n != -999 && (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
                        if (flag3) {
                            this.shiftClickedSlot = slotAtPosition != null && slotAtPosition.getHasStack() ? slotAtPosition.getStack() : null;
                        }

                        this.handleMouseClick(slotAtPosition, n, state, flag3 ? 1 : 0);
                    }
                }
            }

            if (mc.thePlayer.inventory.getItemStack() == null) {
                this.lastClickTime = 0L;
            }

            this.dragSplitting = false;
        }
    }

    private boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY) {
        return this.isPointInRegion(slotIn.xDisplayPosition, slotIn.yDisplayPosition, 16, 16, mouseX, mouseY);
    }

    protected boolean isPointInRegion(int left, int top, int right, int bottom, int pointX, int pointY) {
        int guiLeft = this.guiLeft;
        int guiTop = this.guiTop;
        pointX -= guiLeft;
        pointY -= guiTop;
        return pointX >= left - 1 && pointX < left + right + 1 && pointY >= top - 1 && pointY < top + bottom + 1;
    }

    protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
        if (slotIn != null && HotbarHelper.matchesSlot2(slotIn) && HotbarHelper.matchesSlot(slotIn)) {
            HotbarHelper.process2();
        } else {
            if (slotIn != null) {
                slotId = slotIn.slotNumber;
            }

            mc.playerController.windowClick(this.inventorySlots.windowId, slotId, clickedButton, clickType, mc.thePlayer);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (!SwapMacros.matchesInt2(keyCode)) {
            if (keyCode == mc.gameSettings.keyBindSlotLock.getKeyCode()) {
                HotbarHelper.handleGuiContainer(this, keyCode);
            } else {
                if (keyCode == 1 || keyCode == mc.gameSettings.keyBindInventory.getKeyCode()) {
                    mc.thePlayer.closeScreen();
                }

                this.checkHotbarKeys(keyCode);
                if (this.theSlot != null && this.theSlot.getHasStack()) {
                    if (keyCode == mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                        this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, 0, 3);
                    } else if (keyCode == mc.gameSettings.keyBindDrop.getKeyCode()) {
                        this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, 4);
                    }
                }
            }
        }
    }

    protected boolean checkHotbarKeys(int keyCode) {
        if (mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
            for (int i = 0; i < 9; i++) {
                if (keyCode == mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean checkHotbarMouseButtons(int mouseButton) {
        if (mc.thePlayer.inventory.getItemStack() == null && this.theSlot != null) {
            int n = -100 + mouseButton;

            for (int i = 0; i < 9; i++) {
                if (n == mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                    this.handleMouseClick(this.theSlot, this.theSlot.slotNumber, i, 2);
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void onGuiClosed() {
        if (mc.thePlayer != null) {
            this.inventorySlots.onContainerClosed(mc.thePlayer);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public Slot getTheSlot() {
        return this.theSlot;
    }

    public Slot getHoveredSlot() {
        int n = Mouse.getX() * this.width / mc.displayWidth;
        int n2 = this.height - Mouse.getY() * this.height / mc.displayHeight - 1;
        return this.getSlotAtPosition(n, n2);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (mc.gameSettings.keyBindSlotLock.getKeyCode() != 0 && mc.gameSettings.keyBindSlotLock.isPressed()) {
            HotbarHelper.handleGuiContainer(this, mc.gameSettings.keyBindSlotLock.getKeyCode());
        }

        if (!mc.thePlayer.isEntityAlive() || mc.thePlayer.isDead) {
            mc.thePlayer.closeScreen();
        }
    }
}
