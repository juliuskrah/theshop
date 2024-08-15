package com.shoperal.core.business;

import java.util.List;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
public class PaginationWrapper<D> {
    private String after;
    private String before;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<D> content;
}
