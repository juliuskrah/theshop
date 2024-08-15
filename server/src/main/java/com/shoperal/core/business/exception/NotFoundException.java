package com.shoperal.core.business.exception;

/**
 * Base superclass of entities that cannot be found
 * 
 * @author Julius Krah
 * @since 1.0
 */
public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
