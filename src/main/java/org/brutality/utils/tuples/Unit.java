package org.brutality.utils.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.brutality.utils.tuples.immutable.ImmutableUnit;

public abstract class Unit<A> implements Serializable {
    @Override
    public int hashCode() {
        return Objects.hash(this.get());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return obj instanceof Unit unit ? Objects.equals(this.get(), unit.get()) : false;
    }

    public abstract A get();

    public static <A> Unit<A> computeUnit(A val) {
        return ImmutableUnit.computeImmutableUnit((A)val);
    }

    public abstract <R> R computeObject(Function<? super A, ? extends R> func);

    public abstract void handleConsumer(Consumer<? super A> func);
}
