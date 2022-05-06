package dev.efnilite.worldserver.util;

import java.util.*;

// todo make less sad
public class Sorting {

    /**
     * Sorts a {@link List} based on the given values.
     *
     * @param   list
     *          The list that will be sorted.
     *
     * @return the same {@link List} instance that was provided, but sorted.
     */
    public static <T extends Comparator<? super T>> List<T> list(List<T> list, Comparator<T> comparator) {
        list.sort(comparator);
        return list;
    }

    /**
     * Sorts this {@link Map} based on all the given keys.
     * This returns a new {@link LinkedHashMap} with all keys in the sorted order.
     *
     * @param   map
     *          The map instance to be sorted.
     *
     * @return a new {@link LinkedHashMap} instance with all keys and values of the given map, but sorted based on the keys.
     *
     * @param   <V>
     *          The value of the used key.
     */
    public static <T extends Comparator<? super T>, V> Map<T, V> mapKeys(Map<T, V> map, Comparator<T> comparator) {
        List<Map.Entry<T, V>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByKey(comparator));

        Map<T, V> sorted = new LinkedHashMap<>();
        for (Map.Entry<T, V> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return sorted;
    }

    /**
     * Sorts this {@link Map} based on all the given values.
     * This returns a new {@link LinkedHashMap} with all values in the sorted order.
     *
     * @param   map
     *          The map instance to be sorted.
     *
     * @return a new {@link LinkedHashMap} instance with all keys and values of the given map, but sorted based on the values.
     *
     * @param   <K>
     *          The type of the used key.
     */
    public static <K, T extends Comparable<? super T>> Map<K, T> mapValues(Map<K, T> map, Comparator<T> comparator) {
        List<Map.Entry<K, T>> entries = new ArrayList<>(map.entrySet());
        entries.sort(Map.Entry.comparingByValue(comparator));

        Map<K, T> sorted = new LinkedHashMap<>();
        for (Map.Entry<K, T> entry : entries) {
            sorted.put(entry.getKey(), entry.getValue());
        }

        return sorted;
    }
}