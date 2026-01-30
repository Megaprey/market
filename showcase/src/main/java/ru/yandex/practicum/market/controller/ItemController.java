package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.HandleItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Cart;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static ru.yandex.practicum.market.util.Utils.mapItemToItemDto;


@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @PreAuthorize("hasAuthority('SERVICE')")
    @GetMapping("/{id}")
    public Mono<ItemDto> getItem(@PathVariable Long id,
                                 ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    Cart cart = session.getAttribute("cart");
                    return itemService.findById(id)
                            .map(item -> mapItemToItemDto(item, cart));
                });
    }

    @GetMapping()
    public Mono<Page<ItemDto>> getItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            ServerWebExchange exchange,
            @RequestBody Cart cart
    ) {
        return exchange.getSession()
                .flatMap(session -> {


                    // Получаем страницу товаров
                    return itemService.getPageItems(cart, pageNumber - 1, pageSize, search, sort);
                });
    }

    @PostMapping
    public Mono<String> handleItemsAction(
            @RequestBody HandleItemDto handleItemDto,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .doOnNext(session -> {
                    Cart cart = session.getAttribute("cart");
                    cart.handleItemAction(handleItemDto.getAction(), handleItemDto.getId());
                    itemService.refreshItemsCashes();
                    session.getAttributes().put("cart", cart);
                })
                .thenReturn("redirect:/items");
    }

    @PostMapping(value = "/handle", consumes = APPLICATION_JSON_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .map(session -> {
                    Cart cart = session.getAttribute("cart");

                    cart.handleItemAction(handleItemDto.getAction(), session.getAttribute("id"));
                    itemService.refreshItemCashes();
                    itemService.refreshItemsCashes();
                    session.getAttributes().put("cart", cart);
                    return "redirect:/items/" + session.getAttribute("id");
                });
    }

}
