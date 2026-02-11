package com.old.silence.webmvc.test.restdocs;

import java.util.Map;
import java.util.Objects;

import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.http.HttpRequestSnippet;
import org.springframework.restdocs.operation.Operation;

/**
 * @author moryzang
 */
public class CustomHttpRequestSnippet extends HttpRequestSnippet {
    @Override
    protected Map<String, Object> createModel(Operation operation) {
        return super.createModel(operation);
    }

    private String removeQueryStringIfPresent(String urlTemplate) {
        int index = urlTemplate.indexOf('?');
        if (index == -1) {
            return urlTemplate;
        }
        return urlTemplate.substring(0, index);
    }

    private String extractUrlTemplate(Operation operation) {
        var urlTemplate = (String) operation.getAttributes()
                .get(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE);
        Objects.requireNonNull(urlTemplate, "urlTemplate not found. If you are using MockMvc did "
        + "you use RestDocumentationRequestBuilders to build the request?");
        return urlTemplate;
    }
}
