package ru.igor.razzh.controller;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.igor.razzh.dto.HandleItemDto;
import ru.igor.razzh.dto.ItemDto;
import ru.igor.razzh.util.Cart;
import ru.igor.razzh.util.Paging;
import ru.igor.razzh.util.RestPage;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;


@Controller
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private final WebClient webClient;

    @GetMapping("/{id}")
    public Mono<String> getItem(@PathVariable Long id,
                                Model model,
                                ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    Cart cart = session.getAttribute("cart");
                    session.getAttributes().put("id", id);
                    session.getAttributes().put("cart", cart);
                    return webClient.get()
                                    .uri("/items/" + id)
                                    .retrieve()
                            .bodyToMono(ItemDto.class)
                            .doOnNext(item -> model.addAttribute("item", item))
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
                    return webClient.method(HttpMethod.GET)
                            .uri(uriBuilder ->  uriBuilder
                                    .path("/items")
                                    .queryParam("search", search)
                                    .queryParam("sort", sort)
                                    .queryParam("pageNumber", pageNumber)
                                    .queryParam("pageSize", pageSize)
                                    .build())
                            .bodyValue(cart)
                            .retrieve()
                            .bodyToMono(new ParameterizedTypeReference<RestPage<ItemDto>>() {})
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
        return webClient.post()
                .uri("/items")
                .bodyValue(handleItemDto)
                .retrieve()
                .bodyToMono(String.class);
    }

    @PostMapping(value = "/handle", consumes = APPLICATION_JSON_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            ServerWebExchange exchange) {
        return webClient.post()
                .uri("/items/handle")
                .bodyValue(handleItemDto)
                .retrieve()
                .bodyToMono(String.class);
    }

}
