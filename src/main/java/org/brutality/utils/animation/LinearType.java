package org.brutality.utils.animation;

import java.util.function.Function;

public enum LinearType {
    LINEAR(val -> val),
    EASE_IN_QUAD(val -> val * val),
    EASE_OUT_QUAD(val -> val * (2.0 - val)),
    EASE_IN_OUT_QUAD(val -> val < 0.5 ? 2.0 * val * val : -1.0 + (4.0 - 2.0 * val) * val),
    EASE_IN_CUBIC(val -> val * val * val),
    EASE_OUT_CUBIC(val -> {
        Double val2;
        return (val2 = val - 1.0) * val2 * val2 + 1.0;
    }),
    EASE_IN_OUT_CUBIC(val -> val < 0.5 ? 4.0 * val * val * val : (val - 1.0) * (2.0 * val - 2.0) * (2.0 * val - 2.0) + 1.0),
    EASE_IN_QUART(val -> val * val * val * val),
    EASE_OUT_QUART(val -> {
        Double val2;
        return 1.0 - (val2 = val - 1.0) * val2 * val2 * val2;
    }),
    EASE_IN_OUT_QUART(val -> {
        Double val2;
        return val < 0.5 ? 8.0 * val * val * val * val : 1.0 - 8.0 * (val2 = val - 1.0) * val2 * val2 * val2;
    }),
    EASE_IN_QUINT(val -> val * val * val * val * val),
    EASE_OUT_QUINT(val -> {
        Double val2;
        return 1.0 + (val2 = val - 1.0) * val2 * val2 * val2 * val2;
    }),
    EASE_IN_OUT_QUINT(val -> {
        Double val2;
        return val < 0.5 ? 16.0 * val * val * val * val * val : 1.0 + 16.0 * (val2 = val - 1.0) * val2 * val2 * val2 * val2;
    }),
    EASE_IN_SINE(val -> 1.0 - Math.cos(val * Math.PI / 2.0)),
    EASE_OUT_SINE(val -> Math.sin(val * Math.PI / 2.0)),
    EASE_IN_OUT_SINE(val -> (1.0 - Math.cos(Math.PI * val)) / 2.0),
    EASE_IN_EXPO(val -> val == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * val - 10.0)),
    EASE_OUT_EXPO(val -> val == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * val)),
    EASE_IN_OUT_EXPO(
        val -> val == 0.0
            ? 0.0
            : (val == 1.0 ? 1.0 : (val < 0.5 ? Math.pow(2.0, 20.0 * val - 10.0) / 2.0 : (2.0 - Math.pow(2.0, -20.0 * val + 10.0)) / 2.0))
    ),
    EASE_IN_CIRC(val -> 1.0 - Math.sqrt(1.0 - val * val)),
    EASE_OUT_CIRC(val -> {
        Double val2;
        return Math.sqrt(1.0 - (val2 = val - 1.0) * val2);
    }),
    EASE_IN_OUT_CIRC(val -> val < 0.5 ? (1.0 - Math.sqrt(1.0 - 4.0 * val * val)) / 2.0 : (Math.sqrt(1.0 - 4.0 * (val - 1.0) * val) + 1.0) / 2.0),
    EASE_OUT_BACK(val -> {
        double d = 1.70158;
        double d2 = d + 1.0;
        return 1.0 + d2 * Math.pow(val - 1.0, 3.0) + d * Math.pow(val - 1.0, 2.0);
    }),
    EASE_OUT_ELASTIC(val -> val != 0.0 && val != 1.0 ? Math.pow(2.0, -10.0 * val) * Math.sin((val * 10.0 - 0.75) * (Math.PI * 2.0 / 3.0)) + 1.0 : val),
    CATEGORY_SWITCH(val -> val < 0.5 ? 2.0 * val * val : -1.0 + (4.0 - 2.0 * val) * val),
    MODULE_TOGGLE(val -> val * val * (3.0 - 2.0 * val)),
    SLIDER_EASE(val -> val * val * (3.0 - 2.0 * val)),
    EXPAND_COLLAPSE(val -> val < 0.5 ? 4.0 * val * val * val : 1.0 - Math.pow(-2.0 * val + 2.0, 3.0) / 2.0);

    private final Function<Double, Double> function;

    public double computeDouble(double d) {
        return this.function.apply(Math.max(0.0, Math.min(1.0, d)));
    }

    private static LinearType[] computeLinearTypeArray() {
        return new LinearType[]{
            LINEAR,
            EASE_IN_QUAD,
            EASE_OUT_QUAD,
            EASE_IN_OUT_QUAD,
            EASE_IN_CUBIC,
            EASE_OUT_CUBIC,
            EASE_IN_OUT_CUBIC,
            EASE_IN_QUART,
            EASE_OUT_QUART,
            EASE_IN_OUT_QUART,
            EASE_IN_QUINT,
            EASE_OUT_QUINT,
            EASE_IN_OUT_QUINT,
            EASE_IN_SINE,
            EASE_OUT_SINE,
            EASE_IN_OUT_SINE,
            EASE_IN_EXPO,
            EASE_OUT_EXPO,
            EASE_IN_OUT_EXPO,
            EASE_IN_CIRC,
            EASE_OUT_CIRC,
            EASE_IN_OUT_CIRC,
            EASE_OUT_BACK,
            EASE_OUT_ELASTIC,
            CATEGORY_SWITCH,
            MODULE_TOGGLE,
            SLIDER_EASE,
            EXPAND_COLLAPSE
        };
    }

    public Function<Double, Double> getFunction() {
        return this.function;
    }

    private LinearType(Function<Double, Double> function) {
        this.function = function;
    }
}
