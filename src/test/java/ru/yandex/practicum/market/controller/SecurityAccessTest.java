package ru.yandex.practicum.market.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.market.config.TestR2dbcConfig;
import ru.yandex.practicum.market.service.PaymentClient;
import ru.yandex.practicum.market.service.PostgreSQLTestContainer;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(PostgreSQLTestContainer.class)
@Import({TestR2dbcConfig.class})
@AutoConfigureWebTestClient
class SecurityAccessTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private PaymentClient paymentClient;

    @Test
    void anonymousUser_canAccessItemsList() {
        webTestClient.get().uri("/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void anonymousUser_canAccessItemDetail() {
        webTestClient.get().uri("/items/1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void anonymousUser_cannotAccessCart() {
        webTestClient.get().uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void anonymousUser_cannotAccessOrders() {
        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void anonymousUser_cannotPostToItems() {
        webTestClient.post().uri("/items")
                .header("Content-Type", "application/json")
                .bodyValue("{\"id\":1,\"action\":\"PLUS\",\"pageSize\":10,\"pageNumber\":1}")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void anonymousUser_cannotPostToBuy() {
        webTestClient.post().uri("/orders/buy")
                .header("Content-Type", "application/json")
                .bodyValue("{\"total\":100.0}")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void authenticatedUser_canAccessItemsList() {
        webTestClient.mutateWith(mockUser("user1").roles("USER"))
                .get().uri("/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void authenticatedUser_canAccessCart() {
        webTestClient.mutateWith(mockUser("user1").roles("USER"))
                .get().uri("/cart/items")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void authenticatedUser_canAccessOrders() {
        webTestClient.mutateWith(mockUser("user1").roles("USER"))
                .get().uri("/orders")
                .exchange()
                .expectStatus().isOk();
    }
}
