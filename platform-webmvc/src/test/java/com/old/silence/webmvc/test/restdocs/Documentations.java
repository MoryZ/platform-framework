package com.old.silence.webmvc.test.restdocs;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.restdocs.RestDocsMockMvcConfigurationCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentationConfigurer;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.ParameterDescriptor;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import com.old.silence.core.util.CollectionUtils;
import com.old.silence.webmvc.test.WebMvcTests;

/**
 * @author moryzang
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@AutoConfigureRestDocs
@Import(Documentations.CustomizationRestDocsConfiguration.class)
public abstract class Documentations extends WebMvcTests {

    protected static HeaderDescriptor header(String name, String value) {
        return HeaderDocumentation.headerWithName(name).description(value);
    }

    protected static ParameterDescriptor pageNoParameter() {
        return pageNoParameter(true);
    }

    protected static ParameterDescriptor pageNoParameter(boolean required) {
        if (required) {
            return requiredParameter("pageNo", "当前页码");
        } else {
            return optionalParameter("pageNo", "当前页码");
        }
    }

    protected static ParameterDescriptor requiredParameter(String name, Object description) {
        return parameter(name, description, false);
    }

    protected static ParameterDescriptor optionalParameter(String name, Object description) {
        return parameter(name, description, true);
    }

    protected static org.springframework.restdocs.request.ParameterDescriptor parameter(String name, Object description, boolean optional) {
        var descriptor = parameterWithName(name).description(description);
        if (optional) {
            descriptor.optional();
        }
        return descriptor;
    }

    protected static ParameterDescriptor pageSizeParameter() {
        return pageSizeParameter(true);
    }

    protected static ParameterDescriptor pageSizeParameter(boolean required) {
        if (required) {
            return requiredParameter("pageSize", "每页显示数据数量");
        } else {
            return optionalParameter("pageSize", "每页显示数据数量");
        }
    }


    protected static ParameterDescriptor skipParameter() {
        return skipParameter(true);
    }

    protected static ParameterDescriptor skipParameter(boolean required) {
        if (required) {
            return requiredParameter("skip", "跳过数据量");
        } else {
            return optionalParameter("skip", "跳过数据量");
        }
    }


    protected static ParameterDescriptor limitParameter() {
        return skipParameter(true);
    }

    protected static ParameterDescriptor limitParameter(boolean required) {
        if (required) {
            return requiredParameter("limit", "每页显示数据量");
        } else {
            return optionalParameter("limit", "每页显示数据量");
        }
    }

    protected static ParameterDescriptor sortParameter(Class<?> entityType, Map<String, String> propertyDescriptions) {
        return sortParameter(entityType, propertyDescriptions, false);
    }

    protected static ParameterDescriptor sortParameter(Class<?> entityType, Map<String, String> propertyDescriptions,
                                                       boolean required) {
        if (CollectionUtils.isEmpty(propertyDescriptions)) {
            throw new IllegalArgumentException("propertyDescriptions must not be empty!");
        }

        // just to validate property
        CollectionUtils.transformToList(propertyDescriptions.keySet(),
                property -> PropertyPath.from(property, entityType));

        String description = "可排序字段：" + propertyDescriptions.entrySet().stream()
                .map(entry -> entry.getKey() + "(" + entry.getValue() + ")").collect(Collectors.joining(", "));

        if (required) {
            return requiredParameter("sort", description);
        } else {
            return optionalParameter("sort", description);
        }
    }

    protected static ParameterDescriptor fieldsParameter() {
        return optionalParameter("fields", "按需指定获取的数据");
    }

    protected static ParameterDescriptor fieldsParameter(boolean required) {
        if (required) {
            return requiredParameter("fields", "按需指定获取的数据");
        } else {
            return optionalParameter("fields", "按需指定获取的数据");
        }
    }

    protected static ParameterDescriptor expandParameter() {
        return optionalParameter("expand", "指定扩展的数据");
    }

    protected static ParameterDescriptor expandParameter(boolean required) {
        if (required) {
            return requiredParameter("expand", "指定扩展的数据");
        } else {
            return optionalParameter("expand", "指定扩展的数据");
        }
    }

    protected static FieldDescriptor requiredField(String name, JsonFieldType type, Object description) {
        return field(name, type, description, false);
    }

    protected static FieldDescriptor field(String name, JsonFieldType type, Object description, boolean optional) {
        var descriptor = fieldWithPath(name).type(type.name().toLowerCase(Locale.ENGLISH))
                .description(description);
        if (optional) {
            descriptor.optional();
        }
        return descriptor;
    }

    protected static FieldDescriptor optionalField(String name, JsonFieldType type, Object description) {
        return field(name, type, description, true);
    }

    protected static FieldDescriptor field(String name, JsonFieldType type, Object description) {
        return field(name, type, description, false);
    }

    protected static FieldDescriptor subsection(String path, JsonFieldType type, Object description) {
        return subsectionWithPath(path).type(type.name().toLowerCase(Locale.ENGLISH)).description(description);
    }

    protected static ResponseFieldsSnippet paginationResponseFields(FieldDescriptor... descriptors) {

        List<FieldDescriptor> paginationDescriptors = new ArrayList<>(descriptors.length + 1);
        paginationDescriptors.add(fieldWithPath("total").type(JsonFieldType.NUMBER).description("总数据条数"));
        for (var descriptor : descriptors) {
            FieldDescriptor actualDescriptor = fieldWithPath("data[]." + descriptor.getPath()).type(
                    descriptor.getType()).description(descriptor.getDescription());
            if (descriptor.isOptional()) {
                paginationDescriptors.add(actualDescriptor.optional());
            } else {
                paginationDescriptors.add(actualDescriptor);
            }
        }

        return responseFields(paginationDescriptors);
    }

    protected static ResultActions documentResponse(ResultActions actions, Snippet... snippets) throws Exception {
        if (ArrayUtils.isEmpty(snippets)) {
            return actions;
        }

        ResultHandler resultHandler = MockMvcRestDocumentation.document("{class-name}/{method-name}",
                Preprocessors.preprocessRequest(), Preprocessors.preprocessResponse(Preprocessors.prettyPrint(),
                        Preprocessors.removeHeaders("Content-Length")), snippets);
        return actions.andDo(resultHandler);
    }

    protected ResultActions documentRequest(MockHttpServletRequestBuilder builder, Snippet... snippets) throws
            Exception {
        return documentRequest(builder, null, null, snippets);
    }

    protected ResultActions documentRequest(MockHttpServletRequestBuilder builder, Object content,
                                            RequestPostProcessor postProcessor, Snippet... snippets) throws Exception {
        return documentResponse(super.exchange(builder, content, postProcessor), snippets);
    }

    protected ResultActions documentRequest(MockHttpServletRequestBuilder builder, RequestPostProcessor postProcessor,
                                            Snippet... snippets) throws Exception {
        return documentRequest(builder, null, postProcessor, snippets);
    }

    protected ResultActions documentRequest(MockHttpServletRequestBuilder builder, Object content,
                                            Snippet... snippets) throws Exception {

        return documentRequest(builder, content, null, snippets);
    }


    @TestConfiguration(proxyBeanMethods = false)
    static class CustomizationRestDocsConfiguration implements RestDocsMockMvcConfigurationCustomizer {

        @Override
        public void customize(MockMvcRestDocumentationConfigurer configurer) {
            configurer.snippets().withDefaults(new CustomCurlRequestSnippet(CliDocumentation.multiLineFormat()),
                    new CustomHttpRequestSnippet(), HttpDocumentation.httpResponse());
        }
    }
}
