package com.old.silence.page;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author murrayZhang
 */
public class PageImpl<T> implements IPage<T> {
    // 数据列表
    private List<T> records;
    // 当前页码（从1开始）
    private long current;
    // 每页数量
    private long size;
    // 总记录数
    private long total;
    // 排序信息
    private final List<OrderItem> orders = Collections.emptyList();

    /**
     * 完整构造函数（适合反序列化）
     */
    @JsonCreator
    public PageImpl(
            @JsonProperty("data") List<T> content,
            @JsonProperty("total") long total) {
        this.records = content;
        this.total = total;
    }

    @Override
    public List<T> getRecords() {
        return records;
    }

    @Override
    public IPage<T> setRecords(List<T> records) {
        this.records = records;
        return this;
    }

    @Override
    public long getCurrent() {
        return current;
    }

    @Override
    public IPage<T> setCurrent(long current) {
        this.current = current;
        return this;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public IPage<T> setSize(long size) {
        this.size = size;
        return this;
    }

    @Override
    public long getTotal() {
        return total;
    }

    @Override
    public IPage<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public List<OrderItem> orders() {
        return orders;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageImpl<?> page = (PageImpl<?>) o;
        return current == page.current &&
                size == page.size &&
                total == page.total &&
                Objects.equals(records, page.records) &&
                Objects.equals(orders, page.orders);
    }

    @Override
    public int hashCode() {
        return Objects.hash(records, current, size, total, orders);
    }

    @Override
    public String toString() {
        return "PageImpl{" +
                "records=" + records +
                ", current=" + current +
                ", size=" + size +
                ", total=" + total +
                ", orders=" + orders +
                '}';
    }
}
