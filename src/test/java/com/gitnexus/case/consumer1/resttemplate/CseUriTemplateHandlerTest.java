package com.gitnexus.case.consumer1.resttemplate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CseUriTemplateHandlerTest {

    @Test
    void convertCseToHttp_shouldConvertValidUrl() {
        CseUriTemplateHandler handler = new CseUriTemplateHandler(
                java.util.Map.of("order-service", "http://localhost:18083")
        );

        String result = handler.convertCseToHttp("cse://order-service/rest/v1/orders/123");
        assertEquals("http://localhost:18083/rest/v1/orders/123", result);
    }

    @Test
    void convertCseToHttp_shouldHandlePathVariables() {
        CseUriTemplateHandler handler = new CseUriTemplateHandler(
                java.util.Map.of("order-service", "http://localhost:18083")
        );

        String result = handler.convertCseToHttp("cse://order-service/rest/v1/orders/{id}");
        assertEquals("http://localhost:18083/rest/v1/orders/{id}", result);
    }

    @Test
    void convertCseToHttp_shouldHandleQueryStrings() {
        CseUriTemplateHandler handler = new CseUriTemplateHandler(
                java.util.Map.of("order-service", "http://localhost:18083")
        );

        String result = handler.convertCseToHttp("cse://order-service/rest/v1/orders/search?status={status}");
        assertEquals("http://localhost:18083/rest/v1/orders/search?status={status}", result);
    }

    @Test
    void convertCseToHttp_shouldThrowForUnknownService() {
        CseUriTemplateHandler handler = new CseUriTemplateHandler(
                java.util.Map.of("order-service", "http://localhost:18083")
        );

        assertThrows(CseUriTemplateHandler.CseUrlConversionException.class, () ->
                handler.convertCseToHttp("cse://unknown-service/rest/v1/orders")
        );
    }

    @Test
    void convertCseToHttp_shouldThrowForInvalidFormat() {
        CseUriTemplateHandler handler = new CseUriTemplateHandler(
                java.util.Map.of("order-service", "http://localhost:18083")
        );

        assertThrows(CseUriTemplateHandler.CseUrlConversionException.class, () ->
                handler.convertCseToHttp("invalid-url")
        );
    }
}
