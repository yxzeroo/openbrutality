package org.brutality.utils.tuples.immutable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.brutality.utils.tuples.Pair;

public final class ImmutablePair<A, B> extends Pair<A, B> {
    private final A first;
    private final B second;

    @Override
    public A getFirst() {
        return this.first;
    }

    public ImmutablePair(A val, B val2) {
        this.first = (A)val;
        this.second = (B)val2;
    }

    @Override
    public B getSecond() {
        return this.second;
    }

    @Override
    public <R> R computeObject(BiFunction<? super A, ? super B, ? extends R> biFunction) {
        return (R)biFunction.apply(this.first, this.second);
    }

    public static <A, B> ImmutablePair<A, B> computeImmutablePair(A val, B val2) {
        return new ImmutablePair<>((A)val, (B)val2);
    }

    public Pair<A, A> computePair2() {
        return Pair.computePair(this.first);
    }

    @Override
    public void handleBiConsumer(BiConsumer<? super A, ? super B> biConsumer) {
        biConsumer.accept(this.first, this.second);
    }

    public Pair<B, B> computePair() {
        return Pair.computePair(this.second);
    }
}
