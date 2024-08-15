package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@SpringBootApplication
public class Shoperal {
    public static void main(String[] args) {
        SpringApplication.run(Shoperal.class, args);
    }

    @GetMapping("/")
    String index() {
        return "index";
    }

    @GetMapping("/categories")
    String category() {
        return "category";
    }

    @GetMapping("/products")
    String product() {
        return "product";
    }
}
