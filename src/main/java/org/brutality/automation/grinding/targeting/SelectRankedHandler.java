package org.brutality.automation.grinding.targeting;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

public final class SelectRankedHandler {
    private SelectRankedHandler() {
    }

    public static <T> T computeObject(Collection<T> collection, Predicate<T> predicate, Predicate<T> predicate2, Comparator<T> comparator, boolean flag) {
        return (T)(flag ? collection.stream().filter(predicate).min(comparator).orElse(null) : computeObject(null, collection, predicate, predicate2, comparator, false));
    }

    public static <T> T computeObject(T val, Collection<T> collection, Predicate<T> predicate, Predicate<T> predicate2, Comparator<T> comparator, boolean flag) {
        boolean flag2 = val != null && collection.contains(val) && predicate.test(val);
        if (!flag2 || !flag && !predicate2.test(val)) {
            Object obj = collection.stream().filter(predicate).filter(predicate2).min(comparator).orElse(null);
            if (obj != null) {
                return (T)obj;
            }
            return (T)(flag2 ? val : collection.stream().filter(predicate).min(comparator).orElse(null));
        }
        return (T)val;
    }

    public static <T> T selectRankedOrdered(Collection<T> collection, Predicate<T> predicate, Predicate<T> predicate2, Comparator<T> comparator, boolean flag, ToIntFunction<T> toIntFunction) {
        int n = collection.stream().filter(predicate).mapToInt(toIntFunction).min().orElse(Integer.MAX_VALUE);
        return computeObject(collection, val -> predicate.test(val) && toIntFunction.applyAsInt(val) == n, predicate2, comparator, flag);
    }

    public static <T> T selectRanked(T val, Collection<T> collection, Predicate<T> predicate, Predicate<T> predicate2, Comparator<T> comparator, boolean flag, ToIntFunction<T> toIntFunction) {
        int n = collection.stream().filter(predicate).mapToInt(toIntFunction).min().orElse(Integer.MAX_VALUE);
        return computeObject((T)val, collection, val2 -> predicate.test(val2) && toIntFunction.applyAsInt(val2) == n, predicate2, comparator, flag);
    }
}
