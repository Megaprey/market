package ru.igor.razzh.wallet.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.igor.razzh.wallet.entity.Wallet;
import ru.igor.razzh.wallet.repository.WalletRepository;

import java.math.BigDecimal;

@Service
@AllArgsConstructor
public class WalletService {
    private final WalletRepository walletRepository;

    public Mono<BigDecimal> getBalance() {
        return walletRepository.findById(1l).map(Wallet::getBalance);
    }

    @Transactional
    public Mono<BigDecimal> paySum(BigDecimal sum) {
        return walletRepository.withdraw(1L, sum)
                .flatMap(rowsUpdated -> {
                    if (rowsUpdated == 0) {
                        return walletRepository.findById(1L)
                                .flatMap(wallet -> Mono.error(new RuntimeException(
                                        "Баланса на вашем счёте не достаточно: " + wallet.getBalance()
                                )));
                    }
                    return walletRepository.findById(1L)
                            .map(Wallet::getBalance);
                });
    }
}
