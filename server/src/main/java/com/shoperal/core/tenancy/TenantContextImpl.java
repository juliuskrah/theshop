package com.shoperal.core.tenancy;

/**
 * Base implementation of {@link TenantContext}.
 * <p>
 * Used by default by {@link TenantContextHolder} strategies.
 * 
 * @author Julius Krah
 *
 */
public class TenantContextImpl implements TenantContext {

	private static final long serialVersionUID = 3209255585062482256L;
	private Settings settings;

	@Override
	public Settings getSettings() {
		return settings;
	}

	@Override
	public void setSettings(Settings settings) {
		this.settings = settings;
	}

}
