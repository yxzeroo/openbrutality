package org.brutality.module.impl.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.brutality.event.Event;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;
import org.brutality.settings.impl.NumberSetting;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.network.NetworkHelper;

public class ComboOneHit extends Module {
    private final Minecraft mc = Minecraft.getMinecraft();
    private final NumberSetting attackPacketsSetting = new NumberSetting("Attack Packets", this, 50.0, 1.0, 1000.0, 1.0);

    public ComboOneHit() {
        super("ComboOneHit", 0, Category.COMBAT);
        this.handleSettingArray(new Setting[]{this.attackPacketsSetting});
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetDamageEvent getDamageEvent) {
                    Entity entity2 = getDamageEvent.getEntity2();

                    for (int i = 0; i < this.attackPacketsSetting.computeDouble(); i++) {
                        C02PacketUseEntity packet = new C02PacketUseEntity(entity2, C02PacketUseEntity.Action.ATTACK);
                        NetworkHelper.handlePacket2(packet);
                    }
                }
            }
        }
    }
}
