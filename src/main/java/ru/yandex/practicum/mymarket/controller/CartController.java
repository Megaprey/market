package ru.yandex.practicum.mymarket.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.mymarket.entity.Item;
import ru.yandex.practicum.mymarket.service.ItemService;
import ru.yandex.practicum.mymarket.util.Paging;

import java.util.List;

@Controller
@RequestMapping("cart")
@AllArgsConstructor
public class CartController {
    ItemService itemService;

    @GetMapping("/items")
    public String getItems(
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            Model model) {

        Page<Item> itemPage = itemService.findAllInCart(PageRequest.of(pageNumber - 1, pageSize));
        Paging paging = new Paging(itemPage.getNumber() + 1,
                pageSize,
                itemPage.getTotalElements());

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("paging", paging);

        return "cart";
    }
}