package com.old.silence.webmvc.test;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.old.silence.autoconfigure.validation.CustomValidationAutoConfiguration;
import com.old.silence.core.test.UnitTests;
import com.old.silence.csv.CsvMapperAutoConfiguration;
import com.old.silence.json.CustomJacksonAutoConfiguration;
import com.old.silence.json.JacksonMapper;
import com.old.silence.web.autoconfigure.CustomHttpMessageConvertersAutoConfiguration;
import com.old.silence.webmvc.autoconfigure.PlatformDataWebAutoConfiguration;
import com.old.silence.webmvc.autoconfigure.WebMvcAutoConfiguration;

/**
 * @author moryzang
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
@ImportAutoConfiguration(
        {WebMvcAutoConfiguration.class, CustomValidationAutoConfiguration.class, PlatformDataWebAutoConfiguration.class,
                CustomHttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class,
                CustomJacksonAutoConfiguration.class, CsvMapperAutoConfiguration.class})
public abstract class WebMvcTests extends UnitTests {

    @Autowired
    protected MockMvc mockMvc;


    @Autowired
    protected ServerProperties serverProperties;

    @Autowired
    protected WebMvcProperties webMvcProperties;

    @Autowired
    protected JacksonMapper jacksonMapper;

    protected ResultActions exchange(MockHttpServletRequestBuilder builder) throws Exception {
        return exchange(builder, null, null, null);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, Object content,
                                     RequestPostProcessor postProcessor, ResultMatcher resultMatcher) throws Exception {
        var servlet = serverProperties.getServlet();
        if (StringUtils.isNoneEmpty(servlet.getContextPath())) {
            builder.contextPath(servlet.getContextPath());
        }
        String servletPath = webMvcProperties.getServlet().getPath();
        if (StringUtils.isNoneEmpty(servletPath) && servletPath.startsWith("/") && !servletPath.endsWith("/")) {
            builder.servletPath(servletPath);
        }
        if (content != null) {
            builder.content(jacksonMapper.toJson(content)).contentType(MediaType.APPLICATION_JSON);
        }
        if (postProcessor != null) {
            builder.with(postProcessor);
        }
        return execute(builder, resultMatcher);
    }

    protected ResultActions execute(MockHttpServletRequestBuilder builder, ResultMatcher resultMatcher) throws
            Exception {
        return this.mockMvc.perform(builder)
                .andExpect(ObjectUtils.defaultIfNull(resultMatcher, MockMvcResultMatchers.status().is2xxSuccessful()));
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, ResultMatcher resultMatcher) throws
            Exception {
        return exchange(builder, null, null, resultMatcher);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, RequestPostProcessor postProcessor) throws
            Exception {
        return exchange(builder, null, postProcessor);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, Object content,
                                     RequestPostProcessor postProcessor) throws Exception {
        return exchange(builder, content, postProcessor, null);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, RequestPostProcessor postProcessor,
                                     ResultMatcher resultMatcher) throws Exception {
        return exchange(builder, null, postProcessor, resultMatcher);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, Object content) throws Exception {
        return exchange(builder, content, null, null);
    }

    protected ResultActions exchange(MockHttpServletRequestBuilder builder, Object content,
                                     ResultMatcher resultMatcher) throws Exception {
        return exchange(builder, content, null, resultMatcher);
    }

    protected ResultActions execute(MockHttpServletRequestBuilder builder) throws Exception {
        return execute(builder, null);
    }

}
