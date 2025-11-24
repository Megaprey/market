package ru.yandex.practicum.mymarket.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.mymarket.entity.Order;
import ru.yandex.practicum.mymarket.repository.OrderRepository;

@Service
@AllArgsConstructor
public class OrderService {
    OrderRepository orderRepository;

    public Page<Order> findAll(PageRequest of) {
        return orderRepository.findAll(of);
    }
}
