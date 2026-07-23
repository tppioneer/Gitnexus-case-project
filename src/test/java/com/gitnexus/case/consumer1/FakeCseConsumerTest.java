package com.gitnexus.case.consumer1;

import com.gitnexus.case.consumer1.resttemplate.RestTemplateBuilder;
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
