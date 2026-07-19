package com.example.telecom.change;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Supplementary test: verifies Spring application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("benchmark")
class NetworkChangeApplicationTest {

    @Test
    void contextLoads() {
        // If this test passes, the Spring context loaded without errors
    }
}
