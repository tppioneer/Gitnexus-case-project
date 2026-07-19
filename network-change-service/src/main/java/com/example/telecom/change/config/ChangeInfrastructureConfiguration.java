package com.example.telecom.change.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Core infrastructure configuration for the change service.
 * Enables async processing for event listeners and AspectJ auto-proxy
 * for AOP test scenarios.
 */
@Configuration
@EnableAsync
@org.springframework.context.annotation.EnableAspectJAutoProxy
public class ChangeInfrastructureConfiguration {

    /**
     * Provides a fixed-size thread pool for async change operations.
     * Using a single-thread executor keeps tests deterministic.
     */
    @Bean(destroyMethod = "shutdown")
    public ExecutorService changeExecutorService() {
        return Executors.newFixedThreadPool(2);
    }
}
