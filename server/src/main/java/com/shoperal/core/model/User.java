package com.shoperal.core.model;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class User extends AbstractAuditEntity {
	private static final long serialVersionUID = -1127842844255016995L;
	private String password;
	private final String username;
	private final Set<String> authorities;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;
}
