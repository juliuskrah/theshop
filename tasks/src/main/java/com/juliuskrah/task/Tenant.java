package com.juliuskrah.task;

import lombok.Data;

@Data
public class Tenant {
	private String name;
	private String databaseUrl;
	private String databasePassword;
	private String databaseUsername;
}
