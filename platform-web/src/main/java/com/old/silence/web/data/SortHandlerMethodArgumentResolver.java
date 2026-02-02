package com.old.silence.web.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public class SortHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public SortHandlerMethodArgumentResolver() {
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 支持两种参数类型：
        return parameter.getParameterType().equals(OrderItem.class) ||
                parameter.getParameterType().equals(Page.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        // 处理分页对象中的排序参数
        if (parameter.getParameterType().equals(Page.class)) {
            return parsePageWithOrder(webRequest);
        }
        // 处理单独的 CustomOrderItem 参数
        return parseSingleOrderItem(webRequest);
    }

    private Page<?> parsePageWithOrder(NativeWebRequest webRequest) {
        // 从请求参数中获取分页数据
        long current = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("pageNo")));
        long size = Long.parseLong(Objects.requireNonNull(webRequest.getParameter("pageSize")));
        Page<?> page = new Page<>(current, size);
        String sort = webRequest.getParameter("sort");
        if (StringUtils.isNotBlank(sort)) {
            page.setOrders(assembleOrderItems(sort));
        }

        return page;
    }

    private List<OrderItem> assembleOrderItems(String sort) {

        List<OrderItem> orderItems = new ArrayList<>();
        String[] orders = sort.split(",");
        for (String orderStr : orders) {
            if (orderStr.isEmpty()) continue; // 跳过空项

            // 解析排序方向
            boolean asc = true;
            String column = orderStr;
            if (orderStr.startsWith("-")) {
                asc = false;
                column = orderStr.substring(1);
            } else if (orderStr.startsWith("+")) {
                column = orderStr.substring(1);
            }

            // 提取有效字段名
            if (column.isEmpty()) continue; // 无效字段如 sort=-,xxx

            // 驼峰转下划线（假设需要转换）
            String dbColumn = camelToUnderline(column);

            orderItems.add(asc? OrderItem.asc(dbColumn): OrderItem.desc(dbColumn));
        }
        return orderItems;
    }

    // 驼峰转下划线工具方法
    private static String camelToUnderline(String str) {
        return str.replaceAll("([a-z0-9])([A-Z])", "$1_$2").toLowerCase();
    }

    private List<OrderItem> parseSingleOrderItem(NativeWebRequest webRequest) {
        String sort = webRequest.getParameter("sort");
        if (StringUtils.isBlank(sort)) {
            return Collections.emptyList();
        }
        return assembleOrderItems(sort);
    }
}
