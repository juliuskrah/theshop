package com.shoperal.core.business.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Julius Krah
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class StoreItemNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 1L;

    public StoreItemNotFoundException(String message) {
        super(message);
    }

    public StoreItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
