package com.old.silence.webmvc.test.restdocs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cli.CommandFormatter;
import org.springframework.restdocs.cli.CurlRequestSnippet;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;

/**
 * @author moryzang
 */
public class CustomCurlRequestSnippet extends CurlRequestSnippet {


    protected CustomCurlRequestSnippet(CommandFormatter commandFormatter) {
        super(commandFormatter);
    }

    public CustomCurlRequestSnippet(Map<String, Object> attributes, CommandFormatter commandFormatter) {
        super(attributes, commandFormatter);
    }

    @Override
    protected Map<String, Object> createModel(Operation operation) {
        Map<String, Object> model = super.createModel(operation);
        model.put("url", getUrl(operation));
        return model;
    }

    private String getUrl(Operation operation) {
        var request = operation.getRequest();
        URI uri = request.getUri();
        // 判断是否包含参数
        if (includeParametersInUrl(request)) {
            // 在 3.0 中，使用 MockMvc 或 WebTestClient 传入的 query param
            // 通常已经自动合并到了 URI 对象中，直接 toString 即可
            return String.format("'%s'", uri.toString());
        } else {
            // 如果逻辑要求不包含参数，我们需要从 URI 中剥离 Query 部分
            return String.format("'%s'", removeQuery(uri));
        }
    }

    /**
     * 辅助方法：移除 URI 中的 Query 参数部分
     */
    private String removeQuery(URI uri) {
        try {
            return new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(),
                    uri.getPort(), uri.getPath(), null, uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            return uri.toString();
        }
    }

    private boolean includeParametersInUrl(OperationRequest request) {
        if (request.getMethod() == HttpMethod.GET) {
            return true;
        }
        if (request.getHeaders() == null) {
            return true;
        }
        MediaType mediaType = request.getHeaders().getContentType();
        return !MediaType.APPLICATION_FORM_URLENCODED.includes(mediaType);
    }
}
