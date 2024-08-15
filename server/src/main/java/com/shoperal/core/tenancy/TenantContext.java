package com.shoperal.core.tenancy;

import java.io.Serializable;

/**
 * Interface defining the minimum tenant information associated with the current
 * thread of execution.
 *
 * <p>
 * The security context is stored in a {@link TenantContextHolder}.
 * </p>
 *
 * @author Julius Krah
 */
public interface TenantContext extends Serializable {
	/**
	 * Obtains the current tenant.
	 *
	 * @return the <code>Settings</code> or <code>null</code> if no tenant
	 *         information is available
	 */
	Settings getSettings();

	/**
	 * Changes the current tenant principal.
	 *
	 * @param settings the new <code>Settings</code>, or <code>null</code> if no
	 *                 tenant information should be stored
	 */
	void setSettings(Settings settings);
}
