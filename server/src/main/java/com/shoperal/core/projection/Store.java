package com.shoperal.core.projection;

import com.shoperal.core.model.CurrencyCode;

public interface Store {
    String getId();
    String getName();
    String getShoperalDomain();
    String getDescription();
    CurrencyCode getCurrencyCode();
}
