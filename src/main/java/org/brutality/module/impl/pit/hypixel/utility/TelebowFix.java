package org.brutality.module.impl.pit.hypixel.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import org.brutality.event.Event;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.PitCategory;
// VENDOR-DISABLED: import org.brutality.system.RuntimeAuthGuard;
import org.brutality.utils.misc.NextLongHelper;
import org.brutality.utils.network.NetworkHelper;
import org.brutality.utils.rotation.OnJumpHelper;

public class TelebowFix extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private long timestamp = 0L;
    private static final long timestamp2 = 5000L;

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            // VENDOR-DISABLED: if (RuntimeAuthGuard.checkState2()) {
            if (true) {
                if (event instanceof GetPlayerEvent getPlayerEvent) {
                    if (getPlayerEvent.getPlayer() == mc.thePlayer) {
                        if (this.matchesItemStack(getPlayerEvent.getItemStack())) {
                            this.timestamp = System.currentTimeMillis() + timestamp2;
                        }
                    }
                } else {
                    if (event instanceof EventPacketReceive event2) {
                        if (!(event2.getPacket() instanceof S08PacketPlayerPosLook)) {
                            return;
                        }

                        if (System.currentTimeMillis() > this.timestamp) {
                            return;
                        }

                        if (mc.thePlayer == null || mc.theWorld == null) {
                            return;
                        }

                        S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook)event2.getPacket();
                        event2.cancel();
                        double x = packet.getX();
                        double y = packet.getY();
                        double z = packet.getZ();
                        float yaw = packet.getYaw();
                        float pitch = packet.getPitch();
                        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
                            x += mc.thePlayer.posX;
                        } else {
                            mc.thePlayer.motionX = 0.0;
                        }

                        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
                            y += mc.thePlayer.posY;
                        } else {
                            mc.thePlayer.motionY = 0.0;
                        }

                        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
                            z += mc.thePlayer.posZ;
                        } else {
                            mc.thePlayer.motionZ = 0.0;
                        }

                        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
                            pitch += mc.thePlayer.rotationPitch;
                        }

                        if (packet.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
                            yaw += mc.thePlayer.rotationYaw;
                        }

                        mc.thePlayer
                            .setPositionAndRotation(
                                x,
                                y,
                                z,
                                OnJumpHelper.computeFloat2(mc.thePlayer.rotationYaw + NextLongHelper.computeFloat(-0.01F, 0.01F)),
                                OnJumpHelper.computeFloat2(mc.thePlayer.rotationPitch + NextLongHelper.computeFloat(-0.01F, 0.01F))
                            );
                        NetworkHelper.handlePacket(
                            new C03PacketPlayer.C06PacketPlayerPosLook(
                                mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY, mc.thePlayer.posZ, yaw % 360.0F, pitch % 360.0F, false
                            )
                        );
                    }

                    if (event instanceof RenderEvent4) {
                        this.timestamp = 0L;
                    }
                }
            }
        }
    }

    public TelebowFix() {
        super("TelebowFix", 0, Category.PIT);
    }

    private boolean matchesItemStack(ItemStack stack) {
        if (stack == null) {
            return false;
        }
        return stack.getDisplayName() != null && stack.getDisplayName().toLowerCase().contains("telebow")
            ? true
            : stack.hasTagCompound() && stack.getTagCompound().toString().toLowerCase().contains("telebow");
    }

    @Override
    public void onDisable() {
        this.timestamp = 0L;
        super.onDisable();
    }
}
