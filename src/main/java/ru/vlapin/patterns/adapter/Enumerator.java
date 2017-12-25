package ru.vlapin.patterns.adapter;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Supplier;

@FunctionalInterface
public interface Enumerator<T> extends Iterator<T>, Supplier<Enumeration<T>> {

    static <T> Enumerator<T> from(Enumeration<T> enumeration) {
        return () -> enumeration;
    }

    @Override
    default boolean hasNext() {
        return get().hasMoreElements();
    }

    @Override
    default T next() {
        return get().nextElement();
    }
}
