package com.gitnexus.demo.consumer2;

import com.gitnexus.demo.consumer2.resttemplate.RestTemplateBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class FakeCseConsumerTest {

    @Test
    void testCseCallInTestSource() {
        RestTemplate template = RestTemplateBuilder.create();
        assertNotNull(template);
    }
}
