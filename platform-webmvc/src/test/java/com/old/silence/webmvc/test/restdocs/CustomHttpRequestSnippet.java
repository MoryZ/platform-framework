package com.old.silence.webmvc.test.restdocs;

import java.util.Map;

import org.springframework.restdocs.cli.CommandFormatter;
import org.springframework.restdocs.cli.CurlRequestSnippet;
import org.springframework.restdocs.operation.Operation;

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
        request.get
    }
}
