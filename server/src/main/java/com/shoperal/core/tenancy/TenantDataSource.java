package com.shoperal.core.tenancy;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class TenantDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		var settings = TenantContextHolder.getContext().getSettings();
		return settings == null ? null : settings.getTenantName();
	}
}
