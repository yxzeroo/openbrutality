package net.minecraft.client.resources;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.brutality.gui.chat.ChatComponent;

public abstract class ResourcePackListEntry implements IGuiListEntry {
    private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation("textures/gui/resource_packs.png");
    private static final IChatComponent field_183020_d = new ChatComponentTranslation("resourcePack.incompatible");
    private static final IChatComponent field_183021_e = new ChatComponentTranslation("resourcePack.incompatible.old");
    private static final IChatComponent field_183022_f = new ChatComponentTranslation("resourcePack.incompatible.new");
    protected final Minecraft mc;
    protected final GuiScreenResourcePacks resourcePacksGUI;

    public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
        this.resourcePacksGUI = resourcePacksGUIIn;
        this.mc = Minecraft.getMinecraft();
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        int n = this.func_183019_a();
        if (n != 1) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Gui.drawRect(x - 1, y - 1, x + listWidth - 9, y + slotHeight + 1, -8978432);
        }

        this.func_148313_c();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        String str = this.func_148312_b();
        String str2 = this.func_148311_a();
        if ((this.mc.gameSettings.touchscreen || isSelected) && this.func_148310_d()) {
            this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
            Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int n2 = mouseX - x;
            int n3 = mouseY - y;
            if (n < 1) {
                str = field_183020_d.getFormattedText();
                str2 = field_183021_e.getFormattedText();
            } else if (n > 1) {
                str = field_183020_d.getFormattedText();
                str2 = field_183022_f.getFormattedText();
            }

            if (this.func_148309_e()) {
                if (n2 < 32) {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            } else {
                if (this.func_148308_f()) {
                    if (n2 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 32.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148314_g()) {
                    if (n2 < 32 && n2 > 16 && n3 < 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }

                if (this.func_148307_h()) {
                    if (n2 < 32 && n2 > 16 && n3 > 16) {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                    } else {
                        Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                    }
                }
            }
        }

        FontRenderer fontRenderer = ChatComponent.computeFontRenderer(this.mc);
        int textWidth = fontRenderer.getStringWidth(str);
        if (textWidth > 157) {
            str = fontRenderer.trimStringToWidth(str, 157 - fontRenderer.getStringWidth("...")) + "...";
        }

        fontRenderer.drawStringWithShadow(str, x + 32 + 2, y + 1, 16777215);
        List list = fontRenderer.listFormattedStringToWidth(str2, 157);

        for (int i = 0; i < 2 && i < list.size(); i++) {
            fontRenderer.drawStringWithShadow((String)list.get(i), x + 32 + 2, y + 12 + 10 * i, 8421504);
        }
    }

    protected abstract int func_183019_a();

    protected abstract String func_148311_a();

    protected abstract String func_148312_b();

    protected abstract void func_148313_c();

    protected boolean func_148310_d() {
        return true;
    }

    protected boolean func_148309_e() {
        return !this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148308_f() {
        return this.resourcePacksGUI.hasResourcePackEntry(this);
    }

    protected boolean func_148314_g() {
        List listContaining = this.resourcePacksGUI.getListContaining(this);
        int n = listContaining.indexOf(this);
        return n > 0 && ((ResourcePackListEntry)listContaining.get(n - 1)).func_148310_d();
    }

    protected boolean func_148307_h() {
        List listContaining = this.resourcePacksGUI.getListContaining(this);
        int n = listContaining.indexOf(this);
        return n >= 0 && n < listContaining.size() - 1 && ((ResourcePackListEntry)listContaining.get(n + 1)).func_148310_d();
    }

    @Override
    public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
        if (this.func_148310_d() && p_148278_5_ <= 32) {
            if (this.func_148309_e()) {
                this.resourcePacksGUI.markChanged();
                int n = this.func_183019_a();
                if (n != 1) {
                    String str = I18n.format("resourcePack.incompatible.confirm.title");
                    String str2 = I18n.format("resourcePack.incompatible.confirm." + (n > 1 ? "new" : "old"));
                    this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                        @Override
                        public void confirmClicked(boolean result, int id) {
                            List listContaining = ResourcePackListEntry.this.resourcePacksGUI.getListContaining(ResourcePackListEntry.this);
                            ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
                            if (result) {
                                listContaining.remove(ResourcePackListEntry.this);
                                ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0, ResourcePackListEntry.this);
                            }
                        }
                    }, str, str2, 0));
                } else {
                    this.resourcePacksGUI.getListContaining(this).remove(this);
                    this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
                }

                return true;
            }

            if (p_148278_5_ < 16 && this.func_148308_f()) {
                this.resourcePacksGUI.getListContaining(this).remove(this);
                this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }

            if (p_148278_5_ > 16 && p_148278_6_ < 16 && this.func_148314_g()) {
                List listContaining2 = this.resourcePacksGUI.getListContaining(this);
                int n2 = listContaining2.indexOf(this);
                listContaining2.remove(this);
                listContaining2.add(n2 - 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }

            if (p_148278_5_ > 16 && p_148278_6_ > 16 && this.func_148307_h()) {
                List listContaining3 = this.resourcePacksGUI.getListContaining(this);
                int n3 = listContaining3.indexOf(this);
                listContaining3.remove(this);
                listContaining3.add(n3 + 1, this);
                this.resourcePacksGUI.markChanged();
                return true;
            }
        }

        return false;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
    }
}
