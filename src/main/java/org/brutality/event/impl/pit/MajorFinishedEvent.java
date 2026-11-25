package org.brutality.event.impl.pit;

import org.brutality.event.Event;
import org.brutality.util.PizzaType;

public class MajorFinishedEvent extends Event {
    public float factor;
    public float factor2;
    private PizzaType pizzaType;
    public int count;

    @Override
    public String toString() {
        return String.valueOf(
            new StringBuilder("MajorFinishedEvent{xpGained=")
                .append(this.factor2)
                .append(", goldGained=")
                .append(this.factor)
                .append(", renownGained=")
                .append(this.count)
                .append('}')
        );
    }

    public String computeString() {
        StringBuilder sb = new StringBuilder();
        if (this.factor2 > 0.0F) {
            sb.append(String.format("%.0f XP", this.factor2));
        }

        if (this.factor > 0.0F) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(String.format("%.0f Gold", this.factor));
        }

        if (this.count > 0) {
            if (sb.length() > 0) {
                sb.append(", ");
            }

            sb.append(String.format("%d Renown", this.count));
        }

        return String.valueOf(sb);
    }

    public String computeString2() {
        return this.pizzaType != null ? this.pizzaType.getDisplayName() : "Unknown Major";
    }

    public MajorFinishedEvent(float f, float factor, int count) {
        this.factor2 = f;
        this.factor = factor;
        this.count = count;
    }

    public MajorFinishedEvent(PizzaType pizzaType) {
        this.pizzaType = pizzaType;
    }
}
