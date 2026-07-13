package com.example.telecom.alarm.federated;

import com.example.telecom.alarm.federated.config.FederatedProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.example.telecom")
@EnableConfigurationProperties(FederatedProperties.class)
@EnableScheduling
public class FederatedAlarmApplication {

    public static void main(String[] args) {
        SpringApplication.run(FederatedAlarmApplication.class, args);
    }
}
