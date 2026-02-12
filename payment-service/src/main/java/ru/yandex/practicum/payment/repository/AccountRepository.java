package ru.yandex.practicum.payment.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.payment.entity.Account;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findByUsername(String username);
}
