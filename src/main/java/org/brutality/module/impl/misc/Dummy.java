package org.brutality.module.impl.misc;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.brutality.event.Event;
import org.brutality.module.Category;
import org.brutality.module.Module;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;

public class Dummy extends Module {
    public EntityOtherPlayerMP entityOtherPlayerMP = null;

    public Dummy() {
        super("Dummy", 0, Category.MISCELLANEOUS);
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                ;
            }
        }
    }

    @Override
    public void onEnable() {
        // VENDOR-DISABLED: if (!RuntimeAuthGuard.checkState2()) {
        if (false) {
            this.handleFlag3(false);
        } else {
            this.entityOtherPlayerMP = new EntityOtherPlayerMP(mc.theWorld, new GameProfile(mc.thePlayer.getUniqueID(), "Dummy"));
            this.entityOtherPlayerMP.copyLocationAndAnglesFrom(mc.thePlayer);
            mc.theWorld.addEntityToWorld(-8008, this.entityOtherPlayerMP);
            this.entityOtherPlayerMP.inventory.armorInventory[0] = new ItemStack(Items.golden_helmet);
            this.entityOtherPlayerMP.setCurrentItemOrArmor(0, new ItemStack(Blocks.wool));
        }
    }

    @Override
    public void onDisable() {
        if (this.entityOtherPlayerMP != null) {
            mc.theWorld.removeEntity(this.entityOtherPlayerMP);
            this.entityOtherPlayerMP = null;
        }
    }
}
