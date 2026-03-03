package ru.igor.razzh.wallet.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.igor.razzh.wallet.entity.Wallet;

import java.math.BigDecimal;

@Repository
public interface WalletRepository extends ReactiveCrudRepository<Wallet, Long> {
    @Modifying
    @Query("UPDATE wallet SET balance = balance - :amount WHERE id = :id AND balance >= :amount")
    Mono<Integer> withdraw(@Param("id") Long id, @Param("amount") BigDecimal amount);
}
