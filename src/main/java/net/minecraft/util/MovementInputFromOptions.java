package net.minecraft.util;

import net.minecraft.client.settings.GameSettings;
import org.brutality.event.impl.movement.MovementEvent3;
import org.brutality.event.impl.movement.MovementEvent5;
import org.brutality.event.impl.movement.SetCancelledEvent;

public class MovementInputFromOptions extends MovementInput {
    private final GameSettings gameSettings;

    public MovementInputFromOptions(GameSettings gameSettingsIn) {
        this.gameSettings = gameSettingsIn;
    }

    @Override
    public void updatePlayerMoveState() {
        this.moveStrafe = 0.0F;
        this.moveForward = 0.0F;
        if (this.gameSettings.keyBindForward.isKeyDown()) {
            this.moveForward++;
        }

        if (this.gameSettings.keyBindBack.isKeyDown()) {
            this.moveForward--;
        }

        if (this.gameSettings.keyBindLeft.isKeyDown()) {
            this.moveStrafe++;
        }

        if (this.gameSettings.keyBindRight.isKeyDown()) {
            this.moveStrafe--;
        }

        this.jump = this.gameSettings.keyBindJump.isKeyDown();
        boolean keyDown = this.gameSettings.keyBindSneak.isKeyDown();
        MovementEvent5 movementEvent5 = new MovementEvent5(this.moveForward, this.moveStrafe, this.jump, keyDown, 0.3);
        movementEvent5.process();
        double amount = movementEvent5.getAmount();
        this.moveForward = movementEvent5.getFactor();
        this.moveStrafe = movementEvent5.getFactor2();
        this.jump = movementEvent5.isJump();
        this.sneak = movementEvent5.isSneak();
        if (this.sneak) {
            SetCancelledEvent setCancelledEvent = new SetCancelledEvent(true);
            setCancelledEvent.process();
            if (setCancelledEvent.isCancelled()) {
                return;
            }

            this.moveStrafe = (float)(this.moveStrafe * amount);
            this.moveForward = (float)(this.moveForward * amount);
        }

        MovementEvent3 movementEvent3 = new MovementEvent3();
        movementEvent3.process();
    }
}
