package com.old.silence.core.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.lang.Nullable;

/**
 * @author MurrayZhang
 */
public final class CollectionUtils {

    private static final int MAX_POWER_OF_TWO = 1 << Integer.SIZE - 2;

    private CollectionUtils() {
        throw new AssertionError();
    }

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !isEmpty(collection);
    }

    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static int size(@Nullable Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static int size(@Nullable Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    public static <T> Optional<T> getUniqueElement(@Nullable Collection<T> collection) {

        if (isEmpty(collection)) {
            return Optional.empty();
        }

        T candidate = null;
        for (T element : collection) {
            if (candidate == null) {
                candidate = element;
            } else {
                return Optional.empty();
            }
        }

        return Optional.ofNullable(candidate);
    }

    public static <K, V> Optional<Map.Entry<K, V>> getUniqueEntry(@Nullable Map<K, V> map) {
        return isEmpty(map) ? Optional.empty() : getUniqueElement(map.entrySet());
    }

    public static <K, V> Optional<K> getUniqueKey(@Nullable Map<K, V> map) {
        return isEmpty(map) ? Optional.empty() : getUniqueElement(map.keySet());
    }

    public static <K, V> Optional<V> getUniqueValue(@Nullable Map<K, V> map) {
        return isEmpty(map) ? Optional.empty() : getUniqueElement(map.values());
    }

    public static <T> Optional<T> firstElement(@Nullable List<T> list) {

        if (isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(0));
    }

    public static <T> Optional<T> firstElement(@Nullable Collection<T> collection) {

        if (isEmpty(collection)) {
            return Optional.empty();
        }

        if (collection instanceof List) {
            return firstElement((List<T>) collection);
        }

        if (collection instanceof SortedSet) {
            return Optional.of(((SortedSet<T>) collection).first());
        }

        return Optional.ofNullable(collection.iterator().next());
    }

    public static <T> Optional<T> lastElement(@Nullable List<T> list) {

        if (isEmpty(list)) {
            return Optional.empty();
        }

        return Optional.ofNullable(list.get(list.size() - 1));
    }

    public static <T> Optional<T> lastElement(@Nullable Collection<T> collection) {

        if (isEmpty(collection)) {
            return Optional.empty();
        }

        if (collection instanceof List) {
            return lastElement((List<T>) collection);
        }

        if (collection instanceof SortedSet) {
            return Optional.ofNullable(((SortedSet<T>) collection).last());
        }

        // Full iteration necessary...
        Iterator<T> iterator = collection.iterator();
        T last = null;
        while (iterator.hasNext()) {
            last = iterator.next();
        }

        return Optional.ofNullable(last);
    }

    public static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            if (expectedSize < 0) {
                throw new IllegalArgumentException("Expected size cannot be negative but was: " + expectedSize);
            }
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make. 0.75 is the default load factor.
            return (int) (expectedSize / 0.75F + 1.0F);
        }
        // any large value
        return Integer.MAX_VALUE;
    }

    public static <T> List<T> asList(Collection<T> collection) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        if (collection instanceof List) {
            return (List<T>) collection;
        } else {
            return new ArrayList<>(collection);
        }
    }

    public static Collection<?> asCollection(Object value) {

        if (value == null) {
            return Collections.emptyList();
        }

        if (value instanceof Collection) {
            return (Collection<?>) value;
        }

        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            return Arrays.asList(array);
        }

        return Collections.singletonList(value);
    }

    public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize) { // NOSONAR
        return new HashSet<>(capacity(expectedSize));
    }

    public static <E> LinkedHashSet<E> newLinkedHashSetWithExpectedSize(int expectedSize) { // NOSONAR
        return new LinkedHashSet<>(capacity(expectedSize));
    }

    public static <T> Set<T> newConcurrentHashSet() {
        return Collections.newSetFromMap(new ConcurrentHashMap<T, Boolean>());
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) { // NOSONAR
        return new HashMap<>(capacity(expectedSize));
    }

    public static <K, V> LinkedHashMap<K, V> newLinkedHashMapWithExpectedSize(int expectedSize) { // NOSONAR
        return new LinkedHashMap<>(capacity(expectedSize));
    }

    public static <T, R> List<R> transformToList(T[] array, Function<T, R> mapper) {
        if (ArrayUtils.isEmpty(array)) {
            return Collections.emptyList();
        }
        List<R> list = new ArrayList<>(array.length);
        for (T obj : array) {
            if (obj == null) {
                continue;
            }
            list.add(mapper.apply(obj));
        }
        return list;
    }

    public static <T, R> List<R> transformToList(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return Collections.emptyList();
        }
        List<R> list = new ArrayList<>(collection.size());
        for (T obj : collection) {
            if (obj == null) {
                continue;
            }
            list.add(mapper.apply(obj));
        }
        return list;
    }

    public static <T, R> Set<R> transformToSet(T[] array, Function<T, R> mapper) {
        if (ArrayUtils.isEmpty(array)) {
            return Collections.emptySet();
        }
        Set<R> set = newHashSetWithExpectedSize(array.length);
        for (T obj : array) {
            if (obj == null) {
                continue;
            }
            set.add(mapper.apply(obj));
        }
        return set;
    }

    public static <T, R> Set<R> transformToSet(Collection<T> collection, Function<T, R> mapper) {
        if (isEmpty(collection)) {
            return Collections.emptySet();
        }
        Set<R> set = newHashSetWithExpectedSize(collection.size());
        for (T obj : collection) {
            if (obj == null) {
                continue;
            }
            set.add(mapper.apply(obj));
        }
        return set;
    }

    public static <T, R, C extends Collection<R>> C transformToCollection(Collection<T> collection, Function<T, R> mapper,
                                                                          Supplier<C> collectionFactory) {

        C result = collectionFactory.get();
        if (isEmpty(collection)) {
            return result;
        }
        for (T obj : collection) {
            if (obj == null) {
                continue;
            }
            result.add(mapper.apply(obj));
        }
        return result;
    }

    public static <T, R, C extends Collection<R>> C transformToCollection(T[] array, Function<T, R> mapper,
                                                                          Supplier<C> collectionFactory) {

        C result = collectionFactory.get();
        if (ArrayUtils.isEmpty(array)) {
            return result;
        }
        for (T obj : array) {
            if (obj == null) {
                continue;
            }
            result.add(mapper.apply(obj));
        }
        return result;
    }

    public static <K, V> Map<K, V> transformToMap(Collection<V> collection, Function<V, K> keyMapper) {
        return transformToMap(collection, keyMapper, Function.identity());
    }

    public static <T, K, V> Map<K, V> transformToMap(Collection<T> collection, Function<T, K> keyMapper,
                                                     Function<T, V> valueMapper) {

        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }
        Map<K, V> map = newLinkedHashMapWithExpectedSize(collection.size());
        for (T obj : collection) {
            if (obj == null) {
                continue;
            }
            K key = keyMapper.apply(obj);
            V value = valueMapper.apply(obj);
            map.put(key, value);
        }
        return map;
    }

    public static <T, K> Map<K, Collection<T>> groupingBy(Collection<T> collection, Function<T, K> keyMapper) {
        return groupingBy(collection, keyMapper, Function.identity(), ArrayList::new);
    }

    public static <T, K> Map<K, Collection<T>> groupingBy(Collection<T> collection, Function<T, K> keyMapper,
                                                          Supplier<Collection<T>> valueCollectionSupplier) {

        return groupingBy(collection, keyMapper, Function.identity(), valueCollectionSupplier);
    }

    public static <T, K, V> Map<K, Collection<V>> groupingBy(Collection<T> collection, Function<T, K> keyMapper,
                                                             Function<T, V> valueMapper) {

        return groupingBy(collection, keyMapper, valueMapper, ArrayList::new);
    }

    public static <T, K, V> Map<K, Collection<V>> groupingBy(Collection<T> collection, Function<T, K> keyMapper,
                                                             Function<T, V> valueMapper, Supplier<Collection<V>> valueCollectionSupplier) {

        Supplier<Map<K, Collection<V>>> mapFactory = HashMap::new;
        return groupingBy(collection, keyMapper, valueMapper, valueCollectionSupplier, mapFactory);
    }

    public static <T, K, V> Map<K, Collection<V>> groupingBy(Collection<T> collection, Function<T, K> keyMapper,
                                                             Function<T, V> valueMapper, Supplier<Collection<V>> valueCollectionSupplier,
                                                             Supplier<Map<K, Collection<V>>> mapFactory) {

        if (isEmpty(collection)) {
            return Collections.emptyMap();
        }

        Map<K, Collection<V>> map = mapFactory.get();
        for (T obj : collection) {
            if (obj == null) {
                continue;
            }
            K key = keyMapper.apply(obj);
            V value = valueMapper.apply(obj);
            Collection<V> values = map.computeIfAbsent(key, _key -> valueCollectionSupplier.get());
            values.add(value);
        }
        return map;
    }

    public static <T> BigDecimal summarizingBigDeciaml(Collection<T> collection, Function<? super T, BigDecimal> mapper) {
        return collection == null ? null : collection.stream().map(mapper).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
