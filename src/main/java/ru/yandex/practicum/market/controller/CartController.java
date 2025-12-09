package ru.yandex.practicum.market.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.market.dto.ItemDto;
import ru.yandex.practicum.market.service.ItemService;
import ru.yandex.practicum.market.util.Cart;
import ru.yandex.practicum.market.util.Paging;
import ru.yandex.practicum.market.util.Utils;


@Controller
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    private ItemService itemService;
    private Cart cart;

    @GetMapping("/items")
    public String getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) {

        Page<ItemDto> itemPage = itemService.getPageItemsInCart(cart, pageNumber - 1, pageSize);
        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
        model.addAttribute("paging", paging);

        return "cart";
    }

    @PostMapping("/items")
    public String handleItemAction(
            @RequestParam Long id,
            @RequestParam String action,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "NO") String sort,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Model model) {

        cart.handleItemAction(action, id);
        Page<ItemDto> itemPage = itemService.getPageItemsInCart(cart, pageNumber - 1, pageSize);
        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("total", Utils.getTotalPriceFromItemsDto(itemPage));
        model.addAttribute("paging", paging);

        return "cart";
    }


}