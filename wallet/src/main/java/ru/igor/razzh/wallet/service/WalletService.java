package ru.igor.razzh.wallet.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class WalletService {
    private final AtomicReference<BigDecimal> balance = new AtomicReference<>(BigDecimal.valueOf(10000));

    public Mono<BigDecimal> getBalance() {
        return Mono.just(balance.get());
    }

    @Transactional
    public Mono<BigDecimal> paySum(BigDecimal sum) {
        return Mono.just(sum)
                .flatMap(s -> {
                    if (s.compareTo(balance.get()) <= 0) {
                        balance.set(balance.get().subtract(s));
                        return Mono.just(balance.get());
                    }
                    return Mono.error(new RuntimeException(
                            "Баланса на вашем счёте не достаточно: " + balance.get()
                    ));
                });
    }
}
