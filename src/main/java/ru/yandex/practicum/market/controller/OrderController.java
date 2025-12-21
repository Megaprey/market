package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.service.OrderService;
import ru.yandex.practicum.market.service.ShopService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private ShopService shopService;

    @PostMapping("/buy")
    public Mono<String> buy(
            @RequestParam(name = "total") BigDecimal totalSum,
            Model model
        ) {
        return shopService.buy(totalSum)
                .doOnNext(order -> model.addAttribute("order", order))
                .thenReturn("order");
    }

    @GetMapping
    public Mono<String> getOrders(
            Model model
    ) {
        return orderService.findAll()
                .collectList()
                .doOnNext(orders -> model.addAttribute("orders", orders))
                .then(Mono.just("orders"));
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(
            @PathVariable(name = "id") Long orderId,
            Model model
    ) {
        return orderService.findById(orderId)
                .doOnSuccess(order -> model.addAttribute("order", order))
                .thenReturn("order");
    }
}
