package com.shoperal.core.projection;

import com.shoperal.core.model.ProductStatus;

/**
 * @author Julius Krah
 */
public interface AdminProduct {
    String getId();
    String getName();
    ProductStatus getStatus();
    String getFeaturedMedia();
    String getType();
    String getVendor();
}
