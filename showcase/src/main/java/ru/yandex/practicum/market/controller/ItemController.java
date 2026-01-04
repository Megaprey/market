package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
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
        return exchange.getSession()
                .flatMap(session -> {
                    Cart cart = session.getAttribute("cart");
                    session.getAttributes().put("id", id);
                    session.getAttributes().put("cart", cart);
                    return itemService.findById(id)
                            .doOnNext(item -> model.addAttribute("item", mapItemToItemDto(item, cart)))
                            .thenReturn("item");
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
        return exchange.getSession()
                .flatMap(session -> {
                    // Получаем корзину из сессии или создаем новую
                    Cart cartFromSession = session.getAttribute("cart");
                    Cart cart = cartFromSession != null ? cartFromSession : new Cart();



                    // Получаем страницу товаров
                    return itemService.getPageItems(cart, pageNumber - 1, pageSize, search, sort)
                            .flatMap(itemPage -> {
                                // Сохраняем корзину обратно в сессию
                                session.getAttributes().put("cart", cart);
                                // Создаем пагинацию
                                Paging paging = new Paging(
                                        itemPage.getNumber() + 1,
                                        pageSize,
                                        itemPage.getTotalElements()
                                );

                                // Устанавливаем атрибуты модели
                                model.addAttribute("items", itemPage.getContent());
                                model.addAttribute("paging", paging);
                                model.addAttribute("search", search);
                                model.addAttribute("sort", sort);
                                model.addAttribute("cart", cart);

                                // Возвращаем имя представления
                                return Mono.just("items");
                            });
                });
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
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
