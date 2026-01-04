package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.OrderRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    OrderRepository orderRepository;

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }

    public Mono<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}
