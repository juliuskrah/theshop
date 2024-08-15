package com.shoperal.core.business;

import com.shoperal.core.model.User;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class ShoperalUserDetails extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 1L;
	private final User user;

	public ShoperalUserDetails(User user) {
		super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), //
				user.isCredentialsNonExpired(), user.isAccountNonLocked(), null);
		this.user = user;
	}
}
