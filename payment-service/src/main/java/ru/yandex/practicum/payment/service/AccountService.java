package ru.yandex.practicum.payment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.entity.Account;
import ru.yandex.practicum.payment.repository.AccountRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Mono<Account> getBalance(String username) {
        return accountRepository.findByUsername(username);
    }

    @Transactional
    public Mono<Account> makePayment(String username, BigDecimal amount) {
        return accountRepository.findByUsername(username)
                .flatMap(account -> {
                    if (account.getBalance().compareTo(amount) < 0) {
                        return Mono.error(new IllegalArgumentException("Недостаточно средств на счёте"));
                    }
                    account.setBalance(account.getBalance().subtract(amount));
                    return accountRepository.save(account);
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Счёт не найден для пользователя: " + username)));
    }
}
