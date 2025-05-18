package com.old.silence.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author murrayZhang
 */
public final class TreeFormatUtils {

    private TreeFormatUtils() {
        throw new AssertionError();
    }


    /**
     * 通用List转Tree方法
     * @param list 原始列表
     * @param idGetter 获取ID的方法
     * @param parentIdGetter 获取父ID的方法
     * @param childrenSetter 设置子节点的方法
     * @param <T> 节点类型
     * @param <K> ID类型
     * @return 树形结构
     */
    public static <T, K> List<T> listToTree(
            List<T> list,
            Function<T, K> idGetter,
            Function<T, K> parentIdGetter,
            ChildrenSetter<T> childrenSetter) {

        // 按父ID分组
        Map<K, List<T>> parentMap = list.stream()
                .collect(Collectors.groupingBy(parentIdGetter));

        // 设置子节点
        list.forEach(node -> {
            K id = idGetter.apply(node);
            List<T> children = parentMap.getOrDefault(id, new ArrayList<>());
            childrenSetter.accept(node, children);
        });

        // 返回顶级节点(父ID为null/0/空值的节点)
        return list.stream()
                .filter(node -> {
                    K parentId = parentIdGetter.apply(node);
                    return parentId == null
                            || parentId.equals(0)
                            || (parentId instanceof Number && ((Number) parentId).longValue() == 0L)
                            || (parentId instanceof String && ((String) parentId).isEmpty());
                })
                .collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface ChildrenSetter<T> {
        void accept(T parent, List<T> children);
    }
}
