package com.gitnexus.demo.consumer1;

import com.gitnexus.demo.consumer1.resttemplate.RestTemplateBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class resides in src/test/java and MUST NOT produce any
 * CSE consumer contract during GitNexus extraction.
 */
class FakeCseConsumerTest {

    @Test
    void testRestTemplateBuilderCreatesTemplate() {
        RestTemplate template = RestTemplateBuilder.create();
        assertNotNull(template);
    }
}
