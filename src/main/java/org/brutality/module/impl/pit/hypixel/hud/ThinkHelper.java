package org.brutality.module.impl.pit.hypixel.hud;

import java.awt.Color;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.brutality.event.Event;
import org.brutality.event.impl.render.GetPartialTicksEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.ColorSetting;
import org.brutality.utils.performance.EntityCache;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;

public class ThinkHelper extends Module {
    private final PitCategory pitCategory = PitCategory.HUD;
    private ColorSetting protectionColorSetting = new ColorSetting("Protection Color", this, 0.6F, 1.0F, 1.0F);

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof GetPartialTicksEvent) {
                this.process();
            }
        }
    }

    public ThinkHelper() {
        super("Think Helper", 0, Category.PIT);
        this.handleSettingArray(new Setting[]{this.protectionColorSetting});
    }

    private void handleEntityPlayer(EntityPlayer player) {
        double d = player.posX - mc.getRenderManager().renderPosX;
        double d2 = player.posY - mc.getRenderManager().renderPosY;
        double d3 = player.posZ - mc.getRenderManager().renderPosZ;
        Color color = this.protectionColorSetting.computeColor();
        float f = color.getRed() / 255.0F;
        float f2 = color.getGreen() / 255.0F;
        float f3 = color.getBlue() / 255.0F;
        GL11.glPushMatrix();
        GL11.glTranslated(d, d2, d3);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4d(f, f2, f3, 0.2);
        Sphere sphere = new Sphere();
        sphere.setDrawStyle(100012);
        sphere.draw(10.0F, 20, 20);
        GL11.glColor4d(f, f2, f3, 1.0);
        Sphere sphere2 = new Sphere();
        sphere2.setDrawStyle(100011);
        sphere2.draw(10.1F, 20, 20);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void process() {
        if (mc.theWorld != null && mc.thePlayer != null) {
            for (EntityPlayer player : EntityCache.getEntries()) {
                if (this.matchesEntityPlayer(player)) {
                    this.handleEntityPlayer(player);
                }
            }
        }
    }

    private boolean matchesEntityPlayer(EntityPlayer player) {
        ItemStack stack = player.inventory.armorInventory[1];
        if (stack != null && stack.getItem() instanceof ItemArmor) {
            NBTTagCompound tagCompound = stack.getTagCompound();
            if (tagCompound != null && tagCompound.hasKey("display", 10)) {
                NBTTagCompound compoundTag = tagCompound.getCompoundTag("display");
                if (compoundTag.hasKey("Lore", 9)) {
                    NBTTagList tagList = compoundTag.getTagList("Lore", 8);

                    for (int i = 0; i < tagList.tagCount(); i++) {
                        String stringTagAt = tagList.getStringTagAt(i);
                        if (stringTagAt.contains("Think of the People")) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}
