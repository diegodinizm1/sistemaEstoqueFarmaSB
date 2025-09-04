package com.diego.sistemafarmaciasb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SistemaFarmaciaSbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaFarmaciaSbApplication.class, args);
    }

}
