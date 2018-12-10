package com.tmw.etl.etlapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.tmw.etl.etlapp")
public class EtlappApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtlappApplication.class, args);
    }
}
