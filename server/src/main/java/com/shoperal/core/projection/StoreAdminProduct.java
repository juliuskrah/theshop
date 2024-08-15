package com.shoperal.core.projection;

import java.util.UUID;

public interface StoreAdminProduct {
    UUID getId();
    Double getCostPrice();
    ProductView getProduct();

    static interface ProductView {
        String getName();
        String getFeaturedMedia();
        String getFriendlyUriFragment();
    }
}
