package ru.yandex.practicum.payment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.payment.PostgreSQLTestContainer;
import ru.yandex.practicum.payment.config.TestR2dbcConfig;
import ru.yandex.practicum.payment.entity.Account;
import ru.yandex.practicum.payment.repository.AccountRepository;

import java.math.BigDecimal;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
@Import({TestR2dbcConfig.class})
@AutoConfigureWebTestClient
class PaymentControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll()
                .then(accountRepository.save(Account.builder()
                        .username("user1")
                        .balance(BigDecimal.valueOf(10000))
                        .build()))
                .then(accountRepository.save(Account.builder()
                        .username("user2")
                        .balance(BigDecimal.valueOf(5000))
                        .build()))
                .block();
    }

    @Test
    void getBalance_withoutToken_shouldReturn401() {
        webTestClient.get().uri("/api/balance/user1")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void makePayment_withoutToken_shouldReturn401() {
        webTestClient.post().uri("/api/payment")
                .header("Content-Type", "application/json")
                .bodyValue("{\"username\":\"user1\",\"amount\":100}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getBalance_withToken_shouldReturnBalance() {
        webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt
                        .claim("scope", "payment.read payment.write")))
                .get().uri("/api/balance/user1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.username").isEqualTo("user1")
                .jsonPath("$.balance").isNumber();
    }

    @Test
    void makePayment_withToken_shouldSucceed() {
        webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt
                        .claim("scope", "payment.read payment.write")))
                .post().uri("/api/payment")
                .header("Content-Type", "application/json")
                .bodyValue("{\"username\":\"user1\",\"amount\":100}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.newBalance").isNumber();
    }

    @Test
    void makePayment_withInsufficientFunds_shouldFail() {
        webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt
                        .claim("scope", "payment.read payment.write")))
                .post().uri("/api/payment")
                .header("Content-Type", "application/json")
                .bodyValue("{\"username\":\"user1\",\"amount\":999999}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false);
    }

    @Test
    void getBalance_withToken_nonExistentUser_shouldReturn404() {
        webTestClient.mutateWith(mockJwt().jwt(jwt -> jwt
                        .claim("scope", "payment.read payment.write")))
                .get().uri("/api/balance/nonexistent")
                .exchange()
                .expectStatus().isNotFound();
    }
}
