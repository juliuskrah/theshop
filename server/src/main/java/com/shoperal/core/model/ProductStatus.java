package com.shoperal.core.model;

public enum ProductStatus {
    /**
     * The product is ready to sell and is available to customers on the online
     * store, sales channels, and apps. By default, existing products are set to
     * active
     */
    ACTIVE,
    /**
     * The product is no longer being sold and isn't available to customers on sales
     * channels and apps
     */
    ARCHIVED,

    /**
     * The product isn't ready to sell and is unavailable to customers on sales
     * channels and apps. By default, duplicated and unarchived products are set to
     * draft
     */
    DRAFT

}
