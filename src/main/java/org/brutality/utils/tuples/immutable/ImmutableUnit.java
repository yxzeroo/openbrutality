package org.brutality.utils.tuples.immutable;

import java.util.function.Consumer;
import java.util.function.Function;
import org.brutality.utils.tuples.Unit;

public final class ImmutableUnit<A> extends Unit<A> {
    private final A object;

    @Override
    public <R> R computeObject(Function<? super A, ? extends R> fn) {
        return (R)fn.apply(this.object);
    }

    public static <A> ImmutableUnit<A> computeImmutableUnit(A val) {
        return new ImmutableUnit<>((A)val);
    }

    @Override
    public void handleConsumer(Consumer<? super A> callback) {
        callback.accept(this.object);
    }

    public ImmutableUnit(A val) {
        this.object = (A)val;
    }

    @Override
    public A get() {
        return this.object;
    }
}
