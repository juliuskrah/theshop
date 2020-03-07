package com.juliuskrah.task;

import java.util.List;
import java.util.regex.Pattern;

public class TenantUtilities {
	public static final List<String> SERVICES;
	public static final String JNDI_BASE = "java:comp/env/jdbc";
	public static final String JNDI_TEMPLATE = JNDI_BASE + "/{tenant}";
	static {
		SERVICES = List.of("baskets", "catalogs", "locations", //
				"marketing", "orders", "payments");
	}

	public static String resolveTemplate(String variable, String template) {
		var namesPattern = Pattern.compile("\\{([^/]+?)\\}");
		var matcher = namesPattern.matcher(template);
		var sb = new StringBuffer();

		if (matcher.find()) {
			matcher.appendReplacement(sb, variable);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
