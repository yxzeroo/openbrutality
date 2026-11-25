package org.brutality.event;

import net.minecraft.client.Minecraft;
import org.brutality.Brutality;
import org.brutality.core.ConnectToServerHandler;
import org.brutality.core.OnClientChatReceivedHandler;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.movement.MovementEvent7;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.packet.GetPacketEvent;
import org.brutality.event.impl.player.EventTick;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.event.impl.render.GetEntityEvent;
import org.brutality.event.impl.render.GetEntityEvent2;
import org.brutality.event.impl.render.GetEntityEvent3;
import org.brutality.event.impl.render.GetStateEvent;
import org.brutality.event.impl.render.RenderEvent3;
import org.brutality.event.impl.render.RenderEvent4;
import org.brutality.event.impl.render.SetShaderTypeEvent;
import org.brutality.module.Module;
import org.brutality.module.ModuleManager;
import org.brutality.module.impl.combat.KillAura;
import org.brutality.module.impl.combat.LagRange;
import org.brutality.module.impl.exploit.PingSpoof;
import org.brutality.module.impl.movement.KeepSprint;
import org.brutality.module.impl.movement.MovementFix;
import org.brutality.module.impl.movement.NoSlowDown;
import org.brutality.module.impl.movement.Scaffold;
import org.brutality.module.impl.movement.Stasis;
import org.brutality.module.impl.pit.hypixel.swap.AntiVenomSwap;
// VENDOR-DISABLED: import org.brutality.system.BackendAuthService;
import org.brutality.utils.EditableModulesHelper;
import org.brutality.utils.combat.CombatHelper;
import org.brutality.utils.misc.ColorMathUtil;
import org.brutality.utils.performance.EntityCache;
import org.brutality.utils.performance.WorldQueryCache;
import org.brutality.utils.performance.PrintCopyPasteReportHelper;
import org.brutality.utils.player.StartHotbarMovesHelper;
import org.brutality.utils.render.GlStateGuard;

public class Event {
    private Minecraft mc = Minecraft.getMinecraft();
    protected boolean cancel = false;

    private boolean checkState4() {
        return this instanceof GetEntityEvent2
            || this instanceof RenderEvent3
            || this instanceof GetStateEvent
            || this instanceof GetEntityEvent
            || this instanceof GetEntityEvent3
            || this instanceof org.brutality.event.impl.player.GetEntityEvent2
            || this instanceof EventUpdate
            || this instanceof MovementEvent7;
    }

    public boolean cancel2() {
        return this.cancel;
    }

    public void cancel() {
        this.cancel = true;
    }

    public void process() {
        if (PingSpoof.onEvent2(this)) {
            if (this instanceof EventPacketSend) {
                ((EventPacketSend)this).setCancelled(true);
            }

            this.cancel();
        } else {
            if (this instanceof EventPacketSend) {
                Scaffold.handlePacket(((EventPacketSend)this).getPacket());
                if (KeepSprint.matchesPacket(((EventPacketSend)this).getPacket())) {
                    ((EventPacketSend)this).setCancelled(true);
                    return;
                }

                ColorMathUtil.Entry8.handlePacket(((EventPacketSend)this).getPacket());
                Stasis instance = Stasis.instance;
                if (instance != null && instance.checkState44() && !instance.matchesPacket(((EventPacketSend)this).getPacket())) {
                    ((EventPacketSend)this).setCancelled(true);
                    return;
                }
            }

            if (StartHotbarMovesHelper.onEvent2(this)) {
                this.cancel();
            } else {
                if (this.mc.thePlayer != null && this.mc.theWorld != null) {
                    // VENDOR-DISABLED: BackendAuthService.process4();
                    if (this instanceof GetDamageEvent) {
                        CombatHelper.onGetDamageEvent((GetDamageEvent)this);
                    }

                    if (this instanceof RenderEvent4) {
                        WorldQueryCache.reset();
                        CombatHelper.reset();
                    }

                    boolean flag = this instanceof GetPacketEvent;
                    boolean flag2 = this instanceof EventRender2D || this instanceof EventRender3D;
                    boolean flag3 = flag2 || this instanceof SetShaderTypeEvent;
                    boolean flag4 = this instanceof MovementEvent5;
                    boolean flag5 = flag2 && EditableModulesHelper.isActive();
                    if (Brutality.lagHandler != null) {
                        if (this instanceof EventPacketSend) {
                            Brutality.lagHandler.onSendPacket((EventPacketSend)this);
                        } else if (this instanceof EventPacketReceive) {
                            Brutality.lagHandler.onReceivePacket((EventPacketReceive)this);
                        } else if (this instanceof EventTick && ((EventTick)this).getState() == EventState.PRE) {
                            Brutality.lagHandler.process2();
                        }
                    }

                    if (flag && LagRange.instance.checkState44()) {
                        LagRange.instance.onEvent(this);
                    }

                    if (this instanceof EventUpdate && ((EventUpdate)this).getState() == EventState.PRE) {
                        WorldQueryCache.process();
                        NoSlowDown.process6();
                        AntiVenomSwap.process();
                        EntityCache.update();
                        if (!KillAura.instance.checkState44()) {
                            KillAura.instance.handleFlag2(true);
                        }
                    }

                    if (this instanceof EventPacketSend) {
                        synchronized (KillAura.instance.entries) {
                            if (KillAura.instance.flag30) {
                                KillAura.instance.entries.add(((EventPacketSend)this).getPacket());
                                ((EventPacketSend)this).setCancelled(true);
                            }
                        }
                    }

                    boolean flag6 = PrintCopyPasteReportHelper.isEnabled() && !this.checkState4();
                    String str = null;
                    if (flag6) {
                        str = this.getClass().getSimpleName();
                        PrintCopyPasteReportHelper.handleString("event." + str);
                    }

                    if (flag4) {
                        MovementFix.onEvent2(this);
                    }

                    for (Module module : ModuleManager.getModuleArray()) {
                        if (module.checkState44()
                            && !(this instanceof SetShaderTypeEvent setShaderTypeEvent && !module.onBlurMode(setShaderTypeEvent.getShaderType()))
                            && !flag5
                            && (!flag2 || !EditableModulesHelper.matchesModule(module))
                            && (!(module instanceof LagRange) || !flag)) {
                            GlStateGuard renderHelper7 = flag3 ? new GlStateGuard() : null;

                            try {
                                if (flag6) {
                                    String str2 = "module." + module.getName();
                                    PrintCopyPasteReportHelper.handleString(str2);

                                    try {
                                        module.onEvent(this);
                                    } finally {
                                        PrintCopyPasteReportHelper.end(str2);
                                    }
                                } else {
                                    module.onEvent(this);
                                }
                            } finally {
                                if (renderHelper7 != null) {
                                    renderHelper7.close();
                                }
                            }
                        }
                    }

                    if (this instanceof EventPacketSend || this instanceof EventPacketReceive) {
                        NoSlowDown.onEvent2(this);
                    }

                    if (flag6) {
                        PrintCopyPasteReportHelper.end("event." + str);
                    }

                    if (!flag4) {
                        MovementFix.onEvent2(this);
                    }

                    ConnectToServerHandler.onEvent(this);
                    OnClientChatReceivedHandler.onEvent(this);
                }
            }
        }
    }
}
