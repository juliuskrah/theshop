package com.shoperal.core.tenancy;

import java.util.UUID;

public interface Settings {
	UUID getTenantId();

	String getTenantName();
}
