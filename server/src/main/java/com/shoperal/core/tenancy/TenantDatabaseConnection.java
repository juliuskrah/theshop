package com.shoperal.core.tenancy;

import java.util.Map;
import java.util.Collections;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TenantDatabaseConnection {
	String url;
	String tenantName;
	String username;
	String password;
	String catalog;
	long connectionTimeout;
	long validationTimeout;
	long idleTimeout;
	long leakDetectionThreshold;
	long maxLifetime;
	int maxPoolSize;
	int minIdle;

	long initializationFailTimeout;
	String connectionInitSql;
	String connectionTestQuery;
	String dataSourceClassName;
	String driverClassName;
	String poolName;
	String schema;
	String transactionIsolationName;
	boolean isIsolateInternalQueries;
	boolean isRegisterMbeans;
	boolean isAllowPoolSuspension;
	@Builder.Default
	Map<Object, Object> dataSourceProperties = Collections.emptyMap();
	@Builder.Default
	Map<Object, Object> healthCheckProperties = Collections.emptyMap();
}
