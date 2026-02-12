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
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Cart;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Utils;

import java.security.Principal;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Controller
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    private ItemService itemService;

    @GetMapping("/items")
    public Mono<String> getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model,
            ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .flatMap(username -> {
                    model.addAttribute("authenticated", true);
                    model.addAttribute("username", username);

                    return exchange.getSession()
                            .flatMap(session -> {
                                Cart cart = getCartFromSession(session);

                                return itemService.getPageItemsInCart(cart, pageNumber - 1, pageSize)
                                        .flatMap(itemPage -> {
                                            session.getAttributes().put("cart", cart);
                                            Paging paging = new Paging(
                                                    itemPage.getNumber() + 1,
                                                    pageSize,
                                                    itemPage.getTotalElements()
                                            );

                                            model.addAttribute("items", itemPage.getContent());
                                            model.addAttribute("paging", paging);
                                            model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
                                            model.addAttribute("cart", cart);

                                            return Mono.just("cart");
                                        });
                            });
                });
    }

    @PostMapping(value = "/items", consumes = APPLICATION_JSON_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            Model model,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    Cart cart = getCartFromSession(session);

                    return itemService.getPageItemsInCart(cart, handleItemDto.getPageNumber() - 1, handleItemDto.getPageSize())
                            .flatMap(itemPage -> {
                                cart.handleItemAction(handleItemDto.getAction(), handleItemDto.getId());
                                session.getAttributes().put("cart", cart);
                                Paging paging = new Paging(itemPage.getNumber() + 1,
                                        handleItemDto.getPageSize(),
                                        itemPage.getTotalElements());
                                model.addAttribute("items", itemPage.getContent());
                                model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
                                model.addAttribute("paging", paging);

                                return Mono.just("cart");
                            });
                });
    }

    private Cart getCartFromSession(org.springframework.web.server.WebSession session) {
        Cart cart = session.getAttribute("cart");
        return cart != null ? cart : new Cart();
    }
}
