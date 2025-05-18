package com.old.silence.webmvc.bind;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalBindingInitializer {

    private static final ThreadLocal<StringTrimmerEditor> STRING_TRIMMER_EDITOR_THREAD_LOCAL = ThreadLocal
            .withInitial(() -> new StringTrimmerEditor(true));

    @InitBinder
    public void registerCustomEditors(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, STRING_TRIMMER_EDITOR_THREAD_LOCAL.get());
    }
}
