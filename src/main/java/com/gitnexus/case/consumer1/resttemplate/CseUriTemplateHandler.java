package com.gitnexus.case.consumer1.resttemplate;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CseUriTemplateHandler implements ClientHttpRequestInterceptor {

    private static final Pattern CSE_URL_PATTERN = Pattern.compile("^cse://([^/]+)(/.*)$");

    private final Map<String, String> serviceMappings;

    public CseUriTemplateHandler(Map<String, String> serviceMappings) {
        this.serviceMappings = serviceMappings;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        String originalUrl = uri.toString();

        if (originalUrl.startsWith("cse://")) {
            String httpUrl = convertCseToHttp(originalUrl);
            URI newUri = URI.create(httpUrl);

            HttpRequest newRequest = new CseHttpRequestWrapper(request, newUri);
            return execution.execute(newRequest, body);
        }

        return execution.execute(request, body);
    }

    public String convertCseToHttp(String cseUrl) {
        Matcher matcher = CSE_URL_PATTERN.matcher(cseUrl);

        if (!matcher.matches()) {
            throw new CseUrlConversionException("Invalid CSE URL format: " + cseUrl);
        }

        String serviceRef = matcher.group(1);
        String path = matcher.group(2);

        String baseUrl = serviceMappings.get(serviceRef);
        if (baseUrl == null) {
            throw new CseUrlConversionException("Unknown serviceRef: " + serviceRef);
        }

        return baseUrl + path;
    }

    public static class CseUrlConversionException extends RuntimeException {
        public CseUrlConversionException(String message) {
            super(message);
        }
    }

    private static class CseHttpRequestWrapper implements HttpRequest {
        private final HttpRequest delegate;
        private final URI newUri;

        public CseHttpRequestWrapper(HttpRequest delegate, URI newUri) {
            this.delegate = delegate;
            this.newUri = newUri;
        }

        @Override
        public URI getURI() {
            return newUri;
        }

        @Override
        public String getMethod() {
            return delegate.getMethod();
        }

        @Override
        public HttpHeaders getHeaders() {
            return delegate.getHeaders();
        }

        @Override
        public String getMethodValue() {
            return delegate.getMethodValue();
        }
    }
}
