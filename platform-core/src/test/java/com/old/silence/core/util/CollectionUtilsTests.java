package com.old.silence.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author moryzang
 */
class CollectionUtilsTests {

    CollectionUtilsTests() {

    }

    @Test
    void testIsEmpty() {
        Assertions.assertThat(CollectionUtils.isEmpty((Set) null)).isTrue();
        Assertions.assertThat(CollectionUtils.isEmpty((Map<?, ?>) null)).isTrue();
        Assertions.assertThat(CollectionUtils.isEmpty((new HashMap<>()))).isTrue();
        Assertions.assertThat(CollectionUtils.isEmpty((new HashSet<>()))).isTrue();
        List<Object> list = Collections.singletonList(new Object());
        Assertions.assertThat(CollectionUtils.isEmpty(list)).isFalse();
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        Assertions.assertThat(CollectionUtils.isEmpty(map)).isFalse();
    }

    @Test
    void testIsNotEmpty() {
        Assertions.assertThat(CollectionUtils.isNotEmpty((Set) null)).isFalse();
        Assertions.assertThat(CollectionUtils.isNotEmpty((Map<?, ?>) null)).isFalse();
        Assertions.assertThat(CollectionUtils.isNotEmpty((new HashMap<>()))).isFalse();
        Assertions.assertThat(CollectionUtils.isNotEmpty((new HashSet<>()))).isFalse();
        List<Object> list = Collections.singletonList(new Object());
        Assertions.assertThat(CollectionUtils.isNotEmpty(list)).isTrue();
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        Assertions.assertThat(CollectionUtils.isNotEmpty(map)).isTrue();
    }

    @Test
    void size() {
        Assertions.assertThat(CollectionUtils.size((Set) null)).isZero();
        Assertions.assertThat(CollectionUtils.size((Map<?, ?>) null)).isZero();
        Assertions.assertThat(CollectionUtils.size((new HashMap<>()))).isZero();
        Assertions.assertThat(CollectionUtils.size((new HashSet<>()))).isZero();
        List<Object> list = Arrays.asList(new Object(), new Object(), new Object());
        Assertions.assertThat(CollectionUtils.size(list)).isEqualTo(list.size());
        Map<String, String> map = new HashMap<>();
        map.put("foo1", "bar1");
        map.put("foo2", "bar2");
        map.put("foo3", "bar3");
        Assertions.assertThat(CollectionUtils.size(map)).isEqualTo(map.size());
    }
}
