package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.BuyRequestDto;
import ru.yandex.practicum.market.service.OrderService;
import ru.yandex.practicum.market.service.ShopService;


import java.math.BigDecimal;
import java.util.Map;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@Controller
@RequestMapping("/orders")
@AllArgsConstructor
public class OrderController {
    private OrderService orderService;
    private ShopService shopService;

    @PostMapping(value = "/buy", consumes = APPLICATION_JSON_VALUE)
    @ResponseBody
    public Mono<Long> buy(
            @RequestBody BuyRequestDto buyRequestDto,
            ServerWebExchange exchange
        ) {
        return exchange.getSession().flatMap( session -> shopService
                        .buy(BigDecimal.valueOf(buyRequestDto.getTotal()),
                                session.getAttribute("cart"))
                .map(order -> order.getId())
                );
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
