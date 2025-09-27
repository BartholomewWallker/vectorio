package com.vectorio.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.vectorio")
public class ApplicationEntryPoint {
    public static void main(String[] args) {
        System.out.println("Sistem is working");
        SpringApplication.run(ApplicationEntryPoint.class, args);
    }
}
