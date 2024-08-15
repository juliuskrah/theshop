package com.shoperal.core.utility;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Utilities for stream operations
 * 
 * @author Julius Krah
 */
public class StreamUtilities {
    private StreamUtilities() {
    }

    /**
     * Performs stream operations on an Iterable
     * 
     * @param <D>         target type
     * @param <E>         source type
     * @param source      the iterable
     * @param transformer the function to apply
     * @return List of target type
     */
    public static <D, E> List<D> stream(Iterable<E> source, Function<E, D> transformer) {
        return StreamSupport.stream(source.spliterator(), false) //
                .map(transformer).collect(Collectors.toList());
    }

    /**
     * Performs stream operations on an Iterable
     * 
     * @param <D>         target type
     * @param <E>         source type
     * @param source      the iterable
     * @param transformer the function to apply
     * @return Set of target type
     */
    public static <D, E> Set<D> streamSet(Iterable<E> source, Function<E, D> transformer) {
        return StreamSupport.stream(source.spliterator(), false) //
                .map(transformer).collect(Collectors.toSet());
    }
}
