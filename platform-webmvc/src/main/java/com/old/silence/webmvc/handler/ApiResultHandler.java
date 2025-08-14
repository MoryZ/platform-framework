package com.old.silence.webmvc.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.old.silence.core.support.ErrorCoded;
import com.old.silence.web.bind.annotation.GetJsonMapping;
import com.old.silence.web.bind.annotation.PostJsonMapping;
import com.old.silence.web.bind.annotation.PutJsonMapping;
import com.old.silence.webmvc.common.ApiResult;

/**
 * @author moryzang
 */
@ControllerAdvice(annotations = RestController.class)
public class ApiResultHandler implements ResponseBodyAdvice<Object> {

    private static final List<Class<? extends Annotation>> ANNOTATIONS = List.of(
            RequestMapping.class,
            GetMapping.class,
            PostMapping.class,
            PutMapping.class,
            DeleteMapping.class,
            GetJsonMapping.class,
            PostJsonMapping.class,
            PutJsonMapping.class
    );

    private final ThreadLocal<ObjectMapper> mapperThreadLocal =
            ThreadLocal.withInitial(ObjectMapper::new);

    /**
     * 对所有RestController 的接口方法进行拦截
     * @param returnType the return type
     * @param converterType the selected converter type
     * @return boolean
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType) {
        AnnotatedElement element = returnType.getAnnotatedElement();
        return ANNOTATIONS.stream().anyMatch(anno -> anno.isAnnotation() &&
                element.isAnnotationPresent(anno));
    }



    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        Object out;
        ObjectMapper mapper = mapperThreadLocal.get();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (body instanceof ApiResult) {
            out = body;
        } else if (body instanceof ErrorCoded) {
            out = body;
        } else if (body instanceof String || body == null) {
            ApiResult result = ApiResult.success(body);
            try {
                out = mapper.writeValueAsString(result);
            } catch (JsonProcessingException e) {
                out = ApiResult.error(500, e.getMessage());
            }
        } else {
            out = ApiResult.success(body);
        }
        return out;
    }
}
