package com.shoperal.core.projection;

/**
 * @author Julius Krah
 */
public interface StoreFrontProduct extends AdminProduct {
    String getDescription();
    String getFriendlyUriFragment();
}
