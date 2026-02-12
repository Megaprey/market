package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.entity.Order;
import ru.yandex.practicum.market.repository.OrderRepository;
import ru.yandex.practicum.market.repository.UserRepository;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }

    public Flux<Order> findAllByUsername(String username) {
        return userRepository.findByUsername(username)
                .flatMapMany(user -> orderRepository.findAllByUserId(user.getId()));
    }

    public Mono<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public Mono<Order> findByIdAndUsername(Long orderId, String username) {
        return userRepository.findByUsername(username)
                .flatMap(user -> orderRepository.findById(orderId)
                        .filter(order -> user.getId().equals(order.getUserId())));
    }
}
