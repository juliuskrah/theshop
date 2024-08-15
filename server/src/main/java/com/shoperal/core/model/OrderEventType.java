package com.shoperal.core.model;

public enum OrderEventType {
    ORDER_PLACED,
    PAYMENT_PROCESSED,
    /**
     * Order confirmation email sent
     */
    ORDER_CONFIRMATION_EMAIL,
    /**
     * Shipping confirmation email sent
     */
    SHIPPING_CONFIRMATION_EMAIL,
    ORDER_FULFILLED,
    /**
     * Order is archived immediately after fulfillment
     */
    ORDER_ARCHIVED,
    NOTE_ADDEDs
}
