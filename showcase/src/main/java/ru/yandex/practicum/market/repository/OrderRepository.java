package ru.yandex.practicum.market.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.market.entity.Order;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {
}
