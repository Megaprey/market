package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.market.dto.HandleItemDto;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Cart;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Utils;

import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;


@Controller
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    private ItemService itemService;
    private Cart cart;

    @GetMapping("/items")
    public Mono<String> getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) {

        return itemService.getPageItemsInCart(cart, pageNumber - 1, pageSize)
                .doOnNext(itemPage -> {
                    Paging paging = new Paging(itemPage.getNumber() + 1,
                            pageSize,
                            itemPage.getTotalElements());
                    model.addAttribute("items", itemPage.getContent());
                    model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
                    model.addAttribute("paging", paging);
                }).thenReturn("cart");
    }

    @PostMapping(value = "/items", consumes = TEXT_PLAIN_VALUE)
    public Mono<String> handleItemAction(
            @RequestBody HandleItemDto handleItemDto,
            Model model) {
        return itemService.getPageItemsInCart(cart, handleItemDto.getPageNumber() - 1, handleItemDto.getPageSize())
                .doOnNext(itemPage1 -> {
                    setModelAttribute(model, itemPage1, handleItemDto.getPageSize());
                    cart.handleItemAction(handleItemDto.getAction(), handleItemDto.getId());
                })
                .thenReturn("cart");
    }

    private void setModelAttribute(Model model, Page<ItemDto> itemPage, int pageSize) {
        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());
        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
        model.addAttribute("paging", paging);
    }

}