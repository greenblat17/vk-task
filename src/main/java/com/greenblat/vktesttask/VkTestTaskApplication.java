package com.greenblat.vktesttask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VkTestTaskApplication {

    public static void main(String[] args) {
        SpringApplication.run(VkTestTaskApplication.class, args);
    }

}
