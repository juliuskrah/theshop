package com.shoperal.core.tenancy;

import org.springframework.util.Assert;

public class TenantContextHolder {
	private static final ThreadLocal<TenantContext> contextHolder = new InheritableThreadLocal<>();
	
	private TenantContextHolder() {}

	public static void clearContext() {
		contextHolder.remove();
	}

	public static TenantContext getContext() {
		TenantContext ctx = contextHolder.get();

		if (ctx == null) {
			ctx = createEmptyContext();
			contextHolder.set(ctx);
		}

		return ctx;
	}

	public static void setContext(TenantContext context) {
		Assert.notNull(context, "Only non-null TenantContext instances are permitted");
		contextHolder.set(context);
	}

	public static TenantContext createEmptyContext() {
		return new TenantContextImpl();
	}
}
