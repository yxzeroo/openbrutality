package org.brutality.utils.tuples.mutable;

import java.util.function.UnaryOperator;
import org.brutality.utils.tuples.Triple;

public class MutableTriple<A, B, C> extends Triple<A, B, C> {
    private A first;
    private B second;
    private C object;

    public MutableTriple(A val, B val2, C val3) {
        this.first = (A)val;
        this.second = (B)val2;
        this.object = (C)val3;
    }

    public void handleUnaryOperator2(UnaryOperator<A> unaryOperator) {
        this.first = unaryOperator.apply(this.first);
    }

    public MutableTriple<A, A, A> computeMutableTriple2() {
        return computeMutableTriple(this.first);
    }

    public static <A> MutableTriple<A, A, A> computeMutableTriple(A val) {
        return new MutableTriple<>((A)val, (A)val, (A)val);
    }

    public MutableTriple<C, C, C> computeMutableTriple3() {
        return computeMutableTriple(this.object);
    }

    @Override
    public void handleListener(Triple.Listener<? super A, ? super B, ? super C> listener) {
        listener.handleObject(this.first, this.second, this.object);
    }

    public void setSecond(B val) {
        this.second = (B)val;
    }

    public void setObject(C val) {
        this.object = (C)val;
    }

    public void handleUnaryOperator(UnaryOperator<C> unaryOperator) {
        this.object = unaryOperator.apply(this.object);
    }

    @Override
    public C getObject() {
        return this.object;
    }

    @Override
    public <R> R computeObject(Triple.Listener2<? super A, ? super B, ? super C, ? extends R> listener2) {
        return (R)listener2.computeObject(this.first, this.second, this.object);
    }

    public void setFirst(A val) {
        this.first = (A)val;
    }

    @Override
    public B getSecond() {
        return this.second;
    }

    public void handleUnaryOperator3(UnaryOperator<B> unaryOperator) {
        this.second = unaryOperator.apply(this.second);
    }

    public static <A, B, C> MutableTriple<A, B, C> computeMutableTriple(A val, B val2, C val3) {
        return new MutableTriple<>((A)val, (B)val2, (C)val3);
    }

    @Override
    public A getFirst() {
        return this.first;
    }

    public MutableTriple<B, B, B> computeMutableTriple() {
        return computeMutableTriple(this.second);
    }
}
