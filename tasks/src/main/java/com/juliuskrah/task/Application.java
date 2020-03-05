package com.juliuskrah.task;

import java.util.Arrays;

import javax.naming.Context;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

@EnableTask
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner bootStrap() {
		return (args) -> System.out.println("args " + Arrays.toString(args));
	}

}
