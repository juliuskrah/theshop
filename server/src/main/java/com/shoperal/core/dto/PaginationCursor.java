package com.shoperal.core.dto;

import java.util.UUID;

import lombok.Value;

/**
 * Represents a pagination cursor
 * @author Julius Krah
 */
@Value
public class PaginationCursor {
    UUID lastId;
    Property lastValue;

    @Value
    public static class Property {
        String field;
        String value;
    }
}
