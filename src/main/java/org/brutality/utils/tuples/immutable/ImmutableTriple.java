package org.brutality.utils.tuples.immutable;

import org.brutality.utils.tuples.Triple;

public final class ImmutableTriple<A, B, C> extends Triple<A, B, C> {
    private final A first;
    private final B second;
    private final C object;

    public static <A, B, C> ImmutableTriple<A, B, C> computeImmutableTriple(A val, B val2, C val3) {
        return new ImmutableTriple<>((A)val, (B)val2, (C)val3);
    }

    public ImmutableTriple(A val, B val2, C val3) {
        this.first = (A)val;
        this.second = (B)val2;
        this.object = (C)val3;
    }

    public Triple<A, A, A> computeTriple2() {
        return Triple.computeTriple(this.first);
    }

    public Triple<B, B, B> computeTriple() {
        return Triple.computeTriple(this.second);
    }

    @Override
    public void handleListener(Triple.Listener<? super A, ? super B, ? super C> listener) {
        listener.handleObject(this.first, this.second, this.object);
    }

    @Override
    public C getObject() {
        return this.object;
    }

    @Override
    public <R> R computeObject(Triple.Listener2<? super A, ? super B, ? super C, ? extends R> listener2) {
        return (R)listener2.computeObject(this.first, this.second, this.object);
    }

    @Override
    public A getFirst() {
        return this.first;
    }

    public Triple<C, C, C> computeTriple3() {
        return Triple.computeTriple(this.object);
    }

    @Override
    public B getSecond() {
        return this.second;
    }
}
