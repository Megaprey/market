package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.HandleItemDto;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Cart;

import java.security.Principal;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;
import static ru.yandex.practicum.market.util.Utils.mapItemToItemDto;


@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private ItemService itemService;

    @GetMapping("/{id}")
    public Mono<String> getItem(@PathVariable Long id,
                                Model model,
                                ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("")
                .flatMap(username -> {
                    boolean authenticated = !username.isEmpty();
                    model.addAttribute("authenticated", authenticated);
                    model.addAttribute("username", username);

                    return exchange.getSession().flatMap(session -> {
                        Cart cart = authenticated ? getCartFromSession(session) : new Cart();
                        return itemService.findById(id)
                                .doOnNext(item -> model.addAttribute("item", mapItemToItemDto(item, cart)))
                                .thenReturn("item");
                    });
                });
    }

    @GetMapping()
    public Mono<String> getItems(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model,
            ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .map(Principal::getName)
                .defaultIfEmpty("")
                .flatMap(username -> {
                    boolean authenticated = !username.isEmpty();
                    model.addAttribute("authenticated", authenticated);
                    model.addAttribute("username", username);

                    return exchange.getSession()
                            .flatMap(session -> {
                                Cart cart = authenticated ? getCartFromSession(session) : new Cart();

                                return itemService.getPageItems(cart, pageNumber - 1, pageSize, search, sort)
                                        .flatMap(itemPage -> {
                                            if (authenticated) {
                                                session.getAttributes().put("cart", cart);
                                            }
                                            Paging paging = new Paging(
                                                    itemPage.getNumber() + 1,
                                                    pageSize,
                                                    itemPage.getTotalElements()
                                            );

                                            model.addAttribute("items", itemPage.getContent());
                                            model.addAttribute("paging", paging);
                                            model.addAttribute("search", search);
                                            model.addAttribute("sort", sort);
                                            model.addAttribute("cart", cart);

                                            return Mono.just("items");
                                        });
                            });
                });
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .doOnNext(session -> {
                    Cart cart = getCartFromSession(session);
                    cart.handleItemAction(handleItemDto.getAction(), handleItemDto.getId());

                    session.getAttributes().put("flash.search", handleItemDto.getSearch());
                    session.getAttributes().put("flash.sort", handleItemDto.getSort());
                    session.getAttributes().put("flash.pageSize", handleItemDto.getPageSize());
                    session.getAttributes().put("flash.pageNumber", handleItemDto.getPageNumber());
                    session.getAttributes().put("cart", cart);
                })
                .thenReturn("redirect:/items");
    }

    private Cart getCartFromSession(org.springframework.web.server.WebSession session) {
        Cart cart = session.getAttribute("cart");
        return cart != null ? cart : new Cart();
    }
}
