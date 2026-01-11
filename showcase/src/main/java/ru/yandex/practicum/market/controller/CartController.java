package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.HandleItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.service.CartService;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Cart;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Utils;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Controller
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    private CartService cartService;

    @GetMapping("/items")
    public Mono<String> getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model,
            ServerWebExchange exchange) {
            return cartService.getItems(pageNumber, pageSize, model, exchange);
    }

    @PostMapping(value = "/items", consumes = APPLICATION_JSON_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            Model model,
            ServerWebExchange exchange) {
        return cartService.handleItemAction(handleItemDto, model, exchange);
    }

}