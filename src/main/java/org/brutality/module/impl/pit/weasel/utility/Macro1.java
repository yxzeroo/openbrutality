package org.brutality.module.impl.pit.weasel.utility;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.MathHelper;
import org.brutality.event.Event;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.PitCategory;
import org.brutality.utils.MessageUtils;

public class Macro1 extends Module {
    private final PitCategory pitCategory = PitCategory.UTILITY;
    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean flag = false;
    private int count = 0;
    private int count2 = 0;
    private int count3 = 0;
    private int count4 = 0;
    private int count5 = 0;
    private int count6 = 0;
    private int count7 = 0;
    private int count8 = 0;
    private int major = 0;
    private int count9 = 0;
    private int count10 = 0;
    private int state = 0;
    private int count11 = 0;
    private boolean flag2 = false;

    private void process() {
        if (this.count11 == 0) {
            this.count9 = 1;
            this.count4 = 1;
            this.log("spawning");
        }

        double[][] doublesArray = new double[][]{{0.5, 20.5}, {1.5, 20.5}, {0.5, 20.5}, {1.5, 93.5}, {-8.5, 93.5}};
        if (this.count11 < doublesArray.length) {
            double d = doublesArray[this.count11][0];
            double d2 = doublesArray[this.count11][1];
            EntityPlayerSP player = this.mc.thePlayer;
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), true);
            double d3 = player.posY + 1.62;
            float[] floats = this.computeFloatArray(player.posX, d3, player.posZ, d, d3, d2);
            player.rotationYaw = floats[0];
            player.rotationPitch = floats[1];
            if (Math.abs(player.posX - d) <= 0.075 && Math.abs(player.posZ - d2) <= 0.075) {
                KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
                this.count11++;
                int n = this.count11;
                this.log("Reached coord " + n + ": (" + d + ", " + d2 + ")");
            }
        } else if (this.count11 == doublesArray.length) {
            this.mc.thePlayer.sendChatMessage("/spawn");
            this.count10 = 200;
            this.state = 5;
            this.count11++;
            this.log("Sent /spawn, tickDelay=200, state=5");
        } else if (this.count11 == doublesArray.length + 1 && this.state == 5) {
            this.mc.thePlayer.sendChatMessage("/swap");
            this.count10 = 200;
            this.state = 6;
            this.count11++;
            this.log("Sent /swap, tickDelay=200, state=6");
        } else if (this.count11 == doublesArray.length + 2 && this.state == 6) {
            this.mc.thePlayer.sendChatMessage("/l");
            this.count10 = 300;
            this.state = 7;
            this.count11++;
            this.log("Sent /l, tickDelay=300, state=7");
        } else if (this.count11 == doublesArray.length + 3 && this.state == 7) {
            this.mc.thePlayer.sendChatMessage("/play pit");
            this.count11 = 0;
            this.state = 0;
            this.log("Sent /play pit, reset moveStep and state");
        }
    }

    @Override
    public void onEvent(Event event) {
        if (this.checkState44()) {
            if (event instanceof EventUpdate) {
                EntityPlayerSP player = this.mc.thePlayer;
                if (player == null || this.mc.theWorld == null) {
                    this.log("Player or world is null");
                    return;
                }

                boolean flag = this.flag;
                int n = this.count9;
                int major = this.major;
                int n2 = this.count5;
                int n3 = this.count4;
                int n4 = this.count3;
                int n5 = this.count2;
                int count = this.count;
                int n6 = this.count10;
                int state = this.state;
                this.log(
                    "State: "
                        + state
                        + ", tickDelay: "
                        + n6
                        + ", death: "
                        + count
                        + ", mega: "
                        + n5
                        + ", premega: "
                        + n4
                        + ", kill: "
                        + n3
                        + ", i: "
                        + n2
                        + ", major: "
                        + major
                        + ", inSpawn: "
                        + n
                        + ", endStreak: "
                        + flag
                );
                if (player.isDead && !this.flag2) {
                    this.count = 1;
                    this.flag2 = true;
                    this.log("Player death detected");
                } else if (!player.isDead && this.flag2) {
                    this.flag2 = false;
                }

                if (this.count10 > 0) {
                    this.count10--;
                    this.log("Waiting for tickDelay: " + this.count10);
                    return;
                }

                if (this.count == 1) {
                    this.count = 0;
                    if (this.count2 == 0) {
                        this.log("Premega Detected");
                        this.count3 = 1;
                        this.count4 = 1;
                    }

                    this.count2 = 0;
                    this.count4 = 1;
                    this.log("Death handled: premega=" + this.count3 + ", kill=" + this.count4);
                }

                if (this.count6 != 1 && this.count7 != 1 && this.count8 != 1 && this.major != 1) {
                    this.flag = false;
                    this.log("endStreak set to false");
                } else {
                    this.flag = true;
                    this.log("endStreak set to true due to unwanted players or major");
                }

                if (this.flag && this.count2 == 1) {
                    this.count4 = 1;
                    this.mc.thePlayer.sendChatMessage("/oof");
                    this.count10 = 3600;
                    this.state = 1;
                    this.log("Mega and endStreak: sent /oof, tickDelay=3600");
                    return;
                }

                if (this.state == 1) {
                    this.count2 = 0;
                    this.state = 0;
                    this.log("Reset mega, state=0");
                }

                if (!this.flag) {
                    this.count10 = 40;
                    this.state = 2;
                    this.log("No endStreak: tickDelay=40, state=2");
                    return;
                }

                if (this.state == 2) {
                    this.count4 = 0;
                    this.count5 = 1;
                    this.state = 0;
                    this.log("Resumed streaking: kill=0, i=1");
                }

                if (this.count3 == 1) {
                    this.count4 = 1;
                    this.flag = true;
                    this.mc.thePlayer.sendChatMessage("?");
                    this.count10 = 70;
                    this.state = 3;
                    this.log("Premega: sent ?, tickDelay=70, state=3");
                    return;
                }

                if (this.state == 3) {
                    this.mc.thePlayer.sendChatMessage("/swap");
                    this.count10 = 5000;
                    this.state = 4;
                    this.log("Premega: sent /swap, tickDelay=5000, state=4");
                    return;
                }

                if (this.state == 4) {
                    this.count5 = 1;
                    this.flag = false;
                    this.state = 0;
                    this.log("Premega complete: i=1, endStreak=false");
                }

                if (this.major == 1 && this.count9 == 0) {
                    this.count9 = 1;
                    this.log("Leaving PIT");
                    this.process();
                } else {
                    this.log("No leaveMid: major=" + this.major + ", inSpawn=" + this.count9);
                }
            } else if (event instanceof EventPacketSend event2 && event2.getPacket() instanceof C01PacketChatMessage) {
                String message = ((C01PacketChatMessage)event2.getPacket()).getMessage();
                if (message.equalsIgnoreCase("/macro1 major")) {
                    this.major = 1;
                    this.log("Manual trigger: major=1");
                    event2.setCancelled(true);
                }
            }
        }
    }

    @Override
    public void onEnable() {
        this.flag = false;
        this.count = 0;
        this.count2 = 0;
        this.count3 = 0;
        this.count4 = 0;
        this.count5 = 0;
        this.count6 = 0;
        this.count7 = 0;
        this.count8 = 0;
        this.major = 0;
        this.count9 = 0;
        this.count10 = 0;
        this.state = 0;
        this.count11 = 0;
        this.flag2 = false;
        this.log("Macro1 enabled");
        super.onEnable();
    }

    private float[] computeFloatArray(double d, double d2, double d3, double d4, double d5, double d6) {
        double d7 = d4 - d;
        double d8 = d5 - d2;
        double d9 = d6 - d3;
        double dist = Math.sqrt(d7 * d7 + d8 * d8 + d9 * d9);
        d7 /= dist;
        d8 /= dist;
        d9 /= dist;
        float f = (float)Math.toDegrees(Math.atan2(d9, d7)) - 90.0F;
        float f2 = (float)(-Math.toDegrees(Math.asin(d8)));
        return new float[]{MathHelper.wrapAngleTo180_float(f), MathHelper.wrapAngleTo180_float(f2)};
    }

    private void log(String str) {
        ModuleManager.handleString(
            MessageUtils.computeString("[", MessageUtils.text9)
                + MessageUtils.computeString("Macro1", MessageUtils.text5)
                + MessageUtils.computeString("] ", MessageUtils.text9)
                + MessageUtils.computeString(str, MessageUtils.text8)
        );
    }

    public Macro1() {
        super("Macro1", 0, Category.PIT);
    }

    @Override
    public void onDisable() {
        if (this.count11 > 0 && this.count11 < 5) {
            KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindForward.getKeyCode(), false);
        }

        this.log("Macro1 disabled");
        super.onDisable();
    }
}
