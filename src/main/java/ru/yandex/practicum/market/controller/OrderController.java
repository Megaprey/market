package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.BuyRequestDto;
import ru.yandex.practicum.market.service.OrderService;
import ru.yandex.practicum.market.service.ShopService;

import java.math.BigDecimal;
import java.security.Principal;

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
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> exchange.getSession()
                        .flatMap(session -> shopService
                                .buy(BigDecimal.valueOf(buyRequestDto.getTotal()),
                                        session.getAttribute("cart"),
                                        username)
                                .map(order -> order.getId())
                        )
                );
    }

    @GetMapping
    public Mono<String> getOrders(
            Model model,
            ServerWebExchange exchange
    ) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> {
                    model.addAttribute("authenticated", true);
                    model.addAttribute("username", username);
                    return orderService.findAllByUsername(username)
                            .collectList()
                            .doOnNext(orders -> model.addAttribute("orders", orders))
                            .then(Mono.just("orders"));
                });
    }

    @GetMapping("/{id}")
    public Mono<String> getOrder(
            @PathVariable(name = "id") Long orderId,
            Model model,
            ServerWebExchange exchange
    ) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> {
                    model.addAttribute("authenticated", true);
                    model.addAttribute("username", username);
                    return orderService.findByIdAndUsername(orderId, username)
                            .switchIfEmpty(Mono.error(
                                    new ResponseStatusException(HttpStatus.FORBIDDEN, "Доступ запрещён")))
                            .doOnSuccess(order -> model.addAttribute("order", order))
                            .thenReturn("order");
                });
    }
}
