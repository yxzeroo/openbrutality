package org.brutality.utils.tuples.mutable;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import org.brutality.utils.tuples.Pair;

public class MutablePair<A, B> extends Pair<A, B> {
    private A first;
    private B second;

    public void setFirst(A val) {
        this.first = (A)val;
    }

    public static <A> MutablePair<A, A> computeMutablePair(A val) {
        return new MutablePair<>((A)val, (A)val);
    }

    @Override
    public B getSecond() {
        return this.second;
    }

    public MutablePair<A, A> computeMutablePair2() {
        return computeMutablePair(this.first);
    }

    @Override
    public <R> R computeObject(BiFunction<? super A, ? super B, ? extends R> biFunction) {
        return (R)biFunction.apply(this.first, this.second);
    }

    public MutablePair(A val, B val2) {
        this.first = (A)val;
        this.second = (B)val2;
    }

    @Override
    public A getFirst() {
        return this.first;
    }

    public void setSecond(B val) {
        this.second = (B)val;
    }

    @Override
    public void handleBiConsumer(BiConsumer<? super A, ? super B> biConsumer) {
        biConsumer.accept(this.first, this.second);
    }

    public static <A, B> MutablePair<A, B> computeMutablePair(A val, B val2) {
        return new MutablePair<>((A)val, (B)val2);
    }

    public void handleUnaryOperator2(UnaryOperator<B> unaryOperator) {
        this.second = unaryOperator.apply(this.second);
    }

    public void handleUnaryOperator(UnaryOperator<A> unaryOperator) {
        this.first = unaryOperator.apply(this.first);
    }

    public MutablePair<B, B> computeMutablePair() {
        return computeMutablePair(this.second);
    }
}
