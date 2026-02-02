package com.old.silence.webmvc.util;

import com.old.silence.core.exception.ResourceNotFoundException;

public final class RestControllerUtils {

    private RestControllerUtils() {
        throw new AssertionError();
    }

    public static void validateModifyingResult(int rowsAffected) {
        if (rowsAffected == 0) {
            throw new ResourceNotFoundException();
        }
    }
}
