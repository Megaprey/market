package ru.yandex.practicum.market.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entity.Item;

@Repository
public interface ItemRepository extends ReactiveCrudRepository<Item, Long> {
    Mono<Item> findById(Long id);
    Flux<Item> findAllByCheckOrder(boolean checkOrder);
}
