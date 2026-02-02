package com.old.silence.webmvc.validation;

import java.util.Collection;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import com.old.silence.validation.CollectionValidator;

@ControllerAdvice
public class CollectionValidatorAdvice {

    /**
     * Adds the {@link CollectionValidator} to the supplied
     * {@link WebDataBinder}
     *
     * @param binder web data binder.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        if (binder.getTarget() instanceof Collection) {
            binder.addValidators(new CollectionValidator(binder.getValidator()));
        }
    }
}
