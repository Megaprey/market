package ru.yandex.practicum.market.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
public class PaymentClient {

    private final WebClient webClient;

    public PaymentClient(@Qualifier("paymentWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<BigDecimal> getBalance(String username) {
        return webClient.get()
                .uri("/api/balance/{username}", username)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> new BigDecimal(response.get("balance").toString()))
                .doOnError(e -> log.error("Ошибка при получении баланса для {}: {}", username, e.getMessage()));
    }

    public Mono<Boolean> makePayment(String username, BigDecimal amount) {
        Map<String, Object> request = Map.of("username", username, "amount", amount);

        return webClient.post()
                .uri("/api/payment")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> Boolean.TRUE.equals(response.get("success")))
                .doOnError(e -> log.error("Ошибка при платеже для {}: {}", username, e.getMessage()));
    }
}
