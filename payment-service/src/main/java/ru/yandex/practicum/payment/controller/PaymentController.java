package ru.yandex.practicum.payment.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.dto.BalanceResponseDto;
import ru.yandex.practicum.payment.dto.PaymentRequestDto;
import ru.yandex.practicum.payment.dto.PaymentResponseDto;
import ru.yandex.practicum.payment.service.AccountService;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class PaymentController {

    private final AccountService accountService;

    @GetMapping("/balance/{username}")
    public Mono<BalanceResponseDto> getBalance(@PathVariable String username) {
        return accountService.getBalance(username)
                .map(account -> BalanceResponseDto.builder()
                        .username(account.getUsername())
                        .balance(account.getBalance())
                        .build())
                .switchIfEmpty(Mono.error(
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Счёт не найден")));
    }

    @PostMapping("/payment")
    public Mono<PaymentResponseDto> makePayment(@RequestBody PaymentRequestDto request) {
        return accountService.makePayment(request.getUsername(), request.getAmount())
                .map(account -> PaymentResponseDto.builder()
                        .success(true)
                        .newBalance(account.getBalance())
                        .message("Оплата прошла успешно")
                        .build())
                .onErrorResume(IllegalArgumentException.class, e ->
                        Mono.just(PaymentResponseDto.builder()
                                .success(false)
                                .message(e.getMessage())
                                .build()));
    }
}
