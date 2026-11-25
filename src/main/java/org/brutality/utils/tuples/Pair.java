package org.brutality.utils.tuples;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import org.brutality.utils.tuples.immutable.ImmutablePair;

public abstract class Pair<A, B> implements Serializable {
    public abstract A getFirst();

    public abstract B getSecond();

    public static <A, B> Pair<A, B> computePair(A val, B val2) {
        return ImmutablePair.computeImmutablePair((A)val, (B)val2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFirst(), this.getSecond());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return !(obj instanceof Pair pair)
            ? false
            : Objects.equals(this.getFirst(), pair.getFirst()) && Objects.equals(this.getSecond(), pair.getSecond());
    }

    public abstract <R> R computeObject(BiFunction<? super A, ? super B, ? extends R> func);

    public abstract void handleBiConsumer(BiConsumer<? super A, ? super B> func);

    public static <A> Pair<A, A> computePair(A val) {
        return ImmutablePair.computeImmutablePair((A)val, (A)val);
    }
}
