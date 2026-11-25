package org.brutality.utils.tuples;

import java.io.Serializable;
import java.util.Objects;
import org.brutality.utils.tuples.immutable.ImmutableTriple;

public abstract class Triple<A, B, C> implements Serializable {
    public abstract B getSecond();

    public abstract A getFirst();

    public abstract C getObject();

    public static <A> Triple<A, A, A> computeTriple(A val) {
        return ImmutableTriple.computeImmutableTriple((A)val, (A)val, (A)val);
    }

    public static <A, B, C> Triple<A, B, C> computeTriple(A val, B val2, C val3) {
        return ImmutableTriple.computeImmutableTriple((A)val, (B)val2, (C)val3);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return !(obj instanceof Triple triple)
            ? false
            : Objects.equals(this.getFirst(), triple.getFirst())
                && Objects.equals(this.getSecond(), triple.getSecond())
                && Objects.equals(this.getObject(), triple.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFirst(), this.getSecond(), this.getObject());
    }

    public abstract void handleListener(Triple.Listener<? super A, ? super B, ? super C> func);

    public abstract <R> R computeObject(Triple.Listener2<? super A, ? super B, ? super C, ? extends R> func);

    public interface Listener<T, U, V> {
        void handleObject(T t, U u, V v);
    }

    public interface Listener2<T, U, V, R> {
        R computeObject(T t, U u, V v);
    }
}
