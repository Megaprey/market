package ru.yandex.practicum.market.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.HandleItemDto;
import ru.yandex.practicum.market.util.Cart;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Utils;

@Service
@AllArgsConstructor
public class CartService {
    private ItemService itemService;

    public Mono<String> getItems(
            int pageNumber,
            int pageSize,
            Model model,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    // Получаем корзину из сессии или создаем новую
                    Cart cartFromSession = session.getAttribute("cart");
                    Cart cart = cartFromSession != null ? cartFromSession : new Cart();



                    // Получаем страницу товаров
                    return itemService.getPageItemsInCart(cart, pageNumber - 1, pageSize)
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
                                model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
                                model.addAttribute("cart", cart);

                                // Возвращаем имя представления
                                return Mono.just("cart");
                            });
                });
    }

    public Mono<String> handleItemAction(
           HandleItemDto handleItemDto,
            Model model,
            ServerWebExchange exchange) {
        return exchange.getSession()
                .flatMap(session -> {
                    Cart cart = session.getAttribute("cart");

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
}
