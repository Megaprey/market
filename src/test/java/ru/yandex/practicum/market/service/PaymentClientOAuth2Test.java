package ru.yandex.practicum.market.service;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentClientOAuth2Test {

    private MockWebServer mockWebServer;
    private PaymentClient paymentClient;

    private static final String TEST_JWT_TOKEN = "Bearer test-jwt-token";

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", TEST_JWT_TOKEN)
                .build();

        paymentClient = new PaymentClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getBalance_shouldSendAuthorizationHeaderAndReturnBalance() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"username\":\"user1\",\"balance\":10000}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(paymentClient.getBalance("user1"))
                .assertNext(balance -> assertThat(balance).isEqualByComparingTo("10000"))
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo(TEST_JWT_TOKEN);
        assertThat(request.getPath()).isEqualTo("/api/balance/user1");
        assertThat(request.getMethod()).isEqualTo("GET");
    }

    @Test
    void makePayment_shouldSendAuthorizationHeaderAndReturnSuccess() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"success\":true,\"newBalance\":9900,\"message\":\"OK\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(paymentClient.makePayment("user1", BigDecimal.valueOf(100)))
                .assertNext(success -> assertThat(success).isTrue())
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo(TEST_JWT_TOKEN);
        assertThat(request.getPath()).isEqualTo("/api/payment");
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getBody().readUtf8()).contains("\"username\"", "\"user1\"", "\"amount\"", "100");
    }

    @Test
    void makePayment_withInsufficientFunds_shouldReturnFalse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"success\":false,\"message\":\"Insufficient funds\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(paymentClient.makePayment("user1", BigDecimal.valueOf(999999)))
                .assertNext(success -> assertThat(success).isFalse())
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isEqualTo(TEST_JWT_TOKEN);
    }

    @Test
    void getBalance_withoutToken_shouldFailWith401() throws Exception {
        String baseUrl = mockWebServer.url("/").toString();
        WebClient webClientNoAuth = WebClient.builder().baseUrl(baseUrl).build();
        PaymentClient clientNoAuth = new PaymentClient(webClientNoAuth);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(401)
                .setBody("Unauthorized"));

        StepVerifier.create(clientNoAuth.getBalance("user1"))
                .expectError()
                .verify();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getHeader("Authorization")).isNull();
    }
}
