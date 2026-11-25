package org.brutality.script;

import org.brutality.Brutality;
import org.brutality.event.Event;
import org.brutality.event.EventState;
import org.brutality.event.impl.attack.AttackEvent3;
import org.brutality.event.impl.attack.GetDamageEvent;
import org.brutality.event.impl.basic.BasicEvent2;
import org.brutality.event.impl.basic.BasicEvent3;
import org.brutality.event.impl.basic.BasicEvent4;
import org.brutality.event.impl.basic.EventUpdate;
import org.brutality.event.impl.chat.EventChat;
import org.brutality.event.impl.movement.EventPostMotion;
import org.brutality.event.impl.movement.EventPreMotion;
import org.brutality.event.impl.movement.GetYawEvent2;
import org.brutality.event.impl.movement.IsSprintingEvent;
import org.brutality.event.impl.movement.MovementEvent10;
import org.brutality.event.impl.movement.MovementEvent3;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.movement.MovementEvent6;
import org.brutality.event.impl.movement.MovementEvent7;
import org.brutality.event.impl.movement.MovementEvent9;
import org.brutality.event.impl.packet.EventPacketReceive;
import org.brutality.event.impl.packet.EventPacketSend;
import org.brutality.event.impl.player.EventKey;
import org.brutality.event.impl.player.GetEntityEvent;
import org.brutality.event.impl.player.GetPlayerEvent;
import org.brutality.event.impl.player.PlayerEvent;
import org.brutality.event.impl.render.EventRender2D;
import org.brutality.event.impl.render.EventRender3D;
import org.brutality.module.Category;
import org.brutality.module.Module;
import org.brutality.settings.Setting;

public class ScriptModule extends Module {
    private final Script script;

    @Override
    public void onEvent(Event event) {
        if (this.script != null && !this.script.flag) {
            if (event instanceof EventUpdate event2) {
                if (event2.getState() == EventState.PRE) {
                    this.script.invokeVoid("onPreUpdate");
                    this.script.invokeVoid("onUpdate");
                } else if (event2.getState() == EventState.POST) {
                    this.script.invokeVoid("onPostUpdate");
                }
            } else if (event instanceof EventPreMotion event3) {
                Float[] vals = Brutality.scriptManager
                    .computeFloatTypeArray("getRotations", this.script, event3.x2, event3.y2, event3.z2, event3.yaw2, event3.pitch2, event3.ground2);
                if (vals != null && vals.length >= 2) {
                    event3.yaw2 = vals[0];
                    event3.pitch2 = vals[1];
                }

                this.script.invokeVoid("onPreMotion", event3.x2, event3.y2, event3.z2, event3.yaw2, event3.pitch2, event3.ground2);
            } else if (event instanceof EventPostMotion) {
                this.script.invokeVoid("onPostMotion");
            } else if (event instanceof EventPacketSend event4) {
                String simpleName = event4.getPacket().getClass().getSimpleName();
                if (this.script.invokeBoolean("onPacketSend", simpleName) == 0) {
                    event4.setCancelled(true);
                }
            } else if (event instanceof EventPacketReceive event5) {
                String simpleName2 = event5.getPacket().getClass().getSimpleName();
                if (this.script.invokeBoolean("onPacketReceive", simpleName2) == 0) {
                    event5.cancel();
                }
            } else if (event instanceof EventRender2D event6) {
                this.script.invokeVoid("onRender2D", event6.getPartialTicks());
            } else if (event instanceof EventRender3D event7) {
                this.script.invokeVoid("onRender3D", event7.getFactor());
            } else if (event instanceof EventChat event8) {
                if (this.script.invokeBoolean("onChat", event8.getMessage()) == 0) {
                    event8.cancel();
                }
            } else if (event instanceof EventKey event9) {
                this.script.invokeVoid("onKeyPress", event9.getKey());
            } else if (event instanceof BasicEvent3) {
                this.script.invokeVoid("onMouse");
            } else if (event instanceof BasicEvent2) {
                this.script.invokeVoid("onLeftClick");
            } else if (event instanceof BasicEvent4) {
                this.script.invokeVoid("onRightClick");
            } else if (event instanceof GetDamageEvent getDamageEvent) {
                if (this.script.invokeBoolean("onAttackEntity", getDamageEvent.getEntity2()) == 0) {
                    event.cancel();
                }
            } else if (event instanceof AttackEvent3) {
                this.script.invokeVoid("onHurt");
            } else if (event instanceof MovementEvent5 movementEvent5) {
                this.script.invokeVoid("onPrePlayerInput", movementEvent5.getFactor(), movementEvent5.getFactor2(), movementEvent5.isJump(), movementEvent5.isSneak());
            } else if (event instanceof MovementEvent3) {
                this.script.invokeVoid("onPostPlayerInput");
            } else if (event instanceof GetYawEvent2) {
                this.script.invokeVoid("onJump");
            } else if (event instanceof MovementEvent9 movementEvent9) {
                this.script.invokeVoid("onStep", movementEvent9.amount);
            } else if (event instanceof PlayerEvent playerEvent) {
                this.script.invokeVoid("onBoundingBox", playerEvent.getBlock(), playerEvent.getBlockPos());
            } else if (event instanceof GetEntityEvent getEntityEvent) {
                this.script.invokeVoid("onDeath", getEntityEvent.getEntity());
            } else if (event instanceof GetPlayerEvent getPlayerEvent) {
                this.script.invokeVoid("onArrowLoose", getPlayerEvent.getCount());
            } else if (event instanceof MovementEvent6) {
                this.script.invokeVoid("onPushOutOfBlock");
            } else if (event instanceof MovementEvent10) {
                this.script.invokeVoid("onWater");
            } else if (event instanceof MovementEvent7 movementEvent7) {
                this.script.invokeVoid("onSafeWalk", movementEvent7.amount);
            } else if (event instanceof IsSprintingEvent isSprintingEvent) {
                this.script.invokeVoid("onEntityAction", isSprintingEvent.isSprinting(), isSprintingEvent.isSneaking());
            }
        }
    }

    public boolean checkState() {
        return this.script != null && this.script.flag;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (Brutality.scriptManager != null) {
            Brutality.scriptManager.handleScript(this.script);
        }
    }

    private static String computeString(String str) {
        if (str == null) {
            return "Unknown";
        }
        if (str.endsWith(".java")) {
            str = str.substring(0, str.length() - 5);
        }

        return str;
    }

    public ScriptModule(Script script) {
        super(computeString(script.name), 0, Category.SCRIPTS);
        this.script = script;
        this.setRequiresInGame(false);
        this.handleScript(script);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (Brutality.scriptManager != null) {
            Brutality.scriptManager.handleScript2(this.script);
        }
    }

    private void handleScript(Script script) {
        if (script != null && script.object instanceof ScriptDefaults) {
            for (Setting setting : ((ScriptDefaults)script.object).getEntries()) {
                if (setting != null) {
                    setting.setParent(this);
                    this.handleSettingArray(new Setting[]{setting});
                }
            }
        }
    }

    @Override
    public void handleInt4(int n) {
        super.handleInt4(n);
        if (Brutality.scriptManager != null) {
            Brutality.scriptManager.process3();
        }
    }

    @Override
    public void process9() {
    }

    public Script getScript() {
        return this.script;
    }
}
