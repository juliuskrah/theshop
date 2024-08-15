package com.shoperal.core.controller.dto;

import lombok.Value;

/**
 * @author Julius Krah
 */
@Value
public class ShoperalErrorType {
    private int statusCode;
    private String message;
}
