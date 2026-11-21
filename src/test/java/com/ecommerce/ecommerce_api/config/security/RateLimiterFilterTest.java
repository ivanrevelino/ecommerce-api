package com.ecommerce.ecommerce_api.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class RateLimiterFilterTest {

    @Test
    void limitsRequestsByForwardedIp() throws Exception {
        RateLimiterFilter filter = new RateLimiterFilter();
        AtomicInteger forwardedRequests = new AtomicInteger();

        for (int requestNumber = 0; requestNumber < 100; requestNumber++) {
            MockHttpServletResponse response = sendRequest(filter, "/auth/login", "192.0.2.1", forwardedRequests);
            assertThat(response.getStatus()).isEqualTo(200);
        }

        MockHttpServletResponse limitedResponse =
                sendRequest(filter, "/auth/register", "192.0.2.1", forwardedRequests);
        MockHttpServletResponse otherIpResponse =
                sendRequest(filter, "/auth/login", "192.0.2.2", forwardedRequests);
        MockHttpServletResponse sameIpDifferentPathResponse =
                sendRequest(filter, "/products", "192.0.2.1", forwardedRequests);

        assertThat(limitedResponse.getStatus()).isEqualTo(429);
        assertThat(limitedResponse.getHeader("X-Rate-Limit-Retry-After-Seconds")).isNotNull();
        assertThat(otherIpResponse.getStatus()).isEqualTo(200);
        assertThat(sameIpDifferentPathResponse.getStatus()).isEqualTo(429);
        assertThat(forwardedRequests).hasValue(101);
    }

    private MockHttpServletResponse sendRequest(
            RateLimiterFilter filter,
            String path,
            String ipAddress,
            AtomicInteger forwardedRequests
    ) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", path);
        request.setServletPath(path);
        request.addHeader("X-Forwarded-For", ipAddress);
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, (servletRequest, servletResponse) -> forwardedRequests.incrementAndGet());
        return response;
    }
}
