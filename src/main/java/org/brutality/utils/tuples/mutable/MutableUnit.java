package org.brutality.utils.tuples.mutable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import org.brutality.utils.tuples.Unit;

public class MutableUnit<A> extends Unit<A> {
    private A object;

    public void handleUnaryOperator(UnaryOperator<A> unaryOperator) {
        this.object = unaryOperator.apply(this.object);
    }

    @Override
    public void handleConsumer(Consumer<? super A> callback) {
        callback.accept(this.object);
    }

    @Override
    public A get() {
        return this.object;
    }

    public static <A> MutableUnit<A> computeMutableUnit(A val) {
        return new MutableUnit<>((A)val);
    }

    @Override
    public <R> R computeObject(Function<? super A, ? extends R> fn) {
        return (R)fn.apply(this.object);
    }

    public void set(A val) {
        this.object = (A)val;
    }

    public MutableUnit(A val) {
        this.object = (A)val;
    }
}
