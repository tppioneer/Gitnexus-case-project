package com.example.telecom.vendor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class VendorCollaborationApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendorCollaborationApplication.class, args);
    }
}
